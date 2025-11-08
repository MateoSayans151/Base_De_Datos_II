package repository.sql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import entity.CuentaCorriente;
import java.util.List;

@Repository
public interface CuentaCorrienteRepository extends JpaRepository<CuentaCorriente, Integer> {

    // Buscar por número de cuenta
    CuentaCorriente findByNumeroCuenta(String numeroCuenta);

    // 🔹 Buscar todas las cuentas pertenecientes a un usuario
    List<CuentaCorriente> findByUsuario_Id(int usuarioId);

    // 🔹 (Opcional) Buscar por nombre o mail del usuario asociado
    List<CuentaCorriente> findByUsuario_NombreContainingIgnoreCase(String nombreUsuario);
    List<CuentaCorriente> findByUsuario_Mail(String mailUsuario);
}
