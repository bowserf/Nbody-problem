package fr.bowserf.nbodyproblem;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import fr.bowserf.nbodyproblem.listener.ButtonListener;
import fr.bowserf.nbodyproblem.listener.ChangeMethodListener;
import fr.bowserf.nbodyproblem.listener.NbCorpsSeekBarListener;
import fr.bowserf.nbodyproblem.opengl.OpenGLView;

public class MainActivity extends Activity {

  @SuppressWarnings("unused")
  private static final String TAG = "MAIN_ACTIVITY";

  private OpenGLView mGLSurfaceView;

  private DrawerLayout mDrawer = null;
  private ActionBarDrawerToggle mDrawerToggle = null;
  private Button mBtStartStop;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_activity_layout);

    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    getActionBar().setDisplayHomeAsUpEnabled(true);
    getActionBar().setHomeButtonEnabled(true);

    mGLSurfaceView = (OpenGLView)findViewById(R.id.openglview_ncorps);
    assert (mGLSurfaceView != null);
    mBtStartStop = (Button)findViewById(R.id.bt_stop_start);
    assert (mBtStartStop != null);
    mDrawer = (DrawerLayout)findViewById(R.id.drawer_layout);
    assert (mDrawer != null);
    final TextView tv = (TextView)findViewById(R.id.tv_nb_corps);
    assert (tv != null);
    final SeekBar nbCorps = (SeekBar)findViewById(R.id.bar_nb_corps);
    assert (nbCorps != null);
    final RadioGroup chooseMethod = (RadioGroup)findViewById(R.id.radio_group_method);
    assert (chooseMethod != null);


    mDrawerToggle = new ActionBarDrawerToggle(
      this,
      mDrawer,
      R.drawable.ic_drawer,
      R.string.drawer_open,
      R.string.drawer_close
    ) {

      public void onDrawerClosed(final @NonNull View view) {
      }

      public void onDrawerOpened(final @NonNull View drawerView) {
      }
    };

    mDrawer.setDrawerListener(mDrawerToggle);

    tv.setText(getResources().getString(R.string.nb_bodies, 50));

    nbCorps.setOnSeekBarChangeListener(new NbCorpsSeekBarListener(tv, mGLSurfaceView, this));

    chooseMethod.check(R.id.radio_button_cpu);
    chooseMethod.setOnCheckedChangeListener(new ChangeMethodListener());

    mBtStartStop.setOnClickListener(new ButtonListener(mGLSurfaceView.getThread()));
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
    mBtStartStop.setText(R.string.start);
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
  public boolean onOptionsItemSelected(final @NonNull MenuItem item) {
    if (mDrawerToggle.onOptionsItemSelected(item))
      return true;
    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onKeyDown(final int keyCode, final @NonNull KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_MENU) {
      if (mDrawer.isDrawerOpen(Gravity.LEFT)) {
        mDrawer.closeDrawer(Gravity.LEFT);
      }
      else {
        mDrawer.openDrawer(Gravity.LEFT);
      }
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }
}
