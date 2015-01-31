package fr.bowserf.nbodyproblem.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

public class OpenGLView extends GLSurfaceView {

    @SuppressWarnings("unused")
    private static final String TAG = "OPENGL_VIEW";

    private OpenGL20Render mRenderer;

    public OpenGLView(final @NonNull Context context, final @NonNull AttributeSet attrs) {
        super(context, attrs);

        setEGLContextClientVersion(2);
        mRenderer = new OpenGL20Render();
        setRenderer(mRenderer);
    }


    public OpenGL20Render getRenderer() {
        return mRenderer;
    }
}
