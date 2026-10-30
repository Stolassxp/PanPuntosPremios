package com.panpuntos.modelo;

import java.math.BigDecimal;
import java.util.List;

public class Sandwich extends Producto {

    public Sandwich(int idProducto, String codigo, String nombre, BigDecimal precio, int existencia, boolean activo) {
        super(idProducto, codigo, nombre, precio, existencia, activo);
    }

    @Override
    public CategoriaProducto getCategoria() {
        return CategoriaProducto.SANDWICH;
    }

    @Override
    public int puntosOtorgados() {
        return 2;
    }

    @Override
    public List<Producto> componentes() {
        return List.of(this);
    }
}
