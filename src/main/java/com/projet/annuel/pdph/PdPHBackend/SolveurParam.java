package com.projet.annuel.pdph.PdPHBackend;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SolveurParam {
	private int nb_semaine;
	private String input_file;
	private String input_data;
	private double hmax;
	private double hg_max;
	private double offd;
	private double reph;
	private boolean contrainte1;
	private boolean contrainte2;
	
	public SolveurParam(int nb_semaine, String input_filename, String input_data,double hmax, double hg_max, double offd, double reph, boolean contrainte1, boolean contrainte2) {
		super();
		this.nb_semaine = nb_semaine;
		this.input_file = input_filename;
		this.input_data = input_data;
		this.hmax = hmax;
		this.hg_max = hg_max;
		this.offd = offd;
		this.reph = reph;
		this.contrainte1 = contrainte1;
		this.contrainte2 = contrainte2;		
	}
	
	public String createInputFile(String inputData) {
		String file_path = "data/in_tmp/input.txt";
		String file_path_copy = "data/in/input"+ this.generateFileCoding() + ".txt";
		Path source = Paths.get(file_path);
		Path dest = Paths.get(file_path_copy);

	    String encoding = "UTF-8";
	    try {
		    PrintWriter writer = new PrintWriter(file_path, encoding);
		    System.out.print("fichier " + file_path + " crée\n");
		    writer.println(inputData);		    
		    writer.close();	
		    Files.copy(source, dest);
		    return file_path;
	    } catch (IOException e){
		      System.out.println("Erreur lors de la construction du fichier d'entrée !");
		      e.printStackTrace();
		      return null;
	    }
	}
	
	public void saveParametersOnFile(int nombreSemaine, double h_max, double hg_max2, double offD2, double reph2, boolean c1, boolean c2, String input_filename) {
		String file_path = "data/in_tmp/parameters.txt";
		String encoding = "UTF-8";
		
		try {
			PrintWriter writer = new PrintWriter(file_path, encoding);
			writer.println("nb_semaine:" + nombreSemaine);
			writer.println("h_max:" + nombreSemaine);
			writer.println("hg_max:" + hg_max2);
			writer.println("offD:" + offD2);
			writer.println("reph:" + reph2);
			writer.println("contrainte1:" + c1);
			writer.println("contrainte2:" + c2);
			writer.println("input_file:" + input_filename);
			writer.close();
		}catch(IOException e) {
			System.out.println("Erreur lors de la construction du fichier des paramètres !");
			e.printStackTrace();
		}
	}
	
	public String createOutputDirectory(String input_file_path) {
		String output_directory_path = "";
		String tab[] = input_file_path.split("/");
		
		for(int i = 0; i < tab.length - 2; i++) {
			output_directory_path += tab[i] + "/";
		}
		
		return output_directory_path + "out_tmp/";
	}
	
	public String generateFileCoding() {
		String codage = "";
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm-ss");  
		LocalDateTime now = LocalDateTime.now();  
		codage += dtf.format(now).toString().replaceAll("\\s+", "_");
		
		return codage;
	}

	public int getNb_semaine() {
		return nb_semaine;
	}

	public void setNb_semaine(int nb_semaine) {
		this.nb_semaine = nb_semaine;
	}

	

	public String getInput_file() {
		return input_file;
	}

	public void setInput_file(String input_file) {
		this.input_file = input_file;
	}

	public double getHmax() {
		return hmax;
	}

	public void setHmax(double hmax) {
		this.hmax = hmax;
	}

	public double getHg_max() {
		return hg_max;
	}

	public void setHg_max(double hg_max) {
		this.hg_max = hg_max;
	}

	public double getOffd() {
		return offd;
	}

	public void setOffd(double offd) {
		this.offd = offd;
	}

	public double getReph() {
		return reph;
	}

	public void setReph(double reph) {
		this.reph = reph;
	}

	public boolean isContrainte1() {
		return contrainte1;
	}

	public void setContrainte1(boolean contrainte1) {
		this.contrainte1 = contrainte1;
	}

	public boolean isContrainte2() {
		return contrainte2;
	}

	public void setContrainte2(boolean contrainte2) {
		this.contrainte2 = contrainte2;
	}

	public String getInput_data() {
		return input_data;
	}

	public void setInput_data(String input_data) {
		this.input_data = input_data;
	}
}
