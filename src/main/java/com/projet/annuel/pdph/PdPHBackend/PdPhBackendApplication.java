package com.projet.annuel.pdph.PdPHBackend;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.poi.sl.usermodel.ObjectMetaData.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class PdPhBackendApplication {
	
	public static void createDirectory(Path dir_path) throws IOException {
		
		if(!Files.exists(dir_path)) {
			Files.createDirectory(dir_path);
		}
	}

	public static void main(String[] args) throws IOException {
		//SpringApplication.run(PdPhBackendApplication.class, args);
		
		//creation des repertoires qui contiendront les fichiers d'entrée et sorties
		
		createDirectory(Paths.get("data/"));	
		createDirectory(Paths.get("data/in_tmp/"));
		createDirectory(Paths.get("data/in/"));
		createDirectory(Paths.get("data/out/"));
		createDirectory(Paths.get("data/out_tmp/"));
		
		
		SpringApplicationBuilder builder = new SpringApplicationBuilder(PdPhBackendApplication.class);

		builder.headless(false);

		ConfigurableApplicationContext context = builder.run(args);
	
	}

}
