package com.tinf.qmobile.Class.Materias;

import android.content.Context;
import com.tinf.qmobile.R;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

@Entity
public class Period {
    public enum Type {
        PRIMEIRA(0), PRIMEIRA_RP1(1), PRIMEIRA_RP2(2), SEGUNDA(3), SEGUNDA_RP1(4), SEGUNDA_RP2(5);

        private int i;

        Type(final int i) {
            this.i = i;
        }

        public int get() {
            return i;
        }
    }

    @Id public long id;
    private int title;
    private float grade = -1;
    private float gradeRP = -1;
    private float gradeFinal = -1;
    private int absences = -1;
    public ToOne<Matter> matter;
    public ToMany<Journal> journals;
    public ToMany<Aula> aulas;
    
    public Period(int title){
        this.title = title;
    }
    
    public String getTitle(Context context) {
        if (title == Type.PRIMEIRA.get()) {
            return context.getResources().getString(R.string.diarios_PrimeiraEtapa);
        } else if (title == Type.PRIMEIRA_RP1.get()) {
            return context.getResources().getString(R.string.diarios_RP1_PrimeiraEtapa);
        } else if (title == Type.PRIMEIRA_RP2.get()) {
            return context.getResources().getString(R.string.diarios_RP2_PrimeiraEtapa);
        } else if (title == Type.SEGUNDA.get()) {
            return context.getResources().getString(R.string.diarios_SegundaEtapa);
        } else if (title == Type.SEGUNDA_RP1.get()) {
            return context.getResources().getString(R.string.diarios_RP1_SegundaEtapa);
        } else if (title == Type.SEGUNDA_RP2.get()) {
            return context.getResources().getString(R.string.diarios_RP2_SegundaEtapa);
        } else return "?";
    }

    /*
     * Auto-generated methods
     */

    public Period() {}

    public int getTitle() {
        return title;
    }

    public float getGrade() {
        return grade;
    }

    public void setGrade(float grade) {
        this.grade = grade;
    }

    public float getGradeRP() {
        return gradeRP;
    }

    public void setGradeRP(float gradeRP) {
        this.gradeRP = gradeRP;
    }

    public float getGradeFinal() {
        return gradeFinal;
    }

    public void setGradeFinal(float gradeFinal) {
        this.gradeFinal = gradeFinal;
    }

    public int getAbsences() {
        return absences;
    }

    public void setAbsences(int absences) {
        this.absences = absences;
    }

}
