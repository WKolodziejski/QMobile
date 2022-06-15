package com.tinf.qmobile.holder.performance;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.tinf.qmobile.model.ViewType.HEADER;
import static com.tinf.qmobile.model.ViewType.JOURNAL;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.activity.MatterActivity;
import com.tinf.qmobile.databinding.ChartHeaderBinding;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Period;
import com.tinf.qmobile.utility.ColorUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;

public class PerformanceHeaderViewHolder extends PerformanceViewHolder<Matter> {
    private final ChartHeaderBinding binding;

    public PerformanceHeaderViewHolder(@NonNull View view) {
        super(view);
        binding = ChartHeaderBinding.bind(view);
    }

    @Override
    public void bind(Context context, Matter matter) {
        int color1 = matter.getColor();
        int color2 = ColorUtils.INSTANCE.contrast(color1, 0.25f);
        int classesGiven = matter.getClassesGiven();
        int absences = matter.getAbsences();
        int presences = classesGiven - absences;

        Log.d(matter.getAbsencesString(), String.valueOf(presences));

        List<SliceValue> values = new ArrayList<>();

        if (presences > 0) {
            values.add(new SliceValue(presences)
                    .setColor(color1)
                    .setLabel(""));
        } else {
            values.add(new SliceValue(1)
                    .setColor(color2)
                    .setLabel(""));
        }

        if (absences > 0) {
            values.add(new SliceValue(absences)
                    .setColor(color2)
                    .setLabel(""));
        }

        binding.title.setText(matter.getTitle());

        binding.chartPresence.setPieChartData(new PieChartData(values)
                .setHasCenterCircle(true)
                .setCenterCircleScale(0.35f)
                .setHasLabelsOnlyForSelected(true));
        binding.chartPresence.setChartRotation(-90, true);
        binding.chartPresence.setInteractive(false);

        if (absences > 0) {
            binding.chartPresence.selectValue(
                    new SelectedValue(1, 0, SelectedValue.SelectedValueType.LINE));
        }

        binding.presenceIc.setImageTintList(ColorStateList.valueOf(color1));
        binding.absenceIc.setImageTintList(ColorStateList.valueOf(color2));

        binding.presenceTxt.setText(matter.getPresencesString());
        binding.absenceTxt.setText(matter.getAbsencesString());

        int c = 0;

        if (!matter.periods.isEmpty())
            for (Period period : matter.periods)
                if (!period.journals.isEmpty())
                    for (Journal journal : period.journals)
                        if (journal.getGrade_() >= 0)
                            c++;

        if (c > 1) {
            int x = 0;

            List<PointValue> points = new ArrayList<>();
            List<Journal> journals = new ArrayList<>();
            List<AxisValue> axisX = new ArrayList<>();

            for (int i = 0; i < matter.periods.size(); i++) {
                Period period = matter.periods.get(i);

                for (int j = 0; j < period.journals.size(); j++) {
                    Journal journal = period.journals.get(j);

                    if (journal.getGrade_() >= 0) {
                        axisX.add(new AxisValue(x).setLabel(journal.getShort()));
                        points.add(new PointValue(x++, journal.getGrade_() / journal.getMax_() * 10)
                                .setLabel(journal.getGrade()));
                        journals.add(journal);
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

            LineChartData data = new LineChartData(Collections.singletonList(line));
            data.setAxisYLeft(new Axis());
            data.setAxisXBottom(new Axis(axisX));

            binding.chartGrades.setLineChartData(data);
            binding.chartGrades.setZoomEnabled(false);
            binding.chartGrades.setScrollEnabled(false);
            binding.chartGrades.setViewportCalculationEnabled(false);
            final Viewport v = new Viewport(binding.chartGrades.getMaximumViewport());

            v.top += 1;
            v.bottom = 0;

            binding.chartGrades.setMaximumViewport(v);
            binding.chartGrades.setCurrentViewport(v);

            binding.chartGrades.setOnValueTouchListener(new LineChartOnValueSelectListener() {
                @Override
                public void onValueSelected(int l, int p, PointValue value) {
                    Intent intent = new Intent(context, EventViewActivity.class);
                    intent.putExtra("ID", journals.get(p).id);
                    intent.putExtra("TYPE", JOURNAL);
                    intent.putExtra("LOOKUP", true);
                    context.startActivity(intent);
                }

                @Override
                public void onValueDeselected() {

                }
            });

            binding.chartGrades.setVisibility(VISIBLE);
        } else {
            binding.chartGrades.setVisibility(GONE);
        }

        binding.title.setOnClickListener(view -> {
            Intent intent = new Intent(context, MatterActivity.class);
            intent.putExtra("ID", matter.id);
            intent.putExtra("PAGE", HEADER);
            intent.putExtra("LOOKUP", false);
            context.startActivity(intent);
        });

//        List<SliceValue> values = new ArrayList<>();
//
//        values.add(new SliceValue(60)
//                .setColor(Color.RED)
//                .setLabel(""));
//
//        values.add(new SliceValue(15)
//                .setColor(Color.BLUE)
//                .setLabel(""));
//
//        values.add(new SliceValue(25)
//                .setColor(Color.GREEN)
//                .setLabel(""));

//        binding.chartPresence.setPieChartData(new PieChartData(values)
//                .setHasCenterCircle(true)
//                .setCenterCircleScale(0.75f)
//                .setHasLabelsOnlyForSelected(true)
//                .setSlicesSpacing(0));
//        binding.chartPresence.setChartRotation(135, false);
//        binding.chartPresence.setInteractive(false);
//        binding.chartPresence.setHovered(false);
    }

}
