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
        try {
            MongoCollection<Document> collection = connection.getCollection(COLLECTION_NAME);
            Document doc = new Document()
                    .append("id", mensaje.getId())
                    .append("remitente", mensaje.getRemitente())
                    .append("destinatario", mensaje.getDestinatario())
                    .append("contenido", mensaje.getContenido())
                    .append("fechaEnvio", mensaje.getFechaEnvio())
                    .append("tipo", mensaje.getTipo())
                    .append("grupoId", mensaje.getIdGrupo());

            collection.insertOne(doc);
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al crear el mensaje en MongoDB");
        }
    }

    public List<Mensaje> obtenerMensajesPorRemitente(int remitenteId) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        List<Mensaje> mensajes = new ArrayList<>();
        try {
            var collection = connection.getCollection(COLLECTION_NAME);
            var cursor = collection.find(new Document("remitente", remitenteId)).iterator();
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
            var cursor = collection.find(new Document("destinatario", destinatarioId)).iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Mensaje m = mappearMensaje(doc);
                if (m != null) mensajes.add(m);
            }
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al obtener mensajes por destinatario en MongoDB");
        }
        return mensajes;
    }

    public Mensaje mappearMensaje(Document doc) throws ErrorConectionMongoException {
        if (doc == null) return null;
        Usuario remitente = UsuarioService.instance.getById(doc.getInteger("remitente.id"));
        Usuario destinatario = UsuarioService.instance.getById(doc.getInteger("destinatario.id"));
        Mensaje mensaje = new Mensaje();
        mensaje.setId(doc.getInteger("id"));
        mensaje.setRemitente(remitente);
        mensaje.setDestinatario(destinatario);
        mensaje.setContenido(doc.getString("contenido"));
        mensaje.setFechaEnvio(doc.getDate("fechaEnvio").toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
        mensaje.setTipo(doc.getString("tipo"));
        mensaje.setIdGrupo(doc.getInteger("grupoId"));

        return mensaje;
    }
}
