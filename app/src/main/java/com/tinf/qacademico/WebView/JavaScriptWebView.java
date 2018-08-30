package com.tinf.qacademico.WebView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.tinf.qacademico.Class.Boletim;
import com.tinf.qacademico.Class.Calendario.Dia;
import com.tinf.qacademico.Class.Calendario.Evento;
import com.tinf.qacademico.Class.Calendario.Meses;
import com.tinf.qacademico.Class.Diarios.Diarios;
import com.tinf.qacademico.Class.Horario;
import com.tinf.qacademico.Class.Diarios.DiariosList;
import com.tinf.qacademico.Class.Diarios.Etapa;
import com.tinf.qacademico.Class.Materiais.Materiais;
import com.tinf.qacademico.Class.Materiais.MateriaisList;
import com.tinf.qacademico.R;
import com.tinf.qacademico.Utilities.Data;
import com.tinf.qacademico.Utilities.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.tinf.qacademico.Utilities.Utils.PG_BOLETIM;
import static com.tinf.qacademico.Utilities.Utils.PG_CALENDARIO;
import static com.tinf.qacademico.Utilities.Utils.PG_DIARIOS;
import static com.tinf.qacademico.Utilities.Utils.PG_HOME;
import static com.tinf.qacademico.Utilities.Utils.PG_HORARIO;
import static com.tinf.qacademico.Utilities.Utils.PG_MATERIAIS;
import static com.tinf.qacademico.Utilities.Utils.URL;
import static com.tinf.qacademico.Utilities.Utils.getRandomColorGenerator;

public class JavaScriptWebView {
    private Context context;
    private SingletonWebView webView = SingletonWebView.getInstance();
    private OnPageFinished onPageFinish;

    public JavaScriptWebView(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void handleHome(String html_p) {
        Log.i("JavaScriptWebView", "Home handling...");
        new Thread() {
            @Override
            public void run() {
                if (!html_p.equals("<html><head></head><body></body></html>")) {
                    try {
                        Document homePage = Jsoup.parse(html_p);
                        Element drawer_msg = homePage.getElementsByClass("titulo").get(1);

                        Calendar rightNow = Calendar.getInstance();
                        int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
                        int currentDay = rightNow.get(Calendar.DAY_OF_YEAR);
                        int currentMinute = rightNow.get(Calendar.MINUTE);

                        SharedPreferences.Editor editor = context.getSharedPreferences(Utils.LOGIN_INFO, Context.MODE_PRIVATE).edit();
                        editor.putString(Utils.LOGIN_NAME, drawer_msg.text().substring(drawer_msg.text().lastIndexOf(",") + 2, drawer_msg.text().indexOf(" !")));

                        editor.putInt(Utils.LOGIN_DAY, currentDay);
                        editor.putInt(Utils.LOGIN_HOUR, currentHour);
                        editor.putInt(Utils.LOGIN_MINUTE, currentMinute);
                        editor.apply();

                        webView.pg_home_loaded = true;
                        Log.i("JavaScriptWebView", "Home handled!");

                        onPageFinish.onPageFinish(URL + PG_HOME, null);

                    } catch (Exception ignored) {
                        Log.i("JavaScriptWebView", "Home error");
                    }
                }
            }
        }.start();
    }

    @JavascriptInterface
    public void handleBoletim(String html_p) {
        Log.i("JavaScriptWebView", "Boletim handling...");
        new Thread() {
            @Override
            public void run() {
                try {
                    String[][] trtd_boletim;
                    Document homeBoletim = Jsoup.parse(html_p);
                    //webView.bugBoletim = homeBoletim.outerHtml();

                    final Element table_boletim = homeBoletim.select("table").get(6);
                    Element table_notas = table_boletim.select("table").get(7);

                    Elements tables = table_notas.children();

                    List<Boletim> boletim = new ArrayList<>();

                    for (Element table : tables) {
                        Elements trs = table.select("tr");
                        trtd_boletim = new String[trs.size()][];
                        for (int i = 2; i < trs.size(); i++) {
                            Elements tds = trs.get(i).select("td");
                            trtd_boletim[i] = new String[tds.size()];
                            for (int j = 0; j < tds.size(); j++) {
                                if (tds.get(j).text().equals("") || tds.get(j).text().equals(",0")) {
                                    trtd_boletim[i][j] = "-";
                                } else {
                                    trtd_boletim[i][j] = tds.get(j).text();
                                }
                            }

                            boletim.add(new Boletim(trtd_boletim[i][0], trtd_boletim[i][3], trtd_boletim[i][5], trtd_boletim[i][6], trtd_boletim[i][7],
                                    trtd_boletim[i][9], trtd_boletim[i][10], trtd_boletim[i][11], trtd_boletim[i][12], trtd_boletim[i][14]));
                        }
                    }

                    Collections.sort(boletim, (b1, b2) -> b1.getMateria().compareTo(b2.getMateria()));

                    Document ano = Jsoup.parse(homeBoletim.select("#cmbanos").first().toString());
                    Elements options_ano = ano.select("option");

                    webView.infos.data_boletim = new String[options_ano.size()];

                    if (webView.pg_boletim_loaded.length == 1) {
                        webView.pg_boletim_loaded = new boolean[options_ano.size()];
                        webView.pg_boletim_loaded[0] = true;
                    }

                    for (int i = 0; i < options_ano.size(); i++) {
                        webView.infos.data_boletim[i] = options_ano.get(i).text();
                    }

                    Document periodo = Jsoup.parse(homeBoletim.select("#cmbperiodos").first().toString());
                    Elements options_periodo = periodo.select("option");

                    webView.infos.periodo_boletim = new String[options_periodo.size()];

                    for (int i = 0; i < options_periodo.size(); i++) {
                        webView.infos.periodo_boletim[i] = options_periodo.get(i).text();
                    }

                    Data.saveList(context, boletim, Utils.BOLETIM, webView.infos.data_boletim[webView.data_position_boletim],
                               webView.infos.periodo_boletim[webView.periodo_position_boletim]);

                    Data.saveDate(context, webView.infos);

                    webView.pg_boletim_loaded[webView.data_position_boletim] = true;
                    Log.i("JavaScriptWebView", "Boletim handled!");

                    onPageFinish.onPageFinish(URL + PG_BOLETIM, boletim);

                } catch (Exception ignored) {
                    Log.i("JavaScriptWebView", "Boletim error");
                }
            }
        }.start();
    }

    @JavascriptInterface
    public void handleDiarios(String html_p) {
        Log.i("JavaScriptWebView", "DiariosList handling...");
        new Thread() {
            @Override
            public void run() {
                try {
                    Document homeDiarios = Jsoup.parse(html_p);
                    //webView.bugDiarios = homeDiarios.outerHtml();

                    Elements table_diarios = homeDiarios.getElementsByTag("tbody").eq(12);
                    int numMaterias = table_diarios.select("table.conteudoTexto").size();
                    Element nxtElem = null;

                    List<DiariosList> diarios = new ArrayList<>();

                    for (int y = 0; y < numMaterias; y++) {

                        List<Etapa> etapas = new ArrayList<>();

                        if (table_diarios.select("table.conteudoTexto").eq(y).parents().eq(0).parents().eq(0).next().eq(0) != null) {
                            nxtElem = table_diarios.select("table.conteudoTexto").eq(y).parents().eq(0).parents().eq(0).next().eq(0).first();
                        }
                        String nomeMateria = table_diarios.select("table.conteudoTexto").eq(y).parents().eq(0).parents().eq(0).first().child(0).text();
                        nomeMateria = nomeMateria.substring(nomeMateria.indexOf("-") + 2, nomeMateria.indexOf("("));
                        nomeMateria = nomeMateria.substring(nomeMateria.indexOf("-") + 2);
                        String aux;

                        if (nxtElem != null) {
                            aux = nxtElem.child(0).child(0).ownText();
                        } else {
                            aux = "null";
                        }

                        while (aux.contains("Etapa")) {
                            if (aux.equals("1a. Etapa") || aux.equals("1ª Etapa")) {
                                aux = context.getResources().getString(R.string.diarios_PrimeiraEtapa);
                            } else if (aux.equals("1a Reavaliação da 1a Etapa") || aux.equals("1ª Reavaliação da 1ª Etapa")) {
                                aux = context.getResources().getString(R.string.diarios_RP1_PrimeiraEtapa);
                            } else if (aux.equals("2a Reavaliação da 1a Etapa") || aux.equals("2ª Reavaliação da 1ª Etapa")) {
                                aux = context.getResources().getString(R.string.diarios_RP2_PrimeiraEtapa);
                            } else if (aux.equals("2a. Etapa") || aux.equals("2ª Etapa")) {
                                aux = context.getResources().getString(R.string.diarios_SegundaEtapa);
                            } else if (aux.equals("1a Reavaliação da 2a Etapa") || aux.equals("1ª Reavaliação da 2ª Etapa")) {
                                aux = context.getResources().getString(R.string.diarios_RP1_SegundaEtapa);
                            } else if (aux.equals("2a Reavaliação da 2a Etapa") || aux.equals("2ª Reavaliação da 2ª Etapa")) {
                                aux = context.getResources().getString(R.string.diarios_RP2_SegundaEtapa);
                            }

                            Element tabelaNotas = Objects.requireNonNull(nxtElem).child(0).child(1).child(0);
                            Elements notasLinhas = tabelaNotas.getElementsByClass("conteudoTexto");

                            List<Diarios> trabalhos = new ArrayList<>();

                            for (int i = 0; i < notasLinhas.size(); i++) {
                                String data = notasLinhas.eq(i).first().child(1).text().substring(0, 10);
                                String tipo = context.getResources().getString(R.string.sigla_Avaliacao);
                                int tint = context.getResources().getColor(R.color.diarios_avaliacao);
                                if (notasLinhas.eq(i).first().child(1).text().contains("Prova")) {
                                    tint = context.getResources().getColor(R.color.diarios_prova);
                                    tipo = context.getResources().getString(R.string.sigla_Prova);
                                } else if (notasLinhas.eq(i).first().child(1).text().contains("Diarios")) {
                                    tint = context.getResources().getColor(R.color.diarios_trabalho);
                                    tipo = context.getResources().getString(R.string.sigla_Trabalho);
                                } else if (notasLinhas.eq(i).first().child(1).text().contains("Qualitativa")) {
                                    tint = context.getResources().getColor(R.color.diarios_qualitativa);
                                    tipo = context.getResources().getString(R.string.sigla_Qualitativa);
                                }

                                String caps = trimp(trim1(notasLinhas.eq(i).first().child(1).text()));
                                String nome = caps.substring(1, 2).toUpperCase() + caps.substring(2);
                                String peso = trimp(notasLinhas.eq(i).first().child(2).text());
                                String max = trimp(notasLinhas.eq(i).first().child(3).text());
                                String nota = trimp(notasLinhas.eq(i).first().child(4).text());

                                if (nota.equals("")) {
                                    nota = " -";
                                }
                                trabalhos.add(new Diarios(nome, peso, max, nota, tipo, data, tint));
                            }

                            nxtElem = nxtElem.nextElementSibling();

                            etapas.add(new Etapa(aux, trabalhos));

                            if (nxtElem != null) {
                                aux = nxtElem.child(0).child(0).text();
                            } else {
                                aux = "null";
                            }
                        }
                        diarios.add(new DiariosList(nomeMateria, etapas));
                    }

                    Collections.sort(diarios, (d1, d2) -> d1.getTitle().compareTo(d2.getTitle()));

                    Elements options = homeDiarios.getElementsByTag("option");

                    webView.infos.data_diarios = new String[options.size() - 1];

                    for (int i = 0; i < options.size() - 1; i++) {
                        webView.infos.data_diarios[i] = options.get(i + 1).text();
                    }

                    if (webView.pg_diarios_loaded.length == 1) {
                        webView.pg_diarios_loaded = new boolean[options.size()];
                        webView.pg_diarios_loaded[0] = true;
                    }

                    Data.saveList(context, diarios, Utils.DIARIOS, webView.infos.data_diarios[webView.data_position_diarios],
                            null);

                    Data.saveDate(context, webView.infos);

                    webView.pg_diarios_loaded[webView.data_position_diarios] = true;
                    Log.i("JavaScriptWebView", "Diarios handled!");

                    onPageFinish.onPageFinish(URL + PG_DIARIOS, diarios);

                } catch (Exception ignored) {
                    Log.i("JavaScriptWebView", "Diarios error");
                }
            }
        }.start();
    }

    @JavascriptInterface
    public void handleHorario(String html_p) {
        Log.i("JavaScriptWebView", "Horario handling...");
        new Thread() {
            @Override
            public void run() {
                try {
                    String[][] trtd_horario = null;
                    String[] code = null;
                    Document homeHorario = Jsoup.parse(html_p);
                    //webView.bugHorario = homeHorario.outerHtml();

                    Element table_horario = homeHorario.select("table").eq(11).first();
                    Element table_codes = homeHorario.select("table").eq(12).first();
                    Elements codes = table_codes.children();

                    List<Horario> horario = new ArrayList<>();

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

                                for (int k = 1; k < code.length; k++) {
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
                                        Log.v("HORARIO", "MONDAY");
                                    } else if (((trtd_horario[i][j]).contains("3ª-FEIRA"))) {
                                        trtd_horario[i][j] = String.valueOf(Calendar.TUESDAY);
                                        Log.v("HORARIO", "TUESDAY");
                                    } else if (((trtd_horario[i][j]).contains("4ª-FEIRA"))) {
                                        trtd_horario[i][j] = String.valueOf(Calendar.WEDNESDAY);
                                        Log.v("HORARIO", "WEDNESDAY");
                                    } else if (((trtd_horario[i][j]).contains("5ª-FEIRA"))) {
                                        trtd_horario[i][j] = String.valueOf(Calendar.THURSDAY);
                                        Log.v("HORARIO", "THURSDAY");
                                    } else if (((trtd_horario[i][j]).contains("6ª-FEIRA"))) {
                                        trtd_horario[i][j] = String.valueOf(Calendar.FRIDAY);
                                        Log.v("HORARIO", "FRIDAY");
                                    }
                                }
                            }
                        }
                    }

                    for (int i = 1; i <= 5; i++) {
                        for (int j = 1; j < Objects.requireNonNull(trtd_horario).length; j++) {
                            if (!trtd_horario[j][i].equals("")) {

                                Calendar startTime = Calendar.getInstance();
                                startTime.set(Calendar.DAY_OF_WEEK, Integer.valueOf(trtd_horario[0][i]));
                                startTime.set(Calendar.HOUR_OF_DAY, trimh(trimta(trtd_horario[j][0])));
                                startTime.set(Calendar.MINUTE, trimm(trimta(trtd_horario[j][0])));

                                Calendar endTime = (Calendar) startTime.clone();
                                endTime.set(Calendar.HOUR_OF_DAY, trimh(trimtd(trtd_horario[j][0])));
                                endTime.set(Calendar.MINUTE, trimm(trimtd(trtd_horario[j][0])));

                                horario.add(new Horario(trtd_horario[j][i], startTime, endTime, pickColor(trtd_horario[j][i])));
                            }
                        }
                    }

                    horario = setColors(horario);

                    Document ano = Jsoup.parse(homeHorario.select("#cmbanos").first().toString());
                    Elements options_ano = ano.select("option");

                    webView.infos.data_horario = new String[options_ano.size()];

                    if (webView.pg_horario_loaded.length == 1) {
                        webView.pg_horario_loaded = new boolean[options_ano.size()];
                        webView.pg_horario_loaded[0] = true;
                    }

                    for (int i = 0; i < options_ano.size(); i++) {
                        webView.infos.data_horario[i] = options_ano.get(i).text();
                    }

                    Document periodo = Jsoup.parse(homeHorario.select("#cmbperiodos").first().toString());
                    Elements options_periodo = periodo.select("option");

                    webView.infos.periodo_horario = new String[options_periodo.size()];

                    for (int i = 0; i < options_periodo.size(); i++) {
                        webView.infos.periodo_horario[i] = options_periodo.get(i).text();
                    }

                    Data.saveList(context, horario, Utils.HORARIO, webView.infos.data_horario[webView.data_position_horario],
                            webView.infos.periodo_horario[webView.periodo_position_horario]);

                    Data.saveDate(context, webView.infos);

                    webView.pg_horario_loaded[webView.data_position_horario] = true;
                    Log.i("JavaScriptWebView", "Horario handled!");

                    onPageFinish.onPageFinish(URL + PG_HORARIO, horario);

                } catch (Exception ignored) {
                    Log.i("JavaScriptWebView", "Horario error");
                }
            }
        }.start();
    }

    @JavascriptInterface
    public void handleMateriais(String html_p) {
        Log.i("JavaScriptWebView", "Materiais handling...");
        new Thread() {
            @Override
            public void run() {
                try {
                    Document homeMaterial = Jsoup.parse(html_p);
                    Element table = homeMaterial.getElementsByTag("tbody").get(10);
                    Elements rotulos = table.getElementsByClass("rotulo");

                    List<MateriaisList> materiais = new ArrayList<>();

                    for (int i = 1; i < rotulos.size(); i++) {

                        String str = rotulos.get(i).text();
                        str = str.substring(str.indexOf('-') + 2, str.indexOf('('));
                        str = str.substring(str.indexOf('-') + 2);
                        String nomeMateria = str;

                        Log.i("Materia", "\n\n\n**************************" + nomeMateria + "*********************************\n");

                        //parte dos conteudos
                        String classe = rotulos.get(i).nextElementSibling().className();
                        Element element = rotulos.get(i).nextElementSibling();

                        List<Materiais> material = new ArrayList<>();

                        while (classe.equals("conteudoTexto")) {

                            String data = element.child(0).text();
                            String link = element.child(1).child(1).attr("href");
                            String nomeConteudo = element.child(1).child(1).text();
                            String descricao = "";
                            String extension = link.substring(link.indexOf("."));

                            //pode ou nao ter descricao
                            if (element.child(1).children().size() > 2) {
                                descricao = element.child(1).child(3).nextSibling().toString();
                            }

                            int color = context.getResources().getColor(R.color.materiais_file);
                            Drawable img = context.getResources().getDrawable(R.drawable.ic_file);

                            if (extension.equals(".pdf")) {
                                color = context.getResources().getColor(R.color.materiais_pdf);
                                img = context.getResources().getDrawable(R.drawable.ic_pdf);
                            } else if (extension.equals(".docx") || extension.equals(".doc")
                                    || extension.equals(".txt") || extension.equals(".rtf")) {
                                color = context.getResources().getColor(R.color.materiais_doc);
                                img = context.getResources().getDrawable(R.drawable.ic_docs);
                            } else if (extension.equals(".csv") || extension.equals(".svg")) {
                                color = context.getResources().getColor(R.color.materiais_table);
                                img = context.getResources().getDrawable(R.drawable.ic_table);
                            } else if (extension.equals(".zip") || extension.equals(".rar")
                                    || extension.equals(".7z")) {
                                color = context.getResources().getColor(R.color.materiais_zip);
                                img = context.getResources().getDrawable(R.drawable.ic_compressed);
                            } else if (extension.equals(".mp3") || extension.equals(".wav")
                                    || extension.equals(".wma")) {
                                color = context.getResources().getColor(R.color.materiais_audio);
                                img = context.getResources().getDrawable(R.drawable.ic_song);
                            } else if (extension.equals(".mp4") || extension.equals(".wmv")
                                    || extension.equals(".avi")) {
                                color = context.getResources().getColor(R.color.materiais_video);
                                img = context.getResources().getDrawable(R.drawable.ic_video);
                            } else if (extension.equals(".jpg") || extension.equals(".png")) {
                                color = context.getResources().getColor(R.color.materiais_image);
                                img = context.getResources().getDrawable(R.drawable.ic_picture);
                            } else if (extension.equals(".jar") || extension.equals(".php")
                                    || extension.equals(".html") || extension.equals(".css")
                                    || extension.equals(".js") || extension.equals(".json")
                                    || extension.equals(".xml")) {
                                color = context.getResources().getColor(R.color.materiais_script);
                                img = context.getResources().getDrawable(R.drawable.ic_script);
                            }

                            material.add(new Materiais(data, nomeConteudo, link, descricao, color, img));

                            Log.i("Materia", "\n\nNome: " + nomeConteudo + "\nData: " + data + "\nLink: " + link + "\nDesc: " + descricao);
                            if (element.nextElementSibling() != null) {
                                element = element.nextElementSibling();
                                classe = element.className();
                            } else {
                                classe = "quit";
                            }
                        }

                        materiais.add(new MateriaisList(nomeMateria, material, getRandomColorGenerator(context)));
                    }

                    webView.pg_materiais_loaded[webView.data_position_materiais] = true;
                    Log.i("JavaScriptWebView", "Materiais handled!");

                    onPageFinish.onPageFinish(URL + PG_MATERIAIS, materiais);

                } catch (Exception ignored) {
                    Log.i("JavaScriptWebView", "Materiais error");
                }
            }
        }.start();
    }

    @JavascriptInterface
    public void handleCalendario(String html_p) {
        Log.i("JavaScriptWebView", "Calendario handling...");
        new Thread() {
            @Override
            public void run() {
               //try {
                    Document document = Jsoup.parse(html_p);
                    //webView.bugCalendario = document.outerHtml();

                    Elements meses = document.getElementsByTag("table").get(10).getElementsByTag("tbody").get(2).select("#AutoNumber3");
                    //Elements infos = document.getElementsByTag("table").get(10).getElementsByTag("tbody").get(2).select("#AutoNumber3");

                    List<Meses> listMeses = new ArrayList<>();

                    boolean changeYear = false;

                    for (int i = 0; i < 12; i++) {
                        String nomeMes = meses.get(i).previousElementSibling().previousElementSibling().getElementsByTag("div").get(0).text();
                        int numMes = 0;

                        if (nomeMes.equals("JANEIRO")) {
                            numMes = Calendar.JANUARY;
                        } else if (nomeMes.equals("FEVEREIRO")) {
                            numMes = Calendar.FEBRUARY;
                        } else if (nomeMes.equals("MARÇO")) {
                            numMes = Calendar.MARCH;
                        } else if (nomeMes.equals("ABRIL")) {
                            numMes = Calendar.APRIL;
                        } else if (nomeMes.equals("MAIO")) {
                            numMes = Calendar.MAY;
                        } else if (nomeMes.equals("JUNHO")) {
                            numMes = Calendar.JUNE;
                        } else if (nomeMes.equals("JULHO")) {
                            numMes = Calendar.JULY;
                        } else if (nomeMes.equals("AGOSTO")) {
                            numMes = Calendar.AUGUST;
                        } else if (nomeMes.equals("SETEMBRO")) {
                            numMes = Calendar.SEPTEMBER;
                        } else if (nomeMes.equals("OUTUBRO")) {
                            numMes = Calendar.OCTOBER;
                        } else if (nomeMes.equals("NOVEMBRO")) {
                            numMes = Calendar.NOVEMBER;
                        } else if (nomeMes.equals("DEZEMBRO")) {
                            numMes = Calendar.DECEMBER;
                        }

                        Elements arrayEventos = new Elements();

                        if ( meses.get(i).nextElementSibling().childNodeSize() > 0) {
                            arrayEventos = meses.get(i).nextElementSibling().child(0).getElementsByTag("tr");
                        }

                        //Elements arrayEventos = meses.get(i).nextElementSibling().child(0).getElementsByTag("tr");
                        Elements dias = meses.get(i).getElementsByTag("td");

                        List<Dia> diaList = new ArrayList<>();

                        /*Elements tableLegenda = document.getElementsByTag("td").parents()
                                .get(0).children();*/

                        String date = document.getElementsByTag("font").get(2).text();

                        int year = Integer.parseInt(date.substring(date.lastIndexOf("/") + 1));

                        if (listMeses.size() > 1) {
                            if (numMes < listMeses.get(listMeses.size() - 2).getMonth()) {
                                changeYear = true;
                            }
                        }

                        if (changeYear) {
                            year++;
                        }

                        //no mes (cores)
                        for (int j = 7; j < dias.size(); j++) {
                            List<Evento> listEventos = new ArrayList<>();
                            String numeroDia = dias.get(j).text();

                            if (!numeroDia.equals("")) {

                                String corQA = dias.get(j).attr("bgcolor"); // cor original

                                if (!corQA.equals("")) {

                                    for (int k = 0; k < arrayEventos.size(); k++) {

                                        String diaEvento = arrayEventos.get(k).child(0).text();

                                        //Eventos normais
                                        if (diaEvento.equals(numeroDia)) {
                                            String infos = arrayEventos.get(k).child(1).text();
                                            String description = infos.substring(infos.lastIndexOf(")") + 1).trim();
                                            String title = infos.substring(0, infos.lastIndexOf("(") + 1).trim();
                                            title = title.substring(0 , title.lastIndexOf(" ") + 1).trim();

                                            if (title.equals("")){
                                                title = description;
                                                description = "";
                                            }

                                            int cor = corQA.equals("#F0F0F0") ? pickColor(title) : pickColor(corQA);

                                            listEventos.add(new Evento(title, description, cor));
                                            Log.e("Eve", numeroDia + "/" + (numMes + 1) + "/" + year);
                                        }

                                        //Eventos com mais de um dia.
                                        if (diaEvento.contains(" ~ ")){
                                            String data_inicio = diaEvento.substring(0,diaEvento.indexOf(" ~"));
                                            String data_fim =  diaEvento.substring(diaEvento.indexOf("~ ")+2);
                                            diaEvento = data_inicio.substring(0,data_inicio.indexOf("/"));
                                            if (diaEvento.equals(numeroDia)) {
                                                String infos = arrayEventos.get(k).child(1).text();
                                                String description =  data_inicio + " - " + data_fim;
                                                //String title =  infos.substring(infos.lastIndexOf(")") + 1).trim();
                                                //title = title.substring(0 , title.lastIndexOf(" ") + 1).trim();

                                                Evento evento = new Evento(infos, description,
                                                        pickColor(corQA), data_inicio, data_fim);
                                                        //Color.argb(255, 0, 255, 0),data_inicio,data_fim);
                                                listEventos.add(evento);
                                                Log.e("Eve", numeroDia + "/" + (numMes + 1) + "/" + year);
                                            }
                                        }
                                    }
                                }
                                Log.i("Dia", numeroDia + "/" + (numMes + 1) + "/" + year);
                                diaList.add(new Dia(Integer.parseInt(numeroDia), listEventos));
                            }
                        }
                        listMeses.add(new Meses(diaList, numMes, year));
                    }

                    webView.infos.data_calendario = document.getElementsByClass("dado_cabecalho").get(1).text();
                    Data.saveList(context, listMeses, Utils.CALENDARIO, webView.infos.data_calendario, null);
                    Data.saveDate(context, webView.infos);
                    webView.pg_calendario_loaded = true;

                    Log.i("JavaScriptWebView", "Calendario handled!");

                    onPageFinish.onPageFinish(URL + PG_CALENDARIO, listMeses);

                /*} catch (Exception ignored) {
                    Log.i("JavaScriptWebView", "Calendario error");
                }*/
            }
        }.start();
    }


    private String trimp(String string) {
        string = string.substring(string.indexOf(":"));
        string = string.replace(":", "");
        return string;
    }

    private String trim1(String string) {
        string = string.substring(string.indexOf(", ") + 2);
        return string;
    }

    private int trimh(String string) {
        string = string.substring(0, string.indexOf(":"));
        return Integer.valueOf(string);
    }

    private int trimm(String string) {
        string = string.substring(string.indexOf(":") + 1);
        return Integer.valueOf(string);
    }

    private String trimta(String string) {
        string = string.substring(0, string.indexOf("~"));
        return string;
    }

    private String trimtd(String string) {
        string = string.substring(string.indexOf("~") + 1);
        return string;
    }

    private int pickColor(String string){
        int color;

        if (string.contains("Biologia")) {
            color = context.getResources().getColor(R.color.biologia);
        } else if (string.contains("Educação Física")) {
            color = context.getResources().getColor(R.color.edFisica);
        } else if (string.contains("Filosofia")) {
            color = context.getResources().getColor(R.color.filosofia);
        } else if (string.contains("Física")) {
            color = context.getResources().getColor(R.color.fisica);
        } else if (string.contains("Geografia")) {
            color = context.getResources().getColor(R.color.geografia);
        } else if (string.contains("História")) {
            color = context.getResources().getColor(R.color.historia);
        } else if (string.contains("Português") || string.contains("Portuguesa")) {
            color = context.getResources().getColor(R.color.portugues);
        } else if (string.contains("Matemática")) {
            color = context.getResources().getColor(R.color.matematica);
        } else if (string.contains("Química")) {
            color = context.getResources().getColor(R.color.quimica);
        } else if (string.contains("Sociologia")) {
            color = context.getResources().getColor(R.color.sociologia);
        } else if (string.equals("#F0F0F0")){//Avaliação
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
        } else {
            color = Utils.getRandomColorGenerator(context);
        }

        return color;
    }

    private List<Horario> setColors(List<Horario> horario) {
        for (int i = 0; i < horario.size(); i++) {
            if (horario.get(i).getColor() == 0) {
                for (int j = 0; j < horario.size(); j++) {
                    if (horario.get(i).getName().equals(
                            horario.get(j).getName())) {
                        if (horario.get(j).getColor() == 0) {
                            if (horario.get(i).getColor() == 0) {
                                horario.get(i).setColor(Utils.getRandomColorGenerator(Objects.requireNonNull(context)));
                                horario.get(j).setColor(horario.get(i).getColor());
                            } else {
                                horario.get(j).setColor(horario.get(i).getColor());
                            }
                        } else {
                            horario.get(i).setColor(horario.get(j).getColor());
                        }
                    } else {
                        if (horario.get(i).getColor() == 0) {
                            horario.get(i).setColor(Utils.getRandomColorGenerator(Objects.requireNonNull(context)));
                        }
                    }
                }
            }
        }
        return horario;
    }

    public void setOnPageFinished(OnPageFinished onPageFinish) {
        this.onPageFinish = onPageFinish;
    }

    public interface OnPageFinished {
        void onPageFinish(String url_p, List<?> list);
    }
}