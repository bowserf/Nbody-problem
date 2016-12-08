#pragma version(1)
#pragma rs java_package_name(fr.bowserf.nbodyproblem)
#pragma rs_fp_relaxed

float *positions;
float *newPositions;
float *speed;
float *mass;

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

	float3 acc = {0,0,0};

	float squaredEpsilon = pow(epsilon, 2);

	for(int w = 0 ; w < N ; w++){
		int j = w * 3;

		float squared = squaredNorm(positions[j] - positions[i], positions[j+1] - positions[i+1], positions[j+2] - positions[i+2]);
		float denominateur = pow(squared  + squaredEpsilon, 3/2);

		float3 diff = soustraction(positions + i, positions + j);
		float3 numerateur = multFloat3(mass[w], diff);

		acc = additionFloat3(acc, multFloat3(1/denominateur, numerateur));
	}

	float3 acceleration = multFloat3(G, acc);

	speed[i] +=  acceleration[0];
	speed[i+1] +=  acceleration[1];
	speed[i+2] +=  acceleration[2];
	newPositions[i] = speed[i] + positions[i];
	newPositions[i+1] = speed[i+1] + positions[i+1];
	newPositions[i+2] = speed[i+2] + positions[i+2];
}