package service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import entity.HistorialEjecucion;
import entity.Proceso;
import entity.SolicitudProceso;
import entity.Usuario;
import exceptions.ErrorConectionMongoException; // <-- importa tu excepción
import jakarta.transaction.Transactional;
import repository.mongo.HistorialEjecucionRepository;
import repository.mongo.SolicitudProcesoRepository;

@Service
public class SolicitudProcesoService {

    private final SolicitudProcesoRepository repo;
    private final HistorialEjecucionRepository historialRepo;

    public SolicitudProcesoService(SolicitudProcesoRepository repo, HistorialEjecucionRepository historialRepo) {
        this.repo = repo;
        this.historialRepo = historialRepo;
    }

    /* ===========================
       CRUD BÁSICO
       =========================== */

    @Transactional
    public SolicitudProceso crearSolicitud(Usuario usuario, Proceso proceso, String estado) {
        if (usuario == null || proceso == null)
            throw new IllegalArgumentException("Usuario y Proceso no pueden ser nulos");

        try {
            SolicitudProceso solicitud = new SolicitudProceso();
            solicitud.setUsuario(usuario);
            solicitud.setProceso(proceso);
            solicitud.setFechaSolicitud(LocalDateTime.now());
            solicitud.setEstado(estado != null ? estado : "pendiente");
            return repo.save(solicitud);
        } catch (ErrorConectionMongoException e) {
            throw new RuntimeException("Mongo: error al crear SolicitudProceso", e);
        }
    }

    public List<SolicitudProceso> listarTodas() {
        try {
            return repo.findAll();
        } catch (ErrorConectionMongoException e) {
            throw new RuntimeException("Mongo: error al listar SolicitudProceso", e);
        }
    }

    public Optional<SolicitudProceso> obtenerPorId(int id) {
        try {
            return repo.findById(id);
        } catch (ErrorConectionMongoException e) {
            throw new RuntimeException("Mongo: error al obtener SolicitudProceso id=" + id, e);
        }
    }

    @Transactional
    public SolicitudProceso actualizarEstado(int id, String nuevoEstado) {
        try {
            SolicitudProceso solicitud = repo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con id " + id));
            solicitud.setEstado(nuevoEstado);
            return repo.save(solicitud);
        } catch (ErrorConectionMongoException e) {
            throw new RuntimeException("Mongo: error al actualizar estado de SolicitudProceso id=" + id, e);
        }
    }

    @Transactional
    public void eliminar(int id) {
        try {
            if (!repo.existsById(id))
                throw new RuntimeException("No existe solicitud con id " + id);
            repo.deleteById(id);
        } catch (ErrorConectionMongoException e) {
            throw new RuntimeException("Mongo: error al eliminar SolicitudProceso id=" + id, e);
        }
    }

    /* ===========================
       CONSULTAS PERSONALIZADAS
       =========================== */
       
    public List<SolicitudProceso> listarPorEstado(String estado) {
        try {
            return repo.findByEstadoIgnoreCase(estado);
        } catch (ErrorConectionMongoException e) {
            throw new RuntimeException("Mongo: error al listar por estado=" + estado, e);
        }
    }

    public List<SolicitudProceso> listarPorUsuario(int usuarioId) {
        try {
            return repo.findByUsuario_Id(usuarioId);
        } catch (ErrorConectionMongoException e) {
            throw new RuntimeException("Mongo: error al listar por usuarioId=" + usuarioId, e);
        }
    }

    public List<SolicitudProceso> listarPorUsuarioYEstado(int usuarioId, String estado) {
        try {
            return repo.findByUsuario_IdAndEstadoIgnoreCase(usuarioId, estado);
        } catch (ErrorConectionMongoException e) {
            throw new RuntimeException("Mongo: error al listar por usuarioId y estado", e);
        }
    }

    /* ===========================
       MARCAR COMO COMPLETADO
       =========================== */

    @Transactional
    public SolicitudProceso marcarComoCompletado(int id) {
        try {
            SolicitudProceso solicitud = repo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
            solicitud.setEstado("completado");
            SolicitudProceso saved = repo.save(solicitud);

            // Agregar entrada en historial de ejecucion (no provocar rollback si falla)
            try {
                HistorialEjecucion h = new HistorialEjecucion();
                h.setSolicitud(saved); // asume que HistorialEjecucion tiene referencia a SolicitudProceso
                h.setFechaEjecucion(LocalDateTime.now());
                h.setResultado("Solicitud completada");
                h.setEstado("completado");
                historialRepo.save(h);
            } catch (ErrorConectionMongoException ex) {
                // Registrar y continuar; si prefieres fallar la transacción, relanzar aquí
                System.err.println("No se pudo guardar HistorialEjecucion: " + ex.getMessage());
            }

            return saved;
        } catch (ErrorConectionMongoException e) {
            throw new RuntimeException("Mongo: error al marcar como completado id=" + id, e);
        }
    }

}

