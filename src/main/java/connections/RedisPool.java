package connections;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPool {
    private static RedisPool instance;
    private static JedisPool jedisPool;

    private RedisPool(String host, int port) {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(20);
        jedisPoolConfig.setMaxIdle(10);
        jedisPoolConfig.setMinIdle(2);
        jedisPool = new JedisPool(jedisPoolConfig, host, port);

    }

    public static RedisPool getInstance(String host, int port) {
        if (instance == null)
            instance = new RedisPool(host, port);
        return instance;
    }
    public void close(){
        if (jedisPool != null){
            jedisPool.close();
            System.out.println("Conexión de Redis cerrada.");
        }
    }

}
