package service;

import connections.SQLPool;
import entity.CuentaCorriente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.sql.CuentaCorrienteRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class CuentaCorrienteService {

    @Autowired
    private CuentaCorrienteRepository cuentaCorrienteRepository;

    public void createCuentaCorriente(CuentaCorriente cuenta) {
        if (cuentaCorrienteRepository != null) {
            cuentaCorrienteRepository.save(cuenta);
        } else {
            saveCuentaJdbc(cuenta);
        }
    }

    public CuentaCorriente getCuentaByUsuarioId(int usuarioId) {
        if (cuentaCorrienteRepository != null) {
            return cuentaCorrienteRepository.findByUsuarioId(usuarioId);
        } else {
            return getCuentaByUsuarioJdbc(usuarioId);
        }
    }

    public void actualizarSaldo(int usuarioId, double nuevoSaldo) {
        CuentaCorriente cuenta = getCuentaByUsuarioId(usuarioId);
        if (cuenta != null) {
            cuenta.setSaldo(nuevoSaldo);
            if (cuentaCorrienteRepository != null) {
                cuentaCorrienteRepository.save(cuenta);
            } else {
                saveCuentaJdbc(cuenta);
            }
        }
    }

    public double consultarSaldo(int usuarioId) {
        CuentaCorriente cuenta = getCuentaByUsuarioId(usuarioId);
        if (cuenta != null && cuenta.getSaldo() != null) {
            return cuenta.getSaldo();
        }
        return 0.0;
    }

    public void agregarFondos(int usuarioId, double monto) {
        System.out.println("Agregando fondos: " + monto + " para usuario: " + usuarioId);
        CuentaCorriente cuenta = getCuentaByUsuarioId(usuarioId);
        if (cuenta != null) {
            System.out.println("Cuenta encontrada, saldo actual: " + cuenta.getSaldo());
            double nuevoSaldo = (cuenta.getSaldo() == null ? 0.0 : cuenta.getSaldo()) + monto;
            System.out.println("Nuevo saldo calculado: " + nuevoSaldo);
            actualizarSaldo(usuarioId, nuevoSaldo);
        } else {
            System.out.println("Cuenta no encontrada, creando nueva");
            // Si no existe cuenta, crear una nueva con el saldo inicial
            CuentaCorriente nueva = new CuentaCorriente(usuarioId);
            nueva.setSaldo(monto);
            createCuentaCorriente(nueva);
        }
    }

    public void retirarFondos(int usuarioId, Double monto) {
        CuentaCorriente cuenta = getCuentaByUsuarioId(usuarioId);
        if (cuenta != null) {
            double saldoActual = cuenta.getSaldo() == null ? 0.0 : cuenta.getSaldo();
            if (saldoActual >= monto) {
                double nuevoSaldo = saldoActual - monto;
                actualizarSaldo(usuarioId, nuevoSaldo);
            } else {
                throw new IllegalStateException("Saldo insuficiente");
            }
        } else {
            throw new IllegalStateException("El usuario no tiene cuenta corriente");
        }
    }

    // --- JDBC fallbacks when not running inside Spring context ---
    private CuentaCorriente getCuentaByUsuarioJdbc(int usuarioId) {
        String sql = "SELECT id, saldo FROM CuentaCorriente WHERE idUsuario = ?";
        try (Connection c = SQLPool.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CuentaCorriente cuenta = new CuentaCorriente(usuarioId);
                    cuenta.setId(rs.getInt("id"));
                    cuenta.setSaldo(rs.getDouble("saldo"));
                    return cuenta;
                }
            }
        } catch (SQLException e) {
            // log and ignore, return null to indicate not found
            System.err.println("Error JDBC getCuentaByUsuario: " + e.getMessage());
        }
        return null;
    }

    private void saveCuentaJdbc(CuentaCorriente cuenta) {
        System.out.println("Guardando cuenta via JDBC para usuario: " + cuenta.getUsuarioId());
        // Try update first
        String updateSql = "UPDATE CuentaCorriente SET saldo = ? WHERE idUsuario = ?";
        String insertSql = "INSERT INTO CuentaCorriente (idUsuario, saldo) VALUES (?, ?)";
        try (Connection c = SQLPool.getInstance().getConnection();
             PreparedStatement psUpdate = c.prepareStatement(updateSql)) {
            double saldoToSave = cuenta.getSaldo() == null ? 0.0 : cuenta.getSaldo();
            System.out.println("Intentando actualizar saldo a: " + saldoToSave);
            psUpdate.setDouble(1, saldoToSave);
            psUpdate.setInt(2, cuenta.getUsuarioId());
            int updated = psUpdate.executeUpdate();
            System.out.println("Filas actualizadas: " + updated);
            if (updated == 0) {
                System.out.println("No se actualizó ninguna fila, intentando insertar");
                try (PreparedStatement psInsert = c.prepareStatement(insertSql)) {
                    psInsert.setInt(1, cuenta.getUsuarioId());
                    psInsert.setDouble(2, saldoToSave);
                    psInsert.executeUpdate();
                    System.out.println("Nueva cuenta insertada correctamente");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error JDBC saveCuenta: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

