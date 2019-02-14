package com.tinf.qmobile.Class.Materias;

import android.content.Context;
import com.tinf.qmobile.R;
import java.io.Serializable;
import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

@Entity
public class Etapa implements Serializable {
    public enum Tipo {
        PRIMEIRA(0), PRIMEIRA_RP1(1), PRIMEIRA_RP2(2), SEGUNDA(3), SEGUNDA_RP1(4), SEGUNDA_RP2(5);

        private int anInt;

        Tipo(final int anInt) {
            this.anInt = anInt;
        }

        public int getInt() {
            return anInt;
        }
    }

    @Id public long id;
    private int etapa;
    private String nota;
    private String notaRP;
    private String notaFinal;
    private String faltas;
    @Backlink(to = "materia") public ToOne<Materia> materia;
    @Backlink public ToMany<Diarios> diarios;
    @Backlink public ToMany<Aula> aulas;

    public Etapa() {}

    public Etapa(int etapa){
        this.etapa = etapa;
    }

    public long getId() {
        return id;
    }

    public int getEtapa() {
        return etapa;
    }

    private int getEtapaString() {
        if (etapa == Tipo.PRIMEIRA.getInt()) {
            return R.string.diarios_PrimeiraEtapa;
        } else if (etapa == Tipo.PRIMEIRA_RP1.getInt()) {
            return R.string.diarios_RP1_PrimeiraEtapa;
        } else if (etapa == Tipo.PRIMEIRA_RP2.getInt()) {
            return R.string.diarios_RP2_PrimeiraEtapa;
        } else if (etapa == Tipo.SEGUNDA.getInt()) {
            return R.string.diarios_SegundaEtapa;
        } else if (etapa == Tipo.SEGUNDA_RP1.getInt()) {
            return R.string.diarios_RP1_SegundaEtapa;
        } else if (etapa == Tipo.SEGUNDA_RP2.getInt()) {
            return R.string.diarios_RP2_SegundaEtapa;
        } else return etapa;
    }

    public String getEtapaName(Context context) {
        return context.getResources().getString(getEtapaString());
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public String getNotaRP() {
        return notaRP;
    }

    public void setNotaRP(String notaRP) {
        this.notaRP = notaRP;
    }

    public String getNotaFinal() {
        return notaFinal;
    }

    public void setNotaFinal(String notaFinal) {
        this.notaFinal = notaFinal;
    }

    public String getFaltas() {
        return faltas;
    }

    public void setFaltas(String faltas) {
        this.faltas = faltas;
    }

    public void setEtapa(int etapa) {
        this.etapa = etapa;
    }

}
