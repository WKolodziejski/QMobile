package com.tinf.qmobile.model.message;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;

@Entity
public class Sender {
  @Id
  public long id;
  private int color_;
  private String name_;
  public ToMany<Message> messages;

  public Sender(int color_, String name_) {
    this.color_ = color_;
    this.name_ = name_;
  }

  public String getSign() {
    return String.valueOf(name_.charAt(0));
  }

  /*
   * Required methods
   */

  public Sender() {}

  public int getColor_() {
    return color_;
  }

  public String getName_() {
    return name_;
  }

}
