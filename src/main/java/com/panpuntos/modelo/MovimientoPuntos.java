package com.panpuntos.modelo;

import java.sql.Timestamp;

public class MovimientoPuntos {

    private final Timestamp fecha;
    private final int puntos;
    private final String tipo;
    private final Integer idPedido;
    private final Integer idCanje;

    public MovimientoPuntos(Timestamp fecha, int puntos, String tipo, Integer idPedido, Integer idCanje) {
        this.fecha = fecha;
        this.puntos = puntos;
        this.tipo = tipo;
        this.idPedido = idPedido;
        this.idCanje = idCanje;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public int getPuntos() {
        return puntos;
    }

    public String getTipo() {
        return tipo;
    }

    public String getReferencia() {
        if (idPedido != null) {
            return "Pedido " + idPedido;
        }
        if (idCanje != null) {
            return "Canje " + idCanje;
        }
        return "";
    }
}