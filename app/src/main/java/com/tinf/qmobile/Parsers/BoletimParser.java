package com.tinf.qmobile.Parsers;

import android.os.AsyncTask;
import android.util.Log;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Class.Materias.Etapa;
import com.tinf.qmobile.Class.Materias.Materia;
import com.tinf.qmobile.Class.Materias.Materia_;
import com.tinf.qmobile.Utilities.User;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import io.objectbox.Box;

import static com.tinf.qmobile.Network.OnResponse.PG_BOLETIM;

public class BoletimParser extends AsyncTask<String, Void, Void> {
    private final static String TAG = "BoletimParser";
    private OnFinish onFinish;
    private int year;
    private boolean notify;

    public BoletimParser(int year, boolean notify, OnFinish onFinish) {
        this.year = year;
        this.notify = notify;
        this.onFinish = onFinish;

        Log.i(TAG, "New instance");
    }

    @Override
    protected Void doInBackground(String... page) {
        App.getBox().runInTx(() -> {

            Log.i(TAG, "Parsing " + User.getYear(year));

            Box<Materia> materiaBox = App.getBox().boxFor(Materia.class);
            Box<Etapa> etapaBox = App.getBox().boxFor(Etapa.class);

            Document document = Jsoup.parse(page[0]);

            Element table_notas = document.getElementsByTag("form").get(0).nextElementSibling().nextElementSibling().child(0);
            int numero_materias = table_notas.childNodeSize();
            Elements materias = table_notas.getElementsByTag("tr");

            for (int i = 2; i < numero_materias -1; i++) {
                String nomeMateria = formatTd(materias.get(i).child(0).text()).trim();
                String tfaltas = formatTd(materias.get(i).child(3).text()).trim();
                String notaPrimeiraEtapa = formatTd(materias.get(i).child(5).text()).trim();
                String faltasPrimeiraEtapa = formatTd(materias.get(i).child(6).text()).trim();
                String RPPrimeiraEtapa = formatTd(materias.get(i).child(7).text()).trim();
                String notaFinalPrimeiraEtapa = formatTd(materias.get(i).child(9).text()).trim();
                String notaSegundaEtapa = formatTd(materias.get(i).child(10).text()).trim();
                String faltasSegundaEtapa = formatTd(materias.get(i).child(11).text()).trim();
                String RPSegundaEtapa = formatTd(materias.get(i).child(12).text()).trim();
                String notaFinalSegundaEtapa = formatTd(materias.get(i).child(14).text()).trim();

                Materia materia = materiaBox.query()
                        .equal(Materia_.name, nomeMateria).and()
                        .equal(Materia_.year, User.getYear(year))
                        .build().findFirst();

                if (materia != null) {
                    materia.setFaltas(tfaltas);

                    for (int j = 0; j < materia.etapas.size(); j++) {
                        Etapa etapa = materia.etapas.get(j);

                        String nota, faltas, nf, rp;
                        boolean hasNota = false, hasFaltas = false, hasNf = false, hasRp = false;

                        if (etapa.getEtapa() == Etapa.Tipo.PRIMEIRA.getInt()) {
                            nota = notaPrimeiraEtapa;
                            faltas = faltasPrimeiraEtapa;
                            nf = notaFinalPrimeiraEtapa;
                            rp = RPPrimeiraEtapa;

                        } else if (etapa.getEtapa() == Etapa.Tipo.SEGUNDA.getInt()) {
                            nota = notaSegundaEtapa;
                            faltas = faltasSegundaEtapa;
                            nf = notaFinalSegundaEtapa;
                            rp = RPSegundaEtapa;

                        } else break;

                        if (!etapa.getNota().equals(nota)) {
                            etapa.setNota(nota);
                            hasNota = true;
                        }

                        if (!etapa.getFaltas().equals(faltas)) {
                            etapa.setFaltas(faltas);
                            hasFaltas = true;
                        }

                        if (!etapa.getNotaFinal().equals(nf)) {
                            etapa.setNotaFinal(nf);
                            hasNf = true;
                        }

                        if (!etapa.getNotaRP().equals(rp)) {
                            etapa.setNotaRP(rp);
                            hasRp = true;
                        }

                        etapa.materia.setTarget(materia);
                        etapaBox.put(etapa);

                        if (notify) {
                            String msg = "";

                            if (hasNota) {
                                msg = msg.concat("Nota");
                            }

                            if (hasFaltas) {
                                msg = msg.concat("Faltas");
                            }

                            if (hasNf) {
                                msg = msg.concat("Nota Final");
                            }
                            if (hasRp) {
                                msg = msg.concat("Nota RP");
                            }

                            //TODO notificação
                        }
                    }
                    materiaBox.put(materia);
                }
            }
        });
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        onFinish.onFinish(PG_BOLETIM, year);
    }

    public interface OnFinish {
        void onFinish(int pg, int year);
    }

    private String formatTd(String text){
        return text.startsWith(",") ? "" : text;
    }
}
