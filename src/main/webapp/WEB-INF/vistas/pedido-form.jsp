<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Nuevo pedido</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 30px; }
        table { border-collapse: collapse; width: 100%; margin-top: 15px; }
        th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
        th { background: #f2f2f2; }
        select { padding: 6px; min-width: 300px; }
        .error { color: #b00020; font-weight: bold; }
        button { margin-top: 15px; }
    </style>
</head>
<body>
    <h1>Nuevo pedido</h1>
    <c:if test="${not empty error}">
        <p class="error"><c:out value="${error}"/></p>
    </c:if>
    <form method="post" action="${pageContext.request.contextPath}/pedidos">
        <input type="hidden" name="accion" value="crear">
        <label>Cliente</label>
        <select name="cliente">
            <option value="">-- Seleccione --</option>
            <c:forEach var="c" items="${clientes}">
                <option value="${c.idCliente}" ${param.cliente == c.idCliente ? 'selected' : ''}>
                    <c:out value="${c.nombre}"/> (<c:out value="${c.identificador}"/>)
                </option>
            </c:forEach>
        </select>
        <table>
            <tr>
                <th>Código</th>
                <th>Producto</th>
                <th>Categoría</th>
                <th>Precio</th>
                <th>Existencia</th>
                <th>Cantidad</th>
            </tr>
            <c:forEach var="p" items="${productos}">
                <c:set var="esMenu" value="${p.categoria.name() eq 'MENU'}"/>
                <c:set var="clave" value="cant_${p.idProducto}"/>
                <tr>
                    <td><c:out value="${p.codigo}"/></td>
                    <td>
                        <c:out value="${p.nombre}"/>
                        <c:if test="${esMenu}">
                            <br><small>Incluye:
                                <c:forEach var="comp" items="${p.componentes()}" varStatus="st">
                                    <c:out value="${comp.nombre}"/>${st.last ? '' : ', '}
                                </c:forEach>
                            </small>
                        </c:if>
                    </td>
                    <td>${p.categoria}</td>
                    <td>Q <fmt:formatNumber value="${p.precio}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    <td>${esMenu ? '—' : p.existencia}</td>
                    <td><input type="number" name="cant_${p.idProducto}" min="0" style="width:70px"
                               value="<c:out value='${param[clave]}'/>"></td>
                </tr>
            </c:forEach>
        </table>
        <button type="submit">Confirmar pedido</button>
        <a href="${pageContext.request.contextPath}/pedidos">Cancelar</a>
    </form>
</body>
</html>