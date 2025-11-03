package service;

import entity.CuentaCorriente;
import repository.sql.CuentaCorrienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CuentaCorrienteService {

    @Autowired
    private CuentaCorrienteRepository cuentaCorrienteRepository;

    public List<CuentaCorriente> getAll() {
        return cuentaCorrienteRepository.findAll();
    }

    public Optional<CuentaCorriente> getById(int id) {
        return cuentaCorrienteRepository.findById(id);
    }

    public CuentaCorriente save(CuentaCorriente cuentaCorriente) {
        return cuentaCorrienteRepository.save(cuentaCorriente);
    }

    public CuentaCorriente update(int id, CuentaCorriente cuentaCorriente) {
        Optional<CuentaCorriente> cuentaExistente = cuentaCorrienteRepository.findById(id);
        if (cuentaExistente.isPresent()) {
            CuentaCorriente cuenta = cuentaExistente.get();
            cuenta.setNumeroCuenta(cuentaCorriente.getNumeroCuenta());
            cuenta.setSaldo(cuentaCorriente.getSaldo());
            return cuentaCorrienteRepository.save(cuenta);
        }
        return null;
    }

    public void delete(int id) {
        cuentaCorrienteRepository.deleteById(id);
    }
}

