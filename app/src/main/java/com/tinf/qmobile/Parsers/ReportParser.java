package com.tinf.qmobile.Parsers;

import android.os.AsyncTask;
import android.util.Log;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Class.Materias.Matter;
import com.tinf.qmobile.Class.Materias.Period;
import com.tinf.qmobile.Class.Materias.Matter_;
import com.tinf.qmobile.Utilities.User;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import io.objectbox.Box;

import static com.tinf.qmobile.Network.OnResponse.PG_BOLETIM;

public class ReportParser extends AsyncTask<String, Void, Void> {
    private final static String TAG = "ReportParser";
    private OnFinish onFinish;
    private int pos;
    private boolean notify;

    public ReportParser(int pos, boolean notify, OnFinish onFinish) {
        this.pos = pos;
        this.notify = notify;
        this.onFinish = onFinish;

        Log.i(TAG, "New instance");
    }

    @Override
    protected Void doInBackground(String... page) {
        App.getBox().runInTx(() -> {

            Log.i(TAG, "Parsing " + User.getYear(pos));

            Box<Matter> matterBox = App.getBox().boxFor(Matter.class);
            Box<Period> periodBox = App.getBox().boxFor(Period.class);

            Document document = Jsoup.parse(page[0]);

            Element table = document.getElementsByTag("tbody").get(13);

            if (table != null) {
                Elements rows = table.getElementsByTag("tr");

                for (int i = 2; i < table.childNodeSize() - 1; i++) {
                    String matterTitle = formatName(rows.get(i).child(0).text()).trim();
                    String absencesTotal = formatTd(rows.get(i).child(3).text()).trim();
                    String gradeFirst = formatTd(rows.get(i).child(5).text()).trim();
                    String absencesFirst = formatTd(rows.get(i).child(6).text()).trim();
                    String gradeRPFirst = formatTd(rows.get(i).child(7).text()).trim();
                    String gradeFinalFirst = formatTd(rows.get(i).child(9).text()).trim();
                    String gradeSecond = formatTd(rows.get(i).child(10).text()).trim();
                    String absencesSecond = formatTd(rows.get(i).child(11).text()).trim();
                    String gradeRPSecond = formatTd(rows.get(i).child(12).text()).trim();
                    String gradeFinalSecond = formatTd(rows.get(i).child(14).text()).trim();

                    Matter matter = matterBox.query()
                            .equal(Matter_.title, matterTitle).and()
                            .equal(Matter_.year, User.getYear(pos)).and()
                            .equal(Matter_.period, User.getPeriod(pos))
                            .build().findFirst();

                    if (matter != null) {
                        matter.setAbsences(absencesTotal.isEmpty() ? -1 : Integer.parseInt(absencesTotal));

                        for (int j = 0; j < matter.periods.size(); j++) {
                            Period period = matter.periods.get(j);

                            float grade = -1, gradeFinal = -1, gradeRP = -1;
                            int absences = -1;
                            boolean hasGrade = false, hasAbsences = false, hasGradeFinal = false, hasGradeRP = false;
                            boolean isPeriodValid = false;

                            if (period.getTitle() == Period.Type.PRIMEIRA.get()) {
                                grade = gradeFirst.isEmpty() ? - 1 : Float.parseFloat(gradeFirst);
                                absences = absencesFirst.isEmpty() ? - 1 : Integer.parseInt(absencesFirst);
                                gradeFinal = gradeFinalFirst.isEmpty() ? - 1 : Float.parseFloat(gradeFinalFirst);
                                gradeRP = gradeRPFirst.isEmpty() ? - 1 : Float.parseFloat(gradeRPFirst);
                                isPeriodValid = true;

                            } else if (period.getTitle() == Period.Type.SEGUNDA.get()) {
                                grade = gradeSecond.isEmpty() ? - 1 : Float.parseFloat(gradeSecond);
                                absences = absencesSecond.isEmpty() ? - 1 : Integer.parseInt(absencesSecond);
                                gradeFinal = gradeFinalSecond.isEmpty() ? - 1 : Float.parseFloat(gradeFinalSecond);
                                gradeRP = gradeRPSecond.isEmpty() ? - 1 : Float.parseFloat(gradeRPSecond);
                                isPeriodValid = true;
                            }

                            if (isPeriodValid) {

                                if (period.getGrade() != grade) {
                                    period.setGrade(grade);
                                    hasGrade = true;
                                }

                                if (period.getAbsences() != absences) {
                                    period.setAbsences(absences);
                                    hasAbsences = true;
                                }

                                if (period.getGradeFinal() != gradeFinal) {
                                    period.setGradeFinal(gradeFinal);
                                    hasGradeFinal = true;
                                }

                                if (period.getGradeRP() != gradeRP) {
                                    period.setGradeRP(gradeRP);
                                    hasGradeRP = true;
                                }

                                period.matter.setTarget(matter);
                                periodBox.put(period);

                                if (notify) {
                                    String msg = "";

                                    if (hasGrade) {
                                        msg = msg.concat("Nota");
                                    }

                                    if (hasAbsences) {
                                        msg = msg.concat("Faltas");
                                    }

                                    if (hasGradeFinal) {
                                        msg = msg.concat("Nota Final");
                                    }
                                    if (hasGradeRP) {
                                        msg = msg.concat("Nota RP");
                                    }

                                    //TODO notificação
                                }
                            }
                        }
                        matterBox.put(matter);
                    }
                }
            }
        });
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

    private String formatTd(String text){
        return text.startsWith(",") ? "" : text.replaceAll(",", ".");
    }

    private String formatName(String name) {
        return name.contains("-") ? name.substring(0, name.indexOf("-")) : name;
    }
}
