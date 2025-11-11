package service;

import entity.ControlFuncionamiento;
import entity.Sensor;
import exceptions.ErrorConectionMongoException;
import repository.mongo.ControlFuncionamientoRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ControlFuncionamientoService {

    private static ControlFuncionamientoService instance;
    private final ControlFuncionamientoRepository repository = ControlFuncionamientoRepository.getInstance();

    private ControlFuncionamientoService() {}

    public static ControlFuncionamientoService getInstance() {
        if (instance == null) {
            instance = new ControlFuncionamientoService();
        }
        return instance;
    }

    public void create(ControlFuncionamiento control) throws ErrorConectionMongoException {
        if (control == null) return;
        if (control.getFechaControl() == null) {
            control.setFechaControl(LocalDate.now());
        }
        repository.saveControl(control);
    }

    public void update(ControlFuncionamiento control) throws ErrorConectionMongoException {
        SensorService sensorService = SensorService.getInstance();
        List<Sensor> sensors = sensorService.getAllSensors();
        for (Sensor sensor : sensors) {
            if (sensor.getId() == control.getSensor().getId()) {
                if(control.getEstado().equals("fallo")) {
                    Sensor updatedSensor = sensorService.getSensor(sensor.getId());
                    updatedSensor.setEstado(sensor.getEstado());
                    if(sensor.getEstado().equals("activo")) {
                        control.setEstado("funcionando");
                    }
                        repository.updateControl(control);

                }else{
                    if(control.getEstado().equals("funcionando") && sensor.getEstado().equals("inactivo")){
                        Sensor updatedSensor = sensorService.getSensor(sensor.getId());
                        updatedSensor.setEstado("activo");
                        repository.updateControl(control);
                    }
                }
            }

        }


    }
    public void createControls() throws ErrorConectionMongoException {
        SensorService sensorService = SensorService.getInstance();
        List<Sensor> sensors = sensorService.getSensorsByState("inactivo");
        int lastId = 0;
        try {
            for (Sensor sensor : sensors) {
                ControlFuncionamiento control = new ControlFuncionamiento();
                control.setSensor(sensor);
                control.setId(lastId);
                control.setFechaControl(LocalDate.now());
                control.setObservaciones("Control automatico de sensor inactivo");
                if(sensor.getEstado().equals("activo")){
                    control.setEstado("funcionando");
                }else{
                    control.setEstado("fallo");
                }
                create(control);
                lastId = control.getId() + 1;
            }
        } catch (ErrorConectionMongoException e) {
            throw new RuntimeException(e);
        }
    }

    public ControlFuncionamiento getById(int id) throws ErrorConectionMongoException {
        List<ControlFuncionamiento> all = repository.getAllControls();
        if (all == null) return null;
        for (ControlFuncionamiento c : all) {
            if (c != null && c.getId() == id) return c;
        }
        return null;
    }

    public List<ControlFuncionamiento> getAll() throws ErrorConectionMongoException {
        return repository.getAllControls();
    }

    public List<ControlFuncionamiento> getBySensorId(int sensorId) throws ErrorConectionMongoException {
        List<ControlFuncionamiento> result = new ArrayList<>();
        List<ControlFuncionamiento> all = repository.getAllControls();
        if (all == null) return result;
        for (ControlFuncionamiento c : all) {
            if (c != null && c.getSensor() != null && c.getSensor().getId() == sensorId) {
                result.add(c);
            }
        }
        return result;
    }

    public void deleteAll() throws ErrorConectionMongoException {
        repository.deleteAllControls();
    }

}