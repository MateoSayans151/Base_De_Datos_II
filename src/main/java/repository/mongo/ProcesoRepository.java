package repository.mongo;

import com.mongodb.client.MongoCollection;
import connections.MongoPool;
import entity.Proceso;
import exceptions.ErrorConectionMongoException;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class ProcesoRepository {

    private static ProcesoRepository instance;
    private final String COLLECTION_NAME = "proceso";

    private ProcesoRepository() {
    }

    public static ProcesoRepository getInstance() {
        if (instance == null)
            instance = new ProcesoRepository();
        return instance;
    }

    public List<Proceso> obtenerTodosLosProcesos() throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        List<Proceso> procesos = new ArrayList<>();
        try {
            MongoCollection<Document> collection = connection.getCollection(COLLECTION_NAME);
            var cursor = collection.find();
            for (Document doc : cursor) {
                Proceso proceso = mappearProceso(doc);
                if (proceso != null) {
                    procesos.add(proceso);
                }
            }
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al obtener todos los procesos de MongoDB: " + e.getMessage());
        }
        return procesos;
    }

    public void crearProceso(Proceso proceso) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        try {
            MongoCollection<Document> collection = connection.getCollection(COLLECTION_NAME);
            Document newDoc = new Document()
                    .append("id", proceso.getId())
                    .append("nombre", proceso.getNombre())
                    .append("descripcion", proceso.getDescripcion())
                    .append("tipo", proceso.getTipo())
                    .append("costo", proceso.getCosto());

            collection.insertOne(newDoc);

        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al guardar el proceso en MongoDB");
        }
    }

    public Proceso obtenerProceso(int id) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        Proceso proceso = null;
        try {
            MongoCollection<Document> collection = connection.getCollection(COLLECTION_NAME);
            Document filter = new Document("id", id);
            Document result = collection.find(filter).first();
            proceso = mappearProceso(result);
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al obtener el proceso en MongoDB");
        }
        return proceso;
    }

    public List<Proceso> obtenerProcesosPorTipo(String tipo) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        var collection = connection.getCollection(COLLECTION_NAME);
        List<Proceso> procesos = new ArrayList<>();
        try {
            var resultSet = collection.find(new Document("tipo", tipo)).iterator();
            while (resultSet.hasNext()) {
                Document result = resultSet.next();
                Proceso proceso = mappearProceso(result);
                procesos.add(proceso);
            }
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al obtener procesos por tipo en MongoDB");
        }
        return procesos;
    }
    public List<Proceso> getAllProcesses() throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        var collection = connection.getCollection(COLLECTION_NAME);
        List<Proceso> procesos = new ArrayList<>();
        try {
            var resultSet = collection.find().iterator();
            while (resultSet.hasNext()) {
                Document result = resultSet.next();
                Proceso proceso = mappearProceso(result);
                procesos.add(proceso);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return procesos;
    }

    public void actualizarProceso(Proceso proceso) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        try {
            MongoCollection<Document> collection = connection.getCollection(COLLECTION_NAME);
            Document filter = new Document("id", proceso.getId());
            Document update = new Document("$set", new Document()
                    .append("nombre", proceso.getNombre())
                    .append("descripcion", proceso.getDescripcion())
                    .append("tipo", proceso.getTipo())
                    .append("costo", proceso.getCosto()));

            collection.updateOne(filter, update);
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al actualizar el proceso en MongoDB: " + e.getMessage());
        }
    }

    public Proceso mappearProceso(Document doc) {
        if (doc == null) return null;
        Proceso proceso = new Proceso();
        proceso.setId(doc.getInteger("id"));
        proceso.setNombre(doc.getString("nombre"));
        proceso.setDescripcion(doc.getString("descripcion"));
        proceso.setTipo(doc.getString("tipo"));
        proceso.setCosto(doc.getDouble("costo"));

        return proceso;
    }
}