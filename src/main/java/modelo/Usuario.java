package modelo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String nombre;
	private String email;
	private String contraseña;
	private String estado;
	private int fechaRegistro;
	
	public Usuario() { }

	public Usuario(String nombre, String cuit) {
		this.nombre = nombre;
		this.cuit = cuit;

	}

	public int getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getEmail() {
		return cuit;
	}

	public void setEmail(String cuit) {
		this.cuit = cuit;
	}

	public String getContraseña(){
		return contraseña;
	}
	
	public void setContraseña(String contraseña){
		this.contraseña = contraseña;
	}
	
	public Integer getfechaRegistro(){
		return fechaRegistro;
	}

	public void setfechaRegistro(int fechaRegistro){
		this.fechaRegistro = fechaRegistro;
	}
	
}
