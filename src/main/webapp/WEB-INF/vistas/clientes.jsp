<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Clientes</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 30px; }
        table { border-collapse: collapse; width: 100%; margin-top: 15px; }
        th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
        th { background: #f2f2f2; }
        .error { color: #b00020; font-weight: bold; }
        .inactivo { color: #888; }
        form.en-linea { display: inline; }
    </style>
</head>
<body>
    <h1>Clientes</h1>
    <c:if test="${not empty error}">
        <p class="error"><c:out value="${error}"/></p>
    </c:if>
    <form method="get" action="${pageContext.request.contextPath}/clientes">
        <input type="text" name="buscar" placeholder="Nombre o identificador" value="<c:out value='${param.buscar}'/>">
        <button type="submit">Buscar</button>
        <a href="${pageContext.request.contextPath}/clientes?accion=nuevo">Nuevo cliente</a>
    </form>
    <table>
        <tr>
            <th>Identificador</th>
            <th>Nombre</th>
            <th>Teléfono</th>
            <th>Email</th>
            <th>Puntos</th>
            <th>Estado</th>
            <th>Acciones</th>
        </tr>
        <c:forEach var="c" items="${clientes}">
            <tr class="${c.activo ? '' : 'inactivo'}">
                <td><c:out value="${c.identificador}"/></td>
                <td><c:out value="${c.nombre}"/></td>
                <td><c:out value="${c.telefono}"/></td>
                <td><c:out value="${c.email}"/></td>
                <td>${c.saldoPuntos}</td>
                <td>${c.activo ? 'Activo' : 'Inactivo'}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/clientes?accion=editar&id=${c.idCliente}">Editar</a>
                    <form class="en-linea" method="post" action="${pageContext.request.contextPath}/clientes">
                        <input type="hidden" name="accion" value="estado">
                        <input type="hidden" name="id" value="${c.idCliente}">
                        <input type="hidden" name="activo" value="${!c.activo}">
                        <button type="submit">${c.activo ? 'Desactivar' : 'Activar'}</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </table>
</body>
</html>