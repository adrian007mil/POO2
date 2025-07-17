-- Script de actualización de base de datos para MySQL
-- Sistema de Inventario WIN EMPRESAS - VERSIÓN ACTUALIZADA
-- Soporte completo para la lógica de negocio actual

-- Crear la base de datos
CREATE DATABASE IF NOT EXISTS BDVentas;
USE BDVentas;

-- Eliminar tablas existentes para recrear con nueva estructura
DROP TABLE IF EXISTS DetalleOrdenSalida;
DROP TABLE IF EXISTS DetalleOrdenEntrada;
DROP TABLE IF EXISTS OrdenSalida;
DROP TABLE IF EXISTS OrdenEntrada;
DROP TABLE IF EXISTS ProductoProveedor;
DROP TABLE IF EXISTS Producto;
DROP TABLE IF EXISTS Categoria;
DROP TABLE IF EXISTS TipoProducto;
DROP TABLE IF EXISTS Proveedor;
DROP TABLE IF EXISTS Cliente;

-- Tabla de categorías (reemplaza TipoProducto)
CREATE TABLE IF NOT EXISTS Categoria (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    Codigo VARCHAR(15) UNIQUE NOT NULL,
    Nombre VARCHAR(100) NOT NULL,
    Descripcion TEXT,
    EsActivo BOOLEAN DEFAULT TRUE,
    FechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FechaModificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tabla de clientes
CREATE TABLE IF NOT EXISTS Cliente (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    Codigo VARCHAR(15) UNIQUE NOT NULL,
    Nombre VARCHAR(255) NOT NULL,
    RUC VARCHAR(15) UNIQUE,
    Telefono VARCHAR(20),
    Email VARCHAR(100),
    Direccion TEXT,
    EsActivo BOOLEAN DEFAULT TRUE,
    FechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FechaModificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tabla de proveedores (mejorada)
CREATE TABLE IF NOT EXISTS Proveedor (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    CodigoProveedor VARCHAR(15) UNIQUE NOT NULL,
    Nombre VARCHAR(255) NOT NULL,
    RUC VARCHAR(15) UNIQUE,
    Telefono VARCHAR(20),
    Email VARCHAR(100),
    Direccion TEXT,
    EsActivo BOOLEAN DEFAULT TRUE,
    FechaRegistro DATE DEFAULT (CURRENT_DATE),
    TotalOrdenes INT DEFAULT 0,
    MontoTotalCompras DECIMAL(15,2) DEFAULT 0.00,
    Calificacion DECIMAL(3,2) DEFAULT 5.00,
    FechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FechaModificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tabla de productos (mejorada)
CREATE TABLE IF NOT EXISTS Producto (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    CodigoProducto VARCHAR(15) UNIQUE NOT NULL,
    Nombre VARCHAR(255) NOT NULL,
    Descripcion TEXT,
    PrecioVenta DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    CantidadDisponible INT NOT NULL DEFAULT 0,
    CantidadMinima INT NOT NULL DEFAULT 5,
    EsActivo BOOLEAN DEFAULT TRUE,
    CategoriaID INT,
    FechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FechaModificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (CategoriaID) REFERENCES Categoria(ID) ON DELETE SET NULL
);

-- Tabla de relación Producto-Proveedor (NUEVA)
CREATE TABLE IF NOT EXISTS ProductoProveedor (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    ProductoID INT NOT NULL,
    ProveedorID INT NOT NULL,
    PrecioCompra DECIMAL(10,2) NOT NULL,
    TiempoEntrega INT DEFAULT 7,
    EsPreferido BOOLEAN DEFAULT FALSE,
    EsActivo BOOLEAN DEFAULT TRUE,
    FechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FechaModificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (ProductoID) REFERENCES Producto(ID) ON DELETE CASCADE,
    FOREIGN KEY (ProveedorID) REFERENCES Proveedor(ID) ON DELETE CASCADE,
    UNIQUE KEY unique_producto_proveedor (ProductoID, ProveedorID)
);

-- Tabla de órdenes de entrada (mejorada)
CREATE TABLE IF NOT EXISTS OrdenEntrada (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    CodigoOrden VARCHAR(20) UNIQUE NOT NULL,
    ProveedorID INT NOT NULL,
    FechaOrden TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FechaEntrega TIMESTAMP NULL,
    EstadoOrden ENUM('PENDIENTE', 'ENVIADO', 'APROBADA', 'RECIBIDA', 'PARCIAL', 'CANCELADA') DEFAULT 'PENDIENTE',
    MontoTotal DECIMAL(15,2) DEFAULT 0.00,
    UsuarioCreacion VARCHAR(50),
    Observaciones TEXT,
    EsActivo BOOLEAN DEFAULT TRUE,
    FechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FechaModificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (ProveedorID) REFERENCES Proveedor(ID) ON DELETE RESTRICT
);

-- Tabla de detalle de órdenes de entrada (mejorada)
CREATE TABLE IF NOT EXISTS DetalleOrdenEntrada (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    OrdenEntradaID INT NOT NULL,
    ProductoID INT NOT NULL,
    Cantidad INT NOT NULL,
    CantidadRecibida INT DEFAULT 0,
    PrecioUnitario DECIMAL(10,2) NOT NULL,
    Subtotal DECIMAL(12,2) NOT NULL,
    FechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (OrdenEntradaID) REFERENCES OrdenEntrada(ID) ON DELETE CASCADE,
    FOREIGN KEY (ProductoID) REFERENCES Producto(ID) ON DELETE RESTRICT
);

-- Tabla de órdenes de salida (mejorada)
CREATE TABLE IF NOT EXISTS OrdenSalida (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    CodigoOrden VARCHAR(20) UNIQUE NOT NULL,
    ClienteID INT,
    ClienteNombre VARCHAR(255) NOT NULL, -- Para compatibilidad con clientes no registrados
    FechaOrden TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FechaEntrega TIMESTAMP NULL,
    EstadoOrden ENUM('PENDIENTE', 'APROBADA', 'ENTREGADA', 'FACTURADA', 'CANCELADA') DEFAULT 'PENDIENTE',
    MontoTotal DECIMAL(15,2) DEFAULT 0.00,
    UsuarioCreacion VARCHAR(50),
    Observaciones TEXT,
    EsActivo BOOLEAN DEFAULT TRUE,
    FechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FechaModificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (ClienteID) REFERENCES Cliente(ID) ON DELETE SET NULL
);

-- Tabla de detalle de órdenes de salida (mejorada)
CREATE TABLE IF NOT EXISTS DetalleOrdenSalida (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    OrdenSalidaID INT NOT NULL,
    ProductoID INT NOT NULL,
    Cantidad INT NOT NULL,
    PrecioUnitario DECIMAL(10,2) NOT NULL,
    Subtotal DECIMAL(12,2) NOT NULL,
    FechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (OrdenSalidaID) REFERENCES OrdenSalida(ID) ON DELETE CASCADE,
    FOREIGN KEY (ProductoID) REFERENCES Producto(ID) ON DELETE RESTRICT
);

-- Insertar datos iniciales de categorías
INSERT INTO Categoria (Codigo, Nombre, Descripcion) VALUES 
('CAT0001', 'Electrónicos', 'Productos electrónicos y tecnológicos'),
('CAT0002', 'Oficina', 'Suministros y materiales de oficina'),
('CAT0003', 'Limpieza', 'Productos de limpieza y mantenimiento'),
('CAT0004', 'Alimentarios', 'Productos alimenticios y bebidas'),
('CAT0005', 'General', 'Productos de uso general');

-- Insertar proveedores de ejemplo
INSERT INTO Proveedor (CodigoProveedor, Nombre, RUC, Telefono, Email, Direccion) VALUES 
('PROV0001', 'TechSupply SAC', '20123456789', '01-234-5678', 'ventas@techsupply.com', 'Av. Arequipa 123, Lima'),
('PROV0002', 'DistribuTech EIRL', '20987654321', '01-876-5432', 'contacto@distributech.com', 'Jr. Comercio 456, Trujillo'),
('PROV0003', 'Suministros Globales SA', '20555666777', '01-555-6667', 'info@suministrosglobales.com', 'Av. Javier Prado 789, Lima');

-- Insertar productos de ejemplo
INSERT INTO Producto (CodigoProducto, Nombre, Descripcion, PrecioVenta, CantidadDisponible, CantidadMinima, CategoriaID) VALUES 
('PROD0001', 'Laptop Dell Inspiron 15', 'Laptop Dell Inspiron 15 con procesador Intel i5', 2500.00, 10, 5, 1),
('PROD0002', 'Mouse Logitech M100', 'Mouse óptico Logitech M100 con cable USB', 25.50, 50, 10, 1),
('PROD0003', 'Papel Bond A4 75gr', 'Papel Bond A4 75 gramos - Paquete 500 hojas', 15.00, 100, 20, 2),
('PROD0004', 'Detergente Ariel 1kg', 'Detergente en polvo Ariel 1 kilogramo', 12.50, 25, 10, 3),
('PROD0005', 'Coca Cola 600ml', 'Coca Cola botella 600ml', 3.50, 200, 50, 4);

-- Insertar relaciones producto-proveedor
INSERT INTO ProductoProveedor (ProductoID, ProveedorID, PrecioCompra, TiempoEntrega, EsPreferido) VALUES 
(1, 1, 2200.00, 7, TRUE),  -- Laptop Dell - TechSupply (preferido)
(1, 2, 2250.00, 10, FALSE), -- Laptop Dell - DistribuTech (alternativo)
(2, 1, 20.00, 3, TRUE),    -- Mouse Logitech - TechSupply (preferido)
(2, 3, 22.00, 5, FALSE),   -- Mouse Logitech - Suministros Globales (alternativo)
(3, 2, 12.00, 2, TRUE),    -- Papel Bond - DistribuTech (preferido)
(3, 3, 11.50, 3, FALSE),   -- Papel Bond - Suministros Globales (alternativo)
(4, 3, 10.00, 1, TRUE),    -- Detergente - Suministros Globales (preferido)
(5, 3, 2.80, 1, TRUE);     -- Coca Cola - Suministros Globales (preferido)

-- Insertar clientes de ejemplo
INSERT INTO Cliente (Codigo, Nombre, RUC, Telefono, Email, Direccion) VALUES 
('CLI0001', 'Empresa ABC SAC', '20111222333', '01-111-2223', 'compras@empresaabc.com', 'Av. Principal 100, Lima'),
('CLI0002', 'Comercial XYZ EIRL', '20444555666', '01-444-5556', 'ventas@comercialxyz.com', 'Jr. Comercio 200, Arequipa'),
('CLI0003', 'Distribuidora 123 SA', '20777888999', '01-777-8889', 'pedidos@dist123.com', 'Av. Industrial 300, Trujillo');

-- Crear índices para optimización
CREATE INDEX idx_producto_activo ON Producto(EsActivo);
CREATE INDEX idx_producto_stock ON Producto(CantidadDisponible);
CREATE INDEX idx_producto_categoria ON Producto(CategoriaID);
CREATE INDEX idx_proveedor_activo ON Proveedor(EsActivo);
CREATE INDEX idx_orden_entrada_estado ON OrdenEntrada(EstadoOrden);
CREATE INDEX idx_orden_salida_estado ON OrdenSalida(EstadoOrden);
CREATE INDEX idx_producto_proveedor_activo ON ProductoProveedor(EsActivo);
CREATE INDEX idx_producto_proveedor_preferido ON ProductoProveedor(EsPreferido);
CREATE INDEX idx_cliente_activo ON Cliente(EsActivo);

-- Crear vistas útiles para reportes
CREATE VIEW VistaProductosConStock AS
SELECT 
    p.CodigoProducto,
    p.Nombre,
    p.CantidadDisponible,
    p.CantidadMinima,
    c.Nombre AS Categoria,
    CASE 
        WHEN p.CantidadDisponible <= p.CantidadMinima THEN 'BAJO'
        WHEN p.CantidadDisponible <= p.CantidadMinima * 2 THEN 'MEDIO'
        ELSE 'ALTO'
    END AS NivelStock
FROM Producto p
LEFT JOIN Categoria c ON p.CategoriaID = c.ID
WHERE p.EsActivo = TRUE;

CREATE VIEW VistaProveedoresConProductos AS
SELECT 
    pr.CodigoProveedor,
    pr.Nombre AS NombreProveedor,
    COUNT(pp.ProductoID) AS CantidadProductos,
    pr.TotalOrdenes,
    pr.MontoTotalCompras
FROM Proveedor pr
LEFT JOIN ProductoProveedor pp ON pr.ID = pp.ProveedorID AND pp.EsActivo = TRUE
WHERE pr.EsActivo = TRUE
GROUP BY pr.ID, pr.CodigoProveedor, pr.Nombre, pr.TotalOrdenes, pr.MontoTotalCompras;

CREATE VIEW VistaOrdenesEntradaCompletas AS
SELECT 
    oe.CodigoOrden,
    pr.Nombre AS NombreProveedor,
    oe.FechaOrden,
    oe.EstadoOrden,
    oe.MontoTotal,
    COUNT(doe.ID) AS CantidadItems
FROM OrdenEntrada oe
JOIN Proveedor pr ON oe.ProveedorID = pr.ID
LEFT JOIN DetalleOrdenEntrada doe ON oe.ID = doe.OrdenEntradaID
WHERE oe.EsActivo = TRUE
GROUP BY oe.ID, oe.CodigoOrden, pr.Nombre, oe.FechaOrden, oe.EstadoOrden, oe.MontoTotal;

-- Procedimiento para actualizar stock automáticamente
DELIMITER //
CREATE PROCEDURE ActualizarStockProducto(
    IN p_producto_id INT,
    IN p_cantidad INT,
    IN p_tipo_movimiento ENUM('ENTRADA', 'SALIDA')
)
BEGIN
    DECLARE v_stock_actual INT;
    
    -- Obtener stock actual
    SELECT CantidadDisponible INTO v_stock_actual
    FROM Producto
    WHERE ID = p_producto_id;
    
    -- Actualizar según tipo de movimiento
    IF p_tipo_movimiento = 'ENTRADA' THEN
        UPDATE Producto 
        SET CantidadDisponible = CantidadDisponible + p_cantidad,
            FechaModificacion = CURRENT_TIMESTAMP
        WHERE ID = p_producto_id;
    ELSEIF p_tipo_movimiento = 'SALIDA' THEN
        UPDATE Producto 
        SET CantidadDisponible = GREATEST(0, CantidadDisponible - p_cantidad),
            FechaModificacion = CURRENT_TIMESTAMP
        WHERE ID = p_producto_id;
    END IF;
END //
DELIMITER ;

-- Función para obtener el mejor precio de un producto
DELIMITER //
CREATE FUNCTION ObtenerMejorPrecio(p_producto_id INT) 
RETURNS DECIMAL(10,2)
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE v_mejor_precio DECIMAL(10,2);
    
    SELECT MIN(PrecioCompra) INTO v_mejor_precio
    FROM ProductoProveedor
    WHERE ProductoID = p_producto_id AND EsActivo = TRUE;
    
    RETURN IFNULL(v_mejor_precio, 0.00);
END //
DELIMITER ;

-- Trigger para actualizar estadísticas de proveedor
DELIMITER //
CREATE TRIGGER ActualizarEstadisticasProveedor
AFTER UPDATE ON OrdenEntrada
FOR EACH ROW
BEGIN
    IF NEW.EstadoOrden = 'RECIBIDA' AND OLD.EstadoOrden != 'RECIBIDA' THEN
        UPDATE Proveedor 
        SET TotalOrdenes = TotalOrdenes + 1,
            MontoTotalCompras = MontoTotalCompras + NEW.MontoTotal
        WHERE ID = NEW.ProveedorID;
    END IF;
END //
DELIMITER ;

-- Mostrar resumen de la estructura creada
SELECT 'Base de datos actualizada correctamente' AS Mensaje;
SELECT 'Tablas creadas:' AS Info;
SELECT TABLE_NAME AS Tabla FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'BDVentas';
