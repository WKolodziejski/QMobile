package com.tinf.qmobile.parser.messages;

import static com.tinf.qmobile.model.ViewType.MESSAGE;
import static io.objectbox.query.QueryBuilder.StringOrder.CASE_INSENSITIVE;

import android.content.Intent;
import android.util.Log;

import com.tinf.qmobile.App;
import com.tinf.qmobile.activity.MessagesActivity;
import com.tinf.qmobile.model.message.Message;
import com.tinf.qmobile.model.message.Message_;
import com.tinf.qmobile.model.message.Sender;
import com.tinf.qmobile.model.message.Sender_;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.parser.BaseParser;
import com.tinf.qmobile.utility.NotificationUtils;
import com.tinf.qmobile.utility.RandomColor;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import io.objectbox.query.QueryBuilder;

// Processa uma página com uma lista de mensagens
public class PageMessagesParser extends BaseParser {
  private static final RandomColor colors = new RandomColor();

  public PageMessagesParser(int pg,
                            int year,
                            int period,
                            boolean notify,
                            OnFinish onFinish,
                            OnError onError) {
    super(pg, year, period, notify, onFinish, onError);
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

      // Se a página atual é a primeira carrega as demais
      if (tds.get(0)
             .text()
             .equals("1")
          && messageBox.isEmpty()) {
        for (int j = 2; j <= tds.size(); j++) {
          Map<String, String> form = new HashMap<>();
          form.put("__EVENTVALIDATION", eventValidation);
          form.put("__VIEWSTATE", viewState);
          form.put("__EVENTARGUMENT", "Page$" + j);
          form.put("__EVENTTARGET", "ctl00$ContentPlaceHolderPrincipal$wucMensagens1$grdMensagens");

          Client.get()
                .loadMessagesPage(form, j);
        }
      }

      // Pula primeira e última linhas da tabela
      i++;
      size--;
    }

    for (; i < size; i++) {
      // Colunas da tabela
      Elements tds = trs.get(i)
                        .children();

      boolean seen = !trs.get(i)
                         .attributes()
                         .get("style")
                         .contains("bold");
      int uid = Integer.parseInt(tds.get(1)
                                    .text());
      boolean hasAtt = !tds.get(2)
                           .children()
                           .isEmpty();
      boolean isSolved = tds.get(3)
                            .child(0)
                            .tagName()
                            .equals("img");
      String subject = tds.get(4)
                          .text();
      String sender = tds.get(5)
                         .text();
      long date = getDate(tds.get(6)
                             .text());

      // Link para abrir mensagem
      String doPostBack = tds.get(4)
                             .child(0)
                             .attr("href");

      Log.d(subject, sender);
      Log.d(String.valueOf(uid), doPostBack);

      Sender search1 = null;
      Message search2 = null;

      try {
        search1 = senderBox.query()
                           .equal(Sender_.name_, sender, CASE_INSENSITIVE)
                           .build()
                           .findUnique();

        if (search1 == null) {
          search1 = new Sender(colors.getColor(), sender);
          senderBox.put(search1);
        }

        QueryBuilder<Message> builder = messageBox.query()
                                                  .equal(Message_.uid_, uid)
                                                  .and()
                                                  .equal(Message_.subject_, subject,
                                                         CASE_INSENSITIVE)
                                                  .and()
                                                  .between(Message_.date_, date, date);

        builder.link(Message_.sender)
               .equal(Sender_.id, search1.id);

        search2 = builder.build()
                         .findUnique();

      } catch (Exception e) {
        Log.e("MessageParser", sender);
        e.printStackTrace();
      }

      boolean isNew = false;

      if (search2 == null) {
        search2 = new Message(uid, date, subject, search1, hasAtt);
        isNew = true;
      }

      search2.setSeen(seen);
      search2.setSolved(isSolved);

      search1.messages.add(search2);
      messageBox.put(search2);

      if (notify && isNew)
        sendNotification(search2);
    }
  }

  private long getDate(String s) {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.HOUR_OF_DAY,
            Integer.parseInt(s.substring(s.lastIndexOf("/") + 6, s.indexOf(":"))));
    cal.set(Calendar.MINUTE, Integer.parseInt(s.substring(s.indexOf(":") + 1, s.lastIndexOf(":"))));
    cal.set(Calendar.SECOND, Integer.parseInt(s.substring(s.lastIndexOf(":") + 1)));
    cal.set(Calendar.MILLISECOND, 0);
    cal.set(Calendar.YEAR,
            Integer.parseInt(s.substring(s.lastIndexOf("/") + 1, s.lastIndexOf("/") + 5)));
    cal.set(Calendar.MONTH,
            Integer.parseInt(s.substring(s.indexOf("/") + 1, s.lastIndexOf("/"))) - 1);
    cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(s.substring(0, s.indexOf("/"))));

    return cal.getTimeInMillis();
  }

  private void sendNotification(Message message) {
    Intent intent = new Intent(App.getContext(), MessagesActivity.class);

    NotificationUtils.show(message.getSubject_(), message.sender.getTarget()
                                                                .getName_(),
                           MESSAGE, (int) message.id, intent);
  }

}
