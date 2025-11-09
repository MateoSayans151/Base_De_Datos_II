-- Crear DB y usarla (ya que estás en MySQL 9 podés usar esta collation)
CREATE DATABASE IF NOT EXISTS tpo
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

USE tpo;

-- Por si necesitás repetir el script sin errores de claves foráneas:
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS MovimientosCC;
DROP TABLE IF EXISTS Pagos;
DROP TABLE IF EXISTS CuentaCorriente;
DROP TABLE IF EXISTS Factura;
DROP TABLE IF EXISTS Proceso;
DROP TABLE IF EXISTS Rol;
DROP TABLE IF EXISTS Usuario;

SET FOREIGN_KEY_CHECKS = 1;


CREATE TABLE Pagos (
       id BIGINT PRIMARY KEY AUTO_INCREMENT,
       fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
       metodoPago VARCHAR(32) NOT NULL,        -- 'card','transfer','cash', etc.
       monto DECIMAL(18,2) NOT NULL,
       idFactura INT NOT NULL,
       idUsuario INT NOT NULL,
       factura_total DECIMAL(18,2) NOT NULL,
       usuario_email VARCHAR(255) NOT NULL,
       usuario_nombre VARCHAR(120) NOT NULL,
       procesos_count INT NOT NULL DEFAULT 0,
       procesos_preview VARCHAR(255) NULL     -- ej: "Deshumificador, Escoba"
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE CuentaCorriente (
     id BIGINT PRIMARY KEY AUTO_INCREMENT,
     usuario_id INT NOT NULL UNIQUE,
     saldo DECIMAL(18,2) NOT NULL DEFAULT 0.00,
     FOREIGN KEY (usuario_id) REFERENCES Usuario(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE MovimientosCC (
   id BIGINT PRIMARY KEY AUTO_INCREMENT,
   idCuenta BIGINT NOT NULL,
   ts TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   concepto VARCHAR(200) NOT NULL,
   monto DECIMAL(18,2) NOT NULL,
   idPago BIGINT NULL,
   FOREIGN KEY (idCuenta) REFERENCES CuentaCorriente(id),
   FOREIGN KEY (idPago) REFERENCES Pagos(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
