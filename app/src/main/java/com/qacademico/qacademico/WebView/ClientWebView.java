package com.qacademico.qacademico.WebView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.google.firebase.perf.metrics.AddTrace;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.Utilities.Data;
import com.qacademico.qacademico.Utilities.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static com.qacademico.qacademico.Utilities.Utils.pg_boletim;
import static com.qacademico.qacademico.Utilities.Utils.pg_change_password;
import static com.qacademico.qacademico.Utilities.Utils.pg_diarios;
import static com.qacademico.qacademico.Utilities.Utils.pg_erro;
import static com.qacademico.qacademico.Utilities.Utils.pg_home;
import static com.qacademico.qacademico.Utilities.Utils.pg_horario;
import static com.qacademico.qacademico.Utilities.Utils.pg_login;
import static com.qacademico.qacademico.Utilities.Utils.pg_materiais;
import static com.qacademico.qacademico.Utilities.Utils.url;

public class ClientWebView extends WebViewClient {
    private Context context;
    private SingletonWebView webViewMain;
    private SharedPreferences login_info;
    private OnPageFinished onPageFinished;
    private OnPageStarted onPageStarted;

    public ClientWebView(Context context) {
        this.context = context.getApplicationContext();
        this.login_info = this.context.getSharedPreferences("login_info", 0);
        this.webViewMain = SingletonWebView.getInstance();
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    @AddTrace(name = "onRecivedError")
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
            /*dismissLinearProgressbar();
            dismissRoundProgressbar();
            dismissProgressDialog();
            if (Utils.isConnected(getApplicationContext())) {
                if (isLoginPage) {
                    loginFragment.dismissProgressBar();
                    showSnackBar(getResources().getString(R.string.text_connection_error), false);
                } else if (!pg_home_loaded && navigation.getSelectedItemId() == R.id.navigation_home) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_connection_error), Toast.LENGTH_SHORT).show();
                }
            }*/
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    @AddTrace(name = "onRecivedHttpError")
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);
            /*dismissLinearProgressbar();
            dismissRoundProgressbar();
            dismissProgressDialog();*/
            if (Utils.isConnected(context)) {
                if (!errorResponse.getReasonPhrase().equals("Not Found")) {// ignora o erro not found
                    Toast.makeText(context, errorResponse.getReasonPhrase(), Toast.LENGTH_SHORT).show();
                    /*if (isLoginPage) {
                        loginFragment.dismissProgressBar();
                        showSnackBar(getResources().getString(R.string.text_connection_error), false);
                    } else if (!pg_home_loaded && navigation.getSelectedItemId() == R.id.navigation_home) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_connection_error), Toast.LENGTH_SHORT).show();
                    }*/
                }
            }
    }

    @Override
    public void onPageStarted(WebView view, String url_i, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        onPageStarted.onPageStart(url_i);
    }

    @Override
    @AddTrace(name = "setOnPageFinished")
    public void onPageFinished(WebView view, String url_i) { //Chama as funções ao terminar de carregar uma página
        if (Utils.isConnected(context) && !url_i.equals("")) {
            if (url_i.equals(url + pg_login)) {
                webViewMain.html.loadUrl("javascript:var uselessvar = document.getElementById('txtLogin').value='"
                        + login_info.getString("matricula", "") + "';");
                webViewMain.html.loadUrl("javascript:var uselessvar = document.getElementById('txtSenha').value='"
                        + login_info.getString("password", "") + "';");
                webViewMain.html.loadUrl("javascript:document.getElementById('btnOk').click();");
                Log.i("Login", "Tentando Logar...");
            } else if (url_i.equals(url + pg_home)) {
                webViewMain.html.loadUrl("javascript:window.HtmlHandler.handleHome"
                        + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                if (webViewMain.isLoginPage) {
                    webViewMain.isLoginPage = false;
                    SharedPreferences.Editor editor = login_info.edit();
                    editor.putBoolean("valido", true);
                    editor.apply();
                    Log.i("Login", "isLogin = false;");
                    onPageFinished.onPageFinish(url + pg_login);
                    Log.i("WebViewClient", "Login done");
                }
                Log.i("WebViewClient", "Home loaded");
            } else if (url_i.contains(url + pg_boletim)) {
                webViewMain.html.loadUrl("javascript:window.HtmlHandler.handleBoletim"
                        + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                Log.i("WebViewClient", "Boletim loaded");
            } else if (url_i.contains(url + pg_diarios)) {
                if (webViewMain.scriptDiario.contains("javascript:")) {
                    Log.i("SCRIPT", "Ok");
                    webViewMain.html.loadUrl(webViewMain.scriptDiario);
                    webViewMain.scriptDiario = "";
                } else {
                    webViewMain.html.loadUrl("javascript:window.HtmlHandler.handleDiarios"
                            + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                    Log.i("WebViewClient", "Diarios loaded");
                }
            } else if (url_i.contains(url + pg_horario)) {
                webViewMain.html.loadUrl("javascript:window.HtmlHandler.handleHorario"
                        + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                Log.i("WebViewClient", "Horario loaded");
            } else if (url_i.contains(url + pg_materiais)) {
                webViewMain.html.loadUrl("javascript:window.HtmlHandler.handleMateriais"
                        + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                Log.i("WebViewClient", "Materiais loaded");
            } else if (url_i.contains(url + pg_change_password)) {
                webViewMain.isChangePasswordPage = !webViewMain.isChangePasswordPage;
                Log.i("WebViewClient", "ChangePassword loaded");
                if (webViewMain.isChangePasswordPage) {
                    webViewMain.html.loadUrl("javascript:var uselessvar = document.getElementById('senha0').value='"
                            + login_info.getString("password", "") + "';");
                    webViewMain.html.loadUrl("javascript:var uselessvar = document.getElementById('senha1').value='"
                            + webViewMain.new_password + "';");
                    webViewMain.html.loadUrl("javascript:var uselessvar = document.getElementById('senha2').value='"
                            + webViewMain.new_password + "';");
                    webViewMain.html.loadUrl("javascript:document.getElementById('btnConfirmar').click();");
                    SharedPreferences.Editor editor = login_info.edit();
                    editor.putString("password", webViewMain.new_password);
                    editor.apply();
                    new AlertDialog.Builder(context)
                            .setCustomTitle(Utils.customAlertTitle(context, R.drawable.ic_check_black_24dp, R.string.success_title, R.color.green_500))
                            .setMessage(R.string.passchange_txt_success_message)
                            .setPositiveButton(R.string.dialog_close, null)
                            .show();
                }
            } else if (url_i.equals(url + pg_erro)) {
                Log.i("WebViewClient", "Error");
                if (webViewMain.isLoginPage) {
                    onPageFinished.onPageFinish(url + pg_erro);
                    SharedPreferences.Editor editor = login_info.edit();
                    editor.putString("matricula", "");
                    editor.putString("password", "");
                    editor.putBoolean("valido", false);
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
}
