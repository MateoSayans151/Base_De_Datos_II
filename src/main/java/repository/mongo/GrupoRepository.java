package repository.mongo;

import connections.MongoPool;
import entity.Grupo;
import entity.Mensaje;
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
            throw new ErrorConectionMongoException("Error al obtener lastId de grupos: " + e.getMessage());
        }
    }

    public static GrupoRepository getInstance() {
        if (instance == null)
            instance = new GrupoRepository();
        return instance;
    }

    public void createGroup(Grupo grupo) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        try {
            var collection = connection.getCollection(COLLECTION_NAME);

            // Assign an incremental id to the group (similar to UsuarioRepository)
            int lastId = getLastId();
            grupo.setId(lastId + 1);

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
                            .append("estado", u.getEstado().toLowerCase());
                    usuariosDocs.add(du);
                }
            }
            newGrupo.append("usuarios", usuariosDocs);
            newGrupo.append("mensajes", new ArrayList<Document>());

            collection.insertOne(newGrupo);

        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al guardar el grupo en MongoDB " + e.getMessage());
        }
    }

    public Grupo getGroup(int id) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        Grupo grupo = null;
        try {
            var collection = connection.getCollection(COLLECTION_NAME);
            Document filter = new Document("id", id);
            Document result = collection.find(filter).first();
            if (result != null) {
                grupo = mapGroup(result);
            }
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al obtener el grupo en MongoDB");
        }
        return grupo;
    }

    public List<Grupo> getGroupByName(String nombreGrupo) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        var collection = connection.getCollection(COLLECTION_NAME);
        List<Grupo> grupos = new ArrayList<>();
        try {
            var cursor = collection.find(new Document("nombreGrupo", nombreGrupo)).iterator();
            while (cursor.hasNext()) {
                Document result = cursor.next();
                grupos.add(mapGroup(result));
            }
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al obtener los grupos por nombre en MongoDB");
        }
        return grupos;
    }

    public List<Grupo> getGroupByUserId(int usuarioId) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        var collection = connection.getCollection(COLLECTION_NAME);
        List<Grupo> grupos = new ArrayList<>();
        try {
            var cursor = collection.find(new Document("usuarios.id", usuarioId)).iterator();
            while (cursor.hasNext()) {
                Document result = cursor.next();
                grupos.add(mapGroup(result));
            }
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al obtener los grupos por usuario en MongoDB");
        }
        return grupos;
    }
    public void addMessageToGroup(int grupoId, entity.Mensaje mensaje) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        try {
            var collection = connection.getCollection(COLLECTION_NAME);
            // Ensure the message has an id for the group mensajes array. If not, generate one
            int idMensaje = mensaje.getId() != 0 ? mensaje.getId() : (int)(System.currentTimeMillis() % Integer.MAX_VALUE);
        Document mensajeDoc = new Document()
            .append("idMensaje", idMensaje)
            .append("contenido", mensaje.getContenido())
            .append("fechaEnvio", mensaje.getFechaEnvio());

        // include remitente info when available
        if (mensaje.getRemitente() != null) {
        Document remitenteDoc = new Document()
            .append("id", mensaje.getRemitente().getId())
            .append("nombre", mensaje.getRemitente().getNombre())
            .append("mail", mensaje.getRemitente().getMail());
        mensajeDoc.append("remitente", remitenteDoc);
        }

            Document filter = new Document("id", grupoId);
            Document update = new Document("$push", new Document("mensajes", mensajeDoc));
            collection.updateOne(filter, update);
        } catch (Exception e) {
            System.err.println("Error al agregar el mensaje al grupo en MongoDB: " + e.getMessage());
        }
    }

    /**
     * Add a user to the group's usuarios array.
     */
    public void addUserToGroup(int grupoId, Usuario usuario) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        try {
            var collection = connection.getCollection(COLLECTION_NAME);
            Document du = new Document()
                    .append("id", usuario.getId())
                    .append("nombre", usuario.getNombre())
                    .append("mail", usuario.getMail())
                    .append("estado", usuario.getEstado() == null ? "" : usuario.getEstado().toLowerCase());

            Document filter = new Document("id", grupoId);
            Document update = new Document("$push", new Document("usuarios", du));
            collection.updateOne(filter, update);
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al agregar usuario al grupo en MongoDB: " + e.getMessage());
        }
    }
    public List<Mensaje> getMessagesByGroupId(int grupoId) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        List<Mensaje> mensajes = new ArrayList<>();
        try {
            var collection = connection.getCollection(COLLECTION_NAME);
            Document filter = new Document("id", grupoId);
            Document result = collection.find(filter).first();
            if (result != null) {
                System.out.println("Estoy aca");
                @SuppressWarnings("unchecked")
                List<Document> mensajesDocs = (List<Document>) result.get("mensajes");
                if (mensajesDocs != null) {
                    for (Document mdoc : mensajesDocs) {
                        Mensaje m = new Mensaje();
                        m.setId(mdoc.getInteger("idMensaje"));
                        m.setContenido(mdoc.getString("contenido"));
                        m.setFechaEnvio(mdoc.getDate("fechaEnvio").toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
                        // map remitente if embedded
                        Document remitenteDoc = mdoc.get("remitente", Document.class);
                        if (remitenteDoc != null) {
                            Usuario remitente = new Usuario();
                            remitente.setId(remitenteDoc.getInteger("id"));
                            remitente.setNombre(remitenteDoc.getString("nombre"));
                            remitente.setMail(remitenteDoc.getString("mail"));
                            m.setRemitente(remitente);
                        }
                        mensajes.add(m);
                    }
                }
            }
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al obtener los mensajes del grupo en MongoDB " + e.getMessage());
        }
        return mensajes;
    }

    public Grupo mapGroup(Document doc) {
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