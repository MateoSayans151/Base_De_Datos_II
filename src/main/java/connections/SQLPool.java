package connections;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;

public class SQLPool {

    private static SQLPool instance;
    private final String url;
    private final String user = "admin";
    private final String password = "admin";

    private SQLPool(String url){
        this.url = url;
    }
    public static  SQLPool getInstance(String url) {
        if (instance == null) {
            instance = new SQLPool(url);
            System.out.println("SQL inicializado.");
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return java.sql.DriverManager.getConnection(url, user, password); // Después si quiero puedo agregarle un try catch
    }

    public void close(Connection connection) {
        if(connection != null){
            try{connection.close();}catch(SQLException e){}
        }
    }
}
