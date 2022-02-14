package com.tinf.qmobile.model.journal;

import com.tinf.qmobile.App;
import com.tinf.qmobile.R;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.calendar.EventBase;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Period;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToOne;

import static com.tinf.qmobile.model.ViewType.JOURNAL;
import static com.tinf.qmobile.model.journal.Journal.Type.AVALIACAO;

@Entity
public class Journal extends EventBase implements Queryable {

    public enum Type {
        AVALIACAO(0), PROVA(1), TRABALHO(2), EXERCICIO(3), QUALITATIVA(4);

        private final int i;

        Type(final int i) {
            this.i = i;
        }

        public int get() {
            return i;
        }
    }

    //@Transient public boolean highlight;
    private float weight_;
    private float max_;
    private float grade_;
    private int type_;
    public ToOne<Period> period;
    public ToOne<Matter> matter;
    private boolean seen_;

    public Journal(String title, float grade, float weight, float max, long date, int type, Period period, Matter matter, boolean seen) {
        super(title, date);
        this.grade_ = grade;
        this.weight_ = weight;
        this.max_ = max;
        this.type_ = type;
        this.seen_ = seen;
        this.period.setTarget(period);
        this.matter.setTarget(matter);
    }

    public String getMatter() {
        return period.getTarget().matter.getTarget().getTitle();
    }

    public String getGrade() {
        return grade_ == -1 ? "-" : String.valueOf(grade_);
    }

    public String getWeight() {
        return weight_ == -1 ? "-" : String.valueOf(weight_);
    }

    public String getMax() {
        return max_ == -1 ? "-" : String.valueOf(max_);
    }

    public String getType() {
        if (type_ == AVALIACAO.get()) {
            return App.getContext().getResources().getString(R.string.journal_Avaliacao);

        } else if (type_ == Type.PROVA.get()) {
            return App.getContext().getResources().getString(R.string.journal_Prova);

        } else if (type_ == Type.EXERCICIO.get()) {
            return App.getContext().getResources().getString(R.string.journal_Exercicio);

        } else if (type_ == Type.TRABALHO.get()) {
            return App.getContext().getResources().getString(R.string.journal_Trabalho);

        } else if (type_ == Type.QUALITATIVA.get()) {
            return App.getContext().getResources().getString(R.string.journal_Qualitativa);

        } else return App.getContext().getResources().getString(R.string.journal_Avaliacao);
    }

    public String getShort() {
        if (type_ == AVALIACAO.get()) {
            return App.getContext().getResources().getString(R.string.sigla_Avaliacao);

        } else if (type_ == Type.PROVA.get()) {
            return App.getContext().getResources().getString(R.string.sigla_Prova);

        } else if (type_ == Type.EXERCICIO.get()) {
            return App.getContext().getResources().getString(R.string.sigla_Exercicio);

        } else if (type_ == Type.TRABALHO.get()) {
            return App.getContext().getResources().getString(R.string.sigla_Trabalho);

        } else if (type_ == Type.QUALITATIVA.get()) {
            return App.getContext().getResources().getString(R.string.sigla_Qualitativa);

        } else return App.getContext().getResources().getString(R.string.sigla_Avaliacao);
    }

    @Override
    public int getItemType() {
        return JOURNAL;
    }

    public String formatDate() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return format.format(getDate());
    }

    @Override
    public int getColor() {
        return matter.getTarget().getColor();
    }

    public String getPeriod() {
        return period.getTarget().getTitle();
    }

    public void setGrade(float grade) {
        this.grade_ = grade;
    }

    public void see() {
        seen_ = true;
    }

    public float getPlotGrade() {
        return (grade_ / max_) * 10;
    }

    /*
     * Required methods
     */

    public Journal() {}

    public boolean isSeen_() {
        return seen_;
    }

    public float getWeight_() {
        return weight_;
    }

    public float getMax_() {
        return max_;
    }

    public float getGrade_() {
        return grade_;
    }

    public int getType_() {
        return type_;
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
        if (!(o instanceof Journal)) return false;
        if (!super.equals(o)) return false;
        Journal journal = (Journal) o;
        return  Float.compare(journal.getWeight_(), getWeight_()) == 0 &&
                Float.compare(journal.getMax_(), getMax_()) == 0 &&
                Float.compare(journal.getGrade_(), getGrade_()) == 0 &&
                getType_() == journal.getType_();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), weight_, max_, grade_, type_);
    }

}
