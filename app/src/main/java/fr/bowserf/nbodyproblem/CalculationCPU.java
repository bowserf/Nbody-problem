package fr.bowserf.nbodyproblem;


import android.support.annotation.NonNull;


public class CalculationCPU{

	private static final String TAG = "CALCULATION_CPU";

	private int N;
	private float m[];
	private Position v[];
	private Position p[];
	private float epsilon = 0.0001f;
	
	public CalculationCPU(final int minP, final int maxP, final float minM, final float maxM){
		
		this.N = ConstantesCalcul.N;
		
		m = new float[N];
		v = new Position[N];
		p = new Position[N];
		
		initPosition(minP, maxP);
		initMasse(minM, maxM);
		initVitesse();
	}

	private float randomVal(final float b_inf, final float b_max){
		return (float) (b_inf + (Math.random() * (b_max - b_inf)));
	}

	private void initPosition(final float minP, final float maxP) {
		for(int i = 0 ; i < N ; i++){
			float x = randomVal(minP, maxP);
			float y = randomVal(minP, maxP);
			float z = randomVal(minP, maxP);
			p[i] = new Position(x, y, z);
		}
	}


	private void initMasse(final float minM, final float maxM) {
		for(int i = 0 ; i < N ; i++){
			m[i] = randomVal(minM, maxM);
		}
	}

	private void initVitesse() {
		for(int i = 0 ; i < N ; i++){
			v[i] = new Position();
		}
	}
	
	private Position soustraction(final @NonNull Position p1, final @NonNull Position p2){
		return new Position(p1.getX() - p2.getX(),
							p1.getY() - p2.getY(),
							p1.getZ() - p2.getZ());
	}
	
	private Position addition(final @NonNull Position p1, final @NonNull Position p2){
		return new Position(p1.getX() + p2.getX(),
							p1.getY() + p2.getY(),
							p1.getZ() + p2.getZ());
	}
	
	private Position mult(final float m, final @NonNull Position p){
		return new Position(m * p.getX(),
							m * p.getY(),
							m * p.getZ());

	}
	
	private float normeAuCarre(final @NonNull Position p1, final @NonNull Position p2){
		return (float)Math.pow(Math.sqrt(Math.pow(p2.getX() - p1.getX(),2) + Math.pow(p2.getY() - p1.getY(),2) + Math.pow(p2.getZ() - p1.getZ(),2)),2);
	}
	
	public void allAccelerations(){

		final Position rep[] = new Position[N];
		
		for(int i = 0 ; i < N ;i++){
			Position acc = new Position();
			Position numerateur = new Position();
			float denominateur;
			for(int j = 0 ; j < N ;j++){
				denominateur = (float)Math.pow(normeAuCarre(p[i],p[j]) + Math.pow(epsilon,2),3/2);
				numerateur = mult(m[j],soustraction(p[i],p[j]));
				acc = addition(acc,mult(1/denominateur, numerateur));
			}	
			v[i] = addition( v[i] , mult((float)9.8,acc));
			rep[i] = addition( v[i] , p[i]);
		}
		p = rep;
	}
	
	public Position[] getP() {
		return p;
	}

	public void setP(Position[] p) {
		this.p = p;
	}
}
