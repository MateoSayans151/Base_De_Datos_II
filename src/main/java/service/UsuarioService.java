package service;

import entity.Usuario;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import repository.mongo.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository repo;

    public UsuarioService(UsuarioRepository repo) {
        this.repo = repo;
    }

    // LISTAR TODOS LOS USUARIOS
    public List<Usuario> getAll() {
        return repo.obtenerTodosUsuarios();
    }

    // OBTENER USUARIO POR ID
    public Optional<Usuario> getById(int id) {
        return repo.findById(id);
    }

    // CREAR NUEVO USUARIO
    public Usuario create(Usuario usuario) {
        return repo.save(usuario);
    }

    // ACTUALIZAR USUARIO EXISTENTE
    public Usuario update(int id, Usuario usuario) {
        usuario.setId(id);
        return repo.save(usuario);
    }

    // ELIMINAR USUARIO
    public void delete(int id) {
        repo.deleteById(id);
    }
}

