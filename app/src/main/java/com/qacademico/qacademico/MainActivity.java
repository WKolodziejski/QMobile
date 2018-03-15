package com.qacademico.qacademico;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.gms.tasks.Task;
import com.google.firebase.perf.metrics.AddTrace;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AdapterGuide.OnGuideClicked {
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    static String url, pg_login, pg_home, pg_diarios, pg_boletim, pg_horario, pg_material, pg_change_password, pg_erro, download_update_url, email_to, email_from, email_from_pwd;
    private String matricula, password, nome, home_msg, new_password, bugDiarios, bugBoletim, bugHorario, scriptDiario, linkAtt;
    private SharedPreferences login_info;
    private WebView html;
    boolean pg_diarios_loaded, pg_horario_loaded, pg_boletim_loaded, pg_home_loaded, pg_material_loaded, fab_isOpen, change_password, systemClick, isLoginPage;
    LayoutInflater inflater;
    LinearLayout errorConnectionLayout;
    ProgressBar progressBar_Main, progressBar_login;
    ViewGroup mainLayout, loginLayout;
    Dialog loadingDialog;
    DrawerLayout drawer;
    FloatingActionButton fab_action, fab_data, fab_expand;
    TextView txt_expand, txt_data;
    List<Horario> horario;
    List<Boletim> boletim;
    List<Diarios> diarios;
    List<Etapa> etapas;
    List<Trabalho> trabalhos;
    List<Materia> materias;
    List<Guide> guide;
    String[] data_boletim, data_horario, data_diarios, periodo_horario, periodo_boletim;
    int data_position_horario, data_position_boletim, data_position_diarios, periodo_position_horario, periodo_position_boletim;
    BottomNavigationView navigation;
    float verLocal, verWeb;
    Snackbar snackBar;
    NavigationView navigationView;
    CoordinatorLayout buttons_layout;
    RecyclerView recyclerViewDiarios, recyclerViewBoletim, recyclerViewHorario;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    FirebaseRemoteConfig remoteConfig;

    @Override
    @AddTrace(name = "onCreateTrace")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLayout = (ViewGroup) findViewById(R.id.main_container);
        errorConnectionLayout = (LinearLayout) findViewById(R.id.connection);
        progressBar_Main = (ProgressBar) findViewById(R.id.progressbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        fab_action = (FloatingActionButton) findViewById(R.id.fab_action);
        fab_data = (FloatingActionButton) findViewById(R.id.fab_data);
        fab_expand = (FloatingActionButton) findViewById(R.id.fab_expand);
        txt_expand = (TextView) findViewById(R.id.txt_expand);
        txt_data = (TextView) findViewById(R.id.txt_data);
        buttons_layout = (CoordinatorLayout) findViewById(R.id.btns);
        html = new WebView(this);
        WebSettings faller = html.getSettings();
        faller.setJavaScriptEnabled(true);
        faller.setDomStorageEnabled(true);
        faller.setLoadsImagesAutomatically(false);
        faller.setUseWideViewPort(true);
        faller.setLoadWithOverviewMode(true);

        loadingDialog = new Dialog(MainActivity.this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        setDefaultHashMap();
        setUp(); //inicializa as variáveis necessárias
        testLogin(); // testa se o login é válido
    }

    private void setDefaultHashMap() {
        remoteConfig = FirebaseRemoteConfig.getInstance();
        remoteConfig.setConfigSettings(new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(true)
                .build());

        /*defaults.put("default_url", getResources().getString(R.string.url));
        defaults.put("pg_login", getResources().getString(R.string.pg_login));
        defaults.put("pg_home", getResources().getString(R.string.pg_home));
        defaults.put("pg_diarios", getResources().getString(R.string.pg_diarios));
        defaults.put("pg_boletim", getResources().getString(R.string.pg_boletim));
        defaults.put("pg_horario", getResources().getString(R.string.pg_horario));
        defaults.put("pg_change_password", getResources().getString(R.string.pg_change_password));
        defaults.put("pg_erro", getResources().getString(R.string.pg_erro));
        defaults.put("download_update_url", getResources().getString(R.string.download_update_url));*/

        remoteConfig.setDefaults(R.xml.default_values);

        final Task<Void> fetch = remoteConfig.fetch(0);
        fetch.addOnSuccessListener(this, aVoid -> {
            remoteConfig.activateFetched();
            updateDefaultValues();
        });
    }

    private void updateDefaultValues() {
        url = remoteConfig.getString("default_url");
        pg_login = remoteConfig.getString("pg_login");
        pg_home = remoteConfig.getString("pg_home");
        pg_diarios = remoteConfig.getString("pg_diarios");
        pg_boletim = remoteConfig.getString("pg_boletim");
        pg_horario = remoteConfig.getString("pg_horario");
        pg_material = remoteConfig.getString("pg_material");
        pg_change_password = remoteConfig.getString("pg_change_password");
        pg_erro = remoteConfig.getString("pg_erro");
        download_update_url = remoteConfig.getString("download_update_url");
        email_to = remoteConfig.getString("email_to");
        email_from = remoteConfig.getString("email_from");
        email_from_pwd = remoteConfig.getString("email_from_pass");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("OnConfigurationChanged", "Não foi destruído yey");
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
                    .setCustomTitle(customAlertTitle(R.drawable.ic_exit_to_app_black_24dp, R.string.dialog_quit_title, R.color.colorPrimary))
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

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            View layout;

            if (item.getItemId() != navigation.getSelectedItemId() || systemClick) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        layout = inflater.inflate(R.layout.layout_home, null);
                        changeView(layout, R.string.app_name);
                        setHome();
                        return true;

                    case R.id.navigation_diarios:
                        layout = inflater.inflate(R.layout.layout_diarios, null);
                        changeView(layout, R.string.title_diarios);
                        setDiarios();
                        return true;

                    case R.id.navigation_boletim:
                        layout = inflater.inflate(R.layout.layout_boletim, null);
                        changeView(layout, R.string.title_boletim);
                        setBoletim();
                        return true;

                    case R.id.navigation_horario:
                        layout = inflater.inflate(R.layout.layout_horario, null);
                        changeView(layout, R.string.title_horario);
                        setHorario();
                        return true;
                }
            } else {
                RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getApplicationContext()) {
                    @Override
                    protected int getVerticalSnapPreference() {
                        return LinearSmoothScroller.SNAP_TO_ANY;
                    }
                };
                smoothScroller.setTargetPosition(0);
                try {
                    if (item.getItemId() == R.id.navigation_home && pg_home_loaded) {
                        ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_home);
                        scrollView.smoothScrollTo(0, 0);
                    } else if (item.getItemId() == R.id.navigation_diarios && recyclerViewDiarios != null) {
                        RecyclerView.LayoutManager recyclerLayout = recyclerViewDiarios.getLayoutManager();
                        recyclerLayout.startSmoothScroll(smoothScroller);
                    } else if (item.getItemId() == R.id.navigation_boletim && recyclerViewBoletim != null) {
                        RecyclerView.LayoutManager recyclerLayout = recyclerViewBoletim.getLayoutManager();
                        recyclerLayout.startSmoothScroll(smoothScroller);
                    } else if (item.getItemId() == R.id.navigation_horario && recyclerViewHorario != null) {
                        RecyclerView.LayoutManager recyclerLayout = recyclerViewHorario.getLayoutManager();
                        recyclerLayout.startSmoothScroll(smoothScroller);
                    }
                } catch (Exception e) {
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
            final String version;
            try {
                PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
                version = pInfo.versionName;
                getWebUpdate(version, true);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return true;
        } else if (id == R.id.action_about) {
            Intent about = new Intent(getApplicationContext(), AboutActivity.class);
            startActivity(about);
            return true;
        } else if (id == R.id.action_changes) {
            View theView = inflater.inflate(R.layout.dialog_changelog, null);
            TextView changes = (TextView) theView.findViewById(R.id.changelog);
            changes.setText(getResources().getString(R.string.changelog_list));
            new AlertDialog.Builder(MainActivity.this).setView(theView)
                    .setCustomTitle(customAlertTitle(R.drawable.ic_history_black_24dp, R.string.action_changes, R.color.light_blue_A400))
                    .setPositiveButton(R.string.dialog_close, null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @AddTrace(name = "getWebUpdate")
    private void getWebUpdate(final String versaoAtual, final boolean showNotFound) {

        if (isConnected(getApplicationContext())) {
            new Thread(() -> {
                verLocal = Float.parseFloat(versaoAtual);
                try {
                    Document doc = Jsoup.connect(download_update_url).get();
                    String verAtt = doc.getElementsByTag("a").last().text();
                    Log.v("VERATT", verAtt);
                    linkAtt = doc.getElementsByTag("a").last().attr("abs:href");
                    if (!verAtt.equals("") && verAtt.contains("Mobile")) {
                        verAtt = verAtt.substring((verAtt.indexOf("Mobile ") + 7), (verAtt.indexOf(".apk")));
                        verWeb = Float.parseFloat(verAtt);
                        Log.v("UPDATEAPP", "web: " + verWeb + " atual: " + verLocal + "  " + linkAtt);
                    } else {
                        verWeb = 0;
                    }
                } catch (IOException e) {
                    Log.v("UPDATEAPP", "erro");
                }

                runOnUiThread(() -> {
                    if (verLocal < verWeb) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setCustomTitle(customAlertTitle(R.drawable.ic_update_black_24dp, R.string.dialog_att_title, R.color.colorPrimary))
                                .setMessage(String.format(getResources().getString(R.string.dialog_att_encontrada), "" + verLocal, "" + verWeb))
                                .setPositiveButton(R.string.dialog_att_download, (dialog, which) -> {
                                    if (Build.VERSION.SDK_INT >= 23) {
                                        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                                != PackageManager.PERMISSION_GRANTED) {

                                            ActivityCompat.requestPermissions(MainActivity.this,
                                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                                        }
                                    }
                                    if (MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE == PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= 23) {
                                        startDownload();
                                    } else if (Build.VERSION.SDK_INT < 23) {
                                        startDownload();
                                    }

                                })
                                .setNegativeButton(R.string.dialog_cancel, null)
                                .show();
                    } else {
                        if (showNotFound) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_nenhuma_atualizacao), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }).start();
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_no_connection), Toast.LENGTH_SHORT).show();
        }
    }

    @AddTrace(name = "setHome")
    public void setHome() { //layout layout_home
        if ((!pg_home_loaded)) {
            html.loadUrl(url + pg_home);
            showErrorConnection();
        } else {
            removeBehavior();
            hideButtons();
            dismissSnackbar();
            dismissProgressbar();
            dismissErrorConnection();

            AdapterGuide adapter = new AdapterGuide(guide, getBaseContext());
            adapter.setOnClick(this);

            new Thread() {
                @Override
                public void run() {
                    TextView msg = (TextView) findViewById(R.id.welcome_msg);
                    SwipeRefreshLayout mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

                    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_guide);
                    FlexboxLayoutManager layout = new FlexboxLayoutManager(getBaseContext());
                    layout.setFlexDirection(FlexDirection.ROW);
                    layout.setJustifyContent(JustifyContent.FLEX_START);

                    runOnUiThread(() -> {
                        mySwipeRefreshLayout.setOnRefreshListener(() -> recreate());
                        msg.setText(String.format(getResources().getString(R.string.home_welcome_message), home_msg, nome));

                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(layout);

                        changePageColor();
                    });
                }
            }.start();
        }
    }

    @AddTrace(name = "setDiarios")
    public void setDiarios() { //layout layout_diarios
        if (pg_home_loaded) {
            if (!pg_diarios_loaded) {
                html.loadUrl(url + pg_diarios);
                showProgressbar();
                hideButtons();
            } else {
                applyBehavior();
                showButtons();
                dismissSnackbar();
                dismissProgressbar();
                dismissErrorConnection();
                new Thread() {
                    @Override
                    public void run() {
                        recyclerViewDiarios = (RecyclerView) findViewById(R.id.recycler_diarios);
                        RecyclerView.LayoutManager layout = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);

                        runOnUiThread(() -> {
                            AdapterDiarios adapter = new AdapterDiarios(diarios, getBaseContext());

                            recyclerViewDiarios.setAdapter(adapter);
                            recyclerViewDiarios.setLayoutManager(layout);
                            recyclerViewDiarios.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

                            adapter.setOnExpandListener(position -> {
                                RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getApplicationContext()) {
                                    @Override
                                    protected int getVerticalSnapPreference() {
                                        return LinearSmoothScroller.SNAP_TO_ANY;
                                    }
                                };
                                if (position != 0) {
                                    smoothScroller.setTargetPosition(position);
                                    layout.startSmoothScroll(smoothScroller);
                                }
                            });

                            changePageColor();

                            fab_expand.setOnClickListener(v -> {
                                adapter.toggleAll();
                                fab_isOpen = true;
                                clickButtons(null);
                            });

                            fab_data.setOnClickListener(v -> {
                                fab_isOpen = true;
                                clickButtons(null);

                                View theView = inflater.inflate(R.layout.dialog_date_picker, null);

                                final NumberPicker year = (NumberPicker) theView.findViewById(R.id.year_picker);
                                year.setMinValue(0);
                                year.setMaxValue(data_diarios.length - 1);
                                year.setValue(data_position_diarios);
                                year.setDisplayedValues(data_diarios);
                                year.setWrapSelectorWheel(false);

                                NumberPicker periodo = (NumberPicker) theView.findViewById(R.id.periodo_picker);
                                periodo.setVisibility(View.GONE);

                                TextView slash = (TextView) theView.findViewById(R.id.slash);
                                slash.setVisibility(View.GONE);

                                new AlertDialog.Builder(MainActivity.this).setView(theView)
                                        .setCustomTitle(customAlertTitle(R.drawable.ic_date_range_black_24dp, R.string.dialog_date_change, R.color.orange_500))
                                        .setPositiveButton(R.string.dialog_confirm, (dialog, which) -> {
                                            showProgressDialog();
                                            data_position_diarios = year.getValue();

                                            Log.v("Ano selecionado", String.valueOf(data_diarios[data_position_diarios]));
                                            html.loadUrl(url + pg_diarios);
                                            scriptDiario = "javascript: var option = document.getElementsByTagName('option'); option["
                                                    + (data_position_diarios + 1) + "].selected = true; document.forms['frmConsultar'].submit();";
                                            Log.i("SCRIPT", "" + scriptDiario);
                                        }).setNegativeButton(R.string.dialog_cancel, null)
                                        .show();
                            });
                        });
                    }
                }.start();
                getSupportActionBar().setTitle(getResources().getString(R.string.title_diarios) + " ー " + data_diarios[data_position_diarios]); //mostra o ano no título
            }
        } else {
            showErrorConnection();
        }
    }

    @AddTrace(name = "setBoletim")
    public void setBoletim() { //layout layout_boletim
        if (pg_home_loaded) {
            if (!pg_boletim_loaded) {
                html.loadUrl(url + pg_boletim);
                showProgressbar();
                hideButtons();
            } else {
                applyBehavior();
                showButtons();
                dismissSnackbar();
                dismissProgressbar();
                dismissErrorConnection();
                new Thread() {
                    @Override
                    public void run() {
                        recyclerViewBoletim = (RecyclerView) findViewById(R.id.recycler_boletim);
                        RecyclerView.LayoutManager layout = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);

                        runOnUiThread(() -> {
                            AdapterBoletim adapter = new AdapterBoletim(boletim, getBaseContext());

                            recyclerViewBoletim.setAdapter(adapter);
                            recyclerViewBoletim.setLayoutManager(layout);
                            recyclerViewBoletim.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

                            adapter.setOnExpandListener(position -> {
                                RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getApplicationContext()) {
                                    @Override
                                    protected int getVerticalSnapPreference() {
                                        return LinearSmoothScroller.SNAP_TO_ANY;
                                    }
                                };
                                if (position != 0) {
                                    smoothScroller.setTargetPosition(position);
                                    layout.startSmoothScroll(smoothScroller);
                                }
                            });

                            changePageColor();

                            fab_expand.setOnClickListener(v -> {
                                adapter.toggleAll();
                                fab_isOpen = true;
                                clickButtons(null);
                            });

                            fab_data.setOnClickListener(v -> {

                                fab_isOpen = true;
                                clickButtons(null);

                                View theView = inflater.inflate(R.layout.dialog_date_picker, null);

                                final NumberPicker year = (NumberPicker) theView.findViewById(R.id.year_picker);
                                year.setMinValue(0);
                                year.setMaxValue(data_boletim.length - 1);
                                year.setValue(data_position_boletim);
                                year.setDisplayedValues(data_boletim);
                                year.setWrapSelectorWheel(false);

                                final NumberPicker periodo = (NumberPicker) theView.findViewById(R.id.periodo_picker);
                                periodo.setMinValue(0);
                                periodo.setMaxValue(periodo_boletim.length - 1);
                                periodo.setValue(periodo_position_boletim);
                                periodo.setDisplayedValues(periodo_boletim);
                                periodo.setWrapSelectorWheel(false);

                                new AlertDialog.Builder(MainActivity.this).setView(theView)
                                        .setCustomTitle(customAlertTitle(R.drawable.ic_date_range_black_24dp, R.string.dialog_date_change, R.color.teal_400))
                                        .setPositiveButton(R.string.dialog_confirm, (dialog, which) -> {
                                            showProgressDialog();
                                            data_position_boletim = year.getValue();
                                            periodo_position_boletim = periodo.getValue();

                                            Log.v("Ano selecionado", String.valueOf(data_boletim[data_position_boletim]));
                                            Log.v("Periodo selecionado", String.valueOf(periodo_boletim[periodo_position_boletim]));

                                            if (data_position_boletim == Integer.parseInt(data_boletim[0])) {
                                                html.loadUrl(url + pg_boletim);
                                            } else {
                                                html.loadUrl(url + pg_boletim + "&COD_MATRICULA=-1&cmbanos=" + data_boletim[data_position_boletim]
                                                        + "&cmbperiodos=" + periodo_boletim[periodo_position_boletim] + "&Exibir+Boletim");
                                            }
                                        }).setNegativeButton(R.string.dialog_cancel, null)
                                        .show();//
                            });
                        });
                    }
                }.start();
                getSupportActionBar().setTitle(getResources().getString(R.string.title_boletim) + " ー " + data_boletim[data_position_boletim] + " / "
                        + periodo_boletim[periodo_position_boletim]); //mostra o ano no título
            }
        } else {
            showErrorConnection();
        }
    }

    @AddTrace(name = "setHorario")
    public void setHorario() { // layout layout_horario
        if (pg_home_loaded) {
            if (!pg_horario_loaded) {
                html.loadUrl(url + pg_horario);
                showProgressbar();
                hideButtons();
            } else {
                applyBehavior();
                showButtons();
                dismissSnackbar();
                dismissProgressbar();
                dismissErrorConnection();
                new Thread() {
                    @Override
                    public void run() {

                        recyclerViewHorario = (RecyclerView) findViewById(R.id.recycler_horario);
                        RecyclerView.LayoutManager layout = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);

                        AdapterHorario adapter = new AdapterHorario(horario, getBaseContext());

                        runOnUiThread(() -> {
                            recyclerViewHorario.setAdapter(adapter);
                            recyclerViewHorario.setLayoutManager(layout);

                            adapter.setOnExpandListener(position -> {
                                RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getApplicationContext()) {
                                    @Override
                                    protected int getVerticalSnapPreference() {
                                        return LinearSmoothScroller.SNAP_TO_ANY;
                                    }
                                };
                                if (position != 0) {
                                    smoothScroller.setTargetPosition(position);
                                    layout.startSmoothScroll(smoothScroller);
                                }
                            });

                            changePageColor();

                            fab_expand.setOnClickListener(v -> {
                                adapter.toggleAll();
                                fab_isOpen = true;
                                clickButtons(null);
                            });

                            fab_data.setOnClickListener(v -> {

                                fab_isOpen = true;
                                clickButtons(null);

                                View theView = inflater.inflate(R.layout.dialog_date_picker, null);

                                final NumberPicker year = (NumberPicker) theView.findViewById(R.id.year_picker);
                                year.setMinValue(0);
                                year.setMaxValue(data_horario.length - 1);
                                year.setValue(data_position_horario);
                                year.setDisplayedValues(data_horario);
                                year.setWrapSelectorWheel(false);

                                final NumberPicker periodo = (NumberPicker) theView.findViewById(R.id.periodo_picker);
                                periodo.setMinValue(0);
                                periodo.setMaxValue(periodo_horario.length - 1);
                                periodo.setValue(periodo_position_horario);
                                periodo.setDisplayedValues(periodo_horario);
                                periodo.setWrapSelectorWheel(false);

                                new AlertDialog.Builder(MainActivity.this).setView(theView)
                                        .setCustomTitle(customAlertTitle(R.drawable.ic_date_range_black_24dp, R.string.dialog_date_change, R.color.blue_400))
                                        .setPositiveButton(R.string.dialog_confirm, (dialog, which) -> {
                                            showProgressDialog();
                                            data_position_horario = year.getValue();
                                            periodo_position_horario = periodo.getValue();

                                            Log.v("Ano selecionado", String.valueOf(data_horario[data_position_horario]));
                                            Log.v("Periodo selecionado", String.valueOf(periodo_horario[periodo_position_horario]));

                                            if (data_position_horario == Integer.parseInt(data_horario[0])) {
                                                html.loadUrl(url + pg_horario);
                                            } else {
                                                html.loadUrl(url + pg_horario + "&COD_MATRICULA=-1&cmbanos=" + data_horario[data_position_horario]
                                                        + "&cmbperiodos=" + periodo_horario[periodo_position_horario] + "&Exibir=OK");
                                            }
                                        }).setNegativeButton(R.string.dialog_cancel, null)
                                        .show();
                            });
                        });
                    }
                }.start();
                getSupportActionBar().setTitle(getResources().getString(R.string.title_horario) + " ー " + data_horario[data_position_horario] + " / "
                        + periodo_horario[periodo_position_horario]); //mostra o ano no título
            }
        } else {
            showErrorConnection();
        }
    }

    @AddTrace(name = "setMaterial")
    public void setMaterial() { //layout layout_home
        if ((!pg_material_loaded)) {
            html.loadUrl(url + pg_material);
            //showErrorConnection();
        } else {


            new Thread() {
                @Override
                public void run() {


                    runOnUiThread(() -> {


                    });
                }
            }.start();
        }
    }

    @Override
    public void OnGuideClick(int position) {
        switch (position) {
            case 0:
                clickDiarios();
                break;

            case 1:
                clickBoletim();
                break;

            case 2:
                clickHorario();
                break;

            case 3:
                clickMateriais();
                break;

            case 4:
                clickCalendario();
                break;

            case 5:
                clickDocumentos();
                break;

            case 6:
                clickBugReport();
                break;

            case 7:
                clickSug();
                break;

            case 8:
                clickShareApp();
                break;
        }
    }

    private class MyJavaScriptInterface { //pega t.odo conteúdo das páginas e carrega nos views
        @JavascriptInterface
        @AddTrace(name = "handleHome")
        public void handleHome(String html_p) {

            Document homePage = Jsoup.parse(html_p);
            Element drawer_msg = homePage.select("td.titulo:eq(1)").first();

            if (drawer_msg.text().contains("dia")) {
                home_msg = getResources().getString(R.string.home_welcome_message_dia);
            } else if (drawer_msg.text().contains("tarde")) {
                home_msg = getResources().getString(R.string.home_welcome_message_tarde);
            } else if (drawer_msg.text().contains("noite")) {
                home_msg = getResources().getString(R.string.home_welcome_message_noite);
            }

            nome = drawer_msg.text().substring(drawer_msg.text().lastIndexOf(",") + 2, drawer_msg.text().indexOf(" !"));

            runOnUiThread(() -> {
                SharedPreferences.Editor editor = login_info.edit();
                editor.putString("nome", nome);
                editor.apply();

                pg_home_loaded = true;

                setNavDrawer();

                clickHome();
                dismissProgressDialog();
                dismissProgressbar();

                autoLoadPages();

                try {
                    PackageInfo pInfo = (getPackageManager().getPackageInfo(getPackageName(), 0));
                    String version = pInfo.versionName;
                    getWebUpdate(version, false);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            });
        }

        @JavascriptInterface
        @AddTrace(name = "handleBoletim")
        public void handleBoletim(String html_p) {

            new Thread() {
                @Override
                public void run() {
                    try {
                        String[][] trtd_boletim;
                        Document homeBoletim = Jsoup.parse(html_p);
                        bugBoletim = homeBoletim.outerHtml();

                        final Element table_boletim = homeBoletim.select("table").get(6);
                        Element table_notas = table_boletim.select("table").get(7);

                        Elements tables = table_notas.children();

                        boletim = new ArrayList<>();

                        for (Element table : tables) {
                            Elements trs = table.select("tr");
                            trtd_boletim = new String[trs.size()][];
                            for (int i = 2; i < trs.size(); i++) {
                                Elements tds = trs.get(i).select("td");
                                trtd_boletim[i] = new String[tds.size()];
                                for (int j = 0; j < tds.size(); j++) {
                                    if (tds.get(j).text().equals("")) {
                                        trtd_boletim[i][j] = "-";
                                    } else {
                                        trtd_boletim[i][j] = tds.get(j).text();
                                    }
                                }
                                boletim.add(new Boletim(trtd_boletim[i][0], trtd_boletim[i][3], trtd_boletim[i][5], trtd_boletim[i][6], trtd_boletim[i][7],
                                        trtd_boletim[i][9], trtd_boletim[i][10], trtd_boletim[i][11], trtd_boletim[i][12], trtd_boletim[i][14]));
                            }
                        }

                        Collections.sort(boletim, (b1, b2) -> b1.getMateria().compareTo(b2.getMateria()));

                        Document ano = Jsoup.parse(homeBoletim.select("#cmbanos").first().toString());
                        Elements options_ano = ano.select("option");

                        data_boletim = new String[options_ano.size()];

                        for (int i = 0; i < options_ano.size(); i++) {
                            data_boletim[i] = options_ano.get(i).text();
                        }

                        Document periodo = Jsoup.parse(homeBoletim.select("#cmbperiodos").first().toString());
                        Elements options_periodo = periodo.select("option");

                        periodo_boletim = new String[options_periodo.size()];

                        for (int i = 0; i < options_periodo.size(); i++) {
                            periodo_boletim[i] = options_periodo.get(i).text();
                        }

                        runOnUiThread(() -> {
                            pg_boletim_loaded = true;
                            if (navigation.getSelectedItemId() == R.id.navigation_boletim) {
                                clickBoletim();
                            }
                            dismissProgressDialog();
                            dismissProgressbar();

                            autoLoadPages();
                        });
                    } catch (Exception ignored) {
                        Log.i("Boletim", "Trocou de layout sem terminar de carregar");
                    }
                }
            }.start();
        }

        @JavascriptInterface
        @AddTrace(name = "handleDiarios")
        public void handleDiarios(String html_p) {

            new Thread() {
                @Override
                public void run() {
                    try {
                        Document homeDiarios = Jsoup.parse(html_p);
                        bugDiarios = homeDiarios.outerHtml();

                        Elements table_diarios = homeDiarios.getElementsByTag("tbody").eq(12);
                        int numMaterias = table_diarios.select("table.conteudoTexto").size();
                        Element nxtElem = null;

                        diarios = new ArrayList<>();

                        for (int y = 0; y < numMaterias; y++) {

                            etapas = new ArrayList<>();

                            if (table_diarios.select("table.conteudoTexto").eq(y).parents().eq(0).parents().eq(0).next().eq(0) != null) {
                                nxtElem = table_diarios.select("table.conteudoTexto").eq(y).parents().eq(0).parents().eq(0).next().eq(0).first();
                            }
                            String nomeMateria = table_diarios.select("table.conteudoTexto").eq(y).parents().eq(0).parents().eq(0).first().child(0).text();
                            nomeMateria = nomeMateria.substring(nomeMateria.indexOf("-") + 2, nomeMateria.indexOf("("));
                            nomeMateria = nomeMateria.substring(nomeMateria.indexOf("-") + 2);
                            String aux;

                            if (nxtElem != null) {
                                aux = nxtElem.child(0).child(0).ownText();
                            } else {
                                aux = "null";
                            }

                            while (aux.contains("Etapa")) {
                                if (aux.equals("1a. Etapa") || aux.equals("1ª Etapa")) {
                                    aux = getResources().getString(R.string.diarios_PrimeiraEtapa);
                                } else if (aux.equals("1a Reavaliação da 1a Etapa") || aux.equals("1ª Reavaliação da 1ª Etapa")) {
                                    aux = getResources().getString(R.string.diarios_RP1_PrimeiraEtapa);
                                } else if (aux.equals("2a Reavaliação da 1a Etapa") || aux.equals("2ª Reavaliação da 1ª Etapa")) {
                                    aux = getResources().getString(R.string.diarios_RP2_PrimeiraEtapa);
                                } else if (aux.equals("2a. Etapa") || aux.equals("2ª Etapa")) {
                                    aux = getResources().getString(R.string.diarios_SegundaEtapa);
                                } else if (aux.equals("1a Reavaliação da 2a Etapa") || aux.equals("1ª Reavaliação da 2ª Etapa")) {
                                    aux = getResources().getString(R.string.diarios_RP1_SegundaEtapa);
                                } else if (aux.equals("2a Reavaliação da 2a Etapa") || aux.equals("2ª Reavaliação da 2ª Etapa")) {
                                    aux = getResources().getString(R.string.diarios_RP2_SegundaEtapa);
                                }

                                Element tabelaNotas = nxtElem.child(0).child(1).child(0);
                                Elements notasLinhas = tabelaNotas.getElementsByClass("conteudoTexto");

                                trabalhos = new ArrayList<>();

                                for (int i = 0; i < notasLinhas.size(); i++) {
                                    String data = notasLinhas.eq(i).first().child(1).text().substring(0, 10);
                                    String tipo = getResources().getString(R.string.sigla_Avaliacao);
                                    int tint = getResources().getColor(R.color.orange_A700);
                                    if (notasLinhas.eq(i).first().child(1).text().contains("Prova")) {
                                        tint = getResources().getColor(R.color.orange_A400);
                                        tipo = getResources().getString(R.string.sigla_Prova);
                                    } else if (notasLinhas.eq(i).first().child(1).text().contains("Trabalho")) {
                                        tint = getResources().getColor(R.color.amber_A400);
                                        tipo = getResources().getString(R.string.sigla_Trabalho);
                                    } else if (notasLinhas.eq(i).first().child(1).text().contains("Qualitativa")) {
                                        tint = getResources().getColor(R.color.yellow_A400);
                                        tipo = getResources().getString(R.string.sigla_Qualitativa);
                                    }

                                    String caps = trim(trim1(notasLinhas.eq(i).first().child(1).text()));
                                    String nome = data + " ー " + caps.substring(1, 2).toUpperCase() + caps.substring(2);
                                    String peso = trim(notasLinhas.eq(i).first().child(2).text());
                                    String max = trim(notasLinhas.eq(i).first().child(3).text());
                                    String nota = trim(notasLinhas.eq(i).first().child(4).text());

                                    if (nota.equals("")) {
                                        nota = " -";
                                    }
                                    trabalhos.add(new Trabalho(nome, peso, max, nota, tipo, tint));
                                }

                                nxtElem = nxtElem.nextElementSibling();

                                etapas.add(new Etapa(aux, trabalhos));

                                if (nxtElem != null) {
                                    aux = nxtElem.child(0).child(0).text();
                                } else {
                                    aux = "null";
                                }
                            }
                            diarios.add(new Diarios(nomeMateria, etapas, getApplicationContext()));
                        }

                        Collections.sort(diarios, (d1, d2) -> d1.getNomeMateria().compareTo(d2.getNomeMateria()));

                        Elements options = homeDiarios.getElementsByTag("option");

                        data_diarios = new String[options.size() - 1];

                        for (int i = 0; i < options.size() - 1; i++) {
                            data_diarios[i] = options.get(i + 1).text();
                        }

                        runOnUiThread(() -> {
                            pg_diarios_loaded = true;
                            if (navigation.getSelectedItemId() == R.id.navigation_diarios) {
                                clickDiarios();
                            }
                            dismissProgressDialog();
                            dismissProgressbar();

                            autoLoadPages();
                        });
                    } catch (Exception ignored) {
                        Log.i("Diarios", "Trocou de layout sem terminar de carregar");
                    }
                }
            }.start();
        }

        @JavascriptInterface
        @AddTrace(name = "handleHorario")
        public void handleHorario(String html_p) {

            new Thread() {
                @Override
                public void run() {
                    try {
                        String[][] trtd_horario = null;
                        String[] code = null;
                        Document homeHorario = Jsoup.parse(html_p);
                        bugHorario = homeHorario.outerHtml();

                        Element table_horario = homeHorario.select("table").eq(11).first();
                        Element table_codes = homeHorario.select("table").eq(12).first();
                        Elements codes = table_codes.children();

                        horario = new ArrayList<>();

                        for (Element table : codes) {
                            Elements trs = table.select("tr");
                            code = new String[trs.size()];
                            for (int i = 0; i < trs.size(); i++) {
                                code[i] = trs.get(i).text();
                            }
                        }

                        Elements tables = table_horario.children();

                        for (Element table : tables) {
                            Elements trs = table.select("tr");
                            trtd_horario = new String[trs.size()][]; //pega total de colunas

                            for (int i = 0; i < trs.size(); i++) {
                                Elements tds = trs.get(i).select("td");
                                trtd_horario[i] = new String[tds.size()]; // pega total de linhas

                                for (int j = 0; j < tds.size(); j++) {
                                    trtd_horario[i][j] = tds.get(j).text();

                                    for (int k = 1; k < code.length; k++) {
                                        String sub = code[k].substring(0, code[k].indexOf("-") + 1);
                                        sub = sub.substring(0, sub.lastIndexOf(" ") + 1);
                                        String recebe = code[k].substring(code[k].indexOf("-"));
                                        recebe = recebe.substring(recebe.indexOf("-"));
                                        recebe = recebe.substring(recebe.indexOf("-") + 2);
                                        recebe = recebe.substring(recebe.indexOf("-") + 2, recebe.lastIndexOf("-"));
                                        if ((trtd_horario[i][j]).contains(sub)) {
                                            trtd_horario[i][j] = recebe;
                                        }
                                        if ((trtd_horario[i][j]).contains("~")) {
                                            trtd_horario[i][j] = trtd_horario[i][j].substring(0, trtd_horario[i][j].indexOf("~"));
                                        }
                                        if (((trtd_horario[i][j]).contains("2ª-FEIRA"))) {
                                            trtd_horario[i][j] = getResources().getString(R.string.day_monday);
                                        } else if (((trtd_horario[i][j]).contains("3ª-FEIRA"))) {
                                            trtd_horario[i][j] = getResources().getString(R.string.day_tuesday);
                                        } else if (((trtd_horario[i][j]).contains("4ª-FEIRA"))) {
                                            trtd_horario[i][j] = getResources().getString(R.string.day_wednesday);
                                        } else if (((trtd_horario[i][j]).contains("5ª-FEIRA"))) {
                                            trtd_horario[i][j] = getResources().getString(R.string.day_thursday);
                                        } else if (((trtd_horario[i][j]).contains("6ª-FEIRA"))) {
                                            trtd_horario[i][j] = getResources().getString(R.string.day_friday);
                                        }
                                    }
                                }
                            }
                        }

                        for (int i = 1; i <= 5; i++) {
                            materias = new ArrayList<>();

                            for (int j = 1; j < trtd_horario.length; j++) {
                                if (!trtd_horario[j][i].equals("")) {
                                    materias.add(new Materia(trtd_horario[j][0], trtd_horario[j][i]));
                                }
                            }
                            horario.add(new Horario(trtd_horario[0][i], materias));
                        }

                        Document ano = Jsoup.parse(homeHorario.select("#cmbanos").first().toString());
                        Elements options_ano = ano.select("option");

                        data_horario = new String[options_ano.size()];

                        for (int i = 0; i < options_ano.size(); i++) {
                            data_horario[i] = options_ano.get(i).text();
                        }

                        Document periodo = Jsoup.parse(homeHorario.select("#cmbperiodos").first().toString());
                        Elements options_periodo = periodo.select("option");

                        periodo_horario = new String[options_periodo.size()];

                        for (int i = 0; i < options_periodo.size(); i++) {
                            periodo_horario[i] = options_periodo.get(i).text();
                        }

                        runOnUiThread(() -> {
                            pg_horario_loaded = true;
                            if (navigation.getSelectedItemId() == R.id.navigation_horario) {
                                clickHorario();
                            }
                            dismissProgressDialog();
                            dismissProgressbar();

                            autoLoadPages();
                        });
                    } catch (Exception ignored) {
                        Log.i("Horario", "Trocou de layout sem terminar de carregar");
                    }
                }
            }.start();
        }

        @JavascriptInterface
        @AddTrace(name = "handleMaterial")
        public void handleMaterial(String html_p) {

            new Thread() {
                @Override
                public void run() {


                    runOnUiThread(() -> {


                    });
                }
            }.start();
        }
    }

    public class CustomWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        @AddTrace(name = "onRecivedError")
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            html.stopLoading();

            if (isLoginPage) {
                progressBar_login.setVisibility(View.GONE);
            } else {
                dismissProgressDialog();
                dismissProgressbar();
                showErrorConnection();
                hideButtons();
            }

            if (isConnected(getApplicationContext())) {
                if (isLoginPage) {
                    showSnackBar(loginLayout, getResources().getString(R.string.text_no_connection), false);
                }
                Crashlytics.log("Erro http: " + error.getDescription());
            } else {
                if (isLoginPage) {
                    showSnackBar(loginLayout, getResources().getString(R.string.text_no_connection), true);
                } else {
                    showSnackBar(buttons_layout, getResources().getString(R.string.text_no_connection), true);
                }
            }
            super.onReceivedError(view, request, error);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        @AddTrace(name = "onRecivedHttpError")
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            if (!errorResponse.getReasonPhrase().equals("Not Found")) { // ignora o erro not found
                html.stopLoading();

                if (isLoginPage) {
                    progressBar_login.setVisibility(View.GONE);
                } else {
                    dismissProgressDialog();
                    dismissProgressbar();
                    showErrorConnection();
                    hideButtons();
                }
                //html.loadUrl("");
                //timeout = false;

                if (isConnected(getApplicationContext())) {
                    if (isLoginPage) {
                        showSnackBar(loginLayout, getResources().getString(R.string.text_no_connection), false);
                    }
                    Crashlytics.log("Erro http: " + errorResponse);
                } else {
                    if (isLoginPage) {
                        showSnackBar(loginLayout, getResources().getString(R.string.text_no_connection), true);
                    } else {
                        showSnackBar(buttons_layout, getResources().getString(R.string.text_no_connection), true);
                    }
                }
            }
        }

        @Override
        @AddTrace(name = "onPageFinished")
        public void onPageFinished(WebView view, String url_i) { //Chama as funções ao terminar de carregar uma página
            if (isConnected(getApplicationContext()) && html.getUrl() != null) {
                if (html.getUrl().equals(url + pg_login)) {
                    html.loadUrl("javascript:var uselessvar = document.getElementById('txtLogin').value='" + matricula + "';");
                    html.loadUrl("javascript:var uselessvar = document.getElementById('txtSenha').value='" + password + "';");
                    html.loadUrl("javascript:document.getElementById('btnOk').click();");
                } else if (html.getUrl().equals(url + pg_home)) {
                    html.loadUrl("javascript:window.HtmlHandler.handleHome" + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                    if (isLoginPage) {
                        //drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                        //navigation.setVisibility(View.VISIBLE);
                        SharedPreferences.Editor editor = login_info.edit();
                        editor.putString("password", password);
                        editor.putString("matricula", matricula);
                        editor.apply();
                        isLoginPage = false;
                    }
                } else if (html.getUrl().contains(url + pg_boletim)) {
                    html.loadUrl("javascript:window.HtmlHandler.handleBoletim" + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                } else if (html.getUrl().equals(url + pg_diarios)) {
                    //scriptDiario = "" + scriptDiario;
                    if (scriptDiario.contains("javascript:")) {
                        Log.i("SCRIPT", "Ok");
                        html.loadUrl(scriptDiario);
                        scriptDiario = "";
                    } else {
                        html.loadUrl("javascript:window.HtmlHandler.handleDiarios" + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                    }
                } else if (html.getUrl().contains(url + pg_horario)) {
                    html.loadUrl("javascript:window.HtmlHandler.handleHorario" + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");

                } else if (html.getUrl().contains(url + pg_material)) {
                    html.loadUrl("javascript:window.HtmlHandler.handleMaterial" + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                } else if (html.getUrl().contains(url + pg_change_password)) {
                    change_password = !change_password;
                    if (change_password) {
                        html.loadUrl("javascript:var uselessvar = document.getElementById('senha0').value='" + password + "';");
                        html.loadUrl("javascript:var uselessvar = document.getElementById('senha1').value='" + new_password + "';");
                        html.loadUrl("javascript:var uselessvar = document.getElementById('senha2').value='" + new_password + "';");
                        html.loadUrl("javascript:document.getElementById('btnConfirmar').click();");
                        SharedPreferences.Editor editor = login_info.edit();
                        password = new_password;
                        editor.putString("password", password);
                        editor.apply();
                        dismissProgressDialog();
                        new AlertDialog.Builder(MainActivity.this)
                                .setCustomTitle(customAlertTitle(R.drawable.ic_check_black_24dp, R.string.success_title, R.color.green_500))
                                .setMessage(R.string.passchange_txt_success_message)
                                .setPositiveButton(R.string.dialog_close, null)
                                .show();
                    }
                } else if (html.getUrl().equals(url + pg_erro)) {
                    if (isLoginPage) {
                        showSnackBar(loginLayout, getResources().getString(R.string.text_invalid_login), false);
                        progressBar_login.setVisibility(View.GONE);
                    } else {
                        dismissProgressDialog();
                        logOut();
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_expired_login), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    dismissProgressDialog();
                    dismissProgressbar();
                    if (isLoginPage) {
                        showSnackBar(loginLayout, getResources().getString(R.string.text_connection_error), false);
                    } else {
                        showSnackBar(buttons_layout, getResources().getString(R.string.text_connection_error), false);
                    }
                }
            }
        }
    }

    public void testLogin() { //Testa se o login é válido
        if (matricula.equals("") || password.equals("")) {
            isLoginPage = true;
            logIn();
        } else {
            showProgressDialog();
            isLoginPage = false;
            html.loadUrl(url + pg_login);
        }
    }

    public void setUp() { //Inicializa as variáveis necessárias
        //showProgressDialog();
        updateDefaultValues();

        html.setWebViewClient(new CustomWebViewClient());
        html.addJavascriptInterface(new MyJavaScriptInterface(), "HtmlHandler");

        login_info = getSharedPreferences("login_info", 0);
        matricula = login_info.getString("matricula", "");
        password = login_info.getString("password", "");
        nome = login_info.getString("nome", "");

        pg_diarios_loaded = false;
        pg_boletim_loaded = false;
        pg_horario_loaded = false;
        pg_home_loaded = false;
        data_position_boletim = 0;
        data_position_horario = 0;
        data_position_diarios = 0;
        periodo_position_horario = 0;
        periodo_position_boletim = 0;
        fab_isOpen = false;
        change_password = false;
        scriptDiario = "";
        bugBoletim = "";
        bugDiarios = "";
        bugHorario = "";

        hideButtons();
        setNavDrawer();
        setGuide();
        setNavigationTransparent();
    }

    protected void setNavigationTransparent() { //Configura os botões de navegação do Android para transparente
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    protected void setGuide() { //Configura os botões da página Home
        guide = new ArrayList<>();
        guide.add(new Guide(getResources().getString(R.string.title_diarios), getResources().getString(R.string.home_diarios_description), R.drawable.ic_newspaper, R.color.orange_500));
        guide.add(new Guide(getResources().getString(R.string.title_boletim), getResources().getString(R.string.home_boletim_description), R.drawable.ic_list, R.color.teal_500));
        guide.add(new Guide(getResources().getString(R.string.title_horario), getResources().getString(R.string.home_horario_description), R.drawable.ic_access_alarm_black_24dp, R.color.blue_500));

        guide.add(new Guide(getResources().getString(R.string.title_materiais), getResources().getString(R.string.home_materiais_description), R.drawable.ic_closed_diary, R.color.cyan_500));
        guide.add(new Guide(getResources().getString(R.string.title_calendario), getResources().getString(R.string.home_calendario_description), R.drawable.ic_event_black_24dp, R.color.dark_purple_500));
        guide.add(new Guide(getResources().getString(R.string.title_documentos), getResources().getString(R.string.home_documentos_description), R.drawable.ic_check_form, R.color.brown_500));

        guide.add(new Guide(getResources().getString(R.string.email_assunto_bug), getResources().getString(R.string.home_bugreport_description), R.drawable.ic_bug_report_black_24dp, R.color.green_500));
        guide.add(new Guide(getResources().getString(R.string.email_assunto_sug), getResources().getString(R.string.home_sug_description), R.drawable.ic_chat_black_24dp, R.color.pink_500));
        guide.add(new Guide(getResources().getString(R.string.menu_share), getResources().getString(R.string.home_share_description), R.drawable.ic_share_black_24dp, R.color.amber_500));
    }

    protected void setNavDrawer() { //Configura o NavigationDrawer lateral
        View header = navigationView.getHeaderView(0);

        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        toggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_UNLOCKED);
        toggle.syncState();
        navigation.setVisibility(View.VISIBLE);

        LinearLayout nav_image = (LinearLayout) header.findViewById(R.id.nav_image);
        TextView user_name_drawer = (TextView) header.findViewById(R.id.msg);
        TextView matricula = (TextView) header.findViewById(R.id.matricula);

        if (!nome.equals("")) {
            user_name_drawer.setText(nome);
        }

        if (!this.matricula.equals("")) {
            matricula.setText(this.matricula);
        }

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

    public void refreshPage(View v) { //Atualiza a página
        showProgressbar();
        dismissErrorConnection();
        if (!pg_home_loaded){
            recreate();
        } else {
            if (navigation.getSelectedItemId() == R.id.navigation_home) {
                html.loadUrl(url + pg_home);
            } else if (navigation.getSelectedItemId() == R.id.navigation_diarios) {
                html.loadUrl(url + pg_diarios);
            } else if (navigation.getSelectedItemId() == R.id.navigation_boletim) {
                html.loadUrl(url + pg_boletim);
            } else if (navigation.getSelectedItemId() == R.id.navigation_horario) {
                html.loadUrl(url + pg_horario);
            }
        }
    }

    protected void passwordCheck(TextInputEditText obj, TextInputEditText pass_atual, TextInputEditText pass_nova,
                                 TextInputEditText pass_nova_confirm, TextInputLayout pass_atual_ly, ImageView img, TextView txt) { //Checa os campos para alterar a senha

        obj.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!pass_atual.getText().toString().equals(password)) {
                    pass_atual_ly.setErrorEnabled(true);
                    pass_atual_ly.setError("teste");
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
                if (pass_atual.getText().toString().equals(password)) {
                    pass_atual_ly.setErrorEnabled(false);
                }
                if (pass_nova.getText().toString().equals(pass_nova_confirm.getText().toString()) && pass_nova.getText().length() >= 8 && pass_atual.getText().toString().equals(password)) {
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

    protected void showErrorConnection(){ //Mostra a página de erro de conexão
        html.stopLoading();
        errorConnectionLayout.setVisibility(View.VISIBLE);
        mainLayout.setVisibility(View.GONE);
    }

    protected void dismissErrorConnection(){ //Esconde a página de erro de conexão
        errorConnectionLayout.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
    }

    protected void showSnackBar(View layout, String message, boolean action){ //Mostra a SnackBar
        snackBar = Snackbar.make(layout, message, Snackbar.LENGTH_INDEFINITE);
        snackBar.setActionTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryLight)));
        if (action){
            snackBar.setAction(R.string.button_wifi, view1 -> {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                snackBar.dismiss();
            });
        }
        snackBar.show();
    }

    protected void dismissSnackbar() { //Esconde a SnackBar
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

    protected void showProgressDialog(){ //Mostra o diálogo de loading
        if (loadingDialog == null || !loadingDialog.isShowing())
            loadingDialog = ProgressDialog.show(MainActivity.this, getResources().getString(R.string.title_loading),
                    getResources().getString(R.string.text_loading), true, false, dialog -> {});
    }

    protected void showButtons() { //Mostra os FloatingActionButtons
        if (fab_action.getAnimation() == null){
            Animation open_linear = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_open_pop);
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

    protected void hideButtons() { //Esconde os FloatingActionButtons
        if (fab_action.getAnimation() != null) {
            Animation close_linear = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_close_pop);
            fab_action.startAnimation(close_linear);
            fab_action.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    fab_action.setAnimation(null);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        }
        if ((fab_data.getAnimation() != null) || (fab_expand.getAnimation() != null)){
            fab_action.getAnimation().setFillAfter(false);
            fab_data.getAnimation().setFillAfter(false);
            fab_expand.getAnimation().setFillAfter(false);
            txt_expand.getAnimation().setFillAfter(false);
            txt_data.getAnimation().setFillAfter(false);
        }
        fab_action.setVisibility(View.INVISIBLE);
        fab_isOpen = false;
    }

    protected void changePageColor(){ //Muda a cor do app dependendo da página
        int colorButtonPrimaryTo = 0;
        int colorButtonSecondaryTo = 0;
        int colorButtonPrimaryFrom = 0;
        int colorButtonSecondaryFrom = 0;
        int colorStatusBarTo = 0;
        int colorStatusBarFrom = 0;
        int colorActionBarTo = 0;
        int colorActionBarFrom = 0;
        int colorTitleTo = 0;
        int colorTitleFrom = 0;
        int colorToolBarTo = 0;
        int colorToolBarFrom = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            colorStatusBarFrom = getWindow().getStatusBarColor();
        }

        if (colorStatusBarFrom == getResources().getColor(R.color.colorPrimaryDark)) {
            colorButtonPrimaryFrom = getResources().getColor(R.color.white);
            colorButtonSecondaryFrom = getResources().getColor(R.color.white);
            colorActionBarFrom = getResources().getColor(R.color.white);
            colorTitleFrom = getResources().getColor(R.color.colorPrimary);
            colorToolBarFrom = getResources().getColor(R.color.colorAccent);
        } else if (colorStatusBarFrom == getResources().getColor(R.color.blue_800)) {
            colorButtonPrimaryFrom = getResources().getColor(R.color.cyan_A700);
            colorButtonSecondaryFrom = getResources().getColor(R.color.cyan_A400);
            colorActionBarFrom = getResources().getColor(R.color.blue_600);
            colorTitleFrom = getResources().getColor(R.color.white);
            colorToolBarFrom = getResources().getColor(R.color.blue_50);
        } else if (colorStatusBarFrom == getResources().getColor(R.color.teal_800)) {
            colorButtonPrimaryFrom = getResources().getColor(R.color.green_A700);
            colorButtonSecondaryFrom = getResources().getColor(R.color.green_A400);
            colorActionBarFrom = getResources().getColor(R.color.teal_600);
            colorTitleFrom = getResources().getColor(R.color.white);
            colorToolBarFrom = getResources().getColor(R.color.teal_50);
        } else if (colorStatusBarFrom == getResources().getColor(R.color.orange_800)) {
            colorButtonPrimaryFrom = getResources().getColor(R.color.yellow_A700);
            colorButtonSecondaryFrom = getResources().getColor(R.color.yellow_A400);
            colorActionBarFrom = getResources().getColor(R.color.orange_600);
            colorTitleFrom = getResources().getColor(R.color.white);
            colorToolBarFrom = getResources().getColor(R.color.deep_orange_50);
        }

        if (navigation.getSelectedItemId() == R.id.navigation_home) {
            colorButtonPrimaryTo = getResources().getColor(R.color.white);
            colorButtonSecondaryTo = getResources().getColor(R.color.white);
            colorStatusBarTo = getResources().getColor(R.color.colorPrimaryDark);
            colorActionBarTo = getResources().getColor(R.color.white);
            colorTitleTo = getResources().getColor(R.color.colorPrimary);
            colorToolBarTo = getResources().getColor(R.color.colorAccent);
            setSystemBarTheme(this, false);
        } else if (navigation.getSelectedItemId() == R.id.navigation_diarios) {
            colorButtonPrimaryTo = getResources().getColor(R.color.yellow_A700);
            colorButtonSecondaryTo = getResources().getColor(R.color.yellow_A400);
            colorStatusBarTo = getResources().getColor(R.color.orange_800);
            colorActionBarTo = getResources().getColor(R.color.orange_600);
            colorTitleTo = getResources().getColor(R.color.white);
            colorToolBarTo = getResources().getColor(R.color.deep_orange_50);
            setSystemBarTheme(this, true);
        } else if (navigation.getSelectedItemId() == R.id.navigation_boletim) {
            colorButtonPrimaryTo = getResources().getColor(R.color.green_A700);
            colorButtonSecondaryTo = getResources().getColor(R.color.green_A400);
            colorStatusBarTo = getResources().getColor(R.color.teal_800);
            colorActionBarTo = getResources().getColor(R.color.teal_600);
            colorTitleTo = getResources().getColor(R.color.white);
            colorToolBarTo = getResources().getColor(R.color.teal_50);
            setSystemBarTheme(this, true);
        } else if (navigation.getSelectedItemId() == R.id.navigation_horario) {
            colorButtonPrimaryTo = getResources().getColor(R.color.cyan_A700);
            colorButtonSecondaryTo = getResources().getColor(R.color.cyan_A400);
            colorStatusBarTo = getResources().getColor(R.color.blue_800);
            colorActionBarTo = getResources().getColor(R.color.blue_600);
            colorTitleTo = getResources().getColor(R.color.white);
            colorToolBarTo = getResources().getColor(R.color.blue_50);
            setSystemBarTheme(this, true);
        }

        changeButtonColorAnim(fab_action, colorButtonPrimaryFrom, colorButtonPrimaryTo);
        changeButtonColorAnim(fab_expand, colorButtonSecondaryFrom, colorButtonSecondaryTo);
        changeButtonColorAnim(fab_data, colorButtonSecondaryFrom, colorButtonSecondaryTo);
        changeStatusBarColor(colorStatusBarFrom, colorStatusBarTo, colorActionBarTo, colorActionBarFrom, colorTitleTo, colorTitleFrom, colorToolBarFrom, colorToolBarTo);
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

    public void clickButtons(View v) { //Animações ao clicar no FloatingActionButton
        Animation open_rotate = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_rotate_fwd);
        Animation close_rotate = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_rotate_bkw);
        Animation open_linear = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_open_pop);
        Animation close_linear = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_close_pop);

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

    protected void changeView(View layout, int titulo) { //Muda de página ao clicar no bottomNavigation
        if (pg_home_loaded || isLoginPage) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(mainLayout, new Fade());
            }
            mainLayout.removeAllViews();
            mainLayout.addView(layout);
            getSupportActionBar().setTitle(titulo);
        } else {
            showErrorConnection();
        }
    }

    protected void applyBehavior(){ //Habilita o actionBar a se esconder ao rolar a página
        CoordinatorLayout.LayoutParams mainLayoutLayoutParams = (CoordinatorLayout.LayoutParams) mainLayout.getLayoutParams();
        mainLayoutLayoutParams.setBehavior(new AppBarLayout.ScrollingViewBehavior());
        mainLayout.requestLayout();
        AppBarLayout.LayoutParams toolbarLayoutParams = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        toolbarLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
    }

    protected void removeBehavior(){ //Desabilita o actionBar a se esconder ao rolar a página
        CoordinatorLayout.LayoutParams mainLayoutLayoutParams = (CoordinatorLayout.LayoutParams) mainLayout.getLayoutParams();
        mainLayoutLayoutParams.setBehavior(null);
        AppBarLayout.LayoutParams toolbarLayoutParams = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        toolbarLayoutParams.setScrollFlags(0);
    }

    protected void showProgressbar(){ //Mostra a progressBar ao carregar a página
        progressBar_Main.setVisibility(View.VISIBLE);
        fab_action.setClickable(false);
        fab_data.setClickable(false);
        fab_expand.setClickable(false);
    }

    protected void dismissProgressbar(){ //Esconde a progressBar ao carregar a página
        progressBar_Main.setVisibility(View.GONE);
        fab_action.setClickable(true);
        fab_data.setClickable(true);
        fab_expand.setClickable(true);
    }

    protected void autoLoadPages(){ //Tenta carregar as páginas em segundo plano
        try{
            if (!pg_diarios_loaded) {
                html.loadUrl(url + pg_diarios);
            } else if (!pg_boletim_loaded) {
                html.loadUrl(url + pg_boletim);
            } else if (!pg_horario_loaded) {
                html.loadUrl(url + pg_horario);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String trim(String string) {
        String x = string;
        x = x.substring(x.indexOf(":"));
        x = x.replace(":", "");
        return x;
    }

    public String trim1(String string) {
        String x = string;
        x = x.substring(x.indexOf(", ") + 2);
        return x;
    }

    @AddTrace(name = "startDownload")
    public void startDownload() {
        try {
            String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
            String fileName = "app-att.apk";
            destination += fileName;
            final Uri uri = Uri.parse("file://" + destination);

            File file = new File(destination);
            file.mkdirs();

            File outputFile = new File(file, "app-att.apk");

            if (outputFile.exists()) {
                outputFile.delete();
            }

            DownloadManager mManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request mRqRequest = new DownloadManager.Request(
                    Uri.parse(linkAtt));
            mRqRequest.setNotificationVisibility(
                    DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
            );
            //mRqRequest.setDescription(getResources().getString(R.string.download_description));
            mRqRequest.setDescription("Qacademico Mobile " + verWeb);
            mRqRequest.setTitle("Qacademico Update");
            mRqRequest.setDestinationUri(uri);
            long downloadId = mManager.enqueue(mRqRequest);

            BroadcastReceiver onComplete = new BroadcastReceiver() {
                public void onReceive(Context ctxt, Intent intent) {
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    install.setDataAndType(uri,
                            mManager.getMimeTypeForDownloadedFile(downloadId));
                    startActivity(install);

                    unregisterReceiver(this);
                    finish();
                }
            };
            //registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_download_start), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_download_fail), Toast.LENGTH_SHORT).show();
        }
    }

    protected void changePassword() {

        if (isConnected(getApplicationContext()) && pg_home_loaded) {

            View theView = inflater.inflate(R.layout.dialog_password_change, null);
            TextInputEditText pass_atual = (TextInputEditText) theView.findViewById(R.id.pass_atual);
            TextInputEditText pass_nova = (TextInputEditText) theView.findViewById(R.id.pass_nova);
            TextInputEditText pass_nova_confirm = (TextInputEditText) theView.findViewById(R.id.pass_nova_confirm);
            TextInputLayout pass_atual_ly = (TextInputLayout) theView.findViewById(R.id.pass_atual_ly);
            ImageView img = (ImageView) theView.findViewById(R.id.pass_img);
            TextView txt = (TextView) theView.findViewById(R.id.pass_txt);

            pass_atual.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!pass_atual.getText().toString().equals(password)) {
                        pass_atual_ly.setErrorEnabled(true);
                        pass_atual_ly.setError("teste");
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (pass_atual.getText().toString().equals(password)) {
                        pass_atual_ly.setErrorEnabled(false);
                    }
                    if (pass_nova.getText().toString().equals(pass_nova_confirm.getText().toString()) && pass_nova.getText().length() >= 8 && pass_atual.getText().toString().equals(password)) {
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
                    .setCustomTitle(customAlertTitle(R.drawable.ic_lock_outline_black_24dp, R.string.menu_password, R.color.colorPrimary))
                    .setPositiveButton(R.string.dialog_confirm, (dialog, which) -> {
                        if (pass_nova.getText().toString().equals(pass_nova_confirm.getText().toString()) && pass_nova.getText().length() >= 8 && pass_atual.getText().toString().equals(password)) {
                            new_password = pass_nova.getText().toString();
                            showProgressDialog();
                            html.loadUrl(url + pg_change_password);
                        } else {
                            new AlertDialog.Builder(this)
                                    .setCustomTitle(customAlertTitle(R.drawable.ic_cancel_black_24dp, R.string.error_title, R.color.red_500))
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

    protected View customAlertTitle(int img, int txt, int color){
        View theTitle = inflater.inflate(R.layout.dialog_title, null);
        ImageView title_img = (ImageView) theTitle.findViewById(R.id.dialog_img);
        TextView title_txt = (TextView) theTitle.findViewById(R.id.dialog_txt);
        LinearLayout title_bckg = (LinearLayout) theTitle.findViewById(R.id.dialog_bckg);
        title_img.setImageResource(img);
        title_bckg.setBackgroundColor(getResources().getColor(color));
        title_txt.setText(txt);
        return theTitle;
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

    protected void sendEmail(){
        if (isConnected(MainActivity.this)) {

            View theView = inflater.inflate(R.layout.dialog_sug, null);
            EditText message = (EditText) theView.findViewById(R.id.email_message);
            RatingBar rating = (RatingBar) theView.findViewById(R.id.ratingBar);

            new AlertDialog.Builder(MainActivity.this).setView(theView)
                    .setCustomTitle(customAlertTitle(R.drawable.ic_chat_black_24dp, R.string.email_assunto_sug, R.color.pink_500))
                    .setPositiveButton(R.string.dialog_confirm, (dialog, which) -> {
                        if (!message.getText().toString().equals("")) {
                            /*BackgroundMail.newBuilder(this)
                                    .withUsername(email_from)
                                    .withPassword(email_from_pwd)
                                    .withMailto(email_to)
                                    .withType(BackgroundMail.TYPE_PLAIN)
                                    .withSubject("QAcadMobile Sugestion")
                                    .withBody(nome + ",\n\n" +message.getText().toString() + "\n\nNota: " + String.valueOf(rating.getRating()))
                                    .withSendingMessage(R.string.email_sending)
                                    .withSendingMessageError(null)
                                    .withSendingMessageSuccess(null)
                                    .withOnSuccessCallback(() -> new AlertDialog.Builder(MainActivity.this)
                                            .setCustomTitle(customAlertTitle(R.drawable.ic_sentiment_very_satisfied_black_24dp, R.string.success_title, R.color.green_500))
                                            .setMessage(R.string.email_success)
                                            .setPositiveButton(R.string.dialog_close, null)
                                            .show())
                                    .withOnFailCallback(() -> new AlertDialog.Builder(MainActivity.this)
                                            .setCustomTitle(customAlertTitle(R.drawable.ic_cancel_black_24dp, R.string.error_title, R.color.red_500))
                                            .setMessage(R.string.email_error)
                                            .setPositiveButton(R.string.dialog_close, null)
                                            .show())
                                    .send();*/
                            emailPattern("QAcadMobile Sugestion", message.getText().toString() + "\n\nNota: " + String.valueOf(rating.getRating()));
                        } else {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setCustomTitle(customAlertTitle(R.drawable.ic_sentiment_neutral_black_24dp, R.string.error_title_oops, R.color.amber_500))
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

    protected void bugReport(){
        if (isConnected(getApplicationContext())) {

            autoLoadPages();

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
                    .setCustomTitle(customAlertTitle(R.drawable.ic_bug_report_black_24dp, R.string.email_assunto_bug, R.color.green_500))
                    .setPositiveButton(R.string.dialog_confirm, (dialog, which) -> {

                        String message_final = "";

                        if (!message.getText().toString().equals("")) {

                            if (check_boletim.isChecked() && !bugBoletim.equals("")){
                                message_final += "\n---------------------------------------------------------------------------------------------------";
                                message_final += "BOLETIM";
                                message_final += "---------------------------------------------------------------------------------------------------\n";
                                message_final += bugBoletim;
                            }

                            if (check_diarios.isChecked() && !bugDiarios.equals("")){
                                message_final += "\n---------------------------------------------------------------------------------------------------";
                                message_final += "DIARIOS";
                                message_final += "---------------------------------------------------------------------------------------------------\n";
                                message_final += bugDiarios;
                            }

                            if (check_horario.isChecked() && !bugBoletim.equals("")){
                                message_final += "\n---------------------------------------------------------------------------------------------------";
                                message_final += "HORARIO";
                                message_final += "---------------------------------------------------------------------------------------------------\n";
                                message_final += bugHorario;
                            }

                            if (!message_final.equals("") || !message.getText().toString().equals("")) {
                                /*BackgroundMail.newBuilder(this)
                                        .withUsername(email_from)
                                        .withPassword(email_from_pwd)
                                        .withMailto(email_to)
                                        .withType(BackgroundMail.TYPE_PLAIN)
                                        .withSubject("QAcadMobile Bug Report")
                                        .withSendingMessage(R.string.email_sending)
                                        .withBody(nome + ",\n\n" + message.getText().toString() + message_final)
                                        .withSendingMessageError(null)
                                        .withSendingMessageSuccess(null)
                                        .withOnSuccessCallback(() -> new AlertDialog.Builder(MainActivity.this)
                                                .setCustomTitle(customAlertTitle(R.drawable.ic_sentiment_very_satisfied_black_24dp, R.string.success_title, R.color.green_500))
                                                .setMessage(R.string.email_success)
                                                .setPositiveButton(R.string.dialog_close, null)
                                                .show())
                                        .withOnFailCallback(() -> new AlertDialog.Builder(MainActivity.this)
                                                .setCustomTitle(customAlertTitle(R.drawable.ic_cancel_black_24dp, R.string.error_title, R.color.red_500))
                                                .setMessage(R.string.email_error)
                                                .setPositiveButton(R.string.dialog_close, null)
                                                .show())
                                        .send();*/
                                emailPattern("QAcadMobile Bug Report", message.getText().toString() + message_final);
                            } else {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setCustomTitle(customAlertTitle(R.drawable.ic_sync_problem_black_24dp, R.string.error_title, R.color.amber_500))
                                        .setMessage(R.string.page_load_empty)
                                        .setPositiveButton(R.string.dialog_close, null)
                                        .show();
                            }
                        } else {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setCustomTitle(customAlertTitle(R.drawable.ic_sentiment_neutral_black_24dp, R.string.error_title_oops, R.color.amber_500))
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
                .withBody(nome + ",\n\n" + message)
                .withSendingMessage(R.string.email_sending)
                .withSendingMessageError(null)
                .withSendingMessageSuccess(null)
                .withOnSuccessCallback(() -> new AlertDialog.Builder(MainActivity.this)
                        .setCustomTitle(customAlertTitle(R.drawable.ic_sentiment_very_satisfied_black_24dp, R.string.success_title, R.color.green_500))
                        .setMessage(R.string.email_success)
                        .setPositiveButton(R.string.dialog_close, null)
                        .show())
                .withOnFailCallback(() -> new AlertDialog.Builder(MainActivity.this)
                        .setCustomTitle(customAlertTitle(R.drawable.ic_cancel_black_24dp, R.string.error_title, R.color.red_500))
                        .setMessage(R.string.email_error)
                        .setPositiveButton(R.string.dialog_close, null)
                        .show())
                .send();
    }

    protected void checkBoxBugReport(GridLayout layout, CheckBox chk, ImageView img, TextView txt){
        layout.setOnClickListener(v -> {
            if (chk.isChecked()){
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
            if (chk.isChecked()){
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
        editor.apply();
        recreate();
    }

    public void logIn() {
        View layout = inflater.inflate(R.layout.layout_login, null);
        changeView(layout, R.string.title_activity_login);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        toggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        toggle.syncState();
        navigation.setVisibility(View.GONE);
        toolbar.setNavigationIcon(R.drawable.ic_lock_outline_black_24dp);

        matricula = "";
        password = "";
        nome = "";

        loginLayout = findViewById(R.id.container_login);
        EditText user_et = (TextInputEditText) findViewById(R.id.user_input_login);
        EditText password_et = (TextInputEditText) findViewById(R.id.password_input_login);
        progressBar_login  = (ProgressBar) findViewById(R.id.login_progressbar);
        Button login_btn = (Button) findViewById(R.id.btn_login);

        login_btn.setOnClickListener(v -> {
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }

            progressBar_login.setVisibility(View.VISIBLE);
            matricula = user_et.getText().toString().toUpperCase();
            password = password_et.getText().toString();
            html.loadUrl(url + pg_login);
            dismissSnackbar();
        });

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
        systemClick = true;
        navigation.setSelectedItemId(R.id.navigation_diarios);
        systemClick = false;
    }

    public void clickBoletim() {
        systemClick = true;
        navigation.setSelectedItemId(R.id.navigation_boletim);
        systemClick = false;
    }

    public void clickHorario() {
        systemClick = true;
        navigation.setSelectedItemId(R.id.navigation_horario);
        systemClick = false;
    }

    public void clickHome(){
        systemClick = true;
        navigation.setSelectedItemId(R.id.navigation_home);
        systemClick = false;
    }

    public void clickMateriais(){
        setMaterial();
    }

    public void clickCalendario(){
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_unavailable), Toast.LENGTH_SHORT).show();
    }

    public void clickDocumentos(){
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_unavailable), Toast.LENGTH_SHORT).show();
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != cm) {
            NetworkInfo info = cm.getActiveNetworkInfo();
            return (info != null && info.isConnected());
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startDownload();
                }
            }
        }
    }

    @Override
    @AddTrace(name = "onActivityResult")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        recreate();
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
