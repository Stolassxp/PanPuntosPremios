<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Cliente</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 30px; }
        label { display: block; margin-top: 12px; }
        input[type=text], input[type=email] { width: 300px; padding: 6px; }
        .error { color: #b00020; font-weight: bold; }
        button { margin-top: 15px; }
    </style>
</head>
<body>
    <h1>${empty param.id ? 'Nuevo cliente' : 'Modificar cliente'}</h1>
    <c:if test="${not empty error}">
        <p class="error"><c:out value="${error}"/></p>
    </c:if>
    <form method="post" action="${pageContext.request.contextPath}/clientes">
        <input type="hidden" name="accion" value="guardar">
        <input type="hidden" name="id" value="<c:out value='${param.id}'/>">

        <label>Identificador</label>
        <c:choose>
            <c:when test="${empty param.id}">
                <input type="text" name="identificador" value="<c:out value='${param.identificador}'/>">
            </c:when>
            <c:otherwise>
                <input type="text" name="identificador" value="<c:out value='${cliente.identificador}'/>" readonly>
            </c:otherwise>
        </c:choose>

        <label>Nombre</label>
        <input type="text" name="nombre" value="<c:out value='${empty param.nombre ? cliente.nombre : param.nombre}'/>">

        <label>Teléfono</label>
        <input type="text" name="telefono" value="<c:out value='${empty param.telefono ? cliente.telefono : param.telefono}'/>">

        <label>Email</label>
        <input type="text" name="email" value="<c:out value='${empty param.email ? cliente.email : param.email}'/>">

        <br>
        <button type="submit">Guardar</button>
        <a href="${pageContext.request.contextPath}/clientes">Cancelar</a>
    </form>
</body>
</html>