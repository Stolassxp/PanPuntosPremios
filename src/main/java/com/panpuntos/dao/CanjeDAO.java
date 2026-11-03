package com.panpuntos.dao;

import com.panpuntos.modelo.RegistroCanje;
import com.panpuntos.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CanjeDAO {

    public int insertar(Connection con, int idCliente, int idRecompensa, int puntos) throws SQLException {
        String sql = "INSERT INTO canje (id_cliente, id_recompensa, puntos_usados) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idCliente);
            ps.setInt(2, idRecompensa);
            ps.setInt(3, puntos);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    public List<RegistroCanje> listar(Integer idCliente) throws SQLException {
        List<RegistroCanje> lista = new ArrayList<>();
        String sql = "SELECT c.fecha, cl.nombre, cl.identificador, r.nombre AS recompensa, c.puntos_usados "
                + "FROM canje c JOIN cliente cl ON cl.id_cliente = c.id_cliente "
                + "JOIN recompensa r ON r.id_recompensa = c.id_recompensa"
                + (idCliente != null ? " WHERE c.id_cliente = ?" : "")
                + " ORDER BY c.fecha DESC, c.id_canje DESC";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (idCliente != null) {
                ps.setInt(1, idCliente);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new RegistroCanje(rs.getTimestamp("fecha"), rs.getString("nombre"),
                            rs.getString("identificador"), rs.getString("recompensa"),
                            rs.getInt("puntos_usados")));
                }
            }
        }
        return lista;
    }
}