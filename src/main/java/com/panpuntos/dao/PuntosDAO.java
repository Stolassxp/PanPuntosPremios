package com.panpuntos.dao;

import com.panpuntos.modelo.Cliente;
import com.panpuntos.modelo.MovimientoPuntos;
import com.panpuntos.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PuntosDAO {

    public void acreditar(Connection con, int idCliente, int puntos, int idPedido) throws SQLException {
        String sqlSaldo = "UPDATE cliente SET saldo_puntos = saldo_puntos + ? WHERE id_cliente = ?";
        String sqlMovimiento = "INSERT INTO movimiento_puntos (id_cliente, puntos, tipo, id_pedido) "
                + "VALUES (?, ?, 'ACUMULACION', ?)";
        try (PreparedStatement ps = con.prepareStatement(sqlSaldo)) {
            ps.setInt(1, puntos);
            ps.setInt(2, idCliente);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = con.prepareStatement(sqlMovimiento)) {
            ps.setInt(1, idCliente);
            ps.setInt(2, puntos);
            ps.setInt(3, idPedido);
            ps.executeUpdate();
        }
    }

    public Cliente leerClienteBloqueando(Connection con, int idCliente) throws SQLException {
        String sql = "SELECT id_cliente, identificador, nombre, telefono, email, saldo_puntos, activo "
                + "FROM cliente WHERE id_cliente = ? FOR UPDATE";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new Cliente(rs.getInt("id_cliente"), rs.getString("identificador"),
                        rs.getString("nombre"), rs.getString("telefono"), rs.getString("email"),
                        rs.getInt("saldo_puntos"), rs.getBoolean("activo"));
            }
        }
    }

    public void descontar(Connection con, int idCliente, int puntos, int idCanje) throws SQLException {
        String sqlSaldo = "UPDATE cliente SET saldo_puntos = saldo_puntos - ? "
                + "WHERE id_cliente = ? AND saldo_puntos >= ?";
        String sqlMovimiento = "INSERT INTO movimiento_puntos (id_cliente, puntos, tipo, id_canje) "
                + "VALUES (?, ?, 'CANJE', ?)";
        try (PreparedStatement ps = con.prepareStatement(sqlSaldo)) {
            ps.setInt(1, puntos);
            ps.setInt(2, idCliente);
            ps.setInt(3, puntos);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("No fue posible descontar los puntos del cliente");
            }
        }
        try (PreparedStatement ps = con.prepareStatement(sqlMovimiento)) {
            ps.setInt(1, idCliente);
            ps.setInt(2, -puntos);
            ps.setInt(3, idCanje);
            ps.executeUpdate();
        }
    }

    public List<MovimientoPuntos> listarPorCliente(int idCliente) throws SQLException {
        List<MovimientoPuntos> lista = new ArrayList<>();
        String sql = "SELECT fecha, puntos, tipo, id_pedido, id_canje FROM movimiento_puntos "
                + "WHERE id_cliente = ? ORDER BY fecha DESC, id_movimiento DESC";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int pedido = rs.getInt("id_pedido");
                    Integer idPedido = rs.wasNull() ? null : pedido;
                    int canje = rs.getInt("id_canje");
                    Integer idCanje = rs.wasNull() ? null : canje;
                    lista.add(new MovimientoPuntos(rs.getTimestamp("fecha"), rs.getInt("puntos"),
                            rs.getString("tipo"), idPedido, idCanje));
                }
            }
        }
        return lista;
    }
}