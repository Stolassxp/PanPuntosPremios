package com.panpuntos.controlador;

import com.panpuntos.excepcion.ReglaNegocioException;
import com.panpuntos.excepcion.ValidacionException;
import com.panpuntos.servicio.ProductoServicio;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet("/productos")
public class ProductoServlet extends HttpServlet {

    private static final String VISTA_LISTA = "/WEB-INF/vistas/productos.jsp";
    private static final String VISTA_FORM = "/WEB-INF/vistas/producto-form.jsp";
    private static final String VISTA_HISTORIAL = "/WEB-INF/vistas/inventario.jsp";

    private final ProductoServicio servicio = new ProductoServicio();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accion = req.getParameter("accion");
        if ("nuevo".equals(accion)) {
            req.getRequestDispatcher(VISTA_FORM).forward(req, resp);
        } else if ("editar".equals(accion)) {
            try {
                int id = Integer.parseInt(req.getParameter("id"));
                req.setAttribute("producto", servicio.obtener(id));
                req.getRequestDispatcher(VISTA_FORM).forward(req, resp);
            } catch (SQLException | ReglaNegocioException | NumberFormatException e) {
                mostrarLista(req, resp, e.getMessage());
            }
        } else if ("historial".equals(accion)) {
            try {
                req.setAttribute("movimientos", servicio.historial());
            } catch (SQLException e) {
                req.setAttribute("error", e.getMessage());
            }
            req.getRequestDispatcher(VISTA_HISTORIAL).forward(req, resp);
        } else {
            mostrarLista(req, resp, null);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String accion = req.getParameter("accion");
        try {
            if ("guardar".equals(accion)) {
                guardar(req);
            } else if ("estado".equals(accion)) {
                int id = Integer.parseInt(req.getParameter("id"));
                servicio.cambiarEstado(id, Boolean.parseBoolean(req.getParameter("activo")));
            } else if ("abastecer".equals(accion)) {
                int id = Integer.parseInt(req.getParameter("id"));
                servicio.abastecer(id, parsearEntero(req.getParameter("cantidad"), "La cantidad"));
            }
            resp.sendRedirect(req.getContextPath() + "/productos");
        } catch (SQLException | ReglaNegocioException | ValidacionException | NumberFormatException e) {
            if ("guardar".equals(accion)) {
                req.setAttribute("error", e.getMessage());
                req.getRequestDispatcher(VISTA_FORM).forward(req, resp);
            } else {
                mostrarLista(req, resp, e.getMessage());
            }
        }
    }

    private void guardar(HttpServletRequest req) throws SQLException, ReglaNegocioException {
        String idTexto = req.getParameter("id");
        String nombre = req.getParameter("nombre");
        BigDecimal precio = parsearPrecio(req.getParameter("precio"));
        if (idTexto == null || idTexto.isBlank()) {
            int existencia = parsearEntero(req.getParameter("existencia"), "La existencia inicial");
            servicio.registrar(req.getParameter("categoria"), req.getParameter("codigo"), nombre, precio, existencia);
        } else {
            servicio.modificar(Integer.parseInt(idTexto), nombre, precio);
        }
    }

    private BigDecimal parsearPrecio(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new ValidacionException("El precio es obligatorio");
        }
        try {
            return new BigDecimal(texto.trim());
        } catch (NumberFormatException e) {
            throw new ValidacionException("El precio no es un número válido");
        }
    }

    private int parsearEntero(String texto, String campo) {
        if (texto == null || texto.isBlank()) {
            throw new ValidacionException(campo + " es obligatoria");
        }
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new ValidacionException(campo + " debe ser un número entero");
        }
    }

    private void mostrarLista(HttpServletRequest req, HttpServletResponse resp, String error)
            throws ServletException, IOException {
        try {
            req.setAttribute("productos", servicio.listar(req.getParameter("buscar")));
        } catch (SQLException e) {
            error = e.getMessage();
        }
        req.setAttribute("error", error);
        req.getRequestDispatcher(VISTA_LISTA).forward(req, resp);
    }
}