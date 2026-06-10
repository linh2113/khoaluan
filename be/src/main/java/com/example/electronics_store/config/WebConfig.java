package com.example.electronics_store.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.TimeZone;



@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final String OUT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Bean
    public MappingJackson2HttpMessageConverter jacksonConverter() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        // Giữ thêm media types như bạn đang có
        var mediaTypes = new java.util.ArrayList<>(converter.getSupportedMediaTypes());
        mediaTypes.add(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM);
        mediaTypes.add(org.springframework.http.MediaType.TEXT_PLAIN);
        converter.setSupportedMediaTypes(mediaTypes);

        // Serializer cố định (server trả về)
        var serializer = new com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer(
                java.time.format.DateTimeFormatter.ofPattern(OUT_FORMAT));

        // Deserializer linh hoạt (server nhận vào)
        var flexibleDeserializer = new FlexibleLocalDateTimeDeserializer();

        var javaTimeModule = new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule();
        javaTimeModule.addSerializer(java.time.LocalDateTime.class, serializer);
        javaTimeModule.addDeserializer(java.time.LocalDateTime.class, flexibleDeserializer);

        com.fasterxml.jackson.databind.ObjectMapper mapper = new org.springframework.http.converter.json.Jackson2ObjectMapperBuilder()
                .featuresToDisable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .modules(javaTimeModule)
                .build();

        // (Tuỳ chọn) đặt timezone mặc định cho các kiểu có timezone
        mapper.setTimeZone(java.util.TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

        converter.setObjectMapper(mapper);
        return converter;
    }

    /**
     * Deserializer chấp nhận nhiều format LocalDateTime phổ biến.
     */
    static class FlexibleLocalDateTimeDeserializer extends com.fasterxml.jackson.databind.JsonDeserializer<java.time.LocalDateTime> {
        private static final java.util.List<java.time.format.DateTimeFormatter> CANDIDATES = java.util.List.of(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
        );

        @Override
        public java.time.LocalDateTime deserialize(com.fasterxml.jackson.core.JsonParser p,
                                                   com.fasterxml.jackson.databind.DeserializationContext ctxt)
                throws java.io.IOException {
            String text = p.getText();
            if (text == null || text.isBlank()) return null;

            text = text.trim();

            // Thử lần lượt các pattern phổ biến
            for (var f : CANDIDATES) {
                try {
                    return java.time.LocalDateTime.parse(text, f);
                } catch (java.time.format.DateTimeParseException ignored) {}
            }

            // Thử thêm trường hợp có offset/Z → quy về LocalDateTime theo timezone hệ thống
            try {
                return java.time.OffsetDateTime.parse(text).toLocalDateTime();
            } catch (Exception ignored) {}

            try {
                return java.time.Instant.parse(text)
                        .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
            } catch (Exception ignored) {}

            throw new com.fasterxml.jackson.databind.exc.InvalidFormatException(
                    p, "Unsupported LocalDateTime format: " + text, text, java.time.LocalDateTime.class);
        }
    }
}


