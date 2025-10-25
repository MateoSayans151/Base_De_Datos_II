package connections;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;


import exceptions.ErrorConectionMongoException;


public class MongoPool {

	private static MongoPool instancia;
	private MongoClient mongoClient;
	
	private MongoPool(String url) {
		mongoClient = MongoClients.create(url);
	}
	
	public static MongoPool getInstancia(String url){
		if(instancia == null)
			instancia = new MongoPool(url);
		return instancia;
	}
	
	public MongoDatabase getConnection(String database) throws ErrorConectionMongoException {
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
