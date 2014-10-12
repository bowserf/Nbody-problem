#include <math.h>
#include <android/log.h>
#include <stdio.h>
#include <stdlib.h>

#include "computeposition.h"

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "computeposition-ndk", __VA_ARGS__))

jfloat epsilon;
jlong N;
jfloat G;

jfloat *m = NULL;
jfloat *v = NULL;
jfloat *p = NULL;

// pour ne pas avoir a réalouer à chaque fois le tableau et consommer trop de mémoire
static jfloatArray result = NULL;

JNIEXPORT void JNICALL Java_fr_bowserf_nbodyproblem_CalculationNDK_init
(JNIEnv *env, jobject obj, jint _N, jfloat _epsilon, jfloat _G, jfloatArray _p, jfloatArray _v, jfloatArray _m){

	epsilon = (float)_epsilon;
	N = (int)_N;
	G = (float)_G;

	p = (*env)->GetFloatArrayElements(env, _p, 0);
	v = (*env)->GetFloatArrayElements(env, _v, 0);
	m = (*env)->GetFloatArrayElements(env, _m, 0);

    jfloatArray result2;
    result2 = (*env)->NewFloatArray(env, N*3);
    result = (jfloatArray)(*env)->NewGlobalRef(env, result2);
}

float* soustraction(float *p1, float *p2){
	float *rep = (float*)malloc(3 * sizeof(float));
	rep[0] = p2[0] - p1[0];
	rep[1] = p2[1] - p1[1];
	rep[2] = p2[2] - p1[2];
	return rep;
}

float* addition(float *p1, float *p2){
	float *rep = (float*)malloc(3 * sizeof(float));
	rep[0] = p1[0] + p2[0];
	rep[1] = p1[1] + p2[1];
	rep[2] = p1[2] + p2[2];
	return rep;
}

void add(float *p1, float *p2){
	p1[0] += p2[0];
	p1[1] += p2[1];
	p1[2] += p2[2];
}

float* mult(float m, float *p){
	float *rep = (float*)malloc(3 * sizeof(float));
	rep[0] = m * p[0];
	rep[1] = m * p[1];
	rep[2] = m * p[2];
	return rep;
}

float squaredNorm(float *p1, float *p2){
	return  (float) sqrt( pow(p2[0]- p1[0], 2 ) + pow( p2[1] - p1[1], 2 ) + pow( p2[2] - p1[2], 2 ) );
}

JNIEXPORT jfloatArray JNICALL Java_fr_bowserf_nbodyproblem_CalculationNDK_computeNewPosition(JNIEnv *env, jobject obj){

	int i, j;

	float *newPositions = (float*)malloc((N*3) * sizeof(float));

	for(i = 0 ; i < N ; i++){

		float *acc = (float*)malloc(3 * sizeof(float));
		acc[0] = 0;
		acc[1] = 0;
		acc[2] = 0;

		for(j = 0 ; j < N ; j++){
            float square = squaredNorm(p + i * 3, p + j * 3);
			float denominateur = (float) pow(square + pow(epsilon, 2), 3/2);
			float *resultSub = soustraction(p + i * 3, p + j * 3);
			float *numerateur = mult(m[j], resultSub);
			float *resultMult = mult(1/denominateur, numerateur);
			add(acc, resultMult);

			free(resultMult);
			free(resultSub);
			free(numerateur);
		}

		float *tmp_v = addition(v + i * 3, mult(G, acc));

		v[i * 3] = tmp_v[0];
		v[i * 3 + 1] = tmp_v[1];
		v[i * 3 + 2] = tmp_v[2];

		float *tmp_rep = addition(v + i * 3, p + i * 3);

		newPositions[i * 3] = tmp_rep[0];
		newPositions[i * 3 + 1] = tmp_rep[1];
		newPositions[i * 3 + 2] = tmp_rep[2];

		free(tmp_rep);
		free(acc);
		free(tmp_v);
	}

	for(i = 0 ; i < N * 3 ; i++){
		p[i] = newPositions[i];
	}

    free(newPositions);
	(*env)->SetFloatArrayRegion(env, result, 0, N*3, p);
	return result;
}

JNIEXPORT void JNICALL Java_fr_bowserf_nbodyproblem_CalculationNDK_freeNativeMemory(JNIEnv *env, jobject object){
    (*env)->DeleteGlobalRef(env, result);
    result = NULL;
    free(m);
    free(p);
    free(v);
}