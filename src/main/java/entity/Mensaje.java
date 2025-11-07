package entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // idMensaje de mensaje

    // Usuario que envía el mensaje
    @ManyToOne
    @JoinColumn(name = "remitente_id", nullable = false)
    private Usuario remitente;

    // Usuario destinatario (si es mensaje privado)
    @ManyToOne
    @JoinColumn(name = "destinatario_idMensaje")
    private Usuario destinatario;

    // Grupo destinatario (si es mensaje grupal)
    /*
    @ManyToOne
    @JoinColumn(name = "idGrupo")
    private Grupo grupo;

     */

    private LocalDateTime fechaEnvio; // Fecha y hora de envío

    @Column(columnDefinition = "TEXT")
    private String contenido; // Texto del mensaje

    private String tipo; // "privado" o "grupal"

    public Mensaje() {}

    public Mensaje(Usuario remitente, Usuario destinatario, String contenido, String tipo) {
        this.remitente = remitente;
        this.destinatario = destinatario;
        this.contenido = contenido;
        this.tipo = tipo;
        this.fechaEnvio = LocalDateTime.now();
    }

    // --- Getters y Setters ---
    public int getId() {
        return id;
    }

    public void setId(int idMensaje) {
        this.id = idMensaje;
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


    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
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
    /*
    public int getIdGrupo() {return grupo.getId();}
    public  void setIdGrupo(int idGrupo) {this.grupo.setId(idGrupo);}

     */

}
