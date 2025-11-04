package repository.cassandra;

import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import connections.CassandraPool;
import entity.Medicion;
import entity.Sensor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MedicionRepository {
    private static MedicionRepository instance;

    private MedicionRepository() {}

    public static MedicionRepository getInstance(){
        if (instance == null)
            instance = new MedicionRepository();
        return instance;
    }

    public void insertMeasurement(Medicion medicion){
        String cass = "INSERT INTO Mediciones (id,idSensor,cod,tipo,latitud,longitud,ciudad,pais,estado,fecha,temperatura,humedad) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?);";
        CassandraPool cassandraPool = CassandraPool.getInstance();
        var session = cassandraPool.getSession();
        UUID id = UUID.randomUUID();
        Sensor sensor = medicion.getSensor();
        medicion.setId(id);
        session.execute(cass,medicion.getId(),
                sensor.getId(),
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
    public Medicion getMeasurement(String idMedicion){
        String cass = "SELECT * FROM mediciones WHERE id = ?;";
        CassandraPool cassandraPool = CassandraPool.getInstance();
        var session = cassandraPool.getSession();
        UUID id = UUID.fromString(idMedicion);
        Row row = session.execute(cass,id).one();
        Medicion medicion = mappearMedicion(row);
        return medicion;
    }
    public List<Medicion> getMeasurementBySensor(int idSensor){
        String cass = "SELECT * FROM mediciones WHERE idSensor = ?;";
        CassandraPool cassandraPool = CassandraPool.getInstance();
        var session = cassandraPool.getSession();
        var resultSet= session.execute(cass,idSensor);
        List<Medicion> medicions = new ArrayList<>();

        for (Row row : resultSet){
            Medicion medicion = mappearMedicion(row);
            medicions.add(medicion);
        }

        return medicions;
    }
    public ArrayList<Medicion> getMeasurements(){
        String cass = "SELECT * FROM Mediciones ;";
        CassandraPool cassandraPool = CassandraPool.getInstance();
        var session = cassandraPool.getSession();
        var resultSet = session.execute(cass);
        ArrayList<Medicion> mediciones = new ArrayList<>();
        for (Row row : resultSet) {
            Medicion medicion = mappearMedicion(row);
            mediciones.add(medicion);
        }
        return mediciones;

    }

    public List<Medicion> getMeasurementsBetwenDates(LocalDateTime fechaInicio, LocalDateTime fechaFin){
        String cass = "SELECT * FROM Mediciones WHERE fechayHora >= ? AND fechayHora <= ? ;";
        CassandraPool cassandraPool = CassandraPool.getInstance();
        var session = cassandraPool.getSession();
        var resultSet = session.execute(cass,fechaInicio,fechaFin);
        List<Medicion> mediciones = new java.util.ArrayList<>();
        for (Row row : resultSet) {
            Medicion medicion = mappearMedicion(row);
            mediciones.add(medicion);
        }
        return mediciones;
    }

    public Medicion mappearMedicion(Row row){
        Medicion medicion = new Medicion();
        if (row != null) {
            medicion.setSensor(new Sensor());
            medicion.setId(row.getUuid("id")) ;
            Instant date = row.getInstant("fechayHora");
            medicion.setFecha(LocalDateTime.ofInstant(date, ZoneId.systemDefault()));
            medicion.setTemperatura(row.getDouble("temperatura")) ;
            medicion.setHumedad(row.getDouble("humedad"));
            medicion.getSensor().setCod(row.getString("cod"));
            medicion.getSensor().setTipo( row.getString("tipo"));
            medicion.getSensor().setLatitud(row.getDouble("latitud"));
            medicion.getSensor().setLongitud(row.getDouble("longitud"));
            medicion.getSensor().setCiudad(row.getString("ciudad"));
            medicion.getSensor().setPais(row.getString("pais"));
            medicion.getSensor().setEstado(row.getString("estado"));
            medicion.getSensor().setId(row.getInt("idSensor"));

        }else{
            medicion = null;
        }
        return medicion;
    }

}