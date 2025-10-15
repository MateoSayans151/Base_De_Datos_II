package connections;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class ObjectDBPool {
	
	private static ObjectDBPool instance;
	private final String DATABASE_NAME = "catalogo.odb";
	private EntityManagerFactory emf;
	
	private  ObjectDBPool() {
		emf = Persistence.createEntityManagerFactory(DATABASE_NAME);
	} 
	
	public static ObjectDBPool getInstance() {
		if(instance == null)
			instance = new ObjectDBPool();
		return instance;
	}
	
	public EntityManager getEntityManager() {
		return emf.createEntityManager();
	}
	
}
