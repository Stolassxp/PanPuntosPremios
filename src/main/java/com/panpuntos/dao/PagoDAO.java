package com.panpuntos.dao;

import com.panpuntos.modelo.Pago;
import com.panpuntos.modelo.PagoEfectivo;
import com.panpuntos.modelo.PagoTarjeta;
import com.panpuntos.util.ConexionBD;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PagoDAO {

    public void insertar(Connection con, int idPedido, Pago pago) throws SQLException {
        String sql = "INSERT INTO pago (id_pedido, metodo, monto, monto_recibido, cambio, referencia, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            ps.setString(2, pago.getMetodo());
            ps.setBigDecimal(3, pago.getMonto());
            ps.setBigDecimal(4, pago.getMontoRecibido());
            ps.setBigDecimal(5, pago.getCambio());
            ps.setString(6, pago.getReferencia());
            ps.setString(7, pago.isAprobado() ? "APROBADO" : "RECHAZADO");
            ps.executeUpdate();
        }
    }

    public void insertar(int idPedido, Pago pago) throws SQLException {
        try (Connection con = ConexionBD.getConexion()) {
            insertar(con, idPedido, pago);
        }
    }

    public Pago buscarAprobado(int idPedido) throws SQLException {
        String sql = "SELECT metodo, monto, monto_recibido, cambio, referencia, fecha FROM pago "
                + "WHERE id_pedido = ? AND estado = 'APROBADO' ORDER BY id_pago DESC LIMIT 1";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                BigDecimal monto = rs.getBigDecimal("monto");
                BigDecimal recibido = rs.getBigDecimal("monto_recibido");
                Pago pago;
                if ("EFECTIVO".equals(rs.getString("metodo"))) {
                    pago = new PagoEfectivo(monto, recibido == null ? monto : recibido);
                } else {
                    pago = PagoTarjeta.desdeRegistro(monto);
                }
                pago.restaurar(true, rs.getString("referencia"), recibido, rs.getBigDecimal("cambio"),
                        rs.getTimestamp("fecha"));
                return pago;
            }
        }
    }
}