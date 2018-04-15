package com.qacademico.qacademico.Activity;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.google.android.gms.tasks.Task;
import com.google.firebase.perf.metrics.AddTrace;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.qacademico.qacademico.Class.Boletim;
import com.qacademico.qacademico.Class.Diarios;
import com.qacademico.qacademico.Class.Etapa;
import com.qacademico.qacademico.Fragment.BoletimFragment;
import com.qacademico.qacademico.Fragment.DiariosFragment;
import com.qacademico.qacademico.Fragment.HomeFragment;
import com.qacademico.qacademico.Class.Horario;
import com.qacademico.qacademico.Class.Materia;
import com.qacademico.qacademico.Class.Materiais;
import com.qacademico.qacademico.Class.Material;
import com.qacademico.qacademico.Fragment.HorarioFragment;
import com.qacademico.qacademico.Fragment.LoginFragment;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.Utilities.ChangePassword;
import com.qacademico.qacademico.Utilities.CheckUpdate;
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

import static com.qacademico.qacademico.Utilities.Utils.pg_boletim;
import static com.qacademico.qacademico.Utilities.Utils.pg_diarios;
import static com.qacademico.qacademico.Utilities.Utils.pg_home;
import static com.qacademico.qacademico.Utilities.Utils.pg_horario;
import static com.qacademico.qacademico.Utilities.Utils.pg_login;
import static com.qacademico.qacademico.Utilities.Utils.pg_materiais;
import static com.qacademico.qacademico.Utilities.Utils.url;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SingletonWebView.OnPageFinished {
    private SharedPreferences login_info;
    public boolean fab_isOpen;
    LayoutInflater inflater;
    LinearLayout errorConnectionLayout;
    ProgressBar progressBar_Main, progressBar_Top;
    ViewGroup mainLayout;
    Dialog loadingDialog;
    DrawerLayout drawer;
    public FloatingActionButton fab_action, fab_data, fab_expand;
    TextView txt_expand, txt_data;
    BottomNavigationView navigation;
    Snackbar snackBar;
    NavigationView navigationView;
    CoordinatorLayout buttons_layout;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    LoginFragment loginFragment;
    public SingletonWebView mainWebView;
    FirebaseRemoteConfig remoteConfig;

    @Override
    @AddTrace(name = "onCreateTrace")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainWebView = SingletonWebView.getInstance();
        mainWebView.configWebView(this);
        mainWebView.onPageFinished(this);
        setDefaultHashMap();
        Utils.updateDefaultValues(remoteConfig);

        mainLayout = (ViewGroup) findViewById(R.id.main_container);
        errorConnectionLayout = (LinearLayout) findViewById(R.id.connection);
        progressBar_Main = (ProgressBar) findViewById(R.id.progressbar);
        progressBar_Top = (ProgressBar) findViewById(R.id.progressbar_horizontal);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        fab_action = (FloatingActionButton) findViewById(R.id.fab_action);
        fab_data = (FloatingActionButton) findViewById(R.id.fab_data);
        fab_expand = (FloatingActionButton) findViewById(R.id.fab_expand);
        txt_expand = (TextView) findViewById(R.id.txt_expand);
        txt_data = (TextView) findViewById(R.id.txt_data);
        buttons_layout = (CoordinatorLayout) findViewById(R.id.btns);
        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);

        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                Design.setStatusBarLight(MainActivity.this);
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Design.setStatusBarTransparent(MainActivity.this);
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
                                mainLayout, fab_action, fab_expand, fab_data, R.id.navigation_home);
                        return true;

                    case R.id.navigation_diarios:
                        setDiarios();
                        Design.changePageColor(MainActivity.this, toolbar, drawer, progressBar_Top, progressBar_Main,
                                mainLayout, fab_action, fab_expand, fab_data, R.id.navigation_diarios);
                        return true;

                    case R.id.navigation_boletim:
                        Design.changePageColor(MainActivity.this, toolbar, drawer, progressBar_Top, progressBar_Main,
                                mainLayout, fab_action, fab_expand, fab_data, R.id.navigation_boletim);
                        setBoletim();
                        return true;

                    case R.id.navigation_horario:
                        setHorario();
                        Design.changePageColor(MainActivity.this, toolbar, drawer, progressBar_Top, progressBar_Main,
                                mainLayout, fab_action, fab_expand, fab_data, R.id.navigation_horario);
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
    public void setHome() { //layout layout_home

        getSupportActionBar().setTitle(getResources().getString(R.string.title_home));
        hideButtons();
        dismissRoundProgressbar();
        dismissLinearProgressbar();

        HomeFragment homeFragment = new HomeFragment();

        try {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_fragment, homeFragment);
            fragmentTransaction.commit();
        } catch (Exception e){}

        if (!mainWebView.pg_home_loaded) {
            mainWebView.html.loadUrl(url + pg_home);
            showLinearProgressbar();
        }
    }

    @AddTrace(name = "setDiarios")
    public void setDiarios() {//layout layout_diarios

        Log.i("setDiarios", "seted");

        getSupportActionBar().setTitle(getResources().getString(R.string.title_diarios));
        showButtons();
        dismissRoundProgressbar();
        dismissLinearProgressbar();

        ObjectInputStream object;
        List<Diarios> diarios = null;

        try {
            object = new ObjectInputStream(new FileInputStream(getFileStreamPath(login_info.getString("matricula",
                    "") + ".diarios")));
            diarios = (List<Diarios>) object.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (diarios != null) {
            DiariosFragment diariosFragment = new DiariosFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("Diarios", (Serializable) diarios);
            diariosFragment.setArguments(bundle);

            try {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_fragment, diariosFragment);
                fragmentTransaction.commit();
            } catch (Exception e){}

            if (mainWebView.pg_diarios_loaded && mainWebView.data_diarios != null) {

                fab_data.setOnClickListener(v -> {

                    fab_data.setClickable(true);

                    fab_isOpen = true;
                    clickButtons(null);

                    View theView = inflater.inflate(R.layout.dialog_date_picker, null);

                    final NumberPicker year = (NumberPicker) theView.findViewById(R.id.year_picker);
                    year.setMinValue(0);
                    year.setMaxValue(mainWebView.data_diarios.length - 1);
                    year.setValue(mainWebView.data_position_diarios);
                    year.setDisplayedValues(mainWebView.data_diarios);
                    year.setWrapSelectorWheel(false);

                    NumberPicker periodo = (NumberPicker) theView.findViewById(R.id.periodo_picker);
                    periodo.setVisibility(View.GONE);

                    TextView slash = (TextView) theView.findViewById(R.id.slash);
                    slash.setVisibility(View.GONE);

                    new AlertDialog.Builder(this).setView(theView)
                            .setCustomTitle(Utils.customAlertTitle(this, R.drawable.ic_date_range_black_24dp,
                                    R.string.dialog_date_change, R.color.orange_500))
                            .setPositiveButton(R.string.dialog_confirm, (dialog, which) -> {

                                mainWebView.data_position_diarios = year.getValue();

                                Log.v("Ano selecionado", String.valueOf(
                                        mainWebView.data_diarios[mainWebView.data_position_diarios]));
                                mainWebView.html.loadUrl(url + pg_diarios);
                                mainWebView.scriptDiario = "javascript: var option = document.getElementsByTagName('option'); option["
                                        + (mainWebView.data_position_diarios + 1) + "].selected = true; document.forms['frmConsultar'].submit();";
                                Log.i("SCRIPT", "" + mainWebView.scriptDiario);
                            }).setNegativeButton(R.string.dialog_cancel, null)
                            .show();
                });
            } else {
                setButtonUnclickable();
            }
        } else {
            showRoundProgressbar();
        }

            if (mainWebView.pg_home_loaded) {
                if (!mainWebView.pg_diarios_loaded) {
                    mainWebView.html.loadUrl(url + pg_diarios);
                    showLinearProgressbar();
                } else {
                    getSupportActionBar().setTitle(getResources().getString(R.string.title_diarios)
                            + " ー " + mainWebView.data_diarios[mainWebView.data_position_diarios]); //mostra o ano no título
                }
            } else {
                mainWebView.html.loadUrl(url + pg_home);
                showLinearProgressbar();
            }

    }

    @AddTrace(name = "setBoletim")
    public void setBoletim() { //layout layout_boletim

        getSupportActionBar().setTitle(getResources().getString(R.string.title_boletim));
        showButtons();
        dismissRoundProgressbar();
        dismissLinearProgressbar();

        ObjectInputStream object;
        List<Boletim> boletim = null;
        try {
            object = new ObjectInputStream(new FileInputStream(getFileStreamPath(login_info.getString("matricula", "") + ".boletim")));
            boletim = (List<Boletim>) object.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (boletim != null) {
            BoletimFragment boletimFragment = new BoletimFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("Boletim", (Serializable) boletim);
            boletimFragment.setArguments(bundle);

            try {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_fragment, boletimFragment);
                fragmentTransaction.commit();
            } catch (Exception e){}

            if (mainWebView.pg_boletim_loaded && mainWebView.data_boletim != null) {

                fab_data.setOnClickListener(v -> {

                    fab_data.setClickable(true);

                    fab_isOpen = true;
                    clickButtons(null);

                    View theView = inflater.inflate(R.layout.dialog_date_picker, null);

                    final NumberPicker year = (NumberPicker) theView.findViewById(R.id.year_picker);
                    year.setMinValue(0);
                    year.setMaxValue(mainWebView.data_boletim.length - 1);
                    year.setValue(mainWebView.data_position_boletim);
                    year.setDisplayedValues(mainWebView.data_boletim);
                    year.setWrapSelectorWheel(false);

                    final NumberPicker periodo = (NumberPicker) theView.findViewById(R.id.periodo_picker);
                    periodo.setMinValue(0);
                    periodo.setMaxValue(mainWebView.periodo_boletim.length - 1);
                    periodo.setValue(mainWebView.periodo_position_boletim);
                    periodo.setDisplayedValues(mainWebView.periodo_boletim);
                    periodo.setWrapSelectorWheel(false);

                    new AlertDialog.Builder(this).setView(theView)
                            .setCustomTitle(Utils.customAlertTitle(this, R.drawable.ic_date_range_black_24dp,
                                    R.string.dialog_date_change, R.color.teal_400))
                            .setPositiveButton(R.string.dialog_confirm, (dialog, which) -> {

                                mainWebView.data_position_boletim = year.getValue();
                                mainWebView.periodo_position_boletim = periodo.getValue();

                                if (mainWebView.data_position_boletim == Integer.parseInt(mainWebView.data_boletim[0])) {
                                    mainWebView.html.loadUrl(url + pg_boletim);
                                } else {
                                    mainWebView.html.loadUrl(url + pg_boletim + "&COD_MATRICULA=-1&cmbanos="
                                            + mainWebView.data_boletim[mainWebView.data_position_boletim]
                                            + "&cmbperiodos=" + mainWebView.periodo_boletim[mainWebView.periodo_position_boletim] + "&Exibir+Boletim");
                                }
                            }).setNegativeButton(R.string.dialog_cancel, null)
                            .show();//
                });
            } else {
                setButtonUnclickable();
            }
        } else {
            showRoundProgressbar();
        }

        if (mainWebView.pg_home_loaded) {
            if (!mainWebView.pg_boletim_loaded) {
                mainWebView.html.loadUrl(url + pg_boletim);
                showLinearProgressbar();
            } else {
                getSupportActionBar().setTitle(getResources().getString(R.string.title_boletim)
                        + " ー " + mainWebView.data_boletim[mainWebView.data_position_boletim] + " / "
                        + mainWebView.periodo_boletim[mainWebView.periodo_position_boletim]); //mostra o ano no título
            }
        } else {
            mainWebView.html.loadUrl(url + pg_home);
            showLinearProgressbar();
        }

    }

    @AddTrace(name = "setHorario")

    public void setHorario() { // layout layout_horario

        getSupportActionBar().setTitle(getResources().getString(R.string.title_horario));
        showButtons();
        dismissRoundProgressbar();
        dismissLinearProgressbar();

        ObjectInputStream object;
        List<Horario> horario = null;
        try {
            object = new ObjectInputStream(new FileInputStream(getFileStreamPath(login_info.getString("matricula", "") + ".horario")));
            horario = (List<Horario>) object.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (horario != null) {
            HorarioFragment horarioFragment = new HorarioFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("Horario", (Serializable) horario);
            horarioFragment.setArguments(bundle);

            try {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_fragment, horarioFragment);
                fragmentTransaction.commit();
            } catch (Exception e){}

            if (mainWebView.pg_horario_loaded && mainWebView.data_horario != null) {

                fab_data.setOnClickListener(v -> {

                    fab_data.setClickable(true);

                    fab_isOpen = true;
                    clickButtons(null);

                    View theView = inflater.inflate(R.layout.dialog_date_picker, null);

                    final NumberPicker year = (NumberPicker) theView.findViewById(R.id.year_picker);
                    year.setMinValue(0);
                    year.setMaxValue(mainWebView.data_horario.length - 1);
                    year.setValue(mainWebView.data_position_horario);
                    year.setDisplayedValues(mainWebView.data_horario);
                    year.setWrapSelectorWheel(false);

                    final NumberPicker periodo = (NumberPicker) theView.findViewById(R.id.periodo_picker);
                    periodo.setMinValue(0);
                    periodo.setMaxValue(mainWebView.periodo_horario.length - 1);
                    periodo.setValue(mainWebView.periodo_position_horario);
                    periodo.setDisplayedValues(mainWebView.periodo_horario);
                    periodo.setWrapSelectorWheel(false);

                    new AlertDialog.Builder(this).setView(theView)
                            .setCustomTitle(Utils.customAlertTitle(this, R.drawable.ic_date_range_black_24dp,
                                    R.string.dialog_date_change, R.color.blue_400))
                            .setPositiveButton(R.string.dialog_confirm, (dialog, which) -> {

                                mainWebView.data_position_horario = year.getValue();
                                mainWebView.periodo_position_horario = periodo.getValue();


                                if (mainWebView.data_position_horario == Integer.parseInt(mainWebView.data_horario[0])) {
                                    mainWebView.html.loadUrl(url + pg_horario);
                                } else {
                                    mainWebView.html.loadUrl(url + pg_horario + "&COD_MATRICULA=-1&cmbanos=" +
                                            mainWebView.data_horario[mainWebView.data_position_horario]
                                            + "&cmbperiodos=" + mainWebView.periodo_horario[mainWebView.periodo_position_horario] + "&Exibir=OK");
                                }
                            }).setNegativeButton(R.string.dialog_cancel, null)
                            .show();
                });
            } else {
                setButtonUnclickable();
            }
        } else {
            showRoundProgressbar();
        }

            if (mainWebView.pg_home_loaded) {
                if (!mainWebView.pg_horario_loaded) {
                    mainWebView.html.loadUrl(url + pg_horario);
                    showLinearProgressbar();
                } else {
                    getSupportActionBar().setTitle(getResources().getString(R.string.title_horario)
                            + " ー " + mainWebView.data_horario[mainWebView.data_position_horario] + " / "
                            + mainWebView.periodo_horario[mainWebView.periodo_position_horario]); //mostra o ano no título
                }
            } else {
                mainWebView.html.loadUrl(url + pg_home);
                showLinearProgressbar();
            }

    }

    @AddTrace(name = "setMateriais")
    public void setMateriais() { //layout layout_home
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
                    mainLayout, fab_action, fab_expand, fab_data, R.id.navigation_home);
            setHome();
            mainWebView.html.loadUrl(url + pg_login);
        } else {
            logIn();
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
    public void OnPageFinish(String url_p) {
        Log.i("Singleton", "onFinish");
        if (url_p.equals(url + pg_home)) {
            Log.i("onFinish", "loadedHome");
            if (navigation.getSelectedItemId() == R.id.navigation_home) {
                clickHome();
                Log.i("onFinish", "setHome");
            }
            configNavDrawer();
        } else if (url_p.equals(url + pg_boletim)) {
            Log.i("onFinish", "loadedBoletim");
            if (navigation.getSelectedItemId() == R.id.navigation_boletim) {
                clickBoletim();
                Log.i("onFinish", "setBoletim");
            }
        } else if (url_p.equals(url + pg_diarios)) {
            Log.i("onFinish", "loadedDiarios");
            if (navigation.getSelectedItemId() == R.id.navigation_diarios) {
                clickDiarios();
                Log.i("onFinish", "clickDiarios");
            }
        } else if (url_p.equals(url + pg_horario)) {
            Log.i("onFinish", "loadedHorario");
            if (navigation.getSelectedItemId() == R.id.navigation_horario) {
                clickHorario();
                Log.i("onFinish", "setHorario");
            }
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
            fab_expand.getAnimation().setFillAfter(true);
            txt_expand.getAnimation().setFillAfter(true);
            txt_data.getAnimation().setFillAfter(true);
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

    private void setButtonUnclickable() {
        fab_data.setClickable(false);
        fab_data.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.grey_300)));
    }

    protected void showRoundProgressbar() { //Mostra a progressBar ao carregar a página
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

    public void logIn() {
        CoordinatorLayout.LayoutParams mainLayoutLayoutParams = (CoordinatorLayout.LayoutParams) mainLayout.getLayoutParams();
        mainLayoutLayoutParams.setMargins((int) (0 * getResources().getDisplayMetrics().density), (int) (55 * getResources().getDisplayMetrics().density),
                (int) (0 * getResources().getDisplayMetrics().density), (int) (0 * getResources().getDisplayMetrics().density));
        mainLayout.setLayoutParams(mainLayoutLayoutParams);
        mainWebView.isLoginPage = true;
        navigation.setVisibility(View.GONE);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_lock_outline_black_24dp));
        toolbar.setTitle(getResources().getString(R.string.title_activity_login));
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        loginFragment = new LoginFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment, loginFragment);
        fragmentTransaction.commit();
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Utils.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
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
