package fr.bowserf.nbodyproblem.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import fr.bowserf.nbodyproblem.RunComputation;


/**
 * Display an OpenGL Render which is describe in OpenGL20Render
 * @author Frederic
 * @version 0.1
 */
public class OpenGLView extends GLSurfaceView  {

  @SuppressWarnings("unused")
	private static final String TAG = "OpenGLView";

	private OpenGL20Render mRenderer;
	private RunComputation thread;

	public OpenGLView(Context c, AttributeSet attrs) {
		super(c, attrs);
		thread = new RunComputation(c);
		
		// We use OpenGL 2
		setEGLContextClientVersion(2);
		mRenderer = new OpenGL20Render(thread);
		setRenderer(mRenderer);
	}
	
	@Override
	public void onPause()
	{
	    super.onPause();
	    // We stop displaying the view so we stop the thread
	    thread.stopThread();
	}

	//---------
	// Getters
	//---------
	public OpenGL20Render getRenderer() {
		return mRenderer;
	}

	public RunComputation getThread() {
		return thread;
	}
}
