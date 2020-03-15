package com.tinf.qmobile.parser;

import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.network.Client;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ClassParser extends BaseParser {
    private final static String TAG = "ClassParser";
    private final Matter matter;

    public ClassParser(Matter matter, int page, int pos, boolean notify, BaseParser.OnFinish onFinish, OnError onError) {
        super(page, pos, notify, onFinish, onError);
        this.matter = matter;
    }

    @Override
    public void parse(Document document) {

        Element tableMatters = document.getElementsByTag("tbody").get(12);


        for (int i = 0; i < tableMatters.select("table.conteudoTexto").size(); i++) {
            Element nxtElem = null;

            if (tableMatters.select("table.conteudoTexto").eq(i).parents().eq(0).parents().eq(0).next().eq(0) != null) {
                nxtElem = tableMatters.select("table.conteudoTexto").eq(i).parents().eq(0).parents().eq(0).next().eq(0).first();
            }

            String description = tableMatters.select("table.conteudoTexto").eq(i).parents().eq(0).parents().eq(0).first().child(0).text();


            while (nxtElem != null && nxtElem.child(0).child(0).is("div")) {

                String periodTitle = nxtElem.child(0).child(0).ownText();
                Element tableGrades = nxtElem.child(0).child(1).child(0);
                Elements rowGrades = tableGrades.getElementsByClass("conteudoTexto");
                nxtElem = nxtElem.nextElementSibling();


                for (int j = 0; j < rowGrades.size(); j++) {
                    String infos = rowGrades.eq(j).first().child(1).text();




                    }
                }
            }

    }

}
