package repository.cassandra;

import com.datastax.oss.driver.api.core.cql.Row;
import connections.CassandraPool;
import entity.Alerta;
import entity.Sensor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
        String cass = "INSERT INTO Alertas (idAlerta,idSensor,cod,tipo,latitud,longitud,ciudad,pais,estado,fechayHora,descripcion,estado) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?);";
        CassandraPool cassandraPool = CassandraPool.getInstance();
        var session = CassandraPool.getSession();
        UUID id = UUID.randomUUID();
        Sensor sensor = alerta.getSensor();
        alerta.setId(id);
        session.execute(cass,
                alerta.getId(),
                sensor.getIdSensor(),
                sensor.getCod(),
                sensor.getTipo(),
                sensor.getLatitud(),
                sensor.getLongitud(),
                sensor.getCiudad(),
                sensor.getPais(),
                sensor.getEstado(),        // sensor state (as stored in table)
                alerta.getFecha(),         // fechayHora
                alerta.getDescripcion(),   // descripcion (alert text)
                alerta.getEstado()         // estado (alert state)
        );
    }

    public Alerta obtenerAlerta(String idAlerta){
        String cass = "SELECT * FROM Alertas WHERE idAlerta = ?;";
        CassandraPool cassandraPool = CassandraPool.getInstance();
        var session = CassandraPool.getSession();
        Row row = session.execute(cass, idAlerta).one();
        return mappearAlerta(row);
    }

    public void eliminarAlerta(String idAlerta){
        String cass = "DELETE FROM Alertas WHERE idAlerta = ?;";
        CassandraPool cassandraPool = CassandraPool.getInstance();
        var session = CassandraPool.getSession();
        session.execute(cass, idAlerta);
    }

    public Alerta mappearAlerta(Row row){
        Alerta alerta = new Alerta();
        if (row != null) {
            alerta.setSensor(new Sensor());
            alerta.setId(row.getUuid("idAlerta"));

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
            alerta.getSensor().setIdSensor(row.getInt("idSensor"));
        } else {
            alerta = null;
        }
        return alerta;
    }
}