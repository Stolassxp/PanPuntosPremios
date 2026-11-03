package com.panpuntos.modelo;

public class Recompensa {

    private final int idRecompensa;
    private final String nombre;
    private final int puntosRequeridos;
    private final Producto producto;
    private final boolean activa;

    public Recompensa(int idRecompensa, String nombre, int puntosRequeridos, Producto producto, boolean activa) {
        this.idRecompensa = idRecompensa;
        this.nombre = nombre;
        this.puntosRequeridos = puntosRequeridos;
        this.producto = producto;
        this.activa = activa;
    }

    public int getIdRecompensa() {
        return idRecompensa;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPuntosRequeridos() {
        return puntosRequeridos;
    }

    public Producto getProducto() {
        return producto;
    }

    public boolean isActiva() {
        return activa;
    }
}