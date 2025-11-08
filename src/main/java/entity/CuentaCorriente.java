package entity;

import jakarta.persistence.*;

@Entity
public class CuentaCorriente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "numero_cuenta", nullable = false, unique = true)
    private String numeroCuenta;

    @Column(nullable = false)
    private Double saldo;

    // 🔹 Relación con Usuario
    @ManyToOne(optional = false) // cada cuenta pertenece a un usuario
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;

    public CuentaCorriente() { }

    public CuentaCorriente(String numeroCuenta, Double saldo, Usuario usuario) {
        this.numeroCuenta = numeroCuenta;
        this.saldo = saldo;
        this.usuario = usuario;
    }

    // --- Getters y Setters ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}

