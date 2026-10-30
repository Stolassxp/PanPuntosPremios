package com.panpuntos.dao;

import com.panpuntos.modelo.Cliente;
import com.panpuntos.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    private static final String SELECT_BASE =
            "SELECT id_cliente, identificador, nombre, telefono, email, saldo_puntos, activo FROM cliente";

    public void insertar(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO cliente (identificador, nombre, telefono, email, saldo_puntos, activo) VALUES (?, ?, ?, ?, 0, TRUE)";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cliente.getIdentificador());
            ps.setString(2, cliente.getNombre());
            ps.setString(3, cliente.getTelefono());
            ps.setString(4, cliente.getEmail());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    cliente.setIdCliente(keys.getInt(1));
                }
            }
        }
    }

    public void actualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE cliente SET nombre = ?, telefono = ?, email = ? WHERE id_cliente = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getTelefono());
            ps.setString(3, cliente.getEmail());
            ps.setInt(4, cliente.getIdCliente());
            ps.executeUpdate();
        }
    }

    public void cambiarEstado(int idCliente, boolean activo) throws SQLException {
        String sql = "UPDATE cliente SET activo = ? WHERE id_cliente = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, activo);
            ps.setInt(2, idCliente);
            ps.executeUpdate();
        }
    }

    public Cliente buscarPorId(int idCliente) throws SQLException {
        String sql = SELECT_BASE + " WHERE id_cliente = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public Cliente buscarPorIdentificador(String identificador) throws SQLException {
        String sql = SELECT_BASE + " WHERE identificador = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, identificador);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public List<Cliente> listar() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = SELECT_BASE + " ORDER BY nombre";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id_cliente"),
                rs.getString("identificador"),
                rs.getString("nombre"),
                rs.getString("telefono"),
                rs.getString("email"),
                rs.getInt("saldo_puntos"),
                rs.getBoolean("activo"));
    }
}