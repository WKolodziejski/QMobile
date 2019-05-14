package com.tinf.qmobile.model.matter;

import com.tinf.qmobile.App;
import com.tinf.qmobile.R;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

@Entity
public class Period {

    public enum Type {
        PRIMEIRA(0), PRIMEIRA_RP1(1), PRIMEIRA_RP2(2), SEGUNDA(3), SEGUNDA_RP1(4), SEGUNDA_RP2(5);//, UNICO(6);

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
    
    public String getTitleString() {
        if (title == Type.PRIMEIRA.get()) {
            return App.getContext().getResources().getString(R.string.diarios_PrimeiraEtapa);
        } else if (title == Type.PRIMEIRA_RP1.get()) {
            return App.getContext().getResources().getString(R.string.diarios_RP1_PrimeiraEtapa);
        } else if (title == Type.PRIMEIRA_RP2.get()) {
            return App.getContext().getResources().getString(R.string.diarios_RP2_PrimeiraEtapa);
        } else if (title == Type.SEGUNDA.get()) {
            return App.getContext().getResources().getString(R.string.diarios_SegundaEtapa);
        } else if (title == Type.SEGUNDA_RP1.get()) {
            return App.getContext().getResources().getString(R.string.diarios_RP1_SegundaEtapa);
        } else if (title == Type.SEGUNDA_RP2.get()) {
            return App.getContext().getResources().getString(R.string.diarios_RP2_SegundaEtapa);
        } /*else if (title == Type.UNICO.get()) {
            return App.getContext().getResources().getString(R.string.diarios_EtapaUnica);
        }*/ else return "";
    }

    public String getGradeString() {
        return grade == -1 ? "" : String.valueOf(grade);
    }

    public String getGradeRPString() {
        return gradeRP == -1 ? "" : String.valueOf(gradeRP);
    }

    public String getGradeFinalString() {
        return gradeFinal == -1 ? "" : String.valueOf(gradeFinal);
    }

    public String getAbsencesString() {
        return absences == -1 ? "" : String.valueOf(absences);
    }

    /*
     * Required methods
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
