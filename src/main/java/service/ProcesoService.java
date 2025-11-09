package service;

import entity.Factura;
import entity.Medicion;
import entity.Proceso;
import entity.SolicitudProceso;
import entity.Usuario;
import exceptions.ErrorConectionMongoException;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import repository.cassandra.MedicionRepository;
import repository.mongo.ProcesoRepository;
import repository.mongo.SolicitudProcesoRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Collections;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ProcesoService {

    private final ProcesoRepository repo = ProcesoRepository.getInstance();
    private final SolicitudProcesoRepository solicitudRepo = SolicitudProcesoRepository.getInstance();

    /* ===========================
       VALIDACIONES
       =========================== */
    private void validar(Proceso p) {
        if (p == null) throw new IllegalArgumentException("El Proceso no puede ser nulo");
        if (p.getNombre() == null || p.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre del proceso es obligatorio");
        if (p.getTipo() == null || p.getTipo().isBlank())
            throw new IllegalArgumentException("El tipo de proceso es obligatorio");
        // Si usás BigDecimal:
        if (p.getCosto() < 0)
            throw new IllegalArgumentException("El costo no puede ser negativo");
        // Si usás Double:
        // if (p.getCosto() != null && p.getCosto() < 0) throw new IllegalArgumentException("El costo no puede ser negativo");
    }

    /* ===========================
       CREAR
       =========================== */
    public Proceso crear(Proceso proceso) throws ErrorConectionMongoException {
        validar(proceso);
        repo.crearProceso(proceso);
        return proceso;
    }
    @Transactional
    public Factura asignarUltimaSolicitudYEmitirFactura(Usuario tecnico, Medicion medicion) {
        if (tecnico == null) throw new IllegalArgumentException("Usuario tecnico no puede ser nulo");
        // validar rol 'tecnico' usando getRol() (admite entity.Rol o String)
        boolean esTecnico = false;
        try {
            Object rolObj = tecnico.getRol();
            if (rolObj != null) {
                if (rolObj instanceof String) {
                    esTecnico = "tecnico".equalsIgnoreCase((String) rolObj);
                } else if (rolObj instanceof entity.Rol) {
                    entity.Rol r = (entity.Rol) rolObj;
                    esTecnico = r.getNombre() != null && r.getNombre().equalsIgnoreCase("tecnico");
                }
            }
        } catch (Throwable t) { /* ignorar errores de reflexión */ }

        if (!esTecnico) throw new SecurityException("Solo usuarios con rol 'tecnico' pueden asignar mediciones");

        try {
            // obtener la última solicitud pendiente
            List<SolicitudProceso> pendientes = solicitudRepo.findByEstadoIgnoreCase("pendiente");
            if (pendientes == null || pendientes.isEmpty())
                throw new RuntimeException("No hay solicitudes pendientes");

            SolicitudProceso solicitud = pendientes.get(pendientes.size() - 1);

            // insertar la medición en Cassandra (se asume que 'medicion' ya viene con sensor/fecha adecuados)
            MedicionRepository medRepo = MedicionRepository.getInstance();
            medRepo.insertMeasurement(medicion);

            // actualizar la solicitud a completado y guardarla
            solicitud.setEstado("completado");
            SolicitudProceso saved = solicitudRepo.save(solicitud);

            // crear la factura para el usuario que hizo la solicitud
            Proceso proceso = saved.getProceso();
            Double total = (proceso != null) ? proceso.getCosto() : 0.0;
            Factura factura = new Factura(
                    saved.getUsuario(),
                    LocalDate.now(),
                    "pendiente", // estado inicial de la factura
                    (proceso != null) ? Collections.singletonList(proceso) : Collections.emptyList(),
                    total
            );

            // TODO: persistir factura si tenés un repositorio/DAO de Factura (p. ej. facturaRepo.save(factura))
            return factura;
        } catch (ErrorConectionMongoException e) {
            throw new RuntimeException("Mongo: error al procesar solicitud/factura", e);
        } catch (Exception e) {
            throw new RuntimeException("Error al asignar medición y emitir factura: " + e.getMessage(), e);
        }
    }

    /* ===========================
       OBTENER POR ID
       =========================== */
    public Proceso obtenerPorId(int id) throws ErrorConectionMongoException {
        // Requiere que ajustes el repo a int (ver fix #1)
        Proceso p = repo.obtenerProceso(id);
        if (p == null) throw new RuntimeException("Proceso no encontrado con id " + id);
        return p;
    }

    /** Útil para SolicitudProcesoService */
    public Proceso obtenerOError(int id) throws ErrorConectionMongoException {
        return obtenerPorId(id);
    }

    /* ===========================
       LISTAR POR TIPO
       =========================== */
    public List<Proceso> listarPorTipo(String tipo) throws ErrorConectionMongoException {
        if (tipo == null || tipo.isBlank())
            throw new IllegalArgumentException("El tipo no puede ser vacío");
        return repo.obtenerProcesosPorTipo(tipo);
    }

    /* ===========================
       (Opcional) Helpers de negocio
       =========================== */
    public boolean existe(int id) throws ErrorConectionMongoException {
        try {
            return repo.obtenerProceso(id) != null;
        } catch (ErrorConectionMongoException e) {
            throw e;
        }
    }
}
