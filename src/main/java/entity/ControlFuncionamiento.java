package entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class ControlFuncionamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;  // Relación con la entidad Sensor

    private LocalDate fechaControl;
    private String estado;
    private String observaciones;
    //['id', 'sensor', 'fechaControl', 'estado', 'obvservaciones'],
    public ControlFuncionamiento() {}

    public ControlFuncionamiento(Sensor sensor, LocalDate fechaControl, String estado, String observaciones) {
        this.sensor = sensor;
        this.fechaControl = fechaControl;
        this.estado = estado;
        this.observaciones = observaciones;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public LocalDate getFechaControl() {
        return fechaControl;
    }

    public void setFechaControl(LocalDate fechaControl) {
        this.fechaControl = fechaControl;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
