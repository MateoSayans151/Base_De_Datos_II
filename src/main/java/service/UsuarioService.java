package service;

import entity.Usuario;
import exceptions.ErrorConectionMongoException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import repository.mongo.UsuarioRepository;
import repository.redis.InicioSesionRepository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    public static UsuarioService instance;
    private final InicioSesionRepository inicioSesionRepository = InicioSesionRepository.getInstance();

    public UsuarioService() {

    }

    // Backwards-compatible helper used by UI code. Wraps checked exceptions
    public java.util.List<Usuario> getAllUsuarios() {
        try {
            return getAll();
        } catch (ErrorConectionMongoException e) {
            // If Mongo is not available, return empty list to keep UI responsive
            return new java.util.ArrayList<>();
        }
    }
    // LISTAR TODOS LOS USUARIOS
    public List<Usuario> getAll() throws ErrorConectionMongoException {
        return UsuarioRepository.getInstance().getAllUsers();
    }

    // OBTENER USUARIO POR ID
    public Usuario getUsuarioById(int id) {
        try {
            return UsuarioRepository.getInstance().getUserById(id);
        } catch (ErrorConectionMongoException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public Usuario getUsuarioByEmail(String email) {
        try {
            return UsuarioRepository.getInstance().getUserByMail(email);
        } catch (ErrorConectionMongoException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Backwards-compatible alias used by older code
    public Usuario getById(int id) {
        return getUsuarioById(id);
    }

    // CREAR NUEVO USUARIO
    public void create(Usuario usuario) throws ErrorConectionMongoException {
        usuario.setFechaRegistro(java.time.LocalDateTime.now());
        String passwordHashed = hashPassword(usuario.getContrasena());
        usuario.setContrasena(passwordHashed);
        UsuarioRepository.getInstance().createUser(usuario);
    }

    public Usuario getByMail(String mail) throws ErrorConectionMongoException {
        return UsuarioRepository.getInstance().getUserByMail(mail);
    }

    public String login(String email, String password) throws ErrorConectionMongoException {
        Usuario user = UsuarioRepository.getInstance().getUserByMail(email);
        if(user == null){
            return null;
        }
        String passwordHashed = hashPassword(password);
        if(!user.getContrasena().equals(passwordHashed)){
            return null;
        }
        String token = java.util.UUID.randomUUID().toString();
        inicioSesionRepository.crearSesion(token, user);
        return token;
    }
    public Usuario validateToken(String token) throws ErrorConectionMongoException {
        var session = inicioSesionRepository.getSesion(token);
        if (session == null) {
            return null;
        }

        int usuarioId = session.getInt("id");
        return UsuarioRepository.getInstance().getUserById(usuarioId);
    }
    public JSONObject getSession(String token){
        return inicioSesionRepository.getSesion(token);
    }
    public void logout(String token) {
        inicioSesionRepository.eliminarSesion(token);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

