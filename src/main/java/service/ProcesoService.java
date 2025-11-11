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

import java.time.LocalDate;
import java.util.List;
import java.util.Collections;

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

    /*
    * Podremos suponer que marcos en la ventana de solicitudes marcos va a mostrar las solicitudes pendientes y el técnico va a seleccionar una para cambiarle el estado a rechazada o completada.
    * Entonces lo que podríamos hacer es que una vez que el tecnico sellecione la solicitud y le de a un boton de "completar solicitud" se llame a este método pasandole la solicitud seleccionada y el rol del tecnico.
    * */

    @Transactional
    public Factura ejectuarSolicitudYEmitirFactura(SolicitudProceso solicitud,String ubicacion) {

        MedicionService medicionService = MedicionService.getInstance();



        try {
            // obtener la última solicitud pendiente
            List<SolicitudProceso> pendientes = solicitudRepo.findByEstadoIgnoreCase("pendiente");
            if (pendientes == null || pendientes.isEmpty())
                throw new RuntimeException("No hay solicitudes pendientes");
            Proceso proceso = solicitud.getProceso();
            int idProceso = solicitud.getProceso().getId();

            if(idProceso == 1){
                Double min = medicionService.getMinByCity(ubicacion);
                Double max = medicionService.getMaxByCity(ubicacion);
                proceso.setDescripcion("Se obtuvieron las siguientes mediciones en la ciudad de " + ubicacion + ": \n Minima = " + min + " \n Maxima = " + max);


            }else if(idProceso == 2){
                Double min = medicionService.getMinByCountry(ubicacion);
                Double max = medicionService.getMaxByCountry(ubicacion);
                proceso.setDescripcion("Se obtuvieron las siguientes mediciones en el país de " + ubicacion + ": \n Minima = " + min + " \n Maxima = " + max);


            }else if(idProceso == 3){
                Double hum = medicionService.getAverageHumidityBetweenDatesByCity(ubicacion, LocalDate.now().minusYears(1).atStartOfDay(), LocalDate.now().atStartOfDay());
                Double temp = medicionService.getAverageTemperatureBetweenDatesByCity(ubicacion, LocalDate.now().minusYears(1).atStartOfDay(), LocalDate.now().atStartOfDay());
                Double humPer = Math.floor((hum *100)/100.0);
                Double tempPer = Math.floor(( temp*100)/ 100.0);
                proceso.setDescripcion("Se obtuvo la siguiente medicion promedio en la ciudad de " + ubicacion + ":\n Promedio Temperatura = " + tempPer +"%"+ " \n Promedio Humedad = " + humPer+"%");
                System.out.println("Temperatura país: " + temp);
                System.out.println("Humedad país: " + hum);
                System.out.println(idProceso);
                System.out.println(proceso.getDescripcion());
            } else if (idProceso == 4) {
                Double hum = medicionService.getAverageHumidityBetweenDatesByCountry(ubicacion, LocalDate.now().minusYears(1).atStartOfDay(), LocalDate.now().atStartOfDay());
                Double temp = medicionService.getAverageTemperatureBetweenDatesByCountry(ubicacion, LocalDate.now().minusYears(1).atStartOfDay(), LocalDate.now().atStartOfDay());
                System.out.println("Temperatura país: " + temp);
                System.out.println("Humedad país: " + hum);
                Double humPer = Math.floor((hum *100)/100.0);
                Double tempPer = Math.floor(( temp*100)/ 100.0);
                proceso.setDescripcion("Se obtuvo la siguiente medicion promedio en el país de " + ubicacion + ":\n Promedio Temperatura = " + tempPer+"%" + " \n Promedio Humedad = " + humPer+"%");
                System.out.println(idProceso);
                System.out.println(proceso.getDescripcion());
            } else{
                throw new RuntimeException("El id del proceso no es valido");
            }




            // actualizar la solicitud a completado y guardarla
            solicitud.setEstado("Aprobado");
            SolicitudProceso saved = solicitudRepo.save(solicitud);
            System.out.println("Solicitud guardada correctamente" + saved.getProceso());
            // crear la factura para el usuario que hizo la solicitud
            Double total = (proceso != null) ? proceso.getCosto() : 0.0;
            Factura factura = new Factura(
                    saved.getUsuario(),
                    LocalDate.now(),
                    "Pendiente",
                    proceso,
                    total, ubicacion
            );

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
       OBTENER TODOS LOS PROCESOS
       =========================== */
    public List<Proceso> obtenerTodosLosProcesos() throws ErrorConectionMongoException {
        return repo.obtenerTodosLosProcesos();
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
