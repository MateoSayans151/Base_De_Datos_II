package connections;

import com.datastax.oss.driver.api.core.CqlSession;
import org.hibernate.boot.cfgxml.internal.ConfigLoader;

import java.net.InetSocketAddress;

public class CassandraPool {

    private static CqlSession session;

    public static void connect(String node, int port, String datacenter) {
        try{
            session = CqlSession.builder()
                    .addContactPoint(new InetSocketAddress(node, port))
                    .withLocalDatacenter(datacenter)
                    .build();

        }catch(Exception e){
            throw new RuntimeException("No se pudo conectar a Cassandra" + e.getMessage(), e);
        }

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
            sysout.println("Cassandra session closed.");
        }
    }
}