package entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class ControlFuncionamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idControl;

    @ManyToOne
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;  // Relación con la entidad Sensor

    private LocalDate fechaRevision;
    private String estadoSensor;
    private String observaciones;

    public ControlFuncionamiento() {}

    public ControlFuncionamiento(Sensor sensor, LocalDate fechaRevision, String estadoSensor, String observaciones) {
        this.sensor = sensor;
        this.fechaRevision = fechaRevision;
        this.estadoSensor = estadoSensor;
        this.observaciones = observaciones;
    }

    // Getters y Setters
    public int getIdControl() {
        return idControl;
    }

    public void setIdControl(int idControl) {
        this.idControl = idControl;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public LocalDate getFechaRevision() {
        return fechaRevision;
    }

    public void setFechaRevision(LocalDate fechaRevision) {
        this.fechaRevision = fechaRevision;
    }

    public String getEstadoSensor() {
        return estadoSensor;
    }

    public void setEstadoSensor(String estadoSensor) {
        this.estadoSensor = estadoSensor;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
