db = db.getSiblingDB('tpo')

db.createCollection('usuario', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['id', 'nombre', 'contrasena', 'mail', 'estado', 'rol', 'fechaRegistro'],
      properties: {
        id: { bsonType: 'int' },
        nombre: { bsonType: 'string' },
        contrasena: { bsonType: 'string' },
        mail: { bsonType: 'string', pattern: '^.+@.+\\..+$' },
        estado: { enum: ['Activo', 'Inactivo'] },
        rol: {
          bsonType: 'object',
          required: ['idRol', 'nombre'],
          properties: {
            idRol: { bsonType: 'int' },
            nombre: { bsonType: 'string' }
          }
        },
        fechaRegistro: { bsonType: 'date' }
      }
    }
  }
});
db.usuario.createIndex({ id: 1 }, { unique: true });
db.usuario.createIndex({ mail: 1 }, { unique: true });

db.createCollection('rol', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['id', 'nombre'],
      properties: {
        id: { bsonType: 'int' },
        nombre: { bsonType: 'string' }
      }
    }
  }
});
db.rol.createIndex({ id: 1 }, { unique: true });
db.rol.createIndex({ nombre: 1 }, { unique: true });

db.createCollection('proceso', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      properties: {
        id: { bsonType: 'int' },
        nombre: { bsonType: 'string' },
        descripcion: { bsonType: 'string' },
        tipo: { bsonType: 'string' },
        costo: { bsonType: ['double', 'decimal'] },
        usuarios: {
            bsonType: 'array',
            items: {
                bsonType: 'object',
                required: ['idUsuario', 'nombre', 'mail'],
                properties: {
                    idUsuario: { bsonType: 'int' },
                    nombre: { bsonType: 'string' },
                    mail: { bsonType: 'string', pattern: '^.+@.+\\..+$' }
                }
            }
        }
      }
    }
  }
});
db.proceso.createIndex({ id: 1 }, { unique: true });

db.createCollection('factura', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['id', 'usuario', 'fechaEmision', 'procesosFacturados', 'total', 'estado'],
      properties: {
          id: {bsonType: 'int'},
          usuario: {
              bsonType: 'object',
              required: ['idUsuario', 'nombre', 'mail'],
              properties: {
                  idUsuario: {bsonType: 'int'},
                  nombre: {bsonType: 'string'},
                  mail: {bsonType: 'string', pattern: '^.+@.+\\..+$'}
              }
          },
          fechaEmision: {bsonType: 'date'},
          procesosFacturados: {
          bsonType: 'object',
          required: ['idProceso', 'nombre', 'costo', 'descripcion'],
          properties:{
              idProceso: {bsonType: 'int'},
              nombre: {bsonType: 'string'},
              costo: {bsonType: ['double', 'decimal']},
              descripcion: {bsonType: 'string'}
          }
        },
        total: { bsonType: ['double', 'decimal'] },
        estado: { enum: ['Pendiente', 'Pagada'] }
      }
    }
  }
});
db.factura.createIndex({ id: 1 }, { unique: true });

db.createCollection('sensor', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['id', 'cod', 'tipo', 'latitud', 'longitud', 'ciudad', 'pais', 'estado', 'fechaIni'],
      properties: {
        id: { bsonType: 'int' },
        cod: { bsonType: 'string' },
        tipo: { enum: ['temperatura', 'humedad', 'presion', 'movimiento', 'otro'] },
        latitud: { bsonType: ['int', 'double', 'decimal'] },
        longitud: { bsonType: ['int', 'double', 'decimal'] },
        ciudad: { bsonType: 'string' },
        pais: { bsonType: 'string' },
        estado: { enum: ['activo', 'inactivo'] },
        fechaIni: { bsonType: 'date' }
      },
    }
  }
});
db.sensor.createIndex({ id: 1 }, { unique: true });
db.sensor.createIndex({ cod: 1 }, { unique: true });

db.createCollection('grupo', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['id', 'nombreGrupo', 'usuarios'],
      properties: {
        id: { bsonType: 'int' },
        nombreGrupo: { bsonType: 'string' },
        usuarios: {
          bsonType: 'array',
          minItems: 1,
          items: {
            bsonType: 'object',
            required: ['id', 'nombre', 'mail', 'estado'],
            properties: {
              id: { bsonType: 'int' },
              nombre: { bsonType: 'string' },
              mail: { bsonType: 'string', pattern: '^.+@.+\\..+$' },
              estado: { enum: ['activo', 'inactivo'] }
            }
          }
        },
        mensajes:{
            bsonType: 'array',
            items: {
                bsonType: 'object',
                required: ['idMensaje', 'contenido', 'fechaEnvio'],
                properties: {
                    idMensaje: {bsonType: 'int'},
                    contenido: {bsonType: 'string'},
                    fechaEnvio: {bsonType: 'date'}
                }
            }
        }
      }
    }
  }
});
db.grupo.createIndex({ id: 1 }, { unique: true });

db.createCollection('mensajes', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['id', 'remitente', 'destinatario', 'contenido', 'fechaEnvio', 'tipo'],
      properties: {
        id: { bsonType: 'int' },
        remitente: {
          bsonType: 'object',
          required: ['id', 'nombre', 'mail'],
          properties: {
            id: { bsonType: 'int' },
            nombre: { bsonType: 'string' },
            mail: { bsonType: 'string', pattern: '^.+@.+\\..+$' }
          }
        },
        destinatario: {
          bsonType: 'object',
          required: ['id', 'nombre', 'mail'],
          properties: {
            id: { bsonType: 'int' },
            nombre: { bsonType: 'string' },
            mail: { bsonType: 'string', pattern: '^.+@.+\\..+$' }
          }
        },
        contenido: { bsonType: 'string' },
        fechaEnvio: { bsonType: 'date' },
        tipo: { enum: ['privado', 'grupal'] }
      }
    }
  }
});
db.mensajes.createIndex({ remitente: 1, fechaEnvio: -1 });

db.createCollection('logs', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['id', 'solicitud', 'fecha', 'resultado', 'estado'],
      properties: {
        id: { bsonType: 'int' },
        solicitud: { bsonType: 'string' },
        fecha: { bsonType: 'date' },
        resultado: { bsonType: 'string' },
        estado: { enum: ['activa', 'resuelta'] }
      }
    }
  }
});
db.logs.createIndex({ id: 1 }, { unique: true });

db.createCollection('solicitudProceso', {
    validator:{
        $jsonSchema: {
        bsonType: 'object',
        required: ['id', 'usuario', 'procesoSolicitado', 'fechaSolicitud', 'estado'],
        properties: {
            id: { bsonType: 'int' },
            usuario: {
                bsonType: 'object',
                required: ['idUsuario', 'nombre', 'mail'],
                properties: {
                    idUsuario: { bsonType: 'int' },
                    nombre: { bsonType: 'string' },
                    mail: { bsonType: 'string', pattern: '^.+@.+\\..+$' }
                }
            },
            procesoSolicitado: {
                bsonType: 'object',
                required: ['idProceso', 'nombre', 'descripcion', 'tipo', 'costo'],
                properties: {
                    idProceso: { bsonType: 'int' },
                    nombre: { bsonType: 'string' },
                    descripcion: { bsonType: 'string' },
                    tipo: { bsonType: 'string' },
                    costo: { bsonType: ['double', 'decimal'] }
                }
            },
            fechaSolicitud: { bsonType: 'date' },
            estado: { enum: ['Pendiente', 'Completado']}

        }
        }
    }
});
db.solicitudProceso.createIndex({ id: 1 }, { unique: true });

db.createCollection('control',{
    validator:{
        $jsonSchema:{
            bsonType: 'object',
            required: ['id', 'sensor', 'fechaControl', 'estado', 'obvservaciones'],
            properties:{
                id: { bsonType: 'int' },
                sensor:{
                    bsonType: 'object',
                    required: ['idSensor', 'cod', 'tipo'],
                    properties:{
                        idSensor: { bsonType: 'int' },
                        cod: { bsonType: 'string' },
                        tipo: { enum: ['temperatura', 'humedad', 'presion', 'movimiento', 'otro'] }
                    }
                },
                fechaControl: { bsonType: 'date' },
                estado: { enum: ['funcionando', 'fallo'] },
                obvservaciones: { bsonType: 'string' }
            }
        }
            }
});
db.control.createIndex({ id: 1 }, { unique: true });