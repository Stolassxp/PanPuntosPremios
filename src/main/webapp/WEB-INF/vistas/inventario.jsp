<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Historial de inventario</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 30px; }
        table { border-collapse: collapse; width: 100%; margin-top: 15px; }
        th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
        th { background: #f2f2f2; }
        .error { color: #b00020; font-weight: bold; }
    </style>
</head>
<body>
    <h1>Historial de inventario</h1>
    <p><a href="${pageContext.request.contextPath}/productos">Volver a productos</a></p>
    <c:if test="${not empty error}">
        <p class="error"><c:out value="${error}"/></p>
    </c:if>
    <table>
        <tr>
            <th>Fecha</th>
            <th>Código</th>
            <th>Producto</th>
            <th>Cantidad</th>
            <th>Tipo de movimiento</th>
        </tr>
        <c:forEach var="m" items="${movimientos}">
            <tr>
                <td><fmt:formatDate value="${m.fecha}" pattern="dd/MM/yyyy HH:mm"/></td>
                <td><c:out value="${m.codigoProducto}"/></td>
                <td><c:out value="${m.nombreProducto}"/></td>
                <td>${m.cantidad}</td>
                <td>${m.tipo}</td>
            </tr>
        </c:forEach>
    </table>
</body>
</html>