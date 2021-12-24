package com.projet.annuel.pdph.PdPHBackend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import java.util.Iterator;  
import org.apache.poi.ss.usermodel.Cell; 

import com.google.gson.Gson;

public class Response {
	
	/**
	 * Méthode renvoyant les noms des fichiers d'un répertoire
	 * @param chemin vers le répertoire cible (string)
	 * @return json contenant les noms des fichiers du répertoire 
	 */
	public static ArrayList<String> getSolutionNames (String path) {
		ArrayList<String> solutionsList = new ArrayList<String>();
		
		File[] files = new File(path).listFiles(); 
		for (File file : files) {
		    if (file.isFile()) {
		    	solutionsList.add(file.getName());
		    }
		}
		return solutionsList;
	}
	
	/**
	 * Méthode renvoyant le contenu de la solution sélectionnée (fichier xlsx)
	 * @param nom du fichier xlsx (solution)
	 * @return la solution sous format json 
	 * @throws IOException 
	 */
	public static ArrayList<Creneau> getContentSolution (String fileName) throws IOException {
		//tableau de creneau a retourner 
		ArrayList<Creneau> tab_creneaux = new ArrayList<>();
		
		//obtention des octets d'entrée d'un fichier  
		FileInputStream fis = new FileInputStream(new File(fileName)); 
		
		//création d'une instance de classeur faisant référence au fichier .xlsx
		XSSFWorkbook wb =  new  XSSFWorkbook(fis);  
		XSSFSheet sheet = wb.getSheetAt(0); //création d'un objet Sheet pour récupérer l'objet
		Iterator<Row> itr = sheet.iterator(); // itération sur un fichier excel 
		
		itr.next(); //ignoration de la première ligne
		
		while (itr.hasNext()) {
			Row row = itr.next(); 
			
			Iterator<Cell> cellIterator = row.cellIterator(); //itération dans les colonnes
			
			//creation d'une nouvelle instance de creneau 
			Creneau creneau = new Creneau();
			
			while (cellIterator.hasNext())   
			{  
				Cell cell = cellIterator.next();  
				int indice = cell.getColumnIndex();
				switch (indice)               
				{  
					case 0:    //field that represents string cell type  
						creneau.setContrat(row.getCell(indice).toString());
					break;  
					case 1: 
						creneau.setAgent(row.getCell(indice).toString());
					break;
				default:
						creneau.getPostes().add(row.getCell(indice).toString());
					break;
					
				}
			 }
			
			tab_creneaux.add(creneau);
			
		}
		
		
		
		tab_creneaux.remove(tab_creneaux.size()-1);
		return tab_creneaux;
	}
	
	public static int getParameters(String filename) throws IOException {
		int nb_semaine = 0;
		
		try {
			FileReader fr = new FileReader(filename);
			try (BufferedReader br = new BufferedReader(fr)) {
				String sb = new String();
				String line;
				while((line = br.readLine()) != null) {
					sb += line;
				}
				
				nb_semaine = Integer.parseInt(sb.split(";")[0]);
			}
		    
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
		
		return nb_semaine;
	}

}
