package service;

import entity.Medicion;
import entity.Sensor;
import exceptions.ErrorConectionMongoException;
import repository.mongo.SensorRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SensorService {

    public static SensorService instance;


    public SensorService() {}

    public static SensorService getInstance() {
        if (instance == null)
            instance = new SensorService();
        return instance;
    }

    public void createSensor(String cod,String tipo,Double latitud, Double longitud, String ciudad, String pais) throws ErrorConectionMongoException {
        Sensor s = new Sensor(cod,tipo, latitud, longitud, ciudad, pais);
        LocalDateTime fechaIni = java.time.LocalDateTime.now();
        s.setFechaIni(fechaIni);
        int lastId = SensorRepository.getInstance().saveSensor(s);
        double temperatura = Math.round(ThreadLocalRandom.current().nextDouble(-20.0, 50.0) * 10.0) / 10.0;
        double humedad = Math.round(ThreadLocalRandom.current().nextDouble(0.0, 100.0) * 10.0) / 10.0;

        MedicionService medicionService = new MedicionService();
        s.setId(lastId);
        Medicion s1 = new Medicion();
        s1.setSensor(s);
        s1.setFecha(fechaIni);
        s1.setTemperatura(temperatura);
        s1.setHumedad(humedad);
        medicionService.create(s1);
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

    public void changeState(int idSensor) throws ErrorConectionMongoException {
        SensorRepository.getInstance().changeStateSensor(idSensor);
    }

    public Sensor getSensorByCode(String cod) throws ErrorConectionMongoException {
        return SensorRepository.getInstance().getSensorByCode(cod);
    }

}