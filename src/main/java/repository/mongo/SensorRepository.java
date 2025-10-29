package repository.mongo;
import com.mongodb.client.MongoDatabase;
import connections.MongoPool;
import entity.Sensor;
import entity.Usuario;
import exceptions.ErrorConectionMongoException;
import org.bson.Document;


import java.sql.Connection;
import java.sql.SQLException;
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

    public void guardarSensor(Sensor sensor) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        try {
            var collection = connection.getCollection("sensores");
            Document newSensor = new Document()
                    .append("id", sensor.getId())
                    .append("cod", sensor.getCod())
                    .append("tipo", sensor.getTipo())
                    .append("latitud", sensor.getLatitud())
                    .append("longitud", sensor.getLongitud())
                    .append("ciudad", sensor.getCiudad())
                    .append("pais", sensor.getPais())
                    .append("estado", sensor.getEstado())
                    .append("fechaIni", sensor.getFechaIni());


            collection.insertOne(newSensor);

        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al guardar el sensor en MongoDB");
        }
    }
    public Sensor obtenerSensor(String id) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        Sensor sensor = null;
        try {
            var collection = connection.getCollection("sensores");
            Document filter = new Document("id", id);
            Document result = collection.find(filter).first();
            sensor = mappearSensor(result);
        }catch (Exception e) {
            throw new ErrorConectionMongoException("Error al obtener el sensor en MongoDB");
        }
        return sensor;
    }
    public List<Sensor> obtenerSesoresPorCiudad(String ciudad) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        var collection = connection.getCollection(COLLECTION_NAME);
        List<Sensor> sensors = new java.util.ArrayList<>();
        try{
            var resultSet = collection.find(new Document("ciudad", ciudad)).iterator();
            while (resultSet.hasNext()) {
                Document result = resultSet.next();
                Sensor sensor = mappearSensor(result);
                sensors.add(sensor);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sensors;
    }
    public List<Sensor> obtenerSensoresPorEstado(String estado) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        var collection = connection.getCollection(COLLECTION_NAME);
        List<Sensor> sensors = new java.util.ArrayList<>();
        try {
            var resultSet = collection.find(new Document("estado", estado)).iterator();
            while (resultSet.hasNext()) {
                Document result = resultSet.next();
                Sensor sensor = mappearSensor(result);
                sensors.add(sensor);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return  sensors;
    }
    public List<Sensor> obtenerSensoresPorPais(String pais) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        var collection = connection.getCollection(COLLECTION_NAME);
        List<Sensor> sensors = new java.util.ArrayList<>();
        try {
            var resultSet = collection.find(new Document("pais", pais)).iterator();
            while (resultSet.hasNext()) {
                Document result = resultSet.next();
                Sensor sensor = mappearSensor(result);
                sensors.add(sensor);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return  sensors;
    }

    public Sensor mappearSensor(Document doc){
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