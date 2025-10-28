package app;

import com.mongodb.client.MongoDatabase;
import connections.CassandraPool;
import connections.MongoPool;
import connections.RedisPool;
import connections.SQLPool;
import exceptions.ErrorConectionMongoException;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws ErrorConectionMongoException, SQLException {
        System.out.println("Iniciando el TPO");

        CassandraPool cassandraPool = CassandraPool.getInstance();


        MongoPool mongoPool = MongoPool.getInstance();

        System.out.println("Conectado a las bases correctamente.");

        var dbMongo = mongoPool.getConnection();

        RedisPool redisPool = RedisPool.getInstance();


        SQLPool sqlPool = SQLPool.getInstance();


        System.out.println("Conectado a la base correctamente: " + dbMongo.getName());

        CassandraPool.close();
        mongoPool.close();

        System.out.println("Conexiones cerradas correctamente.");


    }
}
