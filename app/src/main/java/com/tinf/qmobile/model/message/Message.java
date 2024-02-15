package com.tinf.qmobile.model.message;

import static com.tinf.qmobile.model.ViewType.MESSAGE;

import com.tinf.qmobile.model.Queryable;

import java.text.SimpleDateFormat;
import java.util.Locale;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

@Entity
public class Message implements Queryable {
  @Id
  public long id;
  @Transient
  public boolean highlight;
  private int uid_;
  private float date_;
  private boolean seen_;
  private boolean hasAtt_;
  private boolean isSolved_;
  private String subject_;
  private String text_;
  public ToMany<Attachment> attachments;
  public ToOne<Sender> sender;

  private static final SimpleDateFormat format = new SimpleDateFormat("dd MMM", Locale.getDefault());

  public Message(int uid_, float date_, String subject_, Sender sender, boolean hasAtt_) {
    this.uid_ = uid_;
    this.date_ = date_;
    this.subject_ = subject_;
    this.hasAtt_ = hasAtt_;
    this.sender.setTarget(sender);
  }

  public void setText(String text) {
    this.text_ = text;
  }

  public String formatDate() {
    return format.format(date_);
  }

  public String getContent() {
    return text_ == null ? "" : text_;
  }

  public void see() {
    seen_ = true;
  }

  public void setSeen(boolean seen) {
    this.seen_ = seen;
  }

  public void setSolved(boolean isSolved) {
    this.isSolved_ = isSolved;
  }

  public String getPreview() {
    return text_ == null ? "" : text_.substring(0, Math.min(text_.length(), 50));
  }

  public String getSender() {
    return sender.getTarget().getName_();
  }

  public int getColor() {
    return sender.getTarget().getColor_();
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

  public String getText_() {
    return text_;
  }

  public boolean isSeen_() {
    return seen_;
  }

  public boolean isHasAtt_() {
    return hasAtt_;
  }

  public boolean isSolved_() {
    return isSolved_;
  }

  @Override
  public int getItemType() {
    return MESSAGE;
  }

  @Override
  public long getId() {
    return id;
  }

  @Override
  public boolean isSame(Queryable queryable) {
    return queryable.equals(this);
  }

}
