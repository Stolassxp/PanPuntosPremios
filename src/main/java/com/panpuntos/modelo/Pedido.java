package com.panpuntos.modelo;

import com.panpuntos.excepcion.ReglaNegocioException;
import com.panpuntos.excepcion.ValidacionException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Pedido {

    private int idPedido;
    private final Cliente cliente;
    private final Timestamp fecha;
    private EstadoPedido estado;
    private final List<DetallePedido> detalles = new ArrayList<>();
    private Pago pago;

    public Pedido(int idPedido, Cliente cliente, Timestamp fecha, EstadoPedido estado) {
        if (cliente == null) {
            throw new ValidacionException("El cliente es obligatorio");
        }
        this.idPedido = idPedido;
        this.cliente = cliente;
        this.fecha = fecha;
        this.estado = estado;
    }

    public void agregar(Producto producto, int cantidad) {
        for (DetallePedido detalle : detalles) {
            if (detalle.getProducto().getIdProducto() == producto.getIdProducto()) {
                detalle.sumarCantidad(cantidad);
                return;
            }
        }
        detalles.add(new DetallePedido(producto, cantidad, producto.getPrecio()));
    }

    public void agregarDetalle(DetallePedido detalle) {
        detalles.add(detalle);
    }

    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (DetallePedido detalle : detalles) {
            total = total.add(detalle.getSubtotal());
        }
        return total;
    }

    public int getPuntosGenerados() {
        int puntos = 0;
        for (DetallePedido detalle : detalles) {
            puntos += detalle.getPuntos();
        }
        return puntos;
    }

    public Map<Integer, Integer> requerimientos() {
        Map<Integer, Integer> necesarios = new LinkedHashMap<>();
        for (DetallePedido detalle : detalles) {
            for (Producto componente : detalle.getProducto().componentes()) {
                necesarios.merge(componente.getIdProducto(), detalle.getCantidad(), Integer::sum);
            }
        }
        return necesarios;
    }

    public Set<Integer> productosInvolucrados() {
        Set<Integer> ids = new LinkedHashSet<>();
        for (DetallePedido detalle : detalles) {
            ids.add(detalle.getProducto().getIdProducto());
        }
        ids.addAll(requerimientos().keySet());
        return ids;
    }

    public String detectarFaltantes(Map<Integer, Producto> actuales) {
        Set<String> problemas = new LinkedHashSet<>();
        for (DetallePedido detalle : detalles) {
            Producto actual = actuales.get(detalle.getProducto().getIdProducto());
            if (actual == null || !actual.isActivo()) {
                problemas.add(detalle.getProducto().getNombre() + " no está disponible para la venta.");
            }
        }
        for (Map.Entry<Integer, Integer> necesario : requerimientos().entrySet()) {
            Producto actual = actuales.get(necesario.getKey());
            if (actual == null) {
                problemas.add("Un componente del pedido ya no existe.");
            } else if (!actual.isActivo()) {
                problemas.add(actual.getNombre() + " no está disponible para la venta.");
            } else if (actual.getExistencia() < necesario.getValue()) {
                problemas.add(actual.getNombre() + ": se necesitan " + necesario.getValue()
                        + " y solo hay " + actual.getExistencia() + " disponibles.");
            }
        }
        return problemas.isEmpty() ? null : String.join(" ", problemas);
    }

    public void anular() throws ReglaNegocioException {
        if (estado == EstadoPedido.PAGADO || estado == EstadoPedido.ENTREGADO) {
            throw new ReglaNegocioException("Un pedido pagado no puede anularse desde el módulo de ventas");
        }
        if (estado != EstadoPedido.PENDIENTE) {
            throw new ReglaNegocioException("Solo un pedido pendiente puede anularse");
        }
        estado = EstadoPedido.ANULADO;
    }

    public void marcarPagado() throws ReglaNegocioException {
        if (estado != EstadoPedido.PENDIENTE) {
            throw new ReglaNegocioException("Solo un pedido pendiente puede cobrarse");
        }
        if (detalles.isEmpty()) {
            throw new ReglaNegocioException("El pedido no tiene productos");
        }
        estado = EstadoPedido.PAGADO;
    }

    public void marcarEntregado() throws ReglaNegocioException {
        if (estado != EstadoPedido.PAGADO) {
            throw new ReglaNegocioException("Solo un pedido pagado puede entregarse");
        }
        estado = EstadoPedido.ENTREGADO;
    }

    public boolean isPendiente() {
        return estado == EstadoPedido.PENDIENTE;
    }

    public boolean isPagado() {
        return estado == EstadoPedido.PAGADO;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public List<DetallePedido> getDetalles() {
        return Collections.unmodifiableList(detalles);
    }

    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }
}