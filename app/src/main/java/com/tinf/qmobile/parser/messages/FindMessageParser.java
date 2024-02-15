package com.tinf.qmobile.parser.messages;

import android.util.Log;

import com.tinf.qmobile.model.message.Message;
import com.tinf.qmobile.parser.BaseParser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FindMessageParser extends BaseParser {
  private final Message message;
  private final OnMessagesLoad onMessagesLoad;

  // Verifica se a página atual possui a mensagem sendo buscada
  public FindMessageParser(OnMessagesLoad onMessagesLoad,
                           Message message,
                           int page,
                           int year,
                           int period,
                           boolean notify,
                           OnFinish onFinish,
                           OnError onError) {
    super(page, year, period, notify, onFinish, onError);
    this.onMessagesLoad = onMessagesLoad;
    this.message = message;
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

    int i = 1;
    int size = trs.size() - 1;
    int msg = 0;

    // Testa se a primeira linha é de paginação
    if (trs.get(0)
           .childrenSize() == 1) {
      i++;
      size--;
    }

    Log.d("FindMessageParser", String.valueOf(message.getUid_()));

    // Itera todas as linhas da tabela
    for (; i < size; i++, msg++) {
      // Colunas da linha selecionada
      Elements tds = trs.get(i)
                        .children();

      int uid = Integer.parseInt(tds.get(1)
                                    .text());

      // Verifica se o uid da mensagem selecionada corresponde ao uuid desejado
      if (uid == message.getUid_()) {
        String eventValidation = document.getElementById("__EVENTVALIDATION")
                                         .attr("value");
        String viewState = document.getElementById("__VIEWSTATE")
                                   .attr("value");

        onMessagesLoad.onForm(eventValidation, viewState, msg);
        break;
      }
    }
  }
}
