package repository.cassandra;

import connections.CassandraPool;
import entity.Medicion;
import entity.Sensor;

import java.util.UUID;

public class MedicionRepository {
    private static MedicionRepository instance;

    private MedicionRepository() {}

    public static MedicionRepository getInstance(){
        if (instance == null)
            instance = new MedicionRepository();
        return instance;
    }

    public void insertarMedicion(Medicion medicion){
        String cass = "INSERT INTO Mediciones (idMedicion,idSensor,cod,tipo,latitud,longitud,ciudad,pais,estado,fecha,temperatura,humedad) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?);";
        CassandraPool cassandraPool = CassandraPool.getInstance();
        var session = CassandraPool.getSession();
        UUID id = UUID.randomUUID();
        Sensor sensor = medicion.getSensor();
        medicion.setId(id);
        session.execute(cass,medicion.getId(),
                sensor.getIdSensor(),
                sensor.getCod(),
                sensor.getTipo(),
                sensor.getLatitud(),
                sensor.getLongitud(),
                sensor.getCiudad(),
                sensor.getPais(),
                sensor.getEstado(),
                medicion.getFecha(),
                medicion.getTemperatura(),
                medicion.getHumedad()
        );


    }
}