package com.tinf.qmobile.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.*;
import android.widget.NumberPicker;
import android.widget.Toast;
import com.tinf.qmobile.Activity.Settings.SettingsActivity;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Fragment.HomeFragment;
import com.tinf.qmobile.Fragment.MateriaisFragment;
import com.tinf.qmobile.Fragment.NotasFragment;
import com.tinf.qmobile.Interfaces.OnUpdate;
import com.tinf.qmobile.Interfaces.OnResponse;
import com.tinf.qmobile.Network.Client;
import com.tinf.qmobile.R;
import com.tinf.qmobile.Utilities.Jobs;
import com.tinf.qmobile.Utilities.User;
import com.tinf.qmobile.Utilities.Utils;
import java.util.Objects;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.tinf.qmobile.Utilities.Utils.UPDATE_REQUEST;

public class MainActivity extends AppCompatActivity implements OnResponse, BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.fab_expand)        public FloatingActionButton fab_expand;
    @BindView(R.id.navigation)        public BottomNavigationView bottomNav;
    @BindView(R.id.tabs)                     TabLayout tabLayout;
    @BindView(R.id.refresh_layout)    public SwipeRefreshLayout refreshLayout;
    //@BindView(R.id.app_bar_layout)    public AppBarLayout appBarLayout;
    private OnUpdate onUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(findViewById(R.id.toolbar));
        testLogin();
    }

    private void testLogin() {
        hideExpandBtn();
        dismissProgressbar();
        if (User.isValid()) {
            ((App) getApplication()).setLogged(true);
                Jobs.scheduleJob(false);
                setTitle(User.getName());
                changeFragment(new HomeFragment());
                hideTabLayout();
                hideExpandBtn();
                bottomNav.setSelectedItemId(R.id.navigation_home);
        } else {
            startActivityForResult(new Intent(getApplicationContext(), LoginActivity.class), 0);
        }
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
                year.setMaxValue(User.getYears().length - 1);
                year.setValue(Client.get().year);
                year.setDisplayedValues(User.getYears());
                year.setWrapSelectorWheel(false);

                new AlertDialog.Builder(MainActivity.this)
                        .setView(view)
                        .setCustomTitle(
                                Utils.customAlertTitle(
                                        Objects.requireNonNull(getApplicationContext()),
                                        R.drawable.ic_date_range_black_24dp,
                                        R.string.dialog_date_change, R.color.colorPrimary))
                        .setPositiveButton(R.string.dialog_confirm, (dialog, which) -> {

                            Client.get().year = year.getValue();
                            onUpdate.onUpdate(UPDATE_REQUEST);

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

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = new HomeFragment();
                    break;

                case R.id.navigation_notas:
                    fragment = new NotasFragment();
                    break;

                case R.id.navigation_materiais:
                    setTitle(User.getYears()[0]);
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
        Jobs.cancellAllJobs();
        ((App) getApplication()).logOut();
        User.clearInfos();
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
            //webView.reload(bottomNav.getSelectedItemId());
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
        //webView.setOnPageLoadListener(this);
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
        //webView.setOnPageLoadListener(this);
        //refreshLayout.setOnRefreshListener(() -> webView.reload(bottomNav.getSelectedItemId()));
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

    @Override
    public void onStart(String url, int year) {
        runOnUiThread(() -> {
            if (!refreshLayout.isRefreshing()) {
                showProgressbar();
            } else {
                dismissProgressbar();
            }
        });
    }

    @Override
    public void onFinish(int pg, int year) {
        runOnUiThread(() -> {
            refreshLayout.setRefreshing(false);
            dismissProgressbar();
        });
    }

    @Override
    public void onError(int pg, String error) {
        runOnUiThread(() -> {
            dismissProgressbar();
            refreshLayout.setRefreshing(false);
            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onAccessDenied(int pg, String message) {
        dismissProgressbar();
        refreshLayout.setRefreshing(false);

        new android.app.AlertDialog.Builder(MainActivity.this)
                .setCustomTitle(Utils.customAlertTitle(this, R.drawable.ic_error_black_24dp, R.string.dialog_access_denied, R.color.error))
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.action_logout), (dialogInterface, i) -> {
                    logOut();
                })
                .create()
                .show();
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
