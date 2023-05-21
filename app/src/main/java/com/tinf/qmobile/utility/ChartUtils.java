package com.tinf.qmobile.utility;

import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Period;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;

public class ChartUtils {

  public static LineChartData getChartData(Matter matter) {
    int x = 0;

    List<PointValue> points = new ArrayList<>();

    for (int i = 0; i < matter.periods.size(); i++) {
      Period period = matter.periods.get(i);

      for (int j = 0; j < period.journals.size(); j++) {
        Journal journal = period.journals.get(j);

        if (journal.getGrade_() >= 0) {
          points.add(new PointValue(x++, journal.getGrade_() / journal.getMax_() * 10)
                         .setLabel(journal.getGrade()));
        }
      }
    }

    Line line = new Line(points);
    line.setColor(ColorUtils.INSTANCE.contrast(matter.getColor(), 0.25f));
    line.setPointColor(matter.getColor());
    line.setShape(ValueShape.CIRCLE);
    line.setCubic(true);
    line.setFilled(false);
    line.setHasLabels(true);

    return new LineChartData(Collections.singletonList(line));
  }
}
