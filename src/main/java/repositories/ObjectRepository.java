package repositories;

import javax.persistence.EntityManager;

import connections.ObjectDBPool;
import modelo.Cliente;

public class ObjectRepository {
	
	private static ObjectRepository instance;
	
	private ObjectRepository() {}
	
	public static ObjectRepository getInstance() {
		if(instance == null)
			instance = new ObjectRepository();
		return instance;
	}

	public void guardarCliente(Cliente c) {
		EntityManager em = ObjectDBPool.getInstance().getEntityManager();
		em.getTransaction().begin();
		em.persist(c);
		em.getTransaction().commit();
	}
}
