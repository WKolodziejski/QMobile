package com.tinf.qmobile.model.matter;

import static com.tinf.qmobile.model.ViewType.HEADER;

import android.util.Log;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.material.Material;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.internal.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;

@Entity
public class Matter implements Queryable {
    //@Transient public boolean isExpanded;
    //@Transient public boolean shouldAnimate;
    @Id public long id;
    @ColorInt private int color_;
    private String title_;
    private String description_;
    private String situation_;
    private String teacher_;
    private String label_;
    private float hours_ = -1;
    private int classesTotal_ = -1;
    private int classesGiven_ = -1;
    private int classesLeft_ = -1;
    private int absences_ = -1;
    private float mean_ = -1;
    private int year_;
    private int period_;
    public ToMany<Period> periods;
    public ToMany<Schedule> schedules;
    public ToMany<Material> materials;

    public Matter(String description, int color, float hours, int classesTotal, int year, int period) {
        this.description_ = description;
        this.color_ = color;
        this.hours_ = hours;
        this.classesTotal_ = classesTotal;
        this.year_ = year;
        this.period_ = period;
    }

    public String getAbsencesString() {
        return absences_ == -1 ? "-" : String.valueOf(absences_);
    }

    public String getPresencesString() {
        return absences_ == -1 || classesGiven_ == -1 ? "-" : String.valueOf(classesGiven_ - absences_);
    }

    public String getMean() {
        return mean_ == -1 ? "-" : String.valueOf(mean_);
    }

    public void setClassesGiven(int classesGiven) {
        this.classesGiven_ = classesGiven;
    }

    public void setClassesTotal(int classesTotal) {
        this.classesTotal_ = classesTotal;
    }

    public void setClassesLeft(int classesLeft) {
        this.classesLeft_ = classesLeft;
    }

    public void setAbsences(int absences) {
        this.absences_ = absences;
    }

    public void setMean(float mean) {
        this.mean_ = mean;
    }

    @ColorInt
    public int getColor() {
        return color_;
    }

    public void setColor(int color) {
        this.color_ = color;
    }

    public String getTitle() {
        return title_ == null ? description_ : title_;
    }

    public String getPeriod() {
        return year_ + "/" + period_;
    }

    public String getSituation() {
        return situation_ == null ? "" : situation_;
    }

    public float getGivenHours() {
        return classesGiven_ * (hours_ / classesTotal_);
    }

    public void setTeacher(String teacher) {
        this.teacher_ = teacher;
    }

    public void setTitle(String title) {
        this.title_ = title;
    }

    public void setLabel(String label) {
        this.label_ = label;
    }

    public void setSituation(String situation) {
        this.situation_ = situation;
    }

    public Period getLastPeriod() {
        int k = 0;

        for (int j = 0; j < periods.size(); j++) {
            if (!periods.get(j).journals.isEmpty() && !periods.get(j).isSub_()) {
                k = j;
            }
        }

        if (periods.isEmpty())
            return null;

        return periods.get(k);
    }

    public float getAllMaxGradesSum() {
        float sum = 0;
        for (Period period : periods) {
            if (period != null) {
                for (Journal journal : period.journals) {
                    if (journal.getMax_() > -1) {
                        sum += journal.getMax_();
                    }
                }
            }
        }

        return sum;
    }

    public float getAllGradesSum() {
        float sum = 0;
        for (Period period : periods) {
            if (period != null) {
                for (Journal journal : period.journals) {
                    if (journal.getGrade_() > -1) {
                        sum += journal.getGrade_();
                    }
                }
            }
        }

        return sum;
    }

    public List<Journal> getLastJournals() {
        List<Journal> journals = new ArrayList<>();

        Period period = getLastPeriod();

        if (period != null)
            journals.addAll(period.journals);

        //Collections.reverse(journals);
        return journals;
    }

    public boolean hasJournals() {
        Period period = getLastPeriod();

        if (period == null)
            return false;

        return !period.journals.isEmpty();
    }

    public int getJournalNotSeenCount() {
        Period period = getLastPeriod();

        if (period == null)
            return 0;

        int sum = 0;

        for (Journal j : period.journals)
            if (!j.isSeen_())
                sum++;

        return sum;
    }

    public int getMaterialNotSeenCount() {
        int sum = 0;

        for (Material m : materials)
            if (!m.isSeen_())
                sum++;

        return sum;
    }

    public String getLastGradeSumString() {
        Period period = getLastPeriod();

        if (period == null)
            return "-";

        return period.getGradeSumString();
    }

    public float getLastGradeSum() {
        Period period = getLastPeriod();

        if (period == null)
            return -1;

        return period.getGradeSum();
    }

    public int getQID() {
        return Integer.parseInt(description_.substring(0, description_.indexOf('-') - 1));
    }

    public String getLabel() {
        return getLabel_();
    }

//    public String getLabel() {
//        String label = getTitle();
//        label = label.replace(" - ", " ").trim();
//        label = label.replace("-", " ").trim();
//        label = label.replace(" e ", " ").trim();
//        label = label.replace(" de ", " ").trim();
//        label = label.replace(" da ", " ").trim();
//        label = label.replace(" do ", " ").trim();
//        label = label.replace(" das ", " ").trim();
//        label = label.replace(" dos ", " ").trim();
//        label = label.replace(" em ", " ").trim();
//        label = label.replace(" para ", " ").trim();
//        label = label.replace(" V", " ").trim();
//        label = label.replace(" III", " ").trim();
//        label = label.replace(" II", " ").trim();
//        label = label.replace(" I", " ").trim();
//        String[] tokens = label.split(" ");
//        StringBuilder ret = new StringBuilder();
//        int length = Math.min(3, tokens.length);
//
//        if (tokens.length < 2)
//            ret = new StringBuilder(label.substring(0, Math.min(label.length() - 1, 3)));
//        else
//            for (int i = 0; i < length; i++) {
//                String token = tokens[i];
//
//                if (token.isEmpty())
//                    continue;
//
//                //if (!token.startsWith("I") && !token.endsWith("I"))
//                ret.append(token.charAt(0));
//            }
//
//        return ret.toString().toUpperCase();
//    }

    public String getChartValue() {
        float sg = getLastPeriod().getGradeSum();
        float fg = getLastPeriod().getGradeFinal_();

        return String.valueOf(Math.max(sg, fg));
    }

    public String getClazz() {
        if (title_ == null)
            return "-";

        String clazz = description_.substring(0, description_.indexOf(StringUtils.stripAccents(title_)) - 2).trim();

        if (!clazz.contains("-"))
            return "-";

        return clazz.substring(clazz.indexOf('-') + 1).trim();
    }
    
    public String getTeacher() {
        return teacher_ == null ? "" : teacher_;
    }

    public int getClassesGiven() {
        return classesGiven_ == -1 ? 0 : classesGiven_;
    }

    public int getClassesTotal() {
        return classesTotal_ == -1 ? getClassesGiven() : classesTotal_;
    }

    public float getHours() {
        return hours_ == -1 ? 0 : hours_;
    }

    public int getAbsences() {
        return absences_ == -1 ? 0 : absences_;
    }

    public String getHoursString() {
        return hours_ <= 0 ? "-" : String.valueOf(hours_);
    }

    public String getClassesTotalString() {
        return classesTotal_ <= 0 ? "-" : String.valueOf(getClassesTotal());
    }

    public String getClassesGivenString() {
        return classesGiven_ == -1 ? "-" : String.valueOf(classesGiven_);
    }

    /*
     * Auto-generated methods
     */

    public Matter() {}

    public String getTitle_() {
        return title_;
    }

    public String getDescription_() {
        return description_;
    }

    public String getSituation_() {
        return situation_;
    }

    public String getTeacher_() {
        return teacher_;
    }

    public String getLabel_() {
        return label_;
    }

    public float getMean_() {
        return mean_;
    }

    public int getColor_() {
        return color_;
    }

    public int getAbsences_() {
        return absences_;
    }

    public int getYear_() {
        return year_;
    }

    public int getPeriod_() {
        return period_;
    }

    public float getHours_() {
        return hours_;
    }

    public int getClassesTotal_() {
        return classesTotal_;
    }

    public int getClassesGiven_() {
        return classesGiven_;
    }

    public int getClassesLeft_() {
        return classesLeft_;
    }

    @Override
    public int getItemType() {
        return HEADER;
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
        if (!(o instanceof Matter)) return false;
        Matter matter = (Matter) o;
        return id == matter.id &&
                getColor_() == matter.getColor_() &&
                Float.compare(matter.getHours_(), getHours_()) == 0 &&
                getClassesTotal_() == matter.getClassesTotal_() &&
                getAbsences_() == matter.getAbsences_() &&
                Float.compare(matter.getMean_(), getMean_()) == 0 &&
                getYear_() == matter.getYear_() &&
                getPeriod_() == matter.getPeriod_() &&
                Objects.equals(getTitle_(), matter.getTitle_()) &&
                Objects.equals(getDescription_(), matter.getDescription_()) &&
                Objects.equals(getSituation_(), matter.getSituation_()) &&
                Objects.equals(getTeacher_(), matter.getTeacher_());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, getColor_(), getTitle_(), getDescription_(), getSituation_(),
                getTeacher_(), getHours_(), getClassesTotal_(), getAbsences_(), getMean_(), getYear_(),
                getPeriod_());
    }

    @NonNull
    @Override
    public String toString() {
        return "Matter{" +
                "id=" + id +
                ", title_='" + title_ + '\'' +
                ", description_='" + description_ + '\'' +
                ", year_=" + year_ +
                ", period_=" + period_ +
                '}';
    }
}
