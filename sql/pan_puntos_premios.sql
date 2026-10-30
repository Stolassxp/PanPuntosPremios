DROP DATABASE IF EXISTS pan_puntos_premios;
CREATE DATABASE pan_puntos_premios CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE pan_puntos_premios;

CREATE TABLE cliente (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    identificador VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    email VARCHAR(100),
    saldo_puntos INT NOT NULL DEFAULT 0,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_cliente_saldo CHECK (saldo_puntos >= 0)
);

CREATE TABLE producto (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    categoria ENUM('SANDWICH', 'BEBIDA', 'ACOMPANAMIENTO', 'MENU') NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    existencia INT NOT NULL DEFAULT 0,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_producto_precio CHECK (precio >= 0),
    CONSTRAINT chk_producto_existencia CHECK (existencia >= 0)
);

CREATE TABLE menu (
    id_producto INT PRIMARY KEY,
    id_sandwich INT NOT NULL,
    id_bebida INT NOT NULL,
    id_acompanamiento INT NOT NULL,
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto),
    FOREIGN KEY (id_sandwich) REFERENCES producto(id_producto),
    FOREIGN KEY (id_bebida) REFERENCES producto(id_producto),
    FOREIGN KEY (id_acompanamiento) REFERENCES producto(id_producto)
);

CREATE TABLE pedido (
    id_pedido INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado ENUM('PENDIENTE', 'PAGADO', 'ENTREGADO', 'ANULADO') NOT NULL DEFAULT 'PENDIENTE',
    total DECIMAL(10,2) NOT NULL DEFAULT 0,
    FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)
);

CREATE TABLE detalle_pedido (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    CONSTRAINT chk_detalle_cantidad CHECK (cantidad > 0),
    FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido),
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
);

CREATE TABLE pago (
    id_pago INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    metodo ENUM('EFECTIVO', 'TARJETA') NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    monto_recibido DECIMAL(10,2),
    cambio DECIMAL(10,2),
    referencia VARCHAR(30),
    estado ENUM('APROBADO', 'RECHAZADO') NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido)
);

CREATE TABLE movimiento_inventario (
    id_movimiento INT AUTO_INCREMENT PRIMARY KEY,
    id_producto INT NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cantidad INT NOT NULL,
    tipo ENUM('ABASTECIMIENTO', 'VENTA', 'CANJE') NOT NULL,
    id_pedido INT,
    CONSTRAINT chk_mov_inv_cantidad CHECK (cantidad > 0),
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto),
    FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido)
);

CREATE TABLE recompensa (
    id_recompensa INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    puntos_requeridos INT NOT NULL,
    id_producto INT NOT NULL,
    activa BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_recompensa_puntos CHECK (puntos_requeridos > 0),
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
);

CREATE TABLE canje (
    id_canje INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_recompensa INT NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    puntos_usados INT NOT NULL,
    FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
    FOREIGN KEY (id_recompensa) REFERENCES recompensa(id_recompensa)
);

CREATE TABLE movimiento_puntos (
    id_movimiento INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    puntos INT NOT NULL,
    tipo ENUM('ACUMULACION', 'CANJE') NOT NULL,
    id_pedido INT,
    id_canje INT,
    FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
    FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido),
    FOREIGN KEY (id_canje) REFERENCES canje(id_canje)
);

INSERT INTO cliente (identificador, nombre, telefono, email, saldo_puntos, activo) VALUES
('2456789010101', 'María López', '55501234', 'maria.lopez@correo.com', 0, TRUE),
('2987654320101', 'Carlos Méndez', '55505678', 'carlos.mendez@correo.com', 0, TRUE),
('3012345670101', 'Ana Ramírez', '55509012', 'ana.ramirez@correo.com', 0, TRUE),
('2765432100101', 'José Pérez', '55503456', 'jose.perez@correo.com', 0, FALSE);

INSERT INTO producto (codigo, categoria, nombre, precio, existencia, activo) VALUES
('S001', 'SANDWICH', 'Sándwich de Pollo', 35.00, 20, TRUE),
('S002', 'SANDWICH', 'Sándwich de Jamón y Queso', 30.00, 20, TRUE),
('S003', 'SANDWICH', 'Sándwich Club', 40.00, 15, TRUE),
('S004', 'SANDWICH', 'Sándwich Vegetariano', 32.00, 3, TRUE),
('B001', 'BEBIDA', 'Gaseosa', 12.00, 30, TRUE),
('B002', 'BEBIDA', 'Jugo Natural', 15.00, 25, TRUE),
('B003', 'BEBIDA', 'Agua Pura', 8.00, 0, TRUE),
('B004', 'BEBIDA', 'Limonada', 14.00, 20, TRUE),
('A001', 'ACOMPANAMIENTO', 'Papas Fritas', 15.00, 30, TRUE),
('A002', 'ACOMPANAMIENTO', 'Aros de Cebolla', 18.00, 20, TRUE),
('A003', 'ACOMPANAMIENTO', 'Ensalada', 16.00, 4, TRUE),
('M001', 'MENU', 'Menú Pollo', 55.00, 0, TRUE),
('M002', 'MENU', 'Menú Jamón y Queso', 50.00, 0, TRUE),
('M003', 'MENU', 'Menú Club', 60.00, 0, TRUE);

INSERT INTO menu (id_producto, id_sandwich, id_bebida, id_acompanamiento)
SELECT m.id_producto, s.id_producto, b.id_producto, a.id_producto
FROM producto m, producto s, producto b, producto a
WHERE m.codigo = 'M001' AND s.codigo = 'S001' AND b.codigo = 'B001' AND a.codigo = 'A001';

INSERT INTO menu (id_producto, id_sandwich, id_bebida, id_acompanamiento)
SELECT m.id_producto, s.id_producto, b.id_producto, a.id_producto
FROM producto m, producto s, producto b, producto a
WHERE m.codigo = 'M002' AND s.codigo = 'S002' AND b.codigo = 'B002' AND a.codigo = 'A001';

INSERT INTO menu (id_producto, id_sandwich, id_bebida, id_acompanamiento)
SELECT m.id_producto, s.id_producto, b.id_producto, a.id_producto
FROM producto m, producto s, producto b, producto a
WHERE m.codigo = 'M003' AND s.codigo = 'S003' AND b.codigo = 'B004' AND a.codigo = 'A002';

INSERT INTO movimiento_inventario (id_producto, cantidad, tipo)
SELECT id_producto, existencia, 'ABASTECIMIENTO'
FROM producto
WHERE categoria <> 'MENU' AND existencia > 0;

INSERT INTO recompensa (nombre, puntos_requeridos, id_producto, activa)
SELECT 'Bebida gratuita', 10, id_producto, TRUE FROM producto WHERE codigo = 'B001';

INSERT INTO recompensa (nombre, puntos_requeridos, id_producto, activa)
SELECT 'Acompañamiento gratuito', 15, id_producto, TRUE FROM producto WHERE codigo = 'A001';

INSERT INTO recompensa (nombre, puntos_requeridos, id_producto, activa)
SELECT 'Sándwich gratuito', 30, id_producto, TRUE FROM producto WHERE codigo = 'S001';

INSERT INTO recompensa (nombre, puntos_requeridos, id_producto, activa)
SELECT 'Menú gratuito', 50, id_producto, TRUE FROM producto WHERE codigo = 'M001';
