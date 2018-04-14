package com.qacademico.qacademico.WebView;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.google.firebase.perf.metrics.AddTrace;
import com.qacademico.qacademico.Class.Boletim;
import com.qacademico.qacademico.Class.Diarios;
import com.qacademico.qacademico.Class.Etapa;
import com.qacademico.qacademico.Class.Horario;
import com.qacademico.qacademico.Class.Materia;
import com.qacademico.qacademico.Class.Materiais;
import com.qacademico.qacademico.Class.Material;
import com.qacademico.qacademico.Class.Trabalho;
import com.qacademico.qacademico.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static com.qacademico.qacademico.Utilities.Utils.pg_boletim;
import static com.qacademico.qacademico.Utilities.Utils.pg_diarios;
import static com.qacademico.qacademico.Utilities.Utils.pg_home;
import static com.qacademico.qacademico.Utilities.Utils.pg_horario;
import static com.qacademico.qacademico.Utilities.Utils.pg_materiais;
import static com.qacademico.qacademico.Utilities.Utils.url;

public class JavaScriptWebView {
    private Context context;
    private SingletonWebView webViewMain;
    private SharedPreferences login_info;
    private OnPageFinished onPageFinish;

    public JavaScriptWebView(Context context) {
        this.context = context.getApplicationContext();
        this.login_info = context.getSharedPreferences("login_info", 0);
        this.webViewMain = SingletonWebView.getInstance();
    }

    @JavascriptInterface
    @AddTrace(name = "handleHome")
    public void handleHome(String html_p) {

        new Thread() {
            @Override
            public void run() {
                try {
                    Document homePage = Jsoup.parse(html_p);
                    Element drawer_msg = homePage.getElementsByClass("titulo").get(1);

                    Calendar rightNow = Calendar.getInstance();
                    int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
                    int currentDay = rightNow.get(Calendar.DAY_OF_YEAR);
                    int currentMinute = rightNow.get(Calendar.MINUTE);

                    SharedPreferences.Editor editor = login_info.edit();
                    try {
                        editor.putString("nome", drawer_msg.text().substring(drawer_msg.text().lastIndexOf(",") + 2, drawer_msg.text().indexOf(" !")));
                    } catch (Exception e) {
                        Log.i("handleHome", "Erro ao atribuir nome");
                    }

                    editor.putInt("last_day", currentDay);
                    editor.putInt("last_hour", currentHour);
                    editor.putInt("last_minute", currentMinute);
                    editor.apply();

                    webViewMain.pg_home_loaded = true;
                    onPageFinish.OnPageFinish(url + pg_home);
                    Log.i("handleHome", "Carregado");

                } catch (Exception ignored) {}
            }
        }.start();
    }

    @JavascriptInterface
    @AddTrace(name = "handleBoletim")
    public void handleBoletim(String html_p) {

        new Thread() {
            @Override
            public void run() {
                try {
                    String[][] trtd_boletim;
                    Document homeBoletim = Jsoup.parse(html_p);
                    webViewMain.bugBoletim = homeBoletim.outerHtml();

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
                                if (tds.get(j).text().equals("")) {
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

                    ObjectOutputStream object;
                    try {
                        object = new ObjectOutputStream(new FileOutputStream(context.getFileStreamPath(
                                login_info.getString("matricula", "") + ".boletim" )));
                        object.writeObject(boletim);
                        object.flush();
                        object.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Document ano = Jsoup.parse(homeBoletim.select("#cmbanos").first().toString());
                    Elements options_ano = ano.select("option");

                    webViewMain.data_boletim = new String[options_ano.size()];

                    for (int i = 0; i < options_ano.size(); i++) {
                        webViewMain.data_boletim[i] = options_ano.get(i).text();
                    }

                    Document periodo = Jsoup.parse(homeBoletim.select("#cmbperiodos").first().toString());
                    Elements options_periodo = periodo.select("option");

                    webViewMain.periodo_boletim = new String[options_periodo.size()];

                    for (int i = 0; i < options_periodo.size(); i++) {
                        webViewMain.periodo_boletim[i] = options_periodo.get(i).text();
                    }

                    webViewMain.pg_boletim_loaded = true;
                    onPageFinish.OnPageFinish(url + pg_boletim);
                    Log.i("handleBoletim", "Carregado");

                } catch (Exception ignored) {}
            }
        }.start();
    }

    @JavascriptInterface
    @AddTrace(name = "handleDiarios")
    public void handleDiarios(String html_p) {

        new Thread() {
            @Override
            public void run() {
                try {
                    Document homeDiarios = Jsoup.parse(html_p);
                    webViewMain.bugDiarios = homeDiarios.outerHtml();

                    Elements table_diarios = homeDiarios.getElementsByTag("tbody").eq(12);
                    int numMaterias = table_diarios.select("table.conteudoTexto").size();
                    Element nxtElem = null;

                    List<Diarios> diarios = new ArrayList<>();

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

                            Element tabelaNotas = nxtElem.child(0).child(1).child(0);
                            Elements notasLinhas = tabelaNotas.getElementsByClass("conteudoTexto");

                            List<Trabalho> trabalhos = new ArrayList<>();

                            for (int i = 0; i < notasLinhas.size(); i++) {
                                String data = notasLinhas.eq(i).first().child(1).text().substring(0, 10);
                                String tipo = context.getResources().getString(R.string.sigla_Avaliacao);
                                int tint = context.getResources().getColor(R.color.orange_A700);
                                if (notasLinhas.eq(i).first().child(1).text().contains("Prova")) {
                                    tint = context.getResources().getColor(R.color.orange_A400);
                                    tipo = context.getResources().getString(R.string.sigla_Prova);
                                } else if (notasLinhas.eq(i).first().child(1).text().contains("Trabalho")) {
                                    tint = context.getResources().getColor(R.color.amber_A400);
                                    tipo = context.getResources().getString(R.string.sigla_Trabalho);
                                } else if (notasLinhas.eq(i).first().child(1).text().contains("Qualitativa")) {
                                    tint = context.getResources().getColor(R.color.yellow_A400);
                                    tipo = context.getResources().getString(R.string.sigla_Qualitativa);
                                }

                                String caps = trim(trim1(notasLinhas.eq(i).first().child(1).text()));
                                String nome = data + " ー " + caps.substring(1, 2).toUpperCase() + caps.substring(2);
                                String peso = trim(notasLinhas.eq(i).first().child(2).text());
                                String max = trim(notasLinhas.eq(i).first().child(3).text());
                                String nota = trim(notasLinhas.eq(i).first().child(4).text());

                                if (nota.equals("")) {
                                    nota = " -";
                                }
                                trabalhos.add(new Trabalho(nome, peso, max, nota, tipo, tint));
                            }

                            nxtElem = nxtElem.nextElementSibling();

                            etapas.add(new Etapa(aux, trabalhos));

                            if (nxtElem != null) {
                                aux = nxtElem.child(0).child(0).text();
                            } else {
                                aux = "null";
                            }
                        }
                        diarios.add(new Diarios(nomeMateria, etapas, context));
                    }

                    Collections.sort(diarios, (d1, d2) -> d1.getNomeMateria().compareTo(d2.getNomeMateria()));

                    ObjectOutputStream object;
                    try {
                        object = new ObjectOutputStream(new FileOutputStream(context.getFileStreamPath(
                                login_info.getString("matricula", "") + ".diarios" )));
                        object.writeObject(diarios);
                        object.flush();
                        object.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Elements options = homeDiarios.getElementsByTag("option");

                    webViewMain.data_diarios = new String[options.size() - 1];

                    for (int i = 0; i < options.size() - 1; i++) {
                        webViewMain.data_diarios[i] = options.get(i + 1).text();
                    }

                    webViewMain.pg_diarios_loaded = true;
                    onPageFinish.OnPageFinish(url + pg_diarios);
                    Log.i("handleDiarios", "Carregado");

                } catch (Exception ignored) {}
            }
        }.start();
    }

    @JavascriptInterface
    @AddTrace(name = "handleHorario")
    public void handleHorario(String html_p) {

        new Thread() {
            @Override
            public void run() {
                try {
                    String[][] trtd_horario = null;
                    String[] code = null;
                    Document homeHorario = Jsoup.parse(html_p);
                    webViewMain.bugHorario = homeHorario.outerHtml();

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
                                    if ((trtd_horario[i][j]).contains("~")) {
                                        trtd_horario[i][j] = trtd_horario[i][j].substring(0, trtd_horario[i][j].indexOf("~"));
                                    }
                                    if (((trtd_horario[i][j]).contains("2ª-FEIRA"))) {
                                        trtd_horario[i][j] = context.getResources().getString(R.string.day_monday);
                                    } else if (((trtd_horario[i][j]).contains("3ª-FEIRA"))) {
                                        trtd_horario[i][j] = context.getResources().getString(R.string.day_tuesday);
                                    } else if (((trtd_horario[i][j]).contains("4ª-FEIRA"))) {
                                        trtd_horario[i][j] = context.getResources().getString(R.string.day_wednesday);
                                    } else if (((trtd_horario[i][j]).contains("5ª-FEIRA"))) {
                                        trtd_horario[i][j] = context.getResources().getString(R.string.day_thursday);
                                    } else if (((trtd_horario[i][j]).contains("6ª-FEIRA"))) {
                                        trtd_horario[i][j] = context.getResources().getString(R.string.day_friday);
                                    }
                                }
                            }
                        }
                    }

                    for (int i = 1; i <= 5; i++) {
                        List<Materia> materias = new ArrayList<>();

                        for (int j = 1; j < trtd_horario.length; j++) {
                            if (!trtd_horario[j][i].equals("")) {
                                materias.add(new Materia(trtd_horario[j][0], trtd_horario[j][i]));
                            }
                        }
                        horario.add(new Horario(trtd_horario[0][i], materias));
                    }

                    ObjectOutputStream object;
                    try {
                        object = new ObjectOutputStream(new FileOutputStream(context.getFileStreamPath(
                                login_info.getString("matricula", "") + ".horario" )));
                        object.writeObject(horario);
                        object.flush();
                        object.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Document ano = Jsoup.parse(homeHorario.select("#cmbanos").first().toString());
                    Elements options_ano = ano.select("option");

                    webViewMain.data_horario = new String[options_ano.size()];

                    for (int i = 0; i < options_ano.size(); i++) {
                        webViewMain.data_horario[i] = options_ano.get(i).text();
                    }

                    Document periodo = Jsoup.parse(homeHorario.select("#cmbperiodos").first().toString());
                    Elements options_periodo = periodo.select("option");

                    webViewMain.periodo_horario = new String[options_periodo.size()];

                    for (int i = 0; i < options_periodo.size(); i++) {
                        webViewMain.periodo_horario[i] = options_periodo.get(i).text();
                    }

                    webViewMain.pg_horario_loaded = true;
                    onPageFinish.OnPageFinish(url + pg_horario);
                    Log.i("handleHorario", "Carregado");

                } catch (Exception ignored) {}
            }
        }.start();
    }

    @JavascriptInterface
    @AddTrace(name = "handleMateriais")
    public void handleMateriais(String html_p) {

        new Thread() {
            @Override
            public void run() {
                try {
                    Document homeMaterial = Jsoup.parse(html_p);
                    Element table = homeMaterial.getElementsByTag("tbody").get(10);
                    Elements rotulos = table.getElementsByClass("rotulo");

                    List<Materiais> materiais = new ArrayList<>();

                    for (int i = 1; i < rotulos.size(); i++) {

                        String str = rotulos.get(i).text();
                        str = str.substring(str.indexOf('-') + 2, str.indexOf('('));
                        str = str.substring(str.indexOf('-') + 2);
                        String nomeMateria = str;

                        Log.i("Materia", "\n\n\n**************************" + nomeMateria + "*********************************\n");

                        //parte dos conteudos
                        String classe = rotulos.get(i).nextElementSibling().className();
                        Element element = rotulos.get(i).nextElementSibling();

                        List<Material> material = new ArrayList<>();

                        while (classe.equals("conteudoTexto")) {

                            String data = element.child(0).text();
                            String link = element.child(1).child(1).attr("href");
                            String nomeConteudo = element.child(1).child(1).text();
                            String descricao = "";

                            //pode ou nao ter descricao
                            if (element.child(1).children().size() > 2) {
                                descricao = element.child(1).child(3).nextSibling().toString();
                            }

                            material.add(new Material(data, nomeConteudo, link, descricao));

                            Log.i("Materia", "\n\nNome: " + nomeConteudo + "\nData: " + data + "\nLink: " + link + "\nDesc: " + descricao);
                            if (element.nextElementSibling() != null) {
                                element = element.nextElementSibling();
                                classe = element.className();
                            } else {
                                classe = "quit";
                            }
                        }
                        materiais.add(new Materiais(nomeMateria, material));
                    }

                    webViewMain.pg_material_loaded = true;
                    onPageFinish.OnPageFinish(url + pg_materiais);
                    Log.i("handleMateriais", "Carregado");

                } catch (Exception ignored) {}
            }
        }.start();
    }

    private String trim(String string) {
        String x = string;
        x = x.substring(x.indexOf(":"));
        x = x.replace(":", "");
        return x;
    }

    private String trim1(String string) {
        String x = string;
        x = x.substring(x.indexOf(", ") + 2);
        return x;
    }

    public void onPageFinished(OnPageFinished onPageFinish){
        this.onPageFinish = onPageFinish;
    }

    public interface OnPageFinished {
        void OnPageFinish(String url_p);
    }
}
