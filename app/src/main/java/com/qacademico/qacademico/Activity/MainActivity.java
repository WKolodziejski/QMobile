package com.qacademico.qacademico.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.qacademico.qacademico.Class.ExpandableList;
import com.qacademico.qacademico.Fragment.HomeFragment;
import com.qacademico.qacademico.Fragment.MateriaisFragment;
import com.qacademico.qacademico.Fragment.ViewPager.NotasFragment;
import com.qacademico.qacademico.Fragment.ViewPager.OrganizacaoFragment;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.Utilities.CheckUpdate;
import com.qacademico.qacademico.Utilities.Design;
import com.qacademico.qacademico.Utilities.Utils;
import com.qacademico.qacademico.WebView.SingletonWebView;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.qacademico.qacademico.Utilities.Utils.PG_BOLETIM;
import static com.qacademico.qacademico.Utilities.Utils.PG_DIARIOS;
import static com.qacademico.qacademico.Utilities.Utils.PG_HOME;
import static com.qacademico.qacademico.Utilities.Utils.PG_HORARIO;
import static com.qacademico.qacademico.Utilities.Utils.PG_LOGIN;
import static com.qacademico.qacademico.Utilities.Utils.PG_MATERIAIS;
import static com.qacademico.qacademico.Utilities.Utils.PG_CALENDARIO;
import static com.qacademico.qacademico.Utilities.Utils.URL;

public class MainActivity extends AppCompatActivity implements SingletonWebView.OnPageFinished, SingletonWebView.OnPageStarted, SingletonWebView.OnRecivedError {
    @BindView(R.id.connection) LinearLayout errorConnectionLayout;
    @BindView(R.id.empty) LinearLayout emptyLayout;
    @BindView(R.id.progressbar_main) ProgressBar progressBar_Main;
    @BindView(R.id.progressbar_horizontal) ProgressBar progressBar_Top;
    @BindView(R.id.main_container) ViewGroup mainLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fab_expand) public FloatingActionButton fab_expand;
    @BindView(R.id.navigation) public BottomNavigationView navigation;
    private SharedPreferences login_info;
    private Snackbar snackBar;
    private OnPageUpdated onPageUpdated;
    public List<ExpandableList> diariosList;
    public SingletonWebView webView = SingletonWebView.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setOnNavigationItemReselectedListener(mOnNavigationItemReselectedListener);

        login_info = getSharedPreferences(Utils.LOGIN_INFO, 0);

        hideExpandBtn();
        Design.setNavigationTransparent(this);
        Design.applyToolbarScrollBehavior(this, mainLayout, toolbar);

        CheckUpdate.checkUpdate(this);

        webView.configWebView(this);
        webView.setOnPageFinishedListener(this);
        webView.setOnPageStartedListener(this);
        webView.setOnErrorRecivedListener(this);

        if (login_info.getBoolean(Utils.LOGIN_VALID, false)) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, new HomeFragment(), Utils.HOME).commit();
            if(!webView.pg_login_loaded) {
                webView.loadUrl(URL + PG_LOGIN);
            }
        } else {
            startActivityForResult(new Intent(getApplicationContext(), LoginActivity.class), 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);

        MenuItem date = menu.findItem(R.id.action_date);
        MenuItem column = menu.findItem(R.id.action_column);
        MenuItem header = menu.findItem(R.id.action_header);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_date) {

            return true;
        } else if (id == R.id.action_column) {

            return true;
        } else if (id == R.id.action_header) {
            invalidateOptionsMenu();

            return true;
        }

        return super.onOptionsItemSelected(item);
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
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, new HomeFragment(), Utils.HOME).commit();
                        return true;

                    case R.id.navigation_notas:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, new NotasFragment(), Utils.NOTAS).commit();
                        return true;

                    case R.id.navigation_organizacao:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, new OrganizacaoFragment(), Utils.ORGANIZACAO).commit();
                        return true;

                    case R.id.navigation_materiais:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, new MateriaisFragment(), Utils.MATERIAIS).commit();
                        return true;
                }
            }
            return false;
        }
    };

    @Override
    public void onPageStart(String url_p) {
        Log.i("Singleton", "onStart");

        if (       (url_p.equals(URL + PG_HOME))
                || (url_p.contains(URL + PG_BOLETIM) && navigation.getSelectedItemId() == R.id.navigation_notas)
                || (url_p.contains(URL + PG_DIARIOS) && navigation.getSelectedItemId() == R.id.navigation_notas)
                || (url_p.contains(URL + PG_MATERIAIS) && navigation.getSelectedItemId() == R.id.navigation_materiais)) {
            showLinearProgressbar();
        }
    }

    @Override
    public void onPageFinish(String url_p, List<?> list) {
        runOnUiThread(() -> {
            Log.i("Singleton", "onFinish");

            dismissRoundProgressbar();
            dismissLinearProgressbar();
            invalidateOptionsMenu();

            if (url_p.equals(URL + PG_HOME)) {
                Log.i("onFinish", "loadedHome");
                if (navigation.getSelectedItemId() == R.id.navigation_home) {
                    onPageUpdated.onPageUpdate(null);
                    Log.i("onFinish", "updatedHome");
                }
            } else if (url_p.equals(URL + PG_BOLETIM) && navigation.getSelectedItemId() == R.id.navigation_notas) {
                onPageUpdated.onPageUpdate(list);
                Log.i("onFinish", "updatedBoletim");
            } else if (url_p.equals(URL + PG_DIARIOS) && navigation.getSelectedItemId() == R.id.navigation_notas) {
                onPageUpdated.onPageUpdate(list);
                Log.i("onFinish", "updatedDiarios");
            } else if (url_p.equals(URL + PG_MATERIAIS) && navigation.getSelectedItemId() == R.id.navigation_materiais) {
                //onPageUpdated.onPageUpdate(list);
                Log.i("onFinish", "updatedMateriais");
            }
        });
    }

    @Override
    public void onErrorRecived(String error) {
        dismissLinearProgressbar();
        dismissRoundProgressbar();
    }

    public void refreshPage(View v) { //Atualiza a página
        /*showRoundProgressbar();
        dismissErrorConnection();
        if (!webView.pg_home_loaded) {
            webView.loadUrl(URL + PG_LOGIN);
        } else {
            if (navigation.getSelectedItemId() == R.id.navigation_home) {
                webView.loadUrl(URL + PG_HOME);
            } else if (navigation.getSelectedItemId() == R.id.navigation_diarios) {
                webView.loadUrl(URL + PG_DIARIOS);
            } else if (navigation.getSelectedItemId() == R.id.navigation_boletim) {
                webView.loadUrl(URL + PG_BOLETIM);
            } else if (navigation.getSelectedItemId() == R.id.navigation_materiais) {
                webView.loadUrl(URL + PG_MATERIAIS);
            }
        }*/
    }

    public void showErrorConnection() {//Mostra a página de erro de conexão
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

    public void dismissLinearProgressbar() { //Esconde a progressBar ao carregar a página
        progressBar_Top.setVisibility(View.INVISIBLE);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        recreate();
    }

    @Override
    public void onBackPressed() {
        if (navigation.getSelectedItemId() != R.id.navigation_home) {
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
}
