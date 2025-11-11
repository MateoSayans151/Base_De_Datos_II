package service;

import entity.Alerta;
import entity.Medicion;
import exceptions.ErrorConectionMongoException;
import repository.cassandra.AlertaRepository;

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

    // Crear alertas para todas las mediciones fuera de rango de todos los sensores
    public void createAllAlerts() throws ErrorConectionMongoException {

        MedicionService medicionService = MedicionService.getInstance();
        List<Medicion> meditions = medicionService.getAll();
        for(Medicion medition : meditions){
            if(medition.getTemperatura() < 0 || medition.getTemperatura() > 35){
                Alerta alerta = new Alerta();
                alerta.setSensor(medition.getSensor());
                alerta.setDescripcion("Alerta de temperatura: " + medition.getTemperatura() + "°C");
                alerta.setEstado("Sensor");
                alerta.setFecha(medition.getFecha());
                alertaRepository.crearAlerta(alerta);
            }
        }

    }
    public void deleteSensorAlerts() {
        alertaRepository.deleteSensorAlerts();
    }

    public List<Alerta> checkAlerts() {
        return alertaRepository.checkAlerts();
    }
}