package com.projet.annuel.pdph.PdPHBackend;

import com.projet.annuel.pdph.solveur.Solver;

public class CallSolver {
	
	//Input data
	double h_max=45;//Nombre max d'heures par semaine (**)
	double hg_max=48;//Nombre max d'heures par 7 jours glissants (**)
	double OffD=12;//Nombre min d'heures de repos (**)
	double RepH=36;//Nombre min d'heures de repos hebdomadaire (**)
	double Ratio_base[] = {1,0.9,0.8,0.75,0.7,0.6,0.5};//Pourcentages des contrats (**)
	double Ratio_dim_base[] = {1,1,1,0.75,0.75,0.6,0.6};//Pourcentages des dimanches par contrat  (**)
	int CDmax=5;//Maximum jours de travail consecutifs
	
	//Liste de contraintes à prendre en compte
			//Règlementaires
	//boolean Constrainte1=true; 	//1 Respect Besoins (**) (**)
	//boolean Constrainte2=true; 	//2 Un poste par jour (**)
	boolean Constrainte3=true;	//3 Temps travail par sem
	boolean Constrainte4=true;	//4 Temps travail gliss
	boolean Constrainte5=true; 	//5 Max heures trav
	boolean Constrainte6=true; 	//6 Min heures trav
	boolean Constrainte7=true;	//7 Rep journalier
	boolean Constrainte8=true;	//8 Rep Hebdo
	boolean Constrainte9=true;	//9 Repos 1 week/2
	boolean Constrainte10=true;	// 10 Ratio dimanche

	//Qualité de vie
	boolean Constrainte11=true; 	//11 Min jours consecutifs
	boolean Constrainte12=true; 	//13 Max jours consecutifs
	boolean Constrainte13=true; 	//14 samedi et dimanche le même poste
	boolean Constrainte14=true; 	//15 Bloc
	boolean Constrainte15=true; 	//16 UB Jca
	boolean Constrainte16=true; 	//17 No Jca le weekend si pas besoin

	//Objectifs
	boolean obj1=true;
	boolean obj2=true;
	boolean obj3=true;
	
	int time_limit[]= {1800,1200,600}; //Temps calcul par objectif time_limit[]={temps objectif 1,temps objectif 2,temps objectif 3};

	//Nombre de semaines de cycle
	int nb_semaines=2;

	private Solver solver;
	
	

	public CallSolver() {
		super();
	}
	
	public void run(int nombre_semaine, double hmax,double hgmax, double offd,double reph,boolean c1, boolean c2,String input_file,String output_directory) {
		//modification des contraintes réglementaire
		// instanciation du solver et son déclenchement sur l'ensemble des contrainte et information d'entrée
		
		
		this.solver = new Solver(nombre_semaine,hmax,hgmax,offd,reph,Ratio_base, Ratio_dim_base, CDmax,
		c1, c2, Constrainte3, Constrainte4, Constrainte5, Constrainte6,
		Constrainte7, Constrainte8, Constrainte9, Constrainte10, Constrainte11, Constrainte12,
		Constrainte13, Constrainte14, Constrainte15, Constrainte16, obj1, obj2, obj3,time_limit,input_file,output_directory);
		//this.solver.pack();
		//this.solver.setVisible(true);
	}
}
