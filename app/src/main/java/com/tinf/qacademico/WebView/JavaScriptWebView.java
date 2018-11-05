package com.tinf.qacademico.WebView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.webkit.JavascriptInterface;
import com.tinf.qacademico.Class.Calendario.Dia;
import com.tinf.qacademico.Class.Calendario.Evento;
import com.tinf.qacademico.Class.Calendario.Mes;
import com.tinf.qacademico.Class.Materias.Diarios;
import com.tinf.qacademico.Class.Materias.Horario;
import com.tinf.qacademico.Class.Materias.Etapa;
import com.tinf.qacademico.Class.Materiais.Materiais;
import com.tinf.qacademico.Class.Materiais.MateriaisList;
import com.tinf.qacademico.Class.Materias.Materia;
import com.tinf.qacademico.Class.Materias.Materia_;
import com.tinf.qacademico.Interfaces.WebView.OnPageLoad;
import com.tinf.qacademico.R;
import com.tinf.qacademico.Utilities.Utils;
import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import static android.content.Context.MODE_PRIVATE;
import static com.tinf.qacademico.Utilities.Utils.LOGIN_INFO;
import static com.tinf.qacademico.Utilities.Utils.MATERIAS;
import static com.tinf.qacademico.Utilities.Utils.PG_BOLETIM;
import static com.tinf.qacademico.Utilities.Utils.PG_CALENDARIO;
import static com.tinf.qacademico.Utilities.Utils.PG_DIARIOS;
import static com.tinf.qacademico.Utilities.Utils.PG_HOME;
import static com.tinf.qacademico.Utilities.Utils.PG_HORARIO;
import static com.tinf.qacademico.Utilities.Utils.URL;
import static com.tinf.qacademico.Utilities.Utils.YEARS;
import static com.tinf.qacademico.Utilities.Utils.getRandomColorGenerator;

public class JavaScriptWebView {
    private Context context;
    private SingletonWebView webView = SingletonWebView.getInstance();
    private OnPageLoad.Main onPageLoad;
    private OnPageLoad.Materiais onMateriaisLoad;

    public JavaScriptWebView(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void handleHome(String html_p) {
        new Thread(() -> {

            Log.i("JavaScriptWebView", "Home handling...");

            Document homePage = Jsoup.parse(html_p);
            Element drawer_msg = homePage.getElementsByClass("titulo").get(1);
            SharedPreferences.Editor editor = context.getSharedPreferences(Utils.LOGIN_INFO, MODE_PRIVATE).edit();
            editor.putString(Utils.LOGIN_NAME, drawer_msg.text().substring(drawer_msg.text().lastIndexOf(",") + 2, drawer_msg.text().indexOf(" !")));
            editor.putLong(Utils.LAST_LOGIN, new Date().getTime());
            editor.apply();

            webView.pg_home_loaded = true;
            Log.i("JavaScriptWebView", "Home handled!");

            onPageLoad.onPageFinish(URL + PG_HOME);

        }).start();
    }

    @JavascriptInterface
    public void handleDiarios(String html_p) {
        getBox().runInTxAsync(() -> {

            Log.i("JavaScriptWebView", "DiariosList handling...");

            Box<Materia> materiaBox = getBox().boxFor(Materia.class);
            Box<Etapa> etapaBox = getBox().boxFor(Etapa.class);
            Box<Diarios> diariosBox = getBox().boxFor(Diarios.class);

            Document document = Jsoup.parse(html_p);

            Elements table_diarios = document.getElementsByTag("tbody").eq(12);

            int numMaterias = table_diarios.select("table.conteudoTexto").size();

            Element nxtElem = null;

            Elements options = document.getElementsByTag("option");

            webView.data_year = new String[options.size() - 1];

            for (int i = 0; i < options.size() - 1; i++) {
                webView.data_year[i] = trimb(options.get(i + 1).text());
            }

            SharedPreferences.Editor editor = context.getSharedPreferences(LOGIN_INFO, MODE_PRIVATE).edit();
            JSONArray jsonArray = new JSONArray();

            for (String z : webView.data_year) {
                jsonArray.put((String) z);
            }

            editor.putString(YEARS, jsonArray.toString());
            editor.apply();

            for (int y = 0; y < numMaterias; y++) {
                if (table_diarios.select("table.conteudoTexto").eq(y).parents().eq(0).parents().eq(0).next().eq(0) != null) {
                    nxtElem = table_diarios.select("table.conteudoTexto").eq(y).parents().eq(0).parents().eq(0).next().eq(0).first();
                }

                String nomeMateria = table_diarios.select("table.conteudoTexto").eq(y).parents().eq(0).parents().eq(0).first().child(0).text();
                nomeMateria = nomeMateria.substring(nomeMateria.indexOf("-") + 2, nomeMateria.indexOf("("));
                nomeMateria = nomeMateria.substring(nomeMateria.indexOf("-") + 2);

                Materia materia = materiaBox.query()
                        .equal(Materia_.name, nomeMateria)
                        .equal(Materia_.year, Integer.valueOf(webView.data_year[webView.year_position]))
                        .build().findUnique();

                if (materia == null) {
                    materia = new Materia(nomeMateria, pickColor(nomeMateria),
                            Integer.valueOf(webView.data_year[webView.year_position]));
                }

                String nome_etapa = "";

                if (nxtElem != null) {
                    nome_etapa = nxtElem.child(0).child(0).ownText();
                }

                while (nome_etapa.contains("Etapa")) {
                    if (nome_etapa.equals("1a. Etapa") || nome_etapa.equals("1ª Etapa")) {
                        nome_etapa = context.getResources().getString(R.string.diarios_PrimeiraEtapa);
                    } else if (nome_etapa.equals("1a Reavaliação da 1a Etapa") || nome_etapa.equals("1ª Reavaliação da 1ª Etapa")) {
                        nome_etapa = context.getResources().getString(R.string.diarios_RP1_PrimeiraEtapa);
                    } else if (nome_etapa.equals("2a Reavaliação da 1a Etapa") || nome_etapa.equals("2ª Reavaliação da 1ª Etapa")) {
                        nome_etapa = context.getResources().getString(R.string.diarios_RP2_PrimeiraEtapa);
                    } else if (nome_etapa.equals("2a. Etapa") || nome_etapa.equals("2ª Etapa")) {
                        nome_etapa = context.getResources().getString(R.string.diarios_SegundaEtapa);
                    } else if (nome_etapa.equals("1a Reavaliação da 2a Etapa") || nome_etapa.equals("1ª Reavaliação da 2ª Etapa")) {
                        nome_etapa = context.getResources().getString(R.string.diarios_RP1_SegundaEtapa);
                    } else if (nome_etapa.equals("2a Reavaliação da 2a Etapa") || nome_etapa.equals("2ª Reavaliação da 2ª Etapa")) {
                        nome_etapa = context.getResources().getString(R.string.diarios_RP2_SegundaEtapa);
                    }

                    Element tabelaNotas = Objects.requireNonNull(nxtElem).child(0).child(1).child(0);
                    Elements notasLinhas = tabelaNotas.getElementsByClass("conteudoTexto");
                    nxtElem = nxtElem.nextElementSibling();

                    List<Etapa> etapas = materia.etapas;
                    Etapa etapa = null;

                    for (int i = 0; i < etapas.size(); i++) {
                        if (etapas.get(i).getEtapa().equals(nome_etapa)) {
                            etapa = etapaBox.get(etapas.get(i).getId());
                            break;
                        }
                    }

                    if (etapa == null) {
                        etapa = new Etapa(nome_etapa);
                    }

                    if (nxtElem != null) {
                        nome_etapa = nxtElem.child(0).child(0).text();
                    } else {
                        nome_etapa = "";
                    }

                    for (int i = 0; i < etapa.diarios.size(); i++) {
                        diariosBox.remove(etapa.diarios.get(i).getId());
                    }

                    etapa.diarios.clear();

                    for (int i = 0; i < notasLinhas.size(); i++) {
                        String data = notasLinhas.eq(i).first().child(1).text().substring(0, 10);
                        String titulo = notasLinhas.eq(i).first().child(1).text();
                        String tipo = context.getResources().getString(R.string.sigla_Avaliacao);
                        int tint = context.getResources().getColor(R.color.diarios_avaliacao);
                        if (titulo.contains("Prova")) {
                            tint = context.getResources().getColor(R.color.diarios_prova);
                            tipo = context.getResources().getString(R.string.sigla_Prova);
                        } else if (titulo.contains("Diarios") || titulo.contains("Trabalho")) {
                            tint = context.getResources().getColor(R.color.diarios_trabalho);
                            tipo = context.getResources().getString(R.string.sigla_Trabalho);
                        } else if (titulo.contains("Qualitativa")) {
                            tint = context.getResources().getColor(R.color.diarios_qualitativa);
                            tipo = context.getResources().getString(R.string.sigla_Qualitativa);
                        } else if (titulo.contains("Exercício")) {
                            tint = context.getResources().getColor(R.color.diarios_exercicio);
                            tipo = context.getResources().getString(R.string.sigla_Exercicio);
                        }

                        String caps = trimp(trim1(notasLinhas.eq(i).first().child(1).text()));
                        String nome = caps.substring(1, 2).toUpperCase() + caps.substring(2);
                        String peso = trimp(notasLinhas.eq(i).first().child(2).text());
                        String max = trimp(notasLinhas.eq(i).first().child(3).text());
                        String nota = trimp(notasLinhas.eq(i).first().child(4).text());

                        if (nota.equals("")) {
                            nota = " -";
                        }

                        Diarios diario = new Diarios(nome, peso, max, nota, tipo, data, tint);

                        diario.etapa.setTarget(etapa);
                        etapa.diarios.add(diario);
                        diariosBox.put(diario);

                        Log.v("Box for Diarios", "size of " + diariosBox.count());
                    }

                    etapa.materia.setTarget(materia);
                    materia.etapas.add(etapa);
                    etapaBox.put(etapa);
                    Log.v("Box for Etapa", "size of " + etapaBox.count());
                }
                materiaBox.put(materia);
                Log.v("Box for Materia", "size of " + materiaBox.count());
            }

            if (webView.pg_diarios_loaded.length == 1) {
                webView.pg_diarios_loaded = new boolean[options.size() - 1];
                webView.pg_diarios_loaded[0] = true;
            }
        }, (result, error) -> {
            if (error == null) {
                webView.pg_diarios_loaded[webView.year_position] = true;
                Log.i("JavaScriptWebView", "Diarios handled!");
                onPageLoad.onPageFinish(URL + PG_DIARIOS);
            } else {
                Log.e("BoxStore", error.getMessage());
                onPageLoad.onErrorRecived(error.getMessage());
            }
        });
    }

    @JavascriptInterface
    public void handleBoletim(String html_p) {
        getBox().runInTxAsync(() -> {

            Log.i("JavaScriptWebView", "Boletim handling...");

            Box<Materia> materiaBox = getBox().boxFor(Materia.class);
            Box<Etapa> etapaBox = getBox().boxFor(Etapa.class);

            String[][] trtd_boletim;
            Document document = Jsoup.parse(html_p);

            Element table_boletim = document.select("table").get(6);

            Element table_notas = table_boletim.select("table").get(7);

            Elements tables = table_notas.children();

            Document ano = Jsoup.parse(document.select("#cmbanos").first().toString());
            Elements options = ano.select("option");

            for (Element table : tables) {
                Elements trs = table.select("tr");
                trtd_boletim = new String[trs.size()][];
                for (int i = 2; i < trs.size(); i++) {
                    Elements tds = trs.get(i).select("td");
                    trtd_boletim[i] = new String[tds.size()];
                    for (int j = 0; j < tds.size(); j++) {
                        if (tds.get(j).text().equals("") || tds.get(j).text().startsWith(",")) {
                            trtd_boletim[i][j] = "-";
                        } else {
                            trtd_boletim[i][j] = tds.get(j).text();
                        }
                    }

                    String nomeMateria = trtd_boletim[i][0].trim();
                    String tfaltas = trtd_boletim[i][3].trim();
                    String notaPrimeiraEtapa = trtd_boletim[i][5].trim();
                    String faltasPrimeiraEtapa = trtd_boletim[i][6].trim();
                    String RPPrimeiraEtapa = trtd_boletim[i][7].trim();
                    String notaFinalPrimeiraEtapa = trtd_boletim[i][9].trim();
                    String notaSegundaEtapa = trtd_boletim[i][10].trim();
                    String faltasSegundaEtapa = trtd_boletim[i][11].trim();
                    String RPSegundaEtapa = trtd_boletim[i][12].trim();
                    String notaFinalSegundaEtapa = trtd_boletim[i][14].trim();

                    Materia materia = materiaBox.query()
                            .equal(Materia_.name, nomeMateria)
                            .equal(Materia_.year, Integer.valueOf(webView.data_year[webView.year_position]))
                            .build().findUnique();

                    if (materia != null) {
                        materia.setTotalFaltas(tfaltas);

                        for (int j = 0; j < materia.etapas.size(); j++) {
                            Etapa etapa = materia.etapas.get(j);

                            if (etapa.getEtapa().equals(context.getResources().getString(R.string.diarios_PrimeiraEtapa))) {
                                etapa.setNota(notaPrimeiraEtapa);
                                etapa.setFaltas(faltasPrimeiraEtapa);
                                etapa.setNotaFinal(notaFinalPrimeiraEtapa);
                                etapa.setNotaRP(RPPrimeiraEtapa);
                            } else if (etapa.getEtapa().equals(context.getResources().getString(R.string.diarios_SegundaEtapa))) {
                                etapa.setNota(notaSegundaEtapa);
                                etapa.setFaltas(faltasSegundaEtapa);
                                etapa.setNotaFinal(notaFinalSegundaEtapa);
                                etapa.setNotaRP(RPSegundaEtapa);
                            }

                            etapa.materia.setTarget(materia);
                            materia.etapas.removeById(etapa.id);
                            materia.etapas.add(etapa);
                            etapaBox.put(etapa);
                            Log.v("Box for etapa", "size of " + etapaBox.count());
                        }
                        materiaBox.put(materia);
                        Log.v("Box for materia", "size of " + materiaBox.count());
                    }
                }
            }

            if (webView.pg_boletim_loaded.length == 1) {
                webView.pg_boletim_loaded = new boolean[options.size()];
                webView.pg_boletim_loaded[0] = true;
            }

        }, (result, error) -> {
            if (error == null) {
                webView.pg_boletim_loaded[webView.year_position] = true;
                Log.i("JavaScriptWebView", "Boletim handled!");
                onPageLoad.onPageFinish(URL + PG_BOLETIM);
            } else {
                Log.e("BoxStore", error.getMessage());
                onPageLoad.onErrorRecived(error.getMessage());
            }
        });
    }

    @JavascriptInterface
    public void handleHorario(String html_p) {
        getBox().runInTxAsync(() -> {

            Log.i("JavaScriptWebView", "Horario handling...");

            Box<Materia> materiaBox = getBox().boxFor(Materia.class);
            Box<Horario> horarioBox = getBox().boxFor(Horario.class);

            String[][] trtd_horario = null;
            String[] code = null;
            Document document = Jsoup.parse(html_p);

            //Element table_horario = document.select("table").get(11).getElementsByTag("tbody").get(0);
            Element table_horario = document.select("table").eq(11).first();

            Element table_codes = document.select("table").get(12);
            Elements codes = table_codes.children();

            Document ano = Jsoup.parse(document.select("#cmbanos").first().toString());
            Elements options = ano.select("option");

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

            List<Materia> materialist = materiaBox.query().equal(Materia_.year,
                    Integer.valueOf(webView.data_year[webView.year_position])).build().find();

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
                                .equal(Materia_.year, Integer.valueOf(webView.data_year[webView.year_position]))
                                .build().findUnique();

                        Horario horario = new Horario(Integer.valueOf(trtd_horario[0][i]), trtd_horario[j][0]);

                        if (materia != null) {
                            horario.materia.setTarget(materia);
                            materia.horarios.add(horario);
                            materiaBox.put(materia);
                            horarioBox.put(horario);
                            Log.v("Box for horario", "size of " + horarioBox.count());
                            Log.v("Box for materia", "size of " + materiaBox.count());
                        }
                    }
                }
            }

            if (webView.pg_horario_loaded.length == 1) {
                webView.pg_horario_loaded = new boolean[options.size()];
                webView.pg_horario_loaded[0] = true;
            }

        }, (result, error) -> {
            if (error == null) {
                webView.pg_horario_loaded[webView.year_position] = true;
                Log.i("JavaScriptWebView", "Horario handled!");
                onPageLoad.onPageFinish(URL + PG_HORARIO);
            } else {
                Log.e("BoxStore", error.getMessage());
                onPageLoad.onErrorRecived(error.getMessage());
            }
        });
    }

    @JavascriptInterface
    public void handleMateriais(String html_p) {
        new Thread() {
            @Override
            public void run() {

                Log.i("JavaScriptWebView", "Materiais handling...");

                try {
                    Document document = Jsoup.parse(html_p);
                    Element table = document.getElementsByTag("tbody").get(10);
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

                    Elements options = document.getElementsByTag("option");

                    if (webView.pg_materiais_loaded.length == 1) {
                        webView.pg_materiais_loaded = new boolean[options.size() - 1];
                        webView.pg_materiais_loaded[0] = true;
                    }

                    webView.pg_materiais_loaded[webView.year_position] = true;
                    Log.i("JavaScriptWebView", "Materiais handled!");

                    onPageLoad.onPageFinish(URL + MATERIAS);
                    onMateriaisLoad.onPageFinish(materiais);

                } catch (Exception e) {
                    Log.e("JavaScriptWebView", "Materiais error: " + e.getMessage());
                    onPageLoad.onErrorRecived(e.getMessage());
                }
            }
        }.start();
    }

    @JavascriptInterface
    public void handleCalendario(String html_p) {

        getBox().runInTxAsync(() -> {

            Log.i("JavaScriptWebView", "Calendario handling...");

            Box<Mes> mesBox = getBox().boxFor(Mes.class);
            Box<Evento> eventoBox = getBox().boxFor(Evento.class);
            Box<Dia> diaBox = getBox().boxFor(Dia.class);
            Box<Materia> materiaBox = getBox().boxFor(Materia.class);

            mesBox.removeAll();
            eventoBox.removeAll();
            diaBox.removeAll();

            Document document = Jsoup.parse(html_p);

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

                                    int cor = corQA.equals("#F0F0F0") ? pickColor(title) : context.getResources().getColor(R.color.colorPrimary);//pickColor(corQA);

                                    Evento evento = new Evento(title, description, cor, false);

                                    if (ev.getTimeInMillis() <= today.getTimeInMillis()) {
                                        evento.setHappened(true);
                                    }

                                    Materia materia = materiaBox.query()
                                            .equal(Materia_.name, title)
                                            .equal(Materia_.year, Integer.valueOf(webView.data_year[webView.year_position]))
                                            .build().findUnique();

                                    Log.i("Eve", infos + " " + description + " " + numeroDia + "/" + (numMes + 1) + "/" + year);

                                    evento.day.setTarget(dia);
                                    dia.eventos.add(evento);

                                    if (materia != null) {
                                        evento.materia.setTarget(materia);
                                        materia.eventos.add(evento);
                                        materiaBox.put(materia);
                                        Log.v("Box for materia", "size of " + materiaBox.count());
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

                                        Evento evento = new Evento(infos, description,
                                                context.getResources().getColor(R.color.colorPrimary), data_inicio, data_fim, false);
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
                                        Log.v("Box for eventos", "size of " + eventoBox.count());

                                        Log.i("Eve", infos + " " + description + " " + numeroDia + "/" + (numMes + 1) + "/" + year + " " + data_inicio + "~" + data_fim);
                                    }
                                }
                            }
                        }
                        dia.mes.setTarget(mes);
                        diaBox.put(dia);
                        mes.days.add(dia);
                        Log.v("Box for dias", "size of " + diaBox.count());
                    }
                }
                mesBox.put(mes);
                mesesList.add(mes.getMonth());
                Log.v("Box for meses", "size of " + mesBox.count());
            }
        }, (result, error) -> {
            if (error == null) {
                webView.pg_calendario_loaded = true;
                Log.i("JavaScriptWebView", "Calendario handled!");
                onPageLoad.onPageFinish(URL + PG_CALENDARIO);
            } else {
                Log.e("BoxStore", error.getMessage());
                onPageLoad.onErrorRecived(error.getMessage());
            }
        });
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

    private int pickColor(String string){
        int color = 0;

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
        } else if (string.contains("Portugu")) {
            color = context.getResources().getColor(R.color.portugues);
        } else if (string.contains("Matemática")) {
            color = context.getResources().getColor(R.color.matematica);
        } else if (string.contains("Química")) {
            color = context.getResources().getColor(R.color.quimica);
        } else if (string.contains("Sociologia")) {
            color = context.getResources().getColor(R.color.sociologia);
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
            Materia materia = getBox().boxFor(Materia.class).query().equal(Materia_.name, string).build().findUnique();

            if (materia != null) {
                color = materia.getColor();
            }

            if (color == 0) {
                color = Utils.getRandomColorGenerator(context);
            }
        }

        return color;
    }

    private BoxStore getBox() {
        return SingletonWebView.getInstance().box;
    }

    private static String trimb(String string) {
        string = string.substring(0, 4);
        return string;
    }

    public void setOnPageLoadListener(OnPageLoad.Main onPageLoad) {
        this.onPageLoad = onPageLoad;
    }

    public void setOnMateriaisLoadListener(OnPageLoad.Materiais onMateriaisLoad) {
        this.onMateriaisLoad = onMateriaisLoad;
    }
}
