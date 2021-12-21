package com.projet.annuel.pdph.PdPHBackend;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
	public static String getContentSolution (String fileName) {
		ArrayList <Creneau> creneaux = readExcelFile(fileName);
		return new Gson().toJson(creneaux);
	}
	
	/**
	 * Méthode renvoyant le contenu de la solution sélectionnée (fichier xlsx)
	 * @param nom du fichier xlsx (solution)
	 * @return la solution sous format json 
	 */
	public static ArrayList <Creneau> readExcelFile(String filePath){
		try {
			FileInputStream excelFile = new FileInputStream(new File(filePath));
    		Workbook workbook = new XSSFWorkbook(excelFile);
     
    		Sheet sheet = workbook.getSheet("Creneaux");
    		Iterator<?> lignes = sheet.iterator();
    		
    		ArrayList <Creneau> listCreneaux = new ArrayList<Creneau>();
    		
    		int nb_ligne = 0;
    		while (lignes.hasNext()) {
    			Row current_ligne = (Row) lignes.next();
    			
    			if(nb_ligne == 0) {
    				nb_ligne++;
    				continue;
    			}
    			
    			Iterator<?> cellule_ligne = current_ligne.iterator();
 
    			String contrat = "";
    			String agent = "";
    			//ArrayList<String> postes = new ArrayList<String>();
    			
    			int index_cellule = 0;
    			while (cellule_ligne.hasNext()) {
    				Cell current_cellule = (Cell) cellule_ligne.next();
    				
    				if(index_cellule == 0) {
    					contrat = String.valueOf(current_cellule.getNumericCellValue());
    				} else if(index_cellule == 1) {
    					agent = current_cellule.getStringCellValue();
    				} //else if(index_cellule >= 2) {
    					//postes = postes.add(currentCell.getStringCellValue());
    				//}
    				index_cellule++;
    			} 
    			listCreneaux.add(new Creneau(contrat, agent));
    		}    		
    		workbook.close();    		
    		return listCreneaux;
    		
        } catch (IOException e) {
        	throw new RuntimeException("Erreur " + e.getMessage());
        }
	}

}
