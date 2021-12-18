package com.projet.annuel.pdph.solveur;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Functions {

	//Dur�e min des postes
	public static double d_min(int P,double d[]) {
		double d_min=100000;
		for(int i=0;i<d.length;i++) {
			if(d[i]<d_min) {
				d_min=d[i];
			}
		}
		return d_min;
	}
	
	//Dur�e max des postes
	public static double d_max(int P,double d[]) {
		double d_max=0;
		for(int i=0;i<d.length;i++) {
			if(d[i]>d_max) {
				d_max=d[i];
			}
		}
		return d_max;
	}
		
		public static double emax(int Pn,int Pj,double d[],double e[]) {
			
			double e_max=0;
			double aux_emax=0;	
			if(Pn==0) {
			e_max=0;	
			}
			if(Pn==1) {
				e_max=e[Pj];	
				}
			if(Pn==2) {
				e_max=Math.max(e[Pj],e[Pj+1]);	
				}
			if(Pn>2) {
				e_max=Math.max(d[Pj],d[Pj+1]);	
				aux_emax=e_max;
				for(int i=Pj+1;i<Pj+Pn;i++) {
				e_max=Math.max(aux_emax,d[i]);
				aux_emax=e_max;
				}
			}
			return e_max;
			}
		
		public static double[] rat_rem_lunven(int besoins[][],int Pj,int Pn) {
			double Ratio_remplacement_lunven[]= new double[Pj+Pn];
		    double totaux_journaliers_lunven=0;
				for(int i=0;i<Pj+Pn;i++){
					totaux_journaliers_lunven+=besoins[i][0];
				}
				
			for(int i=0;i<Pj+Pn;i++){
				Ratio_remplacement_lunven[i]+=besoins[i][0];
			}
			
			for(int i=0;i<Pj+Pn;i++){
				Ratio_remplacement_lunven[i]=Ratio_remplacement_lunven[i]/totaux_journaliers_lunven;
			}
			return Ratio_remplacement_lunven;
			}
		
		public static double[] rat_rem_samdim(int besoins[][],int Pj,int Pn) {
			
			double Ratio_remplacement_samdim[]= new double[Pj+Pn];
		    double totaux_journaliers_samdim=0;	
				for(int i=0;i<Pj+Pn;i++){
					totaux_journaliers_samdim+=besoins[i][6];	
				}
				
			for(int i=0;i<Pj+Pn;i++){
				Ratio_remplacement_samdim[i]+=besoins[i][6];
			}
			
			for(int i=0;i<Pj+Pn;i++){
				if(totaux_journaliers_samdim>0) {
				Ratio_remplacement_samdim[i]=Ratio_remplacement_samdim[i]/totaux_journaliers_samdim;

				}
				else {
				}
			}
			return Ratio_remplacement_samdim;
			}
		
		public static double calcul_lignes_jtable(int Nb_agents[]) {
			
			   double Nb_total=0;
		       for(int i=0;i<7;i++) {
		 	   Nb_total+=Nb_agents[i];
		         }

				return Nb_total;
				}
		
		public static double besoins_dim(double besoins[][][],int Pj,int Pn,int ins) {
			double besoins_dim=0;
				for(int q=0;q<Pj+Pn;q++) {
					besoins_dim=besoins[ins][q][6]; 
				}

					return besoins_dim;
			}
		
		public static double Nb_JCA_min(double Nb_agents[][],int K,double Ratio[],double besoins[][][],double d[],int Pj,int Pn,double d_max,double d_min,int ins) {
			
			double Nb_heures_dispo=0;
		    double Nb_heures_maquette=0;
				for(int i=0;i<Nb_agents[ins].length;i++) {
					Nb_heures_dispo+=Nb_agents[ins][i]*37.5*K*Ratio[i];
				}
				for(int i=0;i<Pj+Pn;i++) {
					for(int j=0;j<7;j++) {
						Nb_heures_maquette+=besoins[ins][i][j]*d[i];
							}
						}
					    Nb_heures_maquette=Nb_heures_maquette*K;
		       double nbJcamin=((Nb_heures_dispo-Nb_heures_maquette)/d_max)/(5*K);
			   double nbJcamax=((Nb_heures_dispo-Nb_heures_maquette)/d_min)/(5*K);
		    
		       nbJcamin=Math.floor(((Nb_heures_dispo-Nb_heures_maquette)/d_max)/(5*K));
			   nbJcamax=Math.ceil(((Nb_heures_dispo-Nb_heures_maquette)/d_min)/(5*K));
			   
			   if(nbJcamin==((Nb_heures_dispo-Nb_heures_maquette)/d_max)/(5*K)) {
			   nbJcamin=nbJcamin-1;
			   }
			   if(nbJcamax==((Nb_heures_dispo-Nb_heures_maquette)/d_min)/(5*K)) {
				   nbJcamax=nbJcamax+1;
				   }
			   

		       
			   
			   if(nbJcamin==nbJcamax) {
				nbJcamax=nbJcamax+1;
			   }
			   return nbJcamin;
			}
			
			public static double Nb_JCA_max(double Nb_agents[][],int K,double Ratio[],double besoins[][][],double d[],int Pj,int Pn,double d_max,double d_min,int ins) {
				
				double Nb_heures_dispo=0;
			    double Nb_heures_maquette=0;
					for(int i=0;i<Nb_agents[ins].length;i++) {
						Nb_heures_dispo+=Nb_agents[ins][i]*37.5*K*Ratio[i];
					}
					for(int i=0;i<Pj+Pn;i++) {
						for(int j=0;j<7;j++) {
							Nb_heures_maquette+=besoins[ins][i][j]*d[i];
								}
							}
						    Nb_heures_maquette=Nb_heures_maquette*K;
			       double nbJcamin=((Nb_heures_dispo-Nb_heures_maquette)/d_max)/(5*K);
				   double nbJcamax=((Nb_heures_dispo-Nb_heures_maquette)/d_min)/(5*K);
			    
			       nbJcamin=Math.floor(((Nb_heures_dispo-Nb_heures_maquette)/d_max)/(5*K));
				   nbJcamax=Math.ceil(((Nb_heures_dispo-Nb_heures_maquette)/d_min)/(5*K));
				   
				   if(nbJcamin==((Nb_heures_dispo-Nb_heures_maquette)/d_max)/(5*K)) {
				   nbJcamin=nbJcamin-1;
				   }
				   if(nbJcamax==((Nb_heures_dispo-Nb_heures_maquette)/d_min)/(5*K)) {
					   nbJcamax=nbJcamax+1;
					   }
				   

			       
				   if(nbJcamin==nbJcamax) {
					nbJcamax=nbJcamax+1;
				   }
				   return nbJcamax;
				}
			
			public static double [][] Tableau_equite(double Nb_agents[][],int K,double Ratio[],double besoins[][][],int Pj,int Pn,int ins) {
				
				
				double Nb_postes_par_agent[][]=new double[Nb_agents[ins].length][Pj+Pn];
				double den=0;
			    for(int w=1;w<Nb_agents[ins].length;w++) {
		        den+=Nb_agents[ins][w]*Ratio[w];
			    }
			    
				for (int i=0; i<Pj+Pn;i++) {
			    if(den>0) {
			    for(int j=0;j<7;j++)
			    Nb_postes_par_agent[0][i]+=besoins[ins][i][j]*K;	    
				}
			    Nb_postes_par_agent[0][i]=Math.ceil(Nb_postes_par_agent[0][i]/(den+Nb_agents[ins][0]));
			    
			    if(den<=0) {
			    for(int j=0;j<7;j++) {
			    Nb_postes_par_agent[0][i]+=besoins[ins][i][j]*K;
			    }
			    }
			    
			    for(int w=1;w<Nb_agents[ins].length;w++) {
			    if(Nb_agents[ins][w]>0) {
				Nb_postes_par_agent[w][i]=Nb_postes_par_agent[0][i]*Ratio[w];
			    }
				}
				}
				return Nb_postes_par_agent;
				}	
			
			public static double [][] Besoins_cycle(int K,int besoins[][],int Pj,int Pn) {
				
		        double[][]Besoins_cycle= new double[Pj+Pn][K*7];
		        
		        for(int i=0;i<Pj+Pn;i++) {
		        	for(int k=0;k<K;k++) {
		 	    	   for(int j=k*7;j<=k*7+6;j++) { 		   
		 	       Besoins_cycle[i][j]=besoins[i][j-k*7];
		 	    	   }
		 	       }
		        	
		        }
		        return Besoins_cycle;
				}
			
			public static double dmin_global(double d[]) {
				//Calcul du dmin global
				double d_min_global=0;
				double aux_dmin_global=0;	
				if(d.length==2) {
				d_min_global=d[0];	
				}
				if(d.length==3) {
					d_min_global=Math.min(d[0],d[1]);	
				}
				if(d.length>3) {
					d_min_global=Math.min(d[0],d[1]);
					aux_dmin_global=d_min_global;
					for(int i=1;i<d.length-1;i++) {
					d_min_global=Math.min(aux_dmin_global,d[i]);
					aux_dmin_global=d_min_global;
					}
				}
				return d_min_global;
				}
				public static double [][] dmin_global(int Pj,int Pn,double d[]) {
				double d_min_ij[][]=new double [Pj+Pn][Pj+Pn];	
				//Calcul du dmin_ij
		        double aux_dmin_ij=0;	
				for(int i=0;i<Pj+Pn;i++) {
					for(int ii=0;ii<Pj+Pn;ii++) {
						for(int b=i;b<=ii;b++) {
							
							if(b==i) {
							d_min_ij[i][ii]=d[b];
							aux_dmin_ij=d_min_ij[i][ii];
							}
							if(b>i) {
						    d_min_ij[i][ii]=Math.min(aux_dmin_ij,d[b]);
						    aux_dmin_ij=d_min_ij[i][ii];
							}
							
						}
					}	
					
				}
				return d_min_ij;
				}
				
				public static double Nb_total_postes(int Pj,int Pn,int besoins [][]) {
					double Nb_total_postes=0;	
					for(int i=0;i<Pj+Pn;i++) {
						for(int j=0;j<7;j++) {
							Nb_total_postes+=besoins[i][j];	
						}
					}
					return Nb_total_postes;
					}
				
				public static int Nb_total_contrats(int L,int Nb_agents[]) {
					int Nb_contrats=0;	
						for(int l=0;l<L;l++) {
							if(Nb_agents[l]>0.1) {
							Nb_contrats+=1;	
						}
						}
					return Nb_contrats;
					}
				
				public static double[] ratioagents(double nb_contrats,int L,int Nb_agents[],double Ratio[]) {
					double ratio_agents2[]=new double[(int)nb_contrats];
					for(int i=0;i<nb_contrats;i++) {
						ratio_agents2[i]=0;	
					}
						for(int l=0;l<L;l++) {
						if(Nb_agents[l]>0.1) {
							for(int ll=0;ll<nb_contrats;ll++) {
								if(ratio_agents2[ll]<0.1) {
								ratio_agents2[ll]=Ratio[l];
								break;
								}
							}
						}
						}
					return ratio_agents2;
					}
								
				public static double[] ratiodim(double nb_contrats,int L,int Nb_agents[],double Ratio_dim[]) {
					double ratio_agents2[]=new double[(int)nb_contrats];
					for(int i=0;i<nb_contrats;i++) {
						ratio_agents2[i]=0;	
					}
						for(int l=0;l<L;l++) {
						if(Nb_agents[l]>0.1) {
							for(int ll=0;ll<nb_contrats;ll++) {
								if(ratio_agents2[ll]<0.1) {
								ratio_agents2[ll]=Ratio_dim[l];
								break;
								}
							}
						}
						}
					return ratio_agents2;
					}
			
				public static int[] nbAgents(int nb_contrats,int L,int Nb_agents[]) {
					int nombre_agents[]=new int[nb_contrats];
					for(int i=0;i<nb_contrats;i++) {
						nombre_agents[i]=0;	
					}
						for(int l=0;l<L;l++) {
						if(Nb_agents[l]>0.1) {
							for(int ll=0;ll<nb_contrats;ll++) {
								if(nombre_agents[ll]<0.1) {
									nombre_agents[ll]=Nb_agents[l];
								break;
								}
							}
						}
						}
					return nombre_agents;
					}
				
				
				
				
				  public static int[] nb_Dim_cycle(int nb_contrats, double Ratio_dim[], int k) {
					int Nb_dimanches_agent[]=new int[nb_contrats];
					for(int l=0;l<nb_contrats;l++) {
				    	if (k%2!=0) {
							Nb_dimanches_agent[l]=(int)Math.floor((k-1)*Ratio_dim[l]/2);
							}
							else {
						    Nb_dimanches_agent[l]=(int)Math.floor(k*Ratio_dim[l]/2);
							}
				    }
					return Nb_dimanches_agent;
					}
				  
				   public static int[][] Coef_couvert(int Pj,int Pn, int NbJcamax) {
						int [][]Coef_couverture=new int[Pj+Pn][NbJcamax];
						for(int poste=0;poste<Pj+Pn;poste++) {
						Coef_couverture[poste][NbJcamax-1]=1;
						int ld=Coef_couverture[poste][NbJcamax-1];
						for(int i=NbJcamax-2;i>=0;i--) {
							Coef_couverture[poste][i]=ld+1;
							ld=ld+Coef_couverture[poste][i];
						}
						}
						return Coef_couverture;
						}
				   
				   public static double[][][] Matrix_poids(int Pj,int Pn, int J, double [][]Besoins_cycle) {
					    double [][][] MP=new double[Pj+Pn][Pj+Pn][J];
					    double ns_j[]=new double[J];
					    double na_j[]=new double[J];
					    double nsc[][][]=new double[Pj+Pn][Pj+Pn][J];
					    double nac[][][]=new double[Pj+Pn][Pj+Pn][J];
					    double ns_jxna_j[]=new double[J];
					    for(int j=0;j<J;j++) {
					    	for(int i=0; i<Besoins_cycle.length;i++) {
					    		if(Besoins_cycle[i][j]>0) {
					    		ns_j[j]++;
					    		na_j[j]+=Besoins_cycle[i][j];
					    		}
					    	}
				    		for(int a=0;a<Pj+Pn;a++) {
				    			for(int b=a;b<Pj+Pn;b++) {
				    				nsc[a][b][j]=b-a+1;
				    				for(int q=a;q<=b;q++) {
				    					nac[a][b][j]+=Besoins_cycle[q][j];
				    				}
				    				
				    			}
				    		}
					    	ns_jxna_j[j]=ns_j[j]*na_j[j];	
					    }
					    int mincommult=mcm(ns_jxna_j);
					    
					    for(int j=0;j<J;j++) {
					    		for(int a=0;a<Pj+Pn;a++) {
					    			for(int b=a;b<Pj+Pn;b++) {
					    				MP[a][b][j]=(nsc[a][b][j]/ns_j[j])*(nac[a][b][j]/na_j[j])*mincommult;
					    			}		
					    		}
					    }
					    return MP;
					    
					}

					public static double alpha_value(double [][]Data_p,int Pj,int Pn,double Ratio[]) {
						   double alpha=0;
						   double alpha_temp[]=new double[Data_p[0].length];
						   if(Data_p[0].length==0) {
						   alpha=0;   
						   }
						   else {
							   
						   for(int i=0;i<Pj+Pn;i++) {
							   for(int j=1;j<Data_p[0].length;j++) {
								   alpha_temp[j]=Math.max(Math.abs((Data_p[i][j]/Data_p[i][0])-Ratio[j]),alpha_temp[j]);   
							   }
						   }
						   for(int j=1;j<Data_p[0].length;j++) {
						   alpha=Math.max(alpha, alpha_temp[j]);
						   }
						   
						   }
						   return alpha; 
						   
					   }
					
				
					   public static int mcm(double numbers[]) {
						   double mincm=1;
						   boolean found=false;
						   while(found==false) {
							   int contador=0;
							   
							   for(int j=0;j<numbers.length;j++) {
								   if(numbers[j]==0) {
									   contador++;
								   }
								   else {
								   		if(mincm%numbers[j]==0) {
								   			contador++;
								   		}
								   }
							   }
							   if(contador==numbers.length) {
								   found=true;
							   }
							   else {
								   mincm++;
								   
							   }
						   }
						   
						   return (int)mincm;
					   }

					public static int nb_shifts(String directory) {
						int P=0;
						String temp="";
						int count_line=1;
						int count_chracter=0;
						
						try {
							FileReader entrada= new FileReader(directory);
						
							int c=0;
							while(count_line<2) {
								c=entrada.read();
								
								char code=(char)c;
								if(count_line==1) {
									count_chracter++;
								}
								
								if(count_chracter>2 && c!=59) {
								temp=temp+Character.toString(code);
								}
								
								if(c==59){
									count_line++;
								}
								
							}
						} 
						catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println("error");
						}
						P=Integer.valueOf(temp);
						return P;
					}
					
					public static int[][] requeriments(int P,String directory) {
						int r[][]=new int[P][7];
						String temp[][]=new String[P][7];
						for(int ii=0;ii<P;ii++) {
							for(int jj=0;jj<7;jj++) {
								temp[ii][jj]="";
							}
						}
						int count_line=1;
						int count_chracter=0;
						int i=-1;
						int j=-1;
						try {
							FileReader entrada= new FileReader(directory);
						
							int c=0;
							while(count_line<3) {
								c=entrada.read();
								
								char code=(char)c;
								if(count_line==2) {
									count_chracter++;
								
								if(verification_int(c)) {
									temp[i][j]=temp[i][j]+Character.toString(code);
								}
								if(c==123) {
									j=0;
									i++;
								}
								if(c==44) {
									j++;
								}

								}
								
								if(c==59){
									count_line++;
								}
								
							}
						} 
						catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println("error");
						}

						for(int ii=0;ii<P;ii++) {
							for(int jj=0;jj<7;jj++) {
								r[ii][jj]=Integer.valueOf(temp[ii][jj]);
							}
						}
						return r;
					}	
					
					public static double[] duration(int P,String directory) {
						double d[]=new double[P];
						String temp[]=new String[P];
						for(int ii=0;ii<P;ii++) {
								temp[ii]="";

						}
						int count_line=1;
						int count_chracter=0;
						int i=0;
						try {
							FileReader entrada= new FileReader(directory);
						
							int c=0;
							while(count_line<4) {
								c=entrada.read();
								
								char code=(char)c;
								if(count_line==3) {
									count_chracter++;
								
								if(verification_double(c)) {
									temp[i]=temp[i]+Character.toString(code);
								}

								if(c==44) {
									i++;
								}

								}
								
								if(c==59){
									count_line++;
								}
								
							}
						} 
						catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println("error");
						}

						for(int ii=0;ii<P;ii++) {
								d[ii]=Double.valueOf(temp[ii]);
						}
						return d;
					}

					public static double[] start(int P,String directory) {
						double s[]=new double[P];
						String temp[]=new String[P];
						for(int ii=0;ii<P;ii++) {
								temp[ii]="";

						}
						int count_line=1;
						int count_chracter=0;
						int i=0;
						try {
							FileReader entrada= new FileReader(directory);
						
							int c=0;
							while(count_line<5) {
								c=entrada.read();
								
								char code=(char)c;
								if(count_line==4) {
									count_chracter++;
								
								if(verification_double(c)) {
									temp[i]=temp[i]+Character.toString(code);
								}

								if(c==44) {
									i++;
								}

								}
								
								if(c==59){
									count_line++;
								}
								
							}
						} 
						catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println("error");
						}

						for(int ii=0;ii<P;ii++) {
								s[ii]=Double.valueOf(temp[ii]);
						}
						return s;
					}
					
					public static double[] ending(int P,String directory) {
						double en[]=new double[P];
						String temp[]=new String[P];
						for(int ii=0;ii<P;ii++) {
								temp[ii]="";

						}
						int count_line=1;
						int count_chracter=0;
						int i=0;
						try {
							FileReader entrada= new FileReader(directory);
						
							int c=0;
							while(count_line<6) {
								c=entrada.read();
								
								char code=(char)c;
								if(count_line==5) {
									count_chracter++;
								
								if(verification_double(c)) {
									temp[i]=temp[i]+Character.toString(code);
								}

								if(c==44) {
									i++;
								}

								}
								
								if(c==59){
									count_line++;
								}
								
							}
						} 
						catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println("error");
						}

						for(int ii=0;ii<P;ii++) {
								en[ii]=Double.valueOf(temp[ii]);
						}
						return en;
					}
					
					public static String[] sn(int P,String directory) {
						String shift_name[]=new String[P];
						for(int ii=0;ii<P;ii++) {
							shift_name[ii]="";

						}
						int count_line=1;
						boolean start=false;
						int i=0;
						try {
							FileReader entrada= new FileReader(directory);
						
							int c=0;
							while(count_line<7) {
								c=entrada.read();
								
								char code=(char)c;
								if(count_line==6) {
									if(c==125) {
										start=false;
									}
								
								if(start && c!= 44 && c!=34) {
									shift_name[i]=shift_name[i]+Character.toString(code);
								}

								if(c==44) {
									i++;
								}

								if(c==123) {
									start=true;
								}
								
								}
								
								if(c==59){
									count_line++;
								}
								
							}
						} 
						catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println("error");
						}


						return shift_name;
					}
					
					
					public static double rs(String directory) {
						double rs=0;
						String temp="";
						int count_line=1;
						
						try {
							FileReader entrada= new FileReader(directory);
						
							int c=0;
							while(count_line<8) {
								c=entrada.read();
								
								char code=(char)c;
								
								if(verification_double(c) && count_line==7) {
								temp=temp+Character.toString(code);
								}
								
								if(c==59){
									count_line++;
								}
								
							}
						} 
						catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println("error");
						}
						rs=Double.valueOf(temp);
						return rs;
					}
					
					public static double bs(String directory) {
						double bs=0;
						String temp="";
						int count_line=1;
						
						try {
							FileReader entrada= new FileReader(directory);
						
							int c=0;
							while(count_line<9) {
								c=entrada.read();
								
								char code=(char)c;
								
								if(verification_double(c) && count_line==8) {
									temp=temp+Character.toString(code);
								}
								
								if(c==59){
									count_line++;
								}
								
							}
						} 
						catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println("error");
						}
						bs=Double.valueOf(temp);
						return bs;
					}
					
					public static int nc(String directory) {
						int nc=0;
						String temp="";
						int count_line=1;
						
						try {
							FileReader entrada= new FileReader(directory);
						
							int c=0;
							while(count_line<10) {
								c=entrada.read();
								
								char code=(char)c;
								
								if(verification_int(c) && count_line==9) {
									temp=temp+Character.toString(code);
								}
								
								if(c==59){
									count_line++;
								}
								
							}
						} 
						catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println("error");
						}
						nc=Integer.valueOf(temp);
						return nc;
					}

					public static int[] nb_agents(String directory) {
						int nb_agents[]=new int[7];
						String temp[]=new String[7];
						for(int ii=0;ii<7;ii++) {
								temp[ii]="";

						}
						int count_line=1;
						int count_chracter=0;
						int i=0;
						try {
							FileReader entrada= new FileReader(directory);
						
							int c=0;
							while(count_line<11) {
								c=entrada.read();
								
								char code=(char)c;
								if(count_line==10) {
									count_chracter++;
								
								if(verification_int(c)) {
									temp[i]=temp[i]+Character.toString(code);
								}

								if(c==44) {
									i++;
								}

								}
								
								if(c==59){
									count_line++;
								}
								
							}
						} 
						catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println("error");
						}

						for(int ii=0;ii<7;ii++) {
								nb_agents[ii]=Integer.valueOf(temp[ii]);
						}
						return nb_agents;
					}
					
					public static boolean verification_int(int c) {
						boolean content=false;
						for(int i=48;i<=57;i++) {
							if(c==i) {
								content=true;
							}
						}
						return content;
					}
					
					public static boolean verification_double(int c) {
						boolean cont=false;
						for(int i=48;i<=57;i++) {
							if(c==i) {
								cont=true;
							}
						}
						if(c==46) {
							cont=true;
						}
						return cont;
					}	

					public static int Pm(String directory) {
						int Pm=0;
						String temp="";
						int count_line=1;
						
						try {
							FileReader entrada= new FileReader(directory);
						
							int c=0;
							while(count_line<12) {
								c=entrada.read();
								
								char code=(char)c;
								
								if(verification_int(c) && count_line==11) {
									temp=temp+Character.toString(code);
								}
								
								if(c==59){
									count_line++;
								}
								
							}
						} 
						catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println("error");
						}
						Pm=Integer.valueOf(temp);
						return Pm;
					}	
					
					public static int Pj(String directory) {
						int Pj=0;
						String temp="";
						int count_line=1;
						
						try {
							FileReader entrada= new FileReader(directory);
						
							int c=0;
							while(count_line<13) {
								c=entrada.read();
								
								char code=(char)c;
								
								if(verification_int(c) && count_line==12) {
									temp=temp+Character.toString(code);
								}
								
								if(c==59){
									count_line++;
								}
								
							}
						} 
						catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println("error");
						}
						Pj=Integer.valueOf(temp);
						return Pj;
					}	
					
					public static int Ps(String directory) {
						int Ps=0;
						String temp="";
						int count_line=1;
						
						try {
							FileReader entrada= new FileReader(directory);
						
							int c=0;
							while(count_line<14) {
								c=entrada.read();
								
								char code=(char)c;
								
								if(verification_int(c) && count_line==13) {
									temp=temp+Character.toString(code);
								}
								
								if(c==59){
									count_line++;
								}
								
							}
						} 
						catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println("error");
						}
						Ps=Integer.valueOf(temp);
						return Ps;
					}
					
					public static int Pn(String directory) {
						int Pn=0;
						String temp="";
						int count_line=1;
						
						try {
							FileReader entrada= new FileReader(directory);
						
							int c=0;
							while(count_line<15) {
								c=entrada.read();
								
								char code=(char)c;
								
								if(verification_int(c) && count_line==14) {
									temp=temp+Character.toString(code);
								}
								
								if(c==59){
									count_line++;
								}
								
							}
						} 
						catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println("error");
						}
						Pn=Integer.valueOf(temp);
						return Pn;
					}
					
					
					public static int[] Dis(String directory) {
						int dis[]=new int[12];
						String temp[]=new String[12];
						for(int ii=0;ii<12;ii++) {
								temp[ii]="";
						}
						int count_line=1;
						int count_chracter=0;
						int i=0;
						try {
							FileReader entrada= new FileReader(directory);
						
							int c=0;
							while(count_line<16) {
								c=entrada.read();
								
								char code=(char)c;
								if(count_line==15) {
									count_chracter++;
								
								if(verification_int(c)) {
									temp[i]=temp[i]+Character.toString(code);
								}

								if(c==44) {
									i++;
								}

								}
								
								if(c==59){
									count_line++;
								}
								
							}
						} 
						catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println("error");
						}

						for(int ii=0;ii<12;ii++) {
								dis[ii]=Integer.valueOf(temp[ii]);
						}
						return dis;
					}
									
					
					public static int total_besoins(int r[][]) {
						int total=0;
						for(int i=0;i<r.length;i++) {
							for(int j=0;j<r[i].length;j++) {
								total+=r[i][j];
							}
						}
						return total;
					}

					  public static double ecart_carr(double []Data) {
						   double ecart=0;
						   double media=0;
						   for(int i=0; i<Data.length;i++) {
						   media+=Data[i];
						   }
						   media=media/Data.length;
						   for(int i=0; i<Data.length;i++) {
						   ecart+=Math.pow(Data[i]-media, 2);
						   }
						   ecart=ecart/(Data.length-1);   
						   return ecart;   
						}
}
