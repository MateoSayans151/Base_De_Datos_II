package service;

import entity.Grupo;
import entity.Mensaje;
import exceptions.ErrorConectionMongoException;
import repository.mongo.GrupoRepository;

import java.util.List;

public class GrupoService {

    public static GrupoService instance;
    private static GrupoRepository grupoRepository = GrupoRepository.getInstance();

    public GrupoService() {}

    public static GrupoService getInstance() {
        if (instance == null)
            instance = new GrupoService();
        return instance;
    }

    public void createGroup(Grupo grupo) throws ErrorConectionMongoException {
        grupoRepository.createGroup(grupo);
    }

    public Grupo getGroup(int id) throws ErrorConectionMongoException {
        return grupoRepository.getGroup(id);
    }

    public List<Grupo> getGroupByName(String nombreGrupo) throws ErrorConectionMongoException {
        return grupoRepository.getGroupByName(nombreGrupo);
    }

    public List<Grupo> getGroupByUserId(int usuarioId) throws ErrorConectionMongoException {
        return grupoRepository.getGroupByUserId(usuarioId);
    }
    public void addMessageToGroup(int grupoId, Mensaje mensaje) throws ErrorConectionMongoException {
        grupoRepository.addMessageToGroup(grupoId, mensaje);
    }
    public List<Mensaje> getMessagesFromGroup(int grupoId) throws ErrorConectionMongoException {
        return grupoRepository.getMessagesByGroupId(grupoId);
    }
}