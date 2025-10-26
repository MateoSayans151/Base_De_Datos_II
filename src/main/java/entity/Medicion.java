package entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Medicion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Relación con Sensor (muchas mediciones pueden pertenecer a un sensor)
    @ManyToOne
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;

    private LocalDateTime fecha;   // fecha y hora de la medición
    private Double temperatura;
    private Double humedad;

    public Medicion() {}

    public Medicion(Sensor sensor, LocalDateTime fecha, Double temperatura, Double humedad) {
        this.sensor = sensor;
        this.fecha = fecha;
        this.temperatura = temperatura;
        this.humedad = humedad;
    }

    // ---- Getters y Setters ----
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Sensor getSensor() { return sensor; }
    public void setSensor(Sensor sensor) { this.sensor = sensor; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public Double getTemperatura() { return temperatura; }
    public void setTemperatura(Double temperatura) { this.temperatura = temperatura; }

    public Double getHumedad() { return humedad; }
    public void setHumedad(Double humedad) { this.humedad = humedad; }
}
