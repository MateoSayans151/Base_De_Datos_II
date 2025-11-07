package service;

import entity.Alerta;
import repository.cassandra.AlertaRepository;

import java.time.LocalDateTime;
import java.util.List;

public class AlertaService {

    private static AlertaService instance;
    private final AlertaRepository alertaRepository = AlertaRepository.getInstance();

    public AlertaService() {}

    public static AlertaService getInstance() {
        if (instance == null) {
            instance = new AlertaService();
        }
        return instance;
    }

    // Obtener alerta por id (id as string because repository uses string id)
    public Alerta getById(String id) {
        return alertaRepository.obtenerAlerta(id);
    }

    // Crear nueva alerta: si no tiene fecha, asigna ahora y delega al repositorio
    public void create(Alerta alerta) {
        if (alerta == null) return;
        if (alerta.getFecha() == null) {
            alerta.setFecha(LocalDateTime.now());
        }
        alertaRepository.crearAlerta(alerta);
    }

    // Eliminar alerta por id
    public void delete(int id) {
        alertaRepository.eliminarAlerta(id);
    }

    public List<Alerta> checkAlerts() {
        return alertaRepository.checkAlerts();
    }
}