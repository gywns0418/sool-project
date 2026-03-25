package com.example.sool.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


@Configuration
public class RedisConfig {

    //문자열 캐시용(이메일 인증)
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        //문자열 -> UTF-8 byte배열 / UTF-8 byte배열 -> 문자열
        return new StringRedisTemplate(connectionFactory);
    }

    //객체 캐시용(주류 TOP4)
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory){

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        //Redis 서버 연결
        redisTemplate.setConnectionFactory(connectionFactory);

        //Redis key 저장
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        //localdatetime 변환 오류로 변환 설정( created_at )
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        //객체 -> JSON / JSON -> 객체
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        redisTemplate.setValueSerializer(serializer);

        return redisTemplate;
    }
}