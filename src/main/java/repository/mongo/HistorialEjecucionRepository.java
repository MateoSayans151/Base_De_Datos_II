package repository.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import connections.MongoPool;
import entity.HistorialEjecucion;
import exceptions.ErrorConectionMongoException;
import org.bson.Document;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class HistorialEjecucionRepository {

    private static HistorialEjecucionRepository instance;
    private static final String COLLECTION_NAME = "logs";
    private static final String COUNTERS_COLLECTION = "counters";
    private static final String COUNTER_KEY = "logs_seq";

    private HistorialEjecucionRepository() {}

    public static HistorialEjecucionRepository getInstance() {
        if (instance == null) instance = new HistorialEjecucionRepository();
        return instance;
    }

    /* ===========================
       CRUD BÁSICO
       =========================== */

    public HistorialEjecucion save(HistorialEjecucion h) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var db = mongoPool.getConnection();
        try {
            MongoCollection<Document> col = db.getCollection(COLLECTION_NAME);

            if (h.getIdEjecucion() == 0)
                h.setIdEjecucion(nextId(db));

            Document doc = new Document()
                    .append("id", h.getIdEjecucion())
                    .append("solicitud", h.getSolicitud() != null ? String.valueOf(h.getSolicitud().getId()) : null)
                    .append("fecha", toDate(h.getFechaEjecucion()))
                    .append("resultado", h.getResultado())
                    .append("estado", h.getEstado());

            Document existing = col.find(Filters.eq("id", h.getIdEjecucion())).first();
            if (existing == null) col.insertOne(doc);
            else col.updateOne(Filters.eq("id", h.getIdEjecucion()), new Document("$set", doc));

            return h;

        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al guardar HistorialEjecucion en MongoDB");
        }
    }

    public Optional<HistorialEjecucion> findById(int id) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var db = mongoPool.getConnection();
        try {
            MongoCollection<Document> col = db.getCollection(COLLECTION_NAME);
            Document doc = col.find(Filters.eq("id", id)).first();
            return Optional.ofNullable(mapear(doc));
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al obtener HistorialEjecucion por id");
        }
    }

    public List<HistorialEjecucion> findAll() throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var db = mongoPool.getConnection();
        List<HistorialEjecucion> out = new ArrayList<>();
        try {
            MongoCollection<Document> col = db.getCollection(COLLECTION_NAME);
            var it = col.find().iterator();
            while (it.hasNext()) out.add(mapear(it.next()));
            return out;
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al listar HistorialEjecucion");
        }
    }

    public void deleteById(int id) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var db = mongoPool.getConnection();
        try {
            MongoCollection<Document> col = db.getCollection(COLLECTION_NAME);
            col.deleteOne(Filters.eq("id", id));
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al eliminar HistorialEjecucion");
        }
    }

    /* ===========================
       CONSULTAS PERSONALIZADAS
       =========================== */

    public List<HistorialEjecucion> findByEstado(String estado) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var db = mongoPool.getConnection();
        List<HistorialEjecucion> out = new ArrayList<>();
        try {
            MongoCollection<Document> col = db.getCollection(COLLECTION_NAME);
            var it = col.find(Filters.eq("estado", estado)).iterator();
            while (it.hasNext()) out.add(mapear(it.next()));
            return out;
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al buscar HistorialEjecucion por estado");
        }
    }

    public List<HistorialEjecucion> findBySolicitud(String solicitud) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var db = mongoPool.getConnection();
        List<HistorialEjecucion> out = new ArrayList<>();
        try {
            MongoCollection<Document> col = db.getCollection(COLLECTION_NAME);
            var it = col.find(Filters.eq("solicitud", solicitud)).iterator();
            while (it.hasNext()) out.add(mapear(it.next()));
            return out;
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al buscar HistorialEjecucion por solicitud");
        }
    }

    /* ===========================
       MAPEO
       =========================== */

    private HistorialEjecucion mapear(Document doc) {
        if (doc == null) return null;

        HistorialEjecucion h = new HistorialEjecucion();
        h.setIdEjecucion(doc.getInteger("id"));
        h.setFechaEjecucion(fromDate(doc.getDate("fecha")));
        h.setResultado(doc.getString("resultado"));
        h.setEstado(doc.getString("estado"));
        return h;
    }

    private static Date toDate(LocalDateTime ldt) {
        return (ldt == null) ? null : Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }

    private static LocalDateTime fromDate(Date date) {
        return (date == null) ? null : LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    private int nextId(com.mongodb.client.MongoDatabase db) {
        MongoCollection<Document> counters = db.getCollection(COUNTERS_COLLECTION);
        Document updated = counters.findOneAndUpdate(
                Filters.eq("_id", COUNTER_KEY),
                Updates.inc("seq", 1),
                new FindOneAndUpdateOptions().upsert(true).returnDocument(ReturnDocument.AFTER)
        );
        return updated.getInteger("seq", 1);
    }
}

