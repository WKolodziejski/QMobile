package com.tinf.qmobile.Parsers;

import android.os.AsyncTask;
import android.util.Log;

import com.tinf.qmobile.App;
import com.tinf.qmobile.Class.Materias.Etapa;
import com.tinf.qmobile.Class.Materias.Materia;
import com.tinf.qmobile.Class.Materias.Materia_;
import com.tinf.qmobile.Interfaces.OnResponse;
import com.tinf.qmobile.R;
import com.tinf.qmobile.Utilities.User;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import io.objectbox.Box;

import static com.tinf.qmobile.Network.Client.PG_BOLETIM;

public class BoletimParser extends AsyncTask<String, Void, Void> {
    private final static String TAG = "BoletimParser";
    private int year;
    private boolean notify;
    private OnResponse onResponse;

    public BoletimParser(int year, boolean notify, OnResponse onResponse) {
        this.year = year;
        this.notify = notify;
        this.onResponse = onResponse;

        Log.i(TAG, "New instance");
    }

    @Override
    protected Void doInBackground(String... page) {
        App.getBox().runInTx(() -> {

            Log.i(TAG, "Parsing");

            Box<Materia> materiaBox = App.getBox().boxFor(Materia.class);
            Box<Etapa> etapaBox = App.getBox().boxFor(Etapa.class);

            String[][] trtd_boletim;
            Document document = Jsoup.parse(page[0]);
            //Element table_notas = document.getElementsByTag("table").get(13).child(0);
            //O proximo se baseia no form acima da table, pode ser melhor caso eles modifiquem o site e coloquem uma table a mais
            Element table_notas = document.getElementsByTag("form").get(0).nextElementSibling().nextElementSibling().child(0);
            int numero_materias = table_notas.childNodeSize();
            Elements materias = table_notas.getElementsByTag("tr");

            //Document ano = Jsoup.parse(document.select("#cmbanos").first().toString());
            //Elements options = ano.select("option");

            for (int i = 2; i < numero_materias -1; i++) {
                String nomeMateria = formatTd(materias.get(i).child(0).text());
                String tfaltas = formatTd(materias.get(i).child(3).text());
                String notaPrimeiraEtapa = formatTd(materias.get(i).child(5).text());
                String faltasPrimeiraEtapa = formatTd(materias.get(i).child(6).text());
                String RPPrimeiraEtapa = formatTd(materias.get(i).child(7).text());
                String notaFinalPrimeiraEtapa = formatTd(materias.get(i).child(9).text());
                String notaSegundaEtapa = formatTd(materias.get(i).child(10).text());
                String faltasSegundaEtapa = formatTd(materias.get(i).child(11).text());
                String RPSegundaEtapa = formatTd(materias.get(i).child(12).text());
                String notaFinalSegundaEtapa = formatTd(materias.get(i).child(14).text());

                Materia materia = materiaBox.query()
                        .equal(Materia_.name, nomeMateria).and()
                        .equal(Materia_.year, User.getYear(year))
                        .build().findFirst();

                if (materia != null) {
                    materia.setFaltas(tfaltas);

                    /*if (materia.etapas.isEmpty()) {
                        materia.etapas.add(new Etapa(R.string.diarios_PrimeiraEtapa));
                        materia.etapas.add(new Etapa(R.string.diarios_SegundaEtapa));

                    } else if (materia.etapas.size() == 2) {
                        if (materia.etapas.get(1).getEtapa() != R.string.diarios_SegundaEtapa) {
                            materia.etapas.add(new Etapa(R.string.diarios_SegundaEtapa));
                        }
                    }*/

                    for (int j = 0; j < materia.etapas.size(); j++) {
                        Etapa etapa = materia.etapas.get(j);

                        if (etapa.getEtapa() == Etapa.Tipo.PRIMEIRA.getInt()) {
                            etapa.setNota(notaPrimeiraEtapa);
                            etapa.setFaltas(faltasPrimeiraEtapa);
                            etapa.setNotaFinal(notaFinalPrimeiraEtapa);
                            etapa.setNotaRP(RPPrimeiraEtapa);
                        } else if (etapa.getEtapa() == Etapa.Tipo.SEGUNDA.getInt()) {
                            etapa.setNota(notaSegundaEtapa);
                            etapa.setFaltas(faltasSegundaEtapa);
                            etapa.setNotaFinal(notaFinalSegundaEtapa);
                            etapa.setNotaRP(RPSegundaEtapa);
                        }

                        etapa.materia.setTarget(materia);
                        //materia.etapas.removeById(etapa.id);
                        //materia.etapas.add(etapa);
                        etapaBox.put(etapa);
                        //Log.v("Box for etapa", "size of " + etapaBox.count());
                    }
                    materiaBox.put(materia);
                    //Log.v("Box for materia", "size of " + materiaBox.count());
                }
            }

        });
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        onResponse.onFinish(PG_BOLETIM, year);
    }

    private String formatTd(String text){
        return text.startsWith(",") ? "" : text;
    }
}
