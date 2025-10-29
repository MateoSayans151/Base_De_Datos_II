package entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    // Relación con Sensor (muchas alertas pueden pertenecer a un sensor)
    @ManyToOne
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;

    private LocalDateTime fecha;    // fechayHora
    private String descripcion;
    private String estado;

    public Alerta() {}

    public Alerta(Sensor sensor, LocalDateTime fecha, String descripcion, String estado) {
        this.sensor = sensor;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    // ---- Getters y Setters ----
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Sensor getSensor() { return sensor; }
    public void setSensor(Sensor sensor) { this.sensor = sensor; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}