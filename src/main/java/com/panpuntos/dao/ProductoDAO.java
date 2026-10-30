package com.panpuntos.dao;

import com.panpuntos.modelo.Acompanamiento;
import com.panpuntos.modelo.Bebida;
import com.panpuntos.modelo.CategoriaProducto;
import com.panpuntos.modelo.Menu;
import com.panpuntos.modelo.Producto;
import com.panpuntos.modelo.Sandwich;
import com.panpuntos.util.ConexionBD;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    private static final String SELECT_BASE =
            "SELECT id_producto, codigo, categoria, nombre, precio, existencia, activo FROM producto";

    public void insertar(Producto producto) throws SQLException {
        String sql = "INSERT INTO producto (codigo, categoria, nombre, precio, existencia, activo) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.getConexion()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, producto.getCodigo());
                ps.setString(2, producto.getCategoria().name());
                ps.setString(3, producto.getNombre());
                ps.setBigDecimal(4, producto.getPrecio());
                ps.setInt(5, producto.getExistencia());
                ps.setBoolean(6, producto.isActivo());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        producto.setIdProducto(keys.getInt(1));
                    }
                }
                if (producto instanceof Menu) {
                    insertarMenu(con, (Menu) producto);
                }
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    private void insertarMenu(Connection con, Menu menu) throws SQLException {
        String sql = "INSERT INTO menu (id_producto, id_sandwich, id_bebida, id_acompanamiento) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, menu.getIdProducto());
            ps.setInt(2, menu.getSandwich().getIdProducto());
            ps.setInt(3, menu.getBebida().getIdProducto());
            ps.setInt(4, menu.getAcompanamiento().getIdProducto());
            ps.executeUpdate();
        }
    }

    public void actualizar(Producto producto) throws SQLException {
        String sql = "UPDATE producto SET codigo = ?, nombre = ?, precio = ? WHERE id_producto = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getNombre());
            ps.setBigDecimal(3, producto.getPrecio());
            ps.setInt(4, producto.getIdProducto());
            ps.executeUpdate();
        }
    }

    public void cambiarEstado(int idProducto, boolean activo) throws SQLException {
        String sql = "UPDATE producto SET activo = ? WHERE id_producto = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, activo);
            ps.setInt(2, idProducto);
            ps.executeUpdate();
        }
    }

    public Producto buscarPorId(int idProducto) throws SQLException {
        try (Connection con = ConexionBD.getConexion()) {
            return buscarPorId(con, idProducto);
        }
    }

    public Producto buscarPorId(Connection con, int idProducto) throws SQLException {
        String sql = SELECT_BASE + " WHERE id_producto = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(con, rs) : null;
            }
        }
    }

    public Producto buscarPorCodigo(String codigo) throws SQLException {
        String sql = SELECT_BASE + " WHERE codigo = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(con, rs) : null;
            }
        }
    }

    public List<Producto> listar() throws SQLException {
        List<Producto> lista = new ArrayList<>();
        String sql = SELECT_BASE + " ORDER BY categoria, nombre";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(con, rs));
            }
        }
        return lista;
    }

    private Producto mapear(Connection con, ResultSet rs) throws SQLException {
        int id = rs.getInt("id_producto");
        String codigo = rs.getString("codigo");
        CategoriaProducto categoria = CategoriaProducto.valueOf(rs.getString("categoria"));
        String nombre = rs.getString("nombre");
        BigDecimal precio = rs.getBigDecimal("precio");
        int existencia = rs.getInt("existencia");
        boolean activo = rs.getBoolean("activo");
        switch (categoria) {
            case SANDWICH:
                return new Sandwich(id, codigo, nombre, precio, existencia, activo);
            case BEBIDA:
                return new Bebida(id, codigo, nombre, precio, existencia, activo);
            case ACOMPANAMIENTO:
                return new Acompanamiento(id, codigo, nombre, precio, existencia, activo);
            default:
                return cargarMenu(con, id, codigo, nombre, precio, activo);
        }
    }

    private Menu cargarMenu(Connection con, int id, String codigo, String nombre,
                            BigDecimal precio, boolean activo) throws SQLException {
        String sql = "SELECT id_sandwich, id_bebida, id_acompanamiento FROM menu WHERE id_producto = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("El menú " + codigo + " no tiene componentes definidos");
                }
                Sandwich sandwich = (Sandwich) buscarPorId(con, rs.getInt("id_sandwich"));
                Bebida bebida = (Bebida) buscarPorId(con, rs.getInt("id_bebida"));
                Acompanamiento acompanamiento = (Acompanamiento) buscarPorId(con, rs.getInt("id_acompanamiento"));
                return new Menu(id, codigo, nombre, precio, activo, sandwich, bebida, acompanamiento);
            }
        }
    }
}