package fr.bowserf.nbodyproblem;


import java.util.Arrays;

public abstract class ComputationFunction {

    protected int N;
    protected float m[];
    protected float v[];
    protected float p[];

    public ComputationFunction(final int nbPoints){
        this.N = nbPoints;
        this.m = new float[N];
        this.v = new float[N * 3];
        this.p = new float[N * 3];
    }

    protected float randomVal(final float inf, final float max) {
        return (float)(inf + (Math.random() * (max - inf)));
    }

    protected void initPosition(final float minP, final float maxP) {
        final int iterationNumber = N * 3;
        for (int i = 0; i < iterationNumber; i++) {
            p[i] = randomVal(minP, maxP);
        }
    }

    protected void initMass(final float minM, final float maxM) {
        for (int i = 0; i < N; i++) {
            m[i] = randomVal(minM, maxM);
        }
    }

    protected void initSpeed() {
        Arrays.fill(v, 0);
    }

    /**
     * Method to implement in order to compute new bodies position.
     * @return float[] representing bodies coordinates. One position is represent by the values
     * (x, y, z) so the float array has a size of N * 3.
     */
    public abstract float[] computation();

    /**
     * Override this method if some variables need to be free (ex: native code)
     */
    public void freeMemory(){}

    public float[] getInitialPosition() {
        return p;
    }

    public int getNumberBodies() {
        return N;
    }
}
