package fr.bowserf.nbodyproblem;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import fr.bowserf.nbodyproblem.opengl.OpenGLView;

public class MainActivity extends Activity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener{

    @SuppressWarnings("unused")
    private static final String TAG = "MAIN_ACTIVITY";

    private static final int STEP_BODIES = 25;

    private OpenGLView mGLSurfaceView;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private LinearLayout mLinearLayout;

    private ComputationFunction mFunction;
    private boolean mIsRunning = false;
    private ComputationMethod mMethod = ComputationMethod.JAVA;

    private int mNbBodiesNumber = 50;

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
        final Button lessBody = (Button)findViewById(R.id.less_body);
        assert(lessBody != null);
        final Button moreBody = (Button)findViewById(R.id.more_body);
        assert(moreBody != null);
        final RadioGroup chooseMethod = (RadioGroup) findViewById(R.id.radio_group_method);
        assert (chooseMethod != null);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawer,
                R.string.drawer_open,
                R.string.drawer_close
        );
        mDrawer.setDrawerListener(mDrawerToggle);

        tvNbCorps.setText(String.valueOf(mNbBodiesNumber));
        chooseMethod.check(R.id.radio_button_java);

        chooseMethod.setOnCheckedChangeListener(this);
        lessBody.setOnClickListener(this);
        moreBody.setOnClickListener(this);

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

    private void initializeFunction(){
        mFunction.freeMemory();
        switch (mMethod) {
            case JAVA:
                mFunction = new CalculationCPU(mNbBodiesNumber);
                mGLSurfaceView.getRenderer().initialisation(mFunction, mNbBodiesNumber);
                break;
            case RENDERSCRIPT:
                mFunction = new CalculationGPU(this, mNbBodiesNumber);
                mGLSurfaceView.getRenderer().initialisation(mFunction, mNbBodiesNumber);
                break;
            case NDK:
                mFunction = new CalculationNDK(mNbBodiesNumber);
                mGLSurfaceView.getRenderer().initialisation(mFunction, mNbBodiesNumber);
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

    @Override
    public void onClick(final @NonNull View v) {
        switch(v.getId()){
            case R.id.less_body:
                if(mNbBodiesNumber > STEP_BODIES)
                    mNbBodiesNumber -= STEP_BODIES;
                break;
            case R.id.more_body:
                mNbBodiesNumber += STEP_BODIES;
                break;
        }

        final TextView tvNbBodies = (TextView) findViewById(R.id.tv_nb_corps);
        tvNbBodies.setText(String.valueOf(mNbBodiesNumber));
        initializeFunction();
        mGLSurfaceView.getRenderer().initialisation(mFunction, mNbBodiesNumber);
    }

    public enum ComputationMethod {
        JAVA, RENDERSCRIPT, NDK
    }
}
