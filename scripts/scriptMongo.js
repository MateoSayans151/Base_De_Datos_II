
use('tp');

//Limpia las bases si ya existen
for (const c of ['sensor']) {
  if (db.getCollectionNames().includes(c)) db.getCollection(c).drop();
}


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
db.sensor.createIndex({ id: 1 }, { unique: true });
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
                estado: {enum:['activo','inactivo'],
            },},},
        },},},},});
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
    fechaEnvio: {bsonType:'string'},
    },},
},});
db.mensajes.createIndex({ remitente: 1, fecha: -1 })

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
},
});
db.alertas.createIndex({ idAlerta: 1 }, { unique: true });