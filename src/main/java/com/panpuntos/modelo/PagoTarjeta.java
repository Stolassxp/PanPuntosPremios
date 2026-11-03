package com.panpuntos.modelo;

import com.panpuntos.excepcion.ValidacionException;
import java.math.BigDecimal;
import java.util.UUID;

public class PagoTarjeta extends Pago {

    private final String numeroTarjeta;

    public PagoTarjeta(BigDecimal monto, String numeroTarjeta) {
        super(monto);
        String limpio = numeroTarjeta == null ? "" : numeroTarjeta.replaceAll("[\\s-]", "");
        if (!limpio.matches("\\d{13,19}")) {
            throw new ValidacionException("El número de tarjeta debe tener entre 13 y 19 dígitos");
        }
        this.numeroTarjeta = limpio;
    }

    private PagoTarjeta(BigDecimal monto) {
        super(monto);
        this.numeroTarjeta = null;
    }

    public static PagoTarjeta desdeRegistro(BigDecimal monto) {
        return new PagoTarjeta(monto);
    }

    @Override
    public String getMetodo() {
        return "TARJETA";
    }

    @Override
    public void procesar() {
        if (numeroTarjeta == null) {
            throw new ValidacionException("Este pago ya fue procesado");
        }
        String referencia = "REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        if (numeroTarjeta.endsWith("0")) {
            rechazar(referencia, "Tarjeta rechazada por el emisor");
        } else {
            aprobar(referencia);
        }
    }
}