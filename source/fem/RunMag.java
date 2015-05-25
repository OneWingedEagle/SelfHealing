package fem;

import static java.lang.Math.PI;

import java.io.File;

import java.text.DecimalFormat;


import math.Vect;
import math.util;

public class RunMag {

	
	private DecimalFormat formatter=new DecimalFormat("0.00");

	private Vect xp;
	int[] nn=new int[1];

	public static void main(String[] args){
		new Main();
	}

	public void runMag(Model model, Main main){

		
			String folder=model.resultFolder;


			int nTsteps=model.nTsteps;
			
			
			int nBegin=model.nBegin;
			int nEnd=model.nEnd;
			

			int inc=model.nInc;

		/*	Model mx3=new Model(System.getProperty("user.dir") + "\\resultsShrink3DOrthot\\statFrameRough.txt");
			mx3.loadStress(System.getProperty("user.dir") + "\\resultsShrink3DOrthot\\stressStatFrameRough.txt");*/
			
/*			int ix=0;
	for(int i=model.region[8].getFirstEl();i<=model.region[8].getLastEl();i++){
				ix++;
				Vect ss=mx3.element[ix].getStress();
				model.element[i].setDeformable(true);
				model.element[i].setStress(ss);
			}
	for(int i=model.region[16].getFirstEl();i<=model.region[16].getLastEl();i++){
		ix++;
		Vect ss=mx3.element[ix].getStress();
		model.element[i].setDeformable(true);
		model.element[i].setStress(ss);
	}*/
	//model.writeStress(System.getProperty("user.dir") + "\\resultsShrink3DOrthot\\stressStMotRough.txt");
			
			
			if(model.loadPrevMag){
				
				model.setMagBC();
				
				String initfile = System.getProperty("user.dir")+"\\initxMag.txt";
				model.loader.loadPrevMag(model,initfile);

				}
			

			// log mode: 0: instantaneous torque, 1: average torque vs. beta, 2: emfs

			
			
			
		//	MatSolver msolver=new MatSolver();
			



			if(model.loadFlux) {model.magAnalysis=false;}

			
			String dispFolder="";
			String fluxFolder="";

			if(model.saveDisp){
					dispFolder = System.getProperty("user.dir")+"\\disps"+model.defMode;
			
			
				File dfolder = new File(dispFolder);
				if(dfolder.exists())
					util.deleteDir(dfolder);
				dfolder.mkdir();

			}

			if(model.saveForce){
			
			
					dispFolder = System.getProperty("user.dir")+"\\forces";
				File dfolder = new File(dispFolder);
				if(dfolder.exists())
					util.deleteDir(dfolder);
				dfolder.mkdir();

			}

			if(model.saveFlux){
					fluxFolder = System.getProperty("user.dir")+"\\fluxes";
				
				File dfolder = new File(fluxFolder);
				if(dfolder.exists())
					util.deleteDir(dfolder);
				dfolder.mkdir();

			}

			
			if(nTsteps>1)
				model.writeFiles=false;
			else
				model.writeFiles=true;

			boolean writeFiles=model.writeFiles;
			
			
			Vect x=new Vect();

			main.gui.lbX[0].setText("rot ang. ");
			main.gui.lbX[1].setText("Trq. ");

			//	if(model.analysisMode>=0) magAnalysis=true;


			double omega=2*PI*model.freq;

			
			double tav=0;
			
			int nSamples=0;
		
	

				for(int i=nBegin;i<=nEnd;i+=inc){

			
					double beta=0;
				model.setBeta(beta);
					
				double t0=0;
				if(model.numberOfRegions==17 && model.motor)
			
				
				util.pr(model.dt);
				
					model.setJ(t0+(nSamples-1)*model.dt);	

		


					if(!model.loadFlux){
						
						
							model.setMagBC();
							
							if(i==nBegin){
							if(main.console)
								io.Console.redirectOutput(main.gui.paramArea);
							model.reportData();
							if(main.console)
								io.Console.redirectOutput(main.gui.progressArea);
							}
							
	
							if(!model.nonLin || i==nBegin){
								
							
									if(!model.loadPrevMag){
										model.saveAp();		

										// x=new Vect(model.numberOfUnknowns);

										x=model.solveMagLin(i);	

									}
									else
									{
										Vect vk=model.getUnknownA();

								
										 x=new Vect(model.numberOfUnknowns);
										for(int j=0;j<vk.length;j++){
											x.el[j]=vk.el[j];
										}
										for(int j=0;j<model.numberOfUnknownCurrents;j++){
											int nr=model.unCurRegNumb[j];
											x.el[vk.length+j]=model.region[nr].current;

										}
										if(model.nNeutral>0)
										x.el[vk.length+model.numberOfUnknownCurrents]=model.vNeutral;
									}

								}
								else
									x=this.xp;
								
								
						if(model.coupled){

								model.solveCoupled(x);
							}
							else
								if(model.nonLin ){

									
									if(model.analysisMode>0 && i!=nBegin )	{	
								
										model.saveAp();				
									}
							
									
									x=model.solveNonLinear(x,true,i-nBegin);
								}

					/*	if(model.analysisMode>0)
							model.setJe();
*/
				
							this.xp=x.deepCopy();

							if(model.saveFlux){
								String fluxFile = fluxFolder+"\\flux"+i+".txt";
							
								model.writeB(fluxFile);
		
							}


							util.pr(" >>>>>>>>>> Bmax >>>>>>>"+model.Bmax);

						model.resetReluctForce();
						model.setReluctForce();
						
						model.setMSForce();
						
						
				}

					else if(model.loadFlux)

					{	
						
					
						String fluxFile = model.fluxFolderIn+"\\flux"+i+".txt";

						

							model.loadFlux(fluxFile,0);

							model.setReluctForce();

							model.setMSForce();
							
							model.node[12045].getCoord().hshow();


					
					}
					else if(model.loadPotentioal) {

						String vPot = System.getProperty("user.dir")+"\\flux\\vPot"+i+".txt";
						model.loadPotential(vPot);
						model.setB();

					}
					
			
					
					folder=System.getProperty("user.dir") + "\\results";
			
					if(model.dim==2)
						model.setTorque(0,model.rm,1);
						else
							model.setTorque(model.r1,1,1);

					
					
					util.pr("torque >>>>>>>"+model.TrqZ);
	
					
					
								//************************************************
				//	writeFiles=true;


					if( writeFiles /*&& i==nEnd*/){

						model.writeMesh( folder+"\\bun"+i+".txt");
						String fluxFile =  folder+"\\flux"+i+".txt";
						model.writeB(fluxFile);

					}

					if(model.saveForce){
					//	String dispFile =dispFolder+"\\force"+mangs+".txt";
						String forceFile =dispFolder+"\\force"+i+".txt";
						model.writeNodalField(forceFile,model.forceCalcMode);
					}


					main.gui.tfX[1].setText(this.formatter.format(model.TrqZ));

					if(model.solver.terminate){

						break;
					}

			if(model.solver.terminate) break;

			}
			
				
				

				boolean writeInit=false;
				
				if(writeInit){
			
			
				String initfile = System.getProperty("user.dir")+"\\initxMag.txt";
				double[][] arr=new double[model.numberOfUnknowns+1+1][2];
				
				Vect vk=model.getUnknownA();

				Vect vp=model.getUnknownAp();

				for(int j=0;j<vp.length;j++){
					arr[j][0]=vp.el[j];
					arr[j][1]=vk.el[j];
				}
				

				for(int i=0;i<model.numberOfUnknownCurrents;i++){

					int nr=model.unCurRegNumb[i];
					
					arr[i+vp.length][0]=model.region[nr].currentp;
					arr[i+vp.length][1]=model.region[nr].current;


				}

				arr[arr.length-2][0]=model.vNeutral;
				arr[arr.length-2][1]=0;
				
				arr[arr.length-1][0]=0;
				arr[arr.length-1][1]=0;



				model.writer.writeArray(arr,initfile);
			
				}




	}

}