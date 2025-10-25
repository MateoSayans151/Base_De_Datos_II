package services;

import modelo.Usuario;
import repositories.ObjectRepository;

public class UsuarioService {

	private static UsuarioService instance;
	
	private UsuarioService() {}
	
	public static UsuarioService getInstance() {
		if(instance == null)
			instance = new UsuarioService();
		return instance;
	}
	public void crearUsuario(String nombre, String cuit) {
		Usuario usuario = new Usuario(nombre, cuit);
		ObjectRepository.getInstance().guardarCliente(c);
		System.out.println("Id " + c.getNumero());
	}
	public Usuario getUsuario(){
		return UsuarioRepository.getUsuario();
	}
	public void updateUsuario(int numero, String nombre, String cuit){
		UsuarioRepository.modificarUsuario(numero,nombre,cuit);
	}
	public void deleteUsuario(@PathVariable int id){
		
	}
}
