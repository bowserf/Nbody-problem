package fr.bowserf.nbodyproblem.listener;

import android.support.annotation.NonNull;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import fr.bowserf.nbodyproblem.ConstantesCalcul;
import fr.bowserf.nbodyproblem.R;


/**
 * Listener for the radiogroup which change the way of compute new bodies positions
 * @author Frederic Torcheux
 * @version 0.1
 */
public class ChangeMethodListener implements OnCheckedChangeListener{

	@Override
	public void onCheckedChanged(final @NonNull RadioGroup group, final int checkedId) {
		switch(checkedId){
		case R.id.radio_button_cpu:
			ConstantesCalcul.computeMethod = 0;
			break;
		case R.id.radio_button_rs:
			ConstantesCalcul.computeMethod = 1;
			break;
		case R.id.radio_button_ndk:
			ConstantesCalcul.computeMethod = 2;
			break;
		default:
			break;
		}
	}

}
