package service;

import entity.Mensaje;
import entity.Usuario;
import service.GrupoService;
import entity.Grupo;
import exceptions.ErrorConectionMongoException;
import repository.mongo.MensajesRepository;

import java.util.List;
import java.time.LocalDateTime;

public class MensajeService {

    public static MensajeService instance;
    private static MensajesRepository mensajesRepository = MensajesRepository.getInstance();
    private static GrupoService grupoService = GrupoService.getInstance();

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

    /**
     * Send a message to a group: delegate to GrupoService which will store the
     * message inside the group's `mensajes` array in MongoDB.
     */
    public void enviarMensajeGrupo(Usuario remitente, int grupoId, String contenido) throws ErrorConectionMongoException {
        Mensaje mensaje = new Mensaje();
        mensaje.setRemitente(remitente);
        mensaje.setContenido(contenido);
        mensaje.setFechaEnvio(LocalDateTime.now());
        mensaje.setTipo("grupal");

        // Delegate to GrupoService to append message into group's mensajes array
        grupoService.addMessageToGroup(grupoId, mensaje);
    }

    public List<Mensaje> getMensajesPorRemitente(int remitenteId) throws ErrorConectionMongoException {
        return mensajesRepository.obtenerMensajesPorRemitente(remitenteId);
    }
    
    public List<Mensaje> getMensajesPorDestinatario(int destinatarioId) throws ErrorConectionMongoException {
        return mensajesRepository.obtenerMensajesPorDestinatario(destinatarioId);
    }

    /**
     * Retrieve messages for a group by delegating to GrupoService.
     */
    public List<Mensaje> getMensajesPorGrupo(int grupoId) throws ErrorConectionMongoException {
        return grupoService.getMessagesFromGroup(grupoId);
    }

    /**
     * Create a new group (uses GrupoService).
     */
    public Grupo crearGrupo(String nombre, Usuario creador) {
        return grupoService.crearGrupo(nombre, creador);
    }

    /**
     * Add a participant to a group (uses GrupoService).
     */
    public void agregarParticipanteAGrupo(int grupoId, Usuario usuario) throws ErrorConectionMongoException {
        grupoService.addParticipantToGroup(grupoId, usuario);
    }
}
