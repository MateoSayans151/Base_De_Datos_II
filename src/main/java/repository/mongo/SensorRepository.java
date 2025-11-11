package repository.mongo;
import connections.MongoPool;
import entity.Sensor;
import exceptions.ErrorConectionMongoException;
import org.bson.Document;


import java.sql.Timestamp;
import java.util.List;

public  class SensorRepository {

    private static SensorRepository instance;
    private final String COLLECTION_NAME = "sensor";

    private SensorRepository() {
    }

    public static SensorRepository getInstance() {
        if (instance == null)
            instance = new SensorRepository();
        return instance;
    }

    public int saveSensor(Sensor sensor) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        int lastId = getLastId();
        sensor.setId(lastId + 1);
        try {
            var collection = connection.getCollection(COLLECTION_NAME);
            Document newSensor = new Document()
                    .append("id", sensor.getId())
                    .append("cod", sensor.getCod())
                    .append("tipo", sensor.getTipo())
                    .append("latitud", sensor.getLatitud())
                    .append("longitud", sensor.getLongitud())
                    .append("ciudad", sensor.getCiudad())
                    .append("pais", sensor.getPais())
                    .append("estado", sensor.getEstado())
                    .append("fechaIni", Timestamp.valueOf(sensor.getFechaIni()));


            collection.insertOne(newSensor);
        return sensor.getId();
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al guardar el sensor en MongoDB" + e.getMessage());
        }
    }
    public Sensor getSensor(int idSensor) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        Sensor sensor = null;
        try {
            var collection = connection.getCollection(COLLECTION_NAME);
            Document filter = new Document("id", idSensor);
            Document result = collection.find(filter).first();
            sensor = mapSensor(result);
        }catch (Exception e) {
            throw new ErrorConectionMongoException("Error al obtener el sensor en MongoDB");
        }
        return sensor;
    }
    public void changeStateSensor(int idSensor) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        try {
            var collection = connection.getCollection(COLLECTION_NAME);
            Document filter = new Document("id", idSensor);
            Document sensorDoc = collection.find(filter).first();
            String currentState = sensorDoc.getString("estado");
            String newState = currentState.equals("activo") ? "inactivo" : "activo";
            Document update = new Document("$set", new Document("estado", newState));
            collection.updateOne(filter, update);
        } catch (Exception e) {
            throw new RuntimeException("Error al cambiar el estado del sensor en MongoDB: " + e.getMessage());
        }
    }
    public Sensor getSensorByCode(String code) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        try {
            var collection = connection.getCollection(COLLECTION_NAME);
            Document filter = new Document("cod", code);
            Document result = collection.find(filter).first();
            return mapSensor(result);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener el sensor por codigo en MongoDB: " + e.getMessage());
        }
    }
    public List<Sensor> getSensorsByCity(String ciudad) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        var collection = connection.getCollection(COLLECTION_NAME);
        List<Sensor> sensors = new java.util.ArrayList<>();
        try{
            var resultSet = collection.find(new Document("ciudad", ciudad)).iterator();
            while (resultSet.hasNext()) {
                Document result = resultSet.next();
                Sensor sensor = mapSensor(result);
                sensors.add(sensor);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sensors;
    }
    public List<Sensor> getSensorsByState(String estado) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        var collection = connection.getCollection(COLLECTION_NAME);
        List<Sensor> sensors = new java.util.ArrayList<>();
        try {
            var resultSet = collection.find(new Document("estado", estado)).iterator();
            while (resultSet.hasNext()) {
                Document result = resultSet.next();
                Sensor sensor = mapSensor(result);
                sensors.add(sensor);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return  sensors;
    }
    public List<Sensor> getSensorsByCountry(String pais) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        var collection = connection.getCollection(COLLECTION_NAME);
        List<Sensor> sensors = new java.util.ArrayList<>();
        try {
            var resultSet = collection.find(new Document("pais", pais)).iterator();
            while (resultSet.hasNext()) {
                Document result = resultSet.next();
                Sensor sensor = mapSensor(result);
                sensors.add(sensor);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return  sensors;
    }
    public List<Sensor> getAllSensors() throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        var collection = connection.getCollection(COLLECTION_NAME);
        List<Sensor> sensors = new java.util.ArrayList<>();

        try {
            var resultSet = collection.find().limit(100).iterator();

            while (resultSet.hasNext()) {
                Document result = resultSet.next();
                Sensor sensor = mapSensor(result);
                sensors.add(sensor);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return sensors;
    }

    private int getLastId() throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        var collection = connection.getCollection(COLLECTION_NAME);
        try {
            Document sort = new Document("id", -1);
            Document result = collection.find().sort(sort).first();
            if (result == null) return 0;
            Integer id = result.getInteger("id");
            return id != null ? id : 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public Sensor mapSensor(Document doc){
        Sensor sensor = new Sensor();
        sensor.setId(doc.getInteger("id"));
        sensor.setCod(doc.getString("cod"));
        sensor.setTipo(doc.getString("tipo"));
        sensor.setLatitud(doc.getDouble("latitud"));
        sensor.setLongitud(doc.getDouble("longitud"));
        sensor.setCiudad(doc.getString("ciudad"));
        sensor.setPais(doc.getString("pais"));
        sensor.setEstado(doc.getString("estado"));
        sensor.setFechaIni(doc.getDate("fechaIni").toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());

        return sensor;
    }
}