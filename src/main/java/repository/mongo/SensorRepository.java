package repository.mongo;
import com.mongodb.client.MongoDatabase;
import connections.MongoPool;
import entity.Sensor;
import exceptions.ErrorConectionMongoException;
import org.bson.Document;


import java.sql.Connection;
import java.sql.SQLException;

public  class SensorRepository {

    private static SensorRepository instance;

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
                    .append("id", sensor.getIdSensor())
                    .append("cod", sensor.getCod())
                    .append("tipo", sensor.getTipo())
                    .append("latitud", sensor.getLatitud())
                    .append("longitud", sensor.getLongitud())
                    .append("ciudad", sensor.getCiudad())
                    .append("pais", sensor.getPais())
                    .append("estado", sensor.getEstado())
                    .append("ubicacion", sensor.getUbicacion())
                    .append("fechaIni", sensor.getFechaIni());

        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al guardar el sensor en MongoDB");
        }
    }

}