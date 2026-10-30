package com.panpuntos.controlador;

import com.panpuntos.dao.ClienteDAO;
import com.panpuntos.dao.ProductoDAO;
import com.panpuntos.modelo.Cliente;
import com.panpuntos.modelo.Producto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/prueba-dao")
public class PruebaDAOServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        try {
            out.println("CLIENTES");
            for (Cliente c : new ClienteDAO().listar()) {
                out.println(c.getIdentificador() + " | " + c.getNombre() + " | puntos: " + c.getSaldoPuntos() + " | activo: " + c.isActivo());
            }
            out.println();
            out.println("PRODUCTOS");
            for (Producto p : new ProductoDAO().listar()) {
                out.println(p.getCodigo() + " | " + p.getNombre() + " | " + p.getCategoria()
                        + " | Q" + p.getPrecio() + " | existencia: " + p.getExistencia()
                        + " | puntos: " + p.puntosOtorgados() + " | componentes: " + p.componentes().size());
            }
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
        }
    }
}