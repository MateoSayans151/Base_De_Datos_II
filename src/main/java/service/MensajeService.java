package service;

import entity.Mensaje;
import exceptions.ErrorConectionMongoException;
import repository.mongo.MensajesRepository;

import java.util.List;

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

    public List<Mensaje> getMensajesPorRemitente(int remitenteId) throws ErrorConectionMongoException {
        return mensajesRepository.obtenerMensajesPorRemitente(remitenteId);
    }

    public List<Mensaje> getMensajesPorDestinatario(int destinatarioId) throws ErrorConectionMongoException {
        return mensajesRepository.obtenerMensajesPorDestinatario(destinatarioId);
    }
}
