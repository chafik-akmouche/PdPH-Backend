package com.projet.annuel.pdph.PdPHBackend;

import java.util.ArrayList;

public class Creneau {
	private String contrat;
	private String agent;
	private ArrayList <String> postes = new ArrayList<>();
	
	public Creneau(String contrat, String agent, ArrayList <String> postes) {
		this.contrat = contrat;
		this.agent = agent;
		this.postes = postes;
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

	@Override
	public String toString() {
		return "Creneau [contrat=" + contrat + ", agent=" + agent + ", postes=" + postes + "]";
	}
	

}
