
db = db.getSiblingDB('tpo')

db.createCollection('usuario',{
    validator:{
        $jsonSchema:{
            bsonType:'object',
            required:['idUsuario','nombre','contrasena','mail','estado','nombreRol','fechaRegistro'],
            properties:{
                idUsuario: {bsonType:'int'},
                nombre: {bsonType:'string'},
                contrasena: {bsonType:'string'},
                mail: {bsonType:'string', pattern: '^.+@.+\\..+$' },
                estado: {enum:['Activo','Inactivo']},
                nombreRol: {bsonType:'string'},
                fechaRegistro: {bsonType:'date'},
            },
        },
    }});
db.usuario.createIndex({ idUsuario: 1 }, { unique: true });
db.usuario.createIndex({ mail: 1 }, { unique: true });

db.createCollection('rol',{
    validator:{
        $jsonSchema:{
            bsonType:'object',
            required:['idRol','nombre'],
            properties:{
                idRol: {bsonType:'int'},
                nombre: {bsonType:'string'},
            },
        },
    }});
db.rol.createIndex({ idRol: 1 }, { unique: true });
db.rol.createIndex({ nombre: 1 }, { unique: true });

db.createCollection('proceso',{
    validator:{
        $jsonSchema:{
            bsonType:'object',
            properties:{
                idProceso: {bsonType:'int'},
                nombre: {bsonType:'string'},
                descripcion: {bsonType:'string'},
                tipo: {bsonType:'string'},
                costo: {bsonType:['double','decimal']},
            },
        },
    }});
db.proceso.createIndex({ idProceso: 1 }, { unique: true});

db.createCollection('factura',{
    validator:{
        $jsonSchema:{
            bsonType:'object',
            required:['idFactura','idUsuario','fechaEmision','procesosFacturados','total','estado'],
            properties:{
                idFactura: {bsonType:'int'},
                idUsuario: {bsonType:'int'},
                fechaEmision: {bsonType:'date'},
                procesosFacturados: {bsonType:'string'},
                total: {bsonType:['double','decimal']},
                estado: {enum:['Pendiente','Pagada']},
            },
        }
    }
});
db.factura.createIndex({ idFactura: 1 }, { unique: true });

db.createCollection('sensor', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['idSensor','cod','tipo','latitud','longitud','ciudad','pais','estado','fechaIni'],
      properties: {
        idSensor: { bsonType: 'int' },
        cod: { bsonType: 'string' },
        tipo: { enum: ['temperatura','humedad','presion','movimiento','otro'] },
        latitud: { bsonType: ['int','double','decimal'] },
        longitud: { bsonType: ['int','double','decimal'] },
        ciudad: { bsonType: 'string' },
        pais: { bsonType: 'string' },
        estado: { enum: ['activo','inactivo'] },
        fechaIni: { bsonType: 'date' }
      },
      additionalProperties: false
    }
  }
});
db.sensor.createIndex({ idSensor: 1 }, { unique: true });
db.sensor.createIndex({ cod: 1 }, { unique: true });


db.createCollection('grupo',{
validator:{
    $jsonSchema:{
        bsonType:'object',
        required:['idGrupo','nombreGrupo','usuarios'],
        properties:{
        idGrupo: {bsonType: 'int'},
        nombreGrupo: {bsonType:'string'},
        usuarios:{
            bsonType:'array',
            minItems: 1,
            items:{
            bsonType:'object',
            required:['id','nombre','mail','estado'],
            properties:{
                id: {bsonType:'int'},
                nombre: {bsonType:'string'},
                mail: {bsonType:'string', pattern: '^.+@.+\\..+$' },
                estado: {enum:['activo','inactivo']},
            },},},
        },},}});
db.grupo.createIndex({ idGrupo: 1 }, { unique: true });

db.createCollection('mensajes',{
validator:{
    $jsonSchema:{
    bsonType:'object',
    required:['idMensaje','remitente','destinatario','contenido','fechaEnvio'],
    properties:{
    idMensaje: {bsonType: 'int'},
    remitente: {bsonType:'int'},
    destinatario:{bsonType:'int'},
    contenido: {bsonType:'string'},
    fechaEnvio: {bsonType:'date'},
    },},
}});
db.mensajes.createIndex({ remitente: 1, fechaEnvio: -1 })

db.createCollection('alertas',{
    validator:{
        $jsonSchema:{
            bsonType:'object',
            required:['idAlerta','tipo','idSensor','fechayHora','descripcion','estado'],
            properties:{
                idAlerta: {bsonType:'int'},
                tipo: {enum:['temperatura','humedad','presion','movimiento','otro']},
                idSensor: {bsonType:'int'},
                fechayHora: {bsonType:'date'},
                descripcion: {bsonType:'string'},
                estado: {enum:['activa','resuelta']},
            },
        },
    }});

db.alertas.createIndex({ idAlerta: 1 }, { unique: true });

db.createCollection('logs',{
    validator:{
        $jsonSchema:{
            bsonType:'object',
            required:['idEjec','solicitud','fecha','resultado','estado'],
            properties:{
                idEjec: {bsonType:'int'},
                solicitud: {bsonType:'string'},
                fecha: {bsonType:'date'},
                resultado: {bsonType:'string'},
                estado:{enum:['activa','resuelta']},
            },
        },
    }
});
db.logs.createIndex({ idEjec:1 },{ unique:true });