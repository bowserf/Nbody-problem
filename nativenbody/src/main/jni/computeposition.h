#include <jni.h>

#ifndef CalculationNDK
#define CalculationNDK

#ifdef __cplusplus
extern "C" {
#endif

void Java_fr_bowserf_nbodyproblem_CalculationNDK_init
(JNIEnv *, jobject, jint, jfloat, jfloat, jfloatArray, jfloatArray, jfloatArray);

void Java_fr_bowserf_nbodyproblem_CalculationNDK_freeNativeMemory
(JNIEnv *, jobject);

jfloatArray Java_fr_bowserf_nbodyproblem_CalculationNDK_computeNewPosition
(JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif

#endif //CalculationNDK
