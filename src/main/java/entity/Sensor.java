package entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String cod;       // antes: Nombre
    private String tipo;

    private Double latitud;
    private Double longitud;

    private String ciudad;
    private String pais;
    private String estado;

    private LocalDateTime fechaIni;



    public Sensor() {}

    public Sensor(String cod,String tipo,Double latitud,Double longitud,String ciudad,String pais,LocalDateTime fechaIni) {
        this.cod = cod;
        this.tipo = tipo;
        this.latitud = latitud;
        this.longitud = longitud;
        this.ciudad = ciudad;
        this.pais = pais;
        this.estado = "activo";
        this.fechaIni = fechaIni;
    }

    // ---- getters/setters ----
    public int getId() { return id; }
    public void setId(int idSensor) { this.id = idSensor; }

    public String getCod() { return cod; }
    public void setCod(String cod) { this.cod = cod; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }

    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaIni() { return fechaIni; }
    public void setFechaIni(LocalDateTime fechaIni) { this.fechaIni = fechaIni; }

}
