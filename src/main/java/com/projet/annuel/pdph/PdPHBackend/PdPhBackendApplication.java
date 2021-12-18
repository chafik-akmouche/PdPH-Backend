package com.projet.annuel.pdph.PdPHBackend;

import org.apache.poi.sl.usermodel.ObjectMetaData.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class PdPhBackendApplication {

	public static void main(String[] args) {
		//SpringApplication.run(PdPhBackendApplication.class, args);
		
		SpringApplicationBuilder builder = new SpringApplicationBuilder(PdPhBackendApplication.class);

		builder.headless(false);

		ConfigurableApplicationContext context = builder.run(args);
	}

}
