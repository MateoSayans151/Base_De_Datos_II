package connections;

import utilities.Config;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLPool {

    private static SQLPool instance;
    private final String url;
    private final String user ;
    private final String password ;


    private SQLPool(){
        Config config = Config.getInstance();
        this.url = config.getProperty("sql.url");
        this.user = config.getProperty("sql.user");
        this.password = config.getProperty("sql.password");
    }
    public static  SQLPool getInstance() {
        if (instance == null) {
            instance = new SQLPool();
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
