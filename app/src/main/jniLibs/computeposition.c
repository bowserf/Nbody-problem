#include <math.h>
#include <android/log.h>
#include <stdio.h>
#include <stdlib.h>

#include "computeposition.h"

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "computeposition-ndk", __VA_ARGS__))

float epsilon = 0;
long N;

float *m = NULL;
float *v = NULL;
float *p = NULL;


float randomVal(float b_inf, float b_max){
	return (float) (( rand()/(double)RAND_MAX ) * (b_max-b_inf) + b_inf);
}

void initPosition(float minP, float maxP, int N) {
	int i;
	p = (float*)malloc((N*3) * sizeof(float));
	for(i = 0 ; i < N*3 ; i++){
		p[i] = randomVal(minP, maxP);
	}
}

void initMasse(float minM, float maxM, int N) {
	int i;
	m = (float*)malloc(N * sizeof(float));
	for(i = 0 ; i < N ; i++){
		m[i] = randomVal(minM, maxM);
	}
}

void initVitesse(int N) {
	int i;
	v = (float*)malloc((N*3) * sizeof(float));
	for(i = 0 ; i < N*3 ; i++){
		v[i] = 0;
	}
}

JNIEXPORT jfloatArray JNICALL Java_com_bowserf_nbody_CalculationNDK_initializeValues
(JNIEnv *env, jobject obj, jint _N, jfloat _epsilon, jint _minP, jint _maxP, jfloat _minM, jfloat _maxM){

	int minP = (int)_minP;
	int maxP = (int)_maxP;
	float minM = (float)_minM;
	float maxM = (float)_maxM;
	epsilon = (float)_epsilon;
	N = (int)_N;

	jfloatArray result;
	result = (*env)->NewFloatArray(env, N*3);
	if (result == NULL) {
		return NULL;
	}

	initPosition(minP, maxP, N);
	initMasse(minM, maxM, N);
	initVitesse(N);

	(*env)->SetFloatArrayRegion(env, result, 0, N*3, p);
	return result;
}


float* soustraction(float *p1, float *p2){
	// We realize a dynamic allocation because if we just use an array, our array will be destroy at the end of the function
	float *rep = (float*)malloc(3 * sizeof(float));
	rep[0] = p2[0] - p1[0];
	rep[1] = p2[1] - p1[1];
	rep[2] = p2[2] - p1[2];
	return rep;
}

float* addition(float *p1, float *p2){
	float *rep = (float*)malloc(3 * sizeof(float));
	//LOGI("p1 acc : %f", p1[0]);
	rep[0] = p1[0] + p2[0];
	rep[1] = p1[1] + p2[1];
	rep[2] = p1[2] + p2[2];
	//LOGI("rep acc : %f", rep[0]);
	return rep;
}

float* mult(float m, float *p){
	float *rep = (float*)malloc(3 * sizeof(float));
	rep[0] = m * p[0];
	rep[1] = m * p[1];
	rep[2] = m * p[2];
	return rep;
}

float norme_au_carre(float *p1, float *p2){
	return  (float) pow( sqrt( pow(p2[0]- p1[0], 2 ) + pow( p2[1] - p1[1], 2 ) + pow( p2[2] - p1[2], 2 ) ), 2);
}


JNIEXPORT jfloatArray JNICALL Java_com_bowserf_nbody_CalculationNDK_computeNewPosition(JNIEnv *env, jobject obj, jfloatArray test){

	int i, j;

	//will contain new position
	float *rep = (float*)malloc((N*3) * sizeof(float));

	//we convert from jfloatArray to float array
	float *pp = (*env)->GetFloatArrayElements(env, test, 0);

	//result array will contain array of all new positions
	jfloatArray result;
	result = (*env)->NewFloatArray(env, N*3);
	if (result == NULL) {
		return NULL;
	}

	//LOGI("valeur de N : %d et p0 : %f", N,pp[0]);

	for(i = 0 ; i < N ; i++){

		float *acc = (float*)malloc(3 * sizeof(float));
		acc[0] = 0;
		acc[1] = 0;
		acc[2] = 0;
		float denominateur = 0;
		float *numerateur = NULL;


		for(j = 0 ; j < N ; j++){

			denominateur = (float) pow( norme_au_carre( &pp[i * 3], &pp[j * 3] ) + pow( epsilon, 2 ), 3/2 );

			numerateur = mult( m[j], soustraction( &pp[i * 3], &pp[j * 3] ) );

			float *resultMult = mult( 1/denominateur, numerateur );

			acc = addition( acc, resultMult );
			if(j < 2){
			LOGI("denominateur : %f", denominateur);
			LOGI("numerateur : %f", numerateur[0]);
			LOGI("resultMult : %f", resultMult[0]);
			LOGI("acc : %f", acc[0]);
			}

		}


		//we compute the new speed
		float *tmp_v = addition( &v[i * 3] , mult( (float) 9.8, acc) );

		v[i * 3] = tmp_v[0];
		v[i * 3 + 1] = tmp_v[1];
		v[i * 3 + 2] = tmp_v[2];

		float *tmp_rep = addition( &v[i * 3] , &pp[i * 3] );

		rep[i * 3] = tmp_rep[0];
		rep[i * 3 + 1] = tmp_rep[1];
		rep[i * 3 + 2] = tmp_rep[2];

		free(tmp_rep);
		free(acc);
		free(numerateur);
		free(tmp_v);
	}

	for(i = 0 ; i < N * 3 ; i++){
		p[i] = rep[i];
	}
	//LOGI("valeur de N : %d et PPPP : %f", N,p[0]);
	//free(rep);

	(*env)->SetFloatArrayRegion(env, result, 0, N*3, p);
	//(*env)->ReleaseFloatArrayElements(env, test, pp, 0);
	return result;
}
