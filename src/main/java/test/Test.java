package test;

import controlador.PedidosController;

public class Test {

	public static void main(String[] args) {
		System.out.println("Hola Persistencia");

		PedidosController p = new PedidosController();
		p.agregarCliente("Cacho Garmendia", "23-4567890-1");

	}

}
