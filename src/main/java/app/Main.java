package app;

import com.mongodb.client.MongoDatabase;
import connections.CassandraPool;
import connections.MongoPool;
import connections.RedisPool;
import connections.SQLPool;
import entity.Medicion;
import entity.Rol;
import entity.Sensor;
import entity.Usuario;
import exceptions.ErrorConectionMongoException;
import repository.mongo.RolRepository;
import repository.redis.InicioSesionRepository;
import service.MedicionService;
import service.SensorService;
import service.UsuarioService;
import ui.WelcomeFrame;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ErrorConectionMongoException, SQLException {
        System.out.println("Iniciando el TPO");
        WelcomeFrame welcomeFrame = new WelcomeFrame();
        /*
        CassandraPool cassandraPool = CassandraPool.getInstance();


        MongoPool mongoPool = MongoPool.getInstance();

        System.out.println("Conectado a las bases correctamente.");

        var dbMongo = mongoPool.getConnection();

        RedisPool redisPool = RedisPool.getInstance();


        SQLPool sqlPool = SQLPool.getInstance();
        UsuarioService usuarioService = new UsuarioService(InicioSesionRepository.getInstance());

        System.out.println("Conectado a la base correctamente: " + dbMongo.getName());
        Usuario user = usuarioService.getById(1);
        System.out.println("Usuario obtenido: " + user.getNombre() + " - " + user.getMail());

        SensorService sensorService = SensorService.getInstance();

        Sensor sensor = sensorService.getSensor(1);
        System.out.println("Sensor obtenido: " + sensor.getCod() + " - " + sensor.getCiudad());

        MedicionService medicionService = MedicionService.getInstance();
        List<Medicion> mediciones = medicionService.getBySensorId(23);
        for (Medicion medicion : mediciones) {
            System.out.println("Medicion obtenida:\n " + "Temperatura: "+ medicion.getTemperatura() + " \n " + "Humedad: "+medicion.getHumedad());
        }

        String from = "2025-11-04";
        String until = "2025-11-04";
        LocalDateTime fromLdt = LocalDate.parse(from).atStartOfDay();
        LocalDateTime untilLdt = LocalDate.parse(until).atTime(23, 59, 59);
        var hum = medicionService.getAverageHumidityBetweenDates("Rosario", fromLdt, untilLdt);
        System.out.println("Humedad promedio del sensor Rosario en 2023: " + hum);
        var temp = medicionService.getAverageTemperatureBetweenDates("Rosario", fromLdt, untilLdt);
        System.out.println("Temperatura promedio del sensor Rosario en 2023: " + temp);

        Rol rol = RolRepository.getInstance().obtenerRol(1);
        Usuario newUser = new Usuario("Juan Perez", "juanperez@gmail.com", "password123",rol);
        usuarioService.create(newUser);
        System.out.println("Nuevo usuario creado: " + newUser.getNombre() + " - " + newUser.getMail());

        sensorService.createSensor("S-5555", "temperatura", -34.6037, -58.3816, "Buenos Aires", "Argentina",LocalDateTime.now());

        List<Sensor> sensors = sensorService.getSensorsByCountry("Argentina");
        for (Sensor s : sensors) {
            System.out.println("Sensor en Argentina: " + s.getCod() + " - " + s.getCiudad());
        }


        CassandraPool.close();
        mongoPool.close();

        System.out.println("Conexiones cerradas correctamente.");

*/
    }
}
