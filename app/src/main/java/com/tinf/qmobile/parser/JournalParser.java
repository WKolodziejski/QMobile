package com.tinf.qmobile.parser;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import com.tinf.qmobile.App;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.model.calendario.Base.CalendarBase;
import com.tinf.qmobile.model.matter.Clazz;
import com.tinf.qmobile.model.matter.Clazz_;
import com.tinf.qmobile.model.matter.Journal;
import com.tinf.qmobile.model.matter.Journal_;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Period;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.model.matter.Period_;
import com.tinf.qmobile.network.OnEvent;
import com.tinf.qmobile.R;
import com.tinf.qmobile.service.Jobs;
import com.tinf.qmobile.utility.User;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.Random;
import java.util.concurrent.Callable;

import io.objectbox.Box;
import io.objectbox.exception.NonUniqueResultException;
import io.objectbox.query.QueryBuilder;

import static com.tinf.qmobile.network.OnResponse.PG_DIARIOS;
import static com.tinf.qmobile.model.calendario.Utils.getDate;

public class JournalParser extends AsyncTask<String, Void, Void> {
    private final static String TAG = "JournalParser";
    private OnFinish onFinish;
    private OnEvent onEvent;
    private int pos, count;
    private boolean notify;

    public JournalParser(int pos, boolean notify, OnFinish onFinish, OnEvent onEvent) {
        this.pos = pos;
        this.notify = notify;
        this.onFinish = onFinish;
        this.onEvent = onEvent;
    }

    @Override
    protected Void doInBackground(String... page) {
        try {
            return App.getBox().callInTx(() -> {

                Log.i(TAG, "Parsing " + User.getYear(pos) + User.getPeriod(pos));

                Box<Matter> matterBox = App.getBox().boxFor(Matter.class);
                Box<Period> periodBox = App.getBox().boxFor(Period.class);
                Box<Journal> journalBox = App.getBox().boxFor(Journal.class);
                Box<Clazz> clazzBox = App.getBox().boxFor(Clazz.class);

                Document document = Jsoup.parse(page[0]);

                Elements tableMatters = document.getElementsByTag("tbody").eq(12);

                for (int i = 0; i < tableMatters.select("table.conteudoTexto").size(); i++) {
                    Element nxtElem = null;

                    if (tableMatters.select("table.conteudoTexto").eq(i).parents().eq(0).parents().eq(0).next().eq(0) != null) {
                        nxtElem = tableMatters.select("table.conteudoTexto").eq(i).parents().eq(0).parents().eq(0).next().eq(0).first();
                    }

                    String info = tableMatters.select("table.conteudoTexto").eq(i).parents().eq(0).parents().eq(0).first().child(0).text();
                    String titleMatter = formatTitle(info);
                    int qid = formatQid(info);

                    Log.d(TAG, titleMatter);

                    Matter matter = matterBox.query()
                            .equal(Matter_.title_, titleMatter).and()
                            .equal(Matter_.year_, User.getYear(pos)).and()
                            .equal(Matter_.period_, User.getPeriod(pos)).and()
                            .equal(Matter_.qid_, qid)
                            .build().findUnique();

                    if (matter == null) {
                        String clazzTitle = formatClazz(info);

                        Clazz clazz = clazzBox.query().equal(Clazz_.title_, clazzTitle).build().findUnique();

                        if (clazz == null) {
                            clazz = new Clazz(clazzTitle);
                        }

                        matter = new Matter(titleMatter, pickColor(titleMatter), User.getYear(pos), User.getPeriod(pos), qid);

                        matterBox.put(matter);
                        clazz.matters.add(matter);
                        clazzBox.put(clazz);
                        matter.setClazz(clazz);

                        String desc = formatDesc(info);
                        if (!desc.isEmpty()) {
                            matter.setDescription(desc);
                        }
                    }

                    int periodCount = 0;

                    while (nxtElem != null && nxtElem.child(0).child(0).is("div")) {
                        String periodTitle = nxtElem.child(0).child(0).ownText();
                        Element tableGrades = nxtElem.child(0).child(1).child(0);
                        Elements rowGrades = tableGrades.getElementsByClass("conteudoTexto");
                        nxtElem = nxtElem.nextElementSibling();

                        Period period = null;

                        if (matter.periods.size() > periodCount) {
                            period = matter.periods.get(periodCount);
                        }

                        if (periodTitle.contains("Exame") || periodTitle.contains("Reavaliação")) {
                            period = periodBox.get(matter.periods.get(periodCount - 1).id);
                        } else {
                            periodCount++;
                            if (period == null) {
                                period = new Period(periodTitle);
                            } else {
                                period.setTitle(periodTitle);
                            }
                        }

                        for (int j = 0; j < rowGrades.size(); j++) {
                            String dateString = formatDate(rowGrades.eq(j).first().child(1).text());
                            String infos = rowGrades.eq(j).first().child(1).text();
                            String t = formatType(infos);

                            int type = Journal.Type.AVALIACAO.get();

                            if (t.equals("Prova")) {
                                type = Journal.Type.PROVA.get();
                            } else if (t.equals("Diarios") || t.equals("Trabalho")) {
                                type = Journal.Type.TRABALHO.get();
                            } else if (t.contains("Qualitativa")) {
                                type = Journal.Type.QUALITATIVA.get();
                            } else if (t.equals("Exercício")) {
                                type = Journal.Type.EXERCICIO.get();
                            }

                            String title = formatJournalTitle(infos);
                            String weightString = formatGrade(formatNumber(rowGrades.eq(j).first().child(2).text()));
                            String maxString = formatGrade(formatNumber(rowGrades.eq(j).first().child(3).text()));
                            String gradeString = formatGrade(formatNumber(rowGrades.eq(j).first().child(4).text()));

                            float grade, weight, max;
                            long date;

                            grade = gradeString.isEmpty() ? -1 : Float.parseFloat(gradeString);

                            weight = weightString.isEmpty() ? -1 : Float.parseFloat(weightString);

                            max = maxString.isEmpty() ? -1 : Float.parseFloat(maxString);

                            date = dateString.isEmpty() ? -1 : getDate(dateString, false);

                            if (date != -1) {

                                Journal newJournal = new Journal(title, grade, weight, max, date, type, matter);

                                Log.d(TAG, title);

                                Journal search = null;

                                try {
                                    QueryBuilder<Journal> builder = journalBox.query()
                                            .equal(Journal_.title_, title).and()
                                            .equal(Journal_.type_, type).and()
                                            .between(Journal_.date_, date, date).and()
                                            .between(Journal_.weight_, weight, weight).and()
                                            .between(Journal_.max_, max, max);

                                    builder.link(Journal_.matter)
                                            .equal(Matter_.title_, titleMatter).and()
                                            .equal(Matter_.year_, User.getYear(pos)).and()
                                            .equal(Matter_.period_, User.getPeriod(pos)).and()
                                            .equal(Matter_.qid_, qid);

                                    search = builder.build().findUnique();
                                } catch (NonUniqueResultException e) {
                                    e.printStackTrace();
                                }

                                if (search == null) {
                                    newJournal.period.setTarget(period);
                                    period.journals.add(newJournal);
                                    journalBox.put(newJournal);

                                    count++;

                                    if (notify) {
                                        Intent intent = new Intent(App.getContext(), EventViewActivity.class);
                                        intent.putExtra("ID", newJournal.id);
                                        intent.putExtra("TYPE", CalendarBase.ViewType.JOURNAL);

                                        Jobs.displayNotification(matter.getTitle(), newJournal.getTitle(),
                                                App.getContext().getResources().getString(R.string.title_diarios), (int) newJournal.id, intent);
                                    }
                                } else {
                                    if (search.getGrade_() != grade) {
                                        search.setGrade(grade);
                                        journalBox.put(search);
                                        count++;

                                        if (notify) {
                                            Intent intent = new Intent(App.getContext(), EventViewActivity.class);
                                            intent.putExtra("ID", newJournal.id);
                                            intent.putExtra("TYPE", CalendarBase.ViewType.JOURNAL);

                                            Jobs.displayNotification(matter.getTitle(), newJournal.getTitle(),
                                                    App.getContext().getResources().getString(R.string.title_diarios), (int) newJournal.id, intent);
                                        }
                                    }
                                }
                            }
                        }
                        period.matter.setTarget(matter);
                        matter.periods.add(period);
                        periodBox.put(period);
                    }
                    matterBox.put(matter);
                }
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        onFinish.onFinish(PG_DIARIOS, pos);
        if (onEvent != null) {
            onEvent.onJournal(count);
        }
    }

    public interface OnFinish {
        void onFinish(int pg, int year);
    }

    private String formatNumber(String s) {
        return s.substring(s.indexOf(":") + 1).trim();
    }

    private String formatGrade(String s){
        return s.startsWith(",") ? "" : s.replaceAll(",", ".");
    }

    private String formatTitle(String s) {
        s = s.substring(s.indexOf("-") + 1, s.indexOf("("));
        s = s.substring(s.indexOf("-") + 1).trim();
        return s;
    }

    private int formatQid(String s) {
        return Integer.parseInt(s.substring(0, s.indexOf("-") - 1).trim());
    }

    private String formatClazz(String s) {
        s = s.substring(s.indexOf("-") + 1);
        s = s.substring(0, s.indexOf("-") - 1).trim();
        return s;
    }

    private String formatDesc(String s) {
        s = s.substring(s.indexOf("-") + 1, s.lastIndexOf("("));
        s = s.substring(s.indexOf("-") + 1);

        if (s.contains("(")) {
            return s.substring(s.indexOf("("), s.indexOf(")"));
        } else return "";
    }

    private String formatDate(String s) {
        return s.substring(0, s.indexOf(',')).trim();
    }

    private String formatType(String s) {
        return s.substring(s.indexOf(",") + 1, s.indexOf(':')).trim();
    }

    private String formatJournalTitle(String s) {
        return s.substring(s.indexOf(":") + 1).trim();
    }

    private int getRandomColorGenerator() {
        int color = new Random().nextInt(9);

        switch (color) {
            case 0: color = R.color.deep_orange_500;
                break;

            case 1: color = R.color.yellow_a700;
                break;

            case 2: color = R.color.lime_a700;
                break;

            case 3: color = R.color.light_green_500;
                break;

            case 4: color = R.color.teal_500;
                break;

            case 5: color = R.color.cyan_500;
                break;

            case 6: color = R.color.light_blue_500;
                break;

            case 7: color = R.color.indigo_500;
                break;

            case 8: color = R.color.deep_purple_500;
                break;
        }
        return App.getContext().getResources().getColor(color);
    }

    private int pickColor(String string) {
        int color = 0;

        if (string.contains("Biologia")) {
            color = R.color.biologia;
        } else if (string.contains("Educação Física")) {
            color = R.color.edFisica;
        } else if (string.contains("Filosofia")) {
            color = R.color.filosofia;
        } else if (string.contains("Física")) {
            color = R.color.fisica;
        } else if (string.contains("Geografia")) {
            color = R.color.geografia;
        } else if (string.contains("História")) {
            color = R.color.historia;
        } else if (string.contains("Portugu")) {
            color = R.color.portugues;
        } else if (string.contains("Matemática")) {
            color = R.color.matematica;
        } else if (string.contains("Química")) {
            color = R.color.quimica;
        } else if (string.contains("Sociologia")) {
            color = R.color.sociologia;
        } else {
            Matter matter = App.getBox().boxFor(Matter.class).query().equal(Matter_.title_, string).build().findFirst();

            if (matter != null) {
                color = matter.getColor();
            }

            if (color == 0) {
                color = getRandomColorGenerator();
            }

            return color;
        }
        return App.getContext().getResources().getColor(color);
    }

}
