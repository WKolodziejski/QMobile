package com.tinf.qacademico.WebView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.tinf.qacademico.R;
import com.tinf.qacademico.Utilities.Utils;
import static com.tinf.qacademico.Utilities.Utils.PG_BOLETIM;
import static com.tinf.qacademico.Utilities.Utils.PG_CALENDARIO;
import static com.tinf.qacademico.Utilities.Utils.PG_DIARIOS;
import static com.tinf.qacademico.Utilities.Utils.PG_ERRO;
import static com.tinf.qacademico.Utilities.Utils.PG_HOME;
import static com.tinf.qacademico.Utilities.Utils.PG_HORARIO;
import static com.tinf.qacademico.Utilities.Utils.PG_LOGIN;
import static com.tinf.qacademico.Utilities.Utils.PG_MATERIAIS;
import static com.tinf.qacademico.Utilities.Utils.URL;

public class ClientWebView extends WebViewClient {
    private Context context;
    private SingletonWebView webView = SingletonWebView.getInstance();
    private SharedPreferences login_info;
    private OnPageFinished onPageFinished;
    private OnPageStarted onPageStarted;
    private OnRecivedError onRecivedError;

    ClientWebView(Context context) {
        this.context = context.getApplicationContext();
        this.login_info = this.context.getSharedPreferences(Utils.LOGIN_INFO, Context.MODE_PRIVATE);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        onRecivedError.onErrorRecived(error.getDescription().toString());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);
        if (Utils.isConnected(context)) {
            if (!errorResponse.getReasonPhrase().equals("Not Found")) {// ignora o erro not found
                onRecivedError.onErrorRecived(errorResponse.getReasonPhrase());
                Toast.makeText(context, errorResponse.getReasonPhrase(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPageStarted(WebView view, String url_i, Bitmap favicon) {
        super.onPageStarted(view, URL, favicon);
        onPageStarted.onPageStart(url_i);
    }

    @Override
    public void onPageFinished(WebView view, String url_i) { //Chama as funções ao terminar de carregar uma página
        if (Utils.isConnected(context) && !url_i.equals("")) {

            if (url_i.equals(URL + PG_LOGIN)) {

                webView.loadUrl("javascript:var uselessvar = document.getElementById('txtLogin').value='"
                        + login_info.getString(Utils.LOGIN_REGISTRATION, "") + "';");

                webView.loadUrl("javascript:var uselessvar = document.getElementById('txtSenha').value='"
                        + login_info.getString(Utils.LOGIN_PASSWORD, "") + "';");

                webView.loadUrl("javascript:document.getElementById('btnOk').click();");

                Log.i("Login", "Tentando Logar...");

            } else if (url_i.equals(URL + PG_HOME)) {

                webView.loadUrl("javascript:window.HtmlHandler.handleHome"
                        + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");

                if (webView.isLoginPage) {
                    webView.isLoginPage = false;
                    onPageFinished.onPageFinish(URL + PG_LOGIN);
                    Log.i("WebViewClient", "Login done");
                }

                Log.i("WebViewClient", "Home loaded");

            } else if (url_i.contains(URL + PG_BOLETIM)) {

                webView.loadUrl("javascript:window.HtmlHandler.handleBoletim"
                        + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");

                Log.i("WebViewClient", "Boletim loaded");

            } else if (url_i.contains(URL + PG_DIARIOS)) {

                if (webView.scriptDiario.contains("javascript:")) {

                    Log.i("SCRIPT", "Ok");
                    webView.loadUrl(webView.scriptDiario);
                    webView.scriptDiario = "";

                } else {

                    webView.loadUrl("javascript:window.HtmlHandler.handleDiarios"
                            + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");

                    Log.i("WebViewClient", "DiariosList loaded");
                }

            } else if (url_i.contains(URL + PG_HORARIO)) {
                webView.loadUrl("javascript:window.HtmlHandler.handleHorario"
                        + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                Log.i("WebViewClient", "Horario loaded");
            } else if (url_i.contains(URL + PG_MATERIAIS)) {
                webView.loadUrl("javascript:window.HtmlHandler.handleMateriais"
                        + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                Log.i("WebViewClient", "Materiais loaded");
            } else if(url_i.equals(URL + PG_CALENDARIO)) {
                webView.loadUrl("javascript:window.HtmlHandler.handleCalendario"
                        + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            } else if (url_i.equals(URL + PG_ERRO)) {
                Log.i("WebViewClient", "Error");
                if (webView.isLoginPage) {
                    onPageFinished.onPageFinish(URL + PG_ERRO);
                    SharedPreferences.Editor editor = login_info.edit();
                    editor.putString(Utils.LOGIN_REGISTRATION, "");
                    editor.putString(Utils.LOGIN_PASSWORD, "");
                    editor.putBoolean(Utils.LOGIN_VALID, false);
                    editor.apply();
                    Log.i("Login", "Login Inválido");
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.text_connection_error), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void setOnPageFinishedListener(OnPageFinished onPageFinished){
        this.onPageFinished = onPageFinished;
    }

    public interface OnPageFinished {
        void onPageFinish(String url_p);
    }

    public void setOnPageStartedListener(OnPageStarted onPageStarted){
        this.onPageStarted = onPageStarted;
    }

    public interface OnPageStarted {
        void onPageStart(String url_p);
    }

    public void setOnErrorRecivedListener(OnRecivedError onRecivedError){
        this.onRecivedError = onRecivedError;
    }

    public interface OnRecivedError {
        void onErrorRecived(String error);
    }
}
