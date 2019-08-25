package com.tinf.qmobile.parser;

import android.os.AsyncTask;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.tinf.qmobile.App;
import com.tinf.qmobile.BuildConfig;
import com.tinf.qmobile.model.materiais.Material;
import com.tinf.qmobile.model.materiais.Material_;
import com.tinf.qmobile.model.matter.Journal;
import com.tinf.qmobile.model.matter.Journal_;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.utility.User;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import io.objectbox.Box;
import io.objectbox.exception.NonUniqueResultException;
import io.objectbox.query.QueryBuilder;

import static com.tinf.qmobile.model.calendario.Utils.getDate;
import static com.tinf.qmobile.network.OnResponse.PG_MATERIAIS;

public class MateriaisParser extends AsyncTask<String, Void, Void> {
    private final static String TAG = "MateriaisParser";
    private OnFinish onFinish;
    private int pos;
    private boolean notify;

    public MateriaisParser(int pos, boolean notify, OnFinish onFinish) {
        this.pos = pos;
        this.notify = notify;
        this.onFinish = onFinish;
    }

    @Override
    protected Void doInBackground(String... page) {

        Log.i(TAG, "Parsing");

        Box<Matter> materiaBox = App.getBox().boxFor(Matter.class);
        Box<Material> materiaisBox = App.getBox().boxFor(Material.class);

        Document document = Jsoup.parse(page[0]);
        Element table = document.getElementsByTag("tbody").get(10);
        Elements rotulos = table.getElementsByClass("rotulo");

        if (!BuildConfig.DEBUG) {
            Crashlytics.log(Log.ERROR, TAG, table.toString());
        }

        for (int i = 1; i < rotulos.size(); i++) {

            String description = rotulos.get(i).text();

            Matter materia = materiaBox.query()
                    .equal(Matter_.description_, description).and()
                    .equal(Matter_.year_, User.getYear(pos)).and()
                    .equal(Matter_.period_, User.getPeriod(pos))
                    .build().findUnique();

            if (materia != null) {
                String classe = rotulos.get(i).nextElementSibling().className();
                Element element = rotulos.get(i).nextElementSibling();

                while (classe.equals("conteudoTexto")) {

                    String dataString = element.child(0).text().trim();
                    String link = element.child(1).child(1).attr("href");
                    String title = element.child(1).child(1).text().trim();

                    String descricao = "";

                    if (element.child(1).children().size() > 2) {
                        descricao = element.child(1).child(3).nextSibling().toString().trim();
                    }

                    long date = getDate(dataString, false);

                    Material search = null;

                    try {
                        QueryBuilder<Material> builder = materiaisBox.query()
                                .equal(Material_.title, title).and()
                                .between(Material_.date, date, date).and()
                                .equal(Material_.link, link);

                        builder.link(Material_.matter)
                                .equal(Matter_.description_, description).and()
                                .equal(Matter_.year_, User.getYear(pos)).and()
                                .equal(Matter_.period_, User.getPeriod(pos));

                        search = builder.build().findUnique();
                    } catch (NonUniqueResultException e) {
                        e.printStackTrace();
                    }

                    if (search == null) {

                        Material material = new Material(title, date, descricao, link);

                        material.matter.setTarget(materia);
                        materia.materials.add(material);
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
