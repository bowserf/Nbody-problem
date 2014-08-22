package fr.bowserf.nbodyproblem;

import android.content.Context;
import android.support.annotation.NonNull;


public class RunComputation  extends Thread{

  @SuppressWarnings("unused")
	private static final String TAG = "RUN_COMPUTATION";

	private boolean mRunning = false;

	private CalculationCPU calcul;
	private CalculationGPU gpu;
	//private CalculationNDK ndk;

	public RunComputation(final @NonNull Context context){
		initialisation(context);
	}
	
	public void initialisation(final @NonNull Context context){
    calcul = new CalculationCPU(-1, 1, 0.000001f, 0.000005f);
		gpu = new CalculationGPU(-1, 1, 0.000001f, 0.000005f, context);
		//ndk = new CalculationNDK(-1, 1, 0.000001f, 0.000005f);
	}

	public void startThread(){
		mRunning = true;
	}

	public void stopThread(){
		mRunning = false;
	}

	@Override
	public void run() {
		while(true){
			if(mRunning){
				final long t1 = System.currentTimeMillis();
				if(ConstantesCalcul.computeMethod == 1) gpu.calculate();
				else if(ConstantesCalcul.computeMethod == 0) calcul.allAccelerations();
				//else ndk.all_accelerations();
				final long t2 = System.currentTimeMillis();
			}
		}
	}
	
	//====================
	// Getters and Setters
	//====================
	public boolean isRunning() {
		return mRunning;
	}
	
	/*public CalculationNDK getNdk() {
		return ndk;
	}*/

	public CalculationGPU getGpu() {
		return gpu;
	}

	public CalculationCPU getCalcul() {
		return calcul;
	}
}
