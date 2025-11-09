package repository.mongo;

import com.mongodb.client.MongoCollection;
import connections.MongoPool;
import entity.Mensaje;
import entity.Usuario;
import entity.Grupo;
import exceptions.ErrorConectionMongoException;
import org.bson.Document;
import service.UsuarioService;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MensajesRepository {

    private static MensajesRepository instance;
    private final String COLLECTION_NAME = "mensajes";

    private MensajesRepository() { }

    public static MensajesRepository getInstance() {
        if (instance == null) instance = new MensajesRepository();
        return instance;
    }

    public void crearMensaje(Mensaje mensaje) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        Document remitenteDoc = buildUsuarioDoc(mensaje.getRemitente());
        Document destinatarioDoc = buildUsuarioDoc(mensaje.getDestinatario());
        try {
            MongoCollection<Document> collection = connection.getCollection(COLLECTION_NAME);
            // assign incremental id similar to UsuarioRepository
            int lastId = getLastId();
            int newId = lastId + 1;
            mensaje.setId(newId);

            // convert fechaEnvio to java.util.Date for Mongo
            java.util.Date fecha = null;
            if (mensaje.getFechaEnvio() != null) {
                fecha = java.util.Date.from(mensaje.getFechaEnvio().atZone(ZoneId.systemDefault()).toInstant());
            }

            Document doc = new Document()
                    .append("id", mensaje.getId())
                    .append("remitente", remitenteDoc)
                    .append("contenido", mensaje.getContenido())
                    .append("fechaEnvio", fecha);

            // If destinatario is present -> personal message. Ensure 'tipo' is set to match DB schema (e.g. 'privado').
            if (mensaje.getDestinatario() != null) {
                doc.append("destinatario", destinatarioDoc);
                // Mongo collection validation requires a 'tipo' property. Use provided tipo or default to 'privado'.
                if (mensaje.getTipo() != null) {
                    doc.append("tipo", mensaje.getTipo());
                } else {
                    doc.append("tipo", "privado");
                }
            } else if (mensaje.getGrupo() != null) {
                // For group messages, include the group and include 'tipo' if set (e.g., 'grupal')
                doc.append("grupo", buildGrupoDoc(mensaje.getGrupo()));
                if (mensaje.getTipo() != null) {
                    doc.append("tipo", mensaje.getTipo());
                }
            }

            // Logging for debugging
            System.out.println("[MensajesRepository] Inserting message document: " + doc.toJson());
            collection.insertOne(doc);
            System.out.println("[MensajesRepository] Insert successful, id=" + mensaje.getId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorConectionMongoException("Error al crear el mensaje en MongoDB: " + e.getMessage());
        }
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
            throw new ErrorConectionMongoException(e.getMessage());
        }
    }

    public List<Mensaje> obtenerMensajesPorRemitente(int remitenteId) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        List<Mensaje> mensajes = new ArrayList<>();
        try {
            var collection = connection.getCollection(COLLECTION_NAME);
            var cursor = collection.find(new Document("remitente.id", remitenteId)).iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Mensaje m = mappearMensaje(doc);
                if (m != null) mensajes.add(m);
            }
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al obtener mensajes por remitente en MongoDB");
        }
        return mensajes;
    }

    public List<Mensaje> obtenerMensajesPorDestinatario(int destinatarioId) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        List<Mensaje> mensajes = new ArrayList<>();
        try {
            var collection = connection.getCollection(COLLECTION_NAME);
            var cursor = collection.find(new Document("destinatario.id", destinatarioId)).iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Mensaje m = mappearMensaje(doc);
                if (m != null) mensajes.add(m);
            }
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al obtener mensajes por destinatario en MongoDB" + e.getMessage());
        }
        return mensajes;
    }
    private Document buildUsuarioDoc(Usuario u) {
        if (u == null) return null;
        Document d = new Document();
        d.append("id", u.getId());
        d.append("nombre", u.getNombre());
        d.append("mail", u.getMail());
        // add more fields if needed, but avoid embedding entire complex POJOs
        return d;
    }

    private Document buildGrupoDoc(Grupo g) {
        if (g == null) return null;
        Document d = new Document();
        d.append("id", g.getId());
        d.append("nombre", g.getNombre());
        return d;
    }

    public List<Mensaje> obtenerMensajesPorGrupo(Grupo grupo) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        List<Mensaje> mensajes = new ArrayList<>();
        try {
            var collection = connection.getCollection(COLLECTION_NAME);
            var cursor = collection.find(new Document("grupo.id", grupo.getId())).iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Mensaje m = mappearMensaje(doc);
                if (m != null) mensajes.add(m);
            }
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al obtener mensajes por grupo en MongoDB" + e.getMessage());
        }
        return mensajes;
    }
    public Mensaje mappearMensaje(Document doc) throws ErrorConectionMongoException {
        if (doc == null) return null;
        
        Mensaje mensaje = new Mensaje();
        mensaje.setId(doc.getInteger("id"));
    mensaje.setContenido(doc.getString("contenido"));
    // tipo may be absent for personal messages; keep it if present
    mensaje.setTipo(doc.getString("tipo"));
        
        // Mapear remitente
        Document remitenteDoc = doc.get("remitente", Document.class);
        if (remitenteDoc != null) {
            Usuario remitente = new Usuario();
            remitente.setId(remitenteDoc.getInteger("id"));
            remitente.setNombre(remitenteDoc.getString("nombre"));
            remitente.setMail(remitenteDoc.getString("mail"));
            mensaje.setRemitente(remitente);
        }
        
        // Convertir fecha
        Date fechaDoc = doc.getDate("fechaEnvio");
        if (fechaDoc != null) {
            mensaje.setFechaEnvio(fechaDoc.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime());
        }

        // Prefer structural detection: if destinatario exists it's a personal message.
        Document destinatarioDoc = doc.get("destinatario", Document.class);
        if (destinatarioDoc != null) {
            Usuario destinatario = new Usuario();
            destinatario.setId(destinatarioDoc.getInteger("id"));
            destinatario.setNombre(destinatarioDoc.getString("nombre"));
            destinatario.setMail(destinatarioDoc.getString("mail"));
            mensaje.setDestinatario(destinatario);
        } else {
            Document grupoDoc = doc.get("grupo", Document.class);
            if (grupoDoc != null) {
                Grupo grupo = new Grupo();
                grupo.setId(grupoDoc.getInteger("id"));
                grupo.setNombre(grupoDoc.getString("nombre"));
                mensaje.setGrupo(grupo);
            } else {
                // Fallback: if neither field present, try using tipo enum (legacy)
                String tipo = mensaje.getTipo() == null ? "" : mensaje.getTipo();
                if ("PERSONAL".equalsIgnoreCase(tipo) || "privado".equalsIgnoreCase(tipo)) {
                    // nothing more to map
                } else if ("GRUPO".equalsIgnoreCase(tipo) || "grupal".equalsIgnoreCase(tipo)) {
                    // no group doc present, nothing to map
                }
            }
        }
        
    return mensaje;
    }
}
