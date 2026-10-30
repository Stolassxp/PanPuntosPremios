package com.panpuntos.modelo;

import com.panpuntos.excepcion.ValidacionException;
import java.math.BigDecimal;
import java.util.List;

public class Menu extends Producto {

    private final Sandwich sandwich;
    private final Bebida bebida;
    private final Acompanamiento acompanamiento;

    public Menu(int idProducto, String codigo, String nombre, BigDecimal precio, boolean activo,
                Sandwich sandwich, Bebida bebida, Acompanamiento acompanamiento) {
        super(idProducto, codigo, nombre, precio, 0, activo);
        if (sandwich == null || bebida == null || acompanamiento == null) {
            throw new ValidacionException("El menú requiere sándwich, bebida y acompañamiento");
        }
        this.sandwich = sandwich;
        this.bebida = bebida;
        this.acompanamiento = acompanamiento;
    }

    @Override
    public CategoriaProducto getCategoria() {
        return CategoriaProducto.MENU;
    }

    @Override
    public int puntosOtorgados() {
        return 8;
    }

    @Override
    public List<Producto> componentes() {
        return List.of(sandwich, bebida, acompanamiento);
    }

    @Override
    public boolean estaDisponible(int cantidad) {
        return isActivo() && sandwich.estaDisponible(cantidad)
                && bebida.estaDisponible(cantidad) && acompanamiento.estaDisponible(cantidad);
    }

    public Sandwich getSandwich() {
        return sandwich;
    }

    public Bebida getBebida() {
        return bebida;
    }

    public Acompanamiento getAcompanamiento() {
        return acompanamiento;
    }
}