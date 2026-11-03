package com.panpuntos.servicio;

import com.panpuntos.dao.ClienteDAO;
import com.panpuntos.excepcion.ReglaNegocioException;
import com.panpuntos.modelo.Cliente;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ClienteServicio {

    private final ClienteDAO clienteDAO = new ClienteDAO();

    public void registrar(String identificador, String nombre, String telefono, String email)
            throws SQLException, ReglaNegocioException {
        Cliente nuevo = new Cliente(0, identificador, nombre, telefono, email, 0, true);
        if (clienteDAO.buscarPorIdentificador(nuevo.getIdentificador()) != null) {
            throw new ReglaNegocioException("Ya existe un cliente con el identificador " + nuevo.getIdentificador());
        }
        clienteDAO.insertar(nuevo);
    }

    public void modificar(int idCliente, String nombre, String telefono, String email)
            throws SQLException, ReglaNegocioException {
        Cliente cliente = obtener(idCliente);
        cliente.setNombre(nombre);
        cliente.setTelefono(telefono);
        cliente.setEmail(email);
        clienteDAO.actualizar(cliente);
    }

    public void cambiarEstado(int idCliente, boolean activo) throws SQLException, ReglaNegocioException {
        obtener(idCliente);
        clienteDAO.cambiarEstado(idCliente, activo);
    }

    public Cliente obtener(int idCliente) throws SQLException, ReglaNegocioException {
        Cliente cliente = clienteDAO.buscarPorId(idCliente);
        if (cliente == null) {
            throw new ReglaNegocioException("El cliente no existe");
        }
        return cliente;
    }

    public List<Cliente> listar(String texto) throws SQLException {
        List<Cliente> todos = clienteDAO.listar();
        if (texto == null || texto.isBlank()) {
            return todos;
        }
        String filtro = texto.trim().toLowerCase();
        return todos.stream()
                .filter(c -> c.getNombre().toLowerCase().contains(filtro)
                        || c.getIdentificador().toLowerCase().contains(filtro))
                .collect(Collectors.toList());
    }
}