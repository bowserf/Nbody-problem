package fr.bowserf.nbodyproblem.listener;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import fr.bowserf.nbodyproblem.ConstantesCalcul;
import fr.bowserf.nbodyproblem.R;
import fr.bowserf.nbodyproblem.opengl.OpenGLView;


/**
 * Listener for the seekbar which allows to change the number of bodies
 * @author Frederic Torcheux
 * @version 0.1
 */
public class NbCorpsSeekBarListener implements OnSeekBarChangeListener{
	
	private TextView mtvNbBordies;
	private OpenGLView mGLSurfaceView;
	private Context mContext;
	
	public NbCorpsSeekBarListener(final @NonNull TextView t, final @NonNull OpenGLView mGLSurfaceView, final @NonNull Context c){
		this.mtvNbBordies = t;
		this.mGLSurfaceView = mGLSurfaceView;
		this.mContext = c;
	}

	@Override
	public void onProgressChanged(final @NonNull SeekBar arg0, final int nb, final boolean arg2) {
		
		//Seekbar can have value 0 but we don't want so we add 1 if it's 0;
    int numberOfBodies = nb;
		if(nb == 0) numberOfBodies++;
		
		//We change the text of the TextView to show the new value
		mtvNbBordies.setText(mContext.getResources().getString(R.string.nb_bodies, numberOfBodies));
		ConstantesCalcul.N = numberOfBodies;
		mGLSurfaceView.getThread().initialisation(mContext);
		mGLSurfaceView.getRenderer().initialisation();
	}

	@Override
	public void onStartTrackingTouch(final @NonNull SeekBar arg0) { }

	@Override
	public void onStopTrackingTouch(final @NonNull SeekBar arg0) { }

}
