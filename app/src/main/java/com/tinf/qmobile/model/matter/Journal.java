package com.tinf.qmobile.model.matter;

import com.tinf.qmobile.App;
import com.tinf.qmobile.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

import static com.tinf.qmobile.model.matter.Journal.Type.AVALIACAO;

@Entity
public class Journal {

    public enum Type {
        AVALIACAO(0), PROVA(1), TRABALHO(2), EXERCICIO(3), QUALITATIVA(4);

        private int i;

        Type(final int i) {
            this.i = i;
        }

        public int get() {
            return i;
        }
    }

    @Id public long id;
    private String title_;
    private float weight_;
    private float max_;
    private float grade_;
    private long date_;
    private int type_;
    public ToOne<Period> period;
    public ToOne<Matter> matter;

    public Journal(String title, float grade, float weight, float max, long date, int type, Matter matter) {
        this.title_ = title;
        this.grade_ = grade;
        this.weight_ = weight;
        this.max_ = max;
        this.date_ = date;
        this.type_ = type;
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

    public String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return format.format(new Date(date_));
    }

    public int getColor() {
        return period.getTarget().matter.getTarget().getColor();
    }

    public String getPeriod() {
        return period.getTarget().getTitle();
    }

    public String getTitle() {
        return title_ == null ? "" : title_;
    }

    public void setGrade(float grade) {
        this.grade_ = grade;
    }

    /*
     * Required methods
     */

    public Journal() {}

    public String getTitle_() {
        return title_;
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

    public long getDate_() {
        return date_;
    }

    public int getType_() {
        return type_;
    }

}
