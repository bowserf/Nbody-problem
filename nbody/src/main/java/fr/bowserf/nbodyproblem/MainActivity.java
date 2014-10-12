package fr.bowserf.nbodyproblem;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import fr.bowserf.nbodyproblem.opengl.OpenGLView;

public class MainActivity extends Activity implements RadioGroup.OnCheckedChangeListener,
        SeekBar.OnSeekBarChangeListener {

    @SuppressWarnings("unused")
    private static final String TAG = "MAIN_ACTIVITY";

    private OpenGLView mGLSurfaceView;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private LinearLayout mLinearLayout;

    private ComputationFunction mFunction;
    private int mBodiesNumber;
    private boolean mIsRunning;
    private ComputationMethod mMethod;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);

        final ActionBar bar = getActionBar();
        if (bar != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);
        }

        mGLSurfaceView = (OpenGLView) findViewById(R.id.openglview_ncorps);
        assert (mGLSurfaceView != null);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert (mDrawer != null);
        mLinearLayout = (LinearLayout) findViewById(R.id.left_drawer);
        assert (mLinearLayout != null);

        final TextView tvNbCorps = (TextView) findViewById(R.id.tv_nb_corps);
        assert (tvNbCorps != null);
        final SeekBar nbCorps = (SeekBar) findViewById(R.id.bar_nb_corps);
        assert (nbCorps != null);
        final RadioGroup chooseMethod = (RadioGroup) findViewById(R.id.radio_group_method);
        assert (chooseMethod != null);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawer,
                R.drawable.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close
        );
        mDrawer.setDrawerListener(mDrawerToggle);

        tvNbCorps.setText(getResources().getString(R.string.nb_bodies, 50));
        chooseMethod.check(R.id.radio_button_java);

        chooseMethod.setOnCheckedChangeListener(this);
        nbCorps.setOnSeekBarChangeListener(this);

        mBodiesNumber = 50;
        mIsRunning = false;
        mMethod = ComputationMethod.JAVA;
        mFunction = mGLSurfaceView.getRenderer().getFunction();
    }

    @Override
    public boolean onOptionsItemSelected(final @NonNull MenuItem item) {
        if (item.getItemId() == R.id.start_and_stop_animation) {
            mIsRunning = !mIsRunning;
            item.setTitle(getResources().getString(mIsRunning ? R.string.stop : R.string.start));
            mGLSurfaceView.getRenderer().setIsRunning(mIsRunning);
        }
        else if (mDrawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(final int keyCode, final @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (mDrawer.isDrawerOpen(mLinearLayout)) {
                mDrawer.closeDrawer(mLinearLayout);
            } else {
                mDrawer.openDrawer(mLinearLayout);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onCheckedChanged(final @NonNull RadioGroup group, final int checkedId) {
        mIsRunning = false;
        mGLSurfaceView.getRenderer().setIsRunning(false);
        invalidateOptionsMenu();

        switch (checkedId) {
            case R.id.radio_button_java:
                mMethod = ComputationMethod.JAVA;
                initializeFunction();
                break;
            case R.id.radio_button_rs:
                mMethod = ComputationMethod.RENDERSCRIPT;
                initializeFunction();
                break;
            case R.id.radio_button_ndk:
                mMethod = ComputationMethod.NDK;
                initializeFunction();
                break;
            default:
                break;
        }
    }

    @Override
    public void onProgressChanged(final @NonNull SeekBar arg0, final int nb,
                                  final boolean arg2) {
        mBodiesNumber = nb;
        if (nb == 0)
            mBodiesNumber++;

        final TextView tvNbBodies = (TextView) findViewById(R.id.tv_nb_corps);
        tvNbBodies.setText(getResources().getString(R.string.nb_bodies, mBodiesNumber));
        initializeFunction();
        mGLSurfaceView.getRenderer().initialisation(mFunction, mBodiesNumber);
    }

    private void initializeFunction(){
        switch (mMethod) {
            case JAVA:
                mFunction = new CalculationCPU(mBodiesNumber);
                mGLSurfaceView.getRenderer().initialisation(mFunction, mBodiesNumber);
                break;
            case RENDERSCRIPT:
                mFunction = new CalculationGPU(this, mBodiesNumber);
                mGLSurfaceView.getRenderer().initialisation(mFunction, mBodiesNumber);
                break;
            case NDK:
                mFunction = new CalculationNDK(mBodiesNumber);
                mGLSurfaceView.getRenderer().initialisation(mFunction, mBodiesNumber);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final @NonNull Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.start_computation, menu);
        return true;
    }

    @Override
    public void onStartTrackingTouch(final @NonNull SeekBar arg0) {
    }

    @Override
    public void onStopTrackingTouch(final @NonNull SeekBar arg0) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(final @NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}
