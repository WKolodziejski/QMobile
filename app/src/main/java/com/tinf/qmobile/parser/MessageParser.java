package com.tinf.qmobile.parser;

import android.content.Intent;
import android.util.Log;

import com.tinf.qmobile.App;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.MessagesActivity;
import com.tinf.qmobile.model.message.Attachment;
import com.tinf.qmobile.model.message.Message;
import com.tinf.qmobile.model.message.Message_;
import com.tinf.qmobile.model.message.Sender;
import com.tinf.qmobile.model.message.Sender_;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.network.OnResponse;
import com.tinf.qmobile.network.message.OnMessages;
import com.tinf.qmobile.service.Jobs;
import com.tinf.qmobile.utility.RandomColor;
import com.tinf.qmobile.utility.User;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Calendar;

import io.objectbox.exception.NonUniqueResultException;
import io.objectbox.query.QueryBuilder;

import static com.tinf.qmobile.App.getContext;
import static com.tinf.qmobile.network.OnResponse.PG_ERROR;
import static com.tinf.qmobile.network.OnResponse.PG_MESSAGES;

public class MessageParser extends BaseParser {
    private final OnMessages onMessages;

    public MessageParser(OnMessages onMessages, OnResponse onResponse) {
        super(PG_MESSAGES, User.getYear(0), User.getPeriod(0), false, onResponse::onFinish, onResponse::onError);
        this.onMessages = onMessages;
    }

    public MessageParser(int pg, int year, int period, boolean notify, OnFinish onFinish, OnError onError) {
        super(pg, year, period, notify, onFinish, onError);
        onMessages = new OnMessages() {
            @Override
            public void onFinish(int pg, boolean hasMorePages) {

            }

            @Override
            public void onFinish() {

            }
        };
    }

    @Override
    public void parse(Document document) {
        Element t = document.getElementById("ctl00_ContentPlaceHolderPrincipal_UpdatePanel1");

        if (t != null)
            if (!t.text().contains("Mensagem")) {
                onError.onError(PG_ERROR, getContext().getString(R.string.login_expired));
                return;
            }

        Element textarea = document.getElementsByTag("textarea").first();

        Element table = document.getElementsByClass("txt").first().child(0);
        Elements trs = table.children();

        if (textarea == null) {
            Log.d("Parsing", "Page");

            RandomColor colors = new RandomColor();

            for (int i = 2; i < trs.size() - 1; i++) {
                Elements tds = trs.get(i).children();

                boolean seen = !trs.get(i).attributes().get("style").contains("bold");
                int uid = Integer.parseInt(tds.get(1).text());
                boolean hasAtt = !tds.get(2).children().isEmpty();
                boolean isSolved = tds.get(3).child(0).tagName().equals("img");
                String subject = tds.get(4).text();
                String sender = tds.get(5).text();
                long date = getDate(tds.get(6).text());

                try {
                    Sender search1 = senderBox.query()
                            .equal(Sender_.name_, sender)
                            .build().findUnique();

                    if (search1 == null) {
                        search1 = new Sender(colors.getColor(), sender);
                        senderBox.put(search1);
                    }

                    QueryBuilder<Message> builder = messageBox.query()
                            .equal(Message_.uid_, uid).and()
                            .equal(Message_.subject_, subject).and()
                            .between(Message_.date_, date, date);

                    builder.link(Message_.sender)
                            .equal(Sender_.id, search1.id);

                    Message search2 = builder.build().findUnique();

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

                } catch (NonUniqueResultException e) {
                    e.printStackTrace();
                }
            }

            Element footer = trs.last();
            Element tbody = footer.getElementsByTag("tbody").first();

            if (tbody.childNodeSize() > 0) {
                Elements tds = tbody.child(0).children();

                for (int i = 0; i < tds.size(); i++) {
                    if (tds.get(i).child(0).tagName().equals("span")) {
                        onMessages.onFinish(Integer.parseInt(tds.get(i).child(0).text()), i < tds.size() - 1);
                        break;
                    }
                }
            }
        } else {
            Log.d("Parsing", "Message");

            for (int i = 2; i < trs.size() - 1; i++) {
                if (trs.get(i).attributes().get("style").contains("#F2CFA5")) {
                    Elements tds = trs.get(i).children();

                    int uid = Integer.parseInt(tds.get(1).text());
                    String subject = tds.get(4).text();
                    long date = getDate(tds.get(6).text());

                    try {
                        Message search = messageBox.query()
                                .equal(Message_.uid_, uid).and()
                                .equal(Message_.subject_, subject).and()
                                .between(Message_.date_, date, date)
                                .build().findUnique();

                        if (search != null && search.attachments.isEmpty()) {
                            String txt = textarea.text();
                            search.setText(txt == null ? "" : txt);

                            Element table2 = document.getElementById("ctl00_ContentPlaceHolderPrincipal_wucMensagens1_wucExibirMensagem1_grdAnexos");
                            Elements trs2 = table2.getElementsByTag("tbody").first().children();

                            for (int j = 1; j < trs2.size(); j++) {
                                Elements tds2 = trs2.get(j).children();

                                String title = tds2.get(0).text();
                                String url = Client.get().getURL() + "/qacademicodotnet/" + tds2.get(0).child(0).attr("href");
                                String obs = tds2.get(1).text();

                                Log.d(title, url);

                                Attachment attachment = new Attachment(title, obs, url);
                                attachment.message.setTarget(search);

                                attachmentBox.put(attachment);

                                search.attachments.add(attachment);
                            }

                            messageBox.put(search);
                        }
                    } catch (NonUniqueResultException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
            onMessages.onFinish();
        }
    }

    private long getDate(String s) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(s.substring(s.lastIndexOf("/") + 6, s.indexOf(":"))));
        cal.set(Calendar.MINUTE, Integer.parseInt(s.substring(s.indexOf(":") + 1, s.lastIndexOf(":"))));
        cal.set(Calendar.SECOND, Integer.parseInt(s.substring(s.lastIndexOf(":") + 1)));
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.YEAR, Integer.parseInt(s.substring(s.lastIndexOf("/") + 1, s.lastIndexOf("/") + 5)));
        cal.set(Calendar.MONTH, Integer.parseInt(s.substring(s.indexOf("/") + 1, s.lastIndexOf("/"))) - 1);
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(s.substring(0, s.indexOf("/"))));

        return cal.getTimeInMillis();
    }

    private void sendNotification(Message message) {
        Intent intent = new Intent(App.getContext(), MessagesActivity.class);

        Jobs.displayNotification(App.getContext(), message.getSubject_(), message.sender.getTarget().getName_(),
                App.getContext().getResources().getString(R.string.title_messages), (int) message.id, intent);
    }

}
