-- Script de creación de base de datos para MySQL
-- Sistema de Inventario WIN EMPRESAS

-- Crear la base de datos
CREATE DATABASE IF NOT EXISTS BDVentas;
USE BDVentas;

-- Tabla de tipos de producto
CREATE TABLE IF NOT EXISTS TipoProducto (
    CodTipoProducto VARCHAR(15) PRIMARY KEY,
    DescTipoProducto VARCHAR(100) NOT NULL,
    Indicador CHAR(1) DEFAULT 'S'
);

-- Tabla principal de productos
CREATE TABLE IF NOT EXISTS Producto (
    CodProducto VARCHAR(15) PRIMARY KEY,
    DescProducto VARCHAR(255) NOT NULL,
    Precio DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    Stock INT NOT NULL DEFAULT 0,
    CodTipoProducto VARCHAR(15) NOT NULL,
    FechaCreacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    FechaModificacion DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    Indicador CHAR(1) DEFAULT 'S',
    FOREIGN KEY (CodTipoProducto) REFERENCES TipoProducto(CodTipoProducto)
);

-- Tabla de proveedores
CREATE TABLE IF NOT EXISTS Proveedor (
    CodProveedor VARCHAR(15) PRIMARY KEY,
    NombreProveedor VARCHAR(255) NOT NULL,
    RUC VARCHAR(15) UNIQUE,
    Telefono VARCHAR(20),
    Email VARCHAR(100),
    Direccion TEXT,
    Calificacion DECIMAL(3,2) DEFAULT 5.00,
    TotalOrdenes INT DEFAULT 0,
    MontoTotalCompras DECIMAL(15,2) DEFAULT 0.00,
    FechaCreacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    Indicador CHAR(1) DEFAULT 'S'
);

-- Tabla de órdenes de entrada (compras)
CREATE TABLE IF NOT EXISTS OrdenEntrada (
    CodOrdenEntrada VARCHAR(20) PRIMARY KEY,
    CodProveedor VARCHAR(15) NOT NULL,
    FechaOrden DATETIME DEFAULT CURRENT_TIMESTAMP,
    FechaEntrega DATETIME,
    EstadoOrden VARCHAR(20) DEFAULT 'PENDIENTE',
    MontoTotal DECIMAL(15,2) DEFAULT 0.00,
    UsuarioCreacion VARCHAR(50),
    Observaciones TEXT,
    Indicador CHAR(1) DEFAULT 'S',
    FOREIGN KEY (CodProveedor) REFERENCES Proveedor(CodProveedor)
);

-- Tabla de detalle de órdenes de entrada
CREATE TABLE IF NOT EXISTS DetalleOrdenEntrada (
    CodDetalleEntrada INT AUTO_INCREMENT PRIMARY KEY,
    CodOrdenEntrada VARCHAR(20) NOT NULL,
    CodProducto VARCHAR(15) NOT NULL,
    Cantidad INT NOT NULL,
    PrecioUnitario DECIMAL(10,2) NOT NULL,
    Subtotal DECIMAL(12,2) NOT NULL,
    FOREIGN KEY (CodOrdenEntrada) REFERENCES OrdenEntrada(CodOrdenEntrada),
    FOREIGN KEY (CodProducto) REFERENCES Producto(CodProducto)
);

-- Tabla de órdenes de salida (ventas)
CREATE TABLE IF NOT EXISTS OrdenSalida (
    CodOrdenSalida VARCHAR(20) PRIMARY KEY,
    Cliente VARCHAR(255) NOT NULL,
    FechaOrden DATETIME DEFAULT CURRENT_TIMESTAMP,
    FechaEntrega DATETIME,
    EstadoOrden VARCHAR(20) DEFAULT 'PENDIENTE',
    MontoTotal DECIMAL(15,2) DEFAULT 0.00,
    UsuarioCreacion VARCHAR(50),
    Observaciones TEXT,
    Indicador CHAR(1) DEFAULT 'S'
);

-- Tabla de detalle de órdenes de salida
CREATE TABLE IF NOT EXISTS DetalleOrdenSalida (
    CodDetalleSalida INT AUTO_INCREMENT PRIMARY KEY,
    CodOrdenSalida VARCHAR(20) NOT NULL,
    CodProducto VARCHAR(15) NOT NULL,
    Cantidad INT NOT NULL,
    PrecioUnitario DECIMAL(10,2) NOT NULL,
    Subtotal DECIMAL(12,2) NOT NULL,
    FOREIGN KEY (CodOrdenSalida) REFERENCES OrdenSalida(CodOrdenSalida),
    FOREIGN KEY (CodProducto) REFERENCES Producto(CodProducto)
);

-- Insertar datos iniciales
INSERT INTO TipoProducto (CodTipoProducto, DescTipoProducto) VALUES 
('TP2011000001', 'Producto General'),
('TP2011000002', 'Producto Electrónico'),
('TP2011000003', 'Producto Alimentario'),
('TP2011000004', 'Producto de Limpieza'),
('TP2011000005', 'Producto de Oficina');

-- Insertar proveedor de ejemplo
INSERT INTO Proveedor (CodProveedor, NombreProveedor, RUC, Telefono, Email, Direccion) VALUES 
('PROV001', 'Proveedor General SAC', '20123456789', '01-234-5678', 'contacto@proveedorgeneral.com', 'Av. Principal 123, Lima'),
('PROV002', 'Distribuidora Norte EIRL', '20987654321', '01-876-5432', 'ventas@distnorte.com', 'Jr. Comercio 456, Trujillo');

-- Insertar productos de ejemplo
INSERT INTO Producto (CodProducto, DescProducto, Precio, Stock, CodTipoProducto) VALUES 
('PROD0001', 'Laptop Dell Inspiron 15', 2500.00, 10, 'TP2011000002'),
('PROD0002', 'Mouse Logitech M100', 25.50, 50, 'TP2011000002'),
('PROD0003', 'Papel Bond A4 75gr', 15.00, 100, 'TP2011000005'),
('PROD0004', 'Detergente Ariel 1kg', 12.50, 25, 'TP2011000004'),
('PROD0005', 'Coca Cola 600ml', 3.50, 200, 'TP2011000003');

-- Crear índices para optimización
CREATE INDEX idx_producto_activo ON Producto(Indicador);
CREATE INDEX idx_producto_stock ON Producto(Stock);
CREATE INDEX idx_producto_precio ON Producto(Precio);
CREATE INDEX idx_proveedor_activo ON Proveedor(Indicador);
CREATE INDEX idx_orden_entrada_estado ON OrdenEntrada(EstadoOrden);
CREATE INDEX idx_orden_salida_estado ON OrdenSalida(EstadoOrden);
