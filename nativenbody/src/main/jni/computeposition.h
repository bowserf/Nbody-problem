#include <jni.h>

#ifndef _Included_fr_bowserf_nbodyproblem_CalculationNDK
#define _Included_fr_bowserf_nbodyproblem_CalculationNDK
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_fr_bowserf_nbodyproblem_CalculationNDK_init
(JNIEnv *, jobject, jint, jfloat, jfloat, jfloatArray, jfloatArray, jfloatArray);

JNIEXPORT void JNICALL Java_fr_bowserf_nbodyproblem_CalculationNDK_freeNativeMemory
(JNIEnv *, jobject);

JNIEXPORT jfloatArray JNICALL Java_fr_bowserf_nbodyproblem_CalculationNDK_computeNewPosition
(JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif