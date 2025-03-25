package com.example.B_MedApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.util.List;

@SpringBootApplication
public class BMedAppApplication {
	public static void main(String[] args) {
		SpringApplication.run(BMedAppApplication.class, args);
	}
	
	public void addCorsMappings(CorsRegistry registry) {
		// Permitir CORS para React Native en localhost:3000
		registry.addMapping("/**")
				.allowedOrigins("http://localhost:3000", "http://localhost:8081")  // Agregar las URL donde tu app está corriendo
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
				.allowedHeaders("*")
				.allowCredentials(true);
	}
}
