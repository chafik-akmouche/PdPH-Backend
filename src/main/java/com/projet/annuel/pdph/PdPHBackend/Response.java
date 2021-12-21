package com.projet.annuel.pdph.PdPHBackend;

import java.io.File;
import java.util.ArrayList;
import com.google.gson.Gson;

public class Response {
	
	/**
	 * Méthode renvoyant les noms des fichiers d'un répertoire
	 * @param chemin vers le répertoire cible (string)
	 * @return json contenant les noms des fichiers du répertoire 
	 */
	public static String getSolutionNames (String path) {
		ArrayList<String> solutionsList = new ArrayList<String>();
		
		File[] files = new File(path).listFiles(); 
		for (File file : files) {
		    if (file.isFile()) {
		    	solutionsList.add(file.getName());
		    }
		}
		return new Gson().toJson(solutionsList);
	}
	
	/**
	 * Méthode renvoyant le contenu de la solution sélectionnée (fichier xlsx)
	 * @param nom du fichier xlsx (solution)
	 * @return la solution sous format json 
	 */
	public String getContentSolution (String fileName) {
		
		return null;
	}

}
