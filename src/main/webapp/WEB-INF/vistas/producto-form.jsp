<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Producto</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 30px; }
        label { display: block; margin-top: 12px; }
        input[type=text], input[type=number], select { width: 300px; padding: 6px; }
        .error { color: #b00020; font-weight: bold; }
        button { margin-top: 15px; }
    </style>
</head>
<body>
    <h1>${empty param.id ? 'Nuevo producto' : 'Modificar producto'}</h1>
    <c:if test="${not empty error}">
        <p class="error"><c:out value="${error}"/></p>
    </c:if>
    <form method="post" action="${pageContext.request.contextPath}/productos">
        <input type="hidden" name="accion" value="guardar">
        <input type="hidden" name="id" value="<c:out value='${param.id}'/>">

        <c:choose>
            <c:when test="${empty param.id}">
                <label>Categoría</label>
                <select name="categoria">
                    <option value="SANDWICH" ${param.categoria == 'SANDWICH' ? 'selected' : ''}>Sándwich</option>
                    <option value="BEBIDA" ${param.categoria == 'BEBIDA' ? 'selected' : ''}>Bebida</option>
                    <option value="ACOMPANAMIENTO" ${param.categoria == 'ACOMPANAMIENTO' ? 'selected' : ''}>Acompañamiento</option>
                </select>
                <label>Código</label>
                <input type="text" name="codigo" value="<c:out value='${param.codigo}'/>">
            </c:when>
            <c:otherwise>
                <label>Código</label>
                <input type="text" name="codigo" value="<c:out value='${producto.codigo}'/>" readonly>
            </c:otherwise>
        </c:choose>

        <label>Nombre</label>
        <input type="text" name="nombre" value="<c:out value='${empty param.nombre ? producto.nombre : param.nombre}'/>">

        <label>Precio (Q)</label>
        <input type="text" name="precio" value="<c:out value='${empty param.precio ? producto.precio : param.precio}'/>">

        <c:if test="${empty param.id}">
            <label>Existencia inicial</label>
            <input type="number" name="existencia" min="0" value="<c:out value='${empty param.existencia ? 0 : param.existencia}'/>">
        </c:if>

        <br>
        <button type="submit">Guardar</button>
        <a href="${pageContext.request.contextPath}/productos">Cancelar</a>
    </form>
</body>
</html>