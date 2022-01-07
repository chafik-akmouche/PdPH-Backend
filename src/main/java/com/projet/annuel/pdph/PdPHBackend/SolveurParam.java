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
	private boolean contrainte11;
	private boolean contrainte12;
	private boolean contrainte13;
	private boolean contrainte14;
	private boolean contrainte15;
	
	
	
	public SolveurParam(int nb_semaine, String input_file, String input_data, boolean contrainte11,
			boolean contrainte12, boolean contrainte13, boolean contrainte14, boolean contrainte15) {
		super();
		this.nb_semaine = nb_semaine;
		this.input_file = input_file;
		this.input_data = input_data;
		this.contrainte11 = contrainte11;
		this.contrainte12 = contrainte12;
		this.contrainte13 = contrainte13;
		this.contrainte14 = contrainte14;
		this.contrainte15 = contrainte15;
	}

	public String createInputFile(String inputData) {
		String file_path = "data/in_tmp/input.txt";
		String file_path_copy = "data/in/input"+ this.generateFileCoding() + ".txt";
		Path source = Paths.get(file_path);
		Path dest = Paths.get(file_path_copy);

	    String encoding = "UTF-8";
	    try {
		    PrintWriter writer = new PrintWriter(file_path, encoding);
		    //System.out.print("fichier " + file_path + " crée\n");
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
	
	public void saveParametersOnFile(int nombreSemaine, boolean c11, boolean c12, boolean c13, boolean c14, boolean c15, String input_filename) {
		String file_path = "data/in_tmp/parameters.txt";
		String encoding = "UTF-8";
		
		try {
			PrintWriter writer = new PrintWriter(file_path, encoding);
			writer.println("nb_semaine:" + nombreSemaine);
			writer.println("contrainte11:" + c11);
			writer.println("contrainte12:" + c12);
			writer.println("contrainte13:" + c13);
			writer.println("contrainte14:" + c14);
			writer.println("contrainte15:" + c15);
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


	public String getInput_data() {
		return input_data;
	}

	public void setInput_data(String input_data) {
		this.input_data = input_data;
	}

	public boolean isContrainte11() {
		return contrainte11;
	}

	public void setContrainte11(boolean contrainte11) {
		this.contrainte11 = contrainte11;
	}

	public boolean isContrainte12() {
		return contrainte12;
	}

	public void setContrainte12(boolean contrainte12) {
		this.contrainte12 = contrainte12;
	}

	public boolean isContrainte13() {
		return contrainte13;
	}

	public void setContrainte13(boolean contrainte13) {
		this.contrainte13 = contrainte13;
	}

	public boolean isContrainte14() {
		return contrainte14;
	}

	public void setContrainte14(boolean contrainte14) {
		this.contrainte14 = contrainte14;
	}

	public boolean isContrainte15() {
		return contrainte15;
	}

	public void setContrainte15(boolean contrainte15) {
		this.contrainte15 = contrainte15;
	}

	@Override
	public String toString() {
		return "SolveurParam [nb_semaine=" + nb_semaine + ", input_file=" + input_file + ", input_data=" + input_data
				+ ", contrainte11=" + contrainte11 + ", contrainte12=" + contrainte12 + ", contrainte13=" + contrainte13
				+ ", contrainte14=" + contrainte14 + ", contrainte15=" + contrainte15 + "]";
	}
	
	
}
