package com.panpuntos.servicio;

import com.panpuntos.dao.InventarioDAO;
import com.panpuntos.dao.ProductoDAO;
import com.panpuntos.excepcion.ReglaNegocioException;
import com.panpuntos.excepcion.ValidacionException;
import com.panpuntos.modelo.Acompanamiento;
import com.panpuntos.modelo.Bebida;
import com.panpuntos.modelo.CategoriaProducto;
import com.panpuntos.modelo.MovimientoInventario;
import com.panpuntos.modelo.Producto;
import com.panpuntos.modelo.Sandwich;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ProductoServicio {

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final InventarioDAO inventarioDAO = new InventarioDAO();

    public void registrar(String categoria, String codigo, String nombre, BigDecimal precio, int existenciaInicial)
            throws SQLException, ReglaNegocioException {
        if (existenciaInicial < 0) {
            throw new ValidacionException("La existencia inicial no puede ser negativa");
        }
        Producto nuevo = crear(categoria, codigo, nombre, precio);
        if (productoDAO.buscarPorCodigo(nuevo.getCodigo()) != null) {
            throw new ReglaNegocioException("Ya existe un producto con el código " + nuevo.getCodigo());
        }
        productoDAO.insertar(nuevo);
        if (existenciaInicial > 0) {
            inventarioDAO.abastecer(nuevo.getIdProducto(), existenciaInicial);
        }
    }

    private Producto crear(String categoriaTexto, String codigo, String nombre, BigDecimal precio) {
        CategoriaProducto categoria;
        try {
            categoria = CategoriaProducto.valueOf(categoriaTexto);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ValidacionException("La categoría no es válida");
        }
        switch (categoria) {
            case SANDWICH:
                return new Sandwich(0, codigo, nombre, precio, 0, true);
            case BEBIDA:
                return new Bebida(0, codigo, nombre, precio, 0, true);
            case ACOMPANAMIENTO:
                return new Acompanamiento(0, codigo, nombre, precio, 0, true);
            default:
                throw new ValidacionException("Solo se registran sándwiches, bebidas y acompañamientos");
        }
    }

    public void modificar(int idProducto, String nombre, BigDecimal precio)
            throws SQLException, ReglaNegocioException {
        Producto producto = obtener(idProducto);
        producto.setNombre(nombre);
        producto.setPrecio(precio);
        productoDAO.actualizar(producto);
    }

    public void cambiarEstado(int idProducto, boolean activo) throws SQLException, ReglaNegocioException {
        obtener(idProducto);
        productoDAO.cambiarEstado(idProducto, activo);
    }

    public void abastecer(int idProducto, int cantidad) throws SQLException, ReglaNegocioException {
        if (cantidad <= 0) {
            throw new ValidacionException("La cantidad debe ser positiva");
        }
        Producto producto = obtener(idProducto);
        if (producto.getCategoria() == CategoriaProducto.MENU) {
            throw new ReglaNegocioException("Los menús no tienen inventario propio, abastezca sus componentes");
        }
        inventarioDAO.abastecer(idProducto, cantidad);
    }

    public Producto obtener(int idProducto) throws SQLException, ReglaNegocioException {
        Producto producto = productoDAO.buscarPorId(idProducto);
        if (producto == null) {
            throw new ReglaNegocioException("El producto no existe");
        }
        return producto;
    }

    public List<Producto> listar(String texto) throws SQLException {
        List<Producto> todos = productoDAO.listar();
        if (texto == null || texto.isBlank()) {
            return todos;
        }
        String filtro = texto.trim().toLowerCase();
        return todos.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(filtro)
                        || p.getCodigo().toLowerCase().contains(filtro)
                        || p.getCategoria().name().toLowerCase().contains(filtro))
                .collect(Collectors.toList());
    }

    public List<MovimientoInventario> historial() throws SQLException {
        return inventarioDAO.listarMovimientos();
    }
}