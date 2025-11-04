package connections;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import utilities.Config;

import java.sql.Connection;

public class RedisPool {
    private static RedisPool instance;
    private static JedisPool jedisPool;

    private RedisPool() {
        Config config = Config.getInstance();
        String host = config.getProperty("redis.host");
        int port = Integer.parseInt(config.getProperty("redis.port"));

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(20);
        jedisPoolConfig.setMaxIdle(10);
        jedisPoolConfig.setMinIdle(2);
        jedisPool = new JedisPool(jedisPoolConfig, host, port);

    }

    public static RedisPool getInstance() {
        if (instance == null)
            instance = new RedisPool();
        return instance;
    }

    public Jedis getConnection() {
        return jedisPool.getResource();
    }
    public void close(){
        if (jedisPool != null){
            jedisPool.close();
            System.out.println("Conexión de Redis cerrada.");
        }
    }

}
