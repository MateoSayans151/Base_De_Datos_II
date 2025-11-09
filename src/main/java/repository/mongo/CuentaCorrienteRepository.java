package repository.mongo;

import connections.MongoPool;
import entity.CuentaCorriente;
import exceptions.ErrorConectionMongoException;
import org.bson.Document;

import java.time.LocalDateTime;

public class CuentaCorrienteRepository {
    private static CuentaCorrienteRepository instance;
    private final String COLLECTION_NAME = "cuenta_corriente";

    private CuentaCorrienteRepository() {}

    public static CuentaCorrienteRepository getInstance() {
        if (instance == null) {
            instance = new CuentaCorrienteRepository();
        }
        return instance;
    }

    public void createCuentaCorriente(CuentaCorriente cuenta) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        try {
            var collection = connection.getCollection(COLLECTION_NAME);
        Document doc = new Document()
            .append("usuario_id", cuenta.getUsuarioId())
            .append("saldo", cuenta.getSaldo())
            .append("ultima_actualizacion", LocalDateTime.now());
            collection.insertOne(doc);
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al crear cuenta corriente");
        }
    }

    public CuentaCorriente getCuentaByUsuario(int usuarioId) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        try {
            var collection = connection.getCollection(COLLECTION_NAME);
            Document query = new Document("usuario_id", usuarioId);
            Document doc = collection.find(query).first();
            
            if (doc != null) {
                CuentaCorriente cuenta = new CuentaCorriente();
                cuenta.setSaldo(doc.getDouble("saldo"));
                Integer uid = doc.getInteger("usuario_id");
                if (uid != null) {
                    cuenta.setUsuarioId(uid);
                }
                return cuenta;
            }
            return null;
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al obtener cuenta corriente");
        }
    }

    public void updateSaldo(int usuarioId, double nuevoSaldo) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        try {
            var collection = connection.getCollection(COLLECTION_NAME);
            Document query = new Document("usuario_id", usuarioId);
            Document update = new Document("$set", new Document("saldo", nuevoSaldo)
                    .append("ultima_actualizacion", LocalDateTime.now()));
            collection.updateOne(query, update);
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al actualizar saldo");
        }
    }
}