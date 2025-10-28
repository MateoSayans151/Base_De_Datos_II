package service;

import entity.Usuario;
import org.springframework.stereotype.Service;
import repository.sql.UsuarioRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository repo;

    public UsuarioService(UsuarioRepository repo) {
        this.repo = repo;
    }

    // LISTAR TODOS LOS USUARIOS
    public List<Usuario> getAll() {
        return repo.findAll();
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

