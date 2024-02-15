package com.tinf.qmobile.parser;

import static com.tinf.qmobile.App.getContext;
import static com.tinf.qmobile.fragment.SettingsFragment.POPUP;

import androidx.preference.PreferenceManager;

import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.UserUtils;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Date;

public class LoginParser extends BaseParser {

  private final OnDialog onDialog;
  private final OnRenewalAvailable onRenewalAvailable;

  public LoginParser(OnDialog onDialog,
                     OnRenewalAvailable onRenewalAvailable,
                     int page,
                     int year,
                     int period,
                     boolean notify,
                     OnFinish onFinish,
                     OnError onError) {
    super(page, year, period, notify, onFinish, onError);
    this.onDialog = onDialog;
    this.onRenewalAvailable = onRenewalAvailable;
  }

  @Override
  public void parse(Document document) {
    UserUtils.setLastLogin(new Date().getTime());

    document.outputSettings(new Document.OutputSettings().prettyPrint(false));
    document.select("br")
            .after("\\n");

    String cod = document.getElementsByTag("q_latente")
                         .get(4)
                         .val();
    cod = cod.substring(cod.indexOf("=") + 1);

    Element img = document.getElementsByAttributeValueEnding("src", cod)
                          .first();

    if (img != null && !Client.background) UserUtils.setImg(cod);

    Elements scripts = document.getElementsByTag("script");

    for (Element script : scripts) {
      String json = script.html();

      if (json.contains("mensagens.push")) {
        String title = json.substring(json.indexOf("assunto: '") + 10);
        title = title.substring(0, title.indexOf("', campo:"));
        title = title.replaceAll("\\\\n", "\n");
        title = title.replaceAll("<br>", "\n");

        String message = json.substring(json.indexOf("campo: '") + 8);
        message = message.substring(0, message.indexOf("', arquivos:"));
        message = message.replaceAll("\\\\n", "\n");
        message = message.replaceAll("<br>", "\n");

        if (PreferenceManager.getDefaultSharedPreferences(getContext())
                             .getBoolean(POPUP, true)) {
          onDialog.onDialog(title.trim(), message.trim());
        }
        break;
      }
    }

    Elements conteudoLink = document.getElementsByClass("conteudoLink");

    if (conteudoLink.size() >= 3) {

      Element renewal = conteudoLink.get(2);

      if (renewal != null && renewal.text()
                                    .contains("matr")) {
        onRenewalAvailable.onRenewalAvailable();
      }
    }
  }

  public interface OnRenewalAvailable {
    void onRenewalAvailable();
  }

  public interface OnDialog {
    void onDialog(String title,
                  String msg);
  }

}
