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
import com.qacademico.qacademico.Class.Trabalho;
import com.qacademico.qacademico.Utilities.CheckUpdate;
import com.qacademico.qacademico.Utilities.Utils;
import com.qacademico.qacademico.WebView.SingletonWebView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static com.qacademico.qacademico.Utilities.Utils.email_from;
import static com.qacademico.qacademico.Utilities.Utils.email_from_pwd;
import static com.qacademico.qacademico.Utilities.Utils.email_to;
import static com.qacademico.qacademico.Utilities.Utils.pg_boletim;
import static com.qacademico.qacademico.Utilities.Utils.pg_change_password;
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
                setStatusBarLight();
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                setStatusBarTransparent();
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        loadingDialog = new Dialog(this);

        navigationView.setNavigationItemSelectedListener(this);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        setUp(); //inicializa as variáveis necessárias
        testLogin(); // testa se o login é válido
        updateApp(false);
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
            sendEmail();
        } else if (id == R.id.nav_bug) {
            bugReport();
        } else if (id == R.id.nav_logout) {
            new AlertDialog.Builder(this)
                    .setCustomTitle(Utils.customAlertTitle(this, R.drawable.ic_exit_to_app_black_24dp, R.string.dialog_quit_title, R.color.colorPrimary))
                    .setMessage(R.string.dialog_quit_msg)
                    .setPositiveButton(R.string.dialog_quit_yes, (dialog, which) -> logOut())
                    .setNegativeButton(R.string.dialog_quit_no, null)
                    .show();
        } else if (id == R.id.nav_password) {
            changePassword();
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
                        changePageColor(R.id.navigation_home);
                        return true;

                    case R.id.navigation_diarios:
                        setDiarios();
                        changePageColor(R.id.navigation_diarios);
                        return true;

                    case R.id.navigation_boletim:
                        changePageColor(R.id.navigation_boletim);
                        setBoletim();
                        return true;

                    case R.id.navigation_horario:
                        setHorario();
                        changePageColor(R.id.navigation_horario);
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
            String[] TO = {email_to};
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "QAcadMobile| " + getResources().getString(R.string.email_assunto_bug));
            emailIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.email_content_bug));
            final PackageManager pm = getPackageManager();
            final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
            ResolveInfo best = null;
            for (final ResolveInfo info : matches)
                if (info.activityInfo.packageName.endsWith(".gm") ||
                        info.activityInfo.name.toLowerCase().contains("gmail")) best = info;
            if (best != null)
                emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
            startActivity(emailIntent);
            return true;
        } else if (id == R.id.action_att) {
            updateApp(true);
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

        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        removeBehavior();
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

        getSupportActionBar().setTitle(getResources().getString(R.string.title_diarios));
        applyBehavior();
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
        applyBehavior();
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
        applyBehavior();
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
            changePageColor(R.id.navigation_home);
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
        setNavigationTransparent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    protected void setNavigationTransparent() { //Configura os botões de navegação do Android para transparente
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    protected void configNavDrawer() { //Configura o NavigationDrawer lateral

        navigation.setVisibility(View.VISIBLE);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_menu_black_24dp));
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

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
            if (navigation.getSelectedItemId() == R.id.navigation_home) {
                setHome();
                Log.i("onFinish", "setHome");
            }
            configNavDrawer();
        } else if (url_p.equals(url + pg_boletim)) {
            if (navigation.getSelectedItemId() == R.id.navigation_boletim) {
                setBoletim();
                Log.i("onFinish", "setHome");
            }
        } else if (url_p.equals(url + pg_diarios)) {
            if (navigation.getSelectedItemId() == R.id.navigation_diarios) {
                setDiarios();
                Log.i("onFinish", "setHome");
            }
        } else if (url_p.equals(url + pg_horario)) {
            if (navigation.getSelectedItemId() == R.id.navigation_horario) {
                setHorario();
                Log.i("onFinish", "setHome");
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

    protected void passwordCheck(TextInputEditText obj, TextInputEditText pass_atual, TextInputEditText pass_nova,
                                 TextInputEditText pass_nova_confirm, TextInputLayout pass_atual_ly, ImageView img, TextView txt) { //Checa os campos para alterar a senha

        obj.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!pass_atual.getText().toString().equals(login_info.getString("password", ""))) {
                    pass_atual_ly.setErrorEnabled(true);
                }
                if (pass_nova.getText().toString().equals("") || pass_nova_confirm.getText().toString().equals("")) {
                    img.setImageResource(R.drawable.ic_edit_black_24dp);
                    txt.setText(R.string.passchange_txt_empty);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        img.setImageTintList(ColorStateList.valueOf((getResources().getColor(R.color.red_500))));
                        txt.setTextColor(getResources().getColor(R.color.red_500));
                    }
                } else if (!pass_nova.getText().toString().equals(pass_nova_confirm.getText().toString())) {
                    img.setImageResource(R.drawable.ic_cancel_black_24dp);
                    txt.setText(R.string.passchange_txt_different);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        img.setImageTintList(ColorStateList.valueOf((getResources().getColor(R.color.red_500))));
                        txt.setTextColor(getResources().getColor(R.color.red_500));
                    }
                } else if (pass_nova.getText().toString().equals(pass_nova_confirm.getText().toString()) && count < 8) {
                    img.setImageResource(R.drawable.ic_short_text_black_24dp);
                    txt.setText(R.string.passchange_txt_short);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        img.setImageTintList(ColorStateList.valueOf((getResources().getColor(R.color.amber_500))));
                        txt.setTextColor(getResources().getColor(R.color.amber_500));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (pass_atual.getText().toString().equals(login_info.getString("password", ""))) {
                    pass_atual_ly.setErrorEnabled(false);
                }
                if (pass_nova.getText().toString().equals(pass_nova_confirm.getText().toString()) && pass_nova.getText().length() >= 8 && pass_atual.getText().toString().equals(login_info.getString("password", ""))) {
                    img.setImageResource(R.drawable.ic_done_all_black_24dp);
                    txt.setText(R.string.passchange_txt_equals);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        img.setImageTintList(ColorStateList.valueOf((getResources().getColor(R.color.green_500))));
                        txt.setTextColor(getResources().getColor(R.color.green_500));
                    }
                } else if (pass_nova.getText().toString().equals(pass_nova_confirm.getText().toString()) && pass_nova.getText().length() >= 8) {
                    img.setImageResource(R.drawable.ic_check_black_24dp);
                    txt.setText(R.string.passchange_txt_old_equals);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        img.setImageTintList(ColorStateList.valueOf((getResources().getColor(R.color.blue_500))));
                        txt.setTextColor(getResources().getColor(R.color.blue_500));
                    }
                }
            }
        });
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

    public void changePageColor(int id) { //Muda a cor do app dependendo da página
        int colorPrimaryBtnTo = 0;
        int colorSecondaryTo = 0;
        int colorPrimaryBtnFrom = 0;
        int colorSecondaryFrom = 0;
        int colorStatusBarTo = 0;
        int colorStatusBarFrom = 0;
        int colorActionBarTo = 0;
        int colorActionBarFrom = 0;
        int colorTitleTo = 0;
        int colorTitleFrom = 0;
        int colorToolBarTo = 0;
        int colorToolBarFrom = 0;
        int colorProgressTo = 0;
        int colorProgressFrom = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            colorStatusBarFrom = getWindow().getStatusBarColor();
        }

        if (colorStatusBarFrom == getResources().getColor(R.color.colorPrimaryDark)) {
            colorPrimaryBtnFrom = getResources().getColor(R.color.white);
            colorSecondaryFrom = getResources().getColor(R.color.colorPrimaryDark);
            colorActionBarFrom = getResources().getColor(R.color.white);
            colorTitleFrom = getResources().getColor(R.color.colorPrimary);
            colorToolBarFrom = getResources().getColor(R.color.colorAccent);
            colorProgressFrom = getResources().getColor(R.color.colorAccent);
        } else if (colorStatusBarFrom == getResources().getColor(R.color.blue_800)) {
            colorPrimaryBtnFrom = getResources().getColor(R.color.cyan_A700);
            colorSecondaryFrom = getResources().getColor(R.color.cyan_A400);
            colorActionBarFrom = getResources().getColor(R.color.blue_600);
            colorTitleFrom = getResources().getColor(R.color.white);
            colorToolBarFrom = getResources().getColor(R.color.blue_50);
            colorProgressFrom = getResources().getColor(R.color.cyan_A700);
        } else if (colorStatusBarFrom == getResources().getColor(R.color.teal_800)) {
            colorPrimaryBtnFrom = getResources().getColor(R.color.green_A700);
            colorSecondaryFrom = getResources().getColor(R.color.green_A400);
            colorActionBarFrom = getResources().getColor(R.color.teal_600);
            colorTitleFrom = getResources().getColor(R.color.white);
            colorToolBarFrom = getResources().getColor(R.color.teal_50);
            colorProgressFrom = getResources().getColor(R.color.green_A700);
        } else if (colorStatusBarFrom == getResources().getColor(R.color.orange_800)) {
            colorPrimaryBtnFrom = getResources().getColor(R.color.yellow_A700);
            colorSecondaryFrom = getResources().getColor(R.color.yellow_A400);
            colorActionBarFrom = getResources().getColor(R.color.orange_600);
            colorTitleFrom = getResources().getColor(R.color.white);
            colorToolBarFrom = getResources().getColor(R.color.deep_orange_50);
            colorProgressFrom = getResources().getColor(R.color.yellow_A700);
        }

        if (id == R.id.navigation_home) {
            colorPrimaryBtnTo = getResources().getColor(R.color.white);
            colorSecondaryTo = getResources().getColor(R.color.colorPrimaryDark);
            colorStatusBarTo = getResources().getColor(R.color.colorPrimaryDark);
            colorActionBarTo = getResources().getColor(R.color.white);
            colorTitleTo = getResources().getColor(R.color.colorPrimary);
            colorToolBarTo = getResources().getColor(R.color.colorAccent);
            colorProgressTo = getResources().getColor(R.color.colorAccent);
            setSystemBarTheme(this, false);
        } else if (id == R.id.navigation_diarios) {
            colorPrimaryBtnTo = getResources().getColor(R.color.yellow_A700);
            colorSecondaryTo = getResources().getColor(R.color.yellow_A400);
            colorStatusBarTo = getResources().getColor(R.color.orange_800);
            colorActionBarTo = getResources().getColor(R.color.orange_600);
            colorTitleTo = getResources().getColor(R.color.white);
            colorToolBarTo = getResources().getColor(R.color.deep_orange_50);
            colorProgressTo = getResources().getColor(R.color.yellow_A700);
            setSystemBarTheme(this, true);
        } else if (id == R.id.navigation_boletim) {
            colorPrimaryBtnTo = getResources().getColor(R.color.green_A700);
            colorSecondaryTo = getResources().getColor(R.color.green_A400);
            colorStatusBarTo = getResources().getColor(R.color.teal_800);
            colorActionBarTo = getResources().getColor(R.color.teal_600);
            colorTitleTo = getResources().getColor(R.color.white);
            colorToolBarTo = getResources().getColor(R.color.teal_50);
            colorProgressTo = getResources().getColor(R.color.green_A700);
            setSystemBarTheme(this, true);
        } else if (id == R.id.navigation_horario) {
            colorPrimaryBtnTo = getResources().getColor(R.color.cyan_A700);
            colorSecondaryTo = getResources().getColor(R.color.cyan_A400);
            colorStatusBarTo = getResources().getColor(R.color.blue_800);
            colorActionBarTo = getResources().getColor(R.color.blue_600);
            colorTitleTo = getResources().getColor(R.color.white);
            colorToolBarTo = getResources().getColor(R.color.blue_50);
            colorProgressTo = getResources().getColor(R.color.cyan_A700);
            setSystemBarTheme(this, true);
        }

        changeButtonColorAnim(fab_action, colorPrimaryBtnFrom, colorPrimaryBtnTo);
        changeButtonColorAnim(fab_expand, colorSecondaryFrom, colorSecondaryTo);
        changeButtonColorAnim(fab_data, colorSecondaryFrom, colorSecondaryTo);
        changeProgressBarColor(colorProgressFrom, colorProgressTo);
        changeStatusBarColor(colorStatusBarFrom, colorStatusBarTo, colorActionBarTo, colorActionBarFrom, colorTitleTo,
                colorTitleFrom, colorToolBarFrom, colorToolBarTo);
    }

    protected void changeButtonColorAnim(FloatingActionButton fab, int colorStart, int colorEnd) {  //Muda a cor dos FloatingActionButtons
        ValueAnimator fabC = ValueAnimator.ofObject(new ArgbEvaluator(), colorStart, colorEnd);
        fabC.addUpdateListener(animator -> {
            fab.setBackgroundTintList(ColorStateList.valueOf((Integer) animator.getAnimatedValue()));
        });
        fabC.setDuration(250);
        fabC.setStartDelay(0);
        fabC.start();
    }

    protected void changeProgressBarColor(int colorStart, int colorEnd) {
        ValueAnimator fabC = ValueAnimator.ofObject(new ArgbEvaluator(), colorStart, colorEnd);
        fabC.addUpdateListener(animator -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                progressBar_Top.setIndeterminateTintList(ColorStateList.valueOf((Integer) animator.getAnimatedValue()));
                progressBar_Main.setIndeterminateTintList(ColorStateList.valueOf((Integer) animator.getAnimatedValue()));
            }
        });
        fabC.setDuration(250);
        fabC.setStartDelay(0);
        fabC.start();
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

    protected void changeStatusBarColor(int colorStatusBarFrom, int colorStatusBarTo, int colorActionBarTo, int colorActionBarFrom,
                                        int colorTitleTo, int colorTitleFrom, int colorToolBarFrom, int colorToolbarTo) { //Muda a cor da StatusBar e da ActionBar
        ValueAnimator actionBar = ValueAnimator.ofObject(new ArgbEvaluator(), colorActionBarFrom, colorActionBarTo);
        ValueAnimator statusBar = ValueAnimator.ofObject(new ArgbEvaluator(), colorStatusBarFrom, colorStatusBarTo);
        ValueAnimator title = ValueAnimator.ofObject(new ArgbEvaluator(), colorTitleFrom, colorTitleTo);
        ValueAnimator tool = ValueAnimator.ofObject(new ArgbEvaluator(), colorToolBarFrom, colorToolbarTo);

        actionBar.addUpdateListener(animator -> {
            toolbar.setBackgroundColor((Integer) animator.getAnimatedValue());
        });

        statusBar.addUpdateListener(animator -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor((Integer) animator.getAnimatedValue());
            }
        });

        title.addUpdateListener(animator -> {
            toolbar.setTitleTextColor((Integer) animator.getAnimatedValue());
        });

        tool.addUpdateListener(animator -> {
            Drawable nav = ContextCompat.getDrawable(this, R.drawable.ic_menu_black_24dp);
            nav = DrawableCompat.wrap(nav);
            DrawableCompat.setTint(nav, (Integer) animator.getAnimatedValue());
            toolbar.setNavigationIcon(nav);

            Drawable menu = ContextCompat.getDrawable(this, R.drawable.ic_more_vert_black_24dp);
            menu = DrawableCompat.wrap(menu);
            DrawableCompat.setTint(menu, (Integer) animator.getAnimatedValue());
            toolbar.setOverflowIcon(menu);
        });

        actionBar.setDuration(250);
        actionBar.setStartDelay(0);
        actionBar.start();
        statusBar.setDuration(250);
        statusBar.setStartDelay(0);
        statusBar.start();
        title.setDuration(250);
        title.setStartDelay(0);
        title.start();
        tool.setDuration(250);
        tool.setStartDelay(0);
        tool.start();

        drawer.setStatusBarBackgroundColor(colorStatusBarTo);
    }

    public static void setSystemBarTheme(final Activity pActivity, final boolean pIsDark) { //Muda o tema do app para StatusBar Light ou Dark
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final int lFlags = pActivity.getWindow().getDecorView().getSystemUiVisibility();
            pActivity.getWindow().getDecorView().setSystemUiVisibility(pIsDark ? (lFlags & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) : (lFlags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR));
        }
    }

    protected void applyBehavior() { //Habilita o actionBar a se esconder ao rolar a página
        CoordinatorLayout.LayoutParams mainLayoutLayoutParams = (CoordinatorLayout.LayoutParams) mainLayout.getLayoutParams();
        mainLayoutLayoutParams.setBehavior(new AppBarLayout.ScrollingViewBehavior());
        mainLayout.requestLayout();
        AppBarLayout.LayoutParams toolbarLayoutParams = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        toolbarLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);

        mainLayoutLayoutParams.setMargins((int) (0 * getResources().getDisplayMetrics().density), (int) (0 * getResources().getDisplayMetrics().density),
                (int) (0 * getResources().getDisplayMetrics().density), (int) (0 * getResources().getDisplayMetrics().density));

        mainLayout.setLayoutParams(mainLayoutLayoutParams);
    }

    public void removeBehavior() { //Desabilita o actionBar a se esconder ao rolar a página
        CoordinatorLayout.LayoutParams mainLayoutLayoutParams = (CoordinatorLayout.LayoutParams) mainLayout.getLayoutParams();
        mainLayoutLayoutParams.setBehavior(null);
        AppBarLayout.LayoutParams toolbarLayoutParams = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        toolbarLayoutParams.setScrollFlags(0);

        mainLayoutLayoutParams.setMargins((int) (0 * getResources().getDisplayMetrics().density), (int) (55 * getResources().getDisplayMetrics().density),
                (int) (0 * getResources().getDisplayMetrics().density), (int) (0 * getResources().getDisplayMetrics().density));
        mainLayout.setLayoutParams(mainLayoutLayoutParams);
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

    protected void changePassword() {

        if (Utils.isConnected(getApplicationContext()) && mainWebView.pg_home_loaded) {

            View theView = inflater.inflate(R.layout.dialog_password_change, null);
            TextInputEditText pass_atual = (TextInputEditText) theView.findViewById(R.id.pass_atual);
            TextInputEditText pass_nova = (TextInputEditText) theView.findViewById(R.id.pass_nova);
            TextInputEditText pass_nova_confirm = (TextInputEditText) theView.findViewById(R.id.pass_nova_confirm);
            TextInputLayout pass_atual_ly = (TextInputLayout) theView.findViewById(R.id.pass_atual_ly);
            ImageView img = (ImageView) theView.findViewById(R.id.pass_img);
            TextView txt = (TextView) theView.findViewById(R.id.pass_txt);

            pass_atual.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!pass_atual.getText().toString().equals(login_info.getString("password", ""))) {
                        pass_atual_ly.setErrorEnabled(true);
                        pass_atual_ly.setError("teste");
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (pass_atual.getText().toString().equals(login_info.getString("password", ""))) {
                        pass_atual_ly.setErrorEnabled(false);
                    }
                    if (pass_nova.getText().toString().equals(pass_nova_confirm.getText().toString()) && pass_nova.getText().length() >= 8
                            && pass_atual.getText().toString().equals(login_info.getString("password", ""))) {
                        img.setImageResource(R.drawable.ic_done_all_black_24dp);
                        txt.setText(R.string.passchange_txt_equals);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            img.setImageTintList(ColorStateList.valueOf((getResources().getColor(R.color.green_500))));
                            txt.setTextColor(getResources().getColor(R.color.green_500));
                        }
                    } else if (pass_nova.getText().toString().equals(pass_nova_confirm.getText().toString()) && pass_nova.getText().length() >= 8) {
                        img.setImageResource(R.drawable.ic_check_black_24dp);
                        txt.setText(R.string.passchange_txt_old_equals);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            img.setImageTintList(ColorStateList.valueOf((getResources().getColor(R.color.blue_500))));
                            txt.setTextColor(getResources().getColor(R.color.blue_500));
                        }
                    }
                }
            });

            passwordCheck(pass_nova, pass_atual, pass_nova, pass_nova_confirm, pass_atual_ly, img, txt);
            passwordCheck(pass_nova_confirm, pass_atual, pass_nova, pass_nova_confirm, pass_atual_ly, img, txt);

            new AlertDialog.Builder(MainActivity.this).setView(theView)
                    .setTitle(R.string.menu_password)
                    .setCustomTitle(Utils.customAlertTitle(this, R.drawable.ic_lock_outline_black_24dp, R.string.menu_password, R.color.colorPrimary))
                    .setPositiveButton(R.string.dialog_confirm, (dialog, which) -> {
                        if (pass_nova.getText().toString().equals(pass_nova_confirm.getText().toString()) && pass_nova.getText().length() >= 8
                                && pass_atual.getText().toString().equals(login_info.getString("password", ""))) {
                            mainWebView.new_password = pass_nova.getText().toString();
                            showProgressDialog();
                            mainWebView.html.loadUrl(url + pg_change_password);
                        } else {
                            new AlertDialog.Builder(this)
                                    .setCustomTitle(Utils.customAlertTitle(this, R.drawable.ic_cancel_black_24dp, R.string.error_title, R.color.red_500))
                                    .setMessage(R.string.passchange_txt_error_message)
                                    .setPositiveButton(R.string.dialog_close, null)
                                    .show();
                        }
                    }).setNegativeButton(R.string.dialog_cancel, null)
                    .show();
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_no_connection), Toast.LENGTH_SHORT).show();
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

    protected void sendEmail() {
        if (Utils.isConnected(this)) {

            View theView = inflater.inflate(R.layout.dialog_sug, null);
            EditText message = (EditText) theView.findViewById(R.id.email_message);
            RatingBar rating = (RatingBar) theView.findViewById(R.id.ratingBar);

            new AlertDialog.Builder(this).setView(theView)
                    .setCustomTitle(Utils.customAlertTitle(this, R.drawable.ic_chat_black_24dp, R.string.email_assunto_sug, R.color.pink_500))
                    .setPositiveButton(R.string.dialog_confirm, (dialog, which) -> {
                        if (!message.getText().toString().equals("")) {
                            emailPattern("QAcadMobile Sugestion", message.getText().toString() + "\n\nNota: " + String.valueOf(rating.getRating()));
                        } else {
                            new AlertDialog.Builder(this)
                                    .setCustomTitle(Utils.customAlertTitle(this, R.drawable.ic_sentiment_neutral_black_24dp, R.string.error_title_oops, R.color.amber_500))
                                    .setMessage(R.string.email_empty)
                                    .setPositiveButton(R.string.dialog_close, null)
                                    .show();
                        }
                    })
                    .setNegativeButton(R.string.dialog_cancel, null)
                    .show();
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_no_connection), Toast.LENGTH_SHORT).show();
        }
    }

    protected void setStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    protected void setStatusBarLight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    protected void bugReport() {
        if (Utils.isConnected(getApplicationContext())) {

            //autoLoadPages();

            View theView = inflater.inflate(R.layout.dialog_bug, null);
            EditText message = (EditText) theView.findViewById(R.id.bug_message);
            CheckBox check_boletim = (CheckBox) theView.findViewById(R.id.bug_check_boletim);
            CheckBox check_diarios = (CheckBox) theView.findViewById(R.id.bug_check_diarios);
            CheckBox check_horario = (CheckBox) theView.findViewById(R.id.bug_check_horario);
            CheckBox check_outro = (CheckBox) theView.findViewById(R.id.bug_check_outro);
            GridLayout grid_boletim = (GridLayout) theView.findViewById(R.id.bug_grid_boletim);
            GridLayout grid_diarios = (GridLayout) theView.findViewById(R.id.bug_grid_diarios);
            GridLayout grid_horario = (GridLayout) theView.findViewById(R.id.bug_grid_horario);
            GridLayout grid_outro = (GridLayout) theView.findViewById(R.id.bug_grid_outro);
            ImageView img_boletim = (ImageView) theView.findViewById(R.id.bug_img_boletim);
            ImageView img_diarios = (ImageView) theView.findViewById(R.id.bug_img_diarios);
            ImageView img_horario = (ImageView) theView.findViewById(R.id.bug_img_horario);
            ImageView img_outros = (ImageView) theView.findViewById(R.id.bug_img_outros);
            TextView txt_boletim = (TextView) theView.findViewById(R.id.bug_txt_boletim);
            TextView txt_diarios = (TextView) theView.findViewById(R.id.bug_txt_diarios);
            TextView txt_horario = (TextView) theView.findViewById(R.id.bug_txt_horario);
            TextView txt_outros = (TextView) theView.findViewById(R.id.bug_txt_outros);

            if (navigation.getSelectedItemId() == R.id.navigation_diarios) {
                check_diarios.setChecked(true);
                check_diarios.setEnabled(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    check_diarios.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.green_500)));
                    img_diarios.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green_500)));
                    txt_diarios.setTextColor(getResources().getColor(R.color.green_500));
                }
            } else {
                checkBoxBugReport(grid_diarios, check_diarios, img_diarios, txt_diarios);
            }

            if (navigation.getSelectedItemId() == R.id.navigation_boletim) {
                check_boletim.setChecked(true);
                check_boletim.setEnabled(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    check_boletim.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.green_500)));
                    img_boletim.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green_500)));
                    txt_boletim.setTextColor(getResources().getColor(R.color.green_500));
                }
            } else {
                checkBoxBugReport(grid_boletim, check_boletim, img_boletim, txt_boletim);
            }

            if (navigation.getSelectedItemId() == R.id.navigation_horario) {
                check_horario.setChecked(true);
                check_horario.setEnabled(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    check_horario.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.green_500)));
                    img_horario.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green_500)));
                    txt_horario.setTextColor(getResources().getColor(R.color.green_500));
                }
            } else {
                checkBoxBugReport(grid_horario, check_horario, img_horario, txt_horario);
            }

            checkBoxBugReport(grid_outro, check_outro, img_outros, txt_outros);

            new AlertDialog.Builder(MainActivity.this).setView(theView)
                    .setCustomTitle(Utils.customAlertTitle(this, R.drawable.ic_bug_report_black_24dp, R.string.email_assunto_bug, R.color.green_500))
                    .setPositiveButton(R.string.dialog_confirm, (dialog, which) -> {

                        String message_final = "";

                        if (!message.getText().toString().equals("")) {

                            if (check_boletim.isChecked() && !mainWebView.bugBoletim.equals("")) {
                                message_final += "\n---------------------------------------------------------------------------------------------------";
                                message_final += "BOLETIM";
                                message_final += "---------------------------------------------------------------------------------------------------\n";
                                message_final += mainWebView.bugBoletim;
                            }

                            if (check_diarios.isChecked() && !mainWebView.bugDiarios.equals("")) {
                                message_final += "\n---------------------------------------------------------------------------------------------------";
                                message_final += "DIARIOS";
                                message_final += "---------------------------------------------------------------------------------------------------\n";
                                message_final += mainWebView.bugDiarios;
                            }

                            if (check_horario.isChecked() && !mainWebView.bugBoletim.equals("")) {
                                message_final += "\n---------------------------------------------------------------------------------------------------";
                                message_final += "HORARIO";
                                message_final += "---------------------------------------------------------------------------------------------------\n";
                                message_final += mainWebView.bugHorario;
                            }

                            if (!message_final.equals("") || !message.getText().toString().equals("")) {
                                emailPattern("QAcadMobile Bug Report", message.getText().toString() + message_final);
                            } else {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setCustomTitle(Utils.customAlertTitle(this, R.drawable.ic_sync_problem_black_24dp, R.string.error_title, R.color.amber_500))
                                        .setMessage(R.string.page_load_empty)
                                        .setPositiveButton(R.string.dialog_close, null)
                                        .show();
                            }
                        } else {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setCustomTitle(Utils.customAlertTitle(this, R.drawable.ic_sentiment_neutral_black_24dp, R.string.error_title_oops, R.color.amber_500))
                                    .setMessage(R.string.email_empty)
                                    .setPositiveButton(R.string.dialog_close, null)
                                    .show();
                        }
                    })
                    .setNegativeButton(R.string.dialog_cancel, null)
                    .show();
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_no_connection), Toast.LENGTH_SHORT).show();
        }
    }

    private void emailPattern(String subject, String message) {
        BackgroundMail.newBuilder(this)
                .withUsername(email_from)
                .withPassword(email_from_pwd)
                .withMailto(email_to)
                .withType(BackgroundMail.TYPE_PLAIN)
                .withSubject(subject)
                .withBody(login_info.getString("nome", "") + ",\n\n" + message)
                .withSendingMessage(R.string.email_sending)
                .withSendingMessageError(null)
                .withSendingMessageSuccess(null)
                .withOnSuccessCallback(() -> new AlertDialog.Builder(this)
                        .setCustomTitle(Utils.customAlertTitle(this, R.drawable.ic_sentiment_very_satisfied_black_24dp, R.string.success_title, R.color.green_500))
                        .setMessage(R.string.email_success)
                        .setPositiveButton(R.string.dialog_close, null)
                        .show())
                .withOnFailCallback(() -> new AlertDialog.Builder(this)
                        .setCustomTitle(Utils.customAlertTitle(this, R.drawable.ic_cancel_black_24dp, R.string.error_title, R.color.red_500))
                        .setMessage(R.string.email_error)
                        .setPositiveButton(R.string.dialog_close, null)
                        .show())
                .send();
    }

    protected void checkBoxBugReport(GridLayout layout, CheckBox chk, ImageView img, TextView txt) {
        layout.setOnClickListener(v -> {
            if (chk.isChecked()) {
                chk.setChecked(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    chk.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                    img.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                    txt.setTextColor(getResources().getColor(R.color.colorAccent));
                }
            } else {
                chk.setChecked(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    chk.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.green_500)));
                    img.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green_500)));
                    txt.setTextColor(getResources().getColor(R.color.green_500));
                }
            }
        });

        chk.setOnClickListener(v -> {
            if (chk.isChecked()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    chk.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.green_500)));
                    img.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green_500)));
                    txt.setTextColor(getResources().getColor(R.color.green_500));
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    chk.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                    img.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                    txt.setTextColor(getResources().getColor(R.color.colorAccent));
                }
            }
        });
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
        bugReport();
    }

    public void clickShareApp() {
        shareApp();
    }

    public void clickSug() {
        sendEmail();
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

    private void updateApp(boolean showNotFound) {
        if (Utils.isConnected(this)) {
            if (!CheckUpdate.checkUpdate(this).equals("")) {
                new AlertDialog.Builder(this)
                        .setCustomTitle(Utils.customAlertTitle(this, R.drawable.ic_update_black_24dp, R.string.dialog_att_title, R.color.colorPrimary))
                        .setMessage(String.format(getResources().getString(R.string.dialog_att_encontrada), "" + CheckUpdate.verLocal, "" + CheckUpdate.verWeb))
                        .setPositiveButton(R.string.dialog_att_download, (dialog, which) -> {
                            if (Build.VERSION.SDK_INT >= 23) {
                                if (ContextCompat.checkSelfPermission(this,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        != PackageManager.PERMISSION_GRANTED) {

                                    ActivityCompat.requestPermissions(this,
                                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            Utils.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                                }
                            }
                            if (Utils.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE == PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= 23) {
                                CheckUpdate.startDownload(this, CheckUpdate.checkUpdate(this));
                            } else if (Build.VERSION.SDK_INT < 23) {
                                CheckUpdate.startDownload(this, CheckUpdate.checkUpdate(this));
                            }
                        })
                        .setNegativeButton(R.string.dialog_cancel, null)
                        .show();
            } else {
                if (showNotFound) {
                    Toast.makeText(this, getResources().getString(R.string.toast_nenhuma_atualizacao), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.text_no_connection), Toast.LENGTH_SHORT).show();
        }
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

    /*@Override
    @AddTrace(name = "onActivityResult")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        recreate();
    }*/

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
