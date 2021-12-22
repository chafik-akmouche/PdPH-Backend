package com.projet.annuel.pdph.PdPHBackend;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.nio.file.Path;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class Controller {
	
	private CallSolver callSolver = new CallSolver();
	
	@GetMapping(path="/")
	public String test() {
		return "test ok";
	}
	
	@PostMapping(path="/callsolveur")
	public String getParameters(@RequestBody SolveurParam params) throws IOException {
		int nb_semaine = params.getNb_semaine();
		double h_max = params.getHmax();
		double hg_max = params.getHg_max();
		double OffD = params.getOffd();
		double reph = params.getReph();
		boolean c1 = params.isContrainte1();
		boolean c2 = params.isContrainte2();
		
		//récuperation des données d'entrée 
		String input_data = params.getInput_file();
		
		//creation du fichier d'entrée
		String input_file = params.createInputFile(input_data);
		
		//creation du répertoire de sortie temporaire
		String output_directory = params.createOutputDirectory(input_file);
		
		callSolver.run(nb_semaine, h_max, hg_max, OffD, reph, c1, c2, input_file, output_directory);
		
		//creation d'un nouveau répertoire de sortie absolu
		String out = "data/out/out" + params.generateFileCoding() + "/";
		
		Path out_path = Paths.get(out);	
		Files.createDirectory(out_path);

		File[] files = new File(output_directory).listFiles(); 
		for (File file : files) {
		    if (file.isFile()) {
		        Files.copy(file.toPath(),Paths.get(out_path + "\\" + file.getName()));
		    }
		}
		
		
		/*
		 * Cet appel doit être fait à la fin de l'exécution du solveur
		 * L'objet Json contenant la liste des solutions doit être envoyé au front
		 */
		 String solutions = Response.getSolutionNames(output_directory);
	
		return solutions;
	}
	
	@GetMapping("/solution")
	public ArrayList<Creneau> getSolution(@RequestParam String name) throws IOException {
		return Response.getContentSolution(name);
	}
	
}
