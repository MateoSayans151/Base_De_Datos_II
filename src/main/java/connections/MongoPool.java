package connections;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;


import exceptions.ErrorConectionMongoException;
import utilities.Config;

public class MongoPool {

	private static MongoPool instance;
	private MongoClient mongoClient;

	private MongoPool() {
        Config config = Config.getInstance();
        String uri = config.getProperty("mongodb.uri");
        mongoClient = MongoClients.create(uri);
        System.out.println("MongoDB inicializado.");
	}
	
	public static MongoPool getInstance(){
		if(instance == null)
			instance = new MongoPool();
		return instance;
	}
	
	public MongoDatabase getConnection() throws ErrorConectionMongoException {
		Config config = Config.getInstance();
        String database = config.getProperty("mongodb.database");
        try {
			MongoDatabase db = mongoClient.getDatabase(database);
			return db;
		}
		catch (Exception e) {
			throw new ErrorConectionMongoException("Error en la conexión a MongoDB");
		}
	}
    public void close(){
        if(mongoClient != null) {
            mongoClient.close();
            System.out.println("La conexión con MongoDB cerró.");
        }
    }
}
