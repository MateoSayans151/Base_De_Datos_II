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

-- 1) Tablas base primero

CREATE TABLE Usuario (
         idUsuario INT PRIMARY KEY AUTO_INCREMENT,
         nombre VARCHAR(50) NOT NULL,
         contrasena VARCHAR(255) NOT NULL,
         mail VARCHAR(100) NOT NULL UNIQUE,
         estado VARCHAR(20) NOT NULL DEFAULT 'Activo',
         fechaRegistro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE Rol (
         idRol INT PRIMARY KEY AUTO_INCREMENT,
         nombre VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE Proceso (
         idProceso INT PRIMARY KEY AUTO_INCREMENT,
         nombre VARCHAR(50) NOT NULL,
         descripcion TEXT NOT NULL,
         tipo VARCHAR(50) NOT NULL,
         costo DECIMAL(18,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 2) Factura depende de Usuario
CREATE TABLE Factura (
         idFactura INT PRIMARY KEY AUTO_INCREMENT,
         idUsuario INT NOT NULL,
         fechaEmision TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
         procesosFacturados TEXT NOT NULL,
         total DECIMAL(18,2) NOT NULL,
         estado VARCHAR(20) NOT NULL DEFAULT 'Pendiente',
         FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 3) Pagos depende de Factura y Usuario
CREATE TABLE Pagos (
       idPago BIGINT PRIMARY KEY AUTO_INCREMENT,
       fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
       metodoPago VARCHAR(32) NOT NULL,        -- 'card','transfer','cash', etc.
       monto DECIMAL(18,2) NOT NULL,
       idFactura INT NOT NULL,
       idUsuario INT NOT NULL,
       factura_total DECIMAL(18,2) NOT NULL,
       usuario_email VARCHAR(255) NOT NULL,
       usuario_nombre VARCHAR(120) NOT NULL,
       procesos_count INT NOT NULL DEFAULT 0,
       procesos_preview VARCHAR(255) NULL,     -- ej: "Deshumificador, Escoba"
       FOREIGN KEY (idFactura) REFERENCES Factura(idFactura),
       FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE CuentaCorriente (
     idCuenta BIGINT PRIMARY KEY AUTO_INCREMENT,
     idUsuario INT NOT NULL UNIQUE,
     saldo DECIMAL(18,2) NOT NULL DEFAULT 0.00,
     FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE MovimientosCC (
   idMovimiento BIGINT PRIMARY KEY AUTO_INCREMENT,
   idCuenta BIGINT NOT NULL,
   ts TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   concepto VARCHAR(200) NOT NULL,
   monto DECIMAL(18,2) NOT NULL,
   idPago BIGINT NULL,
   FOREIGN KEY (idCuenta) REFERENCES CuentaCorriente(idCuenta),
   FOREIGN KEY (idPago) REFERENCES Pagos(idPago)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
