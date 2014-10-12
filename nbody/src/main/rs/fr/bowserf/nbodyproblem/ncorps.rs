#pragma version(1)
#pragma rs java_package_name(fr.bowserf.nbodyproblem)
#pragma rs_fp_imprecise

float *pIn;
float *pOut;
float *vitesse;
float *masse;

uint N;
float G;
float epsilon;

static float3 multFloat3(float m, float3 p){
	float3 rep = {m * p[0], m * p[1], m * p[2]};
	return rep;
}

static float3 additionFloat3(float3 p1, float3 p2){
    float3 rep = {p1[0] + p2[0], p1[1] + p2[1], p1[2] + p2[2]};
    return rep;
}

static float3 soustraction(float *p1, float *p2){
    float3 rep = {*p2 - *p1, *(p2+1) - *(p1+1), *(p2+2) - *(p1+2)};
    return rep;
}

static float squaredNorm(float diffX, float diffY, float diffZ){
	return sqrt(pow(diffX, 2) + pow(diffY, 2) + pow(diffZ, 2));
}

void root(const int32_t *v_in, int32_t *v_out, const void *usrData, uint32_t x, uint32_t y) {

	int32_t i = *v_in;
	
	float denominateur;
	float3 numerateur;
	float3 acc = {0,0,0};
	
	for(int w = 0 ; w < N ; w++){
		int j = w * 3;

		float squared = squaredNorm(pIn[j] - pIn[i], pIn[j+1] - pIn[i+1], pIn[j+2] - pIn[i+2]);
		denominateur = pow(squared  + pow(epsilon, 2), 3/2);

		float3 diff = soustraction(pIn + i, pIn + j);
		numerateur = multFloat3(masse[w], diff);

		acc = additionFloat3(acc, multFloat3(1/denominateur, numerateur));
	}
	
	float3 acceleration = multFloat3(G, acc);

	vitesse[i] +=  acceleration[0];
	vitesse[i+1] +=  acceleration[1];
	vitesse[i+2] +=  acceleration[2];
	pOut[i] = vitesse[i] + pIn[i];
	pOut[i+1] = vitesse[i+1] + pIn[i+1];
	pOut[i+2] = vitesse[i+2] + pIn[i+2];
}