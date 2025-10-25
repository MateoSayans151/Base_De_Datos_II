package controlador;

import modelo.Sensor;
import services.SensorService;

public class SensorController {
    
    public void createSensor (int id, String tipo, String ubicacion){
    }
    public Sensor getSensor(){
        SensorService.getSensor();
    }
    /*
    public void updateSensor(@PathVariable int id, @RequestBody Sensor sensor){
        return SensorService.updateSensor(id,sensor);
    }
    public void deleteSensor(@PathVariable int id){
        return SensorService.updateSensor(id);
    }

     */
}
