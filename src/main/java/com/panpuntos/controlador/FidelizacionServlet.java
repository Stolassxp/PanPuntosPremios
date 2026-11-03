package com.panpuntos.controlador;

import com.panpuntos.excepcion.ReglaNegocioException;
import com.panpuntos.excepcion.ValidacionException;
import com.panpuntos.servicio.CanjeServicio;
import com.panpuntos.servicio.ClienteServicio;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/fidelizacion")
public class FidelizacionServlet extends HttpServlet {

    private static final String VISTA = "/WEB-INF/vistas/fidelizacion.jsp";

    private final CanjeServicio servicio = new CanjeServicio();
    private final ClienteServicio clienteServicio = new ClienteServicio();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        mostrar(req, resp, null);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            int idCliente = leerId(req.getParameter("cliente"));
            int idRecompensa = leerId(req.getParameter("recompensa"));
            if (idCliente <= 0 || idRecompensa <= 0) {
                throw new ValidacionException("Seleccione un cliente y una recompensa");
            }
            servicio.canjear(idCliente, idRecompensa);
            resp.sendRedirect(req.getContextPath() + "/fidelizacion?cliente=" + idCliente + "&ok=1");
        } catch (SQLException | ReglaNegocioException | ValidacionException e) {
            mostrar(req, resp, e.getMessage());
        }
    }

    private int leerId(String texto) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException | NullPointerException e) {
            return 0;
        }
    }

    private void mostrar(HttpServletRequest req, HttpServletResponse resp, String error)
            throws ServletException, IOException {
        try {
            req.setAttribute("clientes", clienteServicio.listar(null));
            req.setAttribute("recompensas", servicio.recompensas());
            int id = leerId(req.getParameter("cliente"));
            if (id > 0) {
                req.setAttribute("cliente", servicio.cliente(id));
                req.setAttribute("movimientos", servicio.movimientos(id));
                req.setAttribute("canjes", servicio.canjes(id));
            } else {
                req.setAttribute("canjes", servicio.canjes(null));
            }
        } catch (SQLException | ReglaNegocioException e) {
            error = e.getMessage();
        }
        req.setAttribute("error", error);
        req.setAttribute("ok", "1".equals(req.getParameter("ok")));
        req.getRequestDispatcher(VISTA).forward(req, resp);
    }
}