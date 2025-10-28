package entity;

import jakarta.persistence.*;

@Entity
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idSensor;

    private String cod;       // antes: Nombre
    private String tipo;

    private Double latitud;
    private Double longitud;

    private String ciudad;
    private String pais;
    private String estado;

    private String fechaIni;

    // Campo que usás en ctor y getters/setters
    private String ubicacion;

    public Sensor() {}

    public Sensor(String tipo, String ubicacion) {
        this.tipo = tipo;
        this.ubicacion = ubicacion;
    }

    // ---- getters/setters ----
    public int getIdSensor() { return idSensor; }
    public void setIdSensor(int idSensor) { this.idSensor = idSensor; }

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

    public String getFechaIni() { return fechaIni; }
    public void setFechaIni(String fechaIni) { this.fechaIni = fechaIni; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
}
