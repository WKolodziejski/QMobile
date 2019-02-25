package com.tinf.qmobile.Parsers;

import android.os.AsyncTask;
import android.util.Log;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Class.Calendario.Dia;
import com.tinf.qmobile.Class.Calendario.Evento;
import com.tinf.qmobile.Class.Calendario.Mes;
import com.tinf.qmobile.Class.Materias.Matter;
import com.tinf.qmobile.Class.Materias.Matter_;
import com.tinf.qmobile.R;
import com.tinf.qmobile.Utilities.User;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import io.objectbox.Box;

import static com.tinf.qmobile.Network.Client.pos;
import static com.tinf.qmobile.Network.OnResponse.PG_CALENDARIO;
import static com.tinf.qmobile.Utilities.Utils.pickColor;

public class CalendarioParser extends AsyncTask<String, Void, Void> {
    private final static String TAG = "CalendarioParser";
    private OnFinish onFinish;
    private boolean notify;

    public CalendarioParser(boolean notify, OnFinish onFinish) {
        this.notify = notify;
        this.onFinish = onFinish;

        Log.i(TAG, "New instance");
    }

    @Override
    protected Void doInBackground(String... page) {
        App.getBox().runInTx(() -> {

            Log.i(TAG, "Parsing");

            Box<Mes> mesBox = App.getBox().boxFor(Mes.class);
            Box<Evento> eventoBox = App.getBox().boxFor(Evento.class);
            Box<Dia> diaBox = App.getBox().boxFor(Dia.class);
            Box<Matter> materiaBox = App.getBox().boxFor(Matter.class);

            mesBox.removeAll();
            eventoBox.removeAll();
            diaBox.removeAll();

            Document document = Jsoup.parse(page[0]);

            Elements meses = document.getElementsByTag("table").get(10).getElementsByTag("tbody").get(2).select("#AutoNumber3");

            List<Integer> mesesList = new ArrayList<>();

            Calendar today = Calendar.getInstance();
            today.setTime(new Date());

            boolean changeYear = false;

            for (int i = 0; i < meses.size(); i++) {

                String nomeMes = meses.get(i).previousElementSibling().previousElementSibling().getElementsByTag("div").get(0).text();

                int numMes = -1;

                switch (nomeMes) {
                    case "JANEIRO":
                        numMes = Calendar.JANUARY;
                        break;
                    case "FEVEREIRO":
                        numMes = Calendar.FEBRUARY;
                        break;
                    case "MARÇO":
                        numMes = Calendar.MARCH;
                        break;
                    case "ABRIL":
                        numMes = Calendar.APRIL;
                        break;
                    case "MAIO":
                        numMes = Calendar.MAY;
                        break;
                    case "JUNHO":
                        numMes = Calendar.JUNE;
                        break;
                    case "JULHO":
                        numMes = Calendar.JULY;
                        break;
                    case "AGOSTO":
                        numMes = Calendar.AUGUST;
                        break;
                    case "SETEMBRO":
                        numMes = Calendar.SEPTEMBER;
                        break;
                    case "OUTUBRO":
                        numMes = Calendar.OCTOBER;
                        break;
                    case "NOVEMBRO":
                        numMes = Calendar.NOVEMBER;
                        break;
                    case "DEZEMBRO":
                        numMes = Calendar.DECEMBER;
                        break;
                }

                //List<Dia> diaList = new ArrayList<>();

                        /*Elements tableLegenda = document.getElementsByTag("td").parents()
                                .get(0).children();*/

                String date = document.getElementsByTag("font").get(2).text();

                int year = Integer.parseInt(date.substring(date.lastIndexOf("/") + 1));

                if (!mesesList.isEmpty()) {
                    if (numMes < mesesList.get(mesesList.size() - 1)) {
                        changeYear = true;
                    }
                }

                if (changeYear) {
                    year++;
                }

                Mes mes = new Mes(numMes, year);

                Elements arrayEventos = new Elements();

                if (meses.get(i).nextElementSibling().childNodeSize() > 0) {
                    arrayEventos = meses.get(i).nextElementSibling().child(0).getElementsByTag("tr");
                }

                //Elements arrayEventos = meses.get(i).nextElementSibling().child(0).getElementsByTag("tr");
                Elements dias = meses.get(i).getElementsByTag("td");

                //no mes (cores)
                for (int j = 7; j < dias.size(); j++) {

                    //List<Evento> listEventos = new ArrayList<>();

                    String numeroDia = dias.get(j).text();

                    if (!numeroDia.equals("")) {

                        Dia dia = new Dia(Integer.parseInt(numeroDia));

                        String corQA = dias.get(j).attr("bgcolor"); // cor original

                        if (!corQA.equals("")) {

                            for (int k = 0; k < arrayEventos.size(); k++) {

                                String diaEvento = arrayEventos.get(k).child(0).text();

                                //Eventos normais
                                if (diaEvento.equals(numeroDia)) {

                                    Calendar ev = Calendar.getInstance();
                                    ev.set(Calendar.YEAR, mes.getYear());
                                    ev.set(Calendar.MONTH, mes.getMonth());
                                    ev.set(Calendar.DAY_OF_MONTH, dia.getDay());

                                    String infos = arrayEventos.get(k).child(1).text();
                                    String description = infos.substring(infos.lastIndexOf(") ") + 1).trim();
                                    String title = infos.substring(0, infos.indexOf(" (") + 1).trim();
                                    //title = title.substring(0 , title.lastIndexOf(" ") + 1).trim();

                                    if (title.equals("")){
                                        title = description;
                                        description = "";
                                    }

                                    int cor = corQA.equals("#F0F0F0") ? pickColor(title) : App.getContext().getResources().getColor(R.color.colorAccent);

                                    Evento evento = new Evento(title, description, cor, false);

                                    if (ev.getTimeInMillis() <= today.getTimeInMillis()) {
                                        evento.setHappened(true);
                                    }

                                    Matter materia = materiaBox.query()
                                            .equal(Matter_.title, title).and()
                                            .equal(Matter_.year, User.getPeriod(pos)).and()
                                            .equal(Matter_.period, User.getPeriod(pos))
                                            .build().findFirst();

                                    evento.day.setTarget(dia);
                                    dia.eventos.add(evento);

                                    if (materia != null) {
                                        evento.materia.setTarget(materia);
                                        materia.eventos.add(evento);
                                        materiaBox.put(materia);
                                        //Log.v("Box for matter", "size of " + materiaBox.count());
                                    }
                                    eventoBox.put(evento);
                                }

                                //Eventos com mais de um day.
                                if (diaEvento.contains(" ~ ")){
                                    String data_inicio = diaEvento.substring(0,diaEvento.indexOf(" ~"));
                                    String data_fim =  diaEvento.substring(diaEvento.indexOf("~ ")+2);
                                    diaEvento = data_inicio.substring(0,data_inicio.indexOf("/"));

                                    if (diaEvento.equals(numeroDia)) {
                                        String infos = arrayEventos.get(k).child(1).text();
                                        String description =  data_inicio + " - " + data_fim;

                                        //String title =  infos.substring(infos.lastIndexOf(")") + 1).trim();
                                        //title = title.substring(0 , title.lastIndexOf(" ") + 1).trim();

                                        Evento evento = new Evento(infos, description, App.getContext().getResources().getColor(R.color.colorAccent), data_inicio, data_fim, false);
                                        //Color.argb(255, 0, 255, 0),data_inicio,data_fim);

                                        Calendar ev = Calendar.getInstance();
                                        ev.set(Calendar.YEAR, mes.getYear());
                                        ev.set(Calendar.MONTH, mes.getMonth());
                                        ev.set(Calendar.DAY_OF_MONTH, dia.getDay());

                                        if (ev.getTimeInMillis() <= today.getTimeInMillis()) {
                                            evento.setHappened(true);
                                        }

                                        evento.day.setTarget(dia);
                                        dia.eventos.add(evento);
                                        eventoBox.put(evento);
                                        //Log.v("Box for eventos", "size of " + eventoBox.count());
                                    }
                                }
                            }
                        }
                        dia.mes.setTarget(mes);
                        diaBox.put(dia);
                        mes.days.add(dia);
                        //Log.v("Box for dias", "size of " + diaBox.count());
                    }
                }
                mesBox.put(mes);
                mesesList.add(mes.getMonth());
                //Log.v("Box for meses", "size of " + mesBox.count());
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
