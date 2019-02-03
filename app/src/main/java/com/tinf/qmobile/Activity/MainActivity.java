package com.tinf.qmobile.Activity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.crashlytics.android.BuildConfig;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.core.CrashlyticsCore;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import com.tinf.qmobile.Activity.Settings.AboutActivity;
import com.tinf.qmobile.Activity.Settings.SettingsActivity;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Fragment.HomeFragment;
import com.tinf.qmobile.Fragment.MateriaisFragment;
import com.tinf.qmobile.Fragment.ViewPager.NotasFragment;
import com.tinf.qmobile.Interfaces.Fragments.OnUpdate;
import com.tinf.qmobile.Interfaces.WebView.OnPageLoad;
import com.tinf.qmobile.R;
import com.tinf.qmobile.Service.BackgroundCheck;
import com.tinf.qmobile.Utilities.User;
import com.tinf.qmobile.Utilities.Utils;
import com.tinf.qmobile.WebView.SingletonWebView;
import java.util.Objects;
import butterknife.BindView;
import butterknife.ButterKnife;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import io.fabric.sdk.android.Fabric;
import io.objectbox.BoxStore;

import static com.tinf.qmobile.Utilities.User.REGISTRATION;
import static com.tinf.qmobile.Utilities.Utils.PG_ACESSO_NEGADO;
import static com.tinf.qmobile.Utilities.Utils.PG_DIARIOS;
import static com.tinf.qmobile.Utilities.Utils.URL;
import static com.tinf.qmobile.Utilities.Utils.VERSION;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MINUTES;

public class MainActivity extends AppCompatActivity implements OnPageLoad.Main, BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.fab_expand)        public FloatingActionButton fab_expand;
    @BindView(R.id.navigation)        public BottomNavigationView bottomNav;
    @BindView(R.id.tabs)                     TabLayout tabLayout;
    @BindView(R.id.refresh_layout)    public SwipeRefreshLayout refreshLayout;
    //@BindView(R.id.app_bar_layout)    public AppBarLayout appBarLayout;
    private SingletonWebView webView = SingletonWebView.getInstance();
    private OnUpdate onUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(findViewById(R.id.toolbar));
        configFireBase();
        testLogin();
    }

    private void configFireBase() {
        Fabric.with(new Fabric.Builder(this)
                .kits(new CrashlyticsCore.Builder()
                        .build(), new Answers())
                .debuggable(true)
                .build());
        Crashlytics.setUserIdentifier(User.getCredential(getApplicationContext(), REGISTRATION));
    }

    private void testLogin() {
        if (User.isValid(getApplicationContext())) {
            ((App) getApplication()).setLogged(true);
            SingletonWebView.getInstance().setBoxStore(((App) getApplication()).getBoxStore());
            /*if (!getPreferences(MODE_PRIVATE).getBoolean(VERSION, false)) {
                Utils.cancellAllJobs(getApplicationContext());
                getPreferences(MODE_PRIVATE).edit().putBoolean(VERSION, true).apply();
                getBox().close();
                getBox().deleteAllFiles();
                logOut();
            } else {*/
                Utils.scheduleJob(getApplicationContext(), false);
                setTitle(User.getName(getApplicationContext()));
                changeFragment(new HomeFragment());
                hideTabLayout();
                hideExpandBtn();
                bottomNav.setSelectedItemId(R.id.navigation_home);
                webView.loadNextUrl();
            //}
        } else {
            startActivityForResult(new Intent(getApplicationContext(), LoginActivity.class), 0);
        }
        hideExpandBtn();
        dismissProgressbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);

        /*final MenuItem menuMsgs = menu.findItem(R.id.action_messages);

        FrameLayout layout = (FrameLayout) menu.findItem(R.id.action_messages).getActionView();

        TextView badge = (TextView) layout.findViewById(R.id.messages_badge);

        int count = 3;

        if (badge != null) {
            if (count == 0) {
                if (badge.getVisibility() != View.GONE) {
                    badge.setVisibility(View.GONE);
                }
            } else {
                badge.setText(String.valueOf(Math.min(count, 99)));
                if (badge.getVisibility() != View.VISIBLE) {
                    badge.setVisibility(View.VISIBLE);
                }
            }
        }

        layout.setOnClickListener(v -> onOptionsItemSelected(menuMsgs));*/

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_settings:

                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;

            case R.id.action_logout:

                new AlertDialog.Builder(MainActivity.this)
                        .setCustomTitle(Utils.customAlertTitle(this, R.drawable.ic_exit_to_app_black_24dp, R.string.dialog_quit, R.color.colorPrimary))
                        .setMessage(R.string.dialog_quit_msg)
                        .setPositiveButton(R.string.dialog_quit, (dialog, which) -> logOut())
                        .setNegativeButton(R.string.dialog_cancel, null)
                        .create()
                        .show();
                return true;

            case R.id.action_date:

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

                            webView.changeDate(year.getValue(), bottomNav.getSelectedItemId());

                        }).setNegativeButton(R.string.dialog_cancel, null)
                        .create()
                        .show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;

        if (item.getItemId() != bottomNav.getSelectedItemId()) {
            dismissProgressbar();
            webView.resumeQueue();
            webView.loadNextUrl();

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = new HomeFragment();
                    break;

                case R.id.navigation_notas:
                    fragment = new NotasFragment();
                    break;

                case R.id.navigation_materiais:
                    setTitle(webView.data_year[webView.year_position]);
                    if (webView.year_position > 0) {
                        webView.scriptMateriais = "javascript: document.getElementById(\"ANO_PERIODO\").selectedIndex ="
                                + (webView.year_position + 1) + ";document.forms[0].submit();";
                    }
                    fragment = new MateriaisFragment();
                    hideExpandBtn();
                    hideTabLayout();
                    break;
            }
        } else {
            if (onUpdate != null) {
                onUpdate.requestScroll();
            }
        }
        return changeFragment(fragment);
    }

    @Override
    public void onPageStart() {
        runOnUiThread(() -> {
            if (!refreshLayout.isRefreshing()) {
                showProgressbar();
            } else {
                dismissProgressbar();
            }
        });
    }

    @Override
    public void onPageFinish(String url_p) {
        runOnUiThread(() -> {
            webView.loadNextUrl();
            refreshLayout.setRefreshing(false);
            dismissProgressbar();
            if (onUpdate != null) {
                onUpdate.onUpdate(url_p);
            }
        });
    }

    @Override
    public void onErrorRecived(String url_p, String error) {
        runOnUiThread(() -> {
            dismissProgressbar();
            refreshLayout.setRefreshing(false);

            if (url_p.equals(URL + PG_ACESSO_NEGADO)) {
                new android.app.AlertDialog.Builder(MainActivity.this)
                        .setCustomTitle(Utils.customAlertTitle(this, R.drawable.ic_error_black_24dp, R.string.dialog_access_denied, R.color.error))
                        .setMessage(error)
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.action_logout), (dialogInterface, i) -> {
                            logOut();
                        })
                        .create()
                        .show();
            } else {
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void showSnackBar(String message, boolean action) {
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

    public void showExpandBtn() {
        fab_expand.show();
    }

    public void hideExpandBtn() {
        fab_expand.hide();
    }

    protected void showProgressbar() {
        refreshLayout.setRefreshing(true);
    }

    public void dismissProgressbar() {
        refreshLayout.setRefreshing(false);
    }

    public void hideTabLayout() {
        tabLayout.setVisibility(View.GONE);
    }

    public void setupTabLayoutWithViewPager(ViewPager viewPager) {
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setVisibility(View.VISIBLE);
    }

    private void logOut() {
        new FirebaseJobDispatcher(new GooglePlayDriver(getApplicationContext())).cancelAll();
        ((App) getApplication()).logOut();
        SingletonWebView.logOut();
        User.clearInfos(getApplicationContext());
        finish();
        startActivity(getIntent());
    }

    private boolean changeFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private void reload() {
        if (Utils.isConnected()) {
            webView.reload(bottomNav.getSelectedItemId());
        } else {
            refreshLayout.setRefreshing(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        testLogin();
    }

    @Override
    public void onStart() {
        super.onStart();
        //appBarLayout.addOnOffsetChangedListener(this);
        webView.setOnPageLoadListener(this);
        refreshLayout.setOnRefreshListener(this::reload);
        bottomNav.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        onUpdate = null;
        //appBarLayout.removeOnOffsetChangedListener(this);
        //webView.setOnPageLoadListener(null);
        //refreshLayout.setOnRefreshListener(null);
        //bottomNav.setOnNavigationItemSelectedListener(null);
        dismissProgressbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //appBarLayout.addOnOffsetChangedListener(this);
        webView.setOnPageLoadListener(this);
        refreshLayout.setOnRefreshListener(() -> webView.reload(bottomNav.getSelectedItemId()));
        bottomNav.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        onUpdate = null;
        //appBarLayout.removeOnOffsetChangedListener(this);
        //webView.setOnPageLoadListener(null);
        //refreshLayout.setOnRefreshListener(null);
        //bottomNav.setOnNavigationItemSelectedListener(null);
        dismissProgressbar();
    }


    public BoxStore getBox() {
        return ((App) getApplication()).getBoxStore();
    }

    @Override
    public void onBackPressed() {
        if (bottomNav.getSelectedItemId() != R.id.navigation_home && getSupportFragmentManager().getBackStackEntryCount() == 0) {
            bottomNav.setSelectedItemId(R.id.navigation_home);
        } else {
            super.onBackPressed();
        }
    }

    public void setOnUpdateListener(OnUpdate onUpdate) {
        this.onUpdate = onUpdate;
    }

    /*@Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            appBarLayout.setElevation(i == 0 ?
                    (4 * getResources().getDisplayMetrics().density) :
                    (0 * getResources().getDisplayMetrics().density));
        }
    }*/
}
