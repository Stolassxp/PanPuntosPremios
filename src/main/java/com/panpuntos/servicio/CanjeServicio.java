package com.panpuntos.servicio;

import com.panpuntos.dao.CanjeDAO;
import com.panpuntos.dao.ClienteDAO;
import com.panpuntos.dao.InventarioDAO;
import com.panpuntos.dao.PuntosDAO;
import com.panpuntos.dao.RecompensaDAO;
import com.panpuntos.excepcion.ReglaNegocioException;
import com.panpuntos.modelo.Cliente;
import com.panpuntos.modelo.MovimientoPuntos;
import com.panpuntos.modelo.Producto;
import com.panpuntos.modelo.Recompensa;
import com.panpuntos.modelo.RegistroCanje;
import com.panpuntos.util.ConexionBD;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class CanjeServicio {

    private final RecompensaDAO recompensaDAO = new RecompensaDAO();
    private final CanjeDAO canjeDAO = new CanjeDAO();
    private final PuntosDAO puntosDAO = new PuntosDAO();
    private final InventarioDAO inventarioDAO = new InventarioDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();

    public void canjear(int idCliente, int idRecompensa) throws SQLException, ReglaNegocioException {
        try (Connection con = ConexionBD.getConexion()) {
            con.setAutoCommit(false);
            try {
                Cliente cliente = puntosDAO.leerClienteBloqueando(con, idCliente);
                if (cliente == null) {
                    throw new ReglaNegocioException("El cliente no existe");
                }
                if (!cliente.isActivo()) {
                    throw new ReglaNegocioException("El cliente está inactivo y no puede canjear");
                }
                Recompensa recompensa = recompensaDAO.buscarPorId(con, idRecompensa);
                if (recompensa == null) {
                    throw new ReglaNegocioException("La recompensa no existe");
                }
                if (!recompensa.isActiva()) {
                    throw new ReglaNegocioException("La recompensa no está activa");
                }
                int puntos = recompensa.getPuntosRequeridos();
                if (!cliente.tieneSaldoSuficiente(puntos)) {
                    throw new ReglaNegocioException("Saldo insuficiente. Se requieren " + puntos
                            + " puntos y el cliente tiene " + cliente.getSaldoPuntos());
                }
                Producto premio = recompensa.getProducto();
                if (!premio.isActivo()) {
                    throw new ReglaNegocioException("El producto de la recompensa no está disponible");
                }
                List<Integer> ids = premio.componentes().stream()
                        .map(Producto::getIdProducto)
                        .sorted()
                        .collect(Collectors.toList());
                for (int id : ids) {
                    Producto actual = inventarioDAO.leerBloqueando(con, id);
                    if (!actual.isActivo() || actual.getExistencia() < 1) {
                        throw new ReglaNegocioException("No hay unidades disponibles de " + actual.getNombre());
                    }
                }
                for (int id : ids) {
                    inventarioDAO.descontar(con, id, 1);
                    inventarioDAO.registrarMovimiento(con, id, 1, "CANJE", null);
                }
                int idCanje = canjeDAO.insertar(con, idCliente, idRecompensa, puntos);
                puntosDAO.descontar(con, idCliente, puntos, idCanje);
                con.commit();
            } catch (SQLException | ReglaNegocioException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public List<Recompensa> recompensas() throws SQLException {
        return recompensaDAO.listar();
    }

    public Cliente cliente(int idCliente) throws SQLException, ReglaNegocioException {
        Cliente cliente = clienteDAO.buscarPorId(idCliente);
        if (cliente == null) {
            throw new ReglaNegocioException("El cliente no existe");
        }
        return cliente;
    }

    public List<MovimientoPuntos> movimientos(int idCliente) throws SQLException {
        return puntosDAO.listarPorCliente(idCliente);
    }

    public List<RegistroCanje> canjes(Integer idCliente) throws SQLException {
        return canjeDAO.listar(idCliente);
    }
}