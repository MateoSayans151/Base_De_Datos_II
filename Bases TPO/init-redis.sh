#!/bin/bash
# Redis initialization script
# Crea estructuras de datos iniciales en Redis

echo "Inicializando Redis..."

redis-cli << EOF
# Para limpiar datos anteriores
FLUSHDB

# Crear estructura de sesiones
HSET sessions:default ttl 3600

# Crear cola de procesos
LPUSH process_queue "INIT"

# Crear contadores
SET sensor_count 0
SET measurement_count 0

# Crear índices
SADD active_sensors "SENSOR_NYC_01"
SADD active_sensors "SENSOR_LON_01"
SADD active_sensors "SENSOR_TYO_01"
SADD active_sensors "SENSOR_SYD_01"
SADD active_sensors "SENSOR_BUE_01"

PING
EOF

echo "Redis inicializado correctamente"
