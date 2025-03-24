package com.example.B_MedApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class BMedAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(BMedAppApplication.class, args);
	}
		public List<String> getList(){
			return List.of(
					"Samuel",
					"Adrian"
			);
		}
}
