package repository.sql;

import modelo.sql.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
  boolean existsByEmail(String email);
}
