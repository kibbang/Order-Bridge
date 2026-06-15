package hello.orderbridge.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;

@Configuration
@EnableCaching // @Cacheable 활성화
public class RedisConfig {

    public static final String CACHE_ORDER = "order";
    public static final String CACHE_ORDERS = "orders";
    public static final String CACHE_ORDERS_ALL_KEY = CACHE_ORDERS + "::all";
    public static final String COLLECTED_KEY_PREFIX = "collected-orders:";
    public static final String CONSUMED_KEY_PREFIX = "consumed:order:";

    private final GenericJacksonJsonRedisSerializer jsonSerializer = new GenericJacksonJsonRedisSerializer(JsonMapper.builder().build());
    /**
     * 수동 조작용 RedisTemplate
     * - 중복 수집 방지 (Set 자료구조)
     * - 멱등성 마킹 (String 자료구조)
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // Key: 문자열 (redis-cli에서 읽기 쉽게)
        template.setKeySerializer(new StringRedisSerializer());
        // Value: JSON
        template.setValueSerializer(jsonSerializer);
        // Hash도 동일하게
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jsonSerializer);

        return template;
    }

    /**
     * @Cacheable용 CacheManager
     * - 캐시별 TTL, 직렬화 방식 설정
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer)
                )
                .disableCachingNullValues();// null은 캐싱 안함

        return RedisCacheManager
                .builder(redisConnectionFactory)
                .cacheDefaults(defaultCacheConfig)
                .build();
    }
}