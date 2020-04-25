package com.tinf.qmobile.parser;

import android.util.Log;

import com.tinf.qmobile.model.message.Attachment;
import com.tinf.qmobile.model.message.Message;
import com.tinf.qmobile.model.message.Message_;
import com.tinf.qmobile.model.message.Sender;
import com.tinf.qmobile.model.message.Sender_;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.RandomColor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.Calendar;
import io.objectbox.exception.NonUniqueResultException;
import io.objectbox.query.QueryBuilder;

public class MessageParser extends BaseParser {

    public MessageParser(OnFinish onFinish, OnError onError) {
        super(0, 0, false, onFinish, onError);
    }

    @Override
    public void parse(Document document) {
        Element textarea = document.getElementsByTag("textarea").first();

        Element table = document.getElementsByClass("txt").first().child(0);
        Elements trs = table.children();

        if (textarea == null) {
            //TODO handle page change

            RandomColor colors = new RandomColor();

            for (int i = 2; i < trs.size() - 1; i++) {
                Elements td = trs.get(i).children();

                int uid = Integer.parseInt(td.get(1).text());
                String subject = td.get(4).text();
                String sender = td.get(5).text();
                long date = getDate(td.get(6).text());

                try {

                    Sender search2 = senderBox.query()
                            .equal(Sender_.name_, sender)
                            .build().findUnique();

                    if (search2 == null)
                        search2 = new Sender(colors.getColor(), sender);

                    senderBox.put(search2);

                    QueryBuilder<Message> builder = messageBox.query()
                            .equal(Message_.uid_, uid).and()
                            .equal(Message_.subject_, subject).and()
                            .between(Message_.date_, date, date);

                    builder.link(Message_.sender)
                            .equal(Sender_.id, search2.id);

                    Message search1 = builder.build().findUnique();

                    if (search1 == null)
                        search1 = new Message(uid, date, subject, search2);

                    search2.messages.add(search1);
                    messageBox.put(search1);

                } catch (NonUniqueResultException e) {
                    e.printStackTrace();
                }
            }
        } else {
            //TODO handle message

            for (int i = 2; i < trs.size() - 1; i++) {
                if (trs.get(i).attributes().get("style").contains("#F2CFA5")) {
                    Elements td = trs.get(i).children();

                    int uid = Integer.parseInt(td.get(1).text());
                    String subject = td.get(4).text();
                    long date = getDate(td.get(6).text());

                    try {
                        Message search = messageBox.query()
                                .equal(Message_.uid_, uid).and()
                                .equal(Message_.subject_, subject).and()
                                .between(Message_.date_, date, date)
                                .build().findUnique();

                        if (search != null && search.attachments.isEmpty()) {
                            search.setText(textarea.text());

                            Element table2 = document.getElementsByClass("txt").get(1).child(0);
                            Elements trs2 = table2.children();

                            for (int j = 1; j < trs2.size(); j++) {
                                Elements td2 = trs2.get(j).children();

                                String title = td2.get(0).text();
                                String url = Client.get().getURL() + "/qacademicodotnet/" + td2.get(0).child(0).attr("href");
                                String obs = td2.get(1).text();

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

}
