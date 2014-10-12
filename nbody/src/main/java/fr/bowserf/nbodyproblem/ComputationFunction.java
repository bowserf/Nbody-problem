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

    protected float randomVal(final float b_inf, final float b_max) {
        return (float)(b_inf + (Math.random() * (b_max - b_inf)));
    }

    protected void initPosition(final float minP, final float maxP) {
        for (int i = 0; i < N * 3; i++) {
            p[i] = randomVal(minP, maxP);
        }
    }

    protected void initMasse(final float minM, final float maxM) {
        for (int i = 0; i < N; i++) {
            m[i] = randomVal(minM, maxM);
        }
    }

    protected void initVitesse() {
        for (int i = 0; i < N * 3; i++) {
            v[i] = 0;
        }
    }

    public abstract float[] computation();

    public float[] getInitialPosition() {
        return p;
    }
}
