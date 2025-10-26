package modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // ID de mensaje

    // Usuario que envía el mensaje
    @ManyToOne
    @JoinColumn(name = "remitente_id", nullable = false)
    private Usuario remitente;

    // Usuario destinatario (si es mensaje privado)
    @ManyToOne
    @JoinColumn(name = "destinatario_id")
    private Usuario destinatario;

    // Grupo destinatario (si es mensaje grupal)
    @ManyToOne
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;

    private LocalDateTime fechaHora; // Fecha y hora de envío

    @Column(columnDefinition = "TEXT")
    private String contenido; // Texto del mensaje

    private String tipo; // "privado" o "grupal"

    public Mensaje() {}

    public Mensaje(Usuario remitente, Usuario destinatario, String contenido, String tipo) {
        this.remitente = remitente;
        this.destinatario = destinatario;
        this.contenido = contenido;
        this.tipo = tipo;
        this.fechaHora = LocalDateTime.now();
    }

    // --- Getters y Setters ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Usuario getRemitente() {
        return remitente;
    }

    public void setRemitente(Usuario remitente) {
        this.remitente = remitente;
    }

    public Usuario getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(Usuario destinatario) {
        this.destinatario = destinatario;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
