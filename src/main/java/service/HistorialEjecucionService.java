package service;

import entity.HistorialEjecucion;
import exceptions.ErrorConectionMongoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import repository.mongo.HistorialEjecucionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HistorialEjecucionService {

    private final HistorialEjecucionRepository repo;

    public HistorialEjecucionService(HistorialEjecucionRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public HistorialEjecucion crear(HistorialEjecucion h) {
        try {
            if (h.getFechaEjecucion() == null)
                h.setFechaEjecucion(LocalDateTime.now());
            if (h.getEstado() == null)
                h.setEstado("activa");
            return repo.save(h);
        } catch (ErrorConectionMongoException e) {
            throw new RuntimeException("Error al crear HistorialEjecucion", e);
        }
    }

    public Optional<HistorialEjecucion> obtenerPorId(int id) {
        try {
            return repo.findById(id);
        } catch (ErrorConectionMongoException e) {
            throw new RuntimeException("Error al obtener HistorialEjecucion id=" + id, e);
        }
    }

    public List<HistorialEjecucion> listarTodos() {
        try {
            return repo.findAll();
        } catch (ErrorConectionMongoException e) {
            throw new RuntimeException("Error al listar HistorialEjecucion", e);
        }
    }

    @Transactional
    public void eliminar(int id) {
        try {
            repo.deleteById(id);
        } catch (ErrorConectionMongoException e) {
            throw new RuntimeException("Error al eliminar HistorialEjecucion id=" + id, e);
        }
    }

    public List<HistorialEjecucion> listarPorEstado(String estado) {
        try {
            return repo.findByEstado(estado);
        } catch (ErrorConectionMongoException e) {
            throw new RuntimeException("Error al listar HistorialEjecucion por estado", e);
        }
    }

    public List<HistorialEjecucion> listarPorSolicitud(String solicitudId) {
        try {
            return repo.findBySolicitud(solicitudId);
        } catch (ErrorConectionMongoException e) {
            throw new RuntimeException("Error al listar HistorialEjecucion por solicitud", e);
        }
    }

    @Transactional
    public HistorialEjecucion registrarResultado(String solicitudId, String resultado, String estado) {
        HistorialEjecucion h = new HistorialEjecucion();
        h.setFechaEjecucion(LocalDateTime.now());
        h.setResultado(resultado);
        h.setEstado(estado);
        return crear(h);
    }
}
