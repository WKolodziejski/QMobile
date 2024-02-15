package com.tinf.qmobile.parser.messages;

import com.tinf.qmobile.parser.BaseParser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

// Busca as infos do form na página
public class FormMessagesParser extends BaseParser {
  private final OnMessagesLoad onMessagesLoad;

  public FormMessagesParser(OnMessagesLoad onMessagesLoad,
                            int pg,
                            int year,
                            int period,
                            boolean notify,
                            OnFinish onFinish,
                            OnError onError) {
    super(pg, year, period, notify, onFinish, onError);
    this.onMessagesLoad = onMessagesLoad;
  }

  @Override
  public void parse(Document document) {
    // Tabela principal
    Element table =
        document.getElementById("ctl00_ContentPlaceHolderPrincipal_wucMensagens1_grdMensagens");

    if (table == null)
      return;

    // Corpo da tabela
    Element tbody = table.child(0);

    // Linhas da tabela
    Elements trs = tbody.children();

    if (trs.isEmpty())
      return;

    int pagesCount = 1;

    String eventValidation = document.getElementById("__EVENTVALIDATION")
                                     .attr("value");
    String viewState = document.getElementById("__VIEWSTATE")
                               .attr("value");

    // Testa se a primeira linha é de paginação
    if (trs.get(0)
           .childrenSize() == 1) {
      Elements tds = trs.get(0) // td
                        .child(0) // table
                        .child(0) // tbody
                        .child(0) // tr
                        .child(0)
                        .children();

      pagesCount = tds.size();
    }

    onMessagesLoad.onForm(eventValidation, viewState, pagesCount);
  }

}
