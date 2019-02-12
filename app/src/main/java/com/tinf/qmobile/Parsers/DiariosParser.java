package com.tinf.qmobile.Parsers;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.tinf.qmobile.App;
import com.tinf.qmobile.Class.Materias.Diarios;
import com.tinf.qmobile.Class.Materias.Diarios_;
import com.tinf.qmobile.Class.Materias.Etapa;
import com.tinf.qmobile.Class.Materias.Materia;
import com.tinf.qmobile.Class.Materias.Materia_;
import com.tinf.qmobile.Interfaces.OnResponse;
import com.tinf.qmobile.Network.Client;
import com.tinf.qmobile.R;
import com.tinf.qmobile.Utilities.Jobs;
import com.tinf.qmobile.Utilities.User;
import com.tinf.qmobile.Utilities.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Objects;
import io.objectbox.Box;
import io.objectbox.BoxStore;

import static com.tinf.qmobile.Network.Client.PG_DIARIOS;
import static com.tinf.qmobile.Utilities.Utils.pickColor;

public class DiariosParser extends AsyncTask<String, Void, Void> {
    private final static String TAG = "DiariosParser";
    private OnFinish onFinish;
    private int year;
    private boolean notify;

    public DiariosParser(int year, boolean notify, OnFinish onFinish) {
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
            Box<Etapa> etapaBox = App.getBox().boxFor(Etapa.class);
            Box<Diarios> diariosBox = App.getBox().boxFor(Diarios.class);

            Document document = Jsoup.parse(page[0]);

            Elements table_diarios = document.getElementsByTag("tbody").eq(12);

            int numMaterias = table_diarios.select("table.conteudoTexto").size();

            Element nxtElem = null;

            Elements options = document.getElementsByTag("option");

            String[] years = new String[options.size() - 1];

            for (int i = 0; i < options.size() - 1; i++) {
                years[i] = trimb(options.get(i + 1).text());
            }

            User.setYears(years);

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
                        .equal(Materia_.year, User.getYear(year))
                        .build().findFirst();

                if (materia == null) {
                    materia = new Materia(nomeMateria, pickColor(nomeMateria), User.getYear(year));
                    for (int i = 0; i < Etapa.Tipo.values().length; i++) {
                        Etapa etapa = new Etapa(Etapa.Tipo.values()[i].getInt());
                        etapa.materia.setTarget(materia);
                        materia.etapas.add(etapa);
                        etapaBox.put(etapa);
                    }
                }

                String nome_etapa = "";
                int id_nome_etapa = 0;

                if (nxtElem != null) {
                    nome_etapa = nxtElem.child(0).child(0).ownText();
                }

                while (nome_etapa.contains("Etapa")) {
                    if (nome_etapa.equals("1a. Etapa") || nome_etapa.equals("1ª Etapa")) {
                        id_nome_etapa = Etapa.Tipo.PRIMEIRA.getInt();
                    } else if (nome_etapa.equals("1a Reavaliação da 1a Etapa") || nome_etapa.equals("1ª Reavaliação da 1ª Etapa")) {
                        id_nome_etapa =Etapa.Tipo.PRIMEIRA_RP1.getInt();
                    } else if (nome_etapa.equals("2a Reavaliação da 1a Etapa") || nome_etapa.equals("2ª Reavaliação da 1ª Etapa")) {
                        id_nome_etapa = Etapa.Tipo.PRIMEIRA_RP2.getInt();
                    } else if (nome_etapa.equals("2a. Etapa") || nome_etapa.equals("2ª Etapa")) {
                        id_nome_etapa = Etapa.Tipo.SEGUNDA.getInt();
                    } else if (nome_etapa.equals("1a Reavaliação da 2a Etapa") || nome_etapa.equals("1ª Reavaliação da 2ª Etapa")) {
                        id_nome_etapa = Etapa.Tipo.SEGUNDA_RP1.getInt();
                    } else if (nome_etapa.equals("2a Reavaliação da 2a Etapa") || nome_etapa.equals("2ª Reavaliação da 2ª Etapa")) {
                        id_nome_etapa = Etapa.Tipo.SEGUNDA_RP2.getInt();
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

                            if (notify) {
                                Jobs.displayNotification(materia.getName(), new_diario.getNome(),
                                        App.getContext().getResources().getString(R.string.title_diarios), (int) new_diario.id, intent.getExtras());
                            }
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
        });

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        onFinish.onFinish(PG_DIARIOS, year);
    }

    public interface OnFinish {
        void onFinish(int pg, int year);
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

    private String trimb(String string) {
        string = string.substring(0, 4);
        return string;
    }

}
