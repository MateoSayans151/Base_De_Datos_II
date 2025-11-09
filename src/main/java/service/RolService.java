
package service;

import entity.Rol;
import exceptions.ErrorConectionMongoException;
import repository.mongo.RolRepository;

public class RolService {

    public static RolService instance;
    private static RolRepository rolRepository = RolRepository.getInstance();

    public RolService() {}

    public static RolService getInstance() {
        if (instance == null)
            instance = new RolService();
        return instance;
    }

    public void createRol(String nombre) throws ErrorConectionMongoException {
        Rol rol = new Rol(nombre);
        rolRepository.createRole(rol);
    }

    public Rol getRol(int id) throws ErrorConectionMongoException {
        return rolRepository.getRole(id);
    }
    public Rol getRolByName(String nombre) throws ErrorConectionMongoException {
        return rolRepository.getRoleByName(nombre);
    }
}
