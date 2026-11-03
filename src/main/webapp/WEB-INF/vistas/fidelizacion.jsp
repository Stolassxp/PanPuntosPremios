<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Fidelización</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 30px; }
        table { border-collapse: collapse; width: 100%; margin: 10px 0 25px; }
        th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
        th { background: #f2f2f2; }
        select { padding: 6px; min-width: 300px; }
        .error { color: #b00020; font-weight: bold; }
        .ok { color: #1b6e2e; font-weight: bold; }
        .saldo { font-size: 1.3em; }
    </style>
</head>
<body>
    <h1>Fidelización y canjes</h1>
    <p><a href="${pageContext.request.contextPath}/">Inicio</a></p>
    <c:if test="${not empty error}">
        <p class="error"><c:out value="${error}"/></p>
    </c:if>
    <c:if test="${ok}">
        <p class="ok">Canje realizado correctamente.</p>
    </c:if>

    <form method="get" action="${pageContext.request.contextPath}/fidelizacion">
        <label>Cliente</label>
        <select name="cliente">
            <option value="">-- Seleccione --</option>
            <c:forEach var="c" items="${clientes}">
                <option value="${c.idCliente}" ${param.cliente == c.idCliente ? 'selected' : ''}>
                    <c:out value="${c.nombre}"/> (<c:out value="${c.identificador}"/>)${c.activo ? '' : ' - inactivo'}
                </option>
            </c:forEach>
        </select>
        <button type="submit">Ver</button>
    </form>

    <c:if test="${not empty cliente}">
        <h2><c:out value="${cliente.nombre}"/> ${cliente.activo ? '' : '(inactivo)'}</h2>
        <p class="saldo">Saldo: <strong>${cliente.saldoPuntos}</strong> puntos</p>
        <p><a href="${pageContext.request.contextPath}/pedidos?cliente=${cliente.idCliente}">Ver historial de pedidos</a></p>

        <h3>Recompensas</h3>
        <table>
            <tr>
                <th>Recompensa</th>
                <th>Producto</th>
                <th>Puntos</th>
                <th>Estado</th>
                <th></th>
            </tr>
            <c:forEach var="r" items="${recompensas}">
                <tr>
                    <td><c:out value="${r.nombre}"/></td>
                    <td>
                        <c:out value="${r.producto.nombre}"/>
                        <c:if test="${r.producto.categoria.name() eq 'MENU'}">
                            <br><small>Incluye:
                                <c:forEach var="comp" items="${r.producto.componentes()}" varStatus="st">
                                    <c:out value="${comp.nombre}"/>${st.last ? '' : ', '}
                                </c:forEach>
                            </small>
                        </c:if>
                    </td>
                    <td>${r.puntosRequeridos}</td>
                    <td>${r.activa ? 'Activa' : 'Inactiva'}</td>
                    <td>
                        <form method="post" action="${pageContext.request.contextPath}/fidelizacion">
                            <input type="hidden" name="cliente" value="${cliente.idCliente}">
                            <input type="hidden" name="recompensa" value="${r.idRecompensa}">
                            <button type="submit">Canjear</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>

        <h3>Movimientos de puntos</h3>
        <table>
            <tr>
                <th>Fecha</th>
                <th>Tipo</th>
                <th>Referencia</th>
                <th>Puntos</th>
            </tr>
            <c:forEach var="m" items="${movimientos}">
                <tr>
                    <td><fmt:formatDate value="${m.fecha}" pattern="dd/MM/yyyy HH:mm"/></td>
                    <td>${m.tipo}</td>
                    <td><c:out value="${m.referencia}"/></td>
                    <td>${m.puntos}</td>
                </tr>
            </c:forEach>
        </table>
    </c:if>

    <h3>Historial de canjes ${empty cliente ? '(todos los clientes)' : ''}</h3>
    <table>
        <tr>
            <th>Fecha</th>
            <th>Cliente</th>
            <th>Recompensa</th>
            <th>Puntos usados</th>
        </tr>
        <c:forEach var="k" items="${canjes}">
            <tr>
                <td><fmt:formatDate value="${k.fecha}" pattern="dd/MM/yyyy HH:mm"/></td>
                <td><c:out value="${k.nombreCliente}"/> (<c:out value="${k.identificador}"/>)</td>
                <td><c:out value="${k.recompensa}"/></td>
                <td>${k.puntosUsados}</td>
            </tr>
        </c:forEach>
    </table>
</body>
</html>