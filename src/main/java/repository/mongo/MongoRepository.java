package repository.mongo;	

public class MongoRepository {
	
	private static MongoRepository instance;
	
	private MongoRepository() {}
	
	public static MongoRepository getInstance() {
		if(instance == null)
			instance = new MongoRepository();
		return instance;
	}

}
