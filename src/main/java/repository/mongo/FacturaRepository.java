package repository.mongo;

import connections.MongoPool;
import entity.Factura;
import entity.Proceso;
import entity.Usuario;
import exceptions.ErrorConectionMongoException;
import org.bson.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public void guardarFactura(Factura factura) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        try {
            var collection = connection.getCollection(COLLECTION_NAME);
            Document facturaDoc = new Document()
                    .append("id", factura.getId())
                    .append("fechaEmision", factura.getFechaEmision())
                    .append("procesosFacturados", factura.getProcesosFacturados())
                    .append("total", factura.getTotal() != null ? factura.getTotal().doubleValue() : null)
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

    public List<Factura> obtenerFacturasPorUsuarioId(int usuarioId) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        var collection = connection.getCollection(COLLECTION_NAME);
        List<Factura> facturas = new ArrayList<>();
        try {
            Document orFilter = new Document("$or", Arrays.asList(
                    new Document("usuario.id", usuarioId),
                    new Document("usuario.idUsuario", usuarioId)
            ));
            var cursor = collection.find(orFilter).iterator();
            while (cursor.hasNext()) {
                Document result = cursor.next();
                facturas.add(mappearFactura(result));
            }
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al obtener las facturas por usuario en MongoDB");
        }
        return facturas;
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


        factura.setProcesosFacturados((List<Proceso>) doc.get("procesosFacturados"));


        factura.setTotal(doc.getDouble(doc.getDouble("total")));

        factura.setEstado(doc.getString("estado"));

        return factura;
    }
}