package com.example.electronics_store.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);

        // Bật type info để tránh deserialize thành LinkedHashMap
        var ptv = com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
        .builder()
        .allowIfSubType("com.example")
        .allowIfSubType("org.springframework.data.domain")
        .allowIfSubType("java.util")
        .allowIfSubType("java.lang")
        .build();
        mapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL);
        return mapper;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory cf, ObjectMapper redisObjectMapper) {
        var keySer = new StringRedisSerializer();
        var valueSer = new GenericJackson2JsonRedisSerializer(redisObjectMapper);

        RedisCacheConfiguration defaultCfg = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .entryTtl(Duration.ofMinutes(30))
                .computePrefixWith(name -> "cache:v10:" + name + ":") // đổi prefix
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSer));

        Map<String, RedisCacheConfiguration> regions = new HashMap<>();
        regions.put("products", defaultCfg.entryTtl(Duration.ofMinutes(30)));
        regions.put("productLists", defaultCfg.entryTtl(Duration.ofMinutes(5)));
        regions.put("categories", defaultCfg.entryTtl(Duration.ofHours(12)));
        regions.put("brands", defaultCfg.entryTtl(Duration.ofHours(12)));
        regions.put("brandNames", defaultCfg.entryTtl(Duration.ofHours(12)));
        regions.put("discounts", defaultCfg.entryTtl(Duration.ofMinutes(5)));
        regions.put("flashSales", defaultCfg.entryTtl(Duration.ofMinutes(2)));

        return RedisCacheManager.builder(cf)
                .cacheDefaults(defaultCfg)
                .withInitialCacheConfigurations(regions)
                .transactionAware()
                .build();
    }
}

