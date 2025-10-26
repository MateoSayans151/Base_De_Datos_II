package entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // ID de grupo

    private String nombre; // Nombre del grupo

    // Relación muchos a muchos con Usuario
    @ManyToMany
    @JoinTable(
        name = "grupo_usuario",
        joinColumns = @JoinColumn(name = "grupo_id"),
        inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private Set<Usuario> miembros = new HashSet<>();

    public Grupo() {}

    public Grupo(String nombre) {
        this.nombre = nombre;
    }

    // --- Getters y Setters ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Set<Usuario> getMiembros() {
        return miembros;
    }

    public void setMiembros(Set<Usuario> miembros) {
        this.miembros = miembros;
    }

    // Métodos convenientes para agregar o quitar miembros
    public void agregarMiembro(Usuario usuario) {
        miembros.add(usuario);
    }

    public void quitarMiembro(Usuario usuario) {
        miembros.remove(usuario);
    }
}
