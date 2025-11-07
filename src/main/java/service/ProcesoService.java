package service;

import entity.Proceso;
import exceptions.ErrorConectionMongoException;
import org.springframework.stereotype.Service;
import repository.mongo.ProcesoRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProcesoService {

    private final ProcesoRepository repo = ProcesoRepository.getInstance();

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
