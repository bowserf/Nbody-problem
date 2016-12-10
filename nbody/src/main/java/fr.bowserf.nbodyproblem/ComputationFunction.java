package fr.bowserf.nbodyproblem;


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
        final int iterationNumber = N * 3;
        for (int i = 0; i < iterationNumber; i++) {
            v[i] = 0;
        }
    }

    public abstract float[] computation();
    public void freeMemory(){}

    public float[] getInitialPosition() {
        return p;
    }
}
