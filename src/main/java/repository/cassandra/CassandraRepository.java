package repository.CassandraRepository;

public class CassandraRepository {
    private static CassandraRepository instance;

    private CassandraRepository() {}

    public static CassandraRepository getInstance(){
        if (instance == null)
            instance = new CassandraRepository();
        return instance;
    }
}
