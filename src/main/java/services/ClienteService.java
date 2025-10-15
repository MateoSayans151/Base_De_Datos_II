package services;

import modelo.Cliente;
import repositories.ObjectRepository;

public class ClienteService {

	private static ClienteService instance;
	
	private ClienteService() {}
	
	public static ClienteService getInstance() {
		if(instance == null)
			instance = new ClienteService();
		return instance;
	}
	public void crearCliente(String nombre, String cuit) {
		Cliente c = new Cliente(nombre, cuit);
		ObjectRepository.getInstance().guardarCliente(c);
		System.out.println("Id " + c.getNumero());
	}
}
