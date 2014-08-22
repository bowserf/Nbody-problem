package fr.bowserf.nbodyproblem.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.support.annotation.NonNull;
import android.util.Log;

import fr.bowserf.nbodyproblem.ConstantesCalcul;
import fr.bowserf.nbodyproblem.RunComputation;


/**
 * This class manage how display the different body on the view
 * @author Frederic Torcheux
 * @version 0.1
 */
public class OpenGL20Render implements GLSurfaceView.Renderer 
{
	private static final String TAG = "OpenGL20Render";

	private float[] mModelMatrix = new float[16];
	private float[] mViewMatrix = new float[16];
	private float[] mProjectionMatrix = new float[16];
	private float[] mMVPMatrix = new float[16];

	private int mMVPMatrixHandle;
	private int mPositionHandle;

	float[] vertices;
	FloatBuffer vertexBuf; 

	private int N;

	private RunComputation mThread;

	public OpenGL20Render(final @NonNull RunComputation t){
		initialisation();
		this.mThread = t;
		mThread.start();	
	}
	
	public void initialisation(){
		this.N = ConstantesCalcul.N;
		vertices = new float[N*3];
	}

	private void calcul(){
		if(ConstantesCalcul.computeMethod == 1){
			for(int i = 0 ; i < N*3 ; i++){
				vertices[i] = mThread.getGpu().getP()[i];
			}
		}else if(ConstantesCalcul.computeMethod == 0){
			for(int i = 0 ; i < N ; i++){
				vertices[i * 3] = mThread.getCalcul().getP()[i].getX();
				vertices[i * 3+1] = mThread.getCalcul().getP()[i].getY();
				vertices[i * 3+2] = mThread.getCalcul().getP()[i].getZ();
			}
		}else{
			/*for(int i = 0 ; i < N*3 ; i++){
				vertices[i] = mThread.getNdk().getP()[i];

			}*/
		}

		vertexBuf = ByteBuffer.allocateDirect(vertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

		vertexBuf.put(vertices).position(0);
	}

	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) 
	{       
		calcul();

		// Set the background clear color to black.
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		float eyeX=0.0f;
		float eyeY=0.0f;
		float eyeZ=0.0f;

		float centerX=0.0f;
		float centerY=0.0f;
		float centerZ=-5.0f;

		float upX=0.0f;
		float upY=1.0f;
		float upZ=0.0f;

		// Set the view matrix. This matrix can be said to represent the camera position.
		// NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
		// view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
		Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);

		//=====================================================================
		// Vertex Shader: function qui est r�alis�e pour chaque sommet
		// Fragment Shader: function qui est r�alis�e pour chaque point dessin�
		//=====================================================================
		/*final String vertexShader =
				"uniform mat4 u_MVPMatrix;      \n"       
						+ "attribute vec4 a_Position;     \n"     
						+ "void main()                    \n"
						+ "{                              \n"
						+ "   gl_Position = u_MVPMatrix   \n"
						+ "               * a_Position;   \n"
						+ "   gl_PointSize = 5.0;       \n"
						+ "}                              \n";*/
		final String vertexShader =
				"uniform mat4 u_MVPMatrix;      \n"       
						+ "attribute vec4 a_Position;     \n"     
						+ "void main()                    \n"
						+ "{                              \n"
						+ "   gl_Position = a_Position;   \n"
						+ "   gl_PointSize = 5.0;       \n"
						+ "}                              \n";

		final String fragmentShader =
				"precision mediump float;       \n"                           
						+ "void main()                    \n"
						+ "{                              \n"
						+ "   gl_FragColor = vec4(1.0,    \n" 
						+ "   0.0, 0.0, 1.0);             \n"
						+ "}                              \n";    


		int vertexShaderHandle = LoadShader(GLES20.GL_VERTEX_SHADER, vertexShader);
		int fragmentShaderHandle = LoadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);



		// Create a program object and store the handle to it.
		int programHandle = GLES20.glCreateProgram();

		if (programHandle != 0) 
		{
			// Bind the vertex shader to the program.
			GLES20.glAttachShader(programHandle, vertexShaderHandle);           
			// Bind the fragment shader to the program.
			GLES20.glAttachShader(programHandle, fragmentShaderHandle);
			// Bind attributes
			GLES20.glBindAttribLocation(programHandle, 0, "a_Position");
			// Link the two shaders together into a program.
			GLES20.glLinkProgram(programHandle);
			// Get the link status.
			final int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
			// If the link failed, delete the program.
			if (linkStatus[0] == 0) 
			{               
				GLES20.glDeleteProgram(programHandle);
				programHandle = 0;
			}
		}

		if (programHandle == 0)
		{
			throw new RuntimeException("Error creating program.");
		}

		// Set program handles. These will later be used to pass in values to the program.
		mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");        
		mPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");

		// Tell OpenGL to use this program when rendering.
		GLES20.glUseProgram(programHandle);        
	}   

	private int LoadShader(int type, String shaderSrc)
	{
		int shader;
		int[] compiled = new int[1];

		// Create the shader object
		shader = GLES20.glCreateShader(type);

		if (shader == 0)
			return 0;

		// Load the shader source
		GLES20.glShaderSource(shader, shaderSrc);

		// Compile the shader
		GLES20.glCompileShader(shader);

		// Check the compile status
		GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);

		if (compiled[0] == 0)
		{
			Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
			GLES20.glDeleteShader(shader);
			return 0;
		}
		return shader;
	}

	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) 
	{
		//====================================================================
		// Viewport : the region of the screen where this volume is projected.
		//====================================================================
		GLES20.glViewport(0, 0, width, height);
		//==========================================================================
		// Create a new perspective projection matrix. The height will stay the same
		// while the width will vary as per aspect ratio.
		//==========================================================================
		final float ratio = (float) width / height;
		final float left = -ratio;
		final float right = ratio;
		final float bottom = -1.0f;
		final float top = 1.0f;
		final float near = 0.1f;
		final float far = 1000.0f;

		Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
	}   

	@Override
	public void onDrawFrame(GL10 glUnused) 
	{
		//===========================================================================================
		// Permet d'effacer le back-buffer (GL_COLOR_BUFFER_BIT) et le Z-Buffer (GL_DEPTH_BUFFER_BIT)
		//===========================================================================================
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);     
		calcul();

		Matrix.setIdentityM(mModelMatrix, 0);
		//Push to the distance - note this will have no effect on a point size
		Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -5.0f);
		Matrix.multiplyMV(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
		Matrix.multiplyMV(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

		GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuf);
		GLES20.glEnableVertexAttribArray(mPositionHandle);

		GLES20.glDrawArrays(GLES20.GL_POINTS, 0, N);

		/*GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 0, vertexBuf);
        GLES20.glEnableVertexAttribArray(0);

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, N);*/
	}   
}