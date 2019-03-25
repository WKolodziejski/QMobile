package com.tinf.qmobile.Parsers;

import android.os.AsyncTask;
import android.util.Log;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Class.Calendario.CalendarBase;
import com.tinf.qmobile.Class.Calendario.Event;
import com.tinf.qmobile.Class.Calendario.Event_;
import com.tinf.qmobile.Class.Calendario.Month;
import com.tinf.qmobile.Class.Calendario.Month_;
import com.tinf.qmobile.Class.Materias.Matter;
import com.tinf.qmobile.Class.Materias.Matter_;
import com.tinf.qmobile.R;
import com.tinf.qmobile.Utilities.User;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Calendar;

import io.objectbox.Box;
import io.objectbox.BoxStore;

import static com.tinf.qmobile.Network.OnResponse.PG_CALENDARIO;
import static com.tinf.qmobile.Utilities.Utils.getDate;

public class CalendarioParser extends AsyncTask<String, Void, Void> {
    private final static String TAG = "CalendarioParser";
    private OnFinish onFinish;
    private int pos;
    private boolean notify;

    public CalendarioParser(int pos, boolean notify, OnFinish onFinish) {
        this.pos = pos;
        this.notify = notify;
        this.onFinish = onFinish;

        Log.i(TAG, "New instance");
    }

    @Override
    protected Void doInBackground(String... page) {
        App.getBox().runInTx(() -> {

            Log.i(TAG, "Parsing");

            Box<Event> eventBox = App.getBox().boxFor(Event.class);
            Box<Matter> matterBox = App.getBox().boxFor(Matter.class);
            Box<Month> monthBox = App.getBox().boxFor(Month.class);

            Document document = Jsoup.parse(page[0]);

            Elements months = document.getElementsByTag("table").get(10).getElementsByTag("tbody").get(2).select("#AutoNumber3");

            int monthLast = -1;
            boolean shouldAddYear = false;

            for (int i = 0; i < months.size(); i++) {

                String monthName = months.get(i).previousElementSibling().previousElementSibling().getElementsByTag("div").get(0).text();

                int monthCurrent = -1;

                switch (monthName) {
                    case "JANEIRO":
                        monthCurrent = 1;
                        break;
                    case "FEVEREIRO":
                        monthCurrent = 2;
                        break;
                    case "MARÇO":
                        monthCurrent = 3;
                        break;
                    case "ABRIL":
                        monthCurrent = 4;
                        break;
                    case "MAIO":
                        monthCurrent = 5;
                        break;
                    case "JUNHO":
                        monthCurrent = 6;
                        break;
                    case "JULHO":
                        monthCurrent = 7;
                        break;
                    case "AGOSTO":
                        monthCurrent = 8;
                        break;
                    case "SETEMBRO":
                        monthCurrent = 9;
                        break;
                    case "OUTUBRO":
                        monthCurrent = 10;
                        break;
                    case "NOVEMBRO":
                        monthCurrent = 11;
                        break;
                    case "DEZEMBRO":
                        monthCurrent = 12;
                        break;
                }

                String dateString = document.getElementsByTag("font").get(2).text();

                int year = Integer.parseInt(dateString.substring(dateString.lastIndexOf("/") + 1));

                if (monthCurrent < monthLast) {
                    shouldAddYear = true;
                }

                monthLast = monthCurrent;

                if (shouldAddYear) {
                    year++;
                }

                Month month = new Month(getDate("1" + "/" + monthCurrent + "/" + year));

                Elements events = new Elements();

                if (months.get(i).nextElementSibling().childNodeSize() > 0) {
                    events = months.get(i).nextElementSibling().child(0).getElementsByTag("tr");
                }

                Elements days = months.get(i).getElementsByTag("td");

                for (int j = 7; j < days.size(); j++) {

                    String day = days.get(j).text();

                    if (!day.equals("")) {

                        String bgcolor = days.get(j).attr("bgcolor");

                        if (!bgcolor.isEmpty()) {

                            for (int k = 0; k < events.size(); k++) {

                                String eventDay = events.get(k).child(0).text();

                                if (eventDay.equals(day)) {

                                    long date = getDate(day + "/" + monthCurrent + "/" + year);

                                    String infos = events.get(k).child(1).text();
                                    String description = infos.substring(infos.lastIndexOf(") ") + 1).trim();
                                    String title = infos.substring(0, infos.indexOf(" (") + 1).trim();

                                    boolean hasDescription = true;

                                    if (title.isEmpty()){
                                        title = description;
                                        hasDescription = false;
                                    }

                                    Event search = eventBox.query().equal(Event_.title, title).and()
                                            .between(Event_.startTime, date, date).and()
                                            .equal(Event_.isFromSite, true).build().findFirst();

                                    if (search == null) {

                                        Event event = new Event(title, date, true);
                                        event.setColor(App.getContext().getResources().getColor(R.color.colorPrimary));

                                        if (hasDescription) {
                                            event.setDescription(description);
                                        }

                                        Matter matter = matterBox.query()
                                                .equal(Matter_.title, title).and()
                                                .equal(Matter_.year, User.getYear(pos)).and()
                                                .equal(Matter_.period, User.getPeriod(pos))
                                                .build().findFirst();

                                        if (matter != null) {
                                            event.matter.setTarget(matter);
                                            matter.events.add(event);
                                            matterBox.put(matter);
                                        }

                                        eventBox.put(event);
                                        month.events.add(event);
                                    }

                                } else if (eventDay.contains("~")) {

                                    String startTime = eventDay.substring(0, eventDay.indexOf(" ~"));
                                    String endTime =  eventDay.substring(eventDay.indexOf("~ ") + 2);
                                    eventDay = startTime.substring(0, startTime.indexOf("/"));

                                    if (eventDay.equals(day)) {

                                        String title = events.get(k).child(1).text();

                                        long start = getDate(startTime + "/" + year);
                                        long end = getDate(endTime + "/" + year);

                                        Event search = eventBox.query().equal(Event_.title, title).and()
                                                .between(Event_.startTime, start, start).and()
                                                .between(Event_.endTime, end, end).and()
                                                .equal(Event_.isFromSite, true).build().findFirst();

                                        if (search == null) {

                                            Event event = new Event(title, start, true);
                                            event.setEndTime(end);
                                            event.setColor(App.getContext().getResources().getColor(R.color.colorPrimary));

                                            eventBox.put(event);
                                            month.events.add(event);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Month search = monthBox.query().between(Month_.date, month.getDate(), month.getDate()).build().findFirst();

                if (search == null) {
                    monthBox.put(month);
                }
            }
        });

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        onFinish.onFinish(PG_CALENDARIO, 0);
    }

    public interface OnFinish {
        void onFinish(int pg, int year);
    }

}
