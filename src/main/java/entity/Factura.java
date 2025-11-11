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


    @OneToOne
    @JoinColumn(name = "proceso_id")
    private Proceso procesoFacturado;

    private Double total;


    public Factura() {}

    public Factura(Usuario usuario, LocalDate fechaEmision, String estado, Proceso procesoFacturado,
            Double total, String tipo) {
        this.usuario = usuario;
        this.fechaEmision = fechaEmision;
        this.estado = estado;
        this.total = total;
        this.procesoFacturado = procesoFacturado;
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



    // Backwards-compatible convenience accessors for code that expects a single proceso
    public Proceso getProcesoFacturado() {
        return procesoFacturado;
    }

    public void setProcesoFacturado(Proceso proceso) {
        this.procesoFacturado = proceso;
    }


    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
}
    

