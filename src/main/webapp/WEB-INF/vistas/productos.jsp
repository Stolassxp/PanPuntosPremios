<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Productos</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 30px; }
        table { border-collapse: collapse; width: 100%; margin-top: 15px; }
        th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
        th { background: #f2f2f2; }
        .error { color: #b00020; font-weight: bold; }
        .inactivo { color: #888; }
        .bajo { background: #fff3cd; }
        form.en-linea { display: inline; }
    </style>
</head>
<body>
    <h1>Productos</h1>
    <p><a href="${pageContext.request.contextPath}/">Inicio</a></p>
    <c:if test="${not empty error}">
        <p class="error"><c:out value="${error}"/></p>
    </c:if>
    <form method="get" action="${pageContext.request.contextPath}/productos">
        <input type="text" name="buscar" placeholder="Nombre, código o categoría" value="<c:out value='${param.buscar}'/>">
        <button type="submit">Buscar</button>
        <a href="${pageContext.request.contextPath}/productos?accion=nuevo">Nuevo producto</a>
        <a href="${pageContext.request.contextPath}/productos?accion=historial">Historial de inventario</a>
    </form>
    <table>
        <tr>
            <th>Código</th>
            <th>Nombre</th>
            <th>Categoría</th>
            <th>Precio</th>
            <th>Existencia</th>
            <th>Estado</th>
            <th>Acciones</th>
        </tr>
        <c:forEach var="p" items="${productos}">
            <c:set var="esMenu" value="${p.categoria.name() eq 'MENU'}"/>
            <tr class="${!p.activo ? 'inactivo' : (!esMenu && p.existencia <= 5 ? 'bajo' : '')}">
                <td><c:out value="${p.codigo}"/></td>
                <td><c:out value="${p.nombre}"/></td>
                <td>${p.categoria}</td>
                <td>Q <fmt:formatNumber value="${p.precio}" minFractionDigits="2" maxFractionDigits="2"/></td>
                <td>${esMenu ? '—' : p.existencia}</td>
                <td>${p.activo ? 'Activo' : 'Inactivo'}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/productos?accion=editar&id=${p.idProducto}">Editar</a>
                    <form class="en-linea" method="post" action="${pageContext.request.contextPath}/productos">
                        <input type="hidden" name="accion" value="estado">
                        <input type="hidden" name="id" value="${p.idProducto}">
                        <input type="hidden" name="activo" value="${!p.activo}">
                        <button type="submit">${p.activo ? 'Desactivar' : 'Activar'}</button>
                    </form>
                    <c:if test="${!esMenu}">
                        <form class="en-linea" method="post" action="${pageContext.request.contextPath}/productos">
                            <input type="hidden" name="accion" value="abastecer">
                            <input type="hidden" name="id" value="${p.idProducto}">
                            <input type="number" name="cantidad" min="1" placeholder="Cant." style="width:70px">
                            <button type="submit">Abastecer</button>
                        </form>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
    </table>
</body>
</html>