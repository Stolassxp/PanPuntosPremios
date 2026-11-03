<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Pedidos</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 30px; }
        table { border-collapse: collapse; width: 100%; margin-top: 15px; }
        th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
        th { background: #f2f2f2; }
        .error { color: #b00020; font-weight: bold; }
    </style>
</head>
<body>
    <h1>Pedidos</h1>
    <p><a href="${pageContext.request.contextPath}/">Inicio</a> |
       <a href="${pageContext.request.contextPath}/pedidos?accion=nuevo">Nuevo pedido</a></p>
    <c:if test="${not empty error}">
        <p class="error"><c:out value="${error}"/></p>
    </c:if>
    <table>
        <tr>
            <th>No.</th>
            <th>Fecha</th>
            <th>Cliente</th>
            <th>Estado</th>
            <th>Total</th>
            <th>Puntos</th>
            <th></th>
        </tr>
        <c:forEach var="p" items="${pedidos}">
            <tr>
                <td>${p.idPedido}</td>
                <td><fmt:formatDate value="${p.fecha}" pattern="dd/MM/yyyy HH:mm"/></td>
                <td><c:out value="${p.cliente.nombre}"/></td>
                <td>${p.estado}</td>
                <td>Q <fmt:formatNumber value="${p.total}" minFractionDigits="2" maxFractionDigits="2"/></td>
                <td>${p.puntosGenerados}</td>
                <td><a href="${pageContext.request.contextPath}/pedidos?accion=ver&id=${p.idPedido}">Ver</a></td>
            </tr>
        </c:forEach>
    </table>
</body>
</html>