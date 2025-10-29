
db = db.getSiblingDB('tpo')

db.createCollection('usuario',{
    validator:{
        $jsonSchema:{
            bsonType:'object',
            required:['ido','nombre','contrasena','mail','estado','nombreRol','fechaRegistro'],
            properties:{
                id: {bsonType:'int'},
                nombre: {bsonType:'string'},
                contrasena: {bsonType:'string'},
                mail: {bsonType:'string', pattern: '^.+@.+\\..+$' },
                estado: {enum:['Activo','Inactivo']},
                rol: {bsonType:'object',
                required:['idRol','nombre'],
                    properties:{
                        idRol: {bsonType:'int'},
                        nombre: {bsonType:'string'},
                    }},
                fechaRegistro: {bsonType:'date'},
            },
        },
    }});
db.usuario.createIndex({ id: 1 }, { unique: true });
db.usuario.createIndex({ mail: 1 }, { unique: true });

db.createCollection('rol',{
    validator:{
        $jsonSchema:{
            bsonType:'object',
            required:['id','nombre'],
            properties:{
                id: {bsonType:'int'},
                nombre: {bsonType:'string'},
            },
        },
    }});
db.rol.createIndex({ id: 1 }, { unique: true });
db.rol.createIndex({ nombre: 1 }, { unique: true });

db.createCollection('proceso',{
    validator:{
        $jsonSchema:{
            bsonType:'object',
            properties:{
                id: {bsonType:'int'},
                nombre: {bsonType:'string'},
                descripcion: {bsonType:'string'},
                tipo: {bsonType:'string'},
                costo: {bsonType:['double','decimal']},
            },
        },
    }});
db.proceso.createIndex({ id: 1 }, { unique: true});

db.createCollection('factura',{
    validator:{
        $jsonSchema:{
            bsonType:'object',
            required:['id','usuario','fechaEmision','procesosFacturados','total','estado'],
            properties:{
                id: {bsonType:'int'},
                usuario: {bsonType:'object',
                required:['idUsuario','nombre','mail'],
                properties:{
                    id: {bsonType:'int'},
                    nombre: {bsonType:'string'},
                    mail: {bsonType:'string', pattern: '^.+@.+\\..+$' },
                }},
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
      required: ['id','cod','tipo','latitud','longitud','ciudad','pais','estado','fechaIni'],
      properties: {
        id: { bsonType: 'int' },
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
db.sensor.createIndex({ id: 1 }, { unique: true });
db.sensor.createIndex({ cod: 1 }, { unique: true });


db.createCollection('grupo',{
validator:{
    $jsonSchema:{
        bsonType:'object',
        required:['id','nombreGrupo','usuarios'],
        properties:{
        id: {bsonType: 'int'},
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
    required:['id','remitente','destinatario','contenido','fechaEnvio','tipo','grupo'],
    properties:{
    id: {bsonType: 'int'},
    remitente: {bsonType:'int'},
    destinatario:{bsonType:'int'},
    contenido: {bsonType:'string'},
    fechaEnvio: {bsonType:'date'},
    tipo: {enum:['privado','grupal']},
    idGrupo: {bsonType: 'int'},
    }
    },
}});
db.mensajes.createIndex({ remitente: 1, fechaEnvio: -1 })


db.createCollection('logs',{
    validator:{
        $jsonSchema:{
            bsonType:'object',
            required:['id','solicitud','fecha','resultado','estado'],
            properties:{
                id: {bsonType:'int'},
                solicitud: {bsonType:'string'},
                fecha: {bsonType:'date'},
                resultado: {bsonType:'string'},
                estado:{enum:['activa','resuelta']},
            },
        },
    }
});
db.logs.createIndex({ id:1 },{ unique:true });