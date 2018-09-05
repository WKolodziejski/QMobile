package com.tinf.qacademico.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.tinf.qacademico.Activity.Settings.SettingsActivity;
import com.tinf.qacademico.Class.Materiais.MateriaisList;
import com.tinf.qacademico.Fragment.CalendarioFragment;
import com.tinf.qacademico.Fragment.HomeFragment;
import com.tinf.qacademico.Fragment.MateriaisFragment;
import com.tinf.qacademico.Fragment.ViewPager.NotasFragment;
import com.tinf.qacademico.R;
import com.tinf.qacademico.Utilities.Design;
import com.tinf.qacademico.Utilities.Utils;
import com.tinf.qacademico.WebView.SingletonWebView;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.Box;
import io.objectbox.BoxStore;

import static com.tinf.qacademico.Utilities.Utils.CALENDARIO;
import static com.tinf.qacademico.Utilities.Utils.HOME;
import static com.tinf.qacademico.Utilities.Utils.LOGIN_INFO;
import static com.tinf.qacademico.Utilities.Utils.LOGIN_NAME;
import static com.tinf.qacademico.Utilities.Utils.LOGIN_PASSWORD;
import static com.tinf.qacademico.Utilities.Utils.LOGIN_REGISTRATION;
import static com.tinf.qacademico.Utilities.Utils.LOGIN_VALID;
import static com.tinf.qacademico.Utilities.Utils.MATERIAIS;
import static com.tinf.qacademico.Utilities.Utils.NOTAS;
import static com.tinf.qacademico.Utilities.Utils.PG_CALENDARIO;
import static com.tinf.qacademico.Utilities.Utils.PG_MATERIAIS;
import static com.tinf.qacademico.Utilities.Utils.URL;

public class MainActivity extends AppCompatActivity implements SingletonWebView.OnPageFinished, SingletonWebView.OnPageStarted, SingletonWebView.OnRecivedError {
    @BindView(R.id.progressbar_horizontal)      ProgressBar progressBar;
    @BindView(R.id.fab_expand)           public FloatingActionButton fab_expand;
    @BindView(R.id.navigation)           public BottomNavigationView navigation;
    @BindView(R.id.compactcalendar_view) public CompactCalendarView calendar;
    @BindView(R.id.tabs)                        TabLayout tabLayout;
    private OnPageUpdated onPageUpdated;
    public List<MateriaisList> materiaisList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(findViewById(R.id.toolbar));

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        calendar.setAnimationListener(new CompactCalendarView.CompactCalendarAnimationListener() {
            @Override
            public void onOpened() {
                calendar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onClosed() {
                calendar.setVisibility(View.GONE);
            }
        });

        hideExpandBtn();

        SingletonWebView.getInstance().configWebView(getApplicationContext());
        SingletonWebView.getInstance().setOnPageFinishedListener(this);
        SingletonWebView.getInstance().setOnPageStartedListener(this);
        SingletonWebView.getInstance().setOnErrorRecivedListener(this);

        if (getSharedPreferences(LOGIN_INFO, MODE_PRIVATE).getBoolean(LOGIN_VALID, false)) {
            setTitle(getSharedPreferences(LOGIN_INFO, MODE_PRIVATE).getString(LOGIN_NAME, ""));
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, new HomeFragment(), HOME).commit();
            SingletonWebView.getInstance().loadNextUrl();

        } else {
            startActivityForResult(new Intent(getApplicationContext(), LoginActivity.class), 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            new AlertDialog.Builder(MainActivity.this)
                    .setCustomTitle(Utils.customAlertTitle(this, R.drawable.ic_exit_to_app_black_24dp, R.string.dialog_quit_title, R.color.colorPrimary))
                    .setMessage(R.string.dialog_quit_msg)
                    .setPositiveButton(R.string.dialog_quit_yes, (dialog, which) -> logOut())
                    .setNegativeButton(R.string.dialog_quit_no, null)
                    .show();
            return true;
        } else if (id == R.id.action_date) {
            //Utils.showChangeDateDialog(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            if (item.getItemId() != navigation.getSelectedItemId()) {
                dismissProgressbar();
                SingletonWebView.getInstance().resumeQueue();
                SingletonWebView.getInstance().loadNextUrl();

                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        setTitle(getSharedPreferences(LOGIN_INFO, MODE_PRIVATE).getString(LOGIN_NAME, ""));
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, new HomeFragment(), HOME).commit();
                        hideTabLayout();
                        hideExpandBtn();
                        hideDatePicker();
                        return true;

                    case R.id.navigation_notas:
                        setTitle(SingletonWebView.getInstance().infos.data_diarios[SingletonWebView.getInstance().data_position_diarios]);
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, new NotasFragment(), NOTAS).commit();
                        showExpandBtn();
                        hideDatePicker();
                        return true;

                    case R.id.navigation_calendario:
                        if (!SingletonWebView.getInstance().pg_calendario_loaded) {
                            SingletonWebView.getInstance().loadUrl(URL + PG_CALENDARIO);
                        }
                        showDatePicker();
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, new CalendarioFragment(), CALENDARIO).commit();
                        hideTabLayout();
                        hideExpandBtn();
                        return true;

                    case R.id.navigation_materiais:
                        setTitle(SingletonWebView.getInstance().infos.data_boletim[SingletonWebView.getInstance().data_position_boletim]
                                + " / " + SingletonWebView.getInstance().infos.periodo_boletim[SingletonWebView.getInstance().periodo_position_boletim]);
                        SingletonWebView.getInstance().loadUrl(URL + PG_MATERIAIS);
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, new MateriaisFragment(), MATERIAIS).commit();
                        hideExpandBtn();
                        hideTabLayout();
                        hideDatePicker();
                        return true;
                }
            }
            return false;
        }
    };

    @Override
    public void onPageStart(String url_p) {
        runOnUiThread(() -> {
            Log.i("Singleton", "onStart");
                showLinearProgressbar();
        });
    }

    @Override
    public void onPageFinish(String url_p, List<?> list) {
        runOnUiThread(() -> {
            Log.i("Singleton", "onFinish");

            SingletonWebView.getInstance().loadNextUrl();
            dismissProgressbar();
            invalidateOptionsMenu();

            if (list != null) {
                onPageUpdated.onPageUpdate(list);
            }
        });
    }

    @Override
    public void onErrorRecived(String error) {
        runOnUiThread(this::dismissProgressbar);
    }

    @Override
    public void setTitle(CharSequence text) {
        TextView title = (TextView) findViewById(R.id.actionBar_title);
        title.setText(text);
    }

    private void showDatePicker() {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.actionBar_picker);
        ImageView arrow = (ImageView) findViewById(R.id.actionBar_arrow);
        arrow.setVisibility(View.VISIBLE);
        layout.setOnClickListener(v -> {
            if (calendar.getVisibility() == View.VISIBLE) {
                hideCalendar();
                ViewCompat.animate(arrow).rotation(0).start();
            } else {
                showCalendar();
                ViewCompat.animate(arrow).rotation(180).start();
            }
        });
        layout.setClickable(true);
        layout.setFocusable(true);
    }

    private void hideDatePicker() {
        calendar.setVisibility(View.GONE);

        ImageView arrow = (ImageView) findViewById(R.id.actionBar_arrow);
        arrow.setVisibility(View.GONE);
        arrow.setRotation(0);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.actionBar_picker);
        layout.setClickable(false);
        layout.setFocusable(false);
    }

    protected void showSnackBar(String message, boolean action) { //Mostra a SnackBar
        Snackbar snackBar = Snackbar.make((ViewGroup) findViewById(R.id.main_container), message, Snackbar.LENGTH_INDEFINITE);
        snackBar.setActionTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryLight)));
        if (action) {
            snackBar.setAction(R.string.button_wifi, view1 -> {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                snackBar.dismiss();
            });
        }
        snackBar.show();
    }

    @SuppressLint("RestrictedApi")
    public void showExpandBtn() { //Mostra os FloatingActionButtons
        if (fab_expand.getAnimation() == null) {
            Animation open_linear = AnimationUtils.loadAnimation(this, R.anim.fab_open_pop);
            fab_expand.startAnimation(open_linear);
        }
        if ((fab_expand.getAnimation() != null)) {
            fab_expand.getAnimation().setFillAfter(true);
        }
        fab_expand.setVisibility(View.VISIBLE);
    }

    @SuppressLint("RestrictedApi")
    public void hideExpandBtn() { //Esconde os FloatingActionButtons
        if (fab_expand.getAnimation() != null) {
            Animation close_linear = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_close_pop);
            fab_expand.startAnimation(close_linear);
            fab_expand.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    fab_expand.setAnimation(null);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
        if ((fab_expand.getAnimation() != null)) {
            fab_expand.getAnimation().setFillAfter(false);
        }
        fab_expand.setVisibility(View.INVISIBLE);
    }

    protected void showLinearProgressbar() { //Mostra a progressBar ao carregar a página
        progressBar.setVisibility(View.VISIBLE);
    }

    public void dismissProgressbar() { //Esconde a progressBar ao carregar a página
        progressBar.setVisibility(View.GONE);
    }

    private void showCalendar() {
        calendar.setVisibility(View.VISIBLE);
        if (calendar.getVisibility() != View.VISIBLE) {
            calendar.showCalendarWithAnimation();
        } else {
            calendar.showCalendar();
        }
        invalidateOptionsMenu();
    }

    private void hideCalendar() {
        if (calendar.getVisibility() == View.VISIBLE) {
            calendar.hideCalendarWithAnimation();
        } else {
            calendar.hideCalendar();
        }
        invalidateOptionsMenu();
    }

    public void hideTabLayout() {
        tabLayout.setVisibility(View.GONE);
    }

    public void setupTabLayoutWithViewPager(ViewPager viewPager) {
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setVisibility(View.VISIBLE);
    }

    private void logOut() {
        SharedPreferences.Editor editor = getSharedPreferences(LOGIN_INFO, MODE_PRIVATE).edit();
        editor.putString(LOGIN_REGISTRATION, "");
        editor.putString(LOGIN_PASSWORD, "");
        editor.putString(LOGIN_NAME, "");
        editor.putBoolean(LOGIN_VALID, false);
        editor.apply();
        recreate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        recreate();
    }

    @Override
    public void onBackPressed() {
        if (navigation.getSelectedItemId() != R.id.navigation_home) {
            navigation.setSelectedItemId(R.id.navigation_home);
        } else {
            super.onBackPressed();
        }
    }

    public void setOnPageUpdateListener(OnPageUpdated onPageUpdated){
        this.onPageUpdated = onPageUpdated;
    }

    public interface OnPageUpdated {
        void onPageUpdate(List<?> list);
    }
}
