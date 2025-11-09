package entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "MovimientosCC")
public class MovimientoCC {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_corriente_id", nullable = false)
    private CuentaCorriente cuentaCorriente;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false)
    private TipoMovimiento tipoMovimiento;

    @Column(nullable = false)
    private Double monto;

    @Column(name = "saldo_resultante", nullable = false)
    private Double saldoResultante;

    @Column(name = "referencia")
    private String referencia;

    @Column(name = "fecha_movimiento", nullable = false)
    private LocalDateTime fechaMovimiento;

    public enum TipoMovimiento {
        DEPOSITO,
        RETIRO,
        PAGO_FACTURA
    }

    public MovimientoCC() {
        this.fechaMovimiento = LocalDateTime.now();
    }

    public MovimientoCC(CuentaCorriente cuenta, TipoMovimiento tipo, Double monto, Double saldoResultante, String referencia) {
        this();
        this.cuentaCorriente = cuenta;
        this.tipoMovimiento = tipo;
        this.monto = monto;
        this.saldoResultante = saldoResultante;
        this.referencia = referencia;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CuentaCorriente getCuentaCorriente() {
        return cuentaCorriente;
    }

    public void setCuentaCorriente(CuentaCorriente cuentaCorriente) {
        this.cuentaCorriente = cuentaCorriente;
    }

    public TipoMovimiento getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(TipoMovimiento tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public Double getSaldoResultante() {
        return saldoResultante;
    }

    public void setSaldoResultante(Double saldoResultante) {
        this.saldoResultante = saldoResultante;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public LocalDateTime getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(LocalDateTime fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }
}