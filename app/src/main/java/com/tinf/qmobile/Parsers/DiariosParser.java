package com.tinf.qmobile.Parsers;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Class.Materias.Diarios;
import com.tinf.qmobile.Class.Materias.Diarios_;
import com.tinf.qmobile.Class.Materias.Etapa;
import com.tinf.qmobile.Class.Materias.Materia;
import com.tinf.qmobile.Class.Materias.Materia_;
import com.tinf.qmobile.Interfaces.Network.OnResponse;
import com.tinf.qmobile.R;
import com.tinf.qmobile.Utilities.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import static com.tinf.qmobile.Utilities.Utils.trim1;
import static com.tinf.qmobile.Utilities.Utils.trimp;

public class DiariosParser extends AsyncTask<Void, Void, Void> {
    private final WeakReference<Context> context;
    private String page;
    private int year;
    private BoxStore boxStore;
    private OnResponse onResponse;

    public DiariosParser(Context context, String page, int year, BoxStore boxStore, OnResponse onResponse) {
        this.context = new WeakReference<>(context);
        this.page = page;
        this.year = year;
        this.boxStore = boxStore;
        this.onResponse = onResponse;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        boxStore.runInTxAsync(() -> {

            Log.i("DiariosParser", "DiariosList handling...");

            Box<Materia> materiaBox = boxStore.boxFor(Materia.class);
            Box<Etapa> etapaBox = boxStore.boxFor(Etapa.class);
            Box<Diarios> diariosBox = boxStore.boxFor(Diarios.class);

            Document document = Jsoup.parse(page);

            Elements table_diarios = document.getElementsByTag("tbody").eq(12);

            int numMaterias = table_diarios.select("table.conteudoTexto").size();

            Element nxtElem = null;

            Elements options = document.getElementsByTag("option");

            String[] years = new String[options.size() - 1];

            /*for (int i = 0; i < options.size() - 1; i++) {
                years[i] = Utils.trimb(options.get(i + 1).text());
            }

            NetworkSingleton.getInstance(context.get()).setYears(context.get(), years);*/

            for (int y = 0; y < numMaterias; y++) {
                if (table_diarios.select("table.conteudoTexto").eq(y).parents().eq(0).parents().eq(0).next().eq(0) != null) {
                    nxtElem = table_diarios.select("table.conteudoTexto").eq(y).parents().eq(0).parents().eq(0).next().eq(0).first();
                }

                String nomeMateria = table_diarios.select("table.conteudoTexto").eq(y).parents().eq(0).parents().eq(0).first().child(0).text();
                nomeMateria = nomeMateria.substring(nomeMateria.indexOf("-") + 2, nomeMateria.indexOf("("));
                nomeMateria = nomeMateria.substring(nomeMateria.indexOf("-") + 2);

                Materia materia = materiaBox.query()
                        .equal(Materia_.name, nomeMateria)
                        .and()
                        .equal(Materia_.year, year)
                        .build().findFirst();

                if (materia == null) {
                    materia = new Materia(nomeMateria, Utils.pickColor(nomeMateria, boxStore), year);
                }

                String nome_etapa = "";
                int id_nome_etapa = 0;

                if (nxtElem != null) {
                    nome_etapa = nxtElem.child(0).child(0).ownText();
                }

                while (nome_etapa.contains("Etapa")) {
                    if (nome_etapa.equals("1a. Etapa") || nome_etapa.equals("1ª Etapa")) {
                        id_nome_etapa = R.string.diarios_PrimeiraEtapa;
                    } else if (nome_etapa.equals("1a Reavaliação da 1a Etapa") || nome_etapa.equals("1ª Reavaliação da 1ª Etapa")) {
                        id_nome_etapa = R.string.diarios_RP1_PrimeiraEtapa;
                    } else if (nome_etapa.equals("2a Reavaliação da 1a Etapa") || nome_etapa.equals("2ª Reavaliação da 1ª Etapa")) {
                        id_nome_etapa =R.string.diarios_RP2_PrimeiraEtapa;
                    } else if (nome_etapa.equals("2a. Etapa") || nome_etapa.equals("2ª Etapa")) {
                        id_nome_etapa = R.string.diarios_SegundaEtapa;
                    } else if (nome_etapa.equals("1a Reavaliação da 2a Etapa") || nome_etapa.equals("1ª Reavaliação da 2ª Etapa")) {
                        id_nome_etapa = R.string.diarios_RP1_SegundaEtapa;
                    } else if (nome_etapa.equals("2a Reavaliação da 2a Etapa") || nome_etapa.equals("2ª Reavaliação da 2ª Etapa")) {
                        id_nome_etapa = R.string.diarios_RP2_SegundaEtapa;
                    }

                    Element tabelaNotas = Objects.requireNonNull(nxtElem).child(0).child(1).child(0);
                    Elements notasLinhas = tabelaNotas.getElementsByClass("conteudoTexto");
                    nxtElem = nxtElem.nextElementSibling();

                    List<Etapa> etapas = materia.etapas;
                    Etapa etapa = null;

                    for (int i = 0; i < etapas.size(); i++) {
                        if (etapas.get(i).getEtapa() == id_nome_etapa) {
                            etapa = etapaBox.get(etapas.get(i).getId());
                            break;
                        }
                    }

                    if (etapa == null) {
                        etapa = new Etapa(id_nome_etapa);
                    }

                    if (nxtElem != null) {
                        nome_etapa = nxtElem.child(0).child(0).text();
                    } else {
                        nome_etapa = "";
                    }

                    /*for (int i = 0; i < etapa.diarios.size(); i++) {
                        diariosBox.remove(etapa.diarios.get(i).id);
                    }

                    etapa.diarios.clear();*/

                    for (int i = 0; i < notasLinhas.size(); i++) {
                        String data = notasLinhas.eq(i).first().child(1).text().substring(0, 10);
                        String titulo = notasLinhas.eq(i).first().child(1).text();
                        int tipo = Diarios.Tipo.AVALIACAO.getInt();
                        int tint = R.color.colorAccent;
                        if (titulo.contains("Prova")) {
                            //tint = R.color.diarios_prova;
                            tipo = Diarios.Tipo.PROVA.getInt();
                        } else if (titulo.contains("Diarios") || titulo.contains("Trabalho")) {
                            //tint = R.color.diarios_trabalho;
                            tipo = Diarios.Tipo.TRABALHO.getInt();
                        } else if (titulo.contains("Qualitativa")) {
                            //tint = R.color.diarios_qualitativa;
                            tipo = Diarios.Tipo.QUALITATIVA.getInt();
                        } else if (titulo.contains("Exercício")) {
                            //tint = R.color.diarios_exercicio;
                            tipo = Diarios.Tipo.EXERCICIO.getInt();
                        }

                        String caps = trimp(trim1(notasLinhas.eq(i).first().child(1).text()));
                        String nome = caps.substring(1, 2).toUpperCase() + caps.substring(2);
                        String peso = trimp(notasLinhas.eq(i).first().child(2).text());
                        String max = trimp(notasLinhas.eq(i).first().child(3).text());
                        String nota = trimp(notasLinhas.eq(i).first().child(4).text());

                        if (nota.equals("")) {
                            nota = " -";
                        }

                        Diarios new_diario = new Diarios(nome, peso, max, nota, tipo, data, tint);

                        Diarios search = diariosBox.query().equal(Diarios_.nome, nome).and()
                                .equal(Diarios_.data, data).and().equal(Diarios_.tipo, tipo).and()
                                .equal(Diarios_.nota, nota).build().findFirst();

                        if (search == null) {
                            new_diario.etapa.setTarget(etapa);
                            etapa.diarios.add(new_diario);
                            diariosBox.put(new_diario);

                            Intent intent = new Intent();
                            intent.putExtra("NAME", materia.getName());
                            intent.putExtra("YEAR", materia.getYear());

                            Utils.displayNotification(context.get(), materia.getName(), new_diario.getNome(), "Diarios", (int) new_diario.id, intent.getExtras());

                            Log.i("Diarios", "novo");
                        }
                        //Log.v("Box for Diarios", "size of " + diariosBox.count());
                    }

                    etapa.materia.setTarget(materia);
                    materia.etapas.add(etapa);
                    etapaBox.put(etapa);
                    //Log.v("Box for Etapa", "size of " + etapaBox.count());
                }
                materiaBox.put(materia);
                //Log.v("Box for Materia", "size of " + materiaBox.count());
            }

            /*if (webView.pg_diarios_loaded.length == 1) {
                webView.pg_diarios_loaded = new boolean[options.size() - 1];
                webView.pg_diarios_loaded[0] = true;
            }*/
        }, (result, error) -> {
            if (error == null) {
                Log.i("JavaScriptWebView", "Diarios handled!");
                onResponse.OnFinish("", "");
            } else {
                Log.e("BoxStore", error.getCause().getMessage());
                Crashlytics.logException(error.getCause());
                onResponse.OnFinish("", error.getCause().getMessage());
            }
        });
        return null;
    }

}
