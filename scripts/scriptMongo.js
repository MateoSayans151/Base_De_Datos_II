
use('tp');

//Limpia las bases si ya existen
for (const c of ['usuario','sensor','rol','proceso','factura']) {
  if (db.getCollectionNames().includes(c)) db.getCollection(c).drop();
}


db.createCollection('usuario', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['id','nombre','mail','estado','fechaR'],
      properties: {
        id: { bsonType: 'int' },
        nombre: { bsonType: 'string' },
        mail: { bsonType: 'string', pattern: '^.+@.+\\..+$' },
        pass: { bsonType: 'string' },
        estado: { enum: ['activo','inactivo'] },
        fechaR: { bsonType: 'date' }
      },
      additionalProperties: false
    }
  }
});
db.usuario.createIndex({ id: 1 }, { unique: true });
db.usuario.createIndex({ mail: 1 }, { unique: true });

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

db.createCollection('rol', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['idRol','descripcion'],
      properties: {
        idRol: { bsonType: 'int' },
        descripcion: { bsonType: 'string' }
      },
      additionalProperties: false
    }
  }
});
db.rol.createIndex({ idRol: 1 }, { unique: true });

db.createCollection('proceso', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['idProc','nombre','descripcion','tipo','costo'],
      properties: {
        idProc: { bsonType: 'int' },
        nombre: { bsonType: 'string' },
        descripcion: { bsonType: 'string' },
        tipo: { bsonType: 'string' },
        costo: { bsonType: ['int','double','decimal'] }
      },
      additionalProperties: false
    }
  }
});
db.proceso.createIndex({ idProc: 1 }, { unique: true });
db.proceso.createIndex({ nombre: 1 });
// Puede existir un proceso con el mismo nombre pero distinto costo

db.createCollection('factura', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['idFac','usuario','fechaEmi','procesosFac','estado'],
      properties: {
        idFac: { bsonType: 'int' },
        usuario: {
          bsonType: 'object',
          required: ['id','nombre','mail','estado'],
          properties: {
            id: { bsonType: 'int' },
            nombre: { bsonType: 'string' },
            mail: { bsonType: 'string', pattern: '^.+@.+\\..+$' },
            estado: { enum: ['activo','inactivo'] }
          },
          additionalProperties: false
        },
        fechaEmi: { bsonType: 'date' },
        procesosFac: {
          bsonType: 'array',
          minItems: 1,
          items: {
            bsonType: 'object',
            required: ['idProc','nombre','costo'],
            properties: {
              idProc: { bsonType: 'int' },
              nombre: { bsonType: 'string' },
              costo: { bsonType: ['int','double','decimal'] }
            },
            additionalProperties: false
          }
        },
        total: { bsonType: ['int','double','decimal'] },
        estado: { enum: ['pagada','impaga','anulada'] }
      },
      additionalProperties: false
    }
  }
});
db.factura.createIndex({ idFac: 1 }, { unique: true });

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
