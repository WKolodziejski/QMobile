package com.tinf.qmobile.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.tinf.qmobile.BuildConfig;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.settings.SettingsActivity;
import com.tinf.qmobile.data.DataBase;
import com.tinf.qmobile.data.OnDataChange;
import com.tinf.qmobile.fragment.ReportFragment;
import com.tinf.qmobile.fragment.HomeFragment;
import com.tinf.qmobile.fragment.JournalFragment;
import com.tinf.qmobile.fragment.MaterialsFragment;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.network.OnEvent;
import com.tinf.qmobile.network.OnResponse;
import com.tinf.qmobile.service.Jobs;
import com.tinf.qmobile.utility.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import static com.tinf.qmobile.network.Client.pos;

public class MainActivity extends AppCompatActivity implements OnResponse, OnEvent, OnDataChange, BottomNavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    @BindView(R.id.navigation)        public BottomNavigationView bottomNav;
    @BindView(R.id.refresh_layout)    public SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(findViewById(R.id.toolbar_main));

        if (savedInstanceState != null) {
            changeFragment(getSupportFragmentManager().getFragment(savedInstanceState, "fragment"));
        } else {
            changeFragment(new HomeFragment());
        }

        if (Client.get().isLogging() && !BuildConfig.DEBUG) {
            Client.get().load(PG_FETCH_YEARS);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        /*SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);*/

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:

                onBackPressed();
                return true;

            case R.id.action_settings:

                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;

            case R.id.action_logout:

                new MaterialAlertDialogBuilder(MainActivity.this)
                        .setTitle(getResources().getString(R.string.dialog_quit))
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
                year.setValue(Client.pos);
                year.setDisplayedValues(User.getYears());
                year.setWrapSelectorWheel(false);

                new MaterialAlertDialogBuilder(MainActivity.this)
                        .setView(view)
                        .setTitle(getResources().getString(R.string.dialog_date_change))
                        .setPositiveButton(R.string.dialog_date_confirm, (dialog, which) -> {
                            Client.get().changeData(year.getValue());
                            Client.get().loadYear(year.getValue());
                        }).setNegativeButton(R.string.dialog_cancel, null)
                        .create()
                        .show();
                return true;

            case R.id.action_grades:

                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment);

                if (fragment instanceof JournalFragment) {
                    return changeFragment(new ReportFragment());

                } else if (fragment instanceof ReportFragment) {
                    return changeFragment(new JournalFragment());

                } else return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        if (item.getItemId() != bottomNav.getSelectedItemId()) {
            switch (item.getItemId()) {

                case R.id.navigation_home:
                    fragment = new HomeFragment();
                    break;

                case R.id.navigation_notas:
                    fragment = new JournalFragment();
                    break;

                case R.id.navigation_materiais:
                    fragment = new MaterialsFragment();
                    break;
            }
        } else {
            Client.get().requestScroll();
        }
        return changeFragment(fragment);
    }

    protected void showProgressbar() {
        refreshLayout.setRefreshing(true);
    }

    public void dismissProgressbar() {
        refreshLayout.setRefreshing(false);
    }

    private void logOut() {
        finish();
        Client.get().clearRequests();
        Jobs.cancelAllJobs();
        DataBase.get().closeBoxStore();
        User.clearInfos();
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().clear().apply();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        startActivity(new Intent(this, LoginActivity.class));
    }

    private boolean changeFragment(Fragment fragment) {
        if (fragment != null) {

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);

            if (!(fragment instanceof HomeFragment)) {
                setTitle(User.getYears()[pos]);
            }

            transaction.replace(R.id.main_fragment, fragment).commit();

            return true;
        }
        return false;
    }

    private void reload() {
        if (Client.isConnected()) {
            switch (bottomNav.getSelectedItemId()) {
                case R.id.navigation_home: Client.get().login();
                    break;
                case R.id.navigation_notas: Client.get().loadYear(pos);
                    break;
                case R.id.navigation_materiais: Client.get().load(PG_MATERIAIS);
                    break;
            }
        } else {
            dismissProgressbar();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");
        //appBarLayout.addOnOffsetChangedListener(this);
        Client.get().addOnResponseListener(this);
        Client.get().setOnEventListener(this);
        DataBase.get().addOnDataChangeListener(this);
        refreshLayout.setOnRefreshListener(this::reload);
        bottomNav.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
        Client.get().removeOnResponseListener(this);
        Client.get().setOnEventListener(null);
        DataBase.get().removeOnDataChangeListener(this);
        //appBarLayout.removeOnResponseListener(this);
        dismissProgressbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
        //appBarLayout.addOnOffsetChangedListener(this);
        Client.get().addOnResponseListener(this);
        Client.get().setOnEventListener(this);
        DataBase.get().addOnDataChangeListener(this);
        bottomNav.setOnNavigationItemSelectedListener(this);
        refreshLayout.setOnRefreshListener(this::reload);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");
        Client.get().removeOnResponseListener(this);
        Client.get().setOnEventListener(null);
        DataBase.get().removeOnDataChangeListener(this);
        //appBarLayout.removeOnResponseListener(this);
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

    @Override
    public void onStart(int pg, int pos) {
        if (!refreshLayout.isRefreshing()) {
            showProgressbar();
        } /*else {
            dismissProgressbar();
        }*/
    }

    @Override
    public void onFinish(int pg, int pos) {
        dismissProgressbar();
    }

    @Override
    public void onError(int pg, String error) {
        dismissProgressbar();
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAccessDenied(int pg, String message) {
        dismissProgressbar();

        if (pg == PG_LOGIN) {
            new MaterialAlertDialogBuilder(MainActivity.this)
                    .setTitle(getResources().getString(R.string.dialog_access_denied))
                    .setMessage(getResources().getString(R.string.dialog_access_changed))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.dialog_try_again), (dialogInterface, i) -> Client.get().login())
                    .setNegativeButton(getResources().getString(R.string.action_logout), (dialogInterface, i) -> logOut())
                    .setNeutralButton(getResources().getString(R.string.dialog_continue_offline), null)
                    .create()
                    .show();

        } else if (pg == PG_ACESSO_NEGADO) {
            new MaterialAlertDialogBuilder(MainActivity.this)
                    .setTitle(getResources().getString(R.string.dialog_access_denied))
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.action_logout), (dialogInterface, i) -> logOut())
                    .create()
                    .show();

        } else {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_permission_denied), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onDialog(String title, String msg) {
        new MaterialAlertDialogBuilder(MainActivity.this)
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(true)
                .setPositiveButton("OK", null)
                .create()
                .show();
    }

    @Override
    public void onRenewalAvailable() {
        new MaterialAlertDialogBuilder(MainActivity.this)
                .setTitle(getResources().getString(R.string.dialog_renewal_title))
                .setMessage(getResources().getString(R.string.dialog_renewal_txt))
                .setCancelable(true)
                .setPositiveButton(getResources().getString(R.string.dialog_open_site), (dialogInterface, i) -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(User.getURL() + INDEX + PG_LOGIN));
                    startActivity(browserIntent);
                })
                .setNegativeButton(getResources().getString(R.string.dialog_later), null)
                .create()
                .show();
    }

    @Override
    public void onNotification(int count1, int count2) {
        if (count1 <= 0)
            bottomNav.removeBadge(R.id.navigation_notas);
        else
            bottomNav.getOrCreateBadge(R.id.navigation_notas).setNumber(count1);

        if (count2 <= 0)
            bottomNav.removeBadge(R.id.navigation_materiais);
        else
            bottomNav.getOrCreateBadge(R.id.navigation_materiais).setNumber(count2);
    }

}
