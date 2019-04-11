package com.tinf.qmobile.parser;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import com.tinf.qmobile.App;
import com.tinf.qmobile.model.matter.Journal;
import com.tinf.qmobile.model.matter.Journal_;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Period;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.network.OnEvent;
import com.tinf.qmobile.R;
import com.tinf.qmobile.utility.Jobs;
import com.tinf.qmobile.utility.User;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.List;
import java.util.Objects;
import io.objectbox.Box;
import static com.tinf.qmobile.network.OnResponse.PG_DIARIOS;
import static com.tinf.qmobile.utility.Utils.getDate;
import static com.tinf.qmobile.utility.Utils.pickColor;

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

        Log.i(TAG, "New instance");
    }

    @Override
    protected Void doInBackground(String... page) {
        App.getBox().runInTx(() -> {

            Log.i(TAG, "Parsing");

            Box<Matter> matterBox = App.getBox().boxFor(Matter.class);
            Box<Period> periodBox = App.getBox().boxFor(Period.class);
            Box<Journal> journalBox = App.getBox().boxFor(Journal.class);

            Document document = Jsoup.parse(page[0]);

            Elements tableMatters = document.getElementsByTag("tbody").eq(12);

            int size = tableMatters.select("table.conteudoTexto").size();

            Element nxtElem = null;

            Elements dates = document.getElementsByTag("option");

            String[] years = new String[dates.size() - 1];

            for (int i = 0; i < dates.size() - 1; i++) {
                years[i] = dates.get(i + 1).text();
            }

            User.setYears(years);

            for (int i = 0; i < size; i++) {
                if (tableMatters.select("table.conteudoTexto").eq(i).parents().eq(0).parents().eq(0).next().eq(0) != null) {
                    nxtElem = tableMatters.select("table.conteudoTexto").eq(i).parents().eq(0).parents().eq(0).next().eq(0).first();
                }

                String matterTitle = tableMatters.select("table.conteudoTexto").eq(i).parents().eq(0).parents().eq(0).first().child(0).text();
                matterTitle = matterTitle.substring(matterTitle.indexOf("-") + 2, matterTitle.indexOf("("));
                matterTitle = matterTitle.substring(matterTitle.indexOf("-") + 2);

                Matter matter = matterBox.query()
                        .equal(Matter_.title, matterTitle.trim()).and()
                        .equal(Matter_.year, User.getYear(pos)).and()
                        .equal(Matter_.period, User.getPeriod(pos))
                        .build().findFirst();

                if (matter == null) {
                    matter = new Matter(matterTitle.trim(), pickColor(matterTitle), User.getYear(pos), User.getPeriod(pos));
                    for (int j = 0; j < Period.Type.values().length; j++) {
                        Period period = new Period(Period.Type.values()[j].get());
                        period.matter.setTarget(matter);
                        matter.periods.add(period);
                        periodBox.put(period);
                    }
                }

                String periodTitle = "";
                int periodType = 0;

                if (nxtElem != null) {
                    periodTitle = nxtElem.child(0).child(0).ownText();
                }

                while (periodTitle.contains("Etapa")) {
                    if (periodTitle.equals("1a. Etapa") || periodTitle.equals("1ª Etapa")) {
                        periodType = Period.Type.PRIMEIRA.get();
                    } else if (periodTitle.equals("1a Reavaliação da 1a Etapa") || periodTitle.equals("1ª Reavaliação da 1ª Etapa")) {
                        periodType =Period.Type.PRIMEIRA_RP1.get();
                    } else if (periodTitle.equals("2a Reavaliação da 1a Etapa") || periodTitle.equals("2ª Reavaliação da 1ª Etapa")) {
                        periodType = Period.Type.PRIMEIRA_RP2.get();
                    } else if (periodTitle.equals("2a. Etapa") || periodTitle.equals("2ª Etapa")) {
                        periodType = Period.Type.SEGUNDA.get();
                    } else if (periodTitle.equals("1a Reavaliação da 2a Etapa") || periodTitle.equals("1ª Reavaliação da 2ª Etapa")) {
                        periodType = Period.Type.SEGUNDA_RP1.get();
                    } else if (periodTitle.equals("2a Reavaliação da 2a Etapa") || periodTitle.equals("2ª Reavaliação da 2ª Etapa")) {
                        periodType = Period.Type.SEGUNDA_RP2.get();
                    }

                    Element tableGrades = Objects.requireNonNull(nxtElem).child(0).child(1).child(0);
                    Elements rowGrades = tableGrades.getElementsByClass("conteudoTexto");
                    nxtElem = nxtElem.nextElementSibling();

                    List<Period> periods = matter.periods;

                    Period period = null;

                    for (int j = 0; j < periods.size(); j++) {
                        if (periods.get(j).getTitle() == periodType) {
                            period = periodBox.get(periods.get(j).id);
                            break;
                        }
                    }

                    if (period == null) {
                        period = new Period(periodType);
                    }

                    if (nxtElem != null) {
                        periodTitle = nxtElem.child(0).child(0).text();
                    } else {
                        periodTitle = "";
                    }

                    for (int j = 0; j < rowGrades.size(); j++) {
                        String dateString = rowGrades.eq(j).first().child(1).text().substring(0, 10).trim();
                        String title = rowGrades.eq(j).first().child(1).text();

                        int type = Journal.Type.AVALIACAO.get();

                        if (title.contains("Prova")) {
                            type = Journal.Type.PROVA.get();
                        } else if (title.contains("Diarios") || title.contains("Trabalho")) {
                            type = Journal.Type.TRABALHO.get();
                        } else if (title.contains("Qualitativa")) {
                            type = Journal.Type.QUALITATIVA.get();
                        } else if (title.contains("Exercício")) {
                            type = Journal.Type.EXERCICIO.get();
                        }

                        String caps = trimp(trim1(title));
                        title = caps.substring(1, 2).toUpperCase() + caps.substring(2);
                        String weightString = formatGrade(trimp(rowGrades.eq(j).first().child(2).text()));
                        String maxString = formatGrade(trimp(rowGrades.eq(j).first().child(3).text()));
                        String gradeString = formatGrade(trimp(rowGrades.eq(j).first().child(4).text()));

                        float grade, weight, max;
                        long date;

                        grade = gradeString.isEmpty() ? -1 : Float.parseFloat(gradeString);

                        weight = weightString.isEmpty() ? -1 : Float.parseFloat(weightString);

                        max = maxString.isEmpty() ? -1 : Float.parseFloat(maxString);

                        date = getDate(dateString, false);

                        Journal newJournal = new Journal(title, grade, weight, max, date, type);

                        Journal search = journalBox.query().equal(Journal_.title, title).and()
                                .equal(Journal_.date, date).and().equal(Journal_.type, type).and()
                                .between(Journal_.grade, grade, grade).build().findFirst();

                        if (search == null) {
                            newJournal.period.setTarget(period);
                            period.journals.add(newJournal);
                            journalBox.put(newJournal);

                            count++;

                            if (notify) {
                                Intent intent = new Intent();
                                intent.putExtra("ID", matter.id);

                                Jobs.displayNotification(matter.getTitle(), newJournal.getTitle(),
                                        App.getContext().getResources().getString(R.string.title_diarios), (int) newJournal.id, intent.getExtras());
                            }
                        }
                    }
                    period.matter.setTarget(matter);
                    matter.periods.add(period);
                    periodBox.put(period);
                }
                matterBox.put(matter);
            }
        });

        return null;
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

    private String trimp(String string) {
        string = string.substring(string.indexOf(":"));
        string = string.replaceFirst(":", "");
        return string;
    }

    private String trim1(String string) {
        string = string.substring(string.indexOf(", ") + 2);
        return string;
    }

    private String formatGrade(String text){
        return text.startsWith(",") ? "" : text.replaceAll(",", ".");
    }

    /*private String trimb(String string) {
        string = string.substring(0, 4);
        return string;
    }*/

}
