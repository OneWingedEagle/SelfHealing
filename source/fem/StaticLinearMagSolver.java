package fem;

import math.SpMat;
import math.Vect;


public class StaticLinearMagSolver{
	int stepNumb;
	

	public StaticLinearMagSolver(){	}

	public Vect solve(Model model, int step ){
		
		this.stepNumb=step;

	
	SpMat L=new SpMat();

	Vect x=new Vect(model.numberOfUnknowns);

	model.solver.terminate(false);

	model.magMat.setRHS(model);

if(step==0)
	model.setMagMat();
			

	//=== known values go to right hand side 


	model.RHS=model.RHS.sub(model.HkAk);
	

	SpMat Ks=model.Hs.deepCopy();

	Vect Ci=Ks.scale(model.RHS);
	


		L=Ks.ichol();

		if(model.RHS.abs().max()>1e-8){

			if(!usePrev || model.xp==null){
				x=model.solver.ICCG(Ks,L, model.RHS,model.errCGmax,model.iterMax);
			}
			else{
				//x=model.solver.ICCG(Ks,L, model.RHS,model.errCGmax,model.iterMax,model.xp);
				x=model.solver.err0ICCG(Ks,L, model.RHS,1e-2*model.errCGmax,model.iterMax,model.xp);	

			}
		}

		else
			x=new Vect(x.length);

		model.xp=x.deepCopy();


		x.timesVoid(Ci);

		model.setSolution(x);	
		

		model.setB();	

		System.out.println("Bmax ( linear analysis): "+model.Bmax);

		return x;



}




}