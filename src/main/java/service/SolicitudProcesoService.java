package service;
import entity.Proceso;
import entity.SolicitudProceso;
import entity.Usuario;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import repository.mongo.SolicitudProcesoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SolicitudProcesoService {

    private final SolicitudProcesoRepository repo;

    public SolicitudProcesoService(SolicitudProcesoRepository repo) {
        this.repo = repo;
    }

    /* ===========================
       CRUD BÁSICO
       =========================== */

    // Crear nueva solicitud
    @Transactional
    public SolicitudProceso crearSolicitud(Usuario usuario, Proceso proceso, String estado) {
        if (usuario == null || proceso == null)
            throw new IllegalArgumentException("Usuario y Proceso no pueden ser nulos");

        SolicitudProceso solicitud = new SolicitudProceso();
        solicitud.setUsuario(usuario);
        solicitud.setProceso(proceso);
        solicitud.setFechaSolicitud(LocalDateTime.now());
        solicitud.setEstado(estado != null ? estado : "pendiente");

        return repo.save(solicitud);
    }

    // Obtener todas las solicitudes
    public List<SolicitudProceso> listarTodas() {
        return repo.findAll();
    }

    // Obtener por ID
    public Optional<SolicitudProceso> obtenerPorId(int id) {
        return repo.findById(id);
    }

    // Actualizar estado de una solicitud
    @Transactional
    public SolicitudProceso actualizarEstado(int id, String nuevoEstado) {
        SolicitudProceso solicitud = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con id " + id));
        solicitud.setEstado(nuevoEstado);
        return repo.save(solicitud);
    }

    // Eliminar una solicitud
    @Transactional
    public void eliminar(int id) {
        if (!repo.existsById(id))
            throw new RuntimeException("No existe solicitud con id " + id);
        repo.deleteById(id);
    }

    /* ===========================
       CONSULTAS PERSONALIZADAS
       =========================== */

    // Listar solicitudes por estado
    public List<SolicitudProceso> listarPorEstado(String estado) {
        return repo.findByEstadoIgnoreCase(estado);
    }

    // Listar solicitudes de un usuario
    public List<SolicitudProceso> listarPorUsuario(int usuarioId) {
        return repo.findByUsuario_Id(usuarioId);
    }

    // Listar solicitudes de un usuario y estado (pendiente/completado)
    public List<SolicitudProceso> listarPorUsuarioYEstado(int usuarioId, String estado) {
        return repo.findByUsuario_IdAndEstadoIgnoreCase(usuarioId, estado);
    }

    /* ===========================
       MARCAR COMO COMPLETADO
       =========================== */

    @Transactional
    public SolicitudProceso marcarComoCompletado(int id) {
        SolicitudProceso solicitud = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        solicitud.setEstado("completado");
        return repo.save(solicitud);
    }
}

