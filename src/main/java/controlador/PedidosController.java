package controlador;

import services.ClienteService;

public class PedidosController {
	
	public void agregarCliente(String nombre, String cuit) {
		ClienteService.getInstance().crearCliente(nombre, cuit);
	}

}
