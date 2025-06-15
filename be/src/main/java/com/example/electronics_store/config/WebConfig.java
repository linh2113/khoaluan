package com.example.electronics_store.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public MappingJackson2HttpMessageConverter jacksonConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        List<MediaType> mediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
        
        // Thêm application/octet-stream vào danh sách các MediaType được hỗ trợ
        mediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
        
        // Thêm text/plain để hỗ trợ JSON trong form-data
        mediaTypes.add(MediaType.TEXT_PLAIN);
        
        converter.setSupportedMediaTypes(mediaTypes);
        return converter;
    }
}