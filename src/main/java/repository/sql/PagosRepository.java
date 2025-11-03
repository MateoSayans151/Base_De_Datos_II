package repository.sql;

import entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagosRepository extends JpaRepository<Pago, Integer> {

    // Buscar pagos por ID de factura
    List<Pago> findByFactura_IdFactura(int idFactura);

    // Buscar pagos por método (por ejemplo "Tarjeta", "Efectivo", etc.)
    List<Pago> findByMetodoPago(String metodoPago);
}

