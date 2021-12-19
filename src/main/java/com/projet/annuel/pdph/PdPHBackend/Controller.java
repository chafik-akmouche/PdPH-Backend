package com.projet.annuel.pdph.PdPHBackend;

import java.io.File;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParser;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class Controller {
	
	private CallSolver callSolver = new CallSolver();
	
	@GetMapping(path="/")
	public String test() {
		return "test ok";
	}
	
	@PostMapping(path="/callsolveur")
	public boolean getParameters(@RequestBody SolveurParam params) {
		int nb_semaine = params.getNb_semaine();
		double h_max = params.getHmax();
		double hg_max = params.getHg_max();
		double OffD = params.getOffd();
		double reph = params.getReph();
		boolean c1 = params.isContrainte1();
		boolean c2 = params.isContrainte2();
		System.out.println("////////////////////////////// "+params);
		String input_data = params.getInput_file();
		params.createInputFile(input_data);
		
		File file = new File("input.txt");
		String input_file = "";
		if(file.isFile()) { 
			input_file = "input.txt";
		}
		
		String output_directory = params.getOutput_directory();
		//callSolver.run(nb_semaine, h_max, hg_max, OffD, reph, c1, c2, input_file, output_directory);
		
		return true;
	}
}
