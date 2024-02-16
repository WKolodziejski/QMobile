package com.tinf.qmobile.activity;

import static com.tinf.qmobile.App.USE_INFO;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.tinf.qmobile.databinding.ActivityWebappBinding;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.network.OnResponse;

public class WebAppActivity extends AppCompatActivity implements OnResponse {

  private ActivityWebappBinding binding;

  @SuppressLint("SetJavaScriptEnabled")
  @Override
  protected void onCreate(
      Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityWebappBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    binding.webview.getSettings().setJavaScriptEnabled(true);
    binding.webview.getSettings().setUseWideViewPort(true);
    binding.webview.setWebViewClient(new WebViewClient() {

      @Override
      public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        binding.progressbar.setVisibility(View.VISIBLE);
      }

      @Override
      public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        binding.progressbar.setVisibility(View.INVISIBLE);
      }

    });

    getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
      @Override
      public void handleOnBackPressed() {
        if (binding.webview.canGoBack()) {
          binding.webview.goBack();
        } else {
          finish();
        }
      }
    });

    if (Client.get().isValid())
      binding.webview.loadUrl(Client.get().getURL() + "/webapp/dashboard");

    if (!getSharedPreferences(USE_INFO, MODE_PRIVATE).getBoolean("WEBAPP", false)) {
      new MaterialAlertDialogBuilder(WebAppActivity.this)
          .setTitle("Mudanças Temporárias")
          .setMessage(
              "O Q-Acadêmico Web fez uma alteração em seu site, provocando problemas no " +
              "aplicativo " +
              "do Qmobile. " +
              "Como solução temporária, o aplicativo irá redicerioná-lo para o site do " +
              "Q-Acadêmico Web, onde você poderá continuar utilizando o serviço. " +
              "Estamos trabalhando em uma correção e pedimos desculpas pelo inconveninente.")
          .setCancelable(false)
          .setPositiveButton("Entendido", null)
          .setNeutralButton("Não exibir novamente", (dialogInterface, i) -> {
            getSharedPreferences(USE_INFO, MODE_PRIVATE).edit().putBoolean("WEBAPP", true).apply();
          })
          .create()
          .show();
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    Client.get()
          .removeOnResponseListener(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    Client.get()
          .addOnResponseListener(this);
  }

  @Override
  public void onStart(int pg) {
    if (pg == PG_LOGIN)
      binding.progressbar.setVisibility(View.VISIBLE);
  }

  @Override
  public void onFinish(int pg, int year, int period) {
    if (pg == PG_LOGIN) {
      binding.progressbar.setVisibility(View.INVISIBLE);
      binding.webview.loadUrl(Client.get().getURL() + "/webapp/dashboard");
    }
  }

  @Override
  public void onError(int pg, int year, int period, String error) {
    binding.progressbar.setVisibility(View.INVISIBLE);
  }

  @Override
  public void onAccessDenied(int pg, String message) {
    binding.progressbar.setVisibility(View.INVISIBLE);
  }

  @Override
  public void onStart() {
    super.onStart();
    Client.get()
          .addOnResponseListener(this);
  }

  @Override
  protected void onStop() {
    super.onStop();
    Client.get()
          .removeOnResponseListener(this);
  }
}
