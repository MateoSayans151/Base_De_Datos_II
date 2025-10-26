package entity;

import jakarta.persistence.*;

@Entity
public class Proceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // ID de proceso

    private String nombre;
    private String descripcion;
    private String tipoProceso;
    private double costo;

    public Proceso() {}

    public Proceso(String nombre, String descripcion, String tipoProceso, double costo) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipoProceso = tipoProceso;
        this.costo = costo;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipoProceso() {
        return tipoProceso;
    }

    public void setTipoProceso(String tipoProceso) {
        this.tipoProceso = tipoProceso;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }
}
