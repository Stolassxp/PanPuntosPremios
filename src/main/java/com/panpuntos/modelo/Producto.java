package com.panpuntos.modelo;

import com.panpuntos.excepcion.ReglaNegocioException;
import com.panpuntos.excepcion.ValidacionException;
import java.math.BigDecimal;
import java.util.List;

public abstract class Producto {

    private int idProducto;
    private String codigo;
    private String nombre;
    private BigDecimal precio;
    private int existencia;
    private boolean activo;

    protected Producto(int idProducto, String codigo, String nombre, BigDecimal precio, int existencia, boolean activo) {
        this.idProducto = idProducto;
        setCodigo(codigo);
        setNombre(nombre);
        setPrecio(precio);
        setExistencia(existencia);
        this.activo = activo;
    }

    public abstract CategoriaProducto getCategoria();

    public abstract int puntosOtorgados();

    public abstract List<Producto> componentes();

    public boolean estaDisponible(int cantidad) {
        return activo && existencia >= cantidad;
    }

    public void aumentarExistencia(int cantidad) {
        if (cantidad <= 0) {
            throw new ValidacionException("La cantidad debe ser positiva");
        }
        existencia += cantidad;
    }

    public void reducirExistencia(int cantidad) throws ReglaNegocioException {
        if (cantidad > existencia) {
            throw new ReglaNegocioException("Inventario insuficiente de " + nombre + ". Disponible: " + existencia);
        }
        existencia -= cantidad;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getCodigo() {
        return codigo;
    }

    public final void setCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new ValidacionException("El código es obligatorio");
        }
        this.codigo = codigo.trim();
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

    public BigDecimal getPrecio() {
        return precio;
    }

    public final void setPrecio(BigDecimal precio) {
        if (precio == null || precio.signum() < 0) {
            throw new ValidacionException("El precio no puede ser negativo");
        }
        this.precio = precio;
    }

    public int getExistencia() {
        return existencia;
    }

    public final void setExistencia(int existencia) {
        if (existencia < 0) {
            throw new ValidacionException("La existencia no puede ser negativa");
        }
        this.existencia = existencia;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}