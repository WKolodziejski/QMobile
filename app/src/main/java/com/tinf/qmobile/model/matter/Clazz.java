package com.tinf.qmobile.model.matter;

import static com.tinf.qmobile.model.ViewType.CLASS;

import com.tinf.qmobile.App;
import com.tinf.qmobile.R;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.calendar.EventBase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToOne;

@Entity
public class Clazz extends EventBase implements Queryable {
    @Transient public boolean highlight;
    private long date_;
    private int classesCount_;
    private int absences_;
    private String teacher_;
    private String content_;
    public ToOne<Period> period;

    public Clazz(long date_, int classesCount_, int absences_, String teacher_, String content_, Period period) {
        super(content_, date_);
        this.date_ = date_;
        this.classesCount_ = classesCount_;
        this.absences_ = absences_;
        this.teacher_ = teacher_;
        this.content_ = content_;
        this.period.setTarget(period);
    }

    public void setAbsences(int absences_) {
        this.absences_ = absences_;
    }

    public void setContent(String content_) {
        this.content_ = content_;
    }

    public String formatDate() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return format.format(new Date(date_));
    }

    public String getTeacher() {
        return teacher_ == null ? "" : teacher_;
    }

    public String getContent() {
        return content_ == null ? App.getContext().getResources().getString(R.string.class_no_content) : content_;
    }

    public Date getDate() {
        return new Date(date_);
    }

    public String getMatter() {
        return period.getTarget().matter.getTarget().getTitle();
    }

    public String getPeriod() {
        return period.getTarget().getTitle();
    }

    @Override
    public int getColor() {
        return period.getTarget().matter.getTarget().getColor();
    }

    @Override
    public int getItemType() {
        return CLASS;
    }

    /*
     * Required methods
     */

    public Clazz() {}

    public long getDate_() {
        return date_;
    }

    public int getClassesCount_() {
        return classesCount_;
    }

    public int getAbsences_() {
        return absences_;
    }

    public String getTeacher_() {
        return teacher_;
    }

    public String getContent_() {
        return content_;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public boolean isSame(Queryable queryable) {
        return queryable.equals(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Clazz)) return false;
        if (!super.equals(o)) return false;
        Clazz clazz = (Clazz) o;
        return  getDate_() == clazz.getDate_() &&
                getClassesCount_() == clazz.getClassesCount_() &&
                getAbsences_() == clazz.getAbsences_() &&
                Objects.equals(getTeacher_(), clazz.getTeacher_()) &&
                Objects.equals(getContent_(), clazz.getContent_());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), date_, classesCount_, absences_, teacher_, content_);
    }

}
