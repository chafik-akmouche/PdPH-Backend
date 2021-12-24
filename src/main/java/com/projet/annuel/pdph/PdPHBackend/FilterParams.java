package com.projet.annuel.pdph.PdPHBackend;

import java.util.ArrayList;

public class FilterParams {
	private int nombreSemaine;
	private ArrayList<String> solutions;
	
	public FilterParams(int nombreSemaine, ArrayList<String> solutions) {
		super();
		this.nombreSemaine = nombreSemaine;
		this.solutions = solutions;
	}

	public int getNombreSemaine() {
		return nombreSemaine;
	}

	public void setNombreSemaine(int nombreSemaine) {
		this.nombreSemaine = nombreSemaine;
	}

	public ArrayList<String> getSolutions() {
		return solutions;
	}

	public void setSolutions(ArrayList<String> solutions) {
		this.solutions = solutions;
	}

	@Override
	public String toString() {
		return "FilterParams [nombreSemaine=" + nombreSemaine + ", solutions=" + solutions + "]";
	}
	
}
