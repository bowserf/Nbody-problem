package fr.bowserf.nbodyproblem;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.Type;

public class CalculationGPU {

  @SuppressWarnings("unused")
  private static final String TAG = "CALCULATION_GPU";

  private int N;
  private float p[];
  private float m[];
  private float v[];
  private float epsilon = 0.0001f;

  private ScriptC_ncorps mScript;

  private Allocation pOutAllocation;

  public CalculationGPU(final int minP, final int maxP, final float minM, final float maxM, final @NonNull Context context) {

    this.N = ConstantesCalcul.N;

    final RenderScript rs = RenderScript.create(context);

    int row_width = 3;
    int[] row_indices = new int[N];
    //on crée un tableau de taille N contenant les chiffres de 3 en 3
    for (int i = 0; i < N; i++) {
      row_indices[i] = i * row_width;
    }

    final Allocation row_indices_alloc = Allocation.createSized(rs, Element.I32(rs), N, Allocation.USAGE_SCRIPT);
    row_indices_alloc.copyFrom(row_indices);

    final Type t = new Type.Builder(rs, Element.F32(rs)).setX(N * 3).create();
    final Allocation pInAllocation = Allocation.createTyped(rs, t);
    pOutAllocation = Allocation.createTyped(rs, pInAllocation.getType());
    final Allocation vitesseAlloc = Allocation.createTyped(rs, pInAllocation.getType());

    final Type t2 = new Type.Builder(rs, Element.F32(rs)).setX(N).create();
    final Allocation masseAlloc = Allocation.createTyped(rs, t2);

    p = new float[N * 3];
    v = new float[N * 3];
    m = new float[N];

    initPosition(minP, maxP);
    initVitesse();
    initMasse(minM, maxM);

    pInAllocation.copyFrom(p);
    masseAlloc.copyFrom(m);
    vitesseAlloc.copyFrom(v);

    //TODO problème
    mScript = new ScriptC_ncorps(rs);

    mScript.set_N(N);
    mScript.set_gIn(row_indices_alloc);
    mScript.set_gOut(row_indices_alloc);
    mScript.set_gScript(mScript);
    mScript.set_epsilon(epsilon);
    mScript.bind_pIn(pInAllocation);
    mScript.bind_pOut(pOutAllocation);
    mScript.bind_masse(masseAlloc);
    mScript.bind_vitesse(vitesseAlloc);


  }

  void calculate() {
    mScript.invoke_call();
    pOutAllocation.copyTo(p);
  }

  private float randomVal(final float b_inf, final float b_max) {
    return (float)(b_inf + (Math.random() * (b_max - b_inf)));
  }

  private void initPosition(final float minP, final float maxP) {
    for (int i = 0; i < N * 3; i++) {
      p[i] = randomVal(minP, maxP);
    }
  }

  private void initMasse(final float minM, final float maxM) {
    for (int i = 0; i < N; i++) {
      m[i] = randomVal(minM, maxM);
    }
  }

  private void initVitesse() {
    for (int i = 0; i < N * 3; i++) {
      v[i] = 0;
    }
  }

  public float[] getP() {
    return p;
  }

  public void setP(final @NonNull float[] p) {
    this.p = p;
  }
}
