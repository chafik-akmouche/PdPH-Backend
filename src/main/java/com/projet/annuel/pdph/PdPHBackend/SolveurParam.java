package com.projet.annuel.pdph.PdPHBackend;

public class SolveurParam {
	private int nb_semaine;
	private String input_file;
	private String output_directory;
	private double hmax;
	private double hg_max;
	private double offd;
	private double reph;
	private boolean contrainte1;
	private boolean contrainte2;
	
	public SolveurParam(int nb_semaine, String input_file, String output_directory,double hmax, double hg_max, double offd, double reph,
			boolean contrainte1, boolean contrainte2) {
		super();
		this.nb_semaine = nb_semaine;
		this.input_file = input_file;
		this.output_directory = output_directory;
		this.hmax = hmax;
		this.hg_max = hg_max;
		this.offd = offd;
		this.reph = reph;
		this.contrainte1 = contrainte1;
		this.contrainte2 = contrainte2;
	}

	@Override
	public String toString() {
		return "SolveurParam [nb_semaine=" + nb_semaine + ", input_file=" + input_file + ", output_directory=" + output_directory + ", hmax=" + hmax + ", hg_max="
				+ hg_max + ", OffD=" + this.offd + ", Reph=" + this.reph + ", contrainte1=" + contrainte1 + ", contrainte2="
				+ contrainte2 + "]";
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

	public String getOutput_directory() {
		return output_directory;
	}

	public void setOutput_directory(String output_directory) {
		this.output_directory = output_directory;
	}
	
	
}
