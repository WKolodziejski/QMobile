package com.tinf.qmobile.model.material;

import com.tinf.qmobile.R;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.matter.Matter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToOne;

import static com.tinf.qmobile.model.Queryable.ViewType.MATERIAL;

@Entity
public class Material implements Queryable {
    @Transient public boolean isDownloaded;
    @Id public long id;
    private String title;
    private String link;
    private String description;
    private long date;
    private String path;
    private String mime;
    private boolean seen_;
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
        if (link.contains(".")) {
            return parseIcon(link.substring(link.lastIndexOf(".")).toLowerCase());
        } else {
            return R.drawable.ic_file;
        }
    }

    public String getFileName() {
        String name = format(title);

        if (link.contains(".")) {
            name += link.substring(link.lastIndexOf(".")).toLowerCase();
        }

        return name;
    }

    private int parseIcon(String ext) {
        int icon = R.drawable.ic_file;

        if (ext.equals(".pdf")) {
            icon = R.drawable.ic_pdf;
        } else if (ext.contains(".doc")) {
            icon = R.drawable.ic_doc;
        } else if (ext.contains(".ppt")) {
            icon = R.drawable.ic_ppt;
        } else if (ext.contains(".xls")) {
            icon = R.drawable.ic_xls;
        } else if (ext.equals(".zip")) {
            icon = R.drawable.ic_zip;
        } else if (ext.equals(".rtf")) {
            icon = R.drawable.ic_rtf;
        } else if (ext.equals(".txt")) {
            icon = R.drawable.ic_txt;
        } else if (ext.equals(".csv")) {
            icon = R.drawable.ic_csv;
        } else if (ext.equals(".svg")) {
            icon = R.drawable.ic_svg;
        } else if (ext.equals(".rar") || ext.equals(".7z")) {
            icon = R.drawable.ic_comp;
        } else if (ext.equals(".css")) {
            icon = R.drawable.ic_css;
        } else if (ext.equals(".dbf")) {
            icon = R.drawable.ic_dbf;
        } else if (ext.equals(".dwg")) {
            icon = R.drawable.ic_dwg;
        } else if (ext.equals(".exe")) {
            icon = R.drawable.ic_exe;
        } else if (ext.equals(".fla")) {
            icon = R.drawable.ic_fla;
        } else if (ext.equals(".html")) {
            icon = R.drawable.ic_html;
        } else if (ext.equals(".xml")) {
            icon = R.drawable.ic_xml;
        } else if (ext.equals(".iso")) {
            icon = R.drawable.ic_iso;
        } else if (ext.equals(".js")) {
            icon = R.drawable.ic_js;
        } else if (ext.equals(".jpg")) {
            icon = R.drawable.ic_jpg;
        } else if (ext.equals(".json")) {
            icon = R.drawable.ic_json;
        } else if (ext.equals(".mp3")) {
            icon = R.drawable.ic_mp3;
        } else if (ext.equals(".mp4")) {
            icon = R.drawable.ic_mp4;
        } else if (ext.equals(".ai")) {
            icon = R.drawable.ic_ai;
        } else if (ext.equals(".avi")) {
            icon = R.drawable.ic_avi;
        } else if (ext.equals(".png")) {
            icon = R.drawable.ic_png;
        } else if (ext.equals(".psd")) {
            icon = R.drawable.ic_psd;
        }
        return icon;
    }

    private String format(String input) {
        return input.replaceAll("[:\\\\/*\"%?|<>'.]", "-");
    }

    public void see() {
        seen_ = true;
    }

    /*
     * Required methods
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

    public boolean isSeen_() {
        return seen_;
    }

    @Override
    public int getItemType() {
        return MATERIAL;
    }

}
