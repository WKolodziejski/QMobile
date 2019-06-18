package com.tinf.qmobile.parser.novo;

import android.os.AsyncTask;
import android.util.Log;
import com.tinf.qmobile.App;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Period;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.utility.User;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import io.objectbox.Box;

import static com.tinf.qmobile.network.OnResponse.PG_BOLETIM;

public class ReportParser2 extends AsyncTask<String, Void, Void> {
    private final static String TAG = "ReportParser";
    private OnFinish onFinish;
    private int pos;

    public ReportParser2(int pos, OnFinish onFinish) {
        this.pos = pos;
        this.onFinish = onFinish;

        Log.i(TAG, "New instance");
    }

    @Override
    protected Void doInBackground(String... page) {
        //App.getBox().runInTx(() -> {

            Log.i(TAG, "Parsing " + User.getYear(pos));

            Box<Matter> matterBox = App.getBox().boxFor(Matter.class);
            Box<Period> periodBox = App.getBox().boxFor(Period.class);

            Document document = Jsoup.parse(page[0]);

            Elements tables = document.getElementsByTag("tbody");
            for (Element e : tables)
                Log.v(TAG, e.getElementsByTag("tr").get(0).text());

            for (int k = 0; k < tables.size(); k++) {
                if (tables.get(k).getElementsByTag("tr").get(0).text().contains("Estrutura")) {

                    if (tables.get(0).childNodeSize() > 1) {
                        Elements rows = tables.get(k).getElementsByTag("tr");

                        for (int i = 2; i < tables.get(k).childNodeSize() - 1; i++) {

                            String matterTitle = formatTitle(rows.get(i).child(0).text());
                            String absencesTotal = formatNumber(rows.get(i).child(3).text());

                            Matter matter = matterBox.query()
                                    .equal(Matter_.title_, matterTitle).and()
                                    .equal(Matter_.year_, User.getYear(pos)).and()
                                    .equal(Matter_.period_, User.getPeriod(pos)).and()
                                    .equal(Matter_.clazz_, formatClazz(rows.get(i).child(2).text()))
                                    .build().findUnique();

                            if (matter != null) {
                                matter.setAbsences(absencesTotal.isEmpty() ? -1 : Integer.parseInt(absencesTotal));

                                if (rows.get(1).children().size() == 11) {
                                    if (matter.periods.size() < 1) {
                                        matter.periods.add(new Period("1"));
                                        User.setType(User.Type.UNICO);
                                    }
                                } else {
                                    switch (rows.get(1).child(5).text()) {
                                        case "1E":
                                            for (int j = matter.periods.size() + 1; j <= 2; j++) {
                                                matter.periods.add(new Period(Integer.toString(j)));
                                            }
                                            User.setType(User.Type.SEMESTRE1);
                                            break;
                                        case "NB1":
                                            for (int j = matter.periods.size() + 1; j <= 4; j++) {
                                                matter.periods.add(new Period(Integer.toString(j)));
                                            }
                                            User.setType(User.Type.BIMESTRE);
                                            break;
                                        case "NS1":
                                            for (int j = matter.periods.size() + 1; j <= 2; j++) {
                                                matter.periods.add(new Period(Integer.toString(j)));
                                            }
                                            User.setType(User.Type.SEMESTRE2);
                                            break;
                                    }
                                }

                                for (int j = 0; j < matter.periods.size(); j++) {
                                    Period period = matter.periods.get(j);

                                    float grade, gradeFinal, gradeRP;
                                    int absences;
                                    boolean isPeriodValid = true;
                                    String grade_ = "", absences_ = "", gradeRP_ = "", gradeFinal_ = "";

                                    if (User.getType() == User.Type.UNICO.get()) { //Etapa única
                                        grade_ = formatNumber(rows.get(i).child(5).text());
                                        absences_ = formatNumber(rows.get(i).child(6).text());
                                        gradeRP_ = formatNumber(rows.get(i).child(7).text());
                                        gradeFinal_ = formatNumber(rows.get(i).child(9).text());

                                    } else { //Semestre ou bimestre
                                        if (User.getType() == User.Type.SEMESTRE1.get()) { //Semestre tabela completa
                                            switch (j) {
                                                case 0:
                                                    grade_ = formatNumber(rows.get(i).child(5).text());
                                                    absences_ = formatNumber(rows.get(i).child(6).text());
                                                    gradeRP_ = formatNumber(rows.get(i).child(7).text());
                                                    gradeFinal_ = formatNumber(rows.get(i).child(9).text());
                                                    break;
                                                case 1:
                                                    grade_ = formatNumber(rows.get(i).child(10).text());
                                                    absences_ = formatNumber(rows.get(i).child(11).text());
                                                    gradeRP_ = formatNumber(rows.get(i).child(12).text());
                                                    gradeFinal_ = formatNumber(rows.get(i).child(14).text());
                                                    break;
                                                default: isPeriodValid = false;
                                            }
                                        } else if (User.getType() == User.Type.SEMESTRE2.get()) { //Semestre tabela simples
                                            switch (j) {
                                                case 0:
                                                    grade_ = formatNumber(rows.get(i).child(5).text());
                                                    absences_ = formatNumber(rows.get(i).child(6).text());
                                                    break;
                                                case 1:
                                                    grade_ = formatNumber(rows.get(i).child(7).text());
                                                    absences_ = formatNumber(rows.get(i).child(8).text());
                                                    break;
                                                default: isPeriodValid = false;
                                            }
                                        } else if (User.getType() == User.Type.BIMESTRE.get()) { //Bimestre
                                            switch (j) {
                                                case 0:
                                                    grade_ = formatNumber(rows.get(i).child(5).text());
                                                    absences_ = formatNumber(rows.get(i).child(6).text());
                                                    break;
                                                case 1:
                                                    grade_ = formatNumber(rows.get(i).child(7).text());
                                                    absences_ = formatNumber(rows.get(i).child(8).text());
                                                    break;
                                                case 2:
                                                    grade_ = formatNumber(rows.get(i).child(9).text());
                                                    absences_ = formatNumber(rows.get(i).child(10).text());
                                                    break;
                                                case 3:
                                                    grade_ = formatNumber(rows.get(i).child(11).text());
                                                    absences_ = formatNumber(rows.get(i).child(12).text());
                                                    break;
                                                default: isPeriodValid = false;
                                            }
                                        }
                                    }

                                    grade = grade_.isEmpty() ? -1 : Float.parseFloat(grade_);
                                    absences = absences_.isEmpty() ? -1 : Integer.parseInt(absences_);
                                    gradeFinal = gradeFinal_.isEmpty() ? -1 : Float.parseFloat(gradeFinal_);
                                    gradeRP = gradeRP_.isEmpty() ? -1 : Float.parseFloat(gradeRP_);

                                    Log.d(TAG, period.getTitle());
                                    Log.d(TAG, "Nota " + grade);
                                    Log.d(TAG, "Nota Final " + gradeFinal);
                                    Log.d(TAG, "Nota RP " + gradeRP);
                                    Log.d(TAG, "Faltas " + absences);

                                    if (isPeriodValid) {

                                        period.setGrade(grade);
                                        period.setAbsences(absences);
                                        period.setGradeFinal(gradeFinal);
                                        period.setGradeRP(gradeRP);

                                        period.matter.setTarget(matter);
                                        periodBox.put(period);

                                    }
                                }
                                matterBox.put(matter);
                            }
                        }
                    }
                }
            }
        //});
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        onFinish.onFinish(PG_BOLETIM, pos);
    }

    public interface OnFinish {
        void onFinish(int pg, int year);
    }

    private String formatNumber(String s){
        return s.startsWith(",") ? "" : s.replaceAll(",", ".").trim();
    }

    private String formatTitle(String s) {
        if (s.contains("- G")) {
            s = s.substring(0, s.indexOf("- G"));
        }
        if (s.contains("(")) {
            s = s.substring(0, s.indexOf("("));
        }
        return s.trim();
    }

    private String formatClazz(String s) {
        s = s.substring(s.indexOf("-") + 1).trim();
        return s;
    }

}
