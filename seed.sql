PRAGMA foreign_keys = ON;

-- =========================================
-- TABLA: CATEGORIAS
-- =========================================
CREATE TABLE categorias (
    id_categoria INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL UNIQUE,
    descripcion TEXT
);

-- =========================================
-- TABLA: PRODUCTOS
-- =========================================
CREATE TABLE productos (
    id_producto INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    descripcion TEXT,
    precio REAL NOT NULL CHECK (precio >= 0),
    stock INTEGER NOT NULL DEFAULT 0 CHECK (stock >= 0),
    stock_minimo INTEGER NOT NULL DEFAULT 5 CHECK (stock_minimo >= 0),
    id_categoria INTEGER NOT NULL,

    FOREIGN KEY (id_categoria)
        REFERENCES categorias(id_categoria)
);

-- =========================================
-- TABLA: VENTAS
-- =========================================
CREATE TABLE ventas (
    id_venta INTEGER PRIMARY KEY AUTOINCREMENT,
    fecha TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total REAL NOT NULL DEFAULT 0 CHECK (total >= 0)
);

-- =========================================
-- TABLA: DETALLE DE VENTAS
-- =========================================
CREATE TABLE detalle_ventas (
    id_detalle INTEGER PRIMARY KEY AUTOINCREMENT,
    id_venta INTEGER NOT NULL,
    id_producto INTEGER NOT NULL,
    cantidad INTEGER NOT NULL CHECK (cantidad > 0),
    precio_unitario REAL NOT NULL CHECK (precio_unitario >= 0),

    FOREIGN KEY (id_venta)
        REFERENCES ventas(id_venta),

    FOREIGN KEY (id_producto)
        REFERENCES productos(id_producto)
);

-- =========================================
-- DATOS DE CATEGORIAS
-- =========================================

INSERT INTO categorias (nombre, descripcion) VALUES
('Laptops', 'Computadoras portátiles'),
('Smartphones', 'Teléfonos inteligentes'),
('Monitores', 'Monitores y pantallas'),
('Periféricos', 'Teclados, mouse y accesorios'),
('Componentes', 'Componentes para PC'),
('Audio', 'Audífonos, parlantes y accesorios de audio');

-- =========================================
-- DATOS DE PRODUCTOS
-- =========================================

INSERT INTO productos
(nombre, descripcion, precio, stock, stock_minimo, id_categoria)
VALUES
('Laptop Lenovo IdeaPad 3',
 'Laptop Intel Core i5, 8GB RAM, 512GB SSD',
 1899.90, 12, 5, 1),

('Laptop ASUS VivoBook 15',
 'Laptop Intel Core i7, 16GB RAM, 512GB SSD',
 2599.90, 4, 5, 1),

('MacBook Air M3',
 'Laptop Apple con chip M3, 16GB RAM',
 4299.90, 7, 3, 1),

('Samsung Galaxy S24',
 'Smartphone Samsung Galaxy S24 256GB',
 2899.90, 15, 5, 2),

('iPhone 15',
 'Smartphone Apple iPhone 15 128GB',
 2999.90, 3, 5, 2),

('Xiaomi Redmi Note 13',
 'Smartphone Xiaomi 256GB',
 899.90, 25, 5, 2),

('Monitor LG UltraGear 27',
 'Monitor gaming 27 pulgadas, 144Hz',
 1199.90, 8, 5, 3),

('Monitor Samsung 24',
 'Monitor Full HD de 24 pulgadas',
 699.90, 2, 5, 3),

('Teclado Logitech K380',
 'Teclado inalámbrico Bluetooth',
 159.90, 20, 5, 4),

('Mouse Logitech G502',
 'Mouse gaming de alta precisión',
 249.90, 6, 5, 4),

('Memoria RAM Kingston 16GB',
 'Memoria RAM DDR4 16GB',
 179.90, 10, 5, 5),

('SSD Kingston 1TB',
 'Unidad SSD NVMe de 1TB',
 329.90, 3, 5, 5),

('Audífonos Sony WH-1000XM5',
 'Audífonos inalámbricos con cancelación de ruido',
 1399.90, 5, 3, 6),

('Parlante JBL Flip 6',
 'Parlante Bluetooth portátil',
 499.90, 9, 5, 6);
