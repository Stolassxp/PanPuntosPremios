package com.panpuntos.dao;

import com.panpuntos.modelo.Recompensa;
import com.panpuntos.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RecompensaDAO {

    private static final String SELECT_BASE =
            "SELECT id_recompensa, nombre, puntos_requeridos, id_producto, activa FROM recompensa";

    private final ProductoDAO productoDAO = new ProductoDAO();

    public List<Recompensa> listar() throws SQLException {
        List<Recompensa> lista = new ArrayList<>();
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SELECT_BASE + " ORDER BY puntos_requeridos");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(con, rs));
            }
        }
        return lista;
    }

    public Recompensa buscarPorId(Connection con, int idRecompensa) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SELECT_BASE + " WHERE id_recompensa = ?")) {
            ps.setInt(1, idRecompensa);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(con, rs) : null;
            }
        }
    }

    private Recompensa mapear(Connection con, ResultSet rs) throws SQLException {
        return new Recompensa(rs.getInt("id_recompensa"), rs.getString("nombre"),
                rs.getInt("puntos_requeridos"), productoDAO.buscarPorId(con, rs.getInt("id_producto")),
                rs.getBoolean("activa"));
    }
}