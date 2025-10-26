package service;

import entity.Movimiento;
import repository.ObjectRepository;

public class MedicionService {

    private static MedicionService instance;

    private MedicionService() {}

    public static MedicionService getInstance() {
        if (instance == null)
            instance = new MedicionService();
        return instance;
    }

    public void crearMovimiento(String descripcion, Double monto) {
        Movimiento m = new Movimiento(descripcion, monto);
        ObjectRepository.getInstance().guardarMovimiento(m);
        System.out.println("Id " + m.getId());
    }
}