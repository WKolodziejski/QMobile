package com.tinf.qmobile.Class.Materiais;

import com.tinf.qmobile.Class.Materias.Matter;
import com.tinf.qmobile.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToOne;

@Entity
public class Material {
    @Transient public boolean isDownloaded;
    @Id public long id;
    private String title;
    private String link;
    private String description;
    private long date;
    private String path;
    private String mime;
    public ToOne<Matter> matter;

    public Material(String title, long date, String description, String link) {
        this.title = title;
        this.date = date;
        this.description = description;
        this.link = link;
    }

    public String getDateString() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return format.format(new Date(getDate()));
    }

    public int getIcon() {
        return getIcon(link.substring(link.lastIndexOf(".")).toLowerCase());
    }

    public String getFileName() {
        return format(title) + link.substring(link.lastIndexOf(".")).toLowerCase();
    }

    private int getIcon(String extension) {
        int icon = R.drawable.ic_file;

        if (extension.equals(".pdf")) {
            icon = R.drawable.ic_pdf;
        } else if (extension.contains(".doc")) {
            icon = R.drawable.ic_doc;
        } else if (extension.contains(".ppt")) {
            icon = R.drawable.ic_ppt;
        } else if (extension.contains(".xls")) {
            icon = R.drawable.ic_xls;
        } else if (extension.equals(".zip")) {
            icon = R.drawable.ic_zip;
        } else if (extension.equals(".rtf")) {
            icon = R.drawable.ic_rtf;
        } else if (extension.equals(".txt")) {
            icon = R.drawable.ic_txt;
        } else if (extension.equals(".csv")) {
            icon = R.drawable.ic_csv;
        } else if (extension.equals(".svg")) {
            icon = R.drawable.ic_svg;
        } else if (extension.equals(".rar") || extension.equals(".7z")) {
            icon = R.drawable.ic_comp;
        } else if (extension.equals(".css")) {
            icon = R.drawable.ic_css;
        } else if (extension.equals(".dbf")) {
            icon = R.drawable.ic_dbf;
        } else if (extension.equals(".dwg")) {
            icon = R.drawable.ic_dwg;
        } else if (extension.equals(".exe")) {
            icon = R.drawable.ic_exe;
        } else if (extension.equals(".fla")) {
            icon = R.drawable.ic_fla;
        } else if (extension.equals(".html")) {
            icon = R.drawable.ic_html;
        } else if (extension.equals(".xml")) {
            icon = R.drawable.ic_xml;
        } else if (extension.equals(".iso")) {
            icon = R.drawable.ic_iso;
        } else if (extension.equals(".js")) {
            icon = R.drawable.ic_js;
        } else if (extension.equals(".jpg")) {
            icon = R.drawable.ic_jpg;
        } else if (extension.equals(".json")) {
            icon = R.drawable.ic_json;
        } else if (extension.equals(".mp3")) {
            icon = R.drawable.ic_mp3;
        } else if (extension.equals(".mp4")) {
            icon = R.drawable.ic_mp4;
        } else if (extension.equals(".ai")) {
            icon = R.drawable.ic_ai;
        } else if (extension.equals(".avi")) {
            icon = R.drawable.ic_avi;
        } else if (extension.equals(".png")) {
            icon = R.drawable.ic_png;
        } else if (extension.equals(".psd")) {
            icon = R.drawable.ic_psd;
        }
        return icon;
    }

    private String format(String input) {
        return input.replaceAll("[:\\\\/*\"%?|<>'.]", "-");
    }

    /*
     * Auto-generated methods
     */

    public Material() {}

    public long getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

}
