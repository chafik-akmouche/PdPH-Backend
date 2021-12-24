package com.projet.annuel.pdph.PdPHBackend;

import java.util.ArrayList;

public class Creneau {
	private String contrat;
	private String agent;
	private ArrayList <String> postes = new ArrayList<>();
	private String color;
	
	public Creneau(String contrat, String agent, ArrayList <String> postes , String color) {
		this.contrat = contrat;
		this.agent = agent;
		this.postes = postes;
		this.color = color;
	}

	public Creneau() {
		super();
	}

	public String getContrat() {
		return contrat;
	}

	public void setContrat(String contrat) {
		this.contrat = contrat;
	}

	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public ArrayList<String> getPostes() {
		return postes;
	}

	public void setPostes(ArrayList<String> postes) {
		this.postes = postes;
	}
	

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	@Override
	public String toString() {
		return "Creneau [contrat=" + contrat + ", agent=" + agent + ", postes=" + postes + ", color=" + color + "]";
	}
	
}
