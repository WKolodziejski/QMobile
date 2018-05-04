package com.qacademico.qacademico.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.perf.metrics.AddTrace;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.qacademico.qacademico.Class.Boletim;
import com.qacademico.qacademico.Class.Diarios;
import com.qacademico.qacademico.Fragment.BoletimFragment;
import com.qacademico.qacademico.Fragment.DiariosFragment;
import com.qacademico.qacademico.Fragment.HomeFragment;
import com.qacademico.qacademico.Class.Horario;
import com.qacademico.qacademico.Fragment.HorarioFragment;
import com.qacademico.qacademico.Fragment.LoginFragment;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.Utilities.ChangePassword;
import com.qacademico.qacademico.Utilities.CheckUpdate;
import com.qacademico.qacademico.Utilities.Data;
import com.qacademico.qacademico.Utilities.Design;
import com.qacademico.qacademico.Utilities.SendEmail;
import com.qacademico.qacademico.Utilities.Utils;
import com.qacademico.qacademico.WebView.SingletonWebView;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.qacademico.qacademico.Utilities.Utils.pg_boletim;
import static com.qacademico.qacademico.Utilities.Utils.pg_diarios;
import static com.qacademico.qacademico.Utilities.Utils.pg_home;
import static com.qacademico.qacademico.Utilities.Utils.pg_horario;
import static com.qacademico.qacademico.Utilities.Utils.pg_login;
import static com.qacademico.qacademico.Utilities.Utils.pg_materiais;
import static com.qacademico.qacademico.Utilities.Utils.url;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        SingletonWebView.OnPageFinished, SingletonWebView.OnPageStarted {
    private SharedPreferences login_info;
    public boolean fab_isOpen;
    LayoutInflater inflater;
    @BindView(R.id.connection) LinearLayout errorConnectionLayout;
    @BindView(R.id.progressbar_main) ProgressBar progressBar_Main;
    @BindView(R.id.progressbar_horizontal) ProgressBar progressBar_Top;
    @BindView(R.id.main_container) ViewGroup mainLayout;
    Dialog loadingDialog;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    //@BindViews({R.id.fab_action, R.id.fab_data, R.id.fab_expand}) List<FloatingActionButton> fab_list;
    @BindView(R.id.fab_action) public FloatingActionButton fab_action;
    @BindView(R.id.fab_data) public FloatingActionButton fab_data;
    @BindView(R.id.fab_expand) public FloatingActionButton fab_expand;
    @BindView(R.id.txt_expand) TextView txt_expand;
    @BindView(R.id.txt_data) TextView txt_data;
    @BindView(R.id.navigation) BottomNavigationView navigation;
    Snackbar snackBar;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.btns) CoordinatorLayout buttons_layout;
    @BindView(R.id.toolbar_main) Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    LoginFragment loginFragment;
    public SingletonWebView mainWebView = SingletonWebView.getInstance();
    FirebaseRemoteConfig remoteConfig;
    private DiariosFragment diariosFragment = new DiariosFragment();
    private HomeFragment homeFragment = new HomeFragment();
    private BoletimFragment boletimFragment = new BoletimFragment();
    private HorarioFragment horarioFragment = new HorarioFragment();

    @Override
    @AddTrace(name = "onCreateTrace")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainWebView.configWebView(this);
        mainWebView.setOnPageFinishedListener(this);
        mainWebView.setOnPageStartedListener(this);
        setDefaultHashMap();
        Utils.updateDefaultValues(remoteConfig);

        ButterKnife.bind(this);

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        setSupportActionBar(toolbar);

            toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    //Design.setStatusBarLight(MainActivity.this);
                }

                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    //Design.setStatusBarTransparent(MainActivity.this);
                }
            };


        drawer.addDrawerListener(toggle);
        toggle.syncState();

        loadingDialog = new Dialog(this);

        navigationView.setNavigationItemSelectedListener(this);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        setUp(); //inicializa as variáveis necessárias
        testLogin(); // testa se o login é válido
        CheckUpdate.checkUpdate(this);
    }

    //static final ButterKnife.Setter<View, Integer> VISIBILITY = (view, value, index) -> view.setVisibility(value);

    /*
     * Método que recebe os valores do servidor remoto
     */
    private void setDefaultHashMap() {
        remoteConfig = FirebaseRemoteConfig.getInstance();
        remoteConfig.setConfigSettings(new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(true)
                .build());

        remoteConfig.setDefaults(R.xml.default_values);

        final Task<Void> fetch = remoteConfig.fetch(0);
        fetch.addOnSuccessListener(command -> {
            remoteConfig.activateFetched();
            Utils.updateDefaultValues(remoteConfig);
            Log.v("DefaultValues", "Valores atualizados");
        });

        fetch.addOnFailureListener(e -> {
            Log.v("DefaultValues", "Erro");
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {//drawer
        int id = item.getItemId();

        if (id == R.id.nav_materiais) {
            clickMateriais();
        } else if (id == R.id.nav_calendario) {
            clickCalendario();
        } else if (id == R.id.nav_documentos) {
            clickDocumentos();
        } else if (id == R.id.nav_share) {
            shareApp();
        } else if (id == R.id.nav_sug) {
            SendEmail.sendSuggestion(this);
        } else if (id == R.id.nav_bug) {
            SendEmail.bugReport(this, navigation.getSelectedItemId());
        } else if (id == R.id.nav_logout) {
            new AlertDialog.Builder(this)
                    .setCustomTitle(Utils.customAlertTitle(this, R.drawable.ic_exit_to_app_black_24dp, R.string.dialog_quit_title, R.color.colorPrimary))
                    .setMessage(R.string.dialog_quit_msg)
                    .setPositiveButton(R.string.dialog_quit_yes, (dialog, which) -> logOut())
                    .setNegativeButton(R.string.dialog_quit_no, null)
                    .show();
        } else if (id == R.id.nav_password) {
            ChangePassword.changePassword(this);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private BottomNavigationView.OnNavigationItemReselectedListener mOnNavigationItemReselectedListener
            = new BottomNavigationView.OnNavigationItemReselectedListener() {
        @Override
        public void onNavigationItemReselected(@NonNull MenuItem item) {
            RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getApplicationContext()) {
                @Override
                protected int getVerticalSnapPreference() {
                    return LinearSmoothScroller.SNAP_TO_ANY;
                }
            };
            smoothScroller.setTargetPosition(0);
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            if (item.getItemId() != navigation.getSelectedItemId()) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        setHome();
                        Design.changePageColor(MainActivity.this, toolbar, drawer, progressBar_Top, progressBar_Main,
                                mainLayout, fab_action, fab_expand, fab_data, navigation.getSelectedItemId(),
                                R.id.navigation_home, false);
                        return true;

                    case R.id.navigation_diarios:
                        setDiarios();
                        Design.changePageColor(MainActivity.this, toolbar, drawer, progressBar_Top, progressBar_Main,
                                mainLayout, fab_action, fab_expand, fab_data, navigation.getSelectedItemId(),
                                R.id.navigation_diarios, false);
                        return true;

                    case R.id.navigation_boletim:
                        setBoletim();
                        Design.changePageColor(MainActivity.this, toolbar, drawer, progressBar_Top, progressBar_Main,
                                mainLayout, fab_action, fab_expand, fab_data, navigation.getSelectedItemId(),
                                R.id.navigation_boletim, false);
                        return true;

                    case R.id.navigation_horario:
                        setHorario();
                        Design.changePageColor(MainActivity.this, toolbar, drawer, progressBar_Top, progressBar_Main,
                                mainLayout, fab_action, fab_expand, fab_data, navigation.getSelectedItemId(),
                                R.id.navigation_horario, false);
                        return true;
                }
            }
            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_bug) {
            SendEmail.openGmail(this);
            return true;
        } else if (id == R.id.action_att) {
            CheckUpdate.updateApp(this, true);
            return true;
        } else if (id == R.id.action_about) {
            Intent about = new Intent(getApplicationContext(), AboutActivity.class);
            startActivity(about);
            return true;
        } else if (id == R.id.action_changes) {
            View theView = inflater.inflate(R.layout.dialog_changelog, null);
            TextView changes = (TextView) theView.findViewById(R.id.changelog);
            changes.setText(getResources().getString(R.string.changelog_list));
            new AlertDialog.Builder(this).setView(theView)
                    .setCustomTitle(Utils.customAlertTitle(this, R.drawable.ic_history_black_24dp, R.string.action_changes, R.color.light_blue_A400))
                    .setPositiveButton(R.string.dialog_close, null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @AddTrace(name = "setHome")
    public void setHome() { //layout fragment_home

        getSupportActionBar().setTitle(getResources().getString(R.string.title_home));
        hideButtons();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment, homeFragment, "HOME");
        fragmentTransaction.commit();

            if (!mainWebView.pg_home_loaded) {
                mainWebView.html.loadUrl(url + pg_home);
            }

    }

    public void updateHome() {
        homeFragment.updateHeaderStatus(homeFragment.getView());
    }

    @AddTrace(name = "setDiarios")
    public void setDiarios() {//layout fragment_diarios

        getSupportActionBar().setTitle(getResources().getString(R.string.title_diarios));
        showButtons();

        if (Data.getDiarios(this) != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("Diarios", (Serializable) Data.getDiarios(this));
            diariosFragment.setArguments(bundle);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_fragment, diariosFragment, "DIARIOS");
            fragmentTransaction.commit();

        } else {
            showRoundProgressbar();
        }

        if (mainWebView.pg_home_loaded) {
            if (!mainWebView.pg_diarios_loaded) {
                mainWebView.html.loadUrl(url + pg_diarios);
            } else {
                getSupportActionBar().setTitle(getResources().getString(R.string.title_diarios)
                        + " ー " + mainWebView.data_diarios[mainWebView.data_position_diarios]); //mostra o ano no título
            }
        } else {
            mainWebView.html.loadUrl(url + pg_home);
        }
    }

    public void updateDiarios() {
        diariosFragment.update(Data.getDiarios(this));

        if (mainWebView.data_diarios != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.title_diarios)
                    + " ー " + mainWebView.data_diarios[mainWebView.data_position_diarios]); //mostra o ano no título
        }
    }

    @SuppressLint("StaticFieldLeak")
    @AddTrace(name = "setBoletim")
    public void setBoletim() { //layout fragment_boletim

        getSupportActionBar().setTitle(getResources().getString(R.string.title_boletim));
        showButtons();

        if (Data.getBoletim(this) != null) {

            Bundle bundle = new Bundle();
            bundle.putSerializable("Boletim", (Serializable) Data.getBoletim(this));
            boletimFragment.setArguments(bundle);

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_fragment, boletimFragment, "BOLETIM");
            fragmentTransaction.commit();

        } else {
            showRoundProgressbar();
        }

            if (mainWebView.pg_home_loaded) {
                if (!mainWebView.pg_boletim_loaded) {
                    mainWebView.html.loadUrl(url + pg_boletim);
                } else {
                    getSupportActionBar().setTitle(getResources().getString(R.string.title_boletim)
                            + " ー " + mainWebView.data_boletim[mainWebView.data_position_boletim] + " / "
                            + mainWebView.periodo_boletim[mainWebView.periodo_position_boletim]); //mostra o ano no título
                }
            } else {
                mainWebView.html.loadUrl(url + pg_home);
            }

    }

    @SuppressLint("StaticFieldLeak")
    public void updateBoletim() {

        if (mainWebView.data_boletim != null && mainWebView.periodo_boletim != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.title_boletim)
                    + " ー " + mainWebView.data_boletim[mainWebView.data_position_boletim] + " / "
                    + mainWebView.periodo_boletim[mainWebView.periodo_position_boletim]); //mostra o ano no título
        }

        new AsyncTask<Void, Void, List<Boletim>>() {
            @Override
            protected List<Boletim> doInBackground(Void... voids) {
                return Data.getBoletim(getApplicationContext());
            }

            @Override
            protected void onPostExecute(List<Boletim> boletim) {
                super.onPostExecute(boletim);
                boletimFragment.update(boletim);
            }
        }.execute();
    }

    @AddTrace(name = "setHorario")
    public void setHorario() { // layout fragment_horario

        getSupportActionBar().setTitle(getResources().getString(R.string.title_horario));
        showButtons();

        if (Data.getHorario(this) != null) {

            Bundle bundle = new Bundle();
            bundle.putSerializable("Horario", (Serializable) Data.getHorario(this));
            horarioFragment.setArguments(bundle);

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_fragment, horarioFragment, "HORARIO");
            fragmentTransaction.commit();

        } else {
            showRoundProgressbar();
        }

        if (mainWebView.pg_home_loaded) {
            if (!mainWebView.pg_horario_loaded) {
                mainWebView.html.loadUrl(url + pg_horario);
                } else {
                getSupportActionBar().setTitle(getResources().getString(R.string.title_horario)
                        + " ー " + mainWebView.data_horario[mainWebView.data_position_horario] + " / "
                        + mainWebView.periodo_horario[mainWebView.periodo_position_horario]); //mostra o ano no título
            }
        } else {
            mainWebView.html.loadUrl(url + pg_home);
        }
    }

    public void updateHorario() {
        horarioFragment.update(Data.getHorario(this));

        if (mainWebView.data_boletim != null && mainWebView.periodo_horario != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.title_horario)
                    + " ー " + mainWebView.data_horario[mainWebView.data_position_horario] + " / "
                    + mainWebView.periodo_horario[mainWebView.periodo_position_horario]); //mostra o ano no título
        }
    }

    @AddTrace(name = "setMateriais")
    public void setMateriais() { //layout fragment_home
        if ((!mainWebView.pg_material_loaded)) {
            mainWebView.html.loadUrl(url + pg_materiais);
        } else {
            /*if (materiais.size() != 0) {
                Intent intent = new Intent(getApplicationContext(), MateriaisActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("Materiais", (Serializable) materiais);
                intent.putExtras(bundle);
                presentActivity(intent, mainLayout);
            }*/
        }
    }

    public void testLogin() { //Testa se o login é válido
        if (login_info.getBoolean("valido", false)) {
            Design.changePageColor(MainActivity.this, toolbar, drawer, progressBar_Top, progressBar_Main,
                    mainLayout, fab_action, fab_expand, fab_data, navigation.getSelectedItemId(),
                    R.id.navigation_home, false);
            setHome();
            mainWebView.html.loadUrl(url + pg_login);
        } else {
            Intent login =  new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(login);
        }
    }

    public void setUp() { //Inicializa as variáveis necessárias
        login_info = getSharedPreferences("login_info", 0);
        hideButtons();
        configNavDrawer();
        Design.setNavigationTransparent(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    protected void configNavDrawer() { //Configura o NavigationDrawer lateral

        View header = navigationView.getHeaderView(0);

        LinearLayout nav_image = (LinearLayout) header.findViewById(R.id.nav_image);
        TextView user_name_drawer = (TextView) header.findViewById(R.id.msg);
        TextView matricula = (TextView) header.findViewById(R.id.matricula);
        ImageView foto = (ImageView) header.findViewById(R.id.img_foto);

        //foto.setImageBitmap(Data.getImage(this));

        user_name_drawer.setText(login_info.getString("nome", ""));

        matricula.setText(login_info.getString("matricula", ""));

        Calendar rightNow = Calendar.getInstance();

        int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);

        if (currentHour >= 5 && currentHour < 10) {
            nav_image.setBackground(getResources().getDrawable(R.drawable.drawer_morning1));
        } else if (currentHour >= 10 && currentHour < 17) {
            nav_image.setBackground(getResources().getDrawable(R.drawable.drawer_morning2));
        } else if (currentHour >= 17 && currentHour < 20) {
            nav_image.setBackground(getResources().getDrawable(R.drawable.drawer_evening));
        } else if (currentHour >= 20 || currentHour < 5) {
            nav_image.setBackground(getResources().getDrawable(R.drawable.drawer_night));
        }
    }

    @Override
    public void onPageStart(String url_p) {
        Log.i("Singleton", "onStart");

        if ((url_p.equals(url + pg_home))
                || (url_p.equals(url + pg_boletim) && navigation.getSelectedItemId() == R.id.navigation_boletim)
                || (url_p.equals(url + pg_diarios) && navigation.getSelectedItemId() == R.id.navigation_diarios)
                || (url_p.equals(url + pg_horario) && navigation.getSelectedItemId() == R.id.navigation_horario)) {
            showLinearProgressbar();
            fab_data.setClickable(false);
            fab_data.setOnClickListener(null);
        }
    }

    @Override
    public void onPageFinish(String url_p) {
        Log.i("Singleton", "onFinish");

        dismissRoundProgressbar();
        dismissLinearProgressbar();

        if (url_p.equals(url + pg_home)) {
            Log.i("onFinish", "loadedHome");
            if (navigation.getSelectedItemId() == R.id.navigation_home) {
                updateHome();
                Log.i("onFinish", "updatedHome");
            }
            configNavDrawer();
        } else if (url_p.equals(url + pg_boletim) && navigation.getSelectedItemId() == R.id.navigation_boletim) {
            updateBoletim();
            Log.i("onFinish", "updatedBoletim");
        } else if (url_p.equals(url + pg_diarios) && navigation.getSelectedItemId() == R.id.navigation_diarios) {
            updateDiarios();
            Log.i("onFinish", "updatedDiarios");
        } else if (url_p.equals(url + pg_horario) && navigation.getSelectedItemId() == R.id.navigation_horario) {
            updateHorario();
            Log.i("onFinish", "updatedHorario");
        }
    }

    public void refreshPage(View v) { //Atualiza a página
        showRoundProgressbar();
        dismissErrorConnection();
        if (!mainWebView.pg_home_loaded) {
            mainWebView.html.loadUrl(url + pg_login);
        } else {
            if (navigation.getSelectedItemId() == R.id.navigation_home) {
                mainWebView.html.loadUrl(url + pg_home);
            } else if (navigation.getSelectedItemId() == R.id.navigation_diarios) {
                mainWebView.html.loadUrl(url + pg_diarios);
            } else if (navigation.getSelectedItemId() == R.id.navigation_boletim) {
                mainWebView.html.loadUrl(url + pg_boletim);
            } else if (navigation.getSelectedItemId() == R.id.navigation_horario) {
                mainWebView.html.loadUrl(url + pg_horario);
            }
        }
    }

    public void showErrorConnection() { //Mostra a página de erro de conexão
        mainWebView.html.stopLoading();
        errorConnectionLayout.setVisibility(View.VISIBLE);
        mainLayout.setVisibility(View.GONE);
    }

    public void dismissErrorConnection() { //Esconde a página de erro de conexão
        errorConnectionLayout.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
    }

    protected void showSnackBar(String message, boolean action) { //Mostra a SnackBar
        snackBar = Snackbar.make(mainLayout, message, Snackbar.LENGTH_INDEFINITE);
        snackBar.setActionTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryLight)));
        if (action) {
            snackBar.setAction(R.string.button_wifi, view1 -> {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                snackBar.dismiss();
            });
        }
        snackBar.show();
    }

    public void dismissSnackbar() { //Esconde a SnackBar
        if (null != snackBar) {
            snackBar.dismiss();
            snackBar = null;
        }
    }

    protected void dismissProgressDialog() {  //Esconde o diálogo de loading
        if (null != loadingDialog) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    protected void showProgressDialog() { //Mostra o diálogo de loading
        if (loadingDialog == null || !loadingDialog.isShowing())
            loadingDialog = ProgressDialog.show(MainActivity.this, getResources().getString(R.string.title_loading),
                    getResources().getString(R.string.text_loading), true, false, dialog -> {
                    });
    }

    protected void showButtons() { //Mostra os FloatingActionButtons
        if (fab_action.getAnimation() == null) {
            Animation open_linear = AnimationUtils.loadAnimation(this, R.anim.fab_open_pop);
            fab_action.startAnimation(open_linear);
        }
        if ((fab_data.getAnimation() != null) || (fab_expand.getAnimation() != null)) {
            fab_action.getAnimation().setFillAfter(true);
            fab_data.getAnimation().setFillAfter(true);
            txt_data.getAnimation().setFillAfter(true);
            fab_expand.getAnimation().setFillAfter(true);
            txt_expand.getAnimation().setFillAfter(true);
        }
        fab_action.setVisibility(View.VISIBLE);
    }

    public void hideButtons() { //Esconde os FloatingActionButtons
        if (fab_action.getAnimation() != null) {
            Animation close_linear = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_close_pop);
            fab_action.startAnimation(close_linear);
            fab_action.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    fab_action.setAnimation(null);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
        if ((fab_data.getAnimation() != null) || (fab_expand.getAnimation() != null)) {
            fab_action.getAnimation().setFillAfter(false);
            fab_data.getAnimation().setFillAfter(false);
            fab_expand.getAnimation().setFillAfter(false);
            txt_expand.getAnimation().setFillAfter(false);
            txt_data.getAnimation().setFillAfter(false);
        }
        fab_action.setVisibility(View.INVISIBLE);
        fab_isOpen = false;
    }

    public void clickButtons(View v) { //Animações ao clicar no FloatingActionButton
        Animation open_rotate = AnimationUtils.loadAnimation(this, R.anim.fab_rotate_fwd);
        Animation close_rotate = AnimationUtils.loadAnimation(this, R.anim.fab_rotate_bkw);
        Animation open_linear = AnimationUtils.loadAnimation(this, R.anim.fab_open_pop);
        Animation close_linear = AnimationUtils.loadAnimation(this, R.anim.fab_close_pop);

        if (fab_isOpen) {
            fab_action.startAnimation(close_rotate);
            fab_data.startAnimation(close_linear);
            fab_expand.startAnimation(close_linear);
            txt_data.startAnimation(close_linear);
            txt_expand.startAnimation(close_linear);
            fab_data.setClickable(false);
            fab_expand.setClickable(false);
            fab_isOpen = false;
        } else {
            fab_action.startAnimation(open_rotate);
            fab_data.startAnimation(open_linear);
            fab_expand.startAnimation(open_linear);
            txt_data.startAnimation(open_linear);
            txt_expand.startAnimation(open_linear);
            fab_data.setClickable(true);
            fab_expand.setClickable(true);
            fab_isOpen = true;
        }
    }

    public void showRoundProgressbar() { //Mostra a progressBar ao carregar a página
        progressBar_Main.setVisibility(View.VISIBLE);
        fab_action.setClickable(false);
        fab_data.setClickable(false);
        fab_expand.setClickable(false);
    }

    protected void dismissRoundProgressbar() { //Esconde a progressBar ao carregar a página
        progressBar_Main.setVisibility(View.GONE);
        fab_action.setClickable(true);
        fab_data.setClickable(true);
        fab_expand.setClickable(true);
    }

    protected void showLinearProgressbar() { //Mostra a progressBar ao carregar a página
        progressBar_Top.setVisibility(View.VISIBLE);
    }

    protected void dismissLinearProgressbar() { //Esconde a progressBar ao carregar a página
        progressBar_Top.setVisibility(View.GONE);
    }

    protected void autoLoadPages() { //Tenta carregar as páginas em segundo plano
        if (navigation.getSelectedItemId() == R.id.navigation_diarios && !mainWebView.pg_diarios_loaded) {
            mainWebView.html.loadUrl(url + pg_diarios);
        } else if (navigation.getSelectedItemId() == R.id.navigation_boletim && !mainWebView.pg_boletim_loaded) {
            mainWebView.html.loadUrl(url + pg_boletim);
        } else if (navigation.getSelectedItemId() == R.id.navigation_horario && !mainWebView.pg_horario_loaded) {
            mainWebView.html.loadUrl(url + pg_horario);
        } else try {
            if (!mainWebView.pg_diarios_loaded) {
                mainWebView.html.loadUrl(url + pg_diarios);
            } else if (!mainWebView.pg_boletim_loaded) {
                mainWebView.html.loadUrl(url + pg_boletim);
            } else if (!mainWebView.pg_horario_loaded) {
                mainWebView.html.loadUrl(url + pg_horario);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void shareApp() {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            String sAux = getResources().getString(R.string.share_message);
            sAux = sAux + " " + getResources().getString(R.string.share_download);
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, getResources().getString(R.string.share_choose)));
        } catch (Exception e) {
        }
    }

    public void logOut() {
        SharedPreferences.Editor editor = login_info.edit();
        editor.putString("matricula", "");
        editor.putString("password", "");
        editor.putString("nome", "");
        editor.putBoolean("valido", false);
        editor.apply();
        recreate();
    }

    public void clickBugReport() {
        SendEmail.bugReport(this, navigation.getSelectedItemId());
    }

    public void clickShareApp() {
        shareApp();
    }

    public void clickSug() {
        SendEmail.sendSuggestion(this);
    }

    public void clickDiarios() {
        navigation.setSelectedItemId(R.id.navigation_diarios);
    }

    public void clickBoletim() {
        navigation.setSelectedItemId(R.id.navigation_boletim);
    }

    public void clickHorario() {
        navigation.setSelectedItemId(R.id.navigation_horario);
    }

    public void clickHome() {
        navigation.setSelectedItemId(R.id.navigation_home);
    }

    public void clickMateriais() {
        setMateriais();
    }

    public void clickCalendario() {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_unavailable), Toast.LENGTH_SHORT).show();
    }

    public void clickDocumentos() {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_unavailable), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case Utils.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CheckUpdate.startDownload(this, CheckUpdate.checkUpdate(this));
                }
            }
        }
    }

    @Override
    public void onDestroy() { //Esconde ProgressDiálogo ao destruir a atividade
        super.onDestroy();
        dismissProgressDialog();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (navigation.getSelectedItemId() != R.id.navigation_home) {
            navigation.setSelectedItemId(R.id.navigation_home);
        } else {
            super.onBackPressed();
        }
    }
}
