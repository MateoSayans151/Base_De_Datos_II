
CREATE TABLE Pagos (
  idPago            BIGINT PRIMARY KEY AUTO_INCREMENT,
  fecha         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  metodoPago        VARCHAR(32) NOT NULL,                       -- 'card','transfer','cash', etc.
  monto             DECIMAL(18,2) NOT NULL,
  facturaID     CHAR(24) NOT NULL,
  usuarioID     CHAR(24) NOT NULL,
  factura_total     DECIMAL(18,2) NOT NULL,
  usuario_email     VARCHAR(255) NOT NULL,
  usuario_nombre    VARCHAR(120) NOT NULL,
  procesos_count    INT NOT NULL DEFAULT 0,
  procesos_preview  VARCHAR(255) NULL,                          -- ej: "Deshumificador, Escoba"

  -- Índices útiles
  INDEX ix_pagos_usuario (usuarioID_ext, fecha),
  INDEX ix_pagos_factura (facturaID_ext),
  INDEX ix_pagos_estado (estado, fecha)
);

CREATE TABLE CuentaCorriente (
  idCuenta          BIGINT PRIMARY KEY AUTO_INCREMENT,
  usuarioID     CHAR(24) NOT NULL UNIQUE,
  saldo             DECIMAL(18,2) NOT NULL DEFAULT 0.00,
);

-- 3) Movimientos de cuenta (en vez de TEXT): cada asiento queda auditable
CREATE TABLE MovimientosCC (
  idMovimiento      BIGINT PRIMARY KEY AUTO_INCREMENT,
  idCuenta          BIGINT NOT NULL,
  ts                TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  concepto          VARCHAR(200) NOT NULL,
  monto             DECIMAL(18,2) NOT NULL,
  idPago           BIGINT NULL,
  CONSTRAINT fk_mov_cc FOREIGN KEY (idCuenta) REFERENCES CuentaCorriente(idCuenta),
  INDEX ix_mov_cc (idCuenta, ts),
  INDEX ix_mov_pago (pago_id)
);
