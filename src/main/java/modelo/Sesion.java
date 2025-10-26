package modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Sesion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // ID de sesión

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario; // Usuario asociado a la sesión

    private String rol; // Rol del usuario en esta sesión

    private LocalDateTime fechaHoraInicio; // Inicio de sesión
    private LocalDateTime fechaHoraCierre; // Cierre de sesión (puede ser null si sigue activa)

    private String estado; // "activa" o "inactiva"

    public Sesion() {}

    public Sesion(Usuario usuario, String rol, LocalDateTime fechaHoraInicio, String estado) {
        this.usuario = usuario;
        this.rol = rol;
        this.fechaHoraInicio = fechaHoraInicio;
        this.estado = estado;
    }

    // --- Getters y Setters ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }

    public LocalDateTime getFechaHoraCierre() {
        return fechaHoraCierre;
    }

    public void setFechaHoraCierre(LocalDateTime fechaHoraCierre) {
        this.fechaHoraCierre = fechaHoraCierre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}

