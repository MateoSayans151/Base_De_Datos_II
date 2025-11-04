package entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import entity.Rol;
@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nombre;
    private String mail;
    private String contrasena;
    private String estado;

    @ManyToOne
    @JoinColumn(name = "rol_id")
    private Rol rol;
    private LocalDateTime fechaRegistro;

    public Usuario() {}

    public Usuario(String nombre, String email, String contrasena, Rol rol) {
        this.nombre = nombre;
        this.mail = email;
        this.contrasena = contrasena;
        this.estado = "activo";
        this.rol = rol;
        this.fechaRegistro = LocalDateTime.now();
    }

    // --- Getters y Setters ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String email) {
        this.mail = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
    public Rol getRol() {return rol;}
    public void setRol(Rol rol) {this.rol = rol;}
}

