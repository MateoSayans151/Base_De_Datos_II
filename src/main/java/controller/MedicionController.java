package controller;

import entity.Medicion;
import org.springframework.web.bind.annotation.*;
//import repositorio.MedicionRepository;

import java.util.List;

@RestController
@RequestMapping("/api/mediciones")
public class MedicionController {
    /*
    private final MedicionRepository repo;

    public MedicionController(MedicionRepository repo) {
        this.repo = repo;
    }

    // LISTAR TODAS LAS MEDICIONES
    @GetMapping
    public List<Medicion> getAll() {
        return repo.findAll();
    }

    // OBTENER UNA MEDICIÓN POR ID
    @GetMapping("/{id}")
    public Medicion getById(@PathVariable int id) {
        return repo.findById(id).orElse(null);
    }

    // CREAR UNA NUEVA MEDICIÓN
    @PostMapping
    public Medicion create(@RequestBody Medicion body) {
        return repo.save(body);
    }

    // ACTUALIZAR UNA MEDICIÓN EXISTENTE
    @PutMapping("/{id}")
    public Medicion update(@PathVariable int id, @RequestBody Medicion body) {
        body.setId(id);
        return repo.save(body);
    }

    // ELIMINAR UNA MEDICIÓN
    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        repo.deleteById(id);
    }

     */
}
