package com.qacademico.qacademico.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.qacademico.qacademico.Activity.Settings.SettingsActivity;
import com.qacademico.qacademico.Class.Boletim;
import com.qacademico.qacademico.Class.Diarios;
import com.qacademico.qacademico.Class.Horario;
import com.qacademico.qacademico.Fragment.BoletimFragment;
import com.qacademico.qacademico.Fragment.DiariosFragment;
import com.qacademico.qacademico.Fragment.HomeFragment;
import com.qacademico.qacademico.Fragment.HorarioFragment;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.Utilities.ChangePassword;
import com.qacademico.qacademico.Utilities.CheckUpdate;
import com.qacademico.qacademico.Utilities.Data;
import com.qacademico.qacademico.Utilities.Design;
import com.qacademico.qacademico.Utilities.SendEmail;
import com.qacademico.qacademico.Utilities.Utils;
import com.qacademico.qacademico.WebView.SingletonWebView;
import java.util.List;
import java.util.Objects;
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
        SingletonWebView.OnPageFinished, SingletonWebView.OnPageStarted, SingletonWebView.OnRecivedError {
    private SharedPreferences login_info;
    LayoutInflater inflater;
    @BindView(R.id.connection) LinearLayout errorConnectionLayout;
    @BindView(R.id.empty) LinearLayout emptyLayout;
    @BindView(R.id.progressbar_main) ProgressBar progressBar_Main;
    @BindView(R.id.progressbar_horizontal) ProgressBar progressBar_Top;
    @BindView(R.id.main_container) ViewGroup mainLayout;
    Dialog loadingDialog;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.fab_expand) public FloatingActionButton fab_expand;
    @BindView(R.id.navigation) BottomNavigationView navigation;
    Snackbar snackBar;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    public SingletonWebView mainWebView = SingletonWebView.getInstance();
    private DiariosFragment diariosFragment = new DiariosFragment();
    private BoletimFragment boletimFragment = new BoletimFragment();
    private HorarioFragment horarioFragment = new HorarioFragment();
    private OnPageUpdated onPageUpdated;
    public List<Diarios> diariosList;
    public List<Boletim> boletimList;
    public List<Horario> horarioList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        loadingDialog = new Dialog(this);

        navigationView.setNavigationItemSelectedListener(this);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setOnNavigationItemReselectedListener(mOnNavigationItemReselectedListener);

        login_info = getSharedPreferences(Utils.LOGIN_INFO, 0);

        mainWebView.data_diarios = Data.getDate(this, Utils.DIARIOS, Data.YEAR);
        mainWebView.data_boletim = Data.getDate(this, Utils.BOLETIM, Data.YEAR);
        mainWebView.periodo_boletim = Data.getDate(this, Utils.BOLETIM, Data.PERIOD);
        mainWebView.data_horario = Data.getDate(this, Utils.HORARIO, Data.YEAR);
        mainWebView.periodo_horario = Data.getDate(this, Utils.HORARIO, Data.PERIOD);

        if(mainWebView.data_diarios != null) {
            diariosList = (List<Diarios>) Data.getList(this, Utils.DIARIOS,
                    mainWebView.data_diarios[0], null);
        } else {
          showErrorConnection();
        }
        if (mainWebView.data_boletim != null && mainWebView.periodo_boletim != null) {
            boletimList = (List<Boletim>) Data.getList(this, Utils.BOLETIM,
                    mainWebView.data_boletim[0], mainWebView.periodo_boletim[0]);
        } else {
            showErrorConnection();
        }
        if (mainWebView.data_horario != null && mainWebView.periodo_horario != null) {
            horarioList = (List<Horario>) Data.getList(this, Utils.HORARIO,
                    mainWebView.data_horario[0], mainWebView.periodo_horario[0]);
        } else {
            showErrorConnection();
        }

        hideExpandBtn();

        configNavDrawer();

        Design.setNavigationTransparent(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        CheckUpdate.checkUpdate(this);

        configWebView();

        testLogin();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {//drawer
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
                    .setCustomTitle(Utils.customAlertTitle(this, R.drawable.ic_exit_to_app_black_24dp, R.string.dialog_quit_title, R.color.exit_dialog))
                    .setMessage(R.string.dialog_quit_msg)
                    .setPositiveButton(R.string.dialog_quit_yes, (dialog, which) -> logOut())
                    .setNegativeButton(R.string.dialog_quit_no, null)
                    .show();
        } else if (id == R.id.nav_password) {
            ChangePassword.changePassword(this);
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
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
                        Design.removeToolbarScrollBehavior(getApplicationContext(), mainLayout, toolbar);
                        invalidateOptionsMenu();
                        return true;

                    case R.id.navigation_diarios:
                        setDiarios();
                        Design.applyToolbarScrollBehavior(getApplicationContext(), mainLayout, toolbar);
                        invalidateOptionsMenu();
                        return true;

                    case R.id.navigation_boletim:
                        setBoletim();
                        Design.applyToolbarScrollBehavior(getApplicationContext(), mainLayout, toolbar);
                        invalidateOptionsMenu();
                        return true;

                    case R.id.navigation_horario:
                        setHorario();
                        Design.applyToolbarScrollBehavior(getApplicationContext(), mainLayout, toolbar);
                        invalidateOptionsMenu();
                        return true;
                }
            }
            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem date = menu.findItem(R.id.action_date);
        MenuItem column = menu.findItem(R.id.action_column);

        if ((navigation.getSelectedItemId() == R.id.navigation_diarios && mainWebView.data_diarios != null)
                || (navigation.getSelectedItemId() == R.id.navigation_boletim && mainWebView.data_boletim != null)
                || (navigation.getSelectedItemId() == R.id.navigation_horario && mainWebView.data_horario != null)) {
            date.setVisible(true);
        } else {
            date.setVisible(false);
        }

        if(navigation.getSelectedItemId() == R.id.navigation_boletim && boletimList != null) {
            column.setVisible(true);
        } else {
            column.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_date) {
            if (navigation.getSelectedItemId() == R.id.navigation_diarios) {
                diariosFragment.openDateDialog();
            } else if (navigation.getSelectedItemId() == R.id.navigation_boletim) {
                boletimFragment.openDateDialog();
            } else if (navigation.getSelectedItemId() == R.id.navigation_horario) {
                horarioFragment.openDateDialog();
            }
            return true;
        } else if (id == R.id.action_column) {
            boletimFragment.show_by_semestre = !boletimFragment.show_by_semestre;
            boletimFragment.changeColumnMode();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setHome() { //layout fragment_home

        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.title_home));
        hideExpandBtn();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment, new HomeFragment(), Utils.HOME);
        fragmentTransaction.commit();

        if(!mainWebView.pg_login_loaded) {
            mainWebView.html.loadUrl(url + pg_login);
        } else if (!mainWebView.pg_home_loaded) {
            mainWebView.html.loadUrl(url + pg_home);
        }
    }

    public void setDiarios() {//layout fragment_diarios

        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.title_diarios));

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment, diariosFragment, Utils.DIARIOS);
        fragmentTransaction.commit();

        if (mainWebView.pg_home_loaded && !mainWebView.pg_diarios_loaded) {
            mainWebView.html.loadUrl(url + pg_diarios);
        } else {
            mainWebView.html.loadUrl(url + pg_home);
        }
    }

    public void setBoletim() { //layout fragment_boletim

        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.title_boletim));
        hideExpandBtn();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment, boletimFragment, Utils.BOLETIM);
        fragmentTransaction.commit();

            if (mainWebView.pg_home_loaded) {
                if (!mainWebView.pg_boletim_loaded) {
                    mainWebView.html.loadUrl(url + pg_boletim);
                } else {
                    getSupportActionBar().setTitle(getResources().getString(R.string.title_boletim)
                            + "・" + mainWebView.data_boletim[mainWebView.data_position_boletim] + " / "
                            + mainWebView.periodo_boletim[mainWebView.periodo_position_boletim]); //mostra o ano no título
                }
            } else {
                mainWebView.html.loadUrl(url + pg_home);
            }

    }

    public void setHorario() { // layout fragment_horario

        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.title_horario));
        hideExpandBtn();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment, horarioFragment, Utils.HORARIO);
        fragmentTransaction.commit();

        if (mainWebView.pg_home_loaded) {
            if (!mainWebView.pg_horario_loaded) {
                mainWebView.html.loadUrl(url + pg_horario);
                } else {
                getSupportActionBar().setTitle(getResources().getString(R.string.title_horario)
                        + "・" + mainWebView.data_horario[mainWebView.data_position_horario] + " / "
                        + mainWebView.periodo_horario[mainWebView.periodo_position_horario]); //mostra o ano no título
            }
        } else {
            mainWebView.html.loadUrl(url + pg_home);
        }
    }

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
        if (login_info.getBoolean(Utils.LOGIN_VALID, false)) {
            setHome();
            Design.removeToolbarScrollBehavior(getApplicationContext(), mainLayout, toolbar);
        } else {
            Intent login =  new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(login);
        }
    }

    protected void configNavDrawer() { //Configura o NavigationDrawer lateral

        View header = navigationView.getHeaderView(0);

        TextView name = (TextView) header.findViewById(R.id.name);
        TextView matricula = (TextView) header.findViewById(R.id.matricula);
        TextView sigla = (TextView) header.findViewById(R.id.sigla);

        String sigla_txt = "";

        if (!login_info.getString(Utils.LOGIN_NAME, "").equals("")) {
            sigla_txt = login_info.getString(Utils.LOGIN_NAME, "").substring(0, 1)
                    + login_info.getString(Utils.LOGIN_NAME, "").substring(
                            login_info.getString(Utils.LOGIN_NAME, "").lastIndexOf(" ") + 1,
                    login_info.getString(Utils.LOGIN_NAME, "").lastIndexOf(" ") + 2);
        }

        name.setText(login_info.getString(Utils.LOGIN_NAME, ""));
        matricula.setText(login_info.getString(Utils.LOGIN_REGISTRATION, ""));
        sigla.setText(sigla_txt);
    }

    @Override
    public void onPageStart(String url_p) {
        Log.i("Singleton", "onStart");

        if ((url_p.equals(url + pg_home))
                || (url_p.equals(url + pg_boletim) && navigation.getSelectedItemId() == R.id.navigation_boletim)
                || (url_p.equals(url + pg_diarios) && navigation.getSelectedItemId() == R.id.navigation_diarios)
                || (url_p.equals(url + pg_horario) && navigation.getSelectedItemId() == R.id.navigation_horario)) {
            showLinearProgressbar();
        }
    }

    @Override
    public void onPageFinish(String url_p, List<?> list) {
        Log.i("Singleton", "onFinish");

        dismissRoundProgressbar();
        dismissLinearProgressbar();
        autoLoadPages();
        invalidateOptionsMenu();

        if (url_p.equals(url + pg_home)) {
            Log.i("onFinish", "loadedHome");
            if (navigation.getSelectedItemId() == R.id.navigation_home) {
                onPageUpdated.onPageUpdate(null);
                Log.i("onFinish", "updatedHome");
            }
            configNavDrawer();
        } else if (url_p.equals(url + pg_boletim) && navigation.getSelectedItemId() == R.id.navigation_boletim) {
            onPageUpdated.onPageUpdate(list);
            Log.i("onFinish", "updatedBoletim");
        } else if (url_p.equals(url + pg_diarios) && navigation.getSelectedItemId() == R.id.navigation_diarios) {
            onPageUpdated.onPageUpdate(list);
            Log.i("onFinish", "updatedDiarios");
        } else if (url_p.equals(url + pg_horario) && navigation.getSelectedItemId() == R.id.navigation_horario) {
            onPageUpdated.onPageUpdate(list);
            Log.i("onFinish", "updatedHorario");
        }
    }

    @Override
    public void onErrorRecived(String error) {
        dismissProgressDialog();
        dismissLinearProgressbar();
        dismissRoundProgressbar();
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
        //mainWebView.html.stopLoading();
        errorConnectionLayout.setVisibility(View.VISIBLE);
        mainLayout.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.GONE);
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

    public void showExpandBtn() { //Mostra os FloatingActionButtons
        if (fab_expand.getAnimation() == null) {
            Animation open_linear = AnimationUtils.loadAnimation(this, R.anim.fab_open_pop);
            fab_expand.startAnimation(open_linear);
        }
        if ((fab_expand.getAnimation() != null)) {
            fab_expand.getAnimation().setFillAfter(true);
        }
        fab_expand.setVisibility(View.VISIBLE);
    }

    public void hideExpandBtn() { //Esconde os FloatingActionButtons
        if (fab_expand.getAnimation() != null) {
            Animation close_linear = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_close_pop);
            fab_expand.startAnimation(close_linear);
            fab_expand.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    fab_expand.setAnimation(null);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
        if ((fab_expand.getAnimation() != null)) {
            fab_expand.getAnimation().setFillAfter(false);
        }
        fab_expand.setVisibility(View.INVISIBLE);
    }

    public void showRoundProgressbar() { //Mostra a progressBar ao carregar a página
        progressBar_Main.setVisibility(View.VISIBLE);
        mainLayout.setVisibility(View.GONE);
    }

    protected void dismissRoundProgressbar() { //Esconde a progressBar ao carregar a página
        progressBar_Main.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
    }

    public void showEmptyLayout() {
        mainLayout.setVisibility(View.GONE);
        errorConnectionLayout.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.VISIBLE);
    }

    public void hideEmptyLayout() {
        mainLayout.setVisibility(View.VISIBLE);
        emptyLayout.setVisibility(View.GONE);
    }

    protected void showLinearProgressbar() { //Mostra a progressBar ao carregar a página
        progressBar_Top.setVisibility(View.VISIBLE);
    }

    protected void dismissLinearProgressbar() { //Esconde a progressBar ao carregar a página
        progressBar_Top.setVisibility(View.GONE);
    }

    protected void autoLoadPages() { //Tenta carregar as páginas em segundo plano
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("key_autoload", false)) {

        /*if (navigation.getSelectedItemId() == R.id.navigation_diarios && !mainWebView.pg_diarios_loaded) {
            mainWebView.html.loadUrl(url + pg_diarios);
        } else if (navigation.getSelectedItemId() == R.id.navigation_boletim && !mainWebView.pg_boletim_loaded) {
            mainWebView.html.loadUrl(url + pg_boletim);
        } else if (navigation.getSelectedItemId() == R.id.navigation_horario && !mainWebView.pg_horario_loaded) {
            mainWebView.html.loadUrl(url + pg_horario);
        } else*/
            try {
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
        editor.putString(Utils.LOGIN_REGISTRATION, "");
        editor.putString(Utils.LOGIN_PASSWORD, "");
        editor.putString(Utils.LOGIN_NAME, "");
        editor.putBoolean(Utils.LOGIN_VALID, false);
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
            case 0: if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        CheckUpdate.startDownload(this, CheckUpdate.checkUpdate(this));
                        break;
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

    public void setOnPageFinishedListener(OnPageUpdated onPageUpdated){
        this.onPageUpdated = onPageUpdated;
    }

    public interface OnPageUpdated {
        void onPageUpdate(List<?> list);
    }

    private void configWebView(){
        mainWebView.configWebView(this);
        mainWebView.setOnPageFinishedListener(this);
        mainWebView.setOnPageStartedListener(this);
        mainWebView.setOnErrorRecivedListener(this);
    }
}
