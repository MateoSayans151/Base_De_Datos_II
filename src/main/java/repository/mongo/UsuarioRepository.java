package repository.mongo;

import connections.MongoPool;
import entity.Rol;
import entity.Usuario;
import exceptions.ErrorConectionMongoException;
import org.bson.Document;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class UsuarioRepository {

    private static UsuarioRepository instance;
    private static String COLLECTION_NAME = "usuario";

    private UsuarioRepository() {}

    public static UsuarioRepository getInstance() {
        if(instance == null)
            instance = new UsuarioRepository();
        return instance;
    }

    public void createUser(Usuario usuario) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        var collection = connection.getCollection(COLLECTION_NAME);
        Document document = new Document()
                .append("id", usuario.getId())
                .append("nombre", usuario.getNombre())
                .append("mail", usuario.getMail())
                .append("contrasena", usuario.getContrasena())
                .append("estado", usuario.getEstado())
                .append("rol", usuario.getRol())
                .append("fechaRegistro", Timestamp.valueOf(usuario.getFechaRegistro()));
        try{
            collection.insertOne(document);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("Usuario creado con mail: " + usuario.getMail());
    }
    public Usuario getUserByMail(String mail) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        var collection = connection.getCollection(COLLECTION_NAME);
        Usuario usuario = new Usuario();
        try {
            Document filter = new Document("mail", mail);
            Document result = collection.find(filter).first();
            if (result != null) {

                usuario.setId(result.getInteger("id"));
                usuario.setNombre(result.getString("nombre"));
                usuario.setMail(result.getString("mail"));
                usuario.setContrasena(result.getString("contrasena"));
                usuario.setEstado(result.getString("estado"));
                usuario.setRol(result.getString("rol"));
                usuario.setFechaRegistro(result.getDate("fechaRegistro").toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());

            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        return usuario;
    }
    public Usuario getUserById(int idUsuario) throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        var collection = connection.getCollection(COLLECTION_NAME);
        Usuario user = new Usuario();
        try{
            Document filter = new Document("id", idUsuario);
            Document result = collection.find(filter).first();
            if (result != null) {

                user = mapearUsuario(result);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return user;
    }
    public List<Usuario> getAllUsers() throws ErrorConectionMongoException {
        MongoPool mongoPool = MongoPool.getInstance();
        var connection = mongoPool.getConnection();
        var collection = connection.getCollection(COLLECTION_NAME);
        List<Usuario> usuarios = new java.util.ArrayList<>();

        try {
            var resultSet = collection.find();

            while (resultSet.iterator().hasNext()) {
                Document result = resultSet.iterator().next();
                Usuario user = mapearUsuario(result);
                usuarios.add(user);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return usuarios;
    }

    public Usuario mapearUsuario(Document resultSet){
        Usuario user = new Usuario();
        Document rolDoc = (Document) resultSet.get("rol");
        Rol rol = new Rol();
        if (rolDoc != null) {
            Integer id = rolDoc.getInteger("idRol");
            rol.setId(id);
            rol.setNombre(rolDoc.getString("nombre"));
        }
        user.setId(resultSet.getInteger("id"));
        user.setNombre(resultSet.getString("nombre"));
        user.setMail(resultSet.getString("mail"));
        user.setContrasena(resultSet.getString("contrasena"));
        user.setEstado(resultSet.getString("estado"));
        user.setRol(rol.getNombre());
        user.setFechaRegistro(resultSet.getDate("fechaRegistro").toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
        return user;
    }
}