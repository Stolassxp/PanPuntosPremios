package com.panpuntos.modelo;

import com.panpuntos.excepcion.ReglaNegocioException;
import com.panpuntos.excepcion.ValidacionException;

public class Cliente {

    private int idCliente;
    private String identificador;
    private String nombre;
    private String telefono;
    private String email;
    private int saldoPuntos;
    private boolean activo;

    public Cliente(int idCliente, String identificador, String nombre, String telefono,
                   String email, int saldoPuntos, boolean activo) {
        this.idCliente = idCliente;
        setIdentificador(identificador);
        setNombre(nombre);
        this.telefono = telefono;
        this.email = email;
        this.saldoPuntos = saldoPuntos;
        this.activo = activo;
    }

    public void acreditarPuntos(int puntos) {
        if (puntos <= 0) {
            throw new ValidacionException("Los puntos a acreditar deben ser positivos");
        }
        saldoPuntos += puntos;
    }

    public boolean tieneSaldoSuficiente(int puntos) {
        return saldoPuntos >= puntos;
    }

    public void descontarPuntos(int puntos) throws ReglaNegocioException {
        if (!tieneSaldoSuficiente(puntos)) {
            throw new ReglaNegocioException("Saldo de puntos insuficiente. Saldo actual: " + saldoPuntos);
        }
        saldoPuntos -= puntos;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getIdentificador() {
        return identificador;
    }

    public final void setIdentificador(String identificador) {
        if (identificador == null || identificador.isBlank()) {
            throw new ValidacionException("El identificador es obligatorio");
        }
        this.identificador = identificador.trim();
    }

    public String getNombre() {
        return nombre;
    }

    public final void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new ValidacionException("El nombre es obligatorio");
        }
        this.nombre = nombre.trim();
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getSaldoPuntos() {
        return saldoPuntos;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}