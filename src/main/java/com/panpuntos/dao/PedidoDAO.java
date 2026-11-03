package com.panpuntos.dao;

import com.panpuntos.modelo.Cliente;
import com.panpuntos.modelo.DetallePedido;
import com.panpuntos.modelo.EstadoPedido;
import com.panpuntos.modelo.Pedido;
import com.panpuntos.modelo.Producto;
import com.panpuntos.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final PagoDAO pagoDAO = new PagoDAO();

    public void insertar(Pedido pedido) throws SQLException {
        String sqlPedido = "INSERT INTO pedido (id_cliente, estado, total) VALUES (?, 'PENDIENTE', ?)";
        String sqlDetalle = "INSERT INTO detalle_pedido (id_pedido, id_producto, cantidad, precio_unitario) "
                + "VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionBD.getConexion()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, pedido.getCliente().getIdCliente());
                    ps.setBigDecimal(2, pedido.getTotal());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            pedido.setIdPedido(keys.getInt(1));
                        }
                    }
                }
                try (PreparedStatement ps = con.prepareStatement(sqlDetalle)) {
                    for (DetallePedido detalle : pedido.getDetalles()) {
                        ps.setInt(1, pedido.getIdPedido());
                        ps.setInt(2, detalle.getProducto().getIdProducto());
                        ps.setInt(3, detalle.getCantidad());
                        ps.setBigDecimal(4, detalle.getPrecioUnitario());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public Pedido buscarPorId(int idPedido) throws SQLException {
        try (Connection con = ConexionBD.getConexion()) {
            return buscarPorId(con, idPedido, false);
        }
    }

    public Pedido buscarPorId(Connection con, int idPedido, boolean bloquear) throws SQLException {
        String sql = "SELECT id_cliente, fecha, estado FROM pedido WHERE id_pedido = ?"
                + (bloquear ? " FOR UPDATE" : "");
        int idCliente;
        Timestamp fecha;
        EstadoPedido estado;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                idCliente = rs.getInt("id_cliente");
                fecha = rs.getTimestamp("fecha");
                estado = EstadoPedido.valueOf(rs.getString("estado"));
            }
        }
        Cliente cliente = clienteDAO.buscarPorId(idCliente);
        Pedido pedido = new Pedido(idPedido, cliente, fecha, estado);
        cargarDetalles(con, pedido);
        if (estado == EstadoPedido.PAGADO || estado == EstadoPedido.ENTREGADO) {
            pedido.setPago(pagoDAO.buscarAprobado(idPedido));
        }
        return pedido;
    }

    private void cargarDetalles(Connection con, Pedido pedido) throws SQLException {
        String sql = "SELECT id_producto, cantidad, precio_unitario FROM detalle_pedido "
                + "WHERE id_pedido = ? ORDER BY id_detalle";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pedido.getIdPedido());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Producto producto = productoDAO.buscarPorId(con, rs.getInt("id_producto"));
                    pedido.agregarDetalle(new DetallePedido(producto, rs.getInt("cantidad"),
                            rs.getBigDecimal("precio_unitario")));
                }
            }
        }
    }

    public List<Pedido> listar(Integer idCliente) throws SQLException {
        String sql = "SELECT id_pedido FROM pedido" + (idCliente != null ? " WHERE id_cliente = ?" : "")
                + " ORDER BY fecha DESC, id_pedido DESC";
        List<Integer> ids = new ArrayList<>();
        List<Pedido> lista = new ArrayList<>();
        try (Connection con = ConexionBD.getConexion()) {
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                if (idCliente != null) {
                    ps.setInt(1, idCliente);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ids.add(rs.getInt("id_pedido"));
                    }
                }
            }
            for (int id : ids) {
                lista.add(buscarPorId(con, id, false));
            }
        }
        return lista;
    }

    public void actualizarEstado(Connection con, int idPedido, EstadoPedido estado) throws SQLException {
        String sql = "UPDATE pedido SET estado = ? WHERE id_pedido = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado.name());
            ps.setInt(2, idPedido);
            ps.executeUpdate();
        }
    }

    public void actualizarEstado(int idPedido, EstadoPedido estado) throws SQLException {
        try (Connection con = ConexionBD.getConexion()) {
            actualizarEstado(con, idPedido, estado);
        }
    }
}