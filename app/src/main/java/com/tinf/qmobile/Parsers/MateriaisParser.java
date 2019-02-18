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
                String extension = link.substring(link.lastIndexOf("."));

                if (element.child(1).children().size() > 2) {
                    descricao = element.child(1).child(3).nextSibling().toString().trim();
                }

                material.add(new Materiais(data, nomeConteudo, link, descricao, extension, getIcon(extension)));

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

    private int getIcon(String extension) {
        int icon = R.drawable.ic_file;

        if (extension.equalsIgnoreCase(".pdf")) {
            icon = R.drawable.ic_pdf;
        } else if (extension.equalsIgnoreCase(".doc") || extension.equalsIgnoreCase(".docx")) {
            icon = R.drawable.ic_doc;
        } else if (extension.equalsIgnoreCase(".ppt") || extension.equalsIgnoreCase(".pptx")) {
            icon = R.drawable.ic_ppt;
        } else if (extension.equalsIgnoreCase(".xls") || extension.equalsIgnoreCase(".xlsx")) {
            icon = R.drawable.ic_xls;
        } else if (extension.equalsIgnoreCase(".zip")) {
            icon = R.drawable.ic_zip;
        } else if (extension.equalsIgnoreCase(".rtf")) {
            icon = R.drawable.ic_rtf;
        } else if (extension.equalsIgnoreCase(".txt")) {
            icon = R.drawable.ic_txt;
        } else if (extension.equalsIgnoreCase(".csv")) {
            icon = R.drawable.ic_csv;
        } else if (extension.equalsIgnoreCase(".svg")) {
            icon = R.drawable.ic_svg;
        } else if (extension.equalsIgnoreCase(".rar") || extension.equalsIgnoreCase(".7z")) {
            icon = R.drawable.ic_comp;
        } else if (extension.equalsIgnoreCase(".css")) {
            icon = R.drawable.ic_css;
        } else if (extension.equalsIgnoreCase(".dbf")) {
            icon = R.drawable.ic_dbf;
        } else if (extension.equalsIgnoreCase(".dwg")) {
            icon = R.drawable.ic_dwg;
        } else if (extension.equalsIgnoreCase(".exe")) {
            icon = R.drawable.ic_exe;
        } else if (extension.equalsIgnoreCase(".fla")) {
            icon = R.drawable.ic_fla;
        } else if (extension.equalsIgnoreCase(".html")) {
            icon = R.drawable.ic_html;
        } else if (extension.equalsIgnoreCase(".xml")) {
            icon = R.drawable.ic_xml;
        } else if (extension.equalsIgnoreCase(".iso")) {
            icon = R.drawable.ic_iso;
        } else if (extension.equalsIgnoreCase(".js")) {
            icon = R.drawable.ic_js;
        } else if (extension.equalsIgnoreCase(".jpg")) {
            icon = R.drawable.ic_jpg;
        } else if (extension.equalsIgnoreCase(".json")) {
            icon = R.drawable.ic_json;
        } else if (extension.equalsIgnoreCase(".mp3")) {
            icon = R.drawable.ic_mp3;
        } else if (extension.equalsIgnoreCase(".mp4")) {
            icon = R.drawable.ic_mp4;
        } else if (extension.equalsIgnoreCase(".ai")) {
            icon = R.drawable.ic_ai;
        } else if (extension.equalsIgnoreCase(".avi")) {
            icon = R.drawable.ic_avi;
        } else if (extension.equalsIgnoreCase(".png")) {
            icon = R.drawable.ic_png;
        } else if (extension.equalsIgnoreCase(".psd")) {
            icon = R.drawable.ic_psd;
        }
        return icon;
    }

}
