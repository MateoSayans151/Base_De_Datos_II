package controlador;

import services.UsuarioService;

public static class UsuarioController{
	
	public void createUsuario(String nombre, String cuit) {
		UsuarioService.getInstance().crearUsuario(nombre, cuit);
	}
	public getUsuario (String nombre){
		UsuarioService.getInstance().getUsuario()
	}
	public updateUsuario (String nombre, String email, String contraseña, String estado,
	 Integer fechaRegistro)

    public void deleteUsuario(@PathVariable Integer id){
		UsuarioService.deleteUsuario(id);
	}
}