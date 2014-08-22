#pragma version(1)
#pragma rs java_package_name(fr.bowserf.nbodyproblem)

float *pIn;
float *pOut;
float *vitesse;
float *masse;
rs_allocation gIn;
rs_allocation gOut;

rs_script gScript;
int N;
float epsilon;

static float3 mult(float m, float p1, float p2, float p3){
	float3 rep = {m*p1, m*p2, m*p3}; 
	return rep;
}

static float3 multFloat3(float m, float3 p){
	float3 rep = {m*p[0], m*p[1], m*p[2]}; 
	return rep;
}

static float norme_au_carre(float x_i_1, float x_i_2, float x_i_3, float x_j_1, float x_j_2, float x_j_3){
	return pow(sqrt(pow(x_j_1 - x_i_1,2) + pow(x_j_2 - x_i_2,2) + pow(x_j_3 - x_i_3,2)),2); 
}

void root(const int32_t *v_in, int32_t *v_out, const void *usrData, uint32_t x, uint32_t y) {
	int32_t i = *v_in;
	
	float denominateur;
	float3 numerateur;
	float3 acc = {0,0,0};
	
	for(int w = 0 ; w < N ; w++){
		int j = w * 3;
		denominateur = pow(norme_au_carre(pIn[i], pIn[i+1], pIn[i+2], pIn[j], pIn[j+1], pIn[j+2]) + pow(epsilon,2), 3/2);
		numerateur = mult(masse[w], pIn[i] - pIn[j], pIn[i+1] - pIn[j+1], pIn[i+2] - pIn[j+2]);
		acc = acc + multFloat3(1/denominateur, numerateur);
	}
	
	float3 repMult = multFloat3(9.8,acc);
	vitesse[i] = vitesse[i] + repMult[0];
	vitesse[i+1] = vitesse[i+1] + repMult[1];
	vitesse[i+2] = vitesse[i+2] + repMult[2];
	pOut[i] = vitesse[i] + pIn[i];
	pOut[i+1] = vitesse[i+1] + pIn[i+1];
	pOut[i+2] = vitesse[i+2] + pIn[i+2];	
}


void call(){
   rsDebug("Number of rows:", rsAllocationGetDimX(gIn));
   rsForEach(gScript, gIn, gOut, NULL, 0);
}
