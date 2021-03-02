package com.tinf.qmobile.activity;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.review.testing.FakeReviewManager;
import com.google.android.play.core.tasks.Task;
import com.tinf.qmobile.App;
import com.tinf.qmobile.BuildConfig;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.settings.SettingsActivity;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.database.OnDataChange;
import com.tinf.qmobile.fragment.HomeFragment;
import com.tinf.qmobile.fragment.OnUpdate;
import com.tinf.qmobile.fragment.dialog.PopUpFragment;
import com.tinf.qmobile.fragment.ReportFragment;
import com.tinf.qmobile.fragment.JournalFragment;
import com.tinf.qmobile.fragment.MaterialsFragment;
import com.tinf.qmobile.fragment.dialog.UserFragment;
import com.tinf.qmobile.model.matter.Clazz;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.network.handler.PopUpHandler;
import com.tinf.qmobile.network.OnEvent;
import com.tinf.qmobile.network.OnResponse;
import com.tinf.qmobile.parser.ClassParser;
import com.tinf.qmobile.service.Jobs;
import com.tinf.qmobile.utility.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import butterknife.BindView;
import butterknife.ButterKnife;
import static com.tinf.qmobile.App.getContext;
import static com.tinf.qmobile.fragment.SettingsFragment.POPUP;
import static com.tinf.qmobile.network.Client.pos;

public class MainActivity extends AppCompatActivity implements OnResponse, OnEvent, OnDataChange, OnUpdate,
        BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.main_refresh)        public SwipeRefreshLayout refreshLayout;
    @BindView(R.id.navigation)          BottomNavigationView bottomNav;
    @BindView(R.id.drawer)              DrawerLayout drawerLayout;
    @BindView(R.id.nav)                 NavigationView navigationView;
    @BindView(R.id.toolbar_main)        Toolbar toolbar;
    @BindView(R.id.main_date)           TextView date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setTitle(User.getName());

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        Menu menu = navigationView.getMenu();

        for (int i = 0; i < User.getYears().length; i++) {
            menu.add(R.id.group1, i, Menu.NONE, User.getYears()[i]);
            menu.getItem(i).setIcon(getDrawable(R.drawable.ic_label));
            menu.getItem(i).setCheckable(true);
        }

        menu.getItem(0).setChecked(true);

        if (User.getYears().length > 0)
            date.setText(User.getYears()[pos]);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            int pg = bundle.getInt("FRAGMENT");

            if (pg != 0) {
                changeFragment(new MaterialsFragment());
                bottomNav.setSelectedItemId(R.id.navigation_materiais);
            }

        } else {
            changeFragment(new HomeFragment());
            /*switch (bottomNav.getSelectedItemId()) {

                case R.id.navigation_home:
                    changeFragment(new HomeFragment());

                case R.id.navigation_notas:
                    changeFragment(new JournalFragment());

                case R.id.navigation_materiais:
                    changeFragment(new MaterialsFragment());
            }*/
        }

        if (Client.get().isLogging() && !BuildConfig.DEBUG) {
            Client.get().load(PG_FETCH_YEARS);
        }

        ReviewManager manager = ReviewManagerFactory.create(this);
        manager.requestReviewFlow().addOnCompleteListener(info -> {
            if (info.isSuccessful())
                 manager.launchReviewFlow(this, info.getResult());
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        /*SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);*/

        Drawable picture = User.getProfilePicture(getContext());

        if (picture != null) {
            MenuItem item = menu.findItem(R.id.action_account);
            item.setActionView(R.layout.action_account);
            ImageView view = (ImageView) item.getActionView();
            view.setImageDrawable(picture.getCurrent());
            view.setOnClickListener(v -> new UserFragment(this::logOut).show(getSupportFragmentManager(), "sheet_user"));
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:

                onBackPressed();
                return true;

            case R.id.action_account:

                new UserFragment(this::logOut).show(getSupportFragmentManager(), "sheet_user");
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getGroupId() == R.id.group1) {
            navigationView.getMenu().getItem(0).setChecked(true);
            Client.get().changeData(item.getItemId());
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        switch (item.getItemId()) {
            case R.id.drawer_mail:
                startActivity(new Intent(getApplicationContext(), MessagesActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;

            case R.id.drawer_calendar:
                startActivity(new Intent(getApplicationContext(), CalendarActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;

            case R.id.drawer_website:
                startActivity(new Intent(getApplicationContext(), WebViewActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;

            case R.id.drawer_settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
        }

        if (item.getItemId() != bottomNav.getSelectedItemId()) {
            switch (item.getItemId()) {

                case R.id.navigation_home:
                    changeFragment(new HomeFragment());
                    return true;

                case R.id.navigation_notas:
                    changeFragment(new JournalFragment());
                    return true;

                case R.id.navigation_materiais:
                    changeFragment(new MaterialsFragment());
                    return true;
            }
        } else {
            Client.get().requestScroll();
        }

        return false;
    }

    protected void showProgressbar() {
        refreshLayout.setRefreshing(true);
    }

    public void dismissProgressbar() {
        refreshLayout.setRefreshing(false);
    }

    private void logOut() {
        getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.main_fragment)).commit();
        finish();
        Client.get().close();
        Jobs.cancelAllJobs();
        DataBase.get().close();
        User.clearInfos();
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().clear().apply();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        startActivity(new Intent(this, LoginActivity.class));
    }

    private boolean changeFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
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
                case R.id.navigation_materiais: Client.get().load(PG_MATERIALS);
                    break;
            }
        } else {
            dismissProgressbar();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Client.get().addOnResponseListener(this);
        Client.get().addOnUpdateListener(this);
        DataBase.get().addOnDataChangeListener(this);
        refreshLayout.setOnRefreshListener(this::reload);
        bottomNav.setOnNavigationItemSelectedListener(this);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Client.get().removeOnResponseListener(this);
        Client.get().removeOnUpdateListener(this);
        DataBase.get().removeOnDataChangeListener(this);
        dismissProgressbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Client.get().addOnResponseListener(this);
        Client.get().addOnUpdateListener(this);
        DataBase.get().addOnDataChangeListener(this);
        bottomNav.setOnNavigationItemSelectedListener(this);
        refreshLayout.setOnRefreshListener(this::reload);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Client.get().removeOnResponseListener(this);
        Client.get().removeOnUpdateListener(this);
        DataBase.get().removeOnDataChangeListener(this);
        dismissProgressbar();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (bottomNav.getSelectedItemId() != R.id.navigation_home && getSupportFragmentManager().getBackStackEntryCount() == 0) {
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

        if (pg == PG_LOGIN) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            if (prefs.getBoolean(POPUP, true)) {

                WebView webView = new WebView(getApplicationContext());
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setLoadsImagesAutomatically(false);
                webView.getSettings().setBlockNetworkImage(true);
                webView.addJavascriptInterface(new PopUpHandler(webView, this), "handler");
                webView.setWebViewClient(new WebViewClient() {

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);

                        Log.d("Webview", url);

                        if (!url.contains("javascript")) {
                            webView.loadUrl("javascript:window.handler.handleLogin"
                                    + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                        }
                    }

                });
                webView.loadUrl(Client.get().getURL() + INDEX + PG_HOME);
            }
        }
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

        } else if (pg == PG_ACCESS_DENIED) {
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
        if (requestCode == 1) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_permission_denied), Toast.LENGTH_LONG).show();
            } else {
                Client.get().load(PG_MATERIALS);
            }
        }
    }

    @Override
    public void onDialog(WebView webView, String title, String msg) {
        PopUpFragment popup = new PopUpFragment();
        popup.setComponents(webView, title, msg);
        popup.show(getSupportFragmentManager(), "sheet_popup");
    }

    @Override
    public void onRenewalAvailable() {
        new MaterialAlertDialogBuilder(MainActivity.this)
                .setTitle(getResources().getString(R.string.dialog_renewal_title))
                .setMessage(getResources().getString(R.string.dialog_renewal_txt))
                .setCancelable(true)
                .setPositiveButton(getResources().getString(R.string.dialog_open_site), (dialogInterface, i) -> {
                    startActivity(new Intent(getApplicationContext(), WebViewActivity.class));
                })
                .setNegativeButton(getResources().getString(R.string.dialog_later), null)
                .create()
                .show();
    }

    @Override
    public void countNotifications(int count1, int count2) {
        if (count1 <= 0)
            bottomNav.removeBadge(R.id.navigation_notas);
        else
            bottomNav.getOrCreateBadge(R.id.navigation_notas).setNumber(Math.min(count1, 99));

        if (count2 <= 0)
            bottomNav.removeBadge(R.id.navigation_materiais);
        else
            bottomNav.getOrCreateBadge(R.id.navigation_materiais).setNumber(Math.min(count2, 99));
    }

    @Override
    public void countMessages(int count) {
        TextView txt = (TextView) navigationView.getMenu().findItem(R.id.drawer_mail).getActionView();

        if (count > 0) {
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setTypeface(null, Typeface.BOLD);
            txt.setText(String.valueOf(Math.min(count, 99)));
            txt.setVisibility(View.VISIBLE);
        } else {
            txt.setVisibility(View.GONE);
        }
    }

    @Override
    public void onScrollRequest() {

    }

    @Override
    public void onDateChanged() {
        date.setText(User.getYears()[pos]);
        setTitle(User.getName());
    }

}
