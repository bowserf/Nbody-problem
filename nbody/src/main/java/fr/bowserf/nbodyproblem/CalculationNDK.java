package fr.bowserf.nbodyproblem;


public class CalculationNDK extends ComputationFunction{

    static{
        System.loadLibrary("computeposition");
    }

    private native void init(final int n, final float epsilon, final float G, final float[]p,
                                final float[]v, final float[]m);
    private native float[] computeNewPosition();

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
}
