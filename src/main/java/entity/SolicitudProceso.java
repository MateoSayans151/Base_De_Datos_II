package entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class SolicitudProceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // ID de solicitud

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario; // Usuario que realiza la solicitud

    @ManyToOne
    @JoinColumn(name = "proceso_id", nullable = false)
    private Proceso proceso; // Proceso solicitado

    private LocalDateTime fechaSolicitud; // Fecha y hora en que se realizó la solicitud

    private String estado; // "pendiente" o "completado"

    public SolicitudProceso() {}

    public SolicitudProceso(Usuario usuario, Proceso proceso, LocalDateTime fechaSolicitud, String estado) {
        this.usuario = usuario;
        this.proceso = proceso;
        this.fechaSolicitud = fechaSolicitud;
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

    public Proceso getProceso() {
        return proceso;
    }

    public void setProceso(Proceso proceso) {
        this.proceso = proceso;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
