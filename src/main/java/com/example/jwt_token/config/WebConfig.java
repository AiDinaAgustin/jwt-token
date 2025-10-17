package com.example.jwt_token.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Akses URL: http://localhost:8080/uploads/questions/namafile.png
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
