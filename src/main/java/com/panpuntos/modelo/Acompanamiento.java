package com.panpuntos.modelo;

import java.math.BigDecimal;
import java.util.List;

public class Acompanamiento extends Producto {

    public Acompanamiento(int idProducto, String codigo, String nombre, BigDecimal precio, int existencia, boolean activo) {
        super(idProducto, codigo, nombre, precio, existencia, activo);
    }

    @Override
    public CategoriaProducto getCategoria() {
        return CategoriaProducto.ACOMPANAMIENTO;
    }

    @Override
    public int puntosOtorgados() {
        return 0;
    }

    @Override
    public List<Producto> componentes() {
        return List.of(this);
    }
}