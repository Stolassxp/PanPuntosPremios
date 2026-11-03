package com.panpuntos.dao;

import com.panpuntos.modelo.MovimientoInventario;
import com.panpuntos.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class InventarioDAO {

    public void abastecer(int idProducto, int cantidad) throws SQLException {
        String sql = "UPDATE producto SET existencia = existencia + ? WHERE id_producto = ?";
        try (Connection con = ConexionBD.getConexion()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, cantidad);
                ps.setInt(2, idProducto);
                ps.executeUpdate();
                registrarMovimiento(con, idProducto, cantidad, "ABASTECIMIENTO", null);
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public void registrarMovimiento(Connection con, int idProducto, int cantidad, String tipo, Integer idPedido)
            throws SQLException {
        String sql = "INSERT INTO movimiento_inventario (id_producto, cantidad, tipo, id_pedido) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ps.setInt(2, cantidad);
            ps.setString(3, tipo);
            if (idPedido == null) {
                ps.setNull(4, Types.INTEGER);
            } else {
                ps.setInt(4, idPedido);
            }
            ps.executeUpdate();
        }
    }

    public List<MovimientoInventario> listarMovimientos() throws SQLException {
        List<MovimientoInventario> lista = new ArrayList<>();
        String sql = "SELECT m.fecha, p.codigo, p.nombre, m.cantidad, m.tipo "
                + "FROM movimiento_inventario m JOIN producto p ON p.id_producto = m.id_producto "
                + "ORDER BY m.fecha DESC, m.id_movimiento DESC";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new MovimientoInventario(
                        rs.getTimestamp("fecha"),
                        rs.getString("codigo"),
                        rs.getString("nombre"),
                        rs.getInt("cantidad"),
                        rs.getString("tipo")));
            }
        }
        return lista;
    }
}