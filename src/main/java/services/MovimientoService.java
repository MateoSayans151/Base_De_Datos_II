package services;

import modelo.Movimiento;
import repositories.ObjectRepository;

public class MovimientoService {

    private static MovimientoService instance;

    private MovimientoService() {}

    public static MovimientoService getInstance() {
        if (instance == null)
            instance = new MovimientoService();
        return instance;
    }

    public void crearMovimiento(String descripcion, Double monto) {
        Movimiento m = new Movimiento(descripcion, monto);
        ObjectRepository.getInstance().guardarMovimiento(m);
        System.out.println("Id " + m.getId());
    }
}