package com.projet.annuel.pdph.PdPHBackend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
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
	public FilterParams getDefaultSolutionsName() throws IOException {
		
		int nb_semaine = 0;
		//recupèration du nombre de semaine du dernier lancement de solveur
		String path = "data//in_tmp//parameters.txt";
		File paramsFile = new File(path);
		
		if(paramsFile.isFile()) {
			nb_semaine = Response.getParamsNombreSemaine(path);
		}
		
		ArrayList<String> solutions = Response.getSolutionNames("data//out_tmp");
		FilterParams filterParams = new FilterParams(nb_semaine,solutions);
		
		return filterParams;
	}
	
	
	@GetMapping(path="/parameters")
	public SolveurParam getParameters() throws IOException{
		SolveurParam sp = new SolveurParam(0, null, null, false, false, false, false, false);
		String path = "data//in_tmp//parameters.txt";
		File paramsFile = new File(path);
		
		if(paramsFile.isFile()) {
			try {
				FileReader fr = new FileReader(paramsFile);
				try (BufferedReader br = new BufferedReader(fr)) {
					String line;
					while((line = br.readLine()) != null) {
						String tab[] = line.split(":");
						if(tab[0].equals("nb_semaine")) 
							sp.setNb_semaine(Integer.parseInt(tab[1]));
						else if(tab[0].equals("input_file"))
							sp.setInput_file(tab[1]);
						else if(tab[0].equals("contrainte11"))
							sp.setContrainte11(Boolean.parseBoolean(tab[1]));
						else if(tab[0].equals("contrainte12"))
							sp.setContrainte12(Boolean.parseBoolean(tab[1]));
						else if(tab[0].equals("contrainte13"))
							sp.setContrainte13(Boolean.parseBoolean(tab[1]));
						else if(tab[0].equals("contrainte14"))
							sp.setContrainte14(Boolean.parseBoolean(tab[1]));
						else if(tab[0].equals("contrainte15"))
							sp.setContrainte15(Boolean.parseBoolean(tab[1]));
						;
					}
				}
			    
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		}
		
		return sp;
	}
	
	@PostMapping(path="/callsolveur")
	public ArrayList<String> getParameters(@RequestBody SolveurParam params) throws IOException {
		int nb_semaine = params.getNb_semaine();
		String input_filename = params.getInput_file();
		//récuperation des données d'entrée 
		String input_data = params.getInput_data();
		boolean contrainte11 = params.isContrainte11();
		boolean contrainte12 = params.isContrainte12();
		boolean contrainte13 = params.isContrainte13();
		boolean contrainte14 = params.isContrainte14();
		boolean contrainte15 = params.isContrainte15();
		
		//sauvegarde des paramètres dans un fichier
		params.saveParametersOnFile(nb_semaine,contrainte11,contrainte12,contrainte13,contrainte14,contrainte15,input_filename);
		
		
		
		//creation du fichier d'entrée
		String input_file = params.createInputFile(input_data);
		
		//creation du répertoire de sortie temporaire
		String output_directory = params.createOutputDirectory(input_file);
		
		callSolver.run(nb_semaine, contrainte11,contrainte12,contrainte13,contrainte14,contrainte15, input_file, output_directory);
		
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
		ArrayList<String> solutions = Response.getSolutionNames(output_directory);
	
		return solutions;
	}
	
	@GetMapping("/solution")
	public List<Creneau> getSolution(@RequestParam String name) throws IOException {
		return Response.getContentSolution("data\\out_tmp\\" + name);
	}
	
}
