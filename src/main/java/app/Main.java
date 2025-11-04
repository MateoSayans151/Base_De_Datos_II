package app;

import com.mongodb.client.MongoDatabase;
import connections.CassandraPool;
import connections.MongoPool;
import connections.RedisPool;
import connections.SQLPool;
import entity.Medicion;
import entity.Sensor;
import entity.Usuario;
import exceptions.ErrorConectionMongoException;
import repository.redis.InicioSesionRepository;
import service.MedicionService;
import service.SensorService;
import service.UsuarioService;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws ErrorConectionMongoException, SQLException {
        System.out.println("Iniciando el TPO");

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
        Medicion medicion = medicionService.getById("f83d5d28-f1b0-4784-aa9e-48a9c81d113f");
        System.out.println("Medicion obtenida: " + medicion.getTemperatura() + " - " + medicion.getHumedad());
        LocalDateTime from = LocalDate.parse("2023-01-01").atStartOfDay();
        LocalDateTime until = LocalDate.parse("2023-12-31").atTime(23,59,59);
        var hum = medicionService.getAverageHumidityBetweenDates("Rosario", from, until);
        System.out.println("Humedad promedio del sensor Rosario en 2023: " + hum);
        CassandraPool.close();
        mongoPool.close();

        System.out.println("Conexiones cerradas correctamente.");


    }
}
