package com.tinf.qmobile.WebView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LoginEvent;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Interfaces.WebView.OnPageLoad;
import com.tinf.qmobile.R;
import com.tinf.qmobile.Utilities.Utils;

import static com.tinf.qmobile.Utilities.Utils.LOGIN_REGISTRATION;
import static com.tinf.qmobile.Utilities.Utils.PG_ACESSO_NEGADO;
import static com.tinf.qmobile.Utilities.Utils.PG_BOLETIM;
import static com.tinf.qmobile.Utilities.Utils.PG_CALENDARIO;
import static com.tinf.qmobile.Utilities.Utils.PG_DIARIOS;
import static com.tinf.qmobile.Utilities.Utils.PG_ERRO;
import static com.tinf.qmobile.Utilities.Utils.PG_HOME;
import static com.tinf.qmobile.Utilities.Utils.PG_HORARIO;
import static com.tinf.qmobile.Utilities.Utils.PG_LOGIN;
import static com.tinf.qmobile.Utilities.Utils.PG_MATERIAIS;
import static com.tinf.qmobile.Utilities.Utils.URL;

public class ClientWebView extends WebViewClient {
    private OnPageLoad.Main onPageLoad;

    ClientWebView() {}

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        callOnError(request.getUrl().toString(), error.getDescription().toString());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);
        if (Utils.isConnected()) {
            if (!errorResponse.getReasonPhrase().equals("Not Found")) {// ignora o erro not found
                callOnError(request.getUrl().toString(), errorResponse.getReasonPhrase());
                //Toast.makeText(App.getAppContext(), errorResponse.getReasonPhrase(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPageStarted(WebView view, String url_i, Bitmap favicon) {
        super.onPageStarted(view, URL, favicon);
        callOnStart();
    }

    @Override
    public void onPageFinished(WebView view, String url_i) { //Chama as funções ao terminar de carregar uma página
        if (Utils.isConnected() && !url_i.isEmpty()) {

            SingletonWebView webView = SingletonWebView.getInstance();
            SharedPreferences login_info = App.getAppContext().getSharedPreferences(Utils.LOGIN_INFO, Context.MODE_PRIVATE);

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
                    //webView.isLoginPage = false;
                    Answers.getInstance().logLogin(new LoginEvent()
                            .putSuccess(true));
                    Log.i("WebViewClient", "Login done");
                    callOnFinish(URL + PG_LOGIN);
                }

                Log.i("WebViewClient", "Home loaded");

            } else if (url_i.contains(URL + PG_BOLETIM)) {

                webView.loadUrl("javascript:window.HtmlHandler.handleBoletim"
                        + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");

                Log.i("WebViewClient", "Boletim loaded");

            } else if (url_i.contains(URL + PG_DIARIOS)) {

                if (!webView.scriptDiario.isEmpty()) {

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

                if (!webView.scriptMateriais.isEmpty()) {

                    Log.i("SCRIPT", "Ok");
                    webView.loadUrl(webView.scriptMateriais);
                    webView.scriptMateriais = "";

                } else {

                    webView.loadUrl("javascript:window.HtmlHandler.handleMateriais"
                            + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");

                    Log.i("WebViewClient", "Materiais loaded");

                }
            } else if(url_i.equals(URL + PG_CALENDARIO)) {

                webView.loadUrl("javascript:window.HtmlHandler.handleCalendario"
                        + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");

                Log.i("WebViewClient", "Calendario loaded");

            } else if (url_i.equals(URL + PG_ERRO)) {

                Log.i("WebViewClient", "Error");

                if (webView.isLoginPage) {
                    Answers.getInstance().logLogin(new LoginEvent()
                            .putCustomAttribute("Matricula", login_info.getString(LOGIN_REGISTRATION, ""))
                            .putSuccess(false));
                    login_info.edit()
                            .putString(Utils.LOGIN_REGISTRATION, "")
                            .putString(Utils.LOGIN_PASSWORD, "")
                            .putBoolean(Utils.LOGIN_VALID, false)
                            .apply();
                    callOnFinish(URL + PG_ERRO);
                    Log.i("Login", "Login Inválido");
                } else {
                    callOnError(url_i, App.getAppContext().getResources().getString(R.string.text_connection_error));
                    //Toast.makeText(App.getAppContext(), App.getAppContext().getResources().getString(R.string.text_connection_error), Toast.LENGTH_SHORT).show();
                }
            } else if (url_i.equals(URL + PG_ACESSO_NEGADO)) {

                webView.loadUrl("javascript:window.HtmlHandler.handleAcessoNegado"
                        + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");

                Log.i("WebViewClient", "Acesso Negado");

            }
        } else {
            Log.e("ClientWebView", "Deu pau");
        }
    }

    private void callOnFinish(String url_p) {
        if (onPageLoad != null) {
            onPageLoad.onPageFinish(url_p);
        }
    }

    private void callOnError(String url_p, String error) {
        if (onPageLoad != null) {
            onPageLoad.onErrorRecived(url_p, error);
        }
    }

    private void callOnStart() {
        if (onPageLoad != null) {
            onPageLoad.onPageStart();
        }
    }

    public void setOnPageLoadListener(OnPageLoad.Main onPageLoad) {
        this.onPageLoad = onPageLoad;
    }
}