package com.tinf.qmobile.parser;

import android.os.AsyncTask;
import android.util.Log;

import com.tinf.qmobile.App;
import com.tinf.qmobile.model.matter.Schedule;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.utility.User;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.objectbox.Box;

import static com.tinf.qmobile.network.OnResponse.PG_HORARIO;

public class HorarioParser extends AsyncTask<String, Void, Void> {
    private final static String TAG = "HorarioParser";
    private OnFinish onFinish;
    private int pos;
    private boolean notify;

    public HorarioParser(int pos, boolean notify, OnFinish onFinish) {
        this.pos = pos;
        this.notify = notify;
        this.onFinish = onFinish;

        Log.i(TAG, "New instance");
    }

    @Override
    protected Void doInBackground(String... page) {
        App.getBox().runInTx(() -> {

            Log.i(TAG, "Parsing " + User.getYear(pos));

            Box<Matter> materiaBox = App.getBox().boxFor(Matter.class);
            Box<Schedule> horarioBox = App.getBox().boxFor(Schedule.class);

            Document document = Jsoup.parse(page[0]);

            Elements tables = document.select("table").get(11).children();

            Log.e("HORARIO", String.valueOf(tables.select("tr").size()));

            Elements codes = document.select("table").get(12).children();

            String[] code = null;

            for (Element table : codes) {
                Elements trs = table.select("tr");
                code = new String[trs.size()];
                for (int i = 0; i < trs.size(); i++) {
                    code[i] = trs.get(i).text();
                }
            }

            String[][] trtd_horario = null;

            for (Element table : tables) {
                Elements trs = table.select("tr");
                trtd_horario = new String[trs.size()][]; //pega total de colunas

                for (int i = 0; i < trs.size(); i++) {
                    Elements tds = trs.get(i).select("td");
                    trtd_horario[i] = new String[tds.size()]; // pega total de linhas

                    for (int j = 0; j < tds.size(); j++) {
                        trtd_horario[i][j] = tds.get(j).text();

                        for (int k = 1; k < code.length; k++) {
                            /*String sub = code[k].substring(0, code[k].indexOf("-") + 1);
                            sub = sub.substring(0, sub.lastIndexOf(" ") + 1);

                            String recebe = code[k].substring(code[k].indexOf("-"));
                            recebe = recebe.substring(recebe.indexOf("-"));
                            recebe = recebe.substring(recebe.indexOf("-") + 2);
                            recebe = recebe.substring(recebe.indexOf("-") + 2, recebe.lastIndexOf("-"));*/

                            String title = code[k].substring(0, code[k].lastIndexOf("-"));

                            String sign = title.substring(0, title.lastIndexOf("-"));
                            sign = sign.substring(0, sign.lastIndexOf("-")).trim();

                            title = title.substring(title.lastIndexOf("-") + 1).trim();

                            if ((trtd_horario[i][j]).contains(sign)) {
                                trtd_horario[i][j] = title;
                            }

                            if (((trtd_horario[i][j]).contains("2ª-FEIRA"))) {
                                trtd_horario[i][j] = String.valueOf(Calendar.MONDAY);

                            } else if (((trtd_horario[i][j]).contains("3ª-FEIRA"))) {
                                trtd_horario[i][j] = String.valueOf(Calendar.TUESDAY);

                            } else if (((trtd_horario[i][j]).contains("4ª-FEIRA"))) {
                                trtd_horario[i][j] = String.valueOf(Calendar.WEDNESDAY);

                            } else if (((trtd_horario[i][j]).contains("5ª-FEIRA"))) {
                                trtd_horario[i][j] = String.valueOf(Calendar.THURSDAY);

                            } else if (((trtd_horario[i][j]).contains("6ª-FEIRA"))) {
                                trtd_horario[i][j] = String.valueOf(Calendar.FRIDAY);
                            }
                        }
                    }
                }
            }

            List<Schedule> olds = new ArrayList<>();
            List<Schedule> news = new ArrayList<>();

            List<Matter> materialist = materiaBox.query()
                    .equal(Matter_.year_, User.getYear(pos)).and()
                    .equal(Matter_.period_, User.getPeriod(pos))
                    .build().find();

                for (int i = 0; i < materialist.size(); i++) {
                    for (int j = 0; j < materialist.get(i).schedules.size(); j++) {
                        Schedule h = materialist.get(i).schedules.get(j);
                        if (h.isFromSite()) {
                            olds.add(h);
                            horarioBox.remove(h.id);
                        }
                    }
                    materialist.get(i).schedules.clear();
                }

            for (int i = 1; i <= 5; i++) {
                for (int j = 1; j < trtd_horario.length; j++) {
                    if (!trtd_horario[j][i].equals("")) {

                        Matter materia = materiaBox.query()
                                .equal(Matter_.title_, trtd_horario[j][i].trim()).and()
                                .equal(Matter_.year_, User.getYear(pos)).and()
                                .equal(Matter_.period_, User.getPeriod(pos))
                                .build().findFirst();

                        Schedule horario = new Schedule(Integer.valueOf(trtd_horario[0][i]) - 1, getStartHour(trtd_horario[j][0]),
                                getStartMinute(trtd_horario[j][0]), getEndHour(trtd_horario[j][0]), getEndMinute(trtd_horario[j][0]));
                        news.add(horario);

                        if (materia != null) {
                            horario.matter.setTarget(materia);
                            materia.schedules.add(horario);
                            horarioBox.put(horario);
                            materiaBox.put(materia);
                        }
                    }
                }
            }

            boolean hasChanged = false;
            int s = 0;

            if (olds.size() == news.size()) {
                for (int i = 0; i < olds.size(); i++) {
                    for (int j = 0; j < news.size(); j++) {
                        if (olds.get(i).getStartDay() == news.get(j).getStartDay()
                                && olds.get(i).getStartTime().equals(news.get(j).getStartTime())) {
                            s++;
                        }
                    }
                }
                if (s != olds.size()) {
                    hasChanged = true;
                }
            } else {
                hasChanged = true;
            }

            if (hasChanged && notify) {
                //TODO notificção
                /*Jobs.displayNotification(matter.getTitleString(), new_diario.getTitleString(),
                        App.getContext().getResources().getString(R.string.title_diarios), (int) new_diario.id, intent.getExtras());*/
            }
        });
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        onFinish.onFinish(PG_HORARIO, pos);
    }

    public interface OnFinish {
        void onFinish(int pg, int year);
    }

    private int getStartHour(String time) {
        return formatHour(formatStart(time));
    }

    private int getEndHour(String time) {
        return formatHour(formatEnd(time));
    }

    private int getStartMinute(String time) {
        return formatMinute(formatStart(time));
    }

    private int getEndMinute(String time) {
        return formatMinute(formatEnd(time));
    }

    private int formatHour(String string) {
        string = string.substring(0, string.indexOf(":"));
        return Integer.valueOf(string);
    }

    private int formatMinute(String string) {
        string = string.substring(string.indexOf(":") + 1);
        return Integer.valueOf(string);
    }

    private String formatStart(String string) {
        string = string.substring(0, string.indexOf("~"));
        return string;
    }

    private String formatEnd(String string) {
        string = string.substring(string.indexOf("~") + 1);
        return string;
    }

}