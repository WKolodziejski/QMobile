package com.tinf.qmobile.model.message;

import com.tinf.qmobile.R;
import com.tinf.qmobile.model.Queryable;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;
import static com.tinf.qmobile.model.Queryable.ViewType.ATTACHMENT;

@Entity
public class Attachment implements Queryable {
    @Id public long id;
    private String title_;
    private String obs_;
    private String url_;
    public ToOne<Message> message;

    public Attachment(String title_, String obs_, String url_) {
        this.title_ = title_;
        this.obs_ = obs_;
        this.url_ = url_;
    }

    public int getIcon() {
        if (title_.contains(".")) {
            return parseIcon(title_.substring(title_.lastIndexOf(".")).toLowerCase());
        } else {
            return R.drawable.ic_file;
        }
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
        } else if (ext.equals(".jpg") || ext.equals(".jpeg")) {
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

    public String getTitle() {
        return title_ == null ? "" : title_;
    }

    public String getObs() {
        return obs_ == null ? "" : obs_;
    }

    /*
     * Required methods
     */

    public Attachment() {}

    public String getTitle_() {
        return title_;
    }

    public String getObs_() {
        return obs_;
    }

    public String getUrl_() {
        return url_;
    }

    @Override
    public int getItemType() {
        return ATTACHMENT;
    }

}
