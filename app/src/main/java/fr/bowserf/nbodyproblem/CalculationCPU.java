package fr.bowserf.nbodyproblem;

import android.support.annotation.NonNull;

import java.util.Arrays;

public class CalculationCPU extends ComputationFunction {

    @SuppressWarnings("unused")
    private static final String TAG = "CalculationCPU";

    private static final float SQUARE_EPSILON = (float) Math.pow(Constantes.EPSILON, 2);
    private static final float THREE_AND_HALF = 3/2;

    public CalculationCPU(final int nbPoints) {
        super(nbPoints);

        initPosition(Constantes.MIN_POSITION, Constantes.MAX_POSITION);
        initMass(Constantes.MIN_MASSE, Constantes.MAX_MASSE);
        initSpeed();
    }

    private float[] subtraction(final @NonNull float[] p1, final @NonNull float[] p2) {
        final float x = p2[0] - p1[0];
        final float y = p2[1] - p1[1];
        final float z = p2[2] - p1[2];
        return new float[]{x, y, z};
    }

    private float[] addition(final @NonNull float[] p1, final @NonNull float[] p2) {
        final float x = p1[0] + p2[0];
        final float y = p1[1] + p2[1];
        final float z = p1[2] + p2[2];
        return new float[]{x, y, z};
    }

    private float[] multiplication(final float m, final @NonNull float[] p) {
        final float x = m * p[0];
        final float y = m * p[1];
        final float z = m * p[2];
        return new float[]{x, y, z};
    }

    private float squaredNorm(final @NonNull float[] p1, final @NonNull float[] p2) {
        return (float) Math.sqrt(Math.pow(p2[0] - p1[0], 2) + Math.pow(p2[1] - p1[1], 2) +
                Math.pow(p2[2] - p1[2], 2));
    }

    public float[] computation() {

        final float[] rep = new float[N * 3];

        for (int i = 0; i < N; i++) {
            final int cpt = i * 3;
            float[] acc = new float[]{0, 0, 0};
            for (int j = 0; j < N; j++) {
                final int cptJ = j * 3;
                final float squaredNorm = squaredNorm(Arrays.copyOfRange(p, cpt, cpt + 3), Arrays.copyOfRange(p, cptJ, cptJ + 3));
                float denominateur = (float) Math.pow(squaredNorm + SQUARE_EPSILON, THREE_AND_HALF);
                float[] numerateur = multiplication(m[j], subtraction(Arrays.copyOfRange(p, cpt, cpt + 3), Arrays.copyOfRange(p, cptJ, cptJ + 3)));
                acc = addition(acc, multiplication(1 / denominateur, numerateur));
            }
            System.arraycopy(addition(Arrays.copyOfRange(v, cpt, cpt + 3), multiplication(Constantes.G, acc)), 0, v, cpt, 3);
            System.arraycopy(addition(Arrays.copyOfRange(v, cpt, cpt + 3), Arrays.copyOfRange(p, cpt, cpt + 3)), 0, rep, cpt, 3);
        }
        p = rep;
        return p;
    }
}
