package fr.bowserf.nbodyproblem;


public class CalculationNDK extends ComputationFunction{

    @SuppressWarnings("unused")
    private static final String TAG = "CalculationNDK";

    static{
        System.loadLibrary("nbody");
    }

    private native void init(final int n, final float epsilon, final float G, final float[]p,
                                final float[]v, final float[]m);
    private native float[] computeNewPosition();
    private native void freeNativeMemory();

    public CalculationNDK(final int nbPoints) {
        super(nbPoints);

        initPosition(Constantes.MIN_POSITION, Constantes.MAX_POSITION);
        initMasse(Constantes.MIN_MASSE, Constantes.MAX_MASSE);
        initVitesse();

        init(nbPoints, Constantes.EPSILON, Constantes.G, p, v, m);
    }

    @Override
    public float[] computation() {
        return computeNewPosition();
    }

    @Override
    public void freeMemory() {
        freeNativeMemory();
    }
}