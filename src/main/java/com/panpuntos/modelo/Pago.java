package com.panpuntos.modelo;

import com.panpuntos.excepcion.ValidacionException;
import java.math.BigDecimal;
import java.sql.Timestamp;

public abstract class Pago {

    private final BigDecimal monto;
    private boolean aprobado;
    private String referencia;
    private BigDecimal montoRecibido;
    private BigDecimal cambio;
    private String motivoRechazo;
    private Timestamp fecha;

    protected Pago(BigDecimal monto) {
        if (monto == null || monto.signum() < 0) {
            throw new ValidacionException("El monto del pago no es válido");
        }
        this.monto = monto;
    }

    public abstract String getMetodo();

    public abstract void procesar();

    protected void aprobar(String referencia) {
        this.aprobado = true;
        this.referencia = referencia;
        this.motivoRechazo = null;
    }

    protected void rechazar(String referencia, String motivo) {
        this.aprobado = false;
        this.referencia = referencia;
        this.motivoRechazo = motivo;
    }

    protected void setMontoRecibido(BigDecimal montoRecibido) {
        this.montoRecibido = montoRecibido;
    }

    protected void setCambio(BigDecimal cambio) {
        this.cambio = cambio;
    }

    public void restaurar(boolean aprobado, String referencia, BigDecimal montoRecibido,
                          BigDecimal cambio, Timestamp fecha) {
        this.aprobado = aprobado;
        this.referencia = referencia;
        this.montoRecibido = montoRecibido;
        this.cambio = cambio;
        this.fecha = fecha;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public boolean isAprobado() {
        return aprobado;
    }

    public String getReferencia() {
        return referencia;
    }

    public BigDecimal getMontoRecibido() {
        return montoRecibido;
    }

    public BigDecimal getCambio() {
        return cambio;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public Timestamp getFecha() {
        return fecha;
    }
}