package repository.mongo;

import com.mongodb.client.MongoCollection;
import connections.MongoPool;
import entity.Mensaje;
import entity.Usuario;
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
            Document doc = new Document()
                    .append("id", mensaje.getId())
                    .append("remitente", remitenteDoc)
                    .append("destinatario", destinatarioDoc)
                    .append("contenido", mensaje.getContenido())
                    .append("fechaEnvio", mensaje.getFechaEnvio())
                    .append("tipo", mensaje.getTipo());


            collection.insertOne(doc);
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al crear el mensaje en MongoDB" + e.getMessage());
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
    public Mensaje mappearMensaje(Document doc) throws ErrorConectionMongoException {
        if (doc == null) return null;
        var remitenteDoc = doc.get("remitente", Document.class);
        Usuario remitente = new Usuario();
        remitente.setNombre(remitenteDoc.getString("nombre"));
        remitente.setMail(remitenteDoc.getString("mail"));
        remitente.setId(remitenteDoc.getInteger("id"));
        var destinatarioDoc = doc.get("destinatario", Document.class);
        Usuario destinatario = new Usuario();
        destinatario.setNombre(destinatarioDoc.getString("nombre"));
        destinatario.setMail(destinatarioDoc.getString("mail"));
        destinatario.setId(destinatarioDoc.getInteger("id"));
        Mensaje mensaje = new Mensaje();
        mensaje.setId(doc.getInteger("id"));
        mensaje.setRemitente(remitente);
        mensaje.setDestinatario(destinatario);
        mensaje.setContenido(doc.getString("contenido"));
        mensaje.setFechaEnvio(doc.getDate("fechaEnvio").toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
        mensaje.setTipo(doc.getString("tipo"));
        //mensaje.setIdGrupo(doc.getInteger("grupoId"));

        return mensaje;
    }
}
