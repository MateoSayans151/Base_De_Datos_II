package service;

import entity.Grupo;
import entity.Mensaje;
import entity.Usuario;
import exceptions.ErrorConectionMongoException;
import repository.mongo.GrupoRepository;

import java.util.List;
import java.util.ArrayList;

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

    /**
     * Add a participant (Usuario) to an existing group.
     */
    public void addParticipantToGroup(int grupoId, Usuario usuario) throws ErrorConectionMongoException {
        grupoRepository.addUserToGroup(grupoId, usuario);
    }

    // Backwards-compatible wrapper used by UI
    public java.util.List<Grupo> getGruposByUsuario(Usuario usuario) {
        try {
            return getGroupByUserId(usuario.getId());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public Grupo crearGrupo(String nombre, Usuario creador) {
        try {
            Grupo nuevo = new Grupo(nombre);
            // Attempt to set initial members list with the creator
            java.util.List<Usuario> miembros = new ArrayList<>();
            miembros.add(creador);
            nuevo.setMiembros(miembros);
            createGroup(nuevo);
            return nuevo;
        } catch (Exception e) {
            return null;
        }
    }
}