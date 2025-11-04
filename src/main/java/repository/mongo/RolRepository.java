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

    public void crearRol(Rol rol) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        try {
            MongoCollection<Document> collection = connection.getCollection(COLLECTION_NAME);
            Document newRol = new Document()
                    .append("id", rol.getId())
                    .append("nombre", rol.getNombre());

            collection.insertOne(newRol);

        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al guardar el rol en MongoDB");
        }
    }

    public Rol obtenerRol(int id) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        Rol rol = null;
        try {
            MongoCollection<Document> collection = connection.getCollection(COLLECTION_NAME);
            Document filter = new Document("id", id);
            Document result = collection.find(filter).first();
            rol = mappearRol(result);
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al obtener el rol en MongoDB");
        }
        return rol;
    }


    public Rol mappearRol(Document doc) {
        if (doc == null) return null;
        Rol rol = new Rol();

        rol.setId(doc.getInteger("id"));
        rol.setNombre(doc.getString("nombre"));

        return rol;
    }
}