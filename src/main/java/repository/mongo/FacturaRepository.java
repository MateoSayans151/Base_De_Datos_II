package repository.mongo;

import connections.MongoPool;
import entity.Factura;
import entity.Mensaje;
import entity.Proceso;
import entity.Usuario;
import exceptions.ErrorConectionMongoException;
import org.bson.Document;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class FacturaRepository {

    private static FacturaRepository instance;
    private final String COLLECTION_NAME = "factura";

    private FacturaRepository() {
    }

    public static FacturaRepository getInstance() {
        if (instance == null)
            instance = new FacturaRepository();
        return instance;
    }

    public void createFactura(Factura factura) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        try {
            var collection = connection.getCollection(COLLECTION_NAME);
            // Ensure factura has an id
            if (factura.getId() == 0) {
                int lastId = getLastId();
                factura.setId(lastId + 1);
            }

            // Convert LocalDate to java.util.Date for MongoDB
            java.util.Date fechaDate = null;
            if (factura.getFechaEmision() != null) {
                fechaDate = java.util.Date.from(factura.getFechaEmision().atStartOfDay(ZoneId.systemDefault()).toInstant());
            }

            Document facturaDoc = new Document()
                    .append("id", factura.getId())
                    .append("fechaEmision", fechaDate)
                    .append("total", factura.getTotal())
                    .append("estado", factura.getEstado());

            if (factura.getUsuario() != null) {
                Document usuDoc = new Document()
                        .append("idUsuario", factura.getUsuario().getId())
                        .append("nombre", factura.getUsuario().getNombre())
                        .append("mail", factura.getUsuario().getMail());
                facturaDoc.append("usuario", usuDoc);
            }       
            if (factura.getProcesoFacturado() != null) {
                Proceso p = factura.getProcesoFacturado();
                Document pd = new Document()
                        .append("idProceso", p.getId())
                        .append("nombre", p.getNombre())
                        .append("costo", p.getCosto())
                        .append("descripcion", p.getDescripcion());
                facturaDoc.append("procesosFacturados", pd);
            }

            collection.insertOne(facturaDoc);
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al crear la factura en MongoDB: " + e.getMessage());      
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
    public List<Factura> getUserFacturas(int usuarioId) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        System.out.println("ESTOY ACA WACHO");
        List<Factura> facturas = new ArrayList<>();
        try {
            var collection = connection.getCollection(COLLECTION_NAME);
            var cursor = collection.find(new Document("usuario.idUsuario", usuarioId)).iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                facturas.add(convertDocumentToFactura(doc));
            }
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al obtener facturas del usuario: " + e.getMessage());
        }
        return facturas;
    }
    public Factura obtenerFactura(int id) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        Factura factura = null;
        try {
            var collection = connection.getCollection(COLLECTION_NAME);
            Document filter = new Document("id", id);
            Document result = collection.find(filter).first();
            if (result != null) {
                factura = mappearFactura(result);
            }
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al obtener la factura en MongoDB");
        }
        return factura;
    }

    public Factura mappearFactura(Document doc) {
        Factura factura = new Factura();
        factura.setId(doc.getInteger("id"));

        // Usuario embebido
        Document usuarioDoc = (Document) doc.get("usuario");
        if (usuarioDoc != null) {
            Usuario u = new Usuario();
            // handle possible field name variations id / idUsuario
            Integer uid = usuarioDoc.containsKey("id") ? usuarioDoc.getInteger("id") : usuarioDoc.getInteger("idUsuario");
            u.setId(uid);
            u.setNombre(usuarioDoc.getString("nombre"));
            u.setMail(usuarioDoc.getString("mail"));
            factura.setUsuario(u);
        }

        // fechaEmision conversion
        Date fecha = doc.getDate("fechaEmision");
        if (fecha != null) {
            Instant instant = Instant.ofEpochMilli(fecha.getTime());
            LocalDate ldt = LocalDate.from(instant.atZone(ZoneId.systemDefault()));
            factura.setFechaEmision(ldt);
        }


        // Handle procesos facturados (stored as a single Document object)
        Object procesosObj = doc.get("procesosFacturados");
        if (procesosObj != null && procesosObj instanceof Document) {
            Document procDoc = (Document) procesosObj;
            Proceso proceso = new Proceso();
            proceso.setId(procDoc.getInteger("idProceso") != null ? procDoc.getInteger("idProceso") : procDoc.getInteger("id"));
            proceso.setNombre(procDoc.getString("nombre"));
            proceso.setDescripcion(procDoc.getString("descripcion"));
            proceso.setTipo(procDoc.getString("tipo"));
            proceso.setCosto(procDoc.getDouble("costo"));
            factura.setProcesoFacturado(proceso);
        }

        if (doc.containsKey("total")) {
            factura.setTotal(doc.getDouble("total"));
        }

        factura.setEstado(doc.getString("estado"));

        return factura;
    }

    private Factura convertDocumentToFactura(Document doc) {
        Factura factura = new Factura();
        factura.setId(doc.getInteger("id"));

        Document usuarioDoc = (Document) doc.get("usuario");
        if (usuarioDoc != null) {
            Usuario u = new Usuario();
            Integer uid = usuarioDoc.containsKey("id") ? usuarioDoc.getInteger("id") : usuarioDoc.getInteger("idUsuario");
            u.setId(uid);
            u.setNombre(usuarioDoc.getString("nombre"));
            u.setMail(usuarioDoc.getString("mail"));
            factura.setUsuario(u);
        }

        Date fecha = doc.getDate("fechaEmision");
        if (fecha != null) {
            Instant instant = Instant.ofEpochMilli(fecha.getTime());
            LocalDate ldt = LocalDate.from(instant.atZone(ZoneId.systemDefault()));
            factura.setFechaEmision(ldt);
        }

        // Handle procesos facturados (stored as a single Document object)
        Object procesosObj = doc.get("procesosFacturados");
        if (procesosObj != null && procesosObj instanceof Document) {
            Document procDoc = (Document) procesosObj;
            Proceso proceso = new Proceso();
            proceso.setId(procDoc.getInteger("idProceso") != null ? procDoc.getInteger("idProceso") : procDoc.getInteger("id"));
            proceso.setNombre(procDoc.getString("nombre"));
            proceso.setDescripcion(procDoc.getString("descripcion"));
            proceso.setTipo(procDoc.getString("tipo"));
            proceso.setCosto(procDoc.getDouble("costo"));
            factura.setProcesoFacturado(proceso);
        }

        if (doc.containsKey("total")) {
            factura.setTotal(doc.getDouble("total"));
        }

        factura.setEstado(doc.getString("estado"));

        return factura;
    }

    public List<Factura> getFacturasByUsuario(int usuarioId) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        List<Factura> facturas = new ArrayList<>();
        try {
            var collection = connection.getCollection(COLLECTION_NAME);
            var query = new Document("usuario.idUsuario", usuarioId);
            var cursor = collection.find(query);
            
            for (Document doc : cursor) {
                facturas.add(convertDocumentToFactura(doc));
            }
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al obtener facturas del usuario: " + usuarioId);
        }
        return facturas;
    }

    public List<Factura> getFacturasByEstado(String estado) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        List<Factura> facturas = new ArrayList<>();
        try {
            var collection = connection.getCollection(COLLECTION_NAME);
            var query = new Document("estado", estado);
            var cursor = collection.find(query);
            
            for (Document doc : cursor) {
                facturas.add(convertDocumentToFactura(doc));
            }
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al obtener facturas por estado: " + estado);
        }
        return facturas;
    }

    public void updateEstado(int facturaId, String nuevoEstado) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        try {
            var collection = connection.getCollection(COLLECTION_NAME);
            var query = new Document("id", facturaId);
            var update = new Document("$set", new Document("estado", nuevoEstado));
            collection.updateOne(query, update);
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al actualizar el estado de la factura: " + facturaId);
        }
    }
}