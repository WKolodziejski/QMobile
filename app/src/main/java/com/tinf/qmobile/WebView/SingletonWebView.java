package com.tinf.qmobile.WebView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Interfaces.WebView.OnPageLoad;
import com.tinf.qmobile.R;
import com.tinf.qmobile.Utilities.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;
import io.objectbox.BoxStore;
import static android.content.Context.MODE_PRIVATE;
import static com.tinf.qmobile.Utilities.Utils.LOGIN_INFO;
import static com.tinf.qmobile.Utilities.Utils.PG_BOLETIM;
import static com.tinf.qmobile.Utilities.Utils.PG_DIARIOS;
import static com.tinf.qmobile.Utilities.Utils.PG_LOGIN;
import static com.tinf.qmobile.Utilities.Utils.PG_MATERIAIS;
import static com.tinf.qmobile.Utilities.Utils.UPDATE_REQUEST;
import static com.tinf.qmobile.Utilities.Utils.URL;
import static com.tinf.qmobile.Utilities.Utils.YEARS;

public class SingletonWebView implements OnPageLoad.Main {
    private static SingletonWebView singleton;
    private WebView webView;
    private boolean isLoading, isPaused;
    private List<String> queue = new ArrayList<>();
    public boolean[] pg_diarios_loaded = {false},
                     pg_horario_loaded = {false},
                     pg_boletim_loaded = {false};
    public boolean isLoginPage, pg_calendario_loaded, pg_home_loaded;
    public String scriptDiario = "", scriptMateriais = "";
    public int year_position;
    public String[] data_year = {""};
    public BoxStore box;
    private Context context;
    private OnPageLoad.Main onPageLoad;
    private JavaScriptWebView javaScriptWebView;

    public void setOnPageLoadListener(OnPageLoad.Main onPageLoad) {
        this.onPageLoad = onPageLoad;
    }

    public void setOnMateriaisLoadListener(OnPageLoad.Materiais onPageLoad) {
        javaScriptWebView.setOnMateriaisLoadListener(onPageLoad);
    }

    private SingletonWebView() {}

    public synchronized static SingletonWebView getInstance() {
        if (singleton == null) {
            singleton = new SingletonWebView();
        }
        return singleton;
    }

    public WebView getWebView() {
        return webView;
    }

    public void loadUrl(String pg) {
        if (Utils.isConnected()) {
            if (!pg.contains("javascript")) {
                Log.i("Client", "Não contém java script");
                if (!isLoading) {
                    if (pg_home_loaded) {
                        Log.i("Client", "Carregando...");
                        webView.loadUrl(pg);
                    } else {
                        webView.loadUrl(URL + PG_LOGIN);
                        addToQueue(pg);
                    }
                } else {
                    addToQueue(pg);
                }
            } else {
                Log.i("Client", "Contém java script");
                webView.loadUrl(pg);
            }
        }
    }

    public void reload(int id) {
        if (id == R.id.navigation_home) {
           loadUrl(URL + PG_LOGIN);

        } else if (id == R.id.navigation_notas) {
            scriptDiario = "javascript: var option = document.getElementsByTagName('option'); option["
                    + (year_position + 1) + "].selected = true; document.forms['frmConsultar'].submit();";

            loadUrl(URL + PG_DIARIOS);

            addToQueue(URL + PG_BOLETIM + "&COD_MATRICULA=-1&cmbanos="
                    + data_year[year_position] + "&cmbperiodos=1&Exibir=Exibir+Boletim");

        } else if (id == R.id.navigation_materiais) {
            webView.reload();
        }
    }

    public void loadNextUrl() {
        if (!isPaused) {
            if (queue.size() > 0) {
                String queue = this.queue.get(this.queue.size() - 1);
                this.queue.remove(this.queue.size() - 1);

                if (!queue.isEmpty()) {
                    Log.i("Client", "Carregando fila...");
                    loadUrl(queue);
                }
            }
        }
    }

    public void resumeQueue() {
        isPaused = false;
    }

    private void addToQueue(String pg) {
        boolean equals = false;
        for (int i = 0; i < queue.size(); i++) {
            if (queue.get(i).equals(pg)) {
                equals = true;
                break;
            }
        }
        if (!equals) {
            Log.i("Client", "add a fila: " + pg);
            queue.add(pg);
        }
    }

    public void downloadMaterial(String link){
        webView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            isLoading = false;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            App.getAppContext().startActivity(i);
        });
        webView.loadUrl("javascript:document.querySelector(\"a[href='" + link + "']\").click();");
    }

    public void changeDate(int value, int id) {
        if (!isLoading) {
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName("Change Year")
                    .putCustomAttribute("To position", value));

            year_position = value;

            scriptMateriais = "javascript: document.getElementById(\"ANO_PERIODO\").selectedIndex ="
                    + (year_position + 1) + ";document.forms[0].submit();";

            if (id == R.id.navigation_materiais) {
                webView.loadUrl(URL + PG_MATERIAIS);
            } else {
               callOnFinish(UPDATE_REQUEST);
            }

        } else {
            Toast.makeText(App.getAppContext(), App.getAppContext().getResources().getString(R.string.text_wait_loading), Toast.LENGTH_SHORT).show();
        }
    }

    private String[] loadYears() {
        List<String> years = new ArrayList<>();
        String jsonArrayString = App.getAppContext().getSharedPreferences(LOGIN_INFO, MODE_PRIVATE).getString(YEARS, "");
        if (!TextUtils.isEmpty(jsonArrayString)) {
            try {
                JSONArray jsonArray = new JSONArray(jsonArrayString);
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        years.add(jsonArray.getString(i));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return years.toArray(new String[0]);
    }

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    public void configWebView(Context context) {
        this.context = context;

        data_year = loadYears();

        //queue.add(URL + PG_CALENDARIO);
        //queue.add(URL + PG_HORARIO);
        queue.add(URL + PG_BOLETIM);
        queue.add(URL + PG_DIARIOS);
        //queue.add(URL + PG_HOME);

        webView = new WebView(context);
        WebSettings faller = webView.getSettings();
        faller.setJavaScriptEnabled(true);
        faller.setDomStorageEnabled(true);
        faller.setLoadsImagesAutomatically(false);
        faller.setUseWideViewPort(true);
        faller.setLoadWithOverviewMode(true);

        javaScriptWebView = new JavaScriptWebView(context);
        ClientWebView clientWebView = new ClientWebView(context);

        javaScriptWebView.setOnPageLoadListener(this);
        clientWebView.setOnPageLoadListener(this);

        webView.setWebViewClient(clientWebView);
        webView.addJavascriptInterface(javaScriptWebView, "HtmlHandler");
    }

    public synchronized static void logOut() {
        singleton = null;
    }

    public void setBoxStore(BoxStore box) {
        this.box = box;
    }

    @Override
    public void onPageStart() {
        isLoading = true;
        callOnStart();
    }

    @Override
    public void onPageFinish(String url_p) {
        isLoading = false;
        if (url_p.contains(URL + PG_MATERIAIS)) {
            isPaused = true;
        }
        Log.i("Finish", url_p);
        callOnFinish(url_p);
    }

    @Override
    public void onErrorRecived(String error) {
        isLoading = false;
        webView.post(() -> webView.stopLoading());
        Log.i("Error", error);
        callOnError(error);
    }

    private void callOnFinish(String url_p) {
        if (onPageLoad != null) {
            onPageLoad.onPageFinish(url_p);
        }
    }

    private void callOnError(String error) {
        if (onPageLoad != null) {
            onPageLoad.onErrorRecived(error);
        }
    }

    private void callOnStart() {
        if (onPageLoad != null) {
            onPageLoad.onPageStart();
        }
    }
}
