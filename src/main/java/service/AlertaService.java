package service;

import entity.Alerta;
import entity.Medicion;
import entity.Sensor;
import exceptions.ErrorConectionMongoException;
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

    // Crear alertas para todas las mediciones fuera de rango de todos los sensores
    public void createAllAlerts() throws ErrorConectionMongoException {
        SensorService sensorService = SensorService.getInstance();
        MedicionService medicionService = MedicionService.getInstance();
        List<Sensor> sensors = sensorService.getAllSensors();
        for (Sensor sensor : sensors) {
            List<Medicion> meditions = medicionService.getBySensorId(sensor.getId());
            for(Medicion medition : meditions){
                if(medition.getTemperatura() < 0 || medition.getTemperatura() > 35){
                    Alerta alerta = new Alerta();
                    alerta.setSensor(sensor);
                    alerta.setDescripcion("Alerta de temperatura: " + medition.getTemperatura() + "°C");
                    alerta.setEstado("ACTIVA");
                    alerta.setFecha(medition.getFecha());
                    create(alerta);
                }
            }
        }
    }

    public List<Alerta> checkAlerts() {
        return alertaRepository.checkAlerts();
    }
}