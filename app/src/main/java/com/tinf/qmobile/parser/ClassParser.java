package com.tinf.qmobile.parser;

import android.util.Log;

import com.tinf.qmobile.model.matter.Clazz;
import com.tinf.qmobile.model.matter.Clazz_;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.model.matter.Period;
import com.tinf.qmobile.model.matter.Period_;
import com.tinf.qmobile.network.Client;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Calendar;

import io.objectbox.exception.NonUniqueResultException;
import io.objectbox.query.QueryBuilder;

import static com.tinf.qmobile.network.OnResponse.INDEX;
import static com.tinf.qmobile.network.OnResponse.PG_JOURNALS;

public class ClassParser extends BaseParser {
    private final static String TAG = "ClassParser";
    private final Matter matter;

    public ClassParser(Matter matter, int page, int year, int period, boolean notify, BaseParser.OnFinish onFinish, OnError onError) {
        super(page, year, period, notify, onFinish, onError);
        this.matter = matter;
    }

    @Override
    public void parse(Document document) {
        Log.d(TAG, "Parsing");

        Elements as = document.getElementsByAttributeValue("href", Client.get().getURL() + INDEX + PG_JOURNALS + "&ANO_PERIODO=" + matter.getYear_() + "_" + matter.getPeriod_());

        if (!as.isEmpty()) {
            Elements tables = as.last().parent().getElementsByTag("table");

            for (Element table : tables) {
                if (!table.hasClass("conteudoTexto"))
                    continue;

                Elements trs = table.getElementsByTag("tr");
                String p = table.previousElementSibling().text();
                p = p.substring(p.indexOf("-") + 1).trim();

                String cs = trs.last().text();

                try {
                    QueryBuilder<Period> builder1 = periodBox.query()
                            .equal(Period_.title_, p);

                    builder1.link(Period_.matter)
                            .equal(Matter_.id, matter.id);

                    Period period = builder1.build().findUnique();

                    if (period != null) {
                        String teacher = "";

                        for (int i = 1; i < trs.size() - 2; i++) {
                            Elements els = trs.get(i).children();

                            String date = formatDate(els.get(0).text());
                            int classesCount = Integer.parseInt(els.get(2).text());
                            int absences = formatAbsences(els.get(3).text());
                            long dateLong = getDate(date);

                            if (els.size() == 5)
                                teacher = els.get(4).text();

                            String content = "";

                            if (cs.contains(date)) {
                                cs = cs.substring(cs.indexOf(date) + 1);
                                cs = cs.substring(cs.indexOf(":") + 1);

                                content = cs;

                                if (content.contains("Data"))
                                    content = content.substring(0, content.indexOf("Data")).trim();

                                if (content.startsWith("-"))
                                    content = content.substring(content.indexOf("-") + 1).trim();
                            }

                            try {

                                QueryBuilder<Clazz> builder2 = classBox.query()
                                        .equal(Clazz_.classesCount_, classesCount).and()
                                        .equal(Clazz_.teacher_, teacher).and()
                                        .between(Clazz_.date_, dateLong, dateLong);

                                builder2.link(Clazz_.period)
                                        .equal(Period_.id, period.id);

                                Clazz search = builder2.build().findUnique();

                                if (search == null) {
                                    Clazz clazz = new Clazz(dateLong, classesCount, absences, teacher, content, period);
                                    period.classes.add(clazz);
                                    classBox.put(clazz);
                                } else {
                                    search.setAbsences(absences);
                                    search.setContent(content);
                                    classBox.put(search);
                                }

                                matter.setTeacher(teacher);

                                periodBox.put(period);
                                matterBox.put(matter);
                            } catch (NonUniqueResultException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (NonUniqueResultException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String formatDate(String s) {
        return s.substring(0, s.indexOf(',')).trim();
    }

    private long getDate(String s) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.YEAR, Integer.parseInt(s.substring(s.lastIndexOf("/") + 1, s.lastIndexOf("/") + 5)));
        cal.set(Calendar.MONTH, Integer.parseInt(s.substring(s.indexOf("/") + 1, s.lastIndexOf("/"))) - 1);
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(s.substring(0, s.indexOf("/"))));

        return cal.getTimeInMillis();
    }

    private int formatAbsences(String s) {
        if (s.equalsIgnoreCase("P"))
            return 0;
        else
            return Integer.parseInt(s);
    }

}
