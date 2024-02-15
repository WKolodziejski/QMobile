package com.tinf.qmobile.parser.messages;

import android.util.Log;

import com.tinf.qmobile.model.message.Attachment;
import com.tinf.qmobile.model.message.Message;
import com.tinf.qmobile.model.message.Message_;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.parser.BaseParser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SingleMessageParser extends BaseParser {
  private final Message message;

  // Processa uma mensagem
  public SingleMessageParser(Message message,
                             int pg,
                             int year,
                             int period,
                             boolean notify,
                             OnFinish onFinish,
                             OnError onError) {
    super(pg, year, period, notify, onFinish, onError);
    this.message = message;
  }

  @Override
  public void parse(Document document) throws Exception {
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

    // Testa se a primeira linha é de paginação
    if (trs.get(0)
           .childrenSize() == 1) {
      i++;
      size--;
    }

    Log.d("SingleMessageParser", String.valueOf(message.getUid_()));

    // Itera todas as linhas da tabela
    for (; i < size; i++) {
      // Busca pela linha marcada como selecionada
      if (!trs.get(i)
              .attributes()
              .get("style")
              .contains("#F2CFA5")) {
        continue;
      }

      // Colunas da linha selecionada
      Elements tds = trs.get(i)
                        .children();

      int uid = Integer.parseInt(tds.get(1)
                                    .text());

      // Verifica se o uid da mensagem selecionada corresponde ao uuid desejado
      if (uid != message.getUid_()) {
        throw new RuntimeException(
            "Message uid different from found\nExpected " + message.getUid_() + "\n Found " + uid);
      }

      String subject = tds.get(4)
                          .text();
      Message search = null;

      try {
        search = messageBox.query()
                           .equal(Message_.uid_, uid)
                           .build()
                           .findUnique();
      } catch (Exception e) {
        Log.e("MessageParser", String.valueOf(uid));
        e.printStackTrace();
      }

      if (search == null) {
        crashlytics.recordException(new Exception(subject + " not found in DB"));
        Log.d(subject, "Not found in DB");
        continue;
      }

      // Area de texto com a mensagem
      Element textArea = document.getElementById(
          "ctl00_ContentPlaceHolderPrincipal_wucMensagens1_wucExibirMensagem1_txtCorpo");

      if (textArea == null)
        return;

      String text = textArea.text();
      search.setText(text);

      // Tabela de anexos
      Element atTable = document.getElementById(
          "ctl00_ContentPlaceHolderPrincipal_wucMensagens1_wucExibirMensagem1_grdAnexos");

      if (atTable == null)
        return;

      // Corpo da tabela de anexos
      Element atBody = atTable.child(0);

      // Linhas da tabela de anexos
      Elements atTrs = atBody.children();

      for (int j = 1; j < atTrs.size(); j++) {
        // Colunas da linha de anexos
        Elements atTds = atTrs.get(j)
                              .children();

        String title = atTds.get(0)
                            .text();
        String url =
            Client.get()
                  .getURL() + "/qacademicodotnet/" + atTds.get(0)
                                                          .child(0)
                                                          .attr("href");
        String obs = atTds.get(1)
                          .text();

        Log.d(title, url);

        Attachment attachment = new Attachment(title, obs, url);
        attachment.message.setTarget(search);

        attachmentBox.put(attachment);

        search.attachments.add(attachment);
      }

      messageBox.put(search);
      break;
    }
  }
}
