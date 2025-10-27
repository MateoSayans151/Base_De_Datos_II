package app;

import connections.CassandraPool;
import connections.MongoPool;
import connections.RedisPool;
import connections.SQLPool;
import exceptions.ErrorConectionMongoException;

public class Main {
    public static void main(String[] args) throws ErrorConectionMongoException {
        System.out.println("Iniciando el TPO");

        CassandraPool.connect("localhost",9042,"tpo_db");

        String mongoUri = "mongodb//localhost:27017/authSource=admin";
        MongoPool mongoPool = MongoPool.getInstancia(mongoUri);

        System.out.println("Conectado a las bases correctamente.");

        var db = mongoPool.getConnection("tpo_db");

        RedisPool.getInstance("localhost", 6379);

        String urlMySQL = "jdbc:postgresql://localhost:5432/tpo_db";
        SQLPool.getInstance(urlMySQL);

        System.out.println("Conectado a la base correctamente: " + db.getName());


        CassandraPool.close();
        mongoPool.close();

        System.out.println("Conexiones cerradas correctamente.");


    }
}
