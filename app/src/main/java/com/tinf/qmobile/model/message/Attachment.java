package com.tinf.qmobile.model.message;

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
