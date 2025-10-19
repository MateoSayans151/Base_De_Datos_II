package connections;

import com.datastax.oss.driver.api.core.CqlSession;
import java.net.InetSocketAddress;

public class CassandraPool {

    private static CqlSession session;

    public static void connect(String node, int port, String datacenter) {
        session = CqlSession.builder()
                .addContactPoint(new InetSocketAddress(node, port))
                .withLocalDatacenter(datacenter)
                .build();
    }

    public static CqlSession getSession() {
        if (session == null) {
            throw new IllegalStateException("Cassandra session is not initialized. Call connect() first.");
        }
        return session;
    }

    public static void close() {
        if (session != null) {
            session.close();
        }
    }
}