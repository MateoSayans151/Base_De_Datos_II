package service;

import entity.Factura;
import entity.Pago;
import entity.Proceso;
import entity.SolicitudProceso;
import repository.sql.PagosRepository;
import repository.mongo.FacturaRepository;
import exceptions.ErrorConectionMongoException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * FacturaService gestiona la creación y recuperación de facturas en MongoDB.
 * Cuando se aprueba una solicitud, se genera una factura que le aparecerá al cliente.
 */
public class FacturaService {
    private static FacturaService instance;
    private final PagosRepository pagosRepository;
    private final FacturaRepository facturaRepository;

    public FacturaService() {
        this.pagosRepository = PagosRepository.getInstance();
        this.facturaRepository = FacturaRepository.getInstance();
    }

    public static FacturaService getInstance() {
        if (instance == null) instance = new FacturaService();
        return instance;
    }

    public Factura crearFactura(Factura factura) {
        // Persiste la factura en MongoDB
        try {
            facturaRepository.createFactura(factura);
            return factura;
        } catch (ErrorConectionMongoException e) {
            throw new RuntimeException("Error al crear factura en MongoDB: " + e.getMessage(), e);
        }
    }

    public List<Pago> getPagosByUsuario(int usuarioId) {
        return pagosRepository.getPagosByUsuario(usuarioId);
    }

    public double getSaldoUsuario(int usuarioId) {
        // Paused: return zero balance while account functionality is disabled
        return 0.0;
    }

    public Pago registrarPago(Factura factura, String metodoPago) {
        // Build a Pago from the Factura using available Pago setters
        Pago pago = new Pago();
        pago.setUsuarioOrigen(factura.getUsuario());
        pago.setFechaPago(LocalDate.now());
        pago.setMontoPagado(factura.getTotal());
        pago.setMetodoPago(metodoPago);
        pago.setDescripcion(factura.getDescripcion());
        pago.setEstado("Completado");

        // Persist via stub repository (no-op)
        return pagosRepository.registrarPago(pago);
    }

    public Pago crearFacturaProceso(SolicitudProceso solicitud) {
        Factura factura = new Factura();
        factura.setUsuario(solicitud.getUsuario());
        // If Proceso has costo, try to use it; otherwise set 0
        try {
            Double costo = (Double) solicitud.getProceso().getClass().getMethod("getCosto").invoke(solicitud.getProceso());
            factura.setTotal(costo);
        } catch (Exception e) {
            factura.setTotal(0.0);
        }
        try {
            String nombre = (String) solicitud.getProceso().getClass().getMethod("getNombre").invoke(solicitud.getProceso());
            factura.setDescripcion("Proceso: " + nombre);
        } catch (Exception e) {
            factura.setDescripcion("Proceso");
        }
        factura.setEstado("Pendiente");
        factura.setTipo("Proceso");
        factura.setFechaEmision(LocalDate.now());

        Proceso procesos = new Proceso();
        factura.setProcesoFacturado(procesos);

        crearFactura(factura);
        return registrarPago(factura, "cuenta_corriente");
    }

    // Methods expected by UI
    public List<Factura> getFacturasByUsuario(int usuarioId) throws ErrorConectionMongoException {

            return facturaRepository.getFacturasByUsuario(usuarioId);
    }
    

    public void updateEstado(int facturaId, String estado) throws ErrorConectionMongoException {

            facturaRepository.updateEstado(facturaId, estado);

    }
}