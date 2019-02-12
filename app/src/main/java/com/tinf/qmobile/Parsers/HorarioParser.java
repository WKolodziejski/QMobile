package com.tinf.qmobile.Parsers;

import android.os.AsyncTask;
import android.util.Log;

import com.tinf.qmobile.App;
import com.tinf.qmobile.Class.Materias.Horario;
import com.tinf.qmobile.Class.Materias.Materia;
import com.tinf.qmobile.Class.Materias.Materia_;
import com.tinf.qmobile.Interfaces.OnResponse;
import com.tinf.qmobile.Network.Client;
import com.tinf.qmobile.Utilities.User;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import io.objectbox.Box;
import io.objectbox.BoxStore;

import static com.tinf.qmobile.Network.Client.PG_DIARIOS;
import static com.tinf.qmobile.Network.Client.PG_HORARIO;

public class HorarioParser extends AsyncTask<String, Void, Void> {
    private final static String TAG = "HorarioParser";
    private OnFinish onFinish;
    private int year;
    private boolean notify;

    public HorarioParser(int year, boolean notify, OnFinish onFinish) {
        this.year = year;
        this.notify = notify;
        this.onFinish = onFinish;

        Log.i(TAG, "New instance");
    }

    @Override
    protected Void doInBackground(String... page) {
        App.getBox().runInTx(() -> {

            Log.i(TAG, "Parsing");

            Box<Materia> materiaBox = App.getBox().boxFor(Materia.class);
            Box<Horario> horarioBox = App.getBox().boxFor(Horario.class);

            String[][] trtd_horario = null;
            String[] code = null;
            Document document = Jsoup.parse(page[0]);

            //Element table_horario = document.select("table").get(11).getElementsByTag("tbody").get(0);
            Element table_horario = document.select("table").eq(11).first();

            Element table_codes = document.select("table").get(12);
            Elements codes = table_codes.children();

            //Document ano = Jsoup.parse(document.select("#cmbanos").first().toString());
            //Elements options = ano.select("option");

                  /*webView.infos.data_horario = new String[options_ano.size()];

                            for (int i = 0; i < options_ano.size(); i++) {
                                webView.infos.data_horario[i] = options_ano.get(i).text();
                            }

                            Document periodo = Jsoup.parse(document.select("#cmbperiodos").first().toString());
                            Elements options_periodo = periodo.select("option");

                            webView.infos.periodo_horario = new String[options_periodo.size()];

                            for (int i = 0; i < options_periodo.size(); i++) {
                                webView.infos.periodo_horario[i] = options_periodo.get(i).text();
                            }*/

            for (Element table : codes) {
                Elements trs = table.select("tr");
                code = new String[trs.size()];
                for (int i = 0; i < trs.size(); i++) {
                    code[i] = trs.get(i).text();
                }
            }

            Elements tables = table_horario.children();

            for (Element table : tables) {
                Elements trs = table.select("tr");
                trtd_horario = new String[trs.size()][]; //pega total de colunas

                for (int i = 0; i < trs.size(); i++) {
                    Elements tds = trs.get(i).select("td");
                    trtd_horario[i] = new String[tds.size()]; // pega total de linhas

                    for (int j = 0; j < tds.size(); j++) {
                        trtd_horario[i][j] = tds.get(j).text();

                        for (int k = 1; k < Objects.requireNonNull(code).length; k++) {
                            String sub = code[k].substring(0, code[k].indexOf("-") + 1);
                            sub = sub.substring(0, sub.lastIndexOf(" ") + 1);

                            String recebe = code[k].substring(code[k].indexOf("-"));
                            recebe = recebe.substring(recebe.indexOf("-"));
                            recebe = recebe.substring(recebe.indexOf("-") + 2);
                            recebe = recebe.substring(recebe.indexOf("-") + 2, recebe.lastIndexOf("-"));

                            if ((trtd_horario[i][j]).contains(sub)) {
                                trtd_horario[i][j] = recebe;
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

            List<Materia> materialist = materiaBox.query().equal(Materia_.year,
                    User.getYear(year)).build().find();

            for (int i = 0; i < materialist.size(); i++) {
                for (int j = 0; j < materialist.get(i).horarios.size(); j++) {
                    horarioBox.remove(materialist.get(i).horarios.get(j).getId());
                }
                materialist.get(i).horarios.clear();
            }

            for (int i = 1; i <= 5; i++) {
                for (int j = 1; j < Objects.requireNonNull(trtd_horario).length; j++) {
                    if (!trtd_horario[j][i].equals("")) {

                        Materia materia = materiaBox.query()
                                .equal(Materia_.name, trtd_horario[j][i].trim())
                                .and()
                                .equal(Materia_.year, User.getYear(year))
                                .build().findFirst();

                        Horario horario = new Horario(Integer.valueOf(trtd_horario[0][i]), trtd_horario[j][0]);

                        if (materia != null) {
                            horario.materia.setTarget(materia);
                            materia.horarios.add(horario);
                            //materiaBox.put(materia);
                            horarioBox.put(horario);
                            //Log.v("Box for horario", "size of " + horarioBox.count());
                            //Log.v("Box for materia", "size of " + materiaBox.count());
                        }
                    }
                }
            }

        });

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        onFinish.onFinish(PG_HORARIO, year);
    }

    public interface OnFinish {
        void onFinish(int pg, int year);
    }
}
