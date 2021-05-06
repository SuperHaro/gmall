package online.superh.gmall.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @version: 1.0
 * @author: SuperH
 * @description:
 * @date: 2021-04-27 18:46
 */
@Configuration
public class RedisConfig {
    //disable表示如果未从配置文件中获取，则默认为disable
    @Value("${spring.redis.host:disable}")
    private String host;

    @Value("${spring.redis.port:6379}")
    private int port;

    @Value("${spring.redis.database:0}")
    private int database;

    @Bean //相当于在xml中创建了一个<bean>标签
    /*
    <beans>
        <bean id="redisUtil" class="com.atguigu.gmall0218.config.RedisUtil">
            <property name="host",value="192..168.67.219">
            <property name="port" value="6379">
            <property name="database" value="0">
        </bean>
    </beans>
    */
    public RedisUtil getRredisUtil(){
        if("disable".equals(host)){
            return null;
        }
        RedisUtil redisUtil = new RedisUtil();
        //调用initJedisPool方法将值传入
        redisUtil.initJedisPool(host,port,database);
        return redisUtil;
    }


}
