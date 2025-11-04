package service;

import entity.Usuario;
import exceptions.ErrorConectionMongoException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import repository.mongo.UsuarioRepository;
import repository.redis.InicioSesionRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    public static UsuarioService instance;
    private final InicioSesionRepository inicioSesionRepository;

    public UsuarioService(InicioSesionRepository inicioSesionRepository) {
        this.inicioSesionRepository = inicioSesionRepository;
    }

    // LISTAR TODOS LOS USUARIOS
    public List<Usuario> getAll() throws ErrorConectionMongoException {
        return UsuarioRepository.getInstance().getAllUsers();
    }

    // OBTENER USUARIO POR ID
    public Usuario getById(int id) throws ErrorConectionMongoException {
        return UsuarioRepository.getInstance().getUserById(id);
    }

    // CREAR NUEVO USUARIO
    public void create(Usuario usuario) throws ErrorConectionMongoException {
        UsuarioRepository.getInstance().createUser(usuario);
    }

    public Usuario getByMail(String mail) throws ErrorConectionMongoException {
        return UsuarioRepository.getInstance().getUserByMail(mail);
    }

    public void Login(String token, Usuario usuario){
        inicioSesionRepository.crearSesion(token, usuario);
    }
    public JSONObject getSesion(String token){
        return inicioSesionRepository.getSesion(token);
    }
    public void eliminarSesion(String token) {
        inicioSesionRepository.eliminarSesion(token);
    }

}

