package com.panpuntos.modelo;

import com.panpuntos.excepcion.ValidacionException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class PagoEfectivo extends Pago {

    public PagoEfectivo(BigDecimal monto, BigDecimal montoRecibido) {
        super(monto);
        if (montoRecibido == null || montoRecibido.signum() < 0) {
            throw new ValidacionException("El monto recibido no es válido");
        }
        setMontoRecibido(montoRecibido);
    }

    @Override
    public String getMetodo() {
        return "EFECTIVO";
    }

    @Override
    public void procesar() {
        if (getMontoRecibido().compareTo(getMonto()) < 0) {
            setCambio(BigDecimal.ZERO);
            BigDecimal faltante = getMonto().subtract(getMontoRecibido()).setScale(2, RoundingMode.HALF_UP);
            rechazar(null, "Monto insuficiente. Faltan Q" + faltante);
        } else {
            setCambio(getMontoRecibido().subtract(getMonto()));
            aprobar(null);
        }
    }
}