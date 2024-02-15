package com.tinf.qmobile.parser;

import static com.tinf.qmobile.App.getContext;
import static com.tinf.qmobile.network.OnResponse.PG_ACCESS_DENIED;
import static com.tinf.qmobile.network.OnResponse.PG_LOGIN;
import static com.tinf.qmobile.network.OnResponse.PG_QUEST;
import static com.tinf.qmobile.network.OnResponse.PG_REGISTRATION;
import static com.tinf.qmobile.network.OnResponse.PG_UPDATE;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.tinf.qmobile.R;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.UserUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public final class ResponseParser {

  private ResponseParser() {}

  public static Client.Resp parseResponse(String response,
                                          int pg,
                                          int year,
                                          int period,
                                          BaseParser.OnError onError,
                                          BaseParser.OnError onAccessDenied) {
    Document document = Jsoup.parse(response);

    if (document.text()
                .contains("Houve um erro inesperado")) return Client.Resp.UNKNOWN;

    Element strong = document.getElementsByTag("strong")
                             .first();

    if (strong != null) {
      String s = strong.text()
                       .trim();

      if (s.contains("negado") || s.contains("Negado")) {
        Element div = document.getElementsByClass("conteudoTexto")
                              .first();

        if (div == null) {
          return Client.Resp.DENIED;
        }

        div.select("br")
           .after("\\n");
        String msg = div.text()
                        .replaceAll("\\\\n", "\n")
                        .trim();

        if (msg.contains("inativo")) {
          UserUtils.clearInfo();
          onAccessDenied.onError(PG_ACCESS_DENIED, 0, 0, msg);
          return Client.Resp.EGRESS;
        }

        return Client.Resp.DENIED;
      }
    }

    Element p = document.getElementsByTag("p")
                        .first();

    if (p != null) {
      if (p.text()
           .contains("inacess") || p.text()
                                    .contains("Banco")) {
        onError.onError(PG_LOGIN, year, period, getContext().getResources()
                                              .getString(R.string.client_host));
        return Client.Resp.HOST;
      }
    }

    Element form = document.getElementsByClass("conteudoTexto")
                           .first();

    if (form != null) {
      if (form.text()
              .contains("senha")) {
        String msg = form.text()
                         .replaceAll("\\\\n", "\n")
                         .trim();
        onAccessDenied.onError(PG_UPDATE, year, period, msg);
        return Client.Resp.UPDATE;
      }
    }

    Element quest = document.getElementsByClass("TEXTO_TITULO")
                            .first();

    if (quest != null) {
      if (quest.text()
               .contains("Question")) {
        String msg = "";
        if (form != null) {
          msg = form.text()
                    .replaceAll("\\\\n", "\n")
                    .trim();
        }
        onAccessDenied.onError(PG_QUEST, year, period, msg);
        return Client.Resp.QUEST;
      }

      if (quest.text()
               .contains("Altera")) {
        onAccessDenied.onError(PG_REGISTRATION, year, period, "");
        return Client.Resp.REG;
      }
    }

    Element title = document.getElementsByTag("title")
                            .first();

    if (title != null && title.text()
                              .contains("Erro")) {
      onAccessDenied.onError(pg, year, period, getContext().getString(R.string.client_error));
      return Client.Resp.UNKNOWN;
    }

    Elements sub = document.getElementsByClass("barraRodape");
    Elements sub2 = document.getElementsByClass("titulo");

    String name;

    if (sub.size() >= 2) {
      name = sub.get(1)
                .text();
    } else if (sub2.size() >= 2) {
      name = sub2.get(1)
                 .text();
      name = name.substring(name.indexOf(",") + 1)
                 .trim();
    } else {
      onAccessDenied.onError(pg, year, period, getContext().getString(R.string.client_error));
      FirebaseCrashlytics.getInstance()
                         .recordException(new Exception(document.toString()));
      return Client.Resp.UNKNOWN;
    }

    UserUtils.setName(name);

    return Client.Resp.OK;
  }
}
