package com.panpuntos.modelo;

import java.sql.Timestamp;

public class RegistroCanje {

    private final Timestamp fecha;
    private final String nombreCliente;
    private final String identificador;
    private final String recompensa;
    private final int puntosUsados;

    public RegistroCanje(Timestamp fecha, String nombreCliente, String identificador, String recompensa, int puntosUsados) {
        this.fecha = fecha;
        this.nombreCliente = nombreCliente;
        this.identificador = identificador;
        this.recompensa = recompensa;
        this.puntosUsados = puntosUsados;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public String getIdentificador() {
        return identificador;
    }

    public String getRecompensa() {
        return recompensa;
    }

    public int getPuntosUsados() {
        return puntosUsados;
    }
}