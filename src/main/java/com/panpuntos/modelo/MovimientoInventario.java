package com.panpuntos.modelo;

import java.sql.Timestamp;

public class MovimientoInventario {

    private final Timestamp fecha;
    private final String codigoProducto;
    private final String nombreProducto;
    private final int cantidad;
    private final String tipo;

    public MovimientoInventario(Timestamp fecha, String codigoProducto, String nombreProducto, int cantidad, String tipo) {
        this.fecha = fecha;
        this.codigoProducto = codigoProducto;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.tipo = tipo;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public String getTipo() {
        return tipo;
    }
}
