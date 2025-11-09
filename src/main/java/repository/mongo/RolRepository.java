package repository.mongo;

import com.mongodb.client.MongoCollection;
import connections.MongoPool;
import entity.Rol;
import exceptions.ErrorConectionMongoException;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class RolRepository {

    private static RolRepository instance;
    private final String COLLECTION_NAME = "rol";

    private RolRepository() {
    }

    public static RolRepository getInstance() {
        if (instance == null)
            instance = new RolRepository();
        return instance;
    }

    public void createRole(Rol rol) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        try {
            MongoCollection<Document> collection = connection.getCollection(COLLECTION_NAME);
            // If id not set (0), generate a simple sequential id based on the current max id in collection
            if (rol.getId() == 0) {
                Document last = collection.find().sort(new Document("id", -1)).first();
                int nextId = 1;
                if (last != null && last.getInteger("id") != null) {
                    nextId = last.getInteger("id") + 1;
                }
                rol.setId(nextId);
            }

            Document newRol = new Document()
                    .append("id", rol.getId())
                    .append("nombre", rol.getNombre());

            collection.insertOne(newRol);

        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al guardar el rol en MongoDB");
        }
    }

    public Rol getRole(int id) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        Rol rol = null;
        try {
            MongoCollection<Document> collection = connection.getCollection(COLLECTION_NAME);
            Document filter = new Document("id", id);
            Document result = collection.find(filter).first();
            System.out.println(result);
            rol = mapRol(result);
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al obtener el rol en MongoDB");
        }
        return rol;
    }

    public Rol getRoleByName(String nombre) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        Rol rol = null;
        try {
            MongoCollection<Document> collection = connection.getCollection(COLLECTION_NAME);
            Document filter = new Document("nombre", nombre);
            Document result = collection.find(filter).first();
            rol = mapRol(result);
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al obtener el rol por nombre en MongoDB");
        }
        return rol;
    }
    public Rol mapRol(Document doc) {
        if (doc == null) return null;
        Rol rol = new Rol();

        rol.setId(doc.getInteger("id"));
        rol.setNombre(doc.getString("nombre"));
        return rol;
    }
}