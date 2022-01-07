package com.projet.annuel.pdph.solveur;

import java.util.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class Solver extends JFrame {

	private String input_route;
	private String out;
	private static Boolean res = false;
	private static boolean foundSolution;

	public Solver(int nb_semaines, double h_max, double hg_max, double OffD, double RepH, double Ratio_base[],
			double Ratio_dim_base[], int CDmax, boolean Constrainte1, boolean Constrainte2, boolean Constrainte3,
			boolean Constrainte4, boolean Constrainte5, boolean Constrainte6, boolean Constrainte7,
			boolean Constrainte8, boolean Constrainte9, boolean Constrainte10, boolean Constrainte11,
			boolean Constrainte12, boolean Constrainte13, boolean Constrainte14, boolean Constrainte15,
			boolean Constrainte16, boolean obj1, boolean obj2, boolean obj3, int time_limit[], String in, String out) {

		// Dossier de l'input

		// Scanner e1=new Scanner(System.in);
		// System.out.println("Collez la route de votre fichier txt d'input (par exemple
		// C:\\Users\\fichier.txt)");
		// String input_route=e1.next();//route pour r�cup�rer le ficher d'input

		// Dossier de l'output
		// Scanner e3=new Scanner(System.in);
		// System.out.println("Collez la route pour votre fichier de sortie (par exemple
		// C:\\Users)");
		// String out=e3.next();//route pour r�cup�rer les r�sultats

		// e3.close();
		// e1.close();

		// Creation d'un dossier pour les logs

		/** initialisation du fichier input et dossier output du solver **/
		this.input_route = in;
		this.out = out;

		File log_dossier = new File(out + "\\Log");
		if (!log_dossier.exists()) {
			if (log_dossier.mkdirs()) {
				System.out.println("Creation de dossier pour le log");
			} else {
				System.out.println("Error ###########################################################");
			}
		}

		// Warm start
		boolean warmstart = false; // Warm start?
		// location of each solution

		// Donnees de chaque instanc

		// Nombre de postes
		int nb_postes = Functions.nb_shifts(input_route);
		// Definir la matrice de besoins
		int r[][] = Functions.requeriments(nb_postes, input_route);
		// duration des postes
		double d[] = Functions.duration(nb_postes, input_route);
		// Heure depart
		double s[] = Functions.start(nb_postes, input_route);
		// Heure de fin
		double e[] = Functions.ending(nb_postes, input_route);
		// Nombre des postes
		String shift_name[] = Functions.sn(nb_postes, input_route);
		// Total contrats
		int L = Functions.nc(input_route);
		// Nombre d'agents par type de contrat
		int nb_agents[] = Functions.nb_agents(input_route);
		// Postes de matin
		int Pmatin = Functions.Pm(input_route);
		// Postes de jour
		int Pjour = Functions.Pj(input_route);
		// Postes de soir
		int Psoir = Functions.Ps(input_route);
		// Postes de nuit
		int Pnuit = Functions.Pn(input_route);
		// Total postes
		int P = Pmatin + Pjour + Psoir + Pnuit + 2;
		int Pj = Pmatin + Pjour + Psoir;
		int Pn = Pnuit;
		double e_max = Functions.emax(Pn, Pj, d, e);
		double d_min = Functions.d_min(Pj + Pn, d);
		double d_min_ij[][] = Functions.dmin_global(Pj, Pn, d);
		int total_b = Functions.total_besoins(r);

		int minJca = 0; // input obj 1 pour obj 2 et 3
		int Poidsomme = 0;// input obj 1 pour obj 3
		int nb_contrats = Functions.Nb_total_contrats(L, nb_agents);
		double Ratio_dim[] = Functions.ratiodim(nb_contrats, L, nb_agents, Ratio_dim_base);
		int Nb_agents[] = Functions.nbAgents(nb_contrats, L, nb_agents);
		double Ratio[] = Functions.ratioagents(nb_contrats, L, nb_agents, Ratio_base);

		// Calcul lignes Jtable
		int NbLignes = (int) Functions.calcul_lignes_jtable(nb_agents);
		double Jca_Max = Math.ceil(NbLignes * 0.3);

		int K = nb_semaines;

		for (int obj = 1; obj <= 3; obj++) {

			if (obj == 1) {
				if (obj1 == false) {
					continue;
				}

				warmstart = false;
				File obj1_dossier = new File(out + "\\Variables_O1");
				if (!obj1_dossier.exists()) {
					if (obj1_dossier.mkdirs()) {
						System.out.println("Creation de dossier pour objectif 1");
					} else {
						System.out.println("Error");
					}
				}
			}
			if (obj == 2) {
				if (obj2 == false) {
					continue;
				}
				warmstart = true;
				File obj2_dossier = new File(out + "\\Variables_O2");
				if (!obj2_dossier.exists()) {
					if (obj2_dossier.mkdirs()) {
						System.out.println("Creation de dossier pour objectif 2");
					} else {
						System.out.println("Error");
					}
				}
			}
			if (obj == 3) {
				if (obj3 == false) {
					continue;
				}
				warmstart = true;
				File obj3_dossier = new File(out + "\\Variables_O3");
				if (!obj3_dossier.exists()) {
					if (obj3_dossier.mkdirs()) {
						System.out.println("Creation de dossier pour objectif 3");
					} else {
						System.out.println("Error");
					}
				}
			}
			try {
				OutputStream print = new FileOutputStream(out + "\\Log\\Log_objectif_" + obj + ".txt");

				int J = 7 * K;
				String[][] lignes = new String[nb_contrats][7 * K];
				double Poids_par_Jour[] = new double[J];
				double Jca_par_Jour[] = new double[5 * K];
				double Data_p[][] = new double[Pj + Pn][nb_contrats];

				// Cr�ation d'un array pour les besoins en J jours
				double[][] Besoins_cycle = Functions.Besoins_cycle(K, r, Pj, Pn);
				// Calcul du coeficients pout la FO

				double[][][] Coef = Functions.Matrix_poids(Pj, Pn, J, Besoins_cycle);

				// Define new model

				try {

					// define new model

					IloCplex cplex = new IloCplex();
					// cplex.setOut(null);
					cplex.setParam(IloCplex.DoubleParam.TiLim, time_limit[obj - 1]);
					cplex.setParam(IloCplex.Param.MIP.Interval, -1);
					cplex.setOut(print);
					// cplex.setParam(IloCplex.DoubleParam.EpAGap, 1);
					// cplex.setParam(IloCplex.Param.MIP.Limits.Solutions,1);//max number of
					// solutions
					// cplex.setParam(IloCplex.Param.Emphasis.MIP, 4);//enfasis del MIP 0=balanced
					// 1=feasibility 2=Optimality 3=best bound 4=hidden feas solutions
					// cplex.setParam(IloCplex.Param.MIP.Strategy.VariableSelect, 4);//seleccion de
					// variable -1=minimum inf 0=auto 1=maximum infeasibility 2=pseudo costs
					// 3=Strong branching 4=Branch based on pseudo reduced costs
					// cplex.setParam(IloCplex.Param.MIP.Strategy.NodeSelect, 3);
					// cplex.setParam(IloCplex.Param.MIP.Strategy.Branch, 1);//Best Bound search
					// cplex.setParam(IloCplex.Param.RootAlgorithm, 6);

					Date start = new Date();
					// creating a variable for the new solution

					// variables
					// X[i][j][l]
					IloIntVar[][][] X = new IloIntVar[P][J][nb_contrats];
					for (int i = 0; i < P; i++) {
						for (int j = 0; j < J; j++) {
							X[i][j] = cplex.boolVarArray(nb_contrats);
						}
					}

					// Jca[a][b][j][l]
					IloIntVar[][][][] JCA = new IloIntVar[Pj + Pn][Pj + Pn][J][nb_contrats];
					for (int i = 0; i < Pj + Pn; i++) {
						for (int ii = i; ii < Pj + Pn; ii++) {
							for (int j = 0; j < J; j++) {
								JCA[i][ii][j] = cplex.boolVarArray(nb_contrats);
							}
						}
					}

					// Y[j][l]
					IloIntVar[][] Y = new IloIntVar[K][nb_contrats];
					for (int k = 0; k < K; k++) {
						Y[k] = cplex.boolVarArray(nb_contrats);
					}

					IloIntVar O1 = cplex.intVar(0, Integer.MAX_VALUE);
					IloIntVar O2 = cplex.intVar(0, Integer.MAX_VALUE);
					IloIntVar O3 = cplex.intVar(0, Integer.MAX_VALUE);

					// Contraintes

					// Expression auxiliaires

					// NB Jca par jour du cycle l

					IloLinearNumExpr[][] Jca_total_jour = new IloLinearNumExpr[J][nb_contrats];
					for (int j = 0; j < J; j++) {
						for (int l = 0; l < nb_contrats; l++) {
							Jca_total_jour[j][l] = cplex.linearNumExpr();
							for (int a = 0; a < Pj + Pn; a++) {
								for (int b = a; b < Pj + Pn; b++) {
									Jca_total_jour[j][l].addTerm(1, JCA[a][b][j][l]);
								}
							}
						}
					}

					// NB Repos par jour du cycle l

					IloLinearNumExpr[][] Repos_jour = new IloLinearNumExpr[J][nb_contrats];
					for (int j = 0; j < J; j++) {
						for (int l = 0; l < nb_contrats; l++) {
							Repos_jour[j][l] = cplex.linearNumExpr();
							Repos_jour[j][l].addTerm(1, X[P - 1][j][l]);
							Repos_jour[j][l].addTerm(1, X[P - 2][j][l]);
						}
					}

					// NB Jca total dans le jour j
					IloLinearNumExpr[] Jca_total = new IloLinearNumExpr[J];
					for (int j = 0; j < J; j++) {
						Jca_total[j] = cplex.linearNumExpr();
						for (int l = 0; l < nb_contrats; l++) {
							for (int k = 0; k <= Nb_agents[l] - 1; k++) {
								for (int a = 0; a < Pj + Pn; a++) {
									for (int b = a; b < Pj + Pn; b++) {
										Jca_total[j].addTerm(1, JCA[a][b][(j + (7 * k)) % J][l]);
									}
								}
							}
						}
					}

					// C1 Besoins journalier
					if (Constrainte1) {
						IloLinearNumExpr[][] Nb_Postes_couverts = new IloLinearNumExpr[Pj + Pn][J];
						for (int i = 0; i < Pj + Pn; i++) {
							for (int j = 0; j < J; j++) {
								Nb_Postes_couverts[i][j] = cplex.linearNumExpr();
								for (int l = 0; l < nb_contrats; l++) {
									for (int k = 0; k <= Nb_agents[l] - 1; k++) {
										Nb_Postes_couverts[i][j].addTerm(1, X[i][(j + (7 * k)) % J][l]);
									}
								}
								cplex.addEq(Nb_Postes_couverts[i][j], Besoins_cycle[i][j]);// Contrainte
							}
						}
					}

					if (Constrainte3) {

						// C1 Au maximum TSMax heures de travail par semaine
						IloLinearNumExpr[][] Nb_Heures_Semaine = new IloLinearNumExpr[K][nb_contrats];
						for (int k = 0; k < K; k++) {
							for (int l = 0; l < nb_contrats; l++) {
								Nb_Heures_Semaine[k][l] = cplex.linearNumExpr();
								for (int j = k * 7; j < k * 7 + 7; j++) {
									for (int i = 0; i < Pj + Pn; i++) {
										Nb_Heures_Semaine[k][l].addTerm(d[i], X[i][j][l]);
									}
									for (int a = 0; a < Pj + Pn; a++) {
										for (int b = a; b < Pj + Pn; b++) {
											Nb_Heures_Semaine[k][l].addTerm(d_min_ij[a][b], JCA[a][b][j][l]);
										}
									}

								}
								for (int i = Pj; i < Pj + Pn; i++) {
									Nb_Heures_Semaine[k][l].addTerm(e[i], X[i][(7 * k + 7 * K - 1) % J][l]);
									Nb_Heures_Semaine[k][l].addTerm(-e[i], X[i][(7 * k + 6)][l]);
								}
								for (int a = 0; a < Pj + Pn; a++) {
									for (int b = Pj; b < Pj + Pn; b++) {
										Nb_Heures_Semaine[k][l].addTerm(e[b], JCA[a][b][(7 * k + 7 * K - 1) % J][l]);
										Nb_Heures_Semaine[k][l].addTerm(-e[b], JCA[a][b][(7 * k + 6)][l]);
									}
								}

								cplex.addLe(Nb_Heures_Semaine[k][l], h_max);// Constrainte

							}
						}
					}

					if (Constrainte4) {
						// Constrainte4 Au maximum TGMax heures de travail durant 7 jours glissants

						IloLinearNumExpr[][] Nb_Heures_7G = new IloLinearNumExpr[J][nb_contrats];
						for (int j = 0; j < J; j++) {
							for (int l = 0; l < nb_contrats; l++) {
								Nb_Heures_7G[j][l] = cplex.linearNumExpr();
								for (int q = j; q <= j + 6; q++) {
									for (int i = 0; i < Pj + Pn; i++) {
										Nb_Heures_7G[j][l].addTerm(d[i], X[i][q % J][l]);
									}
									for (int a = 0; a < Pj + Pn; a++) {
										for (int b = a; b < Pj + Pn; b++) {
											Nb_Heures_7G[j][l].addTerm(d_min_ij[a][b], JCA[a][b][q % J][l]);
										}
									}
								}
								for (int i = Pj; i < Pj + Pn; i++) {
									Nb_Heures_7G[j][l].addTerm(e[i], X[i][(j + (7 * K) - 1) % J][l]);
									Nb_Heures_7G[j][l].addTerm(-e[i], X[i][(j + 6) % J][l]);
								}
								for (int a = Pj; a < Pj + Pn; a++) {
									for (int b = a; b < Pj + Pn; b++) {
										Nb_Heures_7G[j][l].addTerm(e[b], JCA[a][b][(j + 7 * K - 1) % J][l]);
										Nb_Heures_7G[j][l].addTerm(-e[b], JCA[a][b][(j + 6) % J][l]);
									}
								}

								cplex.addLe(Nb_Heures_7G[j][l], hg_max); // Contrainte

							}
						}
					}

					// Constrainte7 Au minimum RepJ heures de repos entre postes
					if (Constrainte7) {
						IloLinearNumExpr[][] Repos_Journalier = new IloLinearNumExpr[J][nb_contrats];
						for (int j = 0; j < J; j++) {
							for (int l = 0; l < nb_contrats; l++) {
								Repos_Journalier[j][l] = cplex.linearNumExpr();

								for (int i = 0; i < Pj; i++) {
									Repos_Journalier[j][l].addTerm(24 - e[i], X[i][((j + 7 * K) - 1) % J][l]);
								}
								for (int i = Pj; i < Pj + Pn; i++) {
									Repos_Journalier[j][l].addTerm(-e[i], X[i][((j + 7 * K) - 1) % J][l]);
								}
								for (int i = 0; i < Pj + Pn; i++) {
									Repos_Journalier[j][l].addTerm(s[i], X[i][j][l]);
								}
								for (int a = 0; a < Pj; a++) {
									for (int b = a; b < Pj; b++) {
										Repos_Journalier[j][l].addTerm(24 - e[b], JCA[a][b][((j + 7 * K) - 1) % J][l]);
									}
								}
								for (int a = Pj; a < Pj + Pn; a++) {
									for (int b = a; b < Pj + Pn; b++) {
										Repos_Journalier[j][l].addTerm(-e[b], JCA[a][b][((j + 7 * K) - 1) % J][l]);
									}
								}
								for (int a = 0; a < Pj + Pn; a++) {
									for (int b = a; b < Pj + Pn; b++) {
										Repos_Journalier[j][l].addTerm(s[a], JCA[a][b][j][l]);
									}
								}
								Repos_Journalier[j][l].addTerm(OffD + e_max, X[P - 2][((j + 7 * K) - 1) % J][l]);
								Repos_Journalier[j][l].addTerm(OffD + e_max, X[P - 2][j][l]);
								Repos_Journalier[j][l].addTerm(OffD + e_max, X[P - 1][((j + 7 * K) - 1) % J][l]);
								Repos_Journalier[j][l].addTerm(OffD + e_max, X[P - 1][j][l]);
								cplex.addGe(Repos_Journalier[j][l], OffD);// Contrainte
							}
						}
					}

					// Constrainte8 Repos hebdomadaire de RepH une fois par semaine
					if (Constrainte8) {
						IloLinearNumExpr[][] Repos_Hebdo = new IloLinearNumExpr[J][nb_contrats];
						for (int k = 0; k < K; k++) {
							for (int j = k * 7; j < (k + 1) * 7; j++) {
								for (int l = 0; l < nb_contrats; l++) {
									Repos_Hebdo[j][l] = cplex.linearNumExpr();
									if (j == k * 7) {
										Repos_Hebdo[j][l].addTerm(24, X[P - 1][j][l]);
										Repos_Hebdo[j][l].addTerm(24, X[P - 2][j + 1][l]);
										for (int i = 0; i < Pj + Pn; i++) {
											Repos_Hebdo[j][l].addTerm(s[i], X[i][j + 1][l]);
										}
										for (int i = Pj; i < Pj + Pn; i++) {
											Repos_Hebdo[j][l].addTerm(-e[i], X[i][((j + 7 * K) - 1) % J][l]);
										}
										for (int a = 0; a < Pj + Pn; a++) {
											for (int b = a; b < Pj + Pn; b++) {
												Repos_Hebdo[j][l].addTerm(s[a], JCA[a][b][j + 1][l]);
											}
										}
										for (int a = Pj; a < Pj + Pn; a++) {
											for (int b = a; b < Pj + Pn; b++) {
												Repos_Hebdo[j][l].addTerm(-e[a], JCA[a][b][((j + 7 * K) - 1) % J][l]);
											}
										}
									}

									if (j == ((k + 1) * 7) - 1) {
										Repos_Hebdo[j][l].addTerm(24, X[P - 1][j][l]);
										Repos_Hebdo[j][l].addTerm(24, X[P - 2][j - 1][l]);
										for (int i = 0; i < Pj; i++) {
											Repos_Hebdo[j][l].addTerm(24 - e[i], X[i][j - 1][l]);
										}
										for (int i = Pj; i < Pj + Pn; i++) {
											Repos_Hebdo[j][l].addTerm(-e[i], X[i][j - 1][l]);
										}
										for (int a = 0; a < Pj; a++) {
											for (int b = a; b < Pj; b++) {
												Repos_Hebdo[j][l].addTerm(24 - e[b], JCA[a][b][j - 1][l]);
											}
										}
										for (int a = Pj; a < Pj + Pn; a++) {
											for (int b = a; b < Pj + Pn; b++) {
												Repos_Hebdo[j][l].addTerm(-e[a], JCA[a][b][j - 1][l]);
											}
										}
									}

									if (j > k * 7 && j < ((k + 1) * 7) - 1) {
										Repos_Hebdo[j][l].addTerm(24, X[P - 1][j][l]);
										for (int i = 0; i < Pj; i++) {
											Repos_Hebdo[j][l].addTerm(24 - e[i], X[i][j - 1][l]);
										}
										Repos_Hebdo[j][l].addTerm(24, X[P - 2][j - 1][l]);
										for (int i = Pj; i < Pj + Pn; i++) {
											Repos_Hebdo[j][l].addTerm(-e[i], X[i][j - 1][l]);
										}
										for (int a = 0; a < Pj; a++) {
											for (int b = a; b < Pj; b++) {
												Repos_Hebdo[j][l].addTerm(24 - e[b], JCA[a][b][j - 1][l]);
											}
										}
										for (int a = Pj; a < Pj + Pn; a++) {
											for (int b = a; b < Pj + Pn; b++) {
												Repos_Hebdo[j][l].addTerm(-e[a], JCA[a][b][j - 1][l]);
											}
										}
										for (int i = 0; i < Pj + Pn; i++) {
											Repos_Hebdo[j][l].addTerm(s[i], X[i][j + 1][l]);
										}
										Repos_Hebdo[j][l].addTerm(24, X[P - 2][j + 1][l]);
										for (int a = 0; a < Pj + Pn; a++) {
											for (int b = a; b < Pj + Pn; b++) {
												Repos_Hebdo[j][l].addTerm(s[a], JCA[a][b][j + 1][l]);
											}
										}
									}

									cplex.addGe(Repos_Hebdo[j][l],
											cplex.sum(cplex.prod(RepH + 12, X[P - 1][j][l]), -12)); // Contrainte

								}

							}
						}

						// Constrainte8.2 Repos au moins une fois dans la semaine
						IloLinearNumExpr[][] Repos_hebdo_sem = new IloLinearNumExpr[K][nb_contrats];
						for (int k = 0; k < K; k++) {
							for (int l = 0; l < nb_contrats; l++) {
								Repos_hebdo_sem[k][l] = cplex.linearNumExpr();
								for (int j = k * 7; j < (k + 1) * 7; j++) {
									Repos_hebdo_sem[k][l].addTerm(1, X[P - 1][j][l]);
								}
								cplex.addEq(Repos_hebdo_sem[k][l], 1);// Contrainte
							}

						}
					}
					if (Constrainte9) {
						// Constrainte9-1 Repos hebdo 4 jours par quinzaine
						IloLinearNumExpr[][] Repos_Semaine = new IloLinearNumExpr[K][nb_contrats];
						for (int k = 0; k < K; k++) {
							for (int l = 0; l < nb_contrats; l++) {
								Repos_Semaine[k][l] = cplex.linearNumExpr();
								for (int j = k * 7; j < (k + 1) * 7; j++) {
									Repos_Semaine[k][l].addTerm(1, X[P - 1][j][l]);
									Repos_Semaine[k][l].addTerm(1, X[P - 2][j][l]);
								}

							}
						}

						for (int l = 0; l < nb_contrats; l++) {
							for (int k = 0; k < K; k++) {
								cplex.addGe(cplex.sum(Repos_Semaine[k][l], Repos_Semaine[(k + 1) % K][l]), 4);
							}
						}

						// Constrainte9-2 Repos samedi et dimanche par semaine est control� par Y[k][l]

						for (int l = 0; l < nb_contrats; l++) {
							for (int k = 0; k < K; k++) {
								cplex.addGe(
										cplex.sum(X[P - 1][k * 7 + 5][l], X[P - 2][k * 7 + 5][l],
												X[P - 1][k * 7 + 6][l], X[P - 2][k * 7 + 6][l]),
										cplex.prod(2, Y[k][l]));
								// cplex.addGe(cplex.sum(X[P-1][(k*7)+6][l],X[P-2][(k*7)+6][l]),Y[k][l]);
							}
						}

						// Constrainte9-3 Au moins une fois le repos samedi et dimanche par quinzaine

						for (int l = 0; l < nb_contrats; l++) {
							for (int k = 0; k < K; k++) {
								cplex.addGe(cplex.sum(Y[k][l], Y[(k + 1) % K][l]), 1);
							}
						}
					}
					// Constrainte10 Ratio des dimanches
					if (Constrainte10) {
						IloLinearNumExpr[] Nb_Dimanches = new IloLinearNumExpr[nb_contrats];
						for (int l = 0; l < nb_contrats; l++) {
							Nb_Dimanches[l] = cplex.linearNumExpr();
							for (int k = 0; k < K; k++) {
								for (int i = 0; i < Pj + Pn; i++) {
									Nb_Dimanches[l].addTerm(1, X[i][(k * 7) + 6][l]);
								}
								for (int a = 0; a < Pj + Pn; a++) {
									for (int b = a; b < Pj + Pn; b++) {
										Nb_Dimanches[l].addTerm(1 / Ratio_dim[l], JCA[a][b][(k * 7) + 6][l]);
									}
								}
							}
							if (l != 0) {
								cplex.addLe(Nb_Dimanches[l], Nb_Dimanches[0]);
							}
						}
					}

					// Constrainte5-Constrainte6 le nombre d'heures par cycle
					IloLinearNumExpr[] Nb_Heures_Cycle = new IloLinearNumExpr[nb_contrats];
					for (int l = 0; l < nb_contrats; l++) {
						Nb_Heures_Cycle[l] = cplex.linearNumExpr();
						for (int j = 0; j < J; j++) {
							for (int i = 0; i < Pj + Pn; i++) {
								Nb_Heures_Cycle[l].addTerm(d[i], X[i][j][l]);
							}
							for (int a = 0; a < Pj + Pn; a++) {
								for (int b = a; b < Pj + Pn; b++) {
									Nb_Heures_Cycle[l].addTerm(d_min_ij[a][b], JCA[a][b][j][l]);
								}
							}
						}
						if (Constrainte5) {
							cplex.addLe(Nb_Heures_Cycle[l], 37.5 * K * Ratio[l] + (d_min - 0.1));// Contrainte
																									// Constrainte5
						}
						if (Constrainte6) {
							cplex.addGe(Nb_Heures_Cycle[l], 37.5 * K * Ratio[l] - (d_min - 0.1));// Contrainte
																									// Constrainte6
						}
					}

					// Constrainte2 Un seul poste de travail par jour
					if (Constrainte2) {
						IloLinearNumExpr[][] Nb_Postes = new IloLinearNumExpr[J][nb_contrats];
						for (int j = 0; j < J; j++) {
							for (int l = 0; l < nb_contrats; l++) {
								Nb_Postes[j][l] = cplex.linearNumExpr();
								for (int i = 0; i < P; i++) {
									Nb_Postes[j][l].addTerm(1, X[i][j][l]);
								}
								for (int a = 0; a < Pj + Pn; a++) {
									for (int b = a; b < Pj + Pn; b++) {
										Nb_Postes[j][l].addTerm(1, JCA[a][b][j][l]);
									}
								}
								cplex.addEq(Nb_Postes[j][l], 1);// contrainte
							}
						}
					}
					// Constrainte11 Au minimum 2 jours de travail consecutifs
					if (Constrainte11) {
						IloLinearNumExpr[][] Postes_nb = new IloLinearNumExpr[J][nb_contrats];
						for (int j = 0; j < J; j++) {
							for (int l = 0; l < nb_contrats; l++) {
								Postes_nb[j][l] = cplex.linearNumExpr();

								for (int i = 0; i < Pj + Pn; i++) {
									Postes_nb[j][l].addTerm(-1, X[i][j][l]);
								}
								for (int a = 0; a < Pj + Pn; a++) {
									for (int b = a; b < Pj + Pn; b++) {
										Postes_nb[j][l].addTerm(-1, JCA[a][b][j][l]);
									}
								}

								for (int i = 0; i < Pj + Pn; i++) {
									Postes_nb[j][l].addTerm(1, X[i][(j + 1) % J][l]);
								}
								for (int a = 0; a < Pj + Pn; a++) {
									for (int b = a; b < Pj + Pn; b++) {
										Postes_nb[j][l].addTerm(1, JCA[a][b][(j + 1) % J][l]);
									}
								}

								for (int i = 0; i < Pj + Pn; i++) {
									Postes_nb[j][l].addTerm(-1, X[i][(j + 2) % J][l]);
								}
								for (int a = 0; a < Pj + Pn; a++) {
									for (int b = a; b < Pj + Pn; b++) {
										Postes_nb[j][l].addTerm(-1, JCA[a][b][(j + 2) % J][l]);
									}
								}

								cplex.addLe(Postes_nb[j][l], 0);// contrainte

							}
						}
					}
					// Constrainte12 Au maximum CDmax jours de travail consecutifs
					if (Constrainte12) {
						IloLinearNumExpr[][] NbMax_jours_consecutifs = new IloLinearNumExpr[J][nb_contrats];
						for (int j = 0; j < J; j++) {
							for (int l = 0; l < nb_contrats; l++) {
								NbMax_jours_consecutifs[j][l] = cplex.linearNumExpr();
								for (int q = j; q <= CDmax + j; q++) {
									for (int i = 0; i < Pj + Pn; i++) {
										NbMax_jours_consecutifs[j][l].addTerm(1, X[i][q % J][l]);
									}
									for (int a = 0; a < Pj + Pn; a++) {
										for (int b = a; b < Pj + Pn; b++) {
											NbMax_jours_consecutifs[j][l].addTerm(1, JCA[a][b][q % J][l]);
										}
									}
								}
								cplex.addLe(NbMax_jours_consecutifs[j][l], CDmax);// Contrainte
							}
						}
					}

					// Constrainte13 Samedi et dimanche le m�me poste de travail
					if (Constrainte13) {
						// ancienne version
						for (int k = 0; k < K; k++) {
							for (int l = 0; l < nb_contrats; l++) {
								cplex.addEq(Jca_total_jour[(k * 7) + 5][l], Jca_total_jour[(k * 7) + 6][l]);// contrainte
							}
						}
						for (int k = 0; k < K; k++) {
							for (int l = 0; l < nb_contrats; l++) {
								for (int i = 0; i < Pj + Pn; i++) {
									cplex.addLe(X[i][(k * 7) + 5][l],
											cplex.sum(X[i][(k * 7) + 6][l],
													cplex.prod(Math.abs(r[i][5] - r[i][6]), X[P - 1][(k * 7) + 6][l]),
													cplex.prod(Math.abs(r[i][5] - r[i][6]), X[P - 2][(k * 7) + 6][l])));// contrainte
																														// 1
									cplex.addLe(X[i][(k * 7) + 6][l],
											cplex.sum(X[i][(k * 7) + 5][l],
													cplex.prod(Math.abs(r[i][5] - r[i][6]), X[P - 1][(k * 7) + 5][l]),
													cplex.prod(Math.abs(r[i][5] - r[i][6]), X[P - 2][(k * 7) + 5][l])));// contrainte
																														// 3
								}
							}
						}

					}
					// Constrainte15 UB Jca par jour
					if (Constrainte15) {
						for (int k = 0; k < K; k++) {
							for (int j = 7 * k; j <= 7 * k + 4; j++) {
								cplex.addLe(Jca_total[j], Jca_Max);// contrainte
							}
						}
					}

					// Constrainte14 Gestion des bloques
					// CH[l] Cr�ation d'une expression pour obliger a CH_jl=s'il y a un changement
					IloLinearNumExpr[][][] CH_cote_d = new IloLinearNumExpr[Pj + Pn][J][nb_contrats];
					for (int i = 0; i < Pj + Pn; i++) {
						for (int j = 0; j < J; j++) {
							for (int l = 0; l < nb_contrats; l++) {
								CH_cote_d[i][j][l] = cplex.linearNumExpr();
								CH_cote_d[i][j][l].addTerm(1, X[i][j][l]);
								CH_cote_d[i][j][l].addTerm(-1, X[i][(j + 1) % J][l]);
								CH_cote_d[i][j][l].addTerm(1, X[Pj + Pn][j][l]);
								CH_cote_d[i][j][l].addTerm(1, X[Pj + Pn + 1][j][l]);
								for (int a = 0; a < Pj + Pn; a++) {
									for (int b = a; b < Pj + Pn; b++) {
										CH_cote_d[i][j][l].addTerm(1, JCA[a][b][j][l]);
									}
								}
								for (int ii = 0; ii < Pj + Pn; ii++) {
									CH_cote_d[i][j][l].addTerm(1, X[ii][(j + 1) % J][l]);
								}

							}
						}
					}

					if (Constrainte14) {

						// Alternance de postes suite au changement d'un bloc postes (M,J,S)

						IloLinearNumExpr[][][] Repos_ou_Jca = new IloLinearNumExpr[J][J + 14][nb_contrats];
						for (int j = 0; j < J; j++) {
							for (int k = j + 1; k <= j + 14; k++) {
								for (int l = 0; l < nb_contrats; l++) {
									Repos_ou_Jca[j][k][l] = cplex.linearNumExpr();
									for (int bet = j + 1; bet <= k - 1; bet++) {
										for (int a = 0; a < Pj + Pn; a++) {
											for (int b = a; b < Pj + Pn; b++) {
												Repos_ou_Jca[j][k][l].addTerm(1, JCA[a][b][bet % J][l]);
											}
										}
										Repos_ou_Jca[j][k][l].addTerm(1, X[Pj + Pn][bet % J][l]);
										Repos_ou_Jca[j][k][l].addTerm(1, X[Pj + Pn + 1][bet % J][l]);
									}
								}
							}
						}

						for (int j = 0; j < J; j++) {
							for (int l = 0; l < nb_contrats; l++) {

								for (int i = 0; i < Pmatin + Pjour; i++) {
									for (int k = j + 2; k <= j + 7; k++) {
										// cplex.addLe(cplex.sum(X[i][j][l],X[i][k%J][l],cplex.negative(NB[j][l])),cplex.sum(k-j,cplex.negative(Repos_ou_Jca[j][k][l])));
										cplex.addLe(cplex.sum(X[i][j][l], X[i][k % J][l]),
												cplex.sum(k - j, cplex.negative(Repos_ou_Jca[j][k][l])));
									}
								}

								for (int i = Pmatin + Pjour; i < Pmatin + Pjour + Psoir; i++) {
									for (int k = j + 2; k <= j + 14; k++) {
										// cplex.addLe(cplex.sum(X[i][j][l],X[i][k%J][l],cplex.negative(NB[j][l])),cplex.sum(k-j,cplex.negative(Repos_ou_Jca[j][k][l])));
										cplex.addLe(cplex.sum(X[i][j][l], X[i][k % J][l]),
												cplex.sum(k - j, cplex.negative(Repos_ou_Jca[j][k][l])));
									}
								}

							}
						}

					}

					// Constrainte16 Nombre max de Jca par jour selon les besoins
					if (Constrainte16) {

						// Limiter les Jca couvrant le le poste ii

						IloLinearNumExpr[][] Jca_type_i_couvert = new IloLinearNumExpr[Pj + Pn][J];
						for (int i = 0; i < Pj + Pn; i++) {
							for (int j = 0; j < J; j++) {
								Jca_type_i_couvert[i][j] = cplex.linearNumExpr();
								for (int l = 0; l < nb_contrats; l++) {
									for (int k = 0; k <= Nb_agents[l] - 1; k++) {
										Jca_type_i_couvert[i][j].addTerm(1, JCA[i][i][(j + (7 * k)) % J][l]);
									}
								}
								cplex.addLe(Jca_type_i_couvert[i][j], Besoins_cycle[i][j]);// Contrainte limitant les
																							// Jca couvrant le type de
																							// poste ii
							}
						}
						for (int j = 0; j < J; j++) {
							cplex.addLe(Jca_total[j], total_b);
						}

					}

					// Constrainte41 Contraintes de liaison

					// Nb de Jca qui couvrent un poste "i"

					IloLinearNumExpr[][] Jca_poste = new IloLinearNumExpr[Pj + Pn][J];
					for (int i = 0; i < Pj + Pn; i++) {
						for (int j = 0; j < J; j++) {
							Jca_poste[i][j] = cplex.linearNumExpr();
							for (int l = 0; l < nb_contrats; l++) {
								for (int k = 0; k <= Nb_agents[l] - 1; k++) {
									for (int a = 0; a <= i; a++) {
										for (int b = i; b < Pj + Pn; b++) {
											Jca_poste[i][j].addTerm(1, JCA[a][b][(j + (7 * k)) % J][l]);
										}
									}
								}
							}
						}
					}

					// Objectifs

					if (obj == 1) {
						// NB Poids total jour
						// NB Poids total jour
						IloLinearNumExpr[] Jca_Poids = new IloLinearNumExpr[J];
						for (int j = 0; j < J; j++) {
							Jca_Poids[j] = cplex.linearNumExpr();
							for (int l = 0; l < nb_contrats; l++) {
								for (int k = 0; k <= Nb_agents[l] - 1; k++) {
									for (int a = 0; a < Pj + Pn; a++) {
										for (int b = a; b < Pj + Pn; b++) {
											Jca_Poids[j].addTerm(1, JCA[a][b][(j + (7 * k)) % J][l]);
										}
									}
								}
							}
						}

						for (int k = 0; k < K; k++) {
							for (int j = k * 7; j < (k * 7) + 5; j++) {
								cplex.addGe(Jca_Poids[j], O1);
							}
						}

						cplex.addMaximize(O1);

					}

					if (obj == 2) {

						// NB Jca par jour
						IloLinearNumExpr[] Jca_Poids = new IloLinearNumExpr[J];
						for (int j = 0; j < J; j++) {
							Jca_Poids[j] = cplex.linearNumExpr();
							for (int l = 0; l < nb_contrats; l++) {
								for (int k = 0; k <= Nb_agents[l] - 1; k++) {
									for (int a = 0; a < Pj + Pn; a++) {
										for (int b = a; b < Pj + Pn; b++) {
											Jca_Poids[j].addTerm(1, JCA[a][b][(j + (7 * k)) % J][l]);
										}
									}
								}
							}
						}

						for (int k = 0; k < K; k++) {
							for (int j = k * 7; j < (k * 7) + 5; j++) {
								cplex.addGe(Jca_Poids[j], minJca);
							}
						}
						// NB Poids total jour
						IloLinearNumExpr Jca_sump = cplex.linearNumExpr();
						for (int j = 0; j < J; j++) {
							for (int l = 0; l < nb_contrats; l++) {
								for (int a = 0; a < Pj + Pn; a++) {
									for (int b = a; b < Pj + Pn; b++) {
										Jca_sump.addTerm(Coef[a][b][j] * Nb_agents[l], JCA[a][b][j % J][l]);
									}
								}
							}
						}

						cplex.addEq(O2, Jca_sump);
						cplex.addMaximize(O2);

					}

					if (obj == 3) {

						// NB Jca par jour
						IloLinearNumExpr Jca_Poids[] = new IloLinearNumExpr[J];
						for (int j = 0; j < J; j++) {
							Jca_Poids[j] = cplex.linearNumExpr();
							for (int l = 0; l < nb_contrats; l++) {
								for (int k = 0; k <= Nb_agents[l] - 1; k++) {
									for (int a = 0; a < Pj + Pn; a++) {
										for (int b = a; b < Pj + Pn; b++) {
											Jca_Poids[j].addTerm(1, JCA[a][b][(j + (7 * k)) % J][l]);
										}
									}
								}
							}
						}

						for (int k = 0; k < K; k++) {
							for (int j = k * 7; j < (k * 7) + 5; j++) {
								cplex.addGe(Jca_Poids[j], minJca);
							}
						}
						// NB Poids total jour
						IloLinearNumExpr Jca_sump = cplex.linearNumExpr();
						for (int j = 0; j < J; j++) {
							for (int l = 0; l < nb_contrats; l++) {
								for (int a = 0; a < Pj + Pn; a++) {
									for (int b = a; b < Pj + Pn; b++) {
										Jca_sump.addTerm(Coef[a][b][j] * Nb_agents[l], JCA[a][b][j % J][l]);
									}
								}
							}
						}

						cplex.addGe(Jca_sump, Poidsomme);

						// Equit�

						IloLinearNumExpr[][] NbpostesComp_par_contratUB = new IloLinearNumExpr[Pj + Pn][nb_contrats];
						for (int i = 0; i < Pj + Pn; i++) {
							for (int l = 0; l < nb_contrats; l++) {
								NbpostesComp_par_contratUB[i][l] = cplex.linearNumExpr();
								for (int j = 0; j < J; j++) {
									NbpostesComp_par_contratUB[i][l].addTerm(Ratio[l], X[i][j][0]);
								}
							}
						}

						IloLinearNumExpr[][] NbpostesComp_par_contratLB = new IloLinearNumExpr[Pj + Pn][nb_contrats];
						for (int i = 0; i < Pj + Pn; i++) {
							for (int l = 0; l < nb_contrats; l++) {
								NbpostesComp_par_contratLB[i][l] = cplex.linearNumExpr();
								for (int j = 0; j < J; j++) {
									NbpostesComp_par_contratLB[i][l].addTerm(Ratio[l], X[i][j][0]);
								}
							}
						}

						IloLinearNumExpr[][] Nb_postes_par_contrat = new IloLinearNumExpr[Pj + Pn][nb_contrats];
						for (int i = 0; i < Pj + Pn; i++) {
							for (int l = 0; l < nb_contrats; l++) {
								Nb_postes_par_contrat[i][l] = cplex.linearNumExpr();
								for (int j = 0; j < J; j++) {
									Nb_postes_par_contrat[i][l].addTerm(1, X[i][j][l]);
								}
							}
						}

						for (int l = 1; l < nb_contrats; l++) {
							for (int i = 0; i < Pj + Pn; i++) {
								cplex.addLe(Nb_postes_par_contrat[i][l],
										cplex.sum(NbpostesComp_par_contratUB[i][l], O3));// contrainte UB
								cplex.addGe(Nb_postes_par_contrat[i][l],
										cplex.sum(NbpostesComp_par_contratLB[i][l], cplex.negative(O3)));// contrainte
																											// LB
							}
						}

						cplex.addMinimize(O3);
					}

					// Constraint for the warmstart
					// warmstart

					if (warmstart == true) {
						// Recovering the solution
						int recuperado_count = 0;
						double PSolution[] = new double[(P * J * nb_contrats) + (K * nb_contrats)];
						// System.out.println("Size of the solution="+PSolution.length);
						// Creating a variable for the solution
						IloNumVar[] startVar = new IloNumVar[PSolution.length];

						// X solution
						try {
							FileReader fr = new FileReader(out + "\\Variables_O" + (obj - 1) + "\\X.txt");
							BufferedReader bf = new BufferedReader(fr);
							String temp = "";

							while (temp != null) {
								double temporal = Double.parseDouble(bf.readLine());
								if (temporal < 0.5) {
									PSolution[recuperado_count] = 0;
								} else {
									PSolution[recuperado_count] = 1;
								}
								// System.out.println("Recovering result for object "+recuperado_count+"/300");
								recuperado_count++;
								if (recuperado_count == (P * J * nb_contrats)) {
									break;
								}

							}
							bf.close();

						} catch (IOException ex) {
							// TODO Auto-generated catch block
							ex.printStackTrace();
						}

						try {
							FileReader fr = new FileReader(out + "\\Variables_O" + (obj - 1) + "\\Y.txt");
							BufferedReader bf = new BufferedReader(fr);
							String temp = "";

							while (temp != null) {
								double temporal = Double.parseDouble(bf.readLine());
								if (temporal < 0.5) {
									PSolution[recuperado_count] = 0;
								} else {
									PSolution[recuperado_count] = 1;
								}
								// System.out.println("Recovering result for object "+recuperado_count+"/300");
								recuperado_count++;
								if (recuperado_count == (P * J * nb_contrats) + (K * nb_contrats)) {
									break;
								}

							}

							bf.close();
						} catch (IOException ex) {
							// TODO Auto-generated catch block
							ex.printStackTrace();
						}

						int idx = 0;

						for (int i = 0; i < P; i++) {
							for (int j = 0; j < J; j++) {
								for (int l = 0; l < nb_contrats; l++) {
									startVar[idx] = X[i][j][l];
									++idx;
								}
							}
						}

						for (int k = 0; k < K; k++) {
							for (int l = 0; l < nb_contrats; l++) {
								startVar[idx] = Y[k][l];
								++idx;
							}
						}

						cplex.addMIPStart(startVar, PSolution);

					} // end warmstart

					// Fin model
					// solve model
					if (cplex.solve()) {

						Date end = new Date();
						// Sauvegarder les variables dans les dossiers
						try {
							FileWriter fw = new FileWriter(out + "\\Variables_O" + obj + "\\X.txt", true);
							for (int i = 0; i < P; i++) {
								for (int j = 0; j < J; j++) {
									for (int l = 0; l < nb_contrats; l++) {
										fw.write(cplex.getValue(X[i][j][l]) + "\r");
									}
								}
							}
							fw.close();

						} catch (IOException ex) {

						}

						try {
							FileWriter fw = new FileWriter(out + "\\Variables_O" + obj + "\\JCA.txt", true);
							for (int j = 0; j < J; j++) {
								for (int l = 0; l < nb_contrats; l++) {
									for (int i = 0; i < Pj + Pn; i++) {
										for (int ii = i; ii < Pj + Pn; ii++) {
											fw.write(cplex.getValue(JCA[i][ii][j][l]) + "\r");
										}
									}
								}
							}
							fw.close();

						} catch (IOException ex) {

						}

						try {
							FileWriter fw = new FileWriter(out + "\\Variables_O" + obj + "\\Y.txt", true);
							for (int k = 0; k < K; k++) {
								for (int l = 0; l < nb_contrats; l++) {
									fw.write(cplex.getValue(Y[k][l]) + "\r");
								}
							}

							fw.close();

						} catch (IOException ex) {

						}

						// Poids

						if (obj == 1) {

							System.out.println("Objectif 1 Resolu; Temps de calcul :"
									+ (end.getTime() - start.getTime()) / 1000 + " sec");

							Solver.setFoundSolution(true);
							// Objective

							if (cplex.getValue(O1) - Math.floor(cplex.getValue(O1)) > 0.5) {
								minJca = (int) Math.ceil(cplex.getValue(O1));
							}

							if (cplex.getValue(O1) - Math.floor(cplex.getValue(O1)) < 0.5) {
								minJca = (int) Math.floor(cplex.getValue(O1));
							}

						}
						if (obj == 2) {

							Solver.setFoundSolution(true);

							System.out.println("Objectif 2 Resolu; Temps de calcul :"
									+ (end.getTime() - start.getTime()) / 1000 + " sec");

							if (cplex.getValue(O2) - Math.floor(cplex.getValue(O2)) > 0.5) {
								Poidsomme = (int) Math.ceil(cplex.getValue(O2));
							}
							if (cplex.getValue(O2) - Math.floor(cplex.getValue(O2)) < 0.5) {
								Poidsomme = (int) Math.floor(cplex.getValue(O2));
							}
						}

						if (obj == 3) {

							Solver.setFoundSolution(true);

							System.out.println("Objectif 3 Resolu; Temps de calcul :"
									+ (end.getTime() - start.getTime()) / 1000 + " sec");
						}

						// Remplir le tableau de visualisation selon les r�sultats

						for (int i = 0; i < Pj + Pn; i++) {
							for (int j = 0; j < J; j++) {
								for (int l = 0; l < nb_contrats; l++) {
									if (cplex.getValue(X[i][j][l]) > 0.1) {
										lignes[l][j] = shift_name[i];
										Data_p[i][l]++;
									}
								}
							}
						}

						for (int i = Pj + Pn; i < Pj + Pn + 2; i++) {
							for (int j = 0; j < J; j++) {
								for (int l = 0; l < nb_contrats; l++) {
									if (cplex.getValue(X[i][j][l]) > 0.1) {
										lignes[l][j] = ".";
									}
								}
							}
						}

						for (int j = 0; j < J; j++) {
							for (int l = 0; l < nb_contrats; l++) {
								for (int i = 0; i < Pj + Pn; i++) {
									for (int ii = i; ii < Pj + Pn; ii++) {
										if (cplex.getValue(JCA[i][ii][j][l]) > 0.1) {
											lignes[l][j] = "Jca " + i + "-" + ii;
										}
									}
								}
							}
						}

						// Jtable

						String[] columnNames = new String[7 * K];

						for (int j = 0; j < 7; j++) {
							columnNames[0] = "Lun";
							columnNames[1] = "Mar";
							columnNames[2] = "Mer";
							columnNames[3] = "Jeu";
							columnNames[4] = "Ven";
							columnNames[5] = "Sam";
							columnNames[6] = "Dim";
						}

						int v_aux = 6;
						for (int k = 1; k < K; k++) {

							columnNames[v_aux + 1] = "Lun";
							columnNames[v_aux + 2] = "Mar";
							columnNames[v_aux + 3] = "Mer";
							columnNames[v_aux + 4] = "Jeu";
							columnNames[v_aux + 5] = "Ven";
							columnNames[v_aux + 6] = "Sam";
							columnNames[v_aux + 7] = "Dim";

							v_aux = v_aux + 7;
						}

						String[][] data = new String[NbLignes][7 * K];

						int starting_line[] = new int[nb_contrats];
						int ending_line[] = new int[nb_contrats];
						starting_line[0] = 0;
						ending_line[0] = (int) Nb_agents[0] - 1;
						for (int l = 1; l < nb_contrats; l++) {
							starting_line[l] = ending_line[l - 1] + 1;
							ending_line[l] = starting_line[l] + (int) Nb_agents[l] - 1;
						}

						for (int l = 0; l < nb_contrats; l++) {
							for (int j = 0; j < J; j++) {
								data[starting_line[l]][j] = lignes[l][j];
							}
						}

						for (int l = 0; l < nb_contrats; l++) {
							for (int line = starting_line[l] + 1; line <= ending_line[l]; line++) {
								for (int j = 0; j < 7 * (K - 1); j++) {
									data[line][j] = data[line - 1][j + 7];
									for (int jj = 7 * (K - 1); jj < 7 * K; jj++) {
										data[line][jj] = data[line - 1][jj - (7 * (K - 1))];
									}
								}
							}
						}

						// creamos el Modelo de la tabla con los datos anteriores
						DefaultTableModel dtm = new DefaultTableModel(data, columnNames);
						// se crea la Tabla con el modelo DefaultTableModelfinal
						final JTable table = new JTable(dtm);

						// Agregar la columna para saber que agentes son

						String[] Agents_Col = new String[NbLignes];
						String[] Agents_Pct = new String[NbLignes];

						for (int l = 0; l < nb_contrats; l++) {
							int vaux = 1;
							for (int i = starting_line[l]; i <= ending_line[l]; i++) {
								Agents_Col[i] = "Agent " + vaux;
								vaux = vaux + 1;
							}
						}

						for (int l = 0; l < nb_contrats; l++) {
							int vaux = 1;
							for (int i = starting_line[l]; i <= ending_line[l]; i++) {
								Agents_Pct[i] = (int) (Ratio[l] * 100) + "%";
								vaux = vaux + 1;
							}
						}

						dtm.addColumn("Agent", Agents_Col);
						dtm.addColumn("Pourcentage", Agents_Pct);
						table.moveColumn(7 * K, 0);
						table.moveColumn(7 * K + 1, 0);

						table.setPreferredScrollableViewportSize(new Dimension(1000, 400));

						// Creamos un JscrollPane y le agregamos la JTable
						table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
						table.getColumnModel().getColumn(0).setPreferredWidth(75);
						table.getColumnModel().getColumn(1).setPreferredWidth(60);
						for (int i = 2; i <= (7 * K) + 1; i++) {
							table.getColumnModel().getColumn(i).setPreferredWidth(29);
						}

						JScrollPane pane = new JScrollPane(table);
						add(pane, BorderLayout.CENTER);

						// Agregamos el JScrollPane al contenedor
						getContentPane().add(pane, BorderLayout.CENTER);

						// manejamos la salida
						addWindowListener(new WindowAdapter() {
							public void windowClosing(WindowEvent e) {
								System.exit(0);
							}
						});

						// Exportar a excel

						Workbook book = new XSSFWorkbook();// Indicar crear un archivo en excel
						org.apache.poi.ss.usermodel.Sheet sheet = book.createSheet("Cycle");// Crear una pestana
						// Poniendo los dias de la semana
						Row row1 = sheet.createRow(0);
						String JcaMin = "Ecart_carr�=" + Functions.ecart_carr(Jca_par_Jour);
						String JcaMax = "Alpha=" + Functions.alpha_value(Data_p, Pj, Pn, Ratio);
						row1.createCell(0).setCellValue(JcaMin);
						row1.createCell(1).setCellValue(JcaMax);

						for (int j = 0; j < 7 * K; j++) {
							row1.createCell(j + 2).setCellValue(columnNames[j]);
						}
						// Poniendo los datos
						for (int i = 0; i < NbLignes; i++) {
							Row row = sheet.createRow(i + 1);
							row.createCell(0).setCellValue(Agents_Pct[i]);
							row.createCell(1).setCellValue(Agents_Col[i]);

							for (int j = 0; j < 7 * K; j++) {
								row.createCell(j + 2).setCellValue(data[i][j]);
							}
						}
						Row rowlast = sheet.createRow(NbLignes + 1);
						for (int j = 0; j < 7 * K; j++) {
							rowlast.createCell(j + 2).setCellValue(Poids_par_Jour[j]);
						}

						// Try and catch para crear el archivo
						String File_name = "Cycle_Obj" + obj + ".xlsx";
						try {
							FileOutputStream fileout = new FileOutputStream(out + "\\" + File_name);// Nombre del
																									// archivo
							book.write(fileout);// Crear el archivo
							fileout.close();// Cerrar el archivo
							book.close();
						} catch (FileNotFoundException ex) {
							Logger.getLogger(Solver.class.getName()).log(Level.SEVERE, null, ex);
						} catch (IOException ex) {
							Logger.getLogger(Solver.class.getName()).log(Level.SEVERE, null, ex);
						}
					} else {
						Date end = new Date();

						if ((end.getTime() - start.getTime()) / 1000. >= time_limit[obj - 1]) {
							Solver.setFoundSolution(false);
							System.out.println("Objectif " + obj + " non resolu; Temps de calcul :"
									+ (end.getTime() - start.getTime()) / 1000 + " sec");
						}

						if ((end.getTime() - start.getTime()) / 1000. < time_limit[obj - 1]) {
							Solver.setFoundSolution(false);
							System.out.println("Objectif " + obj + " infaisable; Temps de calcul :"
									+ (end.getTime() - start.getTime()) / 1000 + " sec");
							break;
						}
					}
					cplex.end();
				} catch (IloException exc) {
					exc.printStackTrace();

				}

			} catch (FileNotFoundException exc) {
				// TODO Auto-generated catch block
				exc.printStackTrace();

			}

		}
	}

	public static boolean isFoundSolution() {
		return foundSolution;
	}

	public static void setFoundSolution(boolean foundSolution) {
		Solver.foundSolution = foundSolution;
	}

}