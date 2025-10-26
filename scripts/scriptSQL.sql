
CREATE TABLE Pagos (
  idPago BIGINT PRIMARY KEY AUTO_INCREMENT,
  fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  metodoPago VARCHAR(32) NOT NULL,                       -- 'card','transfer','cash', etc.
  monto DECIMAL(18,2) NOT NULL,
  idFactura INT NOT NULL,
  idUsuario INT NOT NULL,
  factura_total DECIMAL(18,2) NOT NULL,
  usuario_email VARCHAR(255) NOT NULL,
  usuario_nombre VARCHAR(120) NOT NULL,
  procesos_count INT NOT NULL DEFAULT 0,
  procesos_preview VARCHAR(255) NULL,                          -- ej: "Deshumificador, Escoba"
  FOREIGN KEY (idFactura) REFERENCES Factura(idFactura),
  FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario)
);

CREATE TABLE CuentaCorriente (
  idCuenta BIGINT PRIMARY KEY AUTO_INCREMENT,
  idUsuario CHAR(24) NOT NULL UNIQUE,
  saldo DECIMAL(18,2) NOT NULL DEFAULT 0.00
  FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario)
);


CREATE TABLE MovimientosCC (
  idMovimiento BIGINT PRIMARY KEY AUTO_INCREMENT,
  idCuenta BIGINT NOT NULL,
  ts  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  concepto VARCHAR(200) NOT NULL,
  monto  DECIMAL(18,2) NOT NULL,
  idPago BIGINT NULL,
  FOREIGN KEY (idCuenta) REFERENCES CuentaCorriente(idCuenta),
  FOREIGN KEY (idPago) REFERENCES Pagos(idPago)
);

CREATE TABLE Usuario (
idUsuario int PRIMARY KEY AUTO_INCREMENT,
nombre varchar(50) NOT NULL,
contrasena varchar(255) NOT NULL,
mail varchar(100) NOT NULL UNIQUE,
estado varchar(20) NOT NULL DEFAULT 'Activo',
fechaRegistro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Rol (
idRol int PRIMARY KEY AUTO_INCREMENT,
nombre varchar(50) NOT NULL UNIQUE
);

CREATE TABLE Proceso (
idProceso INT PRIMARY KEY AUTO_INCREMENT,
nombre VARCHAR(50) NOT NULL,
descripcion TEXT NOT NULL,
tipo VARCHAR(50) NOT NULL,
costo DECIMAL(18,2) NOT NULL
);

CREATE TABLE Factura (
idFactura INT PRIMARY KEY AUTO_INCREMENT,
idUsuario INT NOT NULL,
fechaEmision TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
procesosFacturados TEXT NOT NULL,
total DECIMAL(18,2) NOT NULL,
estado VARCHAR(20) NOT NULL DEFAULT 'Pendiente',
FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario)
);