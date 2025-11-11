package repository.cassandra;

import com.datastax.oss.driver.api.core.cql.Row;
import connections.CassandraPool;
import entity.Alerta;
import entity.Sensor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AlertaRepository {
    private static AlertaRepository instance;

    private AlertaRepository() {}

    public static AlertaRepository getInstance(){
        if (instance == null)
            instance = new AlertaRepository();
        return instance;
    }

    public void crearAlerta(Alerta alerta){

        String cass = "INSERT INTO Alertas (id,idSensor,cod,tipo,latitud,longitud,ciudad,pais,estado,fechayHora,descripcion,estadoAlerta) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?);";
        CassandraPool cassandraPool = CassandraPool.getInstance();
        var session = cassandraPool.getSession();
        var instant = alerta.getFecha().atZone(ZoneId.systemDefault()).toInstant();
        UUID id = UUID.randomUUID();
        Sensor sensor = alerta.getSensor();
        alerta.setId(id);
        try {

            session.execute(cass,
                    alerta.getId(),
                    sensor.getId(),
                    sensor.getCod(),
                    sensor.getTipo(),
                    sensor.getLatitud(),
                    sensor.getLongitud(),
                    sensor.getCiudad(),
                    sensor.getPais(),
                    sensor.getEstado(),
                    instant,
                    alerta.getDescripcion(),
                    alerta.getEstado()
            );
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public Alerta obtenerAlerta(String idAlerta){
        String cass = "SELECT * FROM Alertas WHERE idSensor = ?;";
        CassandraPool cassandraPool = CassandraPool.getInstance();
        var session = cassandraPool.getSession();
        Row row = session.execute(cass, idAlerta).one();
        return mappearAlerta(row);
    }

    public void eliminarAlerta(int idAlerta){
        String cass = "DELETE FROM Alertas WHERE idSensor = ?;";
        CassandraPool cassandraPool = CassandraPool.getInstance();
        var session = cassandraPool.getSession();
        session.execute(cass, idAlerta);
    }

    public List<Alerta> checkAlerts() {
        String cass = "SELECT * FROM Alertas ;";
        CassandraPool cassandraPool = CassandraPool.getInstance();
        var session = cassandraPool.getSession();
        var resultSet= session.execute(cass);
        List<Alerta> alertas = new ArrayList<>();

        for (Row row : resultSet){
            Alerta alerta = mappearAlerta(row);
            alertas.add(alerta);
        }

        return alertas;
    }
    public void deleteSensorAlerts(){
        String cass = "TRUNCATE Alertas ;";
        CassandraPool cassandraPool = CassandraPool.getInstance();
        var session = cassandraPool.getSession();
        session.execute(cass);
    }

    public Alerta mappearAlerta(Row row){
        Alerta alerta = new Alerta();
        if (row != null) {
            alerta.setSensor(new Sensor());
            alerta.setId(row.getUuid("id"));

            Instant instant = row.getInstant("fechayHora");
            if (instant != null) {
                alerta.setFecha(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));
            } else {
                alerta.setFecha(null);
            }

            // Map alert-specific columns according to the table
            alerta.setDescripcion(row.getString("descripcion"));
            alerta.setEstado(row.getString("estado"));

            // Map sensor fields (as stored in the same row)
            alerta.getSensor().setCod(row.getString("cod"));
            alerta.getSensor().setTipo(row.getString("tipo"));
            alerta.getSensor().setLatitud(row.getDouble("latitud"));
            alerta.getSensor().setLongitud(row.getDouble("longitud"));
            alerta.getSensor().setCiudad(row.getString("ciudad"));
            alerta.getSensor().setPais(row.getString("pais"));
            alerta.getSensor().setEstado(row.getString("estado")); // note: same column name in script
            alerta.getSensor().setId(row.getInt("idSensor"));
        } else {
            alerta = null;
        }
        return alerta;
    }
}