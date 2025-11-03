package repository.sql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import entity.CuentaCorriente;

@Repository
public interface CuentaCorrienteRepository extends JpaRepository<CuentaCorriente, Integer> {

    CuentaCorriente findByNumeroCuenta(String numeroCuenta);
}
