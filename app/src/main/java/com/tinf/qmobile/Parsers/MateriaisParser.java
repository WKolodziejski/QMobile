package com.tinf.qmobile.Parsers;

import android.os.AsyncTask;
import android.util.Log;

import com.tinf.qmobile.Class.Materiais.Materiais;
import com.tinf.qmobile.Class.Materiais.MateriaisList;
import com.tinf.qmobile.Network.OnMateriaisLoad;
import com.tinf.qmobile.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import static com.tinf.qmobile.Utilities.Utils.getRandomColorGenerator;

public class MateriaisParser extends AsyncTask<String, Void, List<MateriaisList>> {
    private final static String TAG = "MateriaisParser";
    private boolean notify;
    private OnMateriaisLoad onMateriaisLoad;

    public MateriaisParser(boolean notify, OnMateriaisLoad onMateriaisLoad) {
        this.notify = notify;
        this.onMateriaisLoad = onMateriaisLoad;

        Log.i(TAG, "New instance");
    }

    @Override
    protected List<MateriaisList> doInBackground(String... page) {

        Log.i(TAG, "Parsing");

        Document document = Jsoup.parse(page[0]);
        Element table = document.getElementsByTag("tbody").get(10);
        Elements rotulos = table.getElementsByClass("rotulo");

        List<MateriaisList> materiais = new ArrayList<>();

        for (int i = 1; i < rotulos.size(); i++) {

            String str = rotulos.get(i).text();
            str = str.substring(str.indexOf('-') + 2, str.indexOf('('));
            str = str.substring(str.indexOf('-') + 2);
            String nomeMateria = str;

            String classe = rotulos.get(i).nextElementSibling().className();
            Element element = rotulos.get(i).nextElementSibling();

            List<Materiais> material = new ArrayList<>();

            while (classe.equals("conteudoTexto")) {

                String data = element.child(0).text().trim();
                String link = element.child(1).child(1).attr("href");
                String nomeConteudo = formatTd(element.child(1).child(1).text().trim());
                String descricao = "";
                String extension = link.substring(link.indexOf("."));

                if (element.child(1).children().size() > 2) {
                    descricao = element.child(1).child(3).nextSibling().toString().trim();
                }

                int color = R.color.materiais_file;
                int img = R.drawable.ic_file;

                if (extension.equals(".pdf")) {
                    color = R.color.materiais_pdf;
                    img = R.drawable.ic_pdf;
                } else if (extension.equals(".docx") || extension.equals(".doc")
                        || extension.equals(".txt") || extension.equals(".rtf")) {
                    color = R.color.materiais_doc;
                    img = R.drawable.ic_docs;
                } else if (extension.equals(".csv") || extension.equals(".svg") | extension.equals(".xls")) {
                    color = R.color.materiais_table;
                    img = R.drawable.ic_table;
                } else if (extension.equals(".zip") || extension.equals(".rar")
                        || extension.equals(".7z")) {
                    color = R.color.materiais_zip;
                    img = R.drawable.ic_compressed;
                } else if (extension.equals(".mp3") || extension.equals(".wav")
                        || extension.equals(".wma")) {
                    color = R.color.materiais_audio;
                    img = R.drawable.ic_song;
                } else if (extension.equals(".mp4") || extension.equals(".wmv")
                        || extension.equals(".avi")) {
                    color = R.color.materiais_video;
                    img = R.drawable.ic_video;
                } else if (extension.equals(".jpg") || extension.equals(".png")) {
                    color = R.color.materiais_image;
                    img = R.drawable.ic_picture;
                } else if (extension.equals(".jar") || extension.equals(".php")
                        || extension.equals(".html") || extension.equals(".css")
                        || extension.equals(".js") || extension.equals(".json")
                        || extension.equals(".xml") || extension.equals(".c")) {
                    color = R.color.materiais_script;
                    img = R.drawable.ic_script;
                }

                material.add(new Materiais(data, nomeConteudo, link, descricao, color, img));

                if (element.nextElementSibling() != null) {
                    element = element.nextElementSibling();
                    classe = element.className();
                } else {
                    classe = "";
                }
            }
            materiais.add(new MateriaisList(nomeMateria, material, getRandomColorGenerator()));
        }

        return materiais;
    }

    @Override
    protected void onPostExecute(List<MateriaisList> materiaisLists) {
        super.onPostExecute(materiaisLists);
        onMateriaisLoad.onMateriaisLoad(materiaisLists);
    }

    private String formatTd(String text){
        return text.replace("/", "-");
    }

}
