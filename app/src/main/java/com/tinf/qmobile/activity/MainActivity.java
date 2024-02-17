package com.tinf.qmobile.activity;

import static com.tinf.qmobile.App.USE_COUNT;
import static com.tinf.qmobile.App.USE_INFO;
import static com.tinf.qmobile.App.USE_RATED;
import static com.tinf.qmobile.App.getContext;
import static com.tinf.qmobile.model.ViewType.JOURNAL;
import static com.tinf.qmobile.model.ViewType.MATERIAL;
import static com.tinf.qmobile.model.ViewType.SCHEDULE;
import static com.tinf.qmobile.network.Client.pos;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.google.android.material.color.ColorRoles;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.settings.SettingsActivity;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.database.OnCount;
import com.tinf.qmobile.databinding.ActivityMainBinding;
import com.tinf.qmobile.fragment.BaseFragment;
import com.tinf.qmobile.fragment.GradesFragment;
import com.tinf.qmobile.fragment.HomeFragment;
import com.tinf.qmobile.fragment.MaterialsFragment;
import com.tinf.qmobile.fragment.OnUpdate;
import com.tinf.qmobile.fragment.dialog.CreateFragment;
import com.tinf.qmobile.fragment.dialog.PopUpFragment;
import com.tinf.qmobile.fragment.dialog.UserFragment;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.network.OnEvent;
import com.tinf.qmobile.network.OnResponse;
import com.tinf.qmobile.service.FirebaseMessageParams;
import com.tinf.qmobile.service.Works;
import com.tinf.qmobile.utility.ColorsUtils;
import com.tinf.qmobile.utility.DesignUtils;
import com.tinf.qmobile.utility.UserUtils;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements OnResponse, OnEvent, OnCount,
    OnUpdate, NavigationView.OnNavigationItemSelectedListener {
  private ActivityMainBinding binding;

  ActivityResultLauncher<Intent> searchLauncher = registerForActivityResult(
      new ActivityResultContracts.StartActivityForResult(), result ->
          Client.get()
                .restorePreviousDate());

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ColorsUtils.setSystemBarColor(this, com.google.android.material.R.attr.colorSurface);
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    setSupportActionBar(binding.toolbar);

    onDateChanged();
    buildDrawer();
    buildToolbar();
    buildNavigation();
    buildFragments(savedInstanceState);

    new Handler(Looper.getMainLooper()).post(() -> {
      if (Client.get()
                .isLogging()) {
        Client.get()
              .load(PG_FETCH_YEARS);
      }

      checkAlerts();
      checkWarningCard();
    });
  }

  private void buildDrawer() {
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawer,
                                                             binding.toolbar,
                                                             R.string.open_drawer,
                                                             R.string.close_drawer);
    binding.drawer.addDrawerListener(toggle);
    toggle.syncState();

    Menu menu = binding.nav.getMenu();

    for (int i = 0; i < UserUtils.getYears().length; i++) {
      menu.add(R.id.group1, i, Menu.NONE, UserUtils.getYears()[i]);
      menu.getItem(i)
          .setIcon(DesignUtils.getDrawable(this, R.drawable.ic_label));
      menu.getItem(i)
          .setCheckable(true);
    }

    menu.getItem(pos)
        .setChecked(true);
  }

  private void buildToolbar() {
    binding.toolbar.setOnClickListener(view ->
                                           searchLauncher.launch(new Intent(getContext(),
                                                                            SearchActivity.class)));
  }

  private void buildNavigation() {
    binding.navigation.setOnItemSelectedListener(item -> {
      int itemId = item.getItemId();

      if (itemId != binding.navigation.getSelectedItemId())
        return changeFragment(itemId);

      requestScroll();

      return false;
    });
  }

  private void buildFragments(Bundle savedInstanceState) {
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

    } else if (bundle != null && bundle.containsKey("FRAGMENT")) {
      int pg = bundle.getInt("FRAGMENT");

      if (pg != 0) {
        changeFragment(R.id.navigation_materials);
        binding.navigation.setSelectedItemId(R.id.navigation_materials);
      } else {
        changeFragment(R.id.navigation_grades);
        binding.navigation.setSelectedItemId(R.id.navigation_grades);
      }
    } else {
      changeFragment(R.id.navigation_grades);
      binding.navigation.setSelectedItemId(R.id.navigation_grades);
    }
  }

  private void checkAlerts() {
    int uses = getSharedPreferences(USE_INFO, MODE_PRIVATE).getInt(USE_COUNT, 0);
    boolean rated = getSharedPreferences(USE_INFO, MODE_PRIVATE).getBoolean(USE_RATED, false);

    if (uses <= 20 || rated) {
      return;
    }

    new MaterialAlertDialogBuilder(MainActivity.this)
        .setTitle(getResources().getString(R.string.dialog_evaluate_title))
        .setMessage(getResources().getString(R.string.dialog_evaluate_text))
        .setCancelable(true)
        .setPositiveButton(getResources().getString(R.string.dialog_evaluate_now),
                           (dialogInterface, i) -> {
                             ReviewManager manager = ReviewManagerFactory.create(this);
                             manager.requestReviewFlow()
                                    .addOnCompleteListener(info -> {
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
        .setNegativeButton(getResources().getString(R.string.dialog_evaluate_no),
                           (dialogInterface, i) ->
                               getSharedPreferences(USE_INFO, MODE_PRIVATE)
                                   .edit()
                                   .putBoolean(USE_RATED, true)
                                   .apply())
        .setNeutralButton(getResources().getString(R.string.dialog_evaluate_later),
                          (dialogInterface, i) ->
                              getSharedPreferences(USE_INFO, MODE_PRIVATE)
                                  .edit()
                                  .putInt(USE_COUNT, 0)
                                  .apply())
        .create()
        .show();
  }

  private void checkWarningCard() {
    if (!Client.isConnected()) {
      return;
    }

    FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();

    try {
      FirebaseMessageParams params = new GsonBuilder()
          .setDateFormat("yyyy-MM-dd HH:mm").
          create()
          .fromJson(
              remoteConfig.getString("message_home"),
              new TypeToken<FirebaseMessageParams>() {
              }.getType());

      if (params == null)
        return;

      if (params.showAfter == null || params.hideAfter == null || params.message == null ||
          params.show == null)
        return;

      Date now = new Date();

      boolean show = params.show && params.showAfter.before(now) && params.hideAfter.after(now);

      if (!show) {
        binding.warningCard.setVisibility(View.GONE);
        return;
      }

      if (params.link != null) {
        binding.warningCard.setOnClickListener(v -> startActivity(
            new Intent(Intent.ACTION_VIEW, Uri.parse(params.link))));
      }

      ColorRoles colorRoles = DesignUtils.getColorForWarning(getBaseContext(), params.color);

      binding.warningClose.setOnClickListener(
          v -> binding.warningCard.setVisibility(View.GONE));
      binding.warningClose.setImageTintList(
          ColorStateList.valueOf(colorRoles.getOnAccentContainer()));
      binding.warningCard.setCardBackgroundColor(colorRoles.getAccentContainer());
      binding.warningText.setTextColor(colorRoles.getOnAccentContainer());
      binding.warningText.setText(params.message);
      binding.warningCard.setVisibility(View.VISIBLE);

    } catch (Exception e) {
      if (e.getMessage() != null && !e.getMessage()
                                      .contains("hostname") &&
          !e.getMessage()
            .contains("backend")) {
        FirebaseCrashlytics.getInstance()
                           .recordException(e);
      }
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);

    MenuItem item = binding.toolbar.getMenu()
                                   .findItem(R.id.action_account);
    item.setActionView(R.layout.action_account);
    ImageView view = (ImageView) item.getActionView();

    if (UserUtils.hasImg() && Client.isConnected()) {
      try {
        Glide.with(getContext())
             .load(UserUtils.getImgUrl())
             .circleCrop()
             .placeholder(R.drawable.ic_account)
             .into(view);
      } catch (Exception e) {
        view.setImageDrawable(
            DesignUtils.getDrawable(this, R.drawable.ic_account));
      }
    } else {
      view.setImageDrawable(
          DesignUtils.getDrawable(this, R.drawable.ic_account));
    }

    view.setOnClickListener(v -> {
      UserFragment fragment = new UserFragment();
      fragment.setListener(this::logOut);
      fragment.show(getSupportFragmentManager(), "sheet_user");
    });

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();

    if (itemId == android.R.id.home) {
      getOnBackPressedDispatcher().onBackPressed();
      return true;
    }
//    else if (itemId == R.id.action_account) {
//      UserFragment fragment = new UserFragment();
//      fragment.setListener(new UserFragment.OnButton() {
//        @Override
//        public void onLogout() {
//          logOut();
//        }
//      });
//
//      fragment.show(getSupportFragmentManager(), "sheet_user");
//
//      return true;
//    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onNavigationItemSelected(
      @NonNull
      MenuItem item) {
    int groupId = item.getGroupId();
    int itemId = item.getItemId();

    if (groupId == R.id.group1) {
      binding.nav.getMenu()
                 .getItem(itemId)
                 .setChecked(true);
      binding.drawer.closeDrawer(GravityCompat.START);
      binding.drawer.postDelayed(() -> {
        Client.get()
              .changeDate(itemId);
        requestScroll();
      }, 250);
      return true;
    }

    if (itemId == R.id.drawer_mail) {
      binding.drawer.closeDrawer(GravityCompat.START);
      binding.drawer.postDelayed(() ->
                                     startActivity(new Intent(getBaseContext(),
                                                              MessagesActivity.class)),
                                 250);
      return true;

    } else if (itemId == R.id.drawer_calendar) {
      binding.drawer.closeDrawer(GravityCompat.START);
      binding.drawer.postDelayed(() ->
                                     startActivity(new Intent(getBaseContext(),
                                                              CalendarActivity.class)),
                                 250);
      return true;

    } else if (itemId == R.id.drawer_schedule) {
      binding.drawer.closeDrawer(GravityCompat.START);
      binding.drawer.postDelayed(() ->
                                     startActivity(new Intent(getBaseContext(),
                                                              ScheduleActivity.class)),
                                 250);
      return true;

//    } else if (itemId == R.id.drawer_report) {
//      binding.drawer.closeDrawer(GravityCompat.START);
//      binding.drawer.postDelayed(() ->
//                                     startActivity(new Intent(getBaseContext(),
//                                                              ReportActivity.class)),
//                                 250);
//      return true;

    } else if (itemId == R.id.drawer_performance) {
      binding.drawer.closeDrawer(GravityCompat.START);
      binding.drawer.postDelayed(() ->
                                     startActivity(new Intent(getBaseContext(),
                                                              PerformanceActivity.class)),
                                 250);
      return true;

    } else if (itemId == R.id.drawer_materials) {
      binding.drawer.closeDrawer(GravityCompat.START);
      binding.drawer.postDelayed(() -> {
                                   try {
                                     startActivity(new Intent(
                                         DownloadManager.ACTION_VIEW_DOWNLOADS));
                                   } catch (Exception e) {
                                     Toast.makeText(getBaseContext(),
                                                    getResources().getString(R.string.text_no_intent),
                                                    Toast.LENGTH_LONG)
                                          .show();
                                   }
                                 },
                                 250);
      return true;

    } else if (itemId == R.id.drawer_website) {
      binding.drawer.closeDrawer(GravityCompat.START);
      binding.drawer.postDelayed(() ->
                                     startActivity(new Intent(getBaseContext(),
                                                              WebViewActivity.class)),
                                 250);
      return true;

    } else if (itemId == R.id.drawer_settings) {
      binding.drawer.closeDrawer(GravityCompat.START);
      binding.drawer.postDelayed(() ->
                                     startActivity(new Intent(getBaseContext(),
                                                              SettingsActivity.class)),
                                 250);
      return true;
    }

    return false;
  }

  private void requestScroll() {
    try {
      BaseFragment fragment =
          (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment);
      fragment.requestScroll();
      binding.appBarLayout.setExpanded(true);
    } catch (Exception ignore) {
    }
  }

  private void dismissProgressbar() {
    binding.refresh.setRefreshing(false);
  }

  private void logOut() {
    finish();
    Client.get()
          .close();
    Works.cancelAll();
    DataBase.get()
            .close();
    UserUtils.clearInfo();
    PreferenceManager.getDefaultSharedPreferences(getBaseContext())
                     .edit()
                     .clear()
                     .apply();
    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    startActivity(new Intent(this, LoginActivity.class));
  }

  private boolean changeFragment(int id) {
    Fragment fragment = null;

    if (id == R.id.navigation_home) {
      HomeFragment homeFragment = new HomeFragment();
      homeFragment.setParams(binding.toolbar, binding.refresh, binding.fab);

      binding.fab.setOnClickListener(v -> new CreateFragment().show(
          getSupportFragmentManager(), "sheet_create"));
      binding.fab.show();

      binding.appBarLayout.setLiftOnScrollTargetViewId(R.id.scroll);

      fragment = homeFragment;

    } else if (id == R.id.navigation_grades) {
      binding.fab.setOnClickListener(null);
      binding.fab.postDelayed(binding.fab::hide, 250);

      binding.appBarLayout.setLiftOnScrollTargetViewId(R.id.recycler);

      fragment = new GradesFragment();

    } else if (id == R.id.navigation_materials) {
      binding.fab.setOnClickListener(null);
      binding.fab.postDelayed(binding.fab::hide, 250);

      binding.appBarLayout.setLiftOnScrollTargetViewId(R.id.recycler);

      MaterialsFragment materialsFragment = new MaterialsFragment();
      materialsFragment.setParams(binding.toolbar, binding.refresh);

      fragment = materialsFragment;
    }

    if (fragment == null)
      return false;

    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.replace(R.id.main_fragment, fragment)
               .commitNow();

    return true;
  }

  private void reload() {
    if (Client.isConnected()) {
      Client.get()
            .loadYear(pos);
    } else {
      dismissProgressbar();
    }
  }

  @Override
  public void onStart() {
    super.onStart();
    Client.get()
          .addOnResponseListener(this);
    Client.get()
          .addOnUpdateListener(this);
    Client.get()
          .addOnEventListener(this);
    DataBase.get()
            .addOnDataChangeListener(this);
    binding.refresh.setOnRefreshListener(this::reload);
    binding.nav.setNavigationItemSelectedListener(this);
  }

  @Override
  protected void onStop() {
    super.onStop();
    Client.get()
          .removeOnResponseListener(this);
    Client.get()
          .removeOnUpdateListener(this);
    Client.get()
          .removeOnEventListener(this);
    DataBase.get()
            .removeOnDataChangeListener(this);
    dismissProgressbar();
  }

  @Override
  protected void onResume() {
    super.onResume();
    Client.get()
          .addOnResponseListener(this);
    Client.get()
          .addOnUpdateListener(this);
    Client.get()
          .addOnEventListener(this);
    DataBase.get()
            .addOnDataChangeListener(this);
    binding.refresh.setOnRefreshListener(this::reload);
    binding.nav.setNavigationItemSelectedListener(this);
  }

  @Override
  protected void onPause() {
    super.onPause();
    Client.get()
          .removeOnResponseListener(this);
    Client.get()
          .removeOnUpdateListener(this);
    Client.get()
          .removeOnEventListener(this);
    DataBase.get()
            .removeOnDataChangeListener(this);
    dismissProgressbar();
  }

  @Override
  public void onStart(int pg) {
    if (!binding.refresh.isRefreshing())
      binding.refresh.setRefreshing(true);
  }

  @Override
  public void onFinish(int pg,
                       int year,
                       int period) {
    dismissProgressbar();

    if (pg != PG_CLASSES) {
      invalidateOptionsMenu();
      supportInvalidateOptionsMenu();
    }

    if (pg == PG_FETCH_YEARS || pg == PG_JOURNALS) {
      Menu menu = binding.nav.getMenu();
      menu.removeGroup(R.id.group1);

      for (int i = 0; i < UserUtils.getYears().length; i++) {
        menu.add(R.id.group1, i, Menu.NONE, UserUtils.getYears()[i]);
        menu.getItem(i)
            .setIcon(DesignUtils.getDrawable(this, R.drawable.ic_label));
        menu.getItem(i)
            .setCheckable(true);
      }

      menu.getItem(pos)
          .setChecked(true);
    }
  }

  @Override
  public void onError(int pg,
                      int year,
                      int period,
                      String error) {
    dismissProgressbar();
    invalidateOptionsMenu();
    Toast.makeText(getBaseContext(), error, Toast.LENGTH_LONG)
         .show();
  }

  @Override
  public void onAccessDenied(int pg,
                             String message) {
    dismissProgressbar();
    invalidateOptionsMenu();

    if (pg == PG_LOGIN) {
      new MaterialAlertDialogBuilder(MainActivity.this)
          .setTitle(getResources().getString(R.string.dialog_access_denied))
          .setMessage(getResources().getString(R.string.dialog_access_changed))
          .setCancelable(false)
          .setPositiveButton(getResources().getString(R.string.dialog_open_site),
                             (dialogInterface, i) -> startActivity(
                                 new Intent(Intent.ACTION_VIEW,
                                            Uri.parse(UserUtils.getURL()))))
          .setNegativeButton(getResources().getString(R.string.action_logout),
                             (dialogInterface, i) -> logOut())
          .setNeutralButton(getResources().getString(R.string.dialog_continue_offline),
                            null)
          .create()
          .show();

    } else if (pg == PG_ACCESS_DENIED) {
      new MaterialAlertDialogBuilder(MainActivity.this)
          .setTitle(getResources().getString(R.string.dialog_access_denied))
          .setMessage(message)
          .setCancelable(false)
          .setPositiveButton(getResources().getString(R.string.action_logout),
                             (dialogInterface, i) -> logOut())
          .setNeutralButton(getResources().getString(R.string.dialog_continue_offline),
                            null)
          .create()
          .show();

    } else if (pg == PG_UPDATE) {
      new MaterialAlertDialogBuilder(MainActivity.this)
          .setTitle(getResources().getString(R.string.dialog_update_password))
          .setMessage(getResources().getString(R.string.dialog_update_password_msg))
          .setCancelable(false)
          .setPositiveButton(getResources().getString(R.string.dialog_open_site),
                             (dialogInterface, i) -> startActivity(
                                 new Intent(Intent.ACTION_VIEW,
                                            Uri.parse(UserUtils.getURL()))))
          .setNegativeButton(getResources().getString(R.string.action_logout),
                             (dialogInterface, i) -> logOut())
          .setNeutralButton(getResources().getString(R.string.dialog_continue_offline),
                            null)
          .create()
          .show();

    } else if (pg == PG_QUEST) {
      new MaterialAlertDialogBuilder(MainActivity.this)
          .setTitle(getResources().getString(R.string.dialog_questionary_title))
          .setMessage(getResources().getString(R.string.dialog_questionary_text))
          .setCancelable(false)
          .setPositiveButton(getResources().getString(R.string.dialog_open_site),
                             (dialogInterface, i) -> startActivity(
                                 new Intent(Intent.ACTION_VIEW,
                                            Uri.parse(UserUtils.getURL()))))
          .setNegativeButton(getResources().getString(R.string.action_logout),
                             (dialogInterface, i) -> logOut())
          .setNeutralButton(getResources().getString(R.string.dialog_continue_offline),
                            null)
          .create()
          .show();

    } else if (pg == PG_REGISTRATION) {
      new MaterialAlertDialogBuilder(MainActivity.this)
          .setTitle(getResources().getString(R.string.dialog_registration_title))
          .setMessage(getResources().getString(R.string.dialog_registration_text))
          .setCancelable(true)
          .setPositiveButton(getResources().getString(R.string.dialog_open_site),
                             (dialogInterface, i) -> startActivity(
                                 new Intent(Intent.ACTION_VIEW,
                                            Uri.parse(UserUtils.getURL()))))
          .create()
          .show();

    } else {
      Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG)
           .show();
    }
  }

  @Override
  public void onDialog(String title,
                       String msg) {
    PopUpFragment popup = new PopUpFragment();
    popup.setComponents(title, msg);
    popup.show(getSupportFragmentManager(), "sheet_popup");
  }

  @Override
  public void onRenewalAvailable() {
    new MaterialAlertDialogBuilder(MainActivity.this)
        .setTitle(getResources().getString(R.string.dialog_renewal_title))
        .setMessage(getResources().getString(R.string.dialog_renewal_txt))
        .setCancelable(true)
        .setPositiveButton(getResources().getString(R.string.dialog_open_site),
                           (dialogInterface, i) ->
                               startActivity(new Intent(getBaseContext(),
                                                        WebViewActivity.class)))
        .setNegativeButton(getResources().getString(R.string.dialog_later), null)
        .create()
        .show();
  }

  @Override
  public void onCountNotifications(int countGrades,
                                   int countMaterials) {
    if (countGrades <= 0)
      binding.navigation.removeBadge(R.id.navigation_grades);
    else
      binding.navigation.getOrCreateBadge(R.id.navigation_grades)
                        .setNumber(Math.min(countGrades, 99));

    if (countMaterials <= 0)
      binding.navigation.removeBadge(R.id.navigation_materials);
    else
      binding.navigation.getOrCreateBadge(R.id.navigation_materials)
                        .setNumber(Math.min(countMaterials, 99));
  }

  @Override
  public void onCountMessages(int count) {
    TextView txt = (TextView) binding.nav.getMenu()
                                         .findItem(R.id.drawer_mail)
                                         .getActionView();

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
    if (UserUtils.getYears().length > pos) {
      binding.date.setText(UserUtils.getYears()[pos]);
    }
  }

  @Override
  protected void onSaveInstanceState(
      @NonNull
      Bundle outState) {
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
