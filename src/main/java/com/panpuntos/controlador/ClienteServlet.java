package com.panpuntos.controlador;

import com.panpuntos.excepcion.ReglaNegocioException;
import com.panpuntos.excepcion.ValidacionException;
import com.panpuntos.servicio.ClienteServicio;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/clientes")
public class ClienteServlet extends HttpServlet {

    private static final String VISTA_LISTA = "/WEB-INF/vistas/clientes.jsp";
    private static final String VISTA_FORM = "/WEB-INF/vistas/cliente-form.jsp";

    private final ClienteServicio servicio = new ClienteServicio();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accion = req.getParameter("accion");
        if ("nuevo".equals(accion)) {
            req.getRequestDispatcher(VISTA_FORM).forward(req, resp);
        } else if ("editar".equals(accion)) {
            try {
                int id = Integer.parseInt(req.getParameter("id"));
                req.setAttribute("cliente", servicio.obtener(id));
                req.getRequestDispatcher(VISTA_FORM).forward(req, resp);
            } catch (SQLException | ReglaNegocioException | NumberFormatException e) {
                mostrarLista(req, resp, e.getMessage());
            }
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
            }
            resp.sendRedirect(req.getContextPath() + "/clientes");
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
        String identificador = req.getParameter("identificador");
        String nombre = req.getParameter("nombre");
        String telefono = req.getParameter("telefono");
        String email = req.getParameter("email");
        if (idTexto == null || idTexto.isBlank()) {
            servicio.registrar(identificador, nombre, telefono, email);
        } else {
            servicio.modificar(Integer.parseInt(idTexto), nombre, telefono, email);
        }
    }

    private void mostrarLista(HttpServletRequest req, HttpServletResponse resp, String error)
            throws ServletException, IOException {
        try {
            req.setAttribute("clientes", servicio.listar(req.getParameter("buscar")));
        } catch (SQLException e) {
            error = e.getMessage();
        }
        req.setAttribute("error", error);
        req.getRequestDispatcher(VISTA_LISTA).forward(req, resp);
    }
}