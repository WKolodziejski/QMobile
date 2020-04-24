package com.tinf.qmobile.model.message;

import com.tinf.qmobile.model.Queryable;

import java.text.SimpleDateFormat;
import java.util.Locale;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;

import static com.tinf.qmobile.model.Queryable.ViewType.MESSAGE;

@Entity
public class Message implements Queryable {
    @Id public long id;
    private int uid_;
    private float date_;
    private String subject_;
    private String sender_;
    private String text_;
    public ToMany<Attachment> attachments;

    public Message(int uid_, float date_, String subject_, String sender_) {
        this.uid_ = uid_;
        this.date_ = date_;
        this.subject_ = subject_;
        this.sender_ = sender_;
    }

    public void setText(String text) {
        this.text_ = text;
    }

    public String formatDate() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return format.format(date_);
    }

    /*
     * Required methods
     */

    public Message() {}

    public int getUid_() {
        return uid_;
    }

    public float getDate_() {
        return date_;
    }

    public String getSubject_() {
        return subject_;
    }

    public String getSender_() {
        return sender_;
    }

    public String getText_() {
        return text_;
    }

    @Override
    public int getItemType() {
        return MESSAGE;
    }

}
