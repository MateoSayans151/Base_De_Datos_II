package entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPago;

    @ManyToOne
    @JoinColumn(name = "cuenta_corriente_id")
    private CuentaCorriente cuentaCorriente;

    @ManyToOne
    @JoinColumn(name = "usuario_origen_id")
    private Usuario usuarioOrigen;

    @ManyToOne
    @JoinColumn(name = "usuario_destino_id")
    private Usuario usuarioDestino;

    private LocalDate fechaPago;
    private Double montoPagado;
    private String metodoPago; // por ejemplo: "Tarjeta", "Transferencia", "Efectivo"
    private String estado; // "Pendiente", "Completado", "Cancelado"
    private String descripcion;

    public Pago() {}

    public Pago(CuentaCorriente cuentaCorriente, Usuario usuarioOrigen, Usuario usuarioDestino, 
                LocalDate fechaPago, Double montoPagado, String metodoPago, String descripcion) {
        this.cuentaCorriente = cuentaCorriente;
        this.usuarioOrigen = usuarioOrigen;
        this.usuarioDestino = usuarioDestino;
        this.fechaPago = fechaPago;
        this.montoPagado = montoPagado;
        this.metodoPago = metodoPago;
        this.descripcion = descripcion;
        this.estado = "Pendiente";
    }

    // Getters y Setters
    public int getIdPago() { return idPago; }
    public void setIdPago(int idPago) { this.idPago = idPago; }

    public CuentaCorriente getCuentaCorriente() { return cuentaCorriente; }
    public void setCuentaCorriente(CuentaCorriente cuentaCorriente) { this.cuentaCorriente = cuentaCorriente; }

    public Usuario getUsuarioOrigen() { return usuarioOrigen; }
    public void setUsuarioOrigen(Usuario usuarioOrigen) { this.usuarioOrigen = usuarioOrigen; }

    public Usuario getUsuarioDestino() { return usuarioDestino; }
    public void setUsuarioDestino(Usuario usuarioDestino) { this.usuarioDestino = usuarioDestino; }

    public LocalDate getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDate fechaPago) { this.fechaPago = fechaPago; }

    public Double getMontoPagado() { return montoPagado; }
    public void setMontoPagado(Double montoPagado) { this.montoPagado = montoPagado; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}

