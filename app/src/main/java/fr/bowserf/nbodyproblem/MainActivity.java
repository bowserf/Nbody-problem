package fr.bowserf.nbodyproblem;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import fr.bowserf.nbodyproblem.opengl.OpenGLView;

public class MainActivity extends AppCompatActivity implements
        RadioGroup.OnCheckedChangeListener,
        View.OnClickListener {

    @SuppressWarnings("unused")
    private static final String TAG = "MainActivity";

    private static final int STEP_BODIES = 25;

    private static final int DEFAULT_START_NUMBER_BODIES = 50;

    private static final int COMPUTATION_JAVA = 0;
    private static final int COMPUTATION_RENDERSCRIPT = 1;
    private static final int COMPUTATION_NDK = 2;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({COMPUTATION_JAVA, COMPUTATION_RENDERSCRIPT, COMPUTATION_NDK})
    public @interface ComputationMode {
    }

    /**
     * UI
     */
    private OpenGLView mGLSurfaceView;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private LinearLayout mLinearLayout;
    private TextView mTvNbBodies;

    /**
     * Method used to compute coordinates particles.
     */
    private ComputationFunction mFunction;

    /**
     * Boolean used to know if computation is processing or not
     */
    private boolean mIsRunning = false;

    /**
     * Integer used to know which method has been chosen.
     */
    private
    @ComputationMode
    int mMethod = COMPUTATION_JAVA;

    /**
     * Current number of bodies.²
     */
    private int mNbBodiesNumber = DEFAULT_START_NUMBER_BODIES;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTvNbBodies = (TextView) findViewById(R.id.tv_nb_corps);
        mGLSurfaceView = (OpenGLView) findViewById(R.id.openglview_ncorps);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mLinearLayout = (LinearLayout) findViewById(R.id.left_drawer);

        final Button btnLessBody= (Button) findViewById(R.id.less_body);
        final Button btnMoreBody = (Button) findViewById(R.id.more_body);
        final RadioGroup radioChooseMethod = (RadioGroup) findViewById(R.id.radio_group_method);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawer,
                R.string.drawer_open,
                R.string.drawer_close
        );
        mDrawer.addDrawerListener(mDrawerToggle);

        mTvNbBodies.setText(String.valueOf(mNbBodiesNumber));
        radioChooseMethod.check(R.id.radio_button_java);

        radioChooseMethod.setOnCheckedChangeListener(this);
        btnLessBody.setOnClickListener(this);
        btnMoreBody.setOnClickListener(this);

        mFunction = mGLSurfaceView.getRenderer().getFunction();
    }

    @Override
    public boolean onOptionsItemSelected(final @NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_start_stop_animation) {
            mIsRunning = !mIsRunning;
            item.setTitle(getResources().getString(mIsRunning ? R.string.stop : R.string.menu_start));
            mGLSurfaceView.getRenderer().setIsRunning(mIsRunning);
        } else if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
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
                mMethod = COMPUTATION_JAVA;
                initializeFunction();
                break;
            case R.id.radio_button_rs:
                mMethod = COMPUTATION_RENDERSCRIPT;
                initializeFunction();
                break;
            case R.id.radio_button_ndk:
                mMethod = COMPUTATION_NDK;
                initializeFunction();
                break;
            default:
                break;
        }
    }

    private void initializeFunction() {
        mFunction.freeMemory();
        switch (mMethod) {
            case COMPUTATION_JAVA:
                mFunction = new CalculationCPU(mNbBodiesNumber);
                break;
            case COMPUTATION_RENDERSCRIPT:
                mFunction = new CalculationGPU(this, mNbBodiesNumber);
                break;
            case COMPUTATION_NDK:
                mFunction = new CalculationNDK(mNbBodiesNumber);
                break;
            default:
                throw new IllegalStateException("Method action is not managed : " + mMethod);
        }
        mGLSurfaceView.getRenderer().initialisation(mFunction);
    }

    @Override
    public boolean onCreateOptionsMenu(final @NonNull Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_computation_state, menu);
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
        switch (v.getId()) {
            case R.id.less_body:
                if (mNbBodiesNumber > STEP_BODIES) {
                    mNbBodiesNumber -= STEP_BODIES;
                }
                break;
            case R.id.more_body:
                mNbBodiesNumber += STEP_BODIES;
                break;
        }


        mTvNbBodies.setText(String.valueOf(mNbBodiesNumber));

        initializeFunction();

        mGLSurfaceView.getRenderer().initialisation(mFunction);
    }

}
