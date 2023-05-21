package com.tinf.qmobile.parser;

import static io.objectbox.query.QueryBuilder.StringOrder.CASE_INSENSITIVE;

import android.util.Log;

import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.model.matter.Period;
import com.tinf.qmobile.utility.RandomColor;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

public class ReportParser extends BaseParser {
  private final static String TAG = "ReportParser";

  public ReportParser(int page, int year, int period, boolean notify, BaseParser.OnFinish onFinish,
                      OnError onError) {
    super(page, year, period, notify, onFinish, onError);
  }

  @Override
  public void parse(Document document) {
    RandomColor colors = new RandomColor();

    Elements tables = document.getElementsByTag("tbody");

    for (int k = 0; k < tables.size(); k++) {
      if (!tables.get(k).getElementsByTag("tr").get(0).text().contains("Estrutura"))
        continue;

      if (tables.get(0).childNodeSize() <= 1)
        continue;

      Elements rows = tables.get(k).getElementsByTag("tr");

      for (int i = 2; i < tables.get(k).childNodeSize() - 1; i++) {
        String matterTitle = formatTitle(rows.get(i).child(0).text()).trim();
        String clazz = formatClass(rows.get(i).child(2).text()).trim();
        //String absencesTotal = formatNumber(rows.get(i).child(3).text());
        String finalMean = formatNumber(rows.get(i).child(4).text()).trim();
        String situation = rows.get(i).child(rows.get(1).children().size() - 1).text().trim();
        String qid =
            formatQID(rows.get(i).child(0).getElementsByTag("q_latente").attr("value")).trim();
        String label = matterTitle;

        label = label.replace(" - ", " ").trim();
        label = label.replace("-", " ").trim();
        label = label.replace(" e ", " ").trim();
        label = label.replace(" de ", " ").trim();
        label = label.replace(" da ", " ").trim();
        label = label.replace(" do ", " ").trim();
        label = label.replace(" das ", " ").trim();
        label = label.replace(" dos ", " ").trim();
        label = label.replace(" em ", " ").trim();
        label = label.replace(" para ", " ").trim();
        label = label.replace(" E ", " ").trim();
        label = label.replace(" DE ", " ").trim();
        label = label.replace(" DA ", " ").trim();
        label = label.replace(" DO ", " ").trim();
        label = label.replace(" DAS ", " ").trim();
        label = label.replace(" DOS ", " ").trim();
        label = label.replace(" EM ", " ").trim();
        label = label.replace(" PARA ", " ").trim();
        label = label.replace(" X", " ").trim();
        label = label.replace(" V", " ").trim();
        label = label.replace(" III", " ").trim();
        label = label.replace(" II", " ").trim();
        label = label.replace(" I", " ").trim();

        String[] tokens = label.split(" ");
        StringBuilder ret = new StringBuilder();

        if (tokens.length < 2) {
          ret = new StringBuilder(label.substring(0, Math.min(label.length() - 1, 3)));
        } else {
          for (String token : tokens) {
            if (token.isEmpty())
              continue;

            ret.append(token.charAt(0));
          }
        }

        label = ret.toString().toUpperCase();

        Matter matter = null;

        Log.d("Matter", StringUtils.stripAccents(matterTitle));
        Log.d("CLAZZ", clazz);
        Log.d("QID", qid);
        Log.d("Year", String.valueOf(year));
        Log.d("Period", String.valueOf(period));

        try {
          crashlytics.log(matterTitle);

          matter = matterBox.query()
                            .contains(Matter_.description_, StringUtils.stripAccents(matterTitle),
                                      CASE_INSENSITIVE).and()
                            .equal(Matter_.year_, year).and()
                            .equal(Matter_.period_, period).and()
                            .contains(Matter_.description_, StringUtils.stripAccents(clazz),
                                      CASE_INSENSITIVE).and()
                            .contains(Matter_.description_, StringUtils.stripAccents(qid),
                                      CASE_INSENSITIVE)
                            .build().findUnique();
        } catch (Exception e) {
          Log.e(TAG, matterTitle);
          e.printStackTrace();
        }

        if (matter == null) {
          try {
            crashlytics.log(matterTitle);

            matter = matterBox.query()
                              .contains(Matter_.description_, StringUtils.stripAccents(matterTitle),
                                        CASE_INSENSITIVE).and()
                              .equal(Matter_.year_, year).and()
                              .equal(Matter_.period_, period).and()
                              .contains(Matter_.description_, StringUtils.stripAccents(qid),
                                        CASE_INSENSITIVE)
                              .build().findUnique();
          } catch (Exception e) {
            Log.e(TAG, matterTitle);
            e.printStackTrace();
          }
        }

        if (matter == null) {
          try {
            crashlytics.log(matterTitle);

            matter = matterBox.query()
                              .contains(Matter_.description_, StringUtils.stripAccents(qid),
                                        CASE_INSENSITIVE).and()
                              .equal(Matter_.year_, year).and()
                              .equal(Matter_.period_, period)
                              .build().findUnique();
          } catch (Exception e) {
            Log.e(TAG, matterTitle);
            e.printStackTrace();
          }
        }

        if (matter == null) {
          try {
            crashlytics.log(matterTitle);

            matter = matterBox.query()
                              .contains(Matter_.description_, StringUtils.stripAccents(matterTitle),
                                        CASE_INSENSITIVE).and()
                              .equal(Matter_.year_, year).and()
                              .equal(Matter_.period_, period)
                              .build().findUnique();
          } catch (Exception e) {
            Log.e(TAG, matterTitle);
            e.printStackTrace();
          }
        }

        if (matter == null) {
          try {
            crashlytics.log(matterTitle);

            matter = matterBox.query()
                              .contains(Matter_.description_, StringUtils.stripAccents(qid),
                                        CASE_INSENSITIVE)
                              .build().findUnique();
          } catch (Exception e) {
            Log.e(TAG, matterTitle);
            e.printStackTrace();
          }
        }

        if (matter == null) {
          boolean found = false;
          boolean moreThanOne = false;

          List<Matter> matters = matterBox.query()
                                          .equal(Matter_.year_, year).and()
                                          .equal(Matter_.period_, period)
                                          .build().find();

          Log.d(matterTitle, "Query failed");

          for (Matter m : matters) {
            if (StringUtils.containsIgnoreCase(m.getDescription_(),
                                               StringUtils.stripAccents(matterTitle))) {
              if (found) {
                moreThanOne = true;
                break;
              }

              found = true;
              matter = m;
            }
          }

          if (moreThanOne) {
            for (Matter m : matters) {
              if (StringUtils.containsIgnoreCase(m.getDescription_(),
                                                 StringUtils.stripAccents(matterTitle))
                  && StringUtils.containsIgnoreCase(m.getDescription_(),
                                                    StringUtils.stripAccents(clazz))
                  && StringUtils.containsIgnoreCase(m.getDescription_(),
                                                    StringUtils.stripAccents(qid))) {
                matter = m;
              }
            }
          }
        }

        if (matter == null) {
          if (situation.contains("Aproveit") || situation.contains("Trancado") ||
              situation.contains("Cancelado")) {
            matter =
                new Matter(StringUtils.stripAccents(matterTitle), colors.getColor(), -1, -1, year,
                           period);
          }
        }

        if (situation.contains("Aproveit")) {
          situation = "Aproveitamento";
        }

        if (matter == null) {
          crashlytics.recordException(new Exception(matterTitle + " not found in DB"));
          Log.e(matterTitle, "Not found in DB");
          continue;
        }

        //matter.setAbsences(absencesTotal.isEmpty() ? -1 : Integer.parseInt(absencesTotal));
        matter.setMean(finalMean.isEmpty() ? -1 : Float.parseFloat(finalMean));
        matter.setTitle(matterTitle.isEmpty() ? null : matterTitle);
        matter.setSituation(situation.isEmpty() ? null : situation);
        matter.setLabel(label.isEmpty() ? null : label);
        matter.setClazz(clazz.isEmpty() ? null : clazz);
        matter.setQid(Integer.parseInt(qid));

        for (int j = 0; j < matter.periods.size(); j++) {
          Period period = matter.periods.get(j);

          for (int l = 5; l < rows.get(1).children().size(); l++) {
            Element h = rows.get(1).child(l);

            if (h.children().size() <= 0)
              continue;

            if (!h.child(0).attr("title").equals(period.getTitle_()))
              continue;

            String grade_ = formatNumber(rows.get(i).child(l).text());
            String absences_ = formatNumber(rows.get(i).child(++l).text());

            period.setGrade(grade_.isEmpty() ? -1 : Float.parseFloat(grade_));
            period.setAbsences(absences_.isEmpty() ? -1 : Integer.parseInt(absences_));

            periodBox.put(period);

            if (!period.isSub_() || ((j + 1) % 2 != 0))
              continue;

            period = matter.periods.get(j - 1);

            String gradeFinal_ = formatNumber(rows.get(i).child(++l).text());
            float gradeFinal = gradeFinal_.isEmpty() ? -1 : Float.parseFloat(gradeFinal_);

            period.setGradeFinal(gradeFinal);

            periodBox.put(period);
          }
        }
        matterBox.put(matter);
      }
    }
  }

  private String formatTitle(String s) {
    if (s.contains("- G"))
      s = s.substring(0, s.indexOf("- G"));

    if (s.contains("("))
      s = s.substring(0, s.indexOf("("));

    return s.trim();
  }

  private String formatClass(String s) {
    s = s.substring(s.indexOf("-") + 1).trim();

    if (s.contains(" "))
      s = s.substring(0, s.indexOf(" "));

    return s.trim();
  }

  private String formatQID(String s) {
    return s.substring(s.indexOf("=") + 1).trim();
  }

  protected String formatNumber(String s) {
    return s.startsWith(",") ? "" : s.replaceAll(",", ".").trim();
  }

}
