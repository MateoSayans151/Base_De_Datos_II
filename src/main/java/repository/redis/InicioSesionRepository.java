package repository.redis;

import connections.RedisPool;
import entity.Usuario;
import org.json.JSONException;
import org.json.JSONObject;
import redis.clients.jedis.JedisPool;

public class InicioSesionRepository {
    private static InicioSesionRepository instance;
    private final int TTLSESSION = 3600;

    private InicioSesionRepository() {}
    public static InicioSesionRepository getInstance() {
        if (instance == null) {
            instance = new InicioSesionRepository();
        }
        return instance;
    }

    public void crearSesion(String token, Usuario user){
        RedisPool redisPool = RedisPool.getInstance();
        var connection = redisPool.getConnection();
        try{
            JSONObject session = new JSONObject();
            session.put("id",user.getId());
            session.put("rol",user.getRol());
            session.put("timpestamp",System.currentTimeMillis());
            connection.setex(token, TTLSESSION,session.toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public JSONObject getSesion(String token){
        RedisPool redisPool = RedisPool.getInstance();
        var connection = redisPool.getConnection();
        JSONObject session = null;
        try{
            String sessionData = connection.get(token);
            if(sessionData != null){
                session = new JSONObject(sessionData);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return session;
    }
    public void eliminarSesion(String token){
        RedisPool redisPool = RedisPool.getInstance();
        var connection = redisPool.getConnection();
        connection.del(token);
    }
}
