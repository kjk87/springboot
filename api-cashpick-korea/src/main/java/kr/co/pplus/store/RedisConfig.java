package kr.co.pplus.store;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
//@PropertySource(value = {"classpath:application.yml"} ,
//        ignoreResourceNotFound = true, factory = YamlPropertySourceFactory.class)
public class RedisConfig {


//    @Value("${STORE.REDIS_HOSTNAME}")
//    private String REDIS_HOSTNAME = "";
//
//    @Value("${STORE.REDIS_PORT}")
//    private Integer REDIS_PORT = 6379;
//
//    @Value("${STORE.REDIS_USEPOOL}")
//    private String REDIS_USEPOOL = "";
//
//    @Value("${STORE.REDIS_PASSWORD}")
//    private String REDIS_PASSWORD = "";


//    @Bean
//    public JedisPoolConfig jedisPoolConfig() {
//        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
//        jedisPoolConfig.setMaxIdle(30);
//        jedisPoolConfig.setMinIdle(10);
//        jedisPoolConfig.setTestOnBorrow(true);
//        jedisPoolConfig.setTestOnReturn(true);
//        return jedisPoolConfig;
//    }


//    @Bean(name = "jedisConnectionFactory")
//    public JedisConnectionFactory jedisConnectionFactory() {
//
//        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(REDIS_HOSTNAME, REDIS_PORT);
//        redisStandaloneConfiguration.setPassword(RedisPassword.of(REDIS_PASSWORD));
//
//        return  new JedisConnectionFactory(redisStandaloneConfiguration);
//    }

//    @Bean(name = "stringRedisSerializer")
//    public StringRedisSerializer stringRedisSerializer(){
//        return new StringRedisSerializer();
//    }

//    @Bean(name = "redisTemplate")
//    public RedisTemplate<String , Object> redisTemplate() {
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(jedisConnectionFactory());
//        template.setKeySerializer(stringRedisSerializer());
//        template.setValueSerializer(stringRedisSerializer());
//        template.setHashKeySerializer(stringRedisSerializer());
//        template.setHashValueSerializer(stringRedisSerializer());
//        template.setEnableDefaultSerializer(false);
//        template.setEnableTransactionSupport(true);
//        return template;
//    }
}
