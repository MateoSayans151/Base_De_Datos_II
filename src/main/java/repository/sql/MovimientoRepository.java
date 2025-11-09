package repository.sql;

import entity.MovimientoCC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<MovimientoCC, Integer> {
    List<MovimientoCC> findByCuentaCorrienteId(int cuentaCorrienteId);
    List<MovimientoCC> findByCuentaCorrienteIdOrderByFechaMovimientoDesc(int cuentaCorrienteId);
}
