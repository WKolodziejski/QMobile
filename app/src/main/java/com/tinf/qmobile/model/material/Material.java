package com.tinf.qmobile.model.material;

import android.os.Environment;

import com.tinf.qmobile.R;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.utility.Design;
import com.tinf.qmobile.utility.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToOne;

import static com.tinf.qmobile.model.ViewType.MATERIAL;
import static com.tinf.qmobile.network.Client.pos;
import static com.tinf.qmobile.service.DownloadReceiver.PATH;

@Entity
public class Material implements Queryable {
    @Transient public boolean isDownloaded;
    @Transient public boolean isDownloading;
    @Transient public boolean isSelected;
    @Transient public boolean highlight;
    @Id public long id;
    private String title;
    private String link;
    private String description;
    private long date;
    private boolean seen_;
    public ToOne<Matter> matter;

    public Material(String title, long date, String description, String link, boolean seen) {
        this.title = title;
        this.date = date;
        this.description = description;
        this.link = link;
        this.seen_ = seen;
    }

    public String getDateString() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return format.format(new Date(getDate()));
    }

    public int getIcon() {
        if (link.contains(".")) {
            return Design.parseIcon(link.substring(link.lastIndexOf(".")).toLowerCase());
        } else {
            return R.drawable.ic_file;
        }
    }

    public String getFileName() {
        /*String name = format(title);

        if (link.contains(".")) {
            name += link.substring(link.lastIndexOf(".")).toLowerCase();
        }

        return name;*/

        return link.substring(link.lastIndexOf("/") + 1);

    }

    private String format(String input) {
        return input.replaceAll("[:\\\\/*\"%?|<>'.]", "-");
    }

    public void see() {
        seen_ = true;
    }

    public String getMatter() {
        return matter.getTarget().getTitle();
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
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                + PATH + "/" + User.getYear(pos) + "/" + User.getPeriod(pos) + "/" + getFileName();
    }

    public boolean isSeen_() {
        return seen_;
    }

    @Override
    public int getItemType() {
        return MATERIAL;
    }

}
