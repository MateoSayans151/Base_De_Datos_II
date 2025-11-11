package repository.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;
import connections.MongoPool;
import entity.Proceso;
import entity.SolicitudProceso;
import entity.Usuario;
import exceptions.ErrorConectionMongoException;
import org.bson.Document;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class SolicitudProcesoRepository {

    private static SolicitudProcesoRepository instance;
    private static final String COLLECTION_NAME = "solicitudProceso";
    private static final String COUNTERS_COLLECTION = "counters";
    private static final String COUNTER_KEY = "solicitud_proceso_seq";

    private final ProcesoRepository procesoRepo = ProcesoRepository.getInstance();
    private final UsuarioRepository usuarioRepo = UsuarioRepository.getInstance();

    private SolicitudProcesoRepository() {}

    public static SolicitudProcesoRepository getInstance() {
        if (instance == null)
            instance = new SolicitudProcesoRepository();
        return instance;
    }

    /* ===========================
       CRUD
       =========================== */

    public SolicitudProceso save(SolicitudProceso solicitud) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var db = mongoPool.getConnection();
        try {
            MongoCollection<Document> col = db.getCollection(COLLECTION_NAME);

            if (solicitud.getId() == 0)
                solicitud.setId(nextId(db));

            Integer usuarioId = null;
            Integer procesoId = null;

            // Resolver usuarioId: si el objeto Usuario no tiene id, intentar buscar por mail en Mongo
            if (solicitud.getUsuario() != null) {
                if (solicitud.getUsuario().getId() != 0) {
                    usuarioId = solicitud.getUsuario().getId();
                } else if (solicitud.getUsuario().getMail() != null) {
                    try {
                        var usuarioMongo = usuarioRepo.getUserByMail(solicitud.getUsuario().getMail());
                        if (usuarioMongo != null && usuarioMongo.getId() != 0) usuarioId = usuarioMongo.getId();
                    } catch (Exception ignored) {
                        // si la búsqueda falla, dejamos usuarioId en null
                    }
                }
            }

            // Si aún no tenemos usuarioId pero tenemos un objeto Usuario, intentar crearlo en Mongo
            if (usuarioId == null && solicitud.getUsuario() != null) {
                try {
                    usuarioRepo.createUser(solicitud.getUsuario());
                    if (solicitud.getUsuario().getId() != 0) usuarioId = solicitud.getUsuario().getId();
                } catch (Exception ex) {
                    // si falla la creación, no interrumpimos el guardado de la solicitud; dejamos usuarioId en null
                    System.err.println("[SolicitudProcesoRepository.save] No se pudo crear usuario en Mongo: " + ex.getMessage());
                }
            }

            // Resolver procesoId: si no tiene id, dejamos null (no siempre existe en Mongo)
            if (solicitud.getProceso() != null) {
                if (solicitud.getProceso().getId() != 0) procesoId = solicitud.getProceso().getId();
            }

            // Construir documento con los campos requeridos por el schema
            Document doc = new Document()
                    .append("id", solicitud.getId())
                    .append("usuario", new Document()
                        .append("idUsuario", solicitud.getUsuario().getId()) // Campo requerido por el schema
                        .append("nombre", solicitud.getUsuario().getNombre())
                        .append("mail", solicitud.getUsuario().getMail()))
                    .append("procesoSolicitado", new Document()
                        .append("idProceso", solicitud.getProceso().getId()) // Campo requerido por el schema
                        .append("nombre", solicitud.getProceso().getNombre())
                        .append("descripcion", solicitud.getProceso().getDescripcion()) // Campo requerido por el schema
                        .append("tipo", solicitud.getProceso().getTipo())
                        .append("costo", solicitud.getProceso().getCosto()))
                    .append("estado", solicitud.getEstado())
                    .append("fechaSolicitud", toDate(solicitud.getFechaSolicitud()));

            // Si la solicitud incluye ciudad/pais, guardarlos (opcional)
            if (solicitud.getCiudad() != null && !solicitud.getCiudad().isEmpty()) {
                doc.append("ciudad", solicitud.getCiudad());
            }
            if (solicitud.getPais() != null && !solicitud.getPais().isEmpty()) {
                doc.append("pais", solicitud.getPais());
            }

            var filter = Filters.eq("id", solicitud.getId());
            Document existing = col.find(filter).first();

            if (existing == null) {
                col.insertOne(doc);
                System.out.println("[SolicitudProcesoRepository.save] Inserted solicitud id=" + solicitud.getId() + ", usuarioId=" + usuarioId + ", procesoId=" + procesoId);
            } else {
                col.updateOne(filter, new Document("$set", doc));
                System.out.println("[SolicitudProcesoRepository.save] Updated solicitud id=" + solicitud.getId());
            }

            return solicitud;

        } catch (Exception e) {
            System.err.println("[SolicitudProcesoRepository.save] Error: " + e.getMessage());
            throw new ErrorConectionMongoException("Error al guardar SolicitudProceso en MongoDB: " + e.getMessage());
        }
    }

    public List<SolicitudProceso> findAll() throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var db = mongoPool.getConnection();
        List<SolicitudProceso> out = new ArrayList<>();
        try {
            MongoCollection<Document> col = db.getCollection(COLLECTION_NAME);
            var it = col.find().iterator();
            while (it.hasNext())
                out.add(mapear(it.next()));
            return out;
        } catch (Exception e) {
            System.err.println("[SolicitudProcesoRepository.findAll] Error: " + e.getMessage());
            throw new ErrorConectionMongoException("Error al listar SolicitudProceso: " + e.getMessage());
        }
    }

    public Optional<SolicitudProceso> findById(int id) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var db = mongoPool.getConnection();
        try {
            MongoCollection<Document> col = db.getCollection(COLLECTION_NAME);
            Document doc = col.find(Filters.eq("id", id)).first();
            return Optional.ofNullable(mapear(doc));
        } catch (Exception e) {
            System.err.println("[SolicitudProcesoRepository.findById] Error: " + e.getMessage());
            throw new ErrorConectionMongoException("Error al obtener SolicitudProceso por id: " + e.getMessage());
        }
    }

    public boolean existsById(int id) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var db = mongoPool.getConnection();
        try {
            MongoCollection<Document> col = db.getCollection(COLLECTION_NAME);
            return col.countDocuments(Filters.eq("id", id)) > 0;
        } catch (Exception e) {
            System.err.println("[SolicitudProcesoRepository.existsById] Error: " + e.getMessage());
            throw new ErrorConectionMongoException("Error al verificar existencia de SolicitudProceso: " + e.getMessage());
        }
    }

    public void deleteById(int id) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var db = mongoPool.getConnection();
        try {
            MongoCollection<Document> col = db.getCollection(COLLECTION_NAME);
            col.deleteOne(Filters.eq("id", id));
        } catch (Exception e) {
            System.err.println("[SolicitudProcesoRepository.deleteById] Error: " + e.getMessage());
            throw new ErrorConectionMongoException("Error al eliminar SolicitudProceso: " + e.getMessage());
        }
    }

    /* ===========================
       QUERIES PERSONALIZADAS
       =========================== */

    public List<SolicitudProceso> findByEstadoIgnoreCase(String estado) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var db = mongoPool.getConnection();
        List<SolicitudProceso> out = new ArrayList<>();
        try {
            MongoCollection<Document> col = db.getCollection(COLLECTION_NAME);
            var filter = Filters.regex("estado", "^" + escapeRegex(estado) + "$", "i");
            var it = col.find(filter).iterator();
            while (it.hasNext())
                out.add(mapear(it.next()));
            return out;
        } catch (Exception e) {
            System.err.println("[SolicitudProcesoRepository.findByEstadoIgnoreCase] Error: " + e.getMessage());
            throw new ErrorConectionMongoException("Error al buscar SolicitudProceso por estado: " + e.getMessage());
        }
    }

    public List<SolicitudProceso> findByUsuario_Id(int usuarioId) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var db = mongoPool.getConnection();
        List<SolicitudProceso> out = new ArrayList<>();
        try {
            MongoCollection<Document> col = db.getCollection(COLLECTION_NAME);
            var it = col.find(Filters.eq("usuario.idUsuario", usuarioId)).iterator();
            while (it.hasNext())
                out.add(mapear(it.next()));
            return out;
        } catch (Exception e) {
            System.err.println("[SolicitudProcesoRepository.findByUsuario_Id] Error: " + e.getMessage());
            throw new ErrorConectionMongoException("Error al buscar SolicitudProceso por usuarioId: " + e.getMessage());
        }
    }

    public List<SolicitudProceso> findByUsuario_IdAndEstadoIgnoreCase(int usuarioId, String estado) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var db = mongoPool.getConnection();
        List<SolicitudProceso> out = new ArrayList<>();
        try {
            MongoCollection<Document> col = db.getCollection(COLLECTION_NAME);
            var filter = Filters.and(
                    Filters.eq("usuario.idUsuario", usuarioId),
                    Filters.regex("estado", "^" + escapeRegex(estado) + "$", "i")
            );
            var it = col.find(filter).iterator();
            while (it.hasNext())
                out.add(mapear(it.next()));
            return out;
        } catch (Exception e) {
            System.err.println("[SolicitudProcesoRepository.findByUsuario_IdAndEstadoIgnoreCase] Error: " + e.getMessage());
            throw new ErrorConectionMongoException("Error al buscar SolicitudProceso por usuarioId y estado: " + e.getMessage());
        }
    }

    /* ===========================
       MAPEOS
       =========================== */

    private SolicitudProceso mapear(Document doc) throws ErrorConectionMongoException {
        if (doc == null) return null;

        SolicitudProceso s = new SolicitudProceso();
        s.setId(doc.getInteger("id", 0));
        s.setEstado(doc.getString("estado"));
        s.setFechaSolicitud(fromDate(doc.getDate("fechaSolicitud")));

        // Obtener documentos embebidos
        Document usuarioDoc = doc.get("usuario", Document.class);
        Document procesoDoc = doc.get("procesoSolicitado", Document.class);

        if (usuarioDoc != null) {
            Integer usuarioId = usuarioDoc.getInteger("idUsuario");
            // Rehidratar Usuario
            if (usuarioId != null) {
                Usuario u = usuarioRepo.getUserById(usuarioId);
                if (u != null && u.getId() != 0)
                    s.setUsuario(u);
            }
        }

        if (procesoDoc != null) {
            Integer procesoId = procesoDoc.getInteger("idProceso");
            // Rehidratar Proceso
            if (procesoId != null) {
                Proceso p = procesoRepo.obtenerProceso(procesoId);
                if (p != null)
                    s.setProceso(p);
            }
        }
        // Leer ciudad/pais si están presentes
        if (doc.containsKey("ciudad")) s.setCiudad(doc.getString("ciudad"));
        if (doc.containsKey("pais")) s.setPais(doc.getString("pais"));
        
        return s;
    }

    /* ===========================
       UTILS
       =========================== */

    private static Date toDate(LocalDateTime ldt) {
        return (ldt == null) ? null : Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }

    private static LocalDateTime fromDate(Date date) {
        return (date == null) ? null : LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    private static String escapeRegex(String text) {
        if (text == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if ("\\.^$|?*+()[]{}".indexOf(c) >= 0) sb.append('\\');
            sb.append(c);
        }
        return sb.toString();
    }

    private int nextId(com.mongodb.client.MongoDatabase db) {
    MongoCollection<Document> counters = db.getCollection("counters");
    Document updated = counters.findOneAndUpdate(
            Filters.eq("_id", "solicitud_proceso_seq"),
            Updates.inc("seq", 1),
            new FindOneAndUpdateOptions()
                    .upsert(true)
                    .returnDocument(ReturnDocument.AFTER)
    );
    return updated.getInteger("seq", 1);
    }
}

