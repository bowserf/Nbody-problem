package fr.bowserf.nbodyproblem;

import android.util.Log;

public class CalculationNDK {
	
	private static final String TAG= "CALCULATION_NDK";
	private float p[];

	static{
		System.loadLibrary("computeposition");
	}

	private native float[] initializeValues(int N, float epsilon, int minP, int maxP, float minM, float maxM);
	private native float[] computeNewPosition(float p[]);

	public CalculationNDK(int minP, int maxP, float minM, float maxM){
		p = initializeValues(ConstantesCalcul.N, ConstantesCalcul.epsilon, minP, maxP, minM, maxM);
	}

	public void all_accelerations(){
		p = computeNewPosition(p);
		/*for(int i = 0 ; i < ConstantesCalcul.N ; i++){*/
			Log.e(TAG, " 0 : " + p[ 0 ] +" 1 : " + p[ 1 ] + " 2 : "+ p[ 2 ] );
		//}*/
	}
	
	//-------
	// Getter
	//-------
	
	public float[] getP() {
		return p;
	}
}
