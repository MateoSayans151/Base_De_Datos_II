package entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;   // Relación con la entidad Usuario

    private LocalDate fechaEmision;
    private String estado; // pendiente, pagada o vencida

    @ManyToMany
    @JoinTable(
        name = "factura_proceso",
        joinColumns = @JoinColumn(name = "factura_id"),
        inverseJoinColumns = @JoinColumn(name = "proceso_id")
    )
    private List<Proceso> procesosFacturados;
    private Double total;
    public Factura() {}

    public Factura(Usuario usuario, LocalDate fechaEmision, String estado, List<Proceso> procesosFacturados, Double total) {
        this.usuario = usuario;
        this.fechaEmision = fechaEmision;
        this.estado = estado;
        this.procesosFacturados = procesosFacturados;
        this.total = total;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public List<Proceso> getProcesosFacturados() { return procesosFacturados; }
    public void setProcesosFacturados(List<Proceso> procesosFacturados) { this.procesosFacturados = procesosFacturados; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
}
    

