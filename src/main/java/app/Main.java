package app;

import com.mongodb.client.MongoDatabase;
import connections.CassandraPool;
import connections.MongoPool;
import connections.RedisPool;
import connections.SQLPool;
import entity.*;
import exceptions.ErrorConectionMongoException;
import repository.mongo.RolRepository;
import repository.redis.InicioSesionRepository;
import service.*;
import ui.WelcomeFrame;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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



        mongoPool.close();

        System.out.println("Conexiones cerradas correctamente.");


        CassandraPool cassandraPool = CassandraPool.getInstance();
        SensorService sensorService = new SensorService();
        AlertaService alertaService = new AlertaService();
        Alerta newAlerta = new Alerta();
        Sensor newSensor = sensorService.getSensor(23);
        newAlerta.setSensor(newSensor);
        newAlerta.setEstado("Activa");
        newAlerta.setDescripcion("Anda mal el sensor");
        newAlerta.setFecha(LocalDateTime.now());
        alertaService.create(newAlerta);
        List<Alerta> alertas =alertaService.checkAlerts();
        for (Alerta alerta : alertas) {
            System.out.println("Alerta obtenida:\n " + "ID Alerta: "+ alerta.getSensor().getCod() + " \n " + "Descripcion: "+alerta.getDescripcion()+ " \n " + "Fecha: "+alerta.getFecha());
        }
        alertaService.delete(newSensor.getId());
        System.out.println(alertaService.checkAlerts());
        CassandraPool.close();

    }


        UsuarioService usuarioService = new UsuarioService();
        Usuario remitente = usuarioService.getById(1);
        Usuario destinatario = usuarioService.getById(2);
        MensajeService mensajeService = new MensajeService();
        Mensaje mensaje = new Mensaje();
        mensaje.setFechaEnvio(LocalDateTime.now());
        mensaje.setContenido("Mensaje de prueba");
        mensaje.setTipo("privado");
        mensaje.setRemitente(remitente);
        mensaje.setDestinatario(destinatario);
        mensaje.setId(1);
        mensajeService.createMensaje(mensaje);
        System.out.println("Mensaje del Remitente: ");
        List<Mensaje> mensajesRemitente = mensajeService.getMensajesPorRemitente(remitente.getId());
        for (Mensaje m : mensajesRemitente) {
            System.out.println("ID: " + m.getId() + ", Contenido: " + m.getContenido() + ", Destinatario: " + m.getDestinatario().getNombre());
        }
        System.out.println("Mensaje recibido por el Destinatario: ");
        List<Mensaje> mensajesDestinatario = mensajeService.getMensajesPorDestinatario(destinatario.getId());
        for (Mensaje m : mensajesDestinatario) {
            System.out.println("ID: " + m.getId() + ", Contenido: " + m.getContenido() + ", Remitente: " + m.getRemitente().getNombre());
        }


        List<Usuario> usuarios = new ArrayList<>();
        UsuarioService usuarioService = new UsuarioService();
        Usuario get1 = usuarioService.getById(1);
        Usuario get2 = usuarioService.getById(2);
        usuarios.add(get1);
        usuarios.add(get2);

        GrupoService grupoService = new GrupoService();
        Grupo grupo = new Grupo();
        grupo.setNombre("Grupo 1");
        grupo.setMiembros(usuarios);
        //grupoService.createGroup(grupo);
        System.out.println("Grupo creado: " + grupo.getNombre());
        Mensaje mensaje = new Mensaje();
        mensaje.setId(1);
        mensaje.setContenido("Hola a todos!");
        mensaje.setFechaEnvio(LocalDateTime.now());
        mensaje.setRemitente(get1);
        mensaje.setTipo("grupal");
        //grupoService.addMessageToGroup(grupo.getId(), mensaje);
        System.out.println("Mensaje agregado al grupo: " + mensaje.getContenido());

        List<Mensaje> mensajesDelGrupo = grupoService.getMessagesFromGroup(grupo.getId());
        System.out.println("Mensajes del grupo " + grupo.getNombre() + ":");
        for (Mensaje m : mensajesDelGrupo) {
            System.out.println("ID: " + m.getId() + ", Contenido: " + m.getContenido());
        }

         */
    }
}
