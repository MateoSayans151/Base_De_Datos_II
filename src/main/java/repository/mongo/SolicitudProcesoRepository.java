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

            Integer usuarioId = solicitud.getUsuario() != null ? solicitud.getUsuario().getId() : null;
            Integer procesoId = solicitud.getProceso() != null ? solicitud.getProceso().getId() : null;

            Document doc = new Document()
                    .append("id", solicitud.getId())
                    .append("usuarioId", usuarioId)
                    .append("procesoId", procesoId)
                    .append("estado", solicitud.getEstado())
                    .append("fechaSolicitud", toDate(solicitud.getFechaSolicitud()));

            var filter = Filters.eq("id", solicitud.getId());
            Document existing = col.find(filter).first();

            if (existing == null)
                col.insertOne(doc);
            else
                col.updateOne(filter, new Document("$set", doc));

            return solicitud;

        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al guardar SolicitudProceso en MongoDB");
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
            throw new ErrorConectionMongoException("Error al listar SolicitudProceso");
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
            throw new ErrorConectionMongoException("Error al obtener SolicitudProceso por id");
        }
    }

    public boolean existsById(int id) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var db = mongoPool.getConnection();
        try {
            MongoCollection<Document> col = db.getCollection(COLLECTION_NAME);
            return col.countDocuments(Filters.eq("id", id)) > 0;
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al verificar existencia de SolicitudProceso");
        }
    }

    public void deleteById(int id) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var db = mongoPool.getConnection();
        try {
            MongoCollection<Document> col = db.getCollection(COLLECTION_NAME);
            col.deleteOne(Filters.eq("id", id));
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al eliminar SolicitudProceso");
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
            throw new ErrorConectionMongoException("Error al buscar SolicitudProceso por estado");
        }
    }

    public List<SolicitudProceso> findByUsuario_Id(int usuarioId) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var db = mongoPool.getConnection();
        List<SolicitudProceso> out = new ArrayList<>();
        try {
            MongoCollection<Document> col = db.getCollection(COLLECTION_NAME);
            var it = col.find(Filters.eq("usuarioId", usuarioId)).iterator();
            while (it.hasNext())
                out.add(mapear(it.next()));
            return out;
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al buscar SolicitudProceso por usuarioId");
        }
    }

    public List<SolicitudProceso> findByUsuario_IdAndEstadoIgnoreCase(int usuarioId, String estado) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var db = mongoPool.getConnection();
        List<SolicitudProceso> out = new ArrayList<>();
        try {
            MongoCollection<Document> col = db.getCollection(COLLECTION_NAME);
            var filter = Filters.and(
                    Filters.eq("usuarioId", usuarioId),
                    Filters.regex("estado", "^" + escapeRegex(estado) + "$", "i")
            );
            var it = col.find(filter).iterator();
            while (it.hasNext())
                out.add(mapear(it.next()));
            return out;
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al buscar SolicitudProceso por usuarioId y estado");
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

        Integer procesoId = doc.getInteger("procesoId");
        Integer usuarioId = doc.getInteger("usuarioId");

        // Rehidratar Usuario
        if (usuarioId != null) {
            Usuario u = usuarioRepo.getUserById(usuarioId);
            if (u != null && u.getId() != 0)
                s.setUsuario(u);
        }

        // Rehidratar Proceso
        if (procesoId != null) {
            Proceso p = procesoRepo.obtenerProceso(procesoId);
            if (p != null)
                s.setProceso(p);
        }
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

