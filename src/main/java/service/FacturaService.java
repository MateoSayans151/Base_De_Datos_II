package service;

import entity.Factura;
import entity.Proceso;
import exceptions.ErrorConectionMongoException;
import repository.mongo.FacturaRepository;
import java.util.List;
import java.util.ArrayList;

public class FacturaService {
    private static FacturaService instance;
    private final FacturaRepository facturaRepository;

    public FacturaService() {
        this.facturaRepository = FacturaRepository.getInstance();
    }

    public static FacturaService getInstance() {
        if (instance == null) {
            instance = new FacturaService();
        }
        return instance;
    }

    public void createFactura(Factura factura) throws ErrorConectionMongoException {
        if (factura == null) {
            throw new IllegalArgumentException("Factura cannot be null");
        }
        facturaRepository.createFactura(factura);
    }

    public List<Factura> getFacturasByUsuario(int usuarioId) throws ErrorConectionMongoException {
        return facturaRepository.getFacturasByUsuario(usuarioId);
    }

    public List<Factura> getFacturasPendientes() throws ErrorConectionMongoException {
        return facturaRepository.getFacturasByEstado("Pendiente");
    }

    public void updateEstado(int facturaId, String nuevoEstado) throws ErrorConectionMongoException {
        facturaRepository.updateEstado(facturaId, nuevoEstado);
    }
}