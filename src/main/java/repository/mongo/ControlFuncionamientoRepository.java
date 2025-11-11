package repository.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import connections.MongoPool;
import entity.ControlFuncionamiento;
import entity.Sensor;
import exceptions.ErrorConectionMongoException;
import org.bson.Document;

import java.time.ZoneId;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ControlFuncionamientoRepository {

    private static ControlFuncionamientoRepository instance;
    private final String COLLECTION_NAME = "control";

    private ControlFuncionamientoRepository() {}

    public static ControlFuncionamientoRepository getInstance() {
        if (instance == null) {
            instance = new ControlFuncionamientoRepository();
        }
        return instance;
    }

    public void saveControl(ControlFuncionamiento control) throws ErrorConectionMongoException {
        try {
            var connection = MongoPool.getInstance().getConnection();
            MongoCollection<Document> collection = connection.getCollection(COLLECTION_NAME);

            Document sensorDoc = new Document()
                    .append("idSensor", control.getSensor().getId())
                    .append("cod", control.getSensor().getCod())
                    .append("tipo", control.getSensor().getTipo());

            Date fecha = null;
            if (control.getFechaControl() != null) {
                fecha = Date.from(control.getFechaControl().atStartOfDay(ZoneId.systemDefault()).toInstant());
            }

            Document doc = new Document()
                    .append("id", control.getId())
                    .append("sensor", sensorDoc)
                    .append("fechaControl", fecha)
                    .append("estado", control.getEstado())
                    .append("obvservaciones", control.getObservaciones());

            collection.insertOne(doc);
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error saving control in MongoDB: " + e.getMessage());
        }
    }

    public void updateControl(ControlFuncionamiento control) throws ErrorConectionMongoException {
        try {
            var connection = MongoPool.getInstance().getConnection();
            MongoCollection<Document> collection = connection.getCollection(COLLECTION_NAME);

            Document sensorDoc = new Document()
                    .append("idSensor", control.getSensor().getId())
                    .append("cod", control.getSensor().getCod())
                    .append("tipo", control.getSensor().getTipo());

            Date fecha = null;
            if (control.getFechaControl() != null) {
                fecha = Date.from(control.getFechaControl().atStartOfDay(ZoneId.systemDefault()).toInstant());
            }

            Document updatedDoc = new Document()
                    .append("sensor", sensorDoc)
                    .append("fechaControl", fecha)
                    .append("estado", control.getEstado())
                    .append("obvservaciones", control.getObservaciones());

            Document filter = new Document("id", control.getId());
            Document updateOperation = new Document("$set", updatedDoc);

            collection.updateOne(filter, updateOperation);
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error updating control in MongoDB: " + e.getMessage());
        }
    }

    public void deleteAllControls() throws ErrorConectionMongoException {
        try {
            var connection = MongoPool.getInstance().getConnection();
            MongoCollection<Document> collection = connection.getCollection(COLLECTION_NAME);
            collection.deleteMany(new Document());
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error deleting all controls from MongoDB: " + e.getMessage());
        }
    }
    public List<ControlFuncionamiento> getAllControls() throws ErrorConectionMongoException {
        try {
            var connection = MongoPool.getInstance().getConnection();
            MongoCollection<Document> collection = connection.getCollection(COLLECTION_NAME);
            List<ControlFuncionamiento> controls = new ArrayList<>();

            MongoCursor<Document> cursor = collection.find().iterator();
            try {
                while (cursor.hasNext()) {
                    controls.add(mapControl(cursor.next()));
                }
            } finally {
                cursor.close();
            }
            return controls;
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error retrieving all controls from MongoDB: " + e.getMessage());
        }
    }

    private ControlFuncionamiento mapControl(Document doc) throws ErrorConectionMongoException {
        if (doc == null) return null;
        ControlFuncionamiento control = new ControlFuncionamiento();

        if (doc.containsKey("id")) {
            control.setId(doc.getInteger("id"));
        }

        Document sensorDoc = doc.get("sensor", Document.class);
        Sensor sensor = null;
        if (sensorDoc != null) {
            // Prefer fetching full sensor from SensorRepository by idSensor if available
            Integer idSensor = sensorDoc.getInteger("idSensor");
            if (idSensor != null) {
                sensor = repository.mongo.SensorRepository.getInstance().getSensor(idSensor);
            }
            // If fetching returned null, map minimal sensor from embedded doc
            if (sensor == null) {
                sensor = new Sensor();
                Integer mappedId = sensorDoc.getInteger("idSensor");
                if (mappedId != null) sensor.setId(mappedId);
                sensor.setCod(sensorDoc.getString("cod"));
                sensor.setTipo(sensorDoc.getString("tipo"));
            }
            control.setSensor(sensor);
        }

        Date fecha = doc.getDate("fechaControl");
        if (fecha != null) {
            LocalDate localDate = fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            control.setFechaControl(localDate);
        }

        control.setEstado(doc.getString("estado"));
        control.setObservaciones(doc.getString("obvservaciones"));

        return control;
    }
}