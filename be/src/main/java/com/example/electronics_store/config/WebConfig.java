package com.example.electronics_store.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


 @Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Bean
    public MappingJackson2HttpMessageConverter jacksonConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        List<MediaType> mediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());

        // Thêm application/octet-stream vào danh sách các MediaType được hỗ trợ
        mediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);

        // Thêm text/plain để hỗ trợ JSON trong form-data
        mediaTypes.add(MediaType.TEXT_PLAIN);

        converter.setSupportedMediaTypes(mediaTypes);

        // Tạo JavaTimeModule với LocalDateTimeSerializer tùy chỉnh
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(new LocalDateTimeSerializer(
                DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));

        // Cấu hình ObjectMapper
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
                .modules(javaTimeModule)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();

        converter.setObjectMapper(objectMapper);
        return converter;
    }
}

