package service;

import entity.CuentaCorriente;
import entity.Usuario;
import exceptions.ErrorConectionMongoException;
import org.springframework.stereotype.Service;
import repository.mongo.UsuarioRepository;        
import repository.sql.CuentaCorrienteRepository;     

import java.util.List;
import java.util.Optional;

@Service
public class CuentaCorrienteService {

    private final CuentaCorrienteRepository cuentaCorrienteRepository;
    private final UsuarioRepository usuarioRepository;

    public CuentaCorrienteService(CuentaCorrienteRepository cuentaCorrienteRepository,
                                  UsuarioRepository usuarioRepository) {
        this.cuentaCorrienteRepository = cuentaCorrienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /* =========================== LISTAR =========================== */
    public List<CuentaCorriente> getAll() {
        return cuentaCorrienteRepository.findAll();
    }

    public Optional<CuentaCorriente> getById(int id) {
        return cuentaCorrienteRepository.findById(id);
    }

    public List<CuentaCorriente> getByUsuarioId(int usuarioId) {
        return cuentaCorrienteRepository.findByUsuario_Id(usuarioId);
    }

    public CuentaCorriente getByNumeroCuenta(String numero) {
        return cuentaCorrienteRepository.findByNumeroCuenta(numero);
    }

    /* =========================== CREAR =========================== */
    public CuentaCorriente create(CuentaCorriente cuentaCorriente, int usuarioId) {
        try {
            Usuario usuario = usuarioRepository.getUserById(usuarioId);
            // Tu repo retorna Usuario "vacío" si no existe => chequeo id==0
            if (usuario == null || usuario.getId() == 0) {
                throw new RuntimeException("No existe usuario con ID " + usuarioId);
            }
            cuentaCorriente.setUsuario(usuario);
            return cuentaCorrienteRepository.save(cuentaCorriente);
        } catch (ErrorConectionMongoException e) {
            throw new RuntimeException("Mongo: error obteniendo usuario " + usuarioId, e);
        }
    }

    /* =========================== ACTUALIZAR =========================== */
    public CuentaCorriente update(int id, CuentaCorriente nuevaCuenta, Integer nuevoUsuarioId) {
        Optional<CuentaCorriente> cuentaOpt = cuentaCorrienteRepository.findById(id);
        if (cuentaOpt.isEmpty()) {
            throw new RuntimeException("No existe cuenta con ID " + id);
        }

        CuentaCorriente cuenta = cuentaOpt.get();
        cuenta.setNumeroCuenta(nuevaCuenta.getNumeroCuenta());
        cuenta.setSaldo(nuevaCuenta.getSaldo());

        if (nuevoUsuarioId != null) {
            try {
                Usuario usuario = usuarioRepository.getUserById(nuevoUsuarioId);
                if (usuario == null || usuario.getId() == 0) {
                    throw new RuntimeException("No existe usuario con ID " + nuevoUsuarioId);
                }
                cuenta.setUsuario(usuario);
            } catch (ErrorConectionMongoException e) {
                throw new RuntimeException("Mongo: error obteniendo usuario " + nuevoUsuarioId, e);
            }
        }

        return cuentaCorrienteRepository.save(cuenta);
    }

    /* =========================== ELIMINAR =========================== */
    public void delete(int id) {
        if (!cuentaCorrienteRepository.existsById(id)) {
            throw new RuntimeException("No existe cuenta corriente con ID " + id);
        }
        cuentaCorrienteRepository.deleteById(id);
    }
}
