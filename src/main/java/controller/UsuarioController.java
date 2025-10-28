package controller;

import entity.Usuario;
import org.springframework.web.bind.annotation.*;
//import repositorio.UsuarioRepository;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    /*
    private final UsuarioRepository repo;

    public UsuarioController(UsuarioRepository repo) {
        this.repo = repo;
    }

    // LISTAR TODOS LOS USUARIOS
    @GetMapping
    public List<Usuario> getAll() {
        return repo.findAll();
    }

    // OBTENER USUARIO POR ID
    @GetMapping("/{id}")
    public Usuario getById(@PathVariable int id) {
        return repo.findById(id).orElse(null);
    }

    // CREAR NUEVO USUARIO
    @PostMapping
    public Usuario create(@RequestBody Usuario body) {
        return repo.save(body);
    }

    // ACTUALIZAR USUARIO EXISTENTE
    @PutMapping("/{id}")
    public Usuario update(@PathVariable int id, @RequestBody Usuario body) {
        body.setId(id);
        return repo.save(body);
    }

    // ELIMINAR USUARIO
    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        repo.deleteById(id);
    }

     */
}
