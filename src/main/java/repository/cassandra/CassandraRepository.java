<<<<<<< HEAD
package repository.cassandra;
=======
<<<<<<<< HEAD:src/main/java/repository/CassandraRepository.java
<<<<<<< Updated upstream:src/main/java/repository/CassandraRepository.java
<<<<<<< Updated upstream:src/main/java/repository/CassandraRepository.java
package repository.cassandra
=======
package repository.cassandra;
>>>>>>> Stashed changes:src/main/java/repository/cassandra/CassandraRepository.java
=======
package repository.cassandra;
>>>>>>> Stashed changes:src/main/java/repository/cassandra/CassandraRepository.java
========
package repository.CassandraRepository;
>>>>>>>> main:src/main/java/repository/cassandra/CassandraRepository.java
>>>>>>> main

public class CassandraRepository {
    private static CassandraRepository instance;

    private CassandraRepository() {}

    public static CassandraRepository getInstance(){
        if (instance == null)
            instance = new CassandraRepository();
        return instance;
    }
}
