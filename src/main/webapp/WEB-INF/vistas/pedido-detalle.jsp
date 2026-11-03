<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Pedido ${pedido.idPedido}</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 30px; }
        table { border-collapse: collapse; width: 100%; margin-top: 15px; }
        th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
        th { background: #f2f2f2; }
        .error { color: #b00020; font-weight: bold; }
        .caja { border: 1px solid #999; padding: 15px; margin-top: 20px; max-width: 420px; }
        .caja label { display: block; margin-top: 8px; }
        form.en-linea { display: inline; }
        @media print { a, button, form { display: none; } }
    </style>
</head>
<body>
    <h1>Pedido No. ${pedido.idPedido}</h1>
    <p><a href="${pageContext.request.contextPath}/pedidos">Volver a pedidos</a></p>
    <c:if test="${not empty error}">
        <p class="error"><c:out value="${error}"/></p>
    </c:if>
    <p>
        <strong>Cliente:</strong> <c:out value="${pedido.cliente.nombre}"/>
        (<c:out value="${pedido.cliente.identificador}"/>)<br>
        <strong>Fecha:</strong> <fmt:formatDate value="${pedido.fecha}" pattern="dd/MM/yyyy HH:mm"/><br>
        <strong>Estado:</strong> ${pedido.estado}
    </p>
    <table>
        <tr>
            <th>Código</th>
            <th>Producto</th>
            <th>Cantidad</th>
            <th>Precio</th>
            <th>Subtotal</th>
        </tr>
        <c:forEach var="d" items="${pedido.detalles}">
            <tr>
                <td><c:out value="${d.producto.codigo}"/></td>
                <td>
                    <c:out value="${d.producto.nombre}"/>
                    <c:if test="${d.producto.categoria.name() eq 'MENU'}">
                        <br><small>Descuenta:
                            <c:forEach var="comp" items="${d.producto.componentes()}" varStatus="st">
                                <c:out value="${comp.nombre}"/>${st.last ? '' : ', '}
                            </c:forEach>
                        </small>
                    </c:if>
                </td>
                <td>${d.cantidad}</td>
                <td>Q <fmt:formatNumber value="${d.precioUnitario}" minFractionDigits="2" maxFractionDigits="2"/></td>
                <td>Q <fmt:formatNumber value="${d.subtotal}" minFractionDigits="2" maxFractionDigits="2"/></td>
            </tr>
        </c:forEach>
    </table>
    <p>
        <strong>Total: Q <fmt:formatNumber value="${pedido.total}" minFractionDigits="2" maxFractionDigits="2"/></strong><br>
        <c:choose>
            <c:when test="${pedido.pendiente}">Puntos que se acreditarán al pagar: ${pedido.puntosGenerados}</c:when>
            <c:when test="${pedido.estado.name() eq 'ANULADO'}">Pedido anulado: no genera puntos.</c:when>
            <c:otherwise>Puntos acreditados: ${pedido.puntosGenerados}</c:otherwise>
        </c:choose>
    </p>

    <c:if test="${pedido.pendiente}">
        <div class="caja">
            <h3>Cobrar en efectivo</h3>
            <form method="post" action="${pageContext.request.contextPath}/pedidos">
                <input type="hidden" name="accion" value="cobrar">
                <input type="hidden" name="id" value="${pedido.idPedido}">
                <input type="hidden" name="metodo" value="EFECTIVO">
                <label>Monto recibido (Q)</label>
                <input type="text" name="monto">
                <button type="submit">Cobrar</button>
            </form>
        </div>
        <div class="caja">
            <h3>Cobrar con tarjeta</h3>
            <form method="post" action="${pageContext.request.contextPath}/pedidos">
                <input type="hidden" name="accion" value="cobrar">
                <input type="hidden" name="id" value="${pedido.idPedido}">
                <input type="hidden" name="metodo" value="TARJETA">
                <label>Número de tarjeta</label>
                <input type="text" name="tarjeta" maxlength="23">
                <button type="submit">Cobrar</button>
            </form>
        </div>
        <form method="post" action="${pageContext.request.contextPath}/pedidos">
            <input type="hidden" name="accion" value="anular">
            <input type="hidden" name="id" value="${pedido.idPedido}">
            <button type="submit">Anular pedido</button>
        </form>
    </c:if>

    <c:if test="${not empty pedido.pago}">
        <div class="caja">
            <h3>Comprobante de pago</h3>
            <p>
                <strong>Método:</strong> ${pedido.pago.metodo}<br>
                <strong>Monto:</strong> Q <fmt:formatNumber value="${pedido.pago.monto}" minFractionDigits="2" maxFractionDigits="2"/><br>
                <c:if test="${pedido.pago.metodo eq 'EFECTIVO'}">
                    <strong>Recibido:</strong> Q <fmt:formatNumber value="${pedido.pago.montoRecibido}" minFractionDigits="2" maxFractionDigits="2"/><br>
                    <strong>Cambio:</strong> Q <fmt:formatNumber value="${pedido.pago.cambio}" minFractionDigits="2" maxFractionDigits="2"/><br>
                </c:if>
                <c:if test="${pedido.pago.metodo eq 'TARJETA'}">
                    <strong>Referencia:</strong> <c:out value="${pedido.pago.referencia}"/><br>
                </c:if>
                <strong>Fecha de pago:</strong> <fmt:formatDate value="${pedido.pago.fecha}" pattern="dd/MM/yyyy HH:mm"/>
            </p>
        </div>
    </c:if>

    <c:if test="${pedido.pagado}">
        <form method="post" action="${pageContext.request.contextPath}/pedidos">
            <input type="hidden" name="accion" value="entregar">
            <input type="hidden" name="id" value="${pedido.idPedido}">
            <button type="submit">Marcar como entregado</button>
        </form>
    </c:if>
    <c:if test="${not empty pedido.pago}">
        <button type="button" onclick="window.print()">Imprimir comprobante</button>
    </c:if>
</body>
</html>