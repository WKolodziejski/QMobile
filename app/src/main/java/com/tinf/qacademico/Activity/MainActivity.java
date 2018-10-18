package com.tinf.qacademico.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.core.view.MotionEventCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.tinf.qacademico.Activity.Settings.SettingsActivity;
import com.tinf.qacademico.App;
import com.tinf.qacademico.Class.Materiais.MateriaisList;
import com.tinf.qacademico.Fragment.HomeFragment;
import com.tinf.qacademico.Fragment.MateriaisFragment;
import com.tinf.qacademico.Fragment.ViewPager.NotasFragment;
import com.tinf.qacademico.R;
import com.tinf.qacademico.Utilities.Utils;
import com.tinf.qacademico.WebView.SingletonWebView;
import java.util.List;
import java.util.Objects;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.BoxStore;
import static com.tinf.qacademico.Utilities.Utils.LOGIN_INFO;
import static com.tinf.qacademico.Utilities.Utils.LOGIN_NAME;
import static com.tinf.qacademico.Utilities.Utils.LOGIN_PASSWORD;
import static com.tinf.qacademico.Utilities.Utils.LOGIN_REGISTRATION;
import static com.tinf.qacademico.Utilities.Utils.LOGIN_VALID;
import static com.tinf.qacademico.Utilities.Utils.PG_LOGIN;
import static com.tinf.qacademico.Utilities.Utils.PG_MATERIAIS;
import static com.tinf.qacademico.Utilities.Utils.URL;

public class MainActivity extends AppCompatActivity implements SingletonWebView.OnPageFinished, SingletonWebView.OnPageStarted, SingletonWebView.OnRecivedError, BottomNavigationView.OnNavigationItemSelectedListener,  AppBarLayout.OnOffsetChangedListener {
    @BindView(R.id.progressbar_horizontal)      ProgressBar progressBar;
    @BindView(R.id.fab_expand)           public FloatingActionButton fab_expand;
    @BindView(R.id.navigation)           public BottomNavigationView bottomNav;
    @BindView(R.id.tabs)                        TabLayout tabLayout;
    @BindView(R.id.refresh_layout)              SwipeRefreshLayout refreshLayout;
    @BindView(R.id.app_bar_layout)              AppBarLayout appBarLayout;
    private SingletonWebView webView = SingletonWebView.getInstance();
    private OnPageUpdated onPageUpdated;
    public List<MateriaisList> materiaisList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(findViewById(R.id.toolbar));
        bottomNav.setOnNavigationItemSelectedListener(this);

        webView.configWebView(this);

        refreshLayout.setOnRefreshListener(() -> webView.reload(bottomNav.getSelectedItemId()));

        testLogin();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        refreshLayout.setEnabled(i == 0);
    }

    private void testLogin() {
        webView.setOnPageFinishedListener(this);
        webView.setOnPageStartedListener(this);
        webView.setOnErrorRecivedListener(this);

        if (getSharedPreferences(LOGIN_INFO, MODE_PRIVATE).getBoolean(LOGIN_VALID, false)) {
            ((App) getApplication()).setLogged(true);
            SingletonWebView.getInstance().setBoxStore(((App) getApplication()).getBoxStore());
            setTitle(getSharedPreferences(LOGIN_INFO, MODE_PRIVATE).getString(LOGIN_NAME, ""));
            changeFragment(new HomeFragment());
            webView.loadNextUrl();
        } else {
            startActivityForResult(new Intent(getApplicationContext(), LoginActivity.class), 0);
        }

        hideExpandBtn();
        dismissProgressbar();
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

            View view = getLayoutInflater().inflate(R.layout.dialog_date_picker, null);

            final NumberPicker year = (NumberPicker) view.findViewById(R.id.year_picker);

            year.setMinValue(0);
            year.setMaxValue(webView.data_year.length - 1);
            year.setValue(webView.year_position);
            year.setDisplayedValues(webView.data_year);
            year.setWrapSelectorWheel(false);

            new AlertDialog.Builder(MainActivity.this)
                    .setView(view)
                    .setCustomTitle(
                            Utils.customAlertTitle(
                                    Objects.requireNonNull(getApplicationContext()),
                                    R.drawable.ic_date_range_black_24dp,
                                    R.string.dialog_date_change, R.color.colorPrimary))
                    .setPositiveButton(R.string.dialog_confirm, (dialog, which) -> {

                        webView.changeDate(this, year.getValue());

                    }).setNegativeButton(R.string.dialog_cancel, null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() != bottomNav.getSelectedItemId()) {
            dismissProgressbar();
            webView.resumeQueue();
            webView.loadNextUrl();

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    setTitle(getSharedPreferences(LOGIN_INFO, MODE_PRIVATE).getString(LOGIN_NAME, ""));
                    changeFragment(new HomeFragment());
                    hideTabLayout();
                    hideExpandBtn();
                    return true;

                case R.id.navigation_notas:
                    setTitle(webView.data_year[webView.year_position]);
                    changeFragment(new NotasFragment());
                    showExpandBtn();
                    return true;

                case R.id.navigation_materiais:
                    setTitle(webView.data_year[webView.year_position]);
                    webView.loadUrl(URL + PG_MATERIAIS);
                    changeFragment(new MateriaisFragment());
                    hideExpandBtn();
                    hideTabLayout();
                    return true;
            }
        }
        return false;
    }

    @Override
    public void onPageStart(String url_p) {
        runOnUiThread(() -> {
            if (!refreshLayout.isRefreshing()) {
                showProgressbar();
            } else {
                dismissProgressbar();
            }
        });
    }

    @Override
    public void onPageFinish(String url_p, List<?> list) {
        runOnUiThread(() -> {
            webView.loadNextUrl();
            refreshLayout.setRefreshing(false);
            dismissProgressbar();
            if (list != null && list.size() > 0) {
                onPageUpdated.onPageUpdate(list);
            }
        });
    }

    @Override
    public void onErrorRecived(String error) {
        runOnUiThread(() -> {
            dismissProgressbar();
            refreshLayout.setRefreshing(false);
            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
        });
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

    protected void showProgressbar() { //Mostra a progressBar ao carregar a página
        progressBar.setVisibility(View.VISIBLE);
    }

    public void dismissProgressbar() { //Esconde a progressBar ao carregar a página
        progressBar.setVisibility(View.GONE);
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
        SingletonWebView.logOut();
        recreate();
    }

    private void changeFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, fragment)
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out) // funcionou?
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        testLogin();
    }

    @Override
    public void onStart() {
        super.onStart();
        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    protected void onStop() {
        appBarLayout.removeOnOffsetChangedListener(this);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        appBarLayout.removeOnOffsetChangedListener(this);
    }

    public BoxStore getBox() {
        return ((App) getApplication()).getBoxStore();
    }

    @Override
    public void onBackPressed() {
        if (bottomNav.getSelectedItemId() != R.id.navigation_home) {
            bottomNav.setSelectedItemId(R.id.navigation_home);
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
