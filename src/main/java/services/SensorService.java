package services;

import modelo.Sensor;
import repositories.ObjectRepository;

public class SensorService {

    private static SensorService instance;

    private SensorService() {}

    public static SensorService getInstance() {
        if (instance == null)
            instance = new SensorService();
        return instance;
    }

    public void createSensor(String tipo, String ubicacion) {
        Sensor s = new Sensor(tipo, ubicacion);
        ObjectRepository.getInstance().guardarSensor(s);
        System.out.println("Id " + s.getId());
    }
    public Sensor getSensor(){
        SensorRepository.getSensor();
    }
    /*
    public void updateSensor(){
        
    }

    public void deleteSensor(String nombre)
    */

}