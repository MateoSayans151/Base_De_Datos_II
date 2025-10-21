package controlador;

import modelo.Sensor;
import services.SensorService;

public class SensorController {
    
    public void createSensor (Integer id, String tipo, String ubicacion){ 
    }
    public Sensor getSensor(){
        SensorService.getSensor()
    }
    public void updateSensor(@PathVariable Integer id, @RequestBody Sensor sensor){
        return SensorService.updateSensor(id,sensor)
    }
    public void deleteSensor(@PathVariable Integer id){
        return SensorService.updateSensor(id)
    }
}
