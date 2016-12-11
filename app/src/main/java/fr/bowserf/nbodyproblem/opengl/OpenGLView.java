package fr.bowserf.nbodyproblem.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import fr.bowserf.nbodyproblem.ComputationFunction;

public class OpenGLView extends GLSurfaceView {

    @SuppressWarnings("unused")
    private static final String TAG = "OpenGLView";

    private OpenGL20Render mRenderer;

    public OpenGLView(@NonNull final Context context, @NonNull final AttributeSet attrs) {
        super(context, attrs);

        setEGLContextClientVersion(2);

        mRenderer = new OpenGL20Render();
        setRenderer(mRenderer);
    }

    public void setIsRunning(final boolean isRunning) {
        mRenderer.setIsRunning(isRunning);
    }

    public void initialisation(@NonNull final ComputationFunction function) {
        mRenderer.initialisation(function);
    }
}
