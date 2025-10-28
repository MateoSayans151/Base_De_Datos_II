package controller;

import org.springframework.web.bind.annotation.*;

import entity.Sensor;
//import repository.RegistroSensorRepository;

import java.util.List;

@RestController
@RequestMapping("/api/registros-sensor")
public class RegistroSensorController {
/*
    private final RegistroSensorRepository repo;

    public RegistroSensorController(RegistroSensorRepository repo) {
        this.repo = repo;
    }

    // LISTAR TODOS LOS REGISTROS
    @GetMapping
    public List<RegistroSensor> getAll() {
        return repo.findAll();
    }

    // OBTENER REGISTRO POR ID
    @GetMapping("/{id}")
    public RegistroSensor getById(@PathVariable int id) {
        return repo.findById(id).orElse(null);
    }

    // CREAR NUEVO REGISTRO
    @PostMapping
    public RegistroSensor create(@RequestBody RegistroSensor body) {
        return repo.save(body);
    }

    // ACTUALIZAR REGISTRO EXISTENTE
    @PutMapping("/{id}")
    public RegistroSensor update(@PathVariable int id, @RequestBody RegistroSensor body) {
        body.setId(id);
        return repo.save(body);
    }

    // ELIMINAR REGISTRO
    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        repo.deleteById(id);
    }

 */
}
