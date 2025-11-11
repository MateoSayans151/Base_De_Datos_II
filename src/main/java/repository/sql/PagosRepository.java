package repository.sql;

import entity.Pago;
import java.util.ArrayList;
import java.util.List;

/**
 * Stub implementation of PagosRepository to keep the app compilable while
 * payment/factura functionality is paused. Methods return no-op or empty results.
 */
public class PagosRepository {
    private static PagosRepository instance;

    private PagosRepository() {}

    public static PagosRepository getInstance() {
        if (instance == null) instance = new PagosRepository();
        return instance;
    }

    public Pago registrarPago(Pago pago) {
        // Do not persist; return the same pago (id will remain default)
        return pago;
    }

    public List<Pago> getPagosByUsuario(int usuarioId) {
        return new ArrayList<>(); // return empty list while payments paused
    }
}

