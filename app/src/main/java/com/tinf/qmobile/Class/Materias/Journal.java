package com.tinf.qmobile.Class.Materias;

import android.content.Context;

import com.tinf.qmobile.App;
import com.tinf.qmobile.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

import static com.tinf.qmobile.Class.Materias.Journal.Type.AVALIACAO;

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
    private String title;
    private float weight;
    private float max;
    private float grade;
    private long date;
    private int type;
    public ToOne<Period> period;

    public Journal(String title, float grade, float weight, float max, long date, int type) {
        this.title = title;
        this.grade = grade;
        this.weight = weight;
        this.max = max;
        this.date = date;
        this.type = type;
    }

    public String getMatter() {
        return period.getTarget().matter.getTarget().getTitle();
    }

    public String getGradeString() {
        return grade == -1 ? "-" : String.valueOf(grade);
    }

    public String getWeightString() {
        return weight == -1 ? "-" : String.valueOf(weight);
    }

    public String getMaxString() {
        return max == -1 ? "-" : String.valueOf(max);
    }

    public String getShort() {
        if (type == AVALIACAO.get()) {
            return App.getContext().getResources().getString(R.string.sigla_Avaliacao);

        } else if (type == Type.PROVA.get()) {
            return App.getContext().getResources().getString(R.string.sigla_Prova);

        } else if (type == Type.EXERCICIO.get()) {
            return App.getContext().getResources().getString(R.string.sigla_Exercicio);

        } else if (type == Type.TRABALHO.get()) {
            return App.getContext().getResources().getString(R.string.sigla_Trabalho);

        } else if (type == Type.QUALITATIVA.get()) {
            return App.getContext().getResources().getString(R.string.sigla_Qualitativa);

        } else return App.getContext().getResources().getString(R.string.sigla_Avaliacao);
    }

    public String getTypeString() {
        if (type == AVALIACAO.get()) {
            return App.getContext().getResources().getString(R.string.journal_Avaliacao);

        } else if (type == Type.PROVA.get()) {
            return App.getContext().getResources().getString(R.string.journal_Prova);

        } else if (type == Type.EXERCICIO.get()) {
            return App.getContext().getResources().getString(R.string.journal_Exercicio);

        } else if (type == Type.TRABALHO.get()) {
            return App.getContext().getResources().getString(R.string.journal_Trabalho);

        } else if (type == Type.QUALITATIVA.get()) {
            return App.getContext().getResources().getString(R.string.journal_Trabalho);

        } else return App.getContext().getResources().getString(R.string.journal_Avaliacao);
    }

    public String getDateString() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return format.format(new Date(date));
    }

    public int getColor() {
        return period.getTarget().matter.getTarget().getColor();
    }

    public String getPeriod() {
        return period.getTarget().getTitleString();
    }

    /*
     * Required methods
     */

    public Journal() {}

    public String getTitle() {
        return title;
    }

    public float getWeight() {
        return weight;
    }

    public float getMax() {
        return max;
    }

    public float getGrade() {
        return grade;
    }

    public int getType() {
        return type;
    }

    public long getDate() {
        return date;
    }

}
