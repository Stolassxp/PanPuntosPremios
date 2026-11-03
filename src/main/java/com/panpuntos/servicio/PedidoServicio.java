package com.panpuntos.servicio;

import com.panpuntos.dao.ClienteDAO;
import com.panpuntos.dao.InventarioDAO;
import com.panpuntos.dao.PagoDAO;
import com.panpuntos.dao.PedidoDAO;
import com.panpuntos.dao.ProductoDAO;
import com.panpuntos.dao.PuntosDAO;
import com.panpuntos.excepcion.ReglaNegocioException;
import com.panpuntos.excepcion.ValidacionException;
import com.panpuntos.modelo.Cliente;
import com.panpuntos.modelo.EstadoPedido;
import com.panpuntos.modelo.Pago;
import com.panpuntos.modelo.PagoEfectivo;
import com.panpuntos.modelo.PagoTarjeta;
import com.panpuntos.modelo.Pedido;
import com.panpuntos.modelo.Producto;
import com.panpuntos.util.ConexionBD;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class PedidoServicio {

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final InventarioDAO inventarioDAO = new InventarioDAO();
    private final PagoDAO pagoDAO = new PagoDAO();
    private final PuntosDAO puntosDAO = new PuntosDAO();

    public Pedido crear(int idCliente, Map<Integer, Integer> cantidades) throws SQLException, ReglaNegocioException {
        Cliente cliente = clienteDAO.buscarPorId(idCliente);
        if (cliente == null) {
            throw new ReglaNegocioException("El cliente no existe");
        }
        if (!cliente.isActivo()) {
            throw new ReglaNegocioException("El cliente está inactivo y no puede realizar pedidos");
        }
        if (cantidades.isEmpty()) {
            throw new ReglaNegocioException("Seleccione al menos un producto");
        }
        Pedido pedido = new Pedido(0, cliente, null, EstadoPedido.PENDIENTE);
        for (Map.Entry<Integer, Integer> linea : cantidades.entrySet()) {
            Producto producto = productoDAO.buscarPorId(linea.getKey());
            if (producto == null) {
                throw new ReglaNegocioException("Uno de los productos seleccionados no existe");
            }
            pedido.agregar(producto, linea.getValue());
        }
        String faltantes = pedido.detectarFaltantes(cargarActuales(pedido));
        if (faltantes != null) {
            throw new ReglaNegocioException("No se puede confirmar el pedido. " + faltantes);
        }
        pedidoDAO.insertar(pedido);
        return pedido;
    }

    private Map<Integer, Producto> cargarActuales(Pedido pedido) throws SQLException {
        Map<Integer, Producto> actuales = new HashMap<>();
        for (int id : pedido.productosInvolucrados()) {
            actuales.put(id, productoDAO.buscarPorId(id));
        }
        return actuales;
    }

    public Pago cobrar(int idPedido, String metodo, BigDecimal montoRecibido, String numeroTarjeta)
            throws SQLException, ReglaNegocioException {
        try (Connection con = ConexionBD.getConexion()) {
            con.setAutoCommit(false);
            try {
                Pedido pedido = pedidoDAO.buscarPorId(con, idPedido, true);
                if (pedido == null) {
                    throw new ReglaNegocioException("El pedido no existe");
                }
                if (!pedido.isPendiente()) {
                    throw new ReglaNegocioException("Solo un pedido pendiente puede cobrarse");
                }
                Pago pago = crearPago(metodo, pedido.getTotal(), montoRecibido, numeroTarjeta);
                pago.procesar();
                if (!pago.isAprobado()) {
                    pagoDAO.insertar(con, idPedido, pago);
                    con.commit();
                    return pago;
                }
                Map<Integer, Producto> actuales = new HashMap<>();
                for (int id : new TreeSet<>(pedido.productosInvolucrados())) {
                    actuales.put(id, inventarioDAO.leerBloqueando(con, id));
                }
                String faltantes = pedido.detectarFaltantes(actuales);
                if (faltantes != null) {
                    throw new ReglaNegocioException("Inventario insuficiente. " + faltantes
                            + " No se registró la venta.");
                }
                for (Map.Entry<Integer, Integer> necesario : pedido.requerimientos().entrySet()) {
                    inventarioDAO.descontar(con, necesario.getKey(), necesario.getValue());
                    inventarioDAO.registrarMovimiento(con, necesario.getKey(), necesario.getValue(), "VENTA", idPedido);
                }
                pedido.marcarPagado();
                pedidoDAO.actualizarEstado(con, idPedido, pedido.getEstado());
                pagoDAO.insertar(con, idPedido, pago);
                int puntos = pedido.getPuntosGenerados();
                if (puntos > 0) {
                    puntosDAO.acreditar(con, pedido.getCliente().getIdCliente(), puntos, idPedido);
                }
                con.commit();
                return pago;
            } catch (SQLException | ReglaNegocioException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
    }

    private Pago crearPago(String metodo, BigDecimal total, BigDecimal montoRecibido, String numeroTarjeta) {
        if ("EFECTIVO".equals(metodo)) {
            return new PagoEfectivo(total, montoRecibido);
        }
        if ("TARJETA".equals(metodo)) {
            return new PagoTarjeta(total, numeroTarjeta);
        }
        throw new ValidacionException("Seleccione un método de pago");
    }

    public void anular(int idPedido) throws SQLException, ReglaNegocioException {
        Pedido pedido = obtener(idPedido);
        pedido.anular();
        pedidoDAO.actualizarEstado(idPedido, pedido.getEstado());
    }

    public void entregar(int idPedido) throws SQLException, ReglaNegocioException {
        Pedido pedido = obtener(idPedido);
        pedido.marcarEntregado();
        pedidoDAO.actualizarEstado(idPedido, pedido.getEstado());
    }

    public Pedido obtener(int idPedido) throws SQLException, ReglaNegocioException {
        Pedido pedido = pedidoDAO.buscarPorId(idPedido);
        if (pedido == null) {
            throw new ReglaNegocioException("El pedido no existe");
        }
        return pedido;
    }

    public List<Pedido> listar(Integer idCliente) throws SQLException {
        return pedidoDAO.listar(idCliente);
    }

    public List<Cliente> clientesActivos() throws SQLException {
        return clienteDAO.listar().stream().filter(Cliente::isActivo).collect(Collectors.toList());
    }

    public List<Producto> productosActivos() throws SQLException {
        return productoDAO.listar().stream().filter(Producto::isActivo).collect(Collectors.toList());
    }
}