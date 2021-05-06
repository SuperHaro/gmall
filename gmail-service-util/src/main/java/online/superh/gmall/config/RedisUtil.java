package online.superh.gmall.config;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @version: 1.0
 * @author: SuperH
 * @description:
 * @date: 2021-04-27 18:26
 */
public class RedisUtil {
    //创建连接池
    private JedisPool jedisPool;
    //host,port等参数可以配置在application.properties
    //初始化连接池
    public void initJedisPool(String host,int port,int database){
        //连接池配置类
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        //设置连接池的最大数
        jedisPoolConfig.setMaxTotal(200);
        //设置等待时间
        jedisPoolConfig.setMaxWaitMillis(10*1000);
        //最少剩余数
        jedisPoolConfig.setMinIdle(10);
        //当用户获取到一个连接池后，自检是否可以使用
        jedisPoolConfig.setTestOnBorrow(true);
        //开启缓冲池
        jedisPoolConfig.setBlockWhenExhausted(true);
        //连接池配置类，host,port,timeout,password
        jedisPool=new JedisPool(jedisPoolConfig,host,port,20*1000);
    }
    //获取jedis
    public Jedis getJedis(){
        Jedis jedis = jedisPool.getResource();
        return jedis;
    }
}
