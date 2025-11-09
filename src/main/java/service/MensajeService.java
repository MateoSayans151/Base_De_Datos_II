package service;

import entity.Mensaje;
import entity.Usuario;
import entity.Grupo;
import exceptions.ErrorConectionMongoException;
import repository.mongo.MensajesRepository;

import java.util.List;
import java.time.LocalDateTime;

public class MensajeService {

    public static MensajeService instance;
    private static MensajesRepository mensajesRepository = MensajesRepository.getInstance();

    public MensajeService() {}

    public static MensajeService getInstance() {
        if (instance == null)
            instance = new MensajeService();
        return instance;
    }

    public void createMensaje(Mensaje mensaje) throws ErrorConectionMongoException {
        mensajesRepository.crearMensaje(mensaje);
    }

    public void enviarMensajePersonal(Usuario remitente, Usuario destinatario, String contenido) throws ErrorConectionMongoException {
        Mensaje mensaje = new Mensaje();
        mensaje.setRemitente(remitente);
        mensaje.setDestinatario(destinatario);
        mensaje.setContenido(contenido);
        mensaje.setFechaEnvio(LocalDateTime.now());
        mensaje.setTipo("privado");
        // For personal messages we intentionally do not set 'tipo' (stored by presence of destinatario)
        createMensaje(mensaje);
    }

    public void enviarMensajeGrupo(Usuario remitente, Grupo grupo, String contenido) throws ErrorConectionMongoException {
        Mensaje mensaje = new Mensaje();
        mensaje.setRemitente(remitente);
        mensaje.setGrupo(grupo);
        mensaje.setContenido(contenido);
        mensaje.setFechaEnvio(LocalDateTime.now());
        // Database expects 'grupal' for group messages
        mensaje.setTipo("grupal");
        createMensaje(mensaje);
    }

    public List<Mensaje> getMensajesPorRemitente(int remitenteId) throws ErrorConectionMongoException {
        return mensajesRepository.obtenerMensajesPorRemitente(remitenteId);
    }
    
    public List<Mensaje> getMensajesPorDestinatario(int destinatarioId) throws ErrorConectionMongoException {
        return mensajesRepository.obtenerMensajesPorDestinatario(destinatarioId);
    }

    public List<Mensaje> getMensajesPorGrupo(Grupo grupo) throws ErrorConectionMongoException {
        return mensajesRepository.obtenerMensajesPorGrupo(grupo);
    }
}
