package entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPago;

    @ManyToOne
    @JoinColumn(name = "factura_id")
    private Factura factura;   // Relación con la factura correspondiente

    private LocalDate fechaPago;
    private Double montoPagado;
    private String metodoPago; // por ejemplo: "Tarjeta", "Transferencia", "Efectivo"

    public Pago() {}

    public Pago(Factura factura, LocalDate fechaPago, Double montoPagado, String metodoPago) {
        this.factura = factura;
        this.fechaPago = fechaPago;
        this.montoPagado = montoPagado;
        this.metodoPago = metodoPago;
    }

    // Getters y Setters
    public int getIdPago() { return idPago; }
    public void setIdPago(int idPago) { this.idPago = idPago; }

    public Factura getFactura() { return factura; }
    public void setFactura(Factura factura) { this.factura = factura; }

    public LocalDate getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDate fechaPago) { this.fechaPago = fechaPago; }

    public Double getMontoPagado() { return montoPagado; }
    public void setMontoPagado(Double montoPagado) { this.montoPagado = montoPagado; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
}

