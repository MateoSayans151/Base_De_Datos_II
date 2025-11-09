package repository.sql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import entity.CuentaCorriente;
import entity.Usuario;

@Repository
public interface CuentaCorrienteRepository extends JpaRepository<CuentaCorriente, Integer> {

    // Find by usuario_id
    CuentaCorriente findByUsuarioId(int usuarioId);
}
