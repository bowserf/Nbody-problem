package fr.bowserf.nbodyproblem;

import android.content.Context;

import android.support.annotation.NonNull;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.Type;

public class CalculationGPU extends ComputationFunction {

    @SuppressWarnings("unused")
    private static final String TAG = "CALCULATION_GPU";

    private ScriptC_ncorps mScript;
    private Allocation mNewPositionsAlloc;
    private Allocation mRowIndicesAlloc;

    public CalculationGPU(final @NonNull Context context, final int nbPoints) {
        super(nbPoints);

        final RenderScript rs = RenderScript.create(context);

        final int rowWidth = 3;
        final int[] rowIndices = new int[N];
        //on crée un tableau de taille N contenant les chiffres de 3 en 3
        for (int i = 0; i < N; i++) {
            rowIndices[i] = i * rowWidth;
        }

        mRowIndicesAlloc = Allocation.createSized(rs, Element.I32(rs), N, Allocation.USAGE_SCRIPT);

        final Type t = new Type.Builder(rs, Element.F32(rs)).setX(N * 3).create();
        final Allocation positionAlloc = Allocation.createTyped(rs, t);
        final Allocation vitesseAlloc = Allocation.createTyped(rs, t);
        mNewPositionsAlloc = Allocation.createTyped(rs, t);

        final Type t2 = new Type.Builder(rs, Element.F32(rs)).setX(N).create();
        final Allocation masseAlloc = Allocation.createTyped(rs, t2);

        initPosition(Constantes.MIN_POSITION, Constantes.MAX_POSITION);
        initMasse(Constantes.MIN_MASSE, Constantes.MAX_MASSE);
        initVitesse();

        mRowIndicesAlloc.copyFrom(rowIndices);
        positionAlloc.copyFrom(p);
        masseAlloc.copyFrom(m);
        vitesseAlloc.copyFrom(v);

        mScript = new ScriptC_ncorps(rs);

        mScript.set_G(Constantes.G);
        mScript.set_N(N);
        mScript.set_epsilon(Constantes.EPSILON);

        mScript.bind_positions(positionAlloc);
        mScript.bind_newPositions(mNewPositionsAlloc);

        mScript.bind_mass(masseAlloc);
        mScript.bind_speed(vitesseAlloc);
    }

    public float[] computation() {
        mScript.forEach_root(mRowIndicesAlloc, mRowIndicesAlloc);
        mNewPositionsAlloc.copyTo(p);
        mScript.bind_positions(mNewPositionsAlloc);
        return p;
    }
}
