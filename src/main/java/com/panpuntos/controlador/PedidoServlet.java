package com.panpuntos.controlador;

import com.panpuntos.excepcion.ReglaNegocioException;
import com.panpuntos.excepcion.ValidacionException;
import com.panpuntos.modelo.Pago;
import com.panpuntos.modelo.Pedido;
import com.panpuntos.servicio.PedidoServicio;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/pedidos")
public class PedidoServlet extends HttpServlet {

    private static final String VISTA_LISTA = "/WEB-INF/vistas/pedidos.jsp";
    private static final String VISTA_FORM = "/WEB-INF/vistas/pedido-form.jsp";
    private static final String VISTA_DETALLE = "/WEB-INF/vistas/pedido-detalle.jsp";

    private final PedidoServicio servicio = new PedidoServicio();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accion = req.getParameter("accion");
        if ("nuevo".equals(accion)) {
            mostrarFormulario(req, resp, null);
        } else if ("ver".equals(accion)) {
            int id = leerId(req.getParameter("id"));
            if (id > 0) {
                mostrarDetalle(req, resp, id, null);
            } else {
                mostrarLista(req, resp, "El pedido solicitado no es válido");
            }
        } else {
            mostrarLista(req, resp, null);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String accion = req.getParameter("accion");
        int id = leerId(req.getParameter("id"));
        try {
            if ("crear".equals(accion)) {
                int idCliente = parsearEntero(req.getParameter("cliente"), "El cliente");
                Pedido pedido = servicio.crear(idCliente, leerCantidades(req));
                resp.sendRedirect(req.getContextPath() + "/pedidos?accion=ver&id=" + pedido.getIdPedido());
                return;
            }
            if (id <= 0) {
                throw new ValidacionException("El pedido no es válido");
            }
            if ("cobrar".equals(accion)) {
                Pago pago = servicio.cobrar(id, req.getParameter("metodo"),
                        parsearMonto(req.getParameter("monto")), req.getParameter("tarjeta"));
                if (!pago.isAprobado()) {
                    mostrarDetalle(req, resp, id, "Pago rechazado: " + pago.getMotivoRechazo());
                    return;
                }
            } else if ("anular".equals(accion)) {
                servicio.anular(id);
            } else if ("entregar".equals(accion)) {
                servicio.entregar(id);
            }
            resp.sendRedirect(req.getContextPath() + "/pedidos?accion=ver&id=" + id);
        } catch (SQLException | ReglaNegocioException | ValidacionException | NumberFormatException e) {
            if ("crear".equals(accion)) {
                mostrarFormulario(req, resp, e.getMessage());
            } else if (id > 0) {
                mostrarDetalle(req, resp, id, e.getMessage());
            } else {
                mostrarLista(req, resp, e.getMessage());
            }
        }
    }

    private Map<Integer, Integer> leerCantidades(HttpServletRequest req) {
        Map<Integer, Integer> cantidades = new LinkedHashMap<>();
        for (String nombre : Collections.list(req.getParameterNames())) {
            if (!nombre.startsWith("cant_")) {
                continue;
            }
            String valor = req.getParameter(nombre);
            if (valor == null || valor.isBlank()) {
                continue;
            }
            int cantidad = parsearEntero(valor, "Las cantidades");
            if (cantidad < 0) {
                throw new ValidacionException("Las cantidades no pueden ser negativas");
            }
            if (cantidad > 0) {
                cantidades.put(Integer.parseInt(nombre.substring(5)), cantidad);
            }
        }
        return cantidades;
    }

    private int leerId(String texto) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException | NullPointerException e) {
            return 0;
        }
    }

    private int parsearEntero(String texto, String campo) {
        if (texto == null || texto.isBlank()) {
            throw new ValidacionException(campo + " es obligatorio");
        }
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new ValidacionException(campo + " debe ser un número entero");
        }
    }

    private BigDecimal parsearMonto(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(texto.trim());
        } catch (NumberFormatException e) {
            throw new ValidacionException("El monto no es un número válido");
        }
    }

    private void mostrarLista(HttpServletRequest req, HttpServletResponse resp, String error)
            throws ServletException, IOException {
        Integer idCliente = null;
        String filtro = req.getParameter("cliente");
        if (filtro != null && !filtro.isBlank()) {
            try {
                idCliente = Integer.valueOf(filtro.trim());
            } catch (NumberFormatException e) {
                error = "El cliente indicado no es válido";
            }
        }
        try {
            req.setAttribute("pedidos", servicio.listar(idCliente));
        } catch (SQLException e) {
            error = e.getMessage();
        }
        req.setAttribute("error", error);
        req.getRequestDispatcher(VISTA_LISTA).forward(req, resp);
    }

    private void mostrarFormulario(HttpServletRequest req, HttpServletResponse resp, String error)
            throws ServletException, IOException {
        try {
            req.setAttribute("clientes", servicio.clientesActivos());
            req.setAttribute("productos", servicio.productosActivos());
        } catch (SQLException e) {
            error = e.getMessage();
        }
        req.setAttribute("error", error);
        req.getRequestDispatcher(VISTA_FORM).forward(req, resp);
    }

    private void mostrarDetalle(HttpServletRequest req, HttpServletResponse resp, int id, String error)
            throws ServletException, IOException {
        try {
            req.setAttribute("pedido", servicio.obtener(id));
        } catch (SQLException | ReglaNegocioException e) {
            mostrarLista(req, resp, e.getMessage());
            return;
        }
        req.setAttribute("error", error);
        req.getRequestDispatcher(VISTA_DETALLE).forward(req, resp);
    }
}