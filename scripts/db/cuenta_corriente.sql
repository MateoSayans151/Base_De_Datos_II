-- Crear las tablas para el manejo de cuenta corriente
CREATE TABLE IF NOT EXISTS CuentaCorriente (
    id INT PRIMARY KEY AUTO_INCREMENT,
    idUsuario INT NOT NULL,
    saldo DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ultima_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_usuario (idUsuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabla para registrar todos los movimientos de cuenta corriente
CREATE TABLE IF NOT EXISTS MovimientosCC (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cuenta_corriente_id INT NOT NULL,
    tipo_movimiento ENUM('DEPOSITO', 'RETIRO', 'PAGO_FACTURA') NOT NULL,
    monto DECIMAL(15,2) NOT NULL,
    saldo_resultante DECIMAL(15,2) NOT NULL,
    referencia VARCHAR(100),  -- ID de factura u otra referencia
    fecha_movimiento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cuenta_corriente_id) REFERENCES CuentaCorriente(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabla para registrar los pagos de facturas
CREATE TABLE IF NOT EXISTS Pagos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    factura_id INT NOT NULL,
    cuenta_corriente_id INT NOT NULL,
    monto DECIMAL(15,2) NOT NULL,
    fecha_pago TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    metodo_pago VARCHAR(50) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'COMPLETADO',
    movimiento_id INT,
    FOREIGN KEY (cuenta_corriente_id) REFERENCES CuentaCorriente(id),
    FOREIGN KEY (movimiento_id) REFERENCES MovimientosCC(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;