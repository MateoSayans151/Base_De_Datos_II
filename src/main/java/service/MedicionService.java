package service;

import entity.Medicion;
import org.springframework.stereotype.Service;
import repository.cassandra.MedicionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MedicionService {

    private final MedicionRepository repo;

    public MedicionService(MedicionRepository repo) {
        this.repo = repo;
    }

    // LISTAR TODAS LAS MEDICIONES
    public List<Medicion> getAll() {
        return repo.findAll();
    }

    // OBTENER MEDICIÓN POR ID
    public Optional<Medicion> getById(int id) {
        return repo.findById(id);
    }

    // CREAR MEDICIÓN
    public Medicion create(Medicion medicion) {
        return repo.save(medicion);
    }

    // ACTUALIZAR MEDICIÓN
    public Medicion update(int id, Medicion medicion) {
        medicion.setId(id);
        return repo.save(medicion);
    }

    // ELIMINAR MEDICIÓN
    public void delete(int id) {
        repo.deleteById(id);
    }

    // OBTENER MEDICIONES POR SENSOR
    public List<Medicion> getBySensorId(int sensorId) {
        return repo.findBySensorId(sensorId);
    }

    // OBTENER MEDICIONES EN UN RANGO DE FECHAS
    public List<Medicion> getByFechaBetween(LocalDateTime desde, LocalDateTime hasta) {
        return repo.findByFechaBetween(desde, hasta);
    }
}
