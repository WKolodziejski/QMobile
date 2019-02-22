package com.tinf.qmobile.Parsers;

import android.os.AsyncTask;
import android.util.Log;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Class.Materiais.Material;
import com.tinf.qmobile.Class.Materiais.Material_;
import com.tinf.qmobile.Class.Materias.Horario;
import com.tinf.qmobile.Class.Materias.Materia;
import com.tinf.qmobile.Class.Materias.Materia_;
import com.tinf.qmobile.Utilities.User;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

import io.objectbox.Box;

import static com.tinf.qmobile.Network.OnResponse.PG_MATERIAIS;
import static com.tinf.qmobile.Utilities.User.REGISTRATION;

public class MateriaisParser extends AsyncTask<String, Void, Void> {
    private final static String TAG = "MateriaisParser";
    private OnFinish onFinish;
    private int pos;
    private boolean notify;

    public MateriaisParser(int pos, boolean notify, OnFinish onFinish) {
        this.pos = pos;
        this.notify = notify;
        this.onFinish = onFinish;

        Log.i(TAG, "New instance");
    }

    @Override
    protected Void doInBackground(String... page) {

        Log.i(TAG, "Parsing");

        Box<Materia> materiaBox = App.getBox().boxFor(Materia.class);
        Box<Material> materiaisBox = App.getBox().boxFor(Material.class);

        Document document = Jsoup.parse(page[0]);
        Element table = document.getElementsByTag("tbody").get(10);
        Elements rotulos = table.getElementsByClass("rotulo");

        for (int i = 1; i < rotulos.size(); i++) {

            String str = rotulos.get(i).text();
            str = str.substring(str.indexOf('-') + 2, str.indexOf('('));
            str = str.substring(str.indexOf('-') + 2);
            String nomeMateria = str;

            Materia materia = materiaBox.query()
                    .equal(Materia_.name, nomeMateria).and()
                    .equal(Materia_.year, User.getYear(pos)).and()
                    .equal(Materia_.period, User.getPeriod(pos))
                    .build().findFirst();

            if (materia != null) {
                String classe = rotulos.get(i).nextElementSibling().className();
                Element element = rotulos.get(i).nextElementSibling();

                while (classe.equals("conteudoTexto")) {

                    String data = element.child(0).text().trim();
                    String link = element.child(1).child(1).attr("href");
                    String title = element.child(1).child(1).text().trim();
                    String descricao = "";

                    if (element.child(1).children().size() > 2) {
                        descricao = element.child(1).child(3).nextSibling().toString().trim();
                    }

                    Material material = new Material(title, data, descricao, link);

                    Material search = materiaisBox.query().equal(Material_.title, title).and()
                            .equal(Material_.date, data).and()
                            .equal(Material_.link, link).build().findFirst();

                    if (search == null) {
                        material.materia.setTarget(materia);
                        materia.materiais.add(material);
                        materiaisBox.put(material);

                        if (notify) {
                            //TODO notificação
                        }
                    }

                    if (element.nextElementSibling() != null) {
                        element = element.nextElementSibling();
                        classe = element.className();
                    } else {
                        classe = "";
                    }
                }
                materiaBox.put(materia);
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        onFinish.onFinish(PG_MATERIAIS, pos);
    }

    public interface OnFinish {
        void onFinish(int pg, int year);
    }

}
