package repository.mongo;

import connections.MongoPool;
import entity.Grupo;
import entity.Usuario;
import exceptions.ErrorConectionMongoException;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class GrupoRepository {

    private static GrupoRepository instance;
    private final String COLLECTION_NAME = "grupo";

    private GrupoRepository() {
    }

    public static GrupoRepository getInstance() {
        if (instance == null)
            instance = new GrupoRepository();
        return instance;
    }

    public void guardarGrupo(Grupo grupo) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        try {
            var collection = connection.getCollection(COLLECTION_NAME);
            Document newGrupo = new Document()
                    .append("id", grupo.getId())
                    .append("nombreGrupo", grupo.getNombre());

            List<Document> usuariosDocs = new ArrayList<>();
            if (grupo.getMiembros() != null) {
                for (Usuario u : grupo.getMiembros()) {
                    Document du = new Document()
                            .append("id", u.getId())
                            .append("nombre", u.getNombre())
                            .append("mail", u.getMail())
                            .append("estado", u.getEstado());
                    usuariosDocs.add(du);
                }
            }
            newGrupo.append("usuarios", usuariosDocs);

            collection.insertOne(newGrupo);

        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al guardar el grupo en MongoDB");
        }
    }

    public Grupo obtenerGrupo(int id) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        Grupo grupo = null;
        try {
            var collection = connection.getCollection(COLLECTION_NAME);
            Document filter = new Document("id", id);
            Document result = collection.find(filter).first();
            if (result != null) {
                grupo = mappearGrupo(result);
            }
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al obtener el grupo en MongoDB");
        }
        return grupo;
    }

    public List<Grupo> obtenerGruposPorNombre(String nombreGrupo) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        var collection = connection.getCollection(COLLECTION_NAME);
        List<Grupo> grupos = new ArrayList<>();
        try {
            var cursor = collection.find(new Document("nombreGrupo", nombreGrupo)).iterator();
            while (cursor.hasNext()) {
                Document result = cursor.next();
                grupos.add(mappearGrupo(result));
            }
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al obtener los grupos por nombre en MongoDB");
        }
        return grupos;
    }

    public List<Grupo> obtenerGruposPorUsuarioId(int usuarioId) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        var collection = connection.getCollection(COLLECTION_NAME);
        List<Grupo> grupos = new ArrayList<>();
        try {
            var cursor = collection.find(new Document("usuarios.id", usuarioId)).iterator();
            while (cursor.hasNext()) {
                Document result = cursor.next();
                grupos.add(mappearGrupo(result));
            }
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al obtener los grupos por usuario en MongoDB");
        }
        return grupos;
    }

    public Grupo mappearGrupo(Document doc) {
        Grupo grupo = new Grupo();
        grupo.setId(doc.getInteger("id"));
        grupo.setNombre(doc.getString("nombreGrupo"));

        List<Usuario> usuarios = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<Document> usuariosDocs = (List<Document>) doc.get("usuarios");
        if (usuariosDocs != null) {
            for (Document udoc : usuariosDocs) {
                Usuario u = new Usuario();
                u.setId(udoc.getInteger("id"));
                u.setNombre(udoc.getString("nombre"));
                u.setMail(udoc.getString("mail"));
                u.setEstado(udoc.getString("estado"));
                usuarios.add(u);
            }
        }
        grupo.setMiembros(usuarios);
        return grupo;
    }
}