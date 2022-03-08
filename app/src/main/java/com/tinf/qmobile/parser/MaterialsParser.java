package com.tinf.qmobile.parser;

import static com.tinf.qmobile.model.ViewType.MATERIAL;
import static com.tinf.qmobile.network.OnResponse.PG_MATERIALS;
import static io.objectbox.query.QueryBuilder.StringOrder.CASE_INSENSITIVE;

import android.content.Intent;
import android.util.Log;

import com.tinf.qmobile.App;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.MainActivity;
import com.tinf.qmobile.model.material.Material;
import com.tinf.qmobile.model.material.Material_;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.NotificationUtils;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Calendar;
import java.util.Locale;

import io.objectbox.exception.NonUniqueResultException;
import io.objectbox.query.QueryBuilder;

public class MaterialsParser extends BaseParser {
    private final static String TAG = "MateriaisParser";

    public MaterialsParser(int page, int year, int period, boolean notify, BaseParser.OnFinish onFinish, OnError onError) {
        super(page, year, period, notify, onFinish, onError);
    }

    @Override
    public void parse(Document document) {
        Log.i(TAG, "Parsing");

        Element table = document.getElementsByTag("tbody").get(10);
        Elements rotulos = table.getElementsByClass("rotulo");

        QueryBuilder<Material> b = materialsBox.query();

        b.link(Material_.matter)
                .equal(Matter_.year_, year).and()
                .equal(Matter_.period_, period);

        boolean isFirstParse = b.build().find().isEmpty();

        for (int i = 1; i < rotulos.size(); i++) {

            String description = rotulos.get(i).text();

            Matter matter = matterBox.query()
                    .contains(Matter_.description_, description, CASE_INSENSITIVE).and()
                    .equal(Matter_.year_, year).and()
                    .equal(Matter_.period_, period)
                    .build().findUnique();

            if (matter != null) {
                String classe = rotulos.get(i).nextElementSibling().className();
                Element element = rotulos.get(i).nextElementSibling();

                while (classe.equals("conteudoTexto")) {

                    String dataString = element.child(0).text().trim();
                    String link = Client.get().getURL() + element.child(1).child(1).attr("href");
                    String title = element.child(1).child(1).text().trim();

                    String descricao = "";

                    if (element.child(1).children().size() > 2) {
                        descricao = element.child(1).child(3).nextSibling().toString().trim();
                    }

                    long date = getDate(dataString);

                    try {
                        QueryBuilder<Material> builder = materialsBox.query()
                                .equal(Material_.title, title, CASE_INSENSITIVE).and()
                                .between(Material_.date, date, date).and()
                                .equal(Material_.link, link, CASE_INSENSITIVE);

                        builder.link(Material_.matter)
                                .equal(Matter_.id, matter.id);

                        Material search = builder.build().findUnique();

                        if (search == null) {

                            Material material = new Material(title, date, descricao, link, isFirstParse);

                            material.matter.setTarget(matter);
                            matter.materials.add(material);
                            materialsBox.put(material);

                            if (notify) {
                                sendNotification(material);
                            }
                        }

                    } catch (NonUniqueResultException e) {
                        e.printStackTrace();
                    }

                    if (element.nextElementSibling() != null) {
                        element = element.nextElementSibling();
                        classe = element.className();
                    } else {
                        classe = "";
                    }
                }
                matterBox.put(matter);
            }
        }
    }

    private long getDate(String s) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.YEAR, Integer.parseInt(s.substring(s.lastIndexOf("/") + 1, s.lastIndexOf("/") + 5)));
        cal.set(Calendar.MONTH, Integer.parseInt(s.substring(s.indexOf("/") + 1, s.lastIndexOf("/"))) - 1);
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(s.substring(0, s.indexOf("/"))));

        return cal.getTimeInMillis();
    }

    private void sendNotification(Material material) {
        Intent intent = new Intent(App.getContext(), MainActivity.class);
        intent.putExtra("FRAGMENT", PG_MATERIALS);

        NotificationUtils.show(
                String.format(Locale.getDefault(),
                        App.getContext().getResources().getString(R.string.notification_material_title),
                        material.matter.getTarget().getTitle()),
                material.getTitle(),
                MATERIAL, (int) material.id, intent);
    }

}
