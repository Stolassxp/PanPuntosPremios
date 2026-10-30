package com.panpuntos.controlador;

import com.panpuntos.util.ConexionBD;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/prueba")
public class PruebaConexionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain;charset=UTF-8");
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM producto");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            resp.getWriter().println("Conexion exitosa. Productos en BD: " + rs.getInt(1));
        } catch (Exception e) {
            resp.getWriter().println("Error: " + e.getMessage());
        }
    }
}