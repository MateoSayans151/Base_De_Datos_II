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

    private static SolicitudProcesoService instance;
    private final SolicitudProcesoRepository repo;
    private final HistorialEjecucionRepository historialRepo;

    public SolicitudProcesoService(SolicitudProcesoRepository repo, HistorialEjecucionRepository historialRepo) {
        this.repo = repo;
        this.historialRepo = historialRepo;
    }

    public static SolicitudProcesoService getInstance() {
        if (instance == null) {
            instance = new SolicitudProcesoService(SolicitudProcesoRepository.getInstance(), HistorialEjecucionRepository.getInstance());
        }
        return instance;
    }

    public void crearSolicitudProceso(Usuario usuario, Proceso proceso) throws ErrorConectionMongoException {
        crearSolicitudProceso(usuario, proceso, null, null);
    }

    /**
     * Crear solicitud indicando opcionalmente ciudad y/o país.
     * Al menos uno puede ser null/empty; validación de UI se debe hacer en la capa de presentación.
     */
    public void crearSolicitudProceso(Usuario usuario, Proceso proceso, String ciudad, String pais) throws ErrorConectionMongoException {
        if (usuario == null || proceso == null) {
            throw new IllegalArgumentException("Usuario y Proceso son requeridos");
        }

        SolicitudProceso solicitud = new SolicitudProceso(
            usuario,
            proceso,
            LocalDateTime.now(),
            "Pendiente",  // Estado inicial, con P mayúscula según el schema
            ciudad,
            pais
        );
        repo.save(solicitud);
    }

    /* ===========================
       GESTIÓN DE SOLICITUDES
       =========================== */

    @Transactional
    public List<SolicitudProceso> obtenerSolicitudesPendientes() throws ErrorConectionMongoException {
        return repo.findByEstadoIgnoreCase("Pendiente");
    }

    @Transactional
    public SolicitudProceso crearSolicitud(Usuario usuario, Proceso proceso, String estado) {
        if (usuario == null || proceso == null)
            throw new IllegalArgumentException("Usuario y Proceso no pueden ser nulos");

        try {
            SolicitudProceso solicitud = new SolicitudProceso();
            solicitud.setUsuario(usuario);
            solicitud.setProceso(proceso);
            solicitud.setFechaSolicitud(LocalDateTime.now());
            solicitud.setEstado(estado != null ? estado : "Pendiente"); // Estado inicial con P mayúscula
            return repo.save(solicitud);
        } catch (ErrorConectionMongoException e) {
            throw new RuntimeException("Mongo: error al crear SolicitudProceso", e);
        }
    }

    public List<SolicitudProceso> obtenerSolicitudesUsuario(int usuarioId) throws ErrorConectionMongoException {
        return repo.findByUsuario_Id(usuarioId);
    }

    public void aprobarSolicitud(int solicitudId, Usuario tecnico) throws ErrorConectionMongoException {
        Optional<SolicitudProceso> solicitudOpt = repo.findById(solicitudId);
        if (!solicitudOpt.isPresent()) {
            throw new IllegalArgumentException("Solicitud no encontrada: " + solicitudId);
        }

        if (!tecnico.getRol().getNombre().equalsIgnoreCase("TECNICO")) {
            throw new IllegalArgumentException("Solo los técnicos pueden aprobar solicitudes");
        }

        SolicitudProceso solicitud = solicitudOpt.get();
        if (!"Pendiente".equals(solicitud.getEstado())) {
            throw new IllegalStateException("Solo se pueden aprobar solicitudes pendientes");
        }

        solicitud.setEstado("Aprobado");
        repo.save(solicitud);
    }

    public void rechazarSolicitud(int solicitudId, Usuario tecnico) throws ErrorConectionMongoException {
        Optional<SolicitudProceso> solicitudOpt = repo.findById(solicitudId);
        if (!solicitudOpt.isPresent()) {
            throw new IllegalArgumentException("Solicitud no encontrada: " + solicitudId);
        }

        if (!tecnico.getRol().getNombre().equalsIgnoreCase("TECNICO")) {
            throw new IllegalArgumentException("Solo los técnicos pueden rechazar solicitudes");
        }

        SolicitudProceso solicitud = solicitudOpt.get();
        if (!"Pendiente".equals(solicitud.getEstado())) {
            throw new IllegalStateException("Solo se pueden rechazar solicitudes pendientes");
        }

        solicitud.setEstado("Rechazado");
        repo.save(solicitud);
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

