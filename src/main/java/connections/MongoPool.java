package connections;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import exceptions.ErrorConectionMongoException;


public class MongoPool {

	private static MongoPool instancia;
	private String url ;
	private MongoClient mongoClient;
	
	private MongoPool() {
		url = "mongodb://127.0.01:27017";
		mongoClient = MongoClients.create(url);
	}
	
	public static MongoPool getInstancia(){
		if(instancia == null)
			instancia = new MongoPool();
		return instancia;
	}
	
	public MongoDatabase getConection(String database) throws ErrorConectionMongoException {
		try {
			MongoDatabase db = mongoClient.getDatabase(database);
			return db;
		}
		catch (Exception e) {
			throw new ErrorConectionMongoException("Error ene la coneccxion a MongoDB");
		}
	}
}
