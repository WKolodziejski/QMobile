package com.tinf.qmobile.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.settings.SettingsActivity;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.database.OnDataChange;
import com.tinf.qmobile.databinding.ActivityMainBinding;
import com.tinf.qmobile.fragment.HomeFragment;
import com.tinf.qmobile.fragment.JournalFragment;
import com.tinf.qmobile.fragment.MaterialsFragment;
import com.tinf.qmobile.fragment.OnUpdate;
import com.tinf.qmobile.fragment.ReportFragment;
import com.tinf.qmobile.fragment.dialog.PopUpFragment;
import com.tinf.qmobile.fragment.dialog.UserFragment;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.network.OnEvent;
import com.tinf.qmobile.network.OnResponse;
import com.tinf.qmobile.network.handler.PopUpHandler;
import com.tinf.qmobile.service.Jobs;
import com.tinf.qmobile.utility.User;

import static com.tinf.qmobile.App.getContext;
import static com.tinf.qmobile.fragment.SettingsFragment.POPUP;
import static com.tinf.qmobile.network.Client.pos;

public class MainActivity extends AppCompatActivity implements OnResponse, OnEvent, OnDataChange, OnUpdate,
        BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {
    public ActivityMainBinding binding;

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> Client.get().restorePreviousDate());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawer, binding.toolbar, R.string.open_drawer, R.string.close_drawer);
        binding.drawer.addDrawerListener(toggle);
        toggle.syncState();

        binding.toolbar.setOnClickListener(view -> {
            launcher.launch(new Intent(getContext(), SearchActivity.class));
        });

        Menu menu = binding.nav.getMenu();

        for (int i = 0; i < User.getYears().length; i++) {
            menu.add(R.id.group1, i, Menu.NONE, User.getYears()[i]);
            menu.getItem(i).setIcon(AppCompatResources.getDrawable(getBaseContext(), R.drawable.ic_label));
            menu.getItem(i).setCheckable(true);
        }

        menu.getItem(0).setChecked(true);

        if (User.getYears().length > 0)
            binding.date.setText(User.getYears()[pos]);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            int pg = bundle.getInt("FRAGMENT");

            if (pg != 0) {
                changeFragment(new MaterialsFragment());
                binding.navigation.setSelectedItemId(R.id.navigation_materiais);
            }

        } else {
            changeFragment(new HomeFragment());
            /*switch (bottomNav.getSelectedItemId()) {

                case R.id.binding.navigation_home:
                    changeFragment(new HomeFragment());

                case R.id.binding.navigation_notas:
                    changeFragment(new JournalFragment());

                case R.id.binding.navigation_materiais:
                    changeFragment(new MaterialsFragment());
            }*/
        }

        if (Client.get().isLogging()) {
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
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;

        } else if (itemId == R.id.action_account) {
            new UserFragment(this::logOut).show(getSupportFragmentManager(), "sheet_user");
            return true;

        } else if (itemId == R.id.action_grades) {
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
        int groupId = item.getGroupId();
        int itemId = item.getItemId();

        if (groupId == R.id.group1) {
            binding.nav.getMenu().getItem(itemId).setChecked(true);
            Client.get().changeDate(itemId);
            binding.drawer.closeDrawer(GravityCompat.START);
            return true;
        }

        if (itemId == R.id.drawer_mail) {
            startActivity(new Intent(getBaseContext(), MessagesActivity.class));
            binding.drawer.closeDrawer(GravityCompat.START);
            return true;

        } else if (itemId == R.id.drawer_calendar) {
            startActivity(new Intent(getBaseContext(), CalendarActivity.class));
            binding.drawer.closeDrawer(GravityCompat.START);
            return true;

        } else if (itemId == R.id.drawer_website) {
            startActivity(new Intent(getBaseContext(), WebViewActivity.class));
            binding.drawer.closeDrawer(GravityCompat.START);
            return true;

        } else if (itemId == R.id.drawer_settings) {
            startActivity(new Intent(getBaseContext(), SettingsActivity.class));
            binding.drawer.closeDrawer(GravityCompat.START);
            return true;
        }

        if (itemId != binding.navigation.getSelectedItemId()) {

            if (itemId == R.id.navigation_home) {
                return changeFragment(new HomeFragment());

            } else if (itemId == R.id.navigation_notas) {
                return changeFragment(new JournalFragment());

            } else if (itemId == R.id.navigation_materiais) {
                return changeFragment(new MaterialsFragment());
            }
        } else {
            Client.get().requestScroll();
        }

        return false;
    }

    private void dismissProgressbar() {
        binding.refresh.setRefreshing(false);
    }

    private void logOut() {
        getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.main_fragment)).commit();
        finish();
        Client.get().close();
        Jobs.cancelAllJobs();
        DataBase.get().close();
        User.clearInfos();
        PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().clear().apply();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        startActivity(new Intent(this, LoginActivity.class));
    }

    private boolean changeFragment(Fragment fragment, Bundle args) {
        if (fragment != null)
            fragment.setArguments(args);

        return changeFragment(fragment);
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
            int selectedItemId = binding.navigation.getSelectedItemId();

            if (selectedItemId == R.id.navigation_home) {
                Client.get().login();

            } else if (selectedItemId == R.id.navigation_notas) {
                Client.get().loadYear(pos);

            } else if (selectedItemId == R.id.navigation_materiais) {
                Client.get().load(PG_MATERIALS);
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
        binding.refresh.setOnRefreshListener(this::reload);
        binding.navigation.setOnNavigationItemSelectedListener(this);
        binding.nav.setNavigationItemSelectedListener(this);
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
        binding.navigation.setOnNavigationItemSelectedListener(this);
        binding.refresh.setOnRefreshListener(this::reload);
        binding.nav.setNavigationItemSelectedListener(this);
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
        if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
            binding.drawer.closeDrawer(GravityCompat.START);

        } else if (binding.navigation.getSelectedItemId() != R.id.navigation_home && getSupportFragmentManager().getBackStackEntryCount() == 0) {
            binding.navigation.setSelectedItemId(R.id.navigation_home);

        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onStart(int pg) {
        if (!binding.refresh.isRefreshing())
            binding.refresh.setRefreshing(true);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onFinish(int pg) {
        dismissProgressbar();

        if (pg == PG_LOGIN) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

            if (prefs.getBoolean(POPUP, true)) {

                WebView webView = new WebView(getBaseContext());
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
        } else if (pg == PG_FETCH_YEARS || pg == PG_JOURNALS) {
            Menu menu = binding.nav.getMenu();
            menu.removeGroup(R.id.group1);

            for (int i = 0; i < User.getYears().length; i++) {
                menu.add(R.id.group1, i, Menu.NONE, User.getYears()[i]);
                menu.getItem(i).setIcon(AppCompatResources.getDrawable(getBaseContext(), R.drawable.ic_label));
                menu.getItem(i).setCheckable(true);
            }

            menu.getItem(pos).setChecked(true);
        }
    }

    @Override
    public void onError(int pg, String error) {
        dismissProgressbar();
        Toast.makeText(getBaseContext(), error, Toast.LENGTH_LONG).show();
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
            Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.text_permission_denied), Toast.LENGTH_LONG).show();
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
                    startActivity(new Intent(getBaseContext(), WebViewActivity.class));
                })
                .setNegativeButton(getResources().getString(R.string.dialog_later), null)
                .create()
                .show();
    }

    @Override
    public void countNotifications(int count1, int count2) {
        if (count1 <= 0)
            binding.navigation.removeBadge(R.id.navigation_notas);
        else
            binding.navigation.getOrCreateBadge(R.id.navigation_notas).setNumber(Math.min(count1, 99));

        if (count2 <= 0)
            binding.navigation.removeBadge(R.id.navigation_materiais);
        else
            binding.navigation.getOrCreateBadge(R.id.navigation_materiais).setNumber(Math.min(count2, 99));
    }

    @Override
    public void countMessages(int count) {
        TextView txt = (TextView) binding.nav.getMenu().findItem(R.id.drawer_mail).getActionView();

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
        binding.date.setText(User.getYears()[pos]);
    }

}
