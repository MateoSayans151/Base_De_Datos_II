package repositories;	

public class MongoRepositoy {
	
	private static MongoRepositoy instance;
	
	private MongoRepositoy() {}
	
	public static MongoRepositoy getInstance() {
		if(instance == null)
			instance = new MongoRepositoy();
		return instance;
	}

}
