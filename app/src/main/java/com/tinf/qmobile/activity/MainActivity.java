package com.tinf.qmobile.activity;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.settings.SettingsActivity;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.database.OnCount;
import com.tinf.qmobile.databinding.ActivityMainBinding;
import com.tinf.qmobile.fragment.HomeFragment;
import com.tinf.qmobile.fragment.JournalsFragment;
import com.tinf.qmobile.fragment.MaterialsFragment;
import com.tinf.qmobile.fragment.OnUpdate;
import com.tinf.qmobile.fragment.ReportFragment;
import com.tinf.qmobile.fragment.dialog.CreateFragment;
import com.tinf.qmobile.fragment.dialog.PopUpFragment;
import com.tinf.qmobile.fragment.dialog.UserFragment;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.network.OnEvent;
import com.tinf.qmobile.network.OnResponse;
import com.tinf.qmobile.network.handler.PopUpHandler;
import com.tinf.qmobile.service.Works;
import com.tinf.qmobile.utility.User;

import static com.tinf.qmobile.App.USE_COUNT;
import static com.tinf.qmobile.App.USE_INFO;
import static com.tinf.qmobile.App.USE_RATED;
import static com.tinf.qmobile.App.getContext;
import static com.tinf.qmobile.fragment.SettingsFragment.POPUP;
import static com.tinf.qmobile.model.ViewType.JOURNAL;
import static com.tinf.qmobile.model.ViewType.MATERIAL;
import static com.tinf.qmobile.model.ViewType.SCHEDULE;
import static com.tinf.qmobile.network.Client.pos;

public class MainActivity extends AppCompatActivity implements OnResponse, OnEvent, OnCount,
        OnUpdate, NavigationView.OnNavigationItemSelectedListener {
    private ActivityMainBinding binding;

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result ->
                    Client.get().restorePreviousDate());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawer,
                binding.toolbar, R.string.open_drawer, R.string.close_drawer);
        binding.drawer.addDrawerListener(toggle);
        toggle.syncState();

        binding.toolbar.setOnClickListener(view ->
                launcher.launch(new Intent(getContext(), SearchActivity.class)));

        MaterialShapeDrawable toolbarBackground = (MaterialShapeDrawable) binding.toolbar.getBackground();
        toolbarBackground.setShapeAppearanceModel(
                toolbarBackground.getShapeAppearanceModel()
                        .toBuilder()
                        .setAllCorners(CornerFamily.ROUNDED, getResources().getDimension(R.dimen.nav_item_background_inset_right))
                        .build()
        );

        binding.navigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId != binding.navigation.getSelectedItemId())
                return changeFragment(itemId);
            else
                binding.scroll.smoothScrollTo(0, 0);
                //Client.get().requestScroll();

            return false;
        });

        Menu menu = binding.nav.getMenu();

        for (int i = 0; i < User.getYears().length; i++) {
            menu.add(R.id.group1, i, Menu.NONE, User.getYears()[i]);
            menu.getItem(i).setIcon(AppCompatResources.getDrawable(getBaseContext(), R.drawable.ic_label));
            menu.getItem(i).setCheckable(true);
        }

        menu.getItem(pos).setChecked(true);

        if (User.getYears().length > 0)
            binding.date.setText(User.getYears()[pos]);

        Bundle bundle = getIntent().getExtras();

        if (savedInstanceState != null) {
            int pg = savedInstanceState.getInt("FRAGMENT");
            int id = R.id.navigation_grades;

            switch (pg) {
                case SCHEDULE:
                    id = R.id.navigation_home;
                    break;

                case JOURNAL:
                    id = R.id.navigation_grades;
                    break;

                case MATERIAL:
                    id = R.id.navigation_materials;
                    break;
            }

            changeFragment(id);
            binding.navigation.setSelectedItemId(id);

        } else if (bundle != null) {
            int pg = bundle.getInt("FRAGMENT");

            if (pg != 0) {
                changeFragment(R.id.navigation_materials);
                binding.navigation.setSelectedItemId(R.id.navigation_materials);
            }
        } else {
            changeFragment(R.id.navigation_grades);
            binding.navigation.setSelectedItemId(R.id.navigation_grades);
        }

        if (Client.get().isLogging()) {
            Client.get().load(PG_FETCH_YEARS);
        }

        int uses = getSharedPreferences(USE_INFO, MODE_PRIVATE).getInt(USE_COUNT, 0);
        boolean rated = getSharedPreferences(USE_INFO, MODE_PRIVATE).getBoolean(USE_RATED, false);

        if (uses > 20 && !rated) {
            new MaterialAlertDialogBuilder(MainActivity.this)
                    .setTitle(getResources().getString(R.string.dialog_evaluate_title))
                    .setMessage(getResources().getString(R.string.dialog_evaluate_text))
                    .setCancelable(true)
                    .setPositiveButton(getResources().getString(R.string.dialog_evaluate_now), (dialogInterface, i) -> {
                        ReviewManager manager = ReviewManagerFactory.create(this);
                        manager.requestReviewFlow().addOnCompleteListener(info -> {
                            if (info.isSuccessful()) {
                                manager.launchReviewFlow(this, info.getResult());
                                getSharedPreferences(USE_INFO, MODE_PRIVATE)
                                        .edit()
                                        .putBoolean(USE_RATED, true)
                                        .apply();
                            } else {
                                getSharedPreferences(USE_INFO, MODE_PRIVATE)
                                        .edit()
                                        .putInt(USE_COUNT, 0)
                                        .apply();
                            }
                        });
                    })
                    .setNegativeButton(getResources().getString(R.string.dialog_evaluate_no), (dialogInterface, i) ->
                            getSharedPreferences(USE_INFO, MODE_PRIVATE)
                                    .edit()
                                    .putBoolean(USE_RATED, true)
                                    .apply())
                    .setNeutralButton(getResources().getString(R.string.dialog_evaluate_later), (dialogInterface, i) ->
                            getSharedPreferences(USE_INFO, MODE_PRIVATE)
                                    .edit()
                                    .putInt(USE_COUNT, 0).apply())
                    .create()
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem item = binding.toolbar.getMenu().findItem(R.id.action_account);
        item.setActionView(R.layout.action_account);
        ImageView view = (ImageView) item.getActionView();

        if (!Client.isConnected() || (!Client.get().isValid() && !Client.get().isLogging())) {
            view.setImageDrawable(AppCompatResources.getDrawable(getBaseContext(), R.drawable.ic_offline));
        } else {
            try {
                Glide.with(getContext())
                        .load(User.getImg())
                        .centerCrop()
                        .placeholder(R.drawable.ic_account)
                        .into(view);
            } catch (Exception ignore) {}

            /*Drawable picture = User.getProfilePicture(getContext());

            if (picture != null)
                view.setImageDrawable(picture.getCurrent());
            else
                view.setImageDrawable(AppCompatResources.getDrawable(getBaseContext(), R.drawable.ic_account));*/
        }

        view.setOnClickListener(v -> {
            UserFragment fragment = new UserFragment();
            fragment.setListener(new UserFragment.OnButton() {
                @Override
                public void onLogout() {
                    logOut();
                }

                @Override
                public void onAlerts() {
                    displayAlerts(true);
                }
            });
            fragment.show(getSupportFragmentManager(), "sheet_user");
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;

        } else if (itemId == R.id.action_account) {
            UserFragment fragment = new UserFragment();
            fragment.setListener(new UserFragment.OnButton() {
                @Override
                public void onLogout() {
                    logOut();
                }

                @Override
                public void onAlerts() {
                    displayAlerts(true);
                }
            });
            fragment.show(getSupportFragmentManager(), "sheet_user");
            return true;

        } else if (itemId == R.id.action_grades) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment);

            if (fragment instanceof JournalsFragment) {
                ReportFragment rFragment = new ReportFragment();
                rFragment.setParams(binding.refresh);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                transaction.replace(R.id.main_fragment, rFragment).commit();

            } else if (fragment instanceof ReportFragment) {
                return changeFragment(R.id.navigation_grades);

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
            binding.drawer.closeDrawer(GravityCompat.START);
            binding.drawer.postDelayed(() -> Client.get().changeDate(itemId), 250);
            return true;
        }

        if (itemId == R.id.drawer_mail) {
            binding.drawer.closeDrawer(GravityCompat.START);
            binding.drawer.postDelayed(() ->
                    startActivity(new Intent(getBaseContext(), MessagesActivity.class)), 250);
            return true;

        } else if (itemId == R.id.drawer_calendar) {
            binding.drawer.closeDrawer(GravityCompat.START);
            binding.drawer.postDelayed(() ->
                    startActivity(new Intent(getBaseContext(), CalendarActivity.class)), 250);
            return true;

        } else if (itemId == R.id.drawer_materials) {
            binding.drawer.closeDrawer(GravityCompat.START);
            binding.drawer.postDelayed(() ->
                    startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS)), 250);
            return true;

        } else if (itemId == R.id.drawer_website) {
            binding.drawer.closeDrawer(GravityCompat.START);
            binding.drawer.postDelayed(() ->
                    startActivity(new Intent(getBaseContext(), WebViewActivity.class)), 250);
            return true;

        } else if (itemId == R.id.drawer_settings) {
            binding.drawer.closeDrawer(GravityCompat.START);
            binding.drawer.postDelayed(() ->
                    startActivity(new Intent(getBaseContext(), SettingsActivity.class)), 250);
            return true;
        }

        return false;
    }

    private void dismissProgressbar() {
        binding.refresh.setRefreshing(false);
    }

    private void logOut() {
        finish();
        Client.get().close();
        Works.cancelAll();
        DataBase.get().close();
        User.clearInfo();
        PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().clear().apply();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        startActivity(new Intent(this, LoginActivity.class));
    }

    private boolean changeFragment(int id) {
        Fragment fragment = null;

        if (id == R.id.navigation_home) {
            HomeFragment homeFragment = new HomeFragment();
            homeFragment.setParams(binding.toolbar, binding.scroll, binding.refresh, binding.fab);

            binding.fab.setOnClickListener(v -> new CreateFragment().show(
                    getSupportFragmentManager(), "sheet_create"));
            binding.fab.show();

            binding.scroll.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                    (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                        binding.refresh.setEnabled(scrollY == 0);

                        if (scrollY < oldScrollY && !binding.fab.isShown())
                            binding.fab.show();
                        else if (scrollY > oldScrollY && binding.fab.isShown())
                            binding.fab.hide();
                    });

            fragment = homeFragment;

        } else if (id == R.id.navigation_grades) {
            binding.scroll.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) null);
            binding.fab.setOnClickListener(null);
            binding.fab.postDelayed(() -> binding.fab.hide(), 250);

            JournalsFragment journalsFragment = new JournalsFragment();
            journalsFragment.setParams(binding.toolbar, binding.scroll, binding.refresh);

            fragment = journalsFragment;

        } else if (id == R.id.navigation_materials) {
            binding.scroll.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) null);
            binding.fab.setOnClickListener(null);
            binding.fab.postDelayed(() -> binding.fab.hide(), 250);

            MaterialsFragment materialsFragment = new MaterialsFragment();
            materialsFragment.setParams(binding.toolbar, binding.scroll, binding.refresh);

            fragment = materialsFragment;
        }

        if (fragment == null)
            return false;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, fragment).commit();

        return true;
    }

    private void reload() {
        if (Client.isConnected()) {
            int selectedItemId = binding.navigation.getSelectedItemId();

            if (selectedItemId == R.id.navigation_home) {
                Client.get().login();

            } else if (selectedItemId == R.id.navigation_grades) {
                Client.get().loadYear(pos);

            } else if (selectedItemId == R.id.navigation_materials) {
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

        } else if (binding.navigation.getSelectedItemId() != R.id.navigation_home
                && getSupportFragmentManager().getBackStackEntryCount() == 0) {
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
    private void displayAlerts(boolean requested) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        if (prefs.getBoolean(POPUP, true)) {

            WebView webView = new WebView(getBaseContext());
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setLoadsImagesAutomatically(false);
            webView.getSettings().setBlockNetworkImage(true);
            webView.addJavascriptInterface(new PopUpHandler(webView, this, requested), "handler");
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

    @Override
    public void onFinish(int pg) {
        dismissProgressbar();
        invalidateOptionsMenu();
        supportInvalidateOptionsMenu();
        //updateMenuIcon();

        if (pg == PG_LOGIN) {
            displayAlerts(false);

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
        invalidateOptionsMenu();
        //updateMenuIcon();
        Toast.makeText(getBaseContext(), error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAccessDenied(int pg, String message) {
        dismissProgressbar();
        invalidateOptionsMenu();
        //updateMenuIcon();

        if (pg == PG_LOGIN) {
            new MaterialAlertDialogBuilder(MainActivity.this)
                    .setTitle(getResources().getString(R.string.dialog_access_denied))
                    .setMessage(getResources().getString(R.string.dialog_access_changed))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.dialog_open_site),
                            (dialogInterface, i) -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(User.getURL()))))
                    .setNegativeButton(getResources().getString(R.string.action_logout),
                            (dialogInterface, i) -> logOut())
                    .setNeutralButton(getResources().getString(R.string.dialog_continue_offline), null)
                    .create()
                    .show();

        } else if (pg == PG_ACCESS_DENIED) {
            new MaterialAlertDialogBuilder(MainActivity.this)
                    .setTitle(getResources().getString(R.string.dialog_access_denied))
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.action_logout), (dialogInterface, i) -> logOut())
                    .setNeutralButton(getResources().getString(R.string.dialog_continue_offline), null)
                    .create()
                    .show();

        } else if (pg == PG_UPDATE) {
            new MaterialAlertDialogBuilder(MainActivity.this)
                    .setTitle(getResources().getString(R.string.dialog_update_password))
                    .setMessage(getResources().getString(R.string.dialog_update_password_msg))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.dialog_open_site),
                            (dialogInterface, i) -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(User.getURL()))))
                    .setNegativeButton(getResources().getString(R.string.action_logout),
                            (dialogInterface, i) -> logOut())
                    .setNeutralButton(getResources().getString(R.string.dialog_continue_offline), null)
                    .create()
                    .show();

        } else if (pg == PG_QUEST) {
            new MaterialAlertDialogBuilder(MainActivity.this)
                    .setTitle(getResources().getString(R.string.dialog_questionary_title))
                    .setMessage(getResources().getString(R.string.dialog_questionary_text))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.dialog_open_site),
                            (dialogInterface, i) -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(User.getURL()))))
                    .setNegativeButton(getResources().getString(R.string.action_logout),
                            (dialogInterface, i) -> logOut())
                    .setNeutralButton(getResources().getString(R.string.dialog_continue_offline), null)
                    .create()
                    .show();

        } else if (pg == PG_REGISTRATION) {
            new MaterialAlertDialogBuilder(MainActivity.this)
                    .setTitle(getResources().getString(R.string.dialog_registration_title))
                    .setMessage(getResources().getString(R.string.dialog_registration_text))
                    .setCancelable(true)
                    .setPositiveButton(getResources().getString(R.string.dialog_open_site),
                            (dialogInterface, i) -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(User.getURL()))))
                    .create()
                    .show();

        } else {
            Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getBaseContext(), getResources()
                        .getString(R.string.text_permission_denied), Toast.LENGTH_LONG).show();
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
                .setPositiveButton(getResources().getString(R.string.dialog_open_site), (dialogInterface, i) ->
                        startActivity(new Intent(getBaseContext(), WebViewActivity.class)))
                .setNegativeButton(getResources().getString(R.string.dialog_later), null)
                .create()
                .show();
    }

    @Override
    public void onCountNotifications(int count1, int count2) {
        if (count1 <= 0)
            binding.navigation.removeBadge(R.id.navigation_grades);
        else
            binding.navigation.getOrCreateBadge(R.id.navigation_grades).setNumber(Math.min(count1, 99));

        if (count2 <= 0)
            binding.navigation.removeBadge(R.id.navigation_materials);
        else
            binding.navigation.getOrCreateBadge(R.id.navigation_materials).setNumber(Math.min(count2, 99));
    }

    @Override
    public void onCountMessages(int count) {
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
    public void onDateChanged() {
        binding.date.setText(User.getYears()[pos]);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("FRAGMENT", getNavigationPosition());
    }

    private int getNavigationPosition() {
        int selectedItemId = binding.navigation.getSelectedItemId();

        if (selectedItemId == R.id.navigation_home)
            return SCHEDULE;

        if (selectedItemId == R.id.navigation_grades)
            return JOURNAL;

        if (selectedItemId == R.id.navigation_materials)
            return MATERIAL;

        return JOURNAL;
    }

}
