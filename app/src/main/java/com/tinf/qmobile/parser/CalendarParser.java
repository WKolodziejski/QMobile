package com.tinf.qmobile.parser;

import android.util.Log;

import com.tinf.qmobile.model.calendar.EventSimple;
import com.tinf.qmobile.model.calendar.EventSimple_;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Calendar;

public class CalendarParser extends BaseParser {
    private final static String TAG = "CalendarioParser";

    public CalendarParser(int page, int year, int period, boolean notify, BaseParser.OnFinish onFinish, OnError onError) {
        super(page, year, period, notify, onFinish, onError);
    }

    @Override
    public void parse(Document document) {
        Log.i(TAG, "Parsing");

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

                                long date = getDate(day + "/" + monthCurrent + "/" + year, false);

                                String title = events.get(k).child(1).text().trim();

                                if (!title.contains("'")) {

                                    EventSimple search = eventSimpleBox.query().equal(EventSimple_.title, title).and()
                                            .between(EventSimple_.startTime, date, date).build().findFirst();

                                    if (search == null)
                                        eventSimpleBox.put(new EventSimple(title, date));
                                }

                            } else if (eventDay.contains("~")) {

                                String startTime = eventDay.substring(0, eventDay.indexOf(" ~"));
                                String endTime =  eventDay.substring(eventDay.indexOf("~ ") + 2);
                                eventDay = startTime.substring(0, startTime.indexOf("/"));

                                if (eventDay.equals(day)) {

                                    String title = events.get(k).child(1).text().trim();

                                    long start = getDate(startTime + "/" + year, false);
                                    long end = getDate(endTime + "/" + year, false);

                                    EventSimple search = eventSimpleBox.query()
                                            .equal(EventSimple_.title, title).and()
                                            .between(EventSimple_.startTime, start, start).and()
                                            .between(EventSimple_.endTime, end, end)
                                            .build().findFirst();

                                    if (search == null)
                                        eventSimpleBox.put(new EventSimple(title, start, end));
                                }
                            }
                        }
                    }
                }
            }

            if (i == 0) {
                Element yearStart = document.getElementsByTag("table").get(6).getElementsByTag("table").get(4).getElementsByTag("td").get(8).child(0);

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(getDate(yearStart.text(), true));
                cal.set(Calendar.MILLISECOND, 1);

                int title = EventSimple.Type.INICIO.get();
                long date = cal.getTimeInMillis();

                EventSimple search = eventSimpleBox.query().equal(EventSimple_.type, title).and()
                        .between(EventSimple_.startTime, date, date).build().findFirst();

                if (search == null) {
                    eventSimpleBox.put(new EventSimple(title, date));
                }

            } else if (i == months.size() - 1) {
                Element yearEnd = document.getElementsByTag("table").get(6).getElementsByTag("table").get(4).getElementsByTag("td").get(10).child(0);

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(getDate(yearEnd.text(), false));
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                cal.set(Calendar.MILLISECOND, 999);

                int title = EventSimple.Type.FIM.get();
                long date = cal.getTimeInMillis();

                EventSimple search = eventSimpleBox.query().equal(EventSimple_.type, title).and()
                        .between(EventSimple_.startTime, date, date).build().findFirst();

                if (search == null) {
                    eventSimpleBox.put(new EventSimple(title, date));
                }
            }
        }
    }

    private long getDate(String s, boolean isMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, isMonth ? 0 : 12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.YEAR, Integer.parseInt(s.substring(s.lastIndexOf("/") + 1, s.lastIndexOf("/") + 5)));
        cal.set(Calendar.MONTH, Integer.parseInt(s.substring(s.indexOf("/") + 1, s.lastIndexOf("/"))) - 1);
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(s.substring(0, s.indexOf("/"))));

        return cal.getTimeInMillis();
    }

}
