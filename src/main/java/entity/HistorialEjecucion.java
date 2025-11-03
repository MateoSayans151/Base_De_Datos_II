package entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class HistorialEjecucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idEjecucion;

    @ManyToOne
    @JoinColumn(name = "solicitud_id")
    private SolicitudProceso solicitud;   // Relación con la entidad Solicitud

    private LocalDateTime fechaEjecucion;
    private String resultado;
    private String estado;

    public HistorialEjecucion() {}

    public HistorialEjecucion(SolicitudProceso solicitud, LocalDateTime fechaEjecucion, String resultado, String estado) {
        this.solicitud = solicitud;
        this.fechaEjecucion = fechaEjecucion;
        this.resultado = resultado;
        this.estado = estado;
    }

    // Getters y Setters
    public int getIdEjecucion() { return idEjecucion; }
    public void setIdEjecucion(int idEjecucion) { this.idEjecucion = idEjecucion; }

    public SolicitudProceso getSolicitud() { return solicitud; }
    public void setSolicitud(SolicitudProceso solicitud) { this.solicitud = solicitud; }

    public LocalDateTime getFechaEjecucion() { return fechaEjecucion; }
    public void setFechaEjecucion(LocalDateTime fechaEjecucion) { this.fechaEjecucion = fechaEjecucion; }

    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
