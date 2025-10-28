package connections;

import com.datastax.oss.driver.api.core.CqlSession;
import utilities.Config;

import java.net.InetSocketAddress;

public class CassandraPool {

    private static CassandraPool instance;
    private static CqlSession session;


    private CassandraPool() {
        Config config = Config.getInstance();
        String node = config.getProperty("cassandra.node");
        int port = Integer.parseInt(config.getProperty("cassandra.port"));
        String datacenter = config.getProperty("cassandra.datacenter");
        try{
            session = CqlSession.builder()
                    .addContactPoint(new InetSocketAddress(node, port))
                    .withLocalDatacenter(datacenter)
                    .build();

        }catch(Exception e){
            throw new RuntimeException("No se pudo conectar a Cassandra" + e.getMessage(), e);
        }

    }
    public static CassandraPool getInstance() {
        if (instance == null){
            instance = new CassandraPool();
            System.out.println("Cassandra inicializado.");
        }
        return instance;
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
            System.out.println("Cassandra session closed.");
        }
    }
}