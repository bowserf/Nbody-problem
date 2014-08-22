package fr.bowserf.nbodyproblem.listener;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import fr.bowserf.nbodyproblem.R;
import fr.bowserf.nbodyproblem.RunComputation;


/**
 * Listener of the button to start or stop the thread
 * @author Frederic Torcheux
 * @version 0.1
 */
public class ButtonListener implements OnClickListener {
	
	@SuppressWarnings("unused")
	private static final String TAG = "ButtonListener";

	private RunComputation mThread;
	
	public ButtonListener(final @NonNull RunComputation t){
		this.mThread = t;
	}

	@Override
	public void onClick(final @NonNull View b) {
		if(b.getId() == R.id.bt_stop_start){
			Button bt = (Button)b;
			if(mThread.isRunning()){
				mThread.stopThread();
				bt.setText("Start");
			}else{
				mThread.startThread();
				bt.setText("Stop");
			}
		}
	}
}
