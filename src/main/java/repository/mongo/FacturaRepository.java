package repository.mongo;

import connections.MongoPool;
import entity.Factura;
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
            Document facturaDoc = new Document()
                    .append("id", factura.getId())
                    .append("fechaEmision", factura.getFechaEmision())
                    .append("procesosFacturados", factura.getProcesosFacturados())
                    .append("total", factura.getTotal())
                    .append("estado", factura.getEstado());

            if (factura.getUsuario() != null) {
                Document usuDoc = new Document()
                        .append("id", factura.getUsuario().getId())
                        .append("nombre", factura.getUsuario().getNombre())
                        .append("mail", factura.getUsuario().getMail());
                facturaDoc.append("usuario", usuDoc);
            }

            collection.insertOne(facturaDoc);
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al guardar la factura en MongoDB");
        }
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


        // Handle procesos facturados
        List<Document> procDocs = (List<Document>) doc.get("procesosFacturados");
        if (procDocs != null) {
            List<Proceso> procesos = new ArrayList<>();
            for (Document procDoc : procDocs) {
                Proceso proceso = new Proceso();
                proceso.setId(procDoc.getInteger("id"));
                proceso.setNombre(procDoc.getString("nombre"));
                proceso.setDescripcion(procDoc.getString("descripcion"));
                proceso.setTipo(procDoc.getString("tipo"));
                proceso.setCosto(procDoc.getDouble("costo"));
                procesos.add(proceso);
            }
            factura.setProcesosFacturados(procesos);
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

        List<Document> procDocs = (List<Document>) doc.get("procesosFacturados");
        if (procDocs != null) {
            List<Proceso> procesos = new ArrayList<>();
            for (Document procDoc : procDocs) {
                Proceso proceso = new Proceso();
                proceso.setId(procDoc.getInteger("id"));
                proceso.setNombre(procDoc.getString("nombre"));
                proceso.setDescripcion(procDoc.getString("descripcion"));
                proceso.setTipo(procDoc.getString("tipo"));
                proceso.setCosto(procDoc.getDouble("costo"));
                procesos.add(proceso);
            }
            factura.setProcesosFacturados(procesos);
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
            var query = new Document("usuario.id", usuarioId);
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