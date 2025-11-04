package repository.mongo;

import connections.MongoPool;
import entity.Usuario;
import exceptions.ErrorConectionMongoException;
import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.IndexOptions;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class UsuarioRepository {

    private static volatile UsuarioRepository instance;
    private static final String COLLECTION_NAME = "usuarios";

    private UsuarioRepository() {
        // crear índice único por mail (idempotente)
        var collection = getCollection();
        collection.createIndex(Indexes.ascending("mail"), new IndexOptions().unique(true));
    }

    public static UsuarioRepository getInstance() {
        if (instance == null) {
            synchronized (UsuarioRepository.class) {
                if (instance == null) instance = new UsuarioRepository();
            }
        }
        return instance;
    }

    private MongoCollection<Document> getCollection() {
        var connection = MongoPool.getInstance().getConnection();
        return connection.getCollection(COLLECTION_NAME);
    }

    // CREATE
    public void createUser(Usuario usuario) throws ErrorConectionMongoException {
        var collection = getCollection();
        Date fecha = usuario.getFechaRegistro() == null
                ? new Date()
                : Date.from(usuario.getFechaRegistro().atZone(ZoneId.systemDefault()).toInstant());

        Document document = new Document()
                .append("id", usuario.getId())                 // si decidís usar tu propio id int
                .append("nombre", usuario.getNombre())
                .append("mail", usuario.getMail())
                .append("contrasena", usuario.getContrasena())
                .append("estado", usuario.getEstado())
                .append("rol", usuario.getRol())               // String consistente
                .append("fechaRegistro", fecha);

        try {
            collection.insertOne(document);
        } catch (Exception e) {
            throw new RuntimeException("Error insertando usuario", e);
        }
        System.out.println("Usuario creado con mail: " + usuario.getMail());
    }

    // READ by mail
    public Optional<Usuario> getUserByMail(String mail) throws ErrorConectionMongoException {
        var collection = getCollection();
        try {
            Document filter = new Document("mail", mail);
            Document result = collection.find(filter).first();
            return Optional.ofNullable(result).map(this::mapearUsuario);
        } catch (Exception e) {
            throw new RuntimeException("Error buscando usuario por mail", e);
        }
    }

    // READ by id (tu id int propio)
    public Optional<Usuario> getUserById(int idUsuario) throws ErrorConectionMongoException {
        var collection = getCollection();
        try {
            Document filter = new Document("id", idUsuario);
            Document result = collection.find(filter).first();
            return Optional.ofNullable(result).map(this::mapearUsuario);
        } catch (Exception e) {
            throw new RuntimeException("Error buscando usuario por id", e);
        }
    }

    // READ all
    public List<Usuario> getAllUsers() throws ErrorConectionMongoException {
        var collection = getCollection();
        List<Usuario> usuarios = new ArrayList<>();
        try {
            for (Document doc : collection.find()) {
                usuarios.add(mapearUsuario(doc));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando usuarios", e);
        }
        return usuarios;
    }

    // UPDATE básico (por mail)
    public boolean updateUserByMail(String mail, Usuario updates) throws ErrorConectionMongoException {
        var collection = getCollection();
        Document set = new Document();
        if (updates.getNombre() != null) set.append("nombre", updates.getNombre());
        if (updates.getContrasena() != null) set.append("contrasena", updates.getContrasena());
        if (updates.getEstado() != null) set.append("estado", updates.getEstado());
        if (updates.getRol() != null) set.append("ro l", updates.getRol());

        if (set.isEmpty()) return false;

        var update = new Document("$set", set);
        var result = collection.updateOne(new Document("mail", mail), update);
        return result.getModifiedCount() > 0;
    }

    // DELETE (por mail)
    public boolean deleteUserByMail(String mail) throws ErrorConectionMongoException {
        var collection = getCollection();
        var result = collection.deleteOne(new Document("mail", mail));
        return result.getDeletedCount() > 0;
    }

    // Mapper consistente con el documento guardado
    private Usuario mapearUsuario(Document d){
        Usuario user = new Usuario();
        user.setId(d.getInteger("id")); // si usás tu id int
        user.setNombre(d.getString("nombre"));
        user.setMail(d.getString("mail"));
        user.setContrasena(d.getString("contrasena"));
        user.setEstado(d.getString("estado"));
        user.setRol(d.getString("rol")); // String directo
        Date fecha = d.getDate("fechaRegistro");
        if (fecha != null) {
            user.setFechaRegistro(fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        }
        return user;
    }
}
