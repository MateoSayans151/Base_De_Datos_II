#!/bin/bash
# init-redis.sh — Inicializa Redis para manejo de sesiones + demo

set -euo pipefail

# Duraciones (podés cambiarlas o exportarlas antes de correr)
ACCESS_TTL_SEC="${ACCESS_TTL_SEC:-900}"       # 15 min
REFRESH_TTL_SEC="${REFRESH_TTL_SEC:-604800}"  # 7 días

echo "Inicializando Redis..."
redis-cli <<EOF
# =========================
# Opcional: limpiar la base
# FLUSHDB
# =========================

# AOF para durabilidad básica (opcional)
CONFIG SET appendonly yes

# ---------- Config app ----------
SET session:cfg:access_ttl ${ACCESS_TTL_SEC}
SET session:cfg:refresh_ttl ${REFRESH_TTL_SEC}

# ---------- Índices/sets auxiliares ----------
# Todas las sesiones activas de un user: sess:uid:<uid> = SET de <sid>
# Lista de sensors activos (tu ejemplo original)
SADD active_sensors "SENSOR_NYC_01" "SENSOR_LON_01" "SENSOR_TYO_01" "SENSOR_SYD_01" "SENSOR_BUE_01"

# ---------- Contadores demo ----------
SET sensor_count 0
SET measurement_count 0

# ---------- Cola de procesos (LPUSH -> RPOP) ----------
LPUSH process_queue "INIT"

# ---------- DEMO: crear dos sesiones ----------
# Supongamos:
#  - Usuario 123 con sid=SID_A y refresh RID_A
#  - Usuario 456 con sid=SID_B y refresh RID_B
# (en tu app serían UUIDs; acá strings fijas para ver las claves)

# Timestamps Unix (simples; en app usás el reloj real)
# iat = now; exp = now + ACCESS_TTL; rexp = now + REFRESH_TTL
# Usamos la hora del servidor Redis:
# Nota: Redis no expone 'now' como variable, así que simulamos con TTLs usando EXPIRE/EXPIREAT.

# Sesión user 123
HSET session:SID_A uid 123 role user device web ip 1.2.3.4 iat 0
EXPIRE session:SID_A ${ACCESS_TTL_SEC}
SADD sess:uid:123 SID_A
HSET refresh:RID_A uid 123 sid SID_A
EXPIRE refresh:RID_A ${REFRESH_TTL_SEC}

# Sesión user 456
HSET session:SID_B uid 456 role admin device mobile ip 5.6.7.8 iat 0
EXPIRE session:SID_B ${ACCESS_TTL_SEC}
SADD sess:uid:456 SID_B
HSET refresh:RID_B uid 456 sid SID_B
EXPIRE refresh:RID_B ${REFRESH_TTL_SEC}

# ---------- Rate limiting (ejemplo básico por IP) ----------
# Ventana de 60s para 1.2.3.4
INCR rl:ip:1.2.3.4
EXPIRE rl:ip:1.2.3.4 60

# ---------- Blacklist (para invalidar un JWT por jti antes de expirar) ----------
# SETEX bl:<jti> <segundos_hasta_expirar> 1
SETEX bl:JTI_DEMO 300 1

# Ping final
PING
EOF

echo "Redis inicializado correctamente."
echo
echo "Claves útiles (probá con redis-cli):"
echo "  KEYS session:*              # sesiones activas"
echo "  HGETALL session:SID_A       # ver una sesión"
echo "  SMEMBERS sess:uid:123       # sesiones del user 123"
echo "  TTL session:SID_A           # cuánto falta para expirar"
echo "  HGETALL refresh:RID_A       # refresh token"
echo "  GET bl:JTI_DEMO             # blacklist demo"
