package com.panpuntos.excepcion;

public class ReglaNegocioException extends Exception {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}