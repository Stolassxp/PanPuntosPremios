package com.panpuntos.modelo;

import com.panpuntos.excepcion.ValidacionException;
import java.math.BigDecimal;

public class DetallePedido {

    private final Producto producto;
    private int cantidad;
    private final BigDecimal precioUnitario;

    public DetallePedido(Producto producto, int cantidad, BigDecimal precioUnitario) {
        if (producto == null) {
            throw new ValidacionException("El producto es obligatorio");
        }
        if (cantidad <= 0) {
            throw new ValidacionException("La cantidad debe ser mayor que cero");
        }
        if (precioUnitario == null || precioUnitario.signum() < 0) {
            throw new ValidacionException("El precio no puede ser negativo");
        }
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public void sumarCantidad(int adicional) {
        if (adicional <= 0) {
            throw new ValidacionException("La cantidad debe ser mayor que cero");
        }
        cantidad += adicional;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    public int getPuntos() {
        return producto.puntosOtorgados() * cantidad;
    }
}