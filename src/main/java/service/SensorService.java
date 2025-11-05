package service;

import entity.Sensor;
import exceptions.ErrorConectionMongoException;
import repository.mongo.SensorRepository;

import java.time.LocalDateTime;
import java.util.List;

public class SensorService {

    public static SensorService instance;


    public SensorService() {}

    public static SensorService getInstance() {
        if (instance == null)
            instance = new SensorService();
        return instance;
    }

    public void createSensor(String cod,String tipo,Double latitud, Double longitud, String ciudad, String pais,LocalDateTime fechaIni) throws ErrorConectionMongoException {
        Sensor s = new Sensor(cod,tipo, latitud, longitud, ciudad, pais,fechaIni);
        SensorRepository.getInstance().saveSensor(s);
    }
    public Sensor getSensor(int idSensor) throws ErrorConectionMongoException {
        return SensorRepository.getInstance().getSensor(idSensor);
    }

    public List<Sensor> getSensorsByCity(String city) throws ErrorConectionMongoException {
        return SensorRepository.getInstance().getSensorsByCity(city);
    }
    public List<Sensor> getSensorsByState(String state) throws ErrorConectionMongoException {
        return SensorRepository.getInstance().getSensorsByState(state);
    }

    public List<Sensor> getSensorsByCountry(String country) throws ErrorConectionMongoException {
        return SensorRepository.getInstance().getSensorsByCountry(country);
    }
    public List<Sensor> getAllSensors() throws ErrorConectionMongoException {
        return SensorRepository.getInstance().getAllSensors();
    }



}