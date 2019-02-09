package com.tinf.qmobile.Parsers;

import android.os.AsyncTask;
import android.util.Log;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Class.Calendario.Dia;
import com.tinf.qmobile.Class.Calendario.Evento;
import com.tinf.qmobile.Class.Calendario.Mes;
import com.tinf.qmobile.Class.Materias.Materia;
import com.tinf.qmobile.Class.Materias.Materia_;
import com.tinf.qmobile.Interfaces.OnResponse;
import com.tinf.qmobile.R;
import com.tinf.qmobile.Utilities.User;
import com.tinf.qmobile.Utilities.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import io.objectbox.Box;

import static com.tinf.qmobile.Network.Client.PG_CALENDARIO;

public class CalendarioParser extends AsyncTask<String, Void, Void> {
    private final static String TAG = "CalendarioParser";
    private boolean notify;
    private OnResponse onResponse;

    public CalendarioParser(boolean notify, OnResponse onResponse) {
        this.notify = notify;
        this.onResponse = onResponse;

        Log.i(TAG, "New instance");
    }

    @Override
    protected Void doInBackground(String... page) {
        App.getBox().runInTx(() -> {

            Log.i(TAG, "Parsing");

            Box<Mes> mesBox = App.getBox().boxFor(Mes.class);
            Box<Evento> eventoBox = App.getBox().boxFor(Evento.class);
            Box<Dia> diaBox = App.getBox().boxFor(Dia.class);
            Box<Materia> materiaBox = App.getBox().boxFor(Materia.class);

            mesBox.removeAll();
            eventoBox.removeAll();
            diaBox.removeAll();

            Document document = Jsoup.parse(page[0]);

            Elements meses = document.getElementsByTag("table").get(10).getElementsByTag("tbody").get(2).select("#AutoNumber3");
            //Elements infos = document.getElementsByTag("table").get(10).getElementsByTag("tbody").get(2).select("#AutoNumber3");

            //webView.data_calendario = trimb(document.getElementsByClass("dado_cabecalho").get(1).text());

            List<Integer> mesesList = new ArrayList<>();

            Calendar today = Calendar.getInstance();
            today.setTime(new Date());

            boolean changeYear = false;

            for (int i = 0; i < 12; i++) {

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

                if ( meses.get(i).nextElementSibling().childNodeSize() > 0) {
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

                                    int cor = corQA.equals("#F0F0F0") ? pickColor(title) : R.color.colorPrimary;//pickColor(corQA);

                                    Evento evento = new Evento(title, description, cor, false);

                                    if (ev.getTimeInMillis() <= today.getTimeInMillis()) {
                                        evento.setHappened(true);
                                    }

                                    Materia materia = materiaBox.query()
                                            .equal(Materia_.name, title)
                                            .and()
                                            .equal(Materia_.year, User.getYear(0))
                                            .build().findFirst();

                                    evento.day.setTarget(dia);
                                    dia.eventos.add(evento);

                                    if (materia != null) {
                                        evento.materia.setTarget(materia);
                                        materia.eventos.add(evento);
                                        materiaBox.put(materia);
                                        //Log.v("Box for materia", "size of " + materiaBox.count());
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

                                        Evento evento = new Evento(infos, description, R.color.colorPrimary, data_inicio, data_fim, false);
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
        onResponse.onFinish(PG_CALENDARIO, 0);
    }

    private int pickColor(String string){
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
        }/* else if (string.equals("#F0F0F0")){//Avaliação
            color = Color.rgb(255, 20, 20);
        } else if (string.equals("#FF0000")){//Feriado Nacional/Feriado Estadual/municipal
            color = Color.rgb(219, 161, 26);
        } else if (string.equals("#008080")){//Férias/Dia não letivo
            color = Color.rgb(255, 212, 0);
        } else if (string.equals("#FFFF00")){//Datas Acadêmicas
            color = Color.rgb(255, 208, 0);
        } else if (string.equals("#000080")){//Início/Fim das aulas
            color = Color.rgb(6, 0, 137);
        } else if (string.equals("#A62A2A")){//Recesso Escolar
            color = Color.rgb(178, 62, 62);
        } else if (string.equals("#800000")){//Reunião CCS
            color = Color.rgb(178, 62, 62);
        } else if (string.equals("#008000")){//Ponto Facultativo/Ajustes de matrícula
            color = Color.rgb(0, 79, 1);
        } else if (string.equals("#CD7F32")){//Paralisação
            color = Color.rgb(255, 89, 0);
        } else if (string.equals("#00FF00")){//Rematrícula/Matriculas/Domingo Letivo
            color = Color.rgb(16, 255, 0);
        } else if (string.equals("#A6CAF0")){//Conselho de Classe
            color = Color.rgb(110, 163, 156);
        } else if (string.equals("#C0DCC0")){//Sábado letivo
            color = Color.rgb(29, 255, 0);
        } else if (string.equals("#D98719")){//Jogos Intermédios
            color = Color.rgb(199, 255, 68);
        } else if (string.equals("#A6CAF0")){//Fim de Etapa
            color = Color.rgb(51, 107, 95);
        } else if (string.equals("#238E23")){//Início de Etapa
            color = Color.rgb(70, 147, 131);
        } else if (string.equals("#C0DCC0")){//início de semestre
            color = Color.rgb(79, 168, 149);
        } else if (string.equals("#808080")){//Planejamento Docente
            color = Color.rgb(145, 145, 145);
        } else if (string.equals("#FFFF00")){//Capacitação de Servidores
            color = Color.rgb(221, 177, 0);
        }*/
        else {
            Materia materia = App.getBox().boxFor(Materia.class).query().equal(Materia_.name, string).build().findFirst();

            if (materia != null) {
                color = materia.getColor();
            }

            if (color == 0) {
                color = Utils.getRandomColorGenerator();
            }
        }

        return color;
    }

}
