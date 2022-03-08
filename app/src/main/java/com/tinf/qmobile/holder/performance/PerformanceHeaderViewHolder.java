package com.tinf.qmobile.holder.performance;

import static com.tinf.qmobile.model.ViewType.HEADER;
import static com.tinf.qmobile.model.ViewType.JOURNAL;

import android.content.Context;
import android.content.Intent;
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

import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
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
        binding.title.setText(matter.getTitle());
        binding.title.setBackgroundColor(matter.getColor());

//        binding.chart.setLineChartData(getChartData(matter));
//        binding.chart.setZoomEnabled(false);
//        binding.chart.setScrollEnabled(false);
//        binding.chart.setViewportCalculationEnabled(false);
//        final Viewport v = new Viewport(binding.chart.getMaximumViewport());
//        v.top = 20;
//        v.bottom = -10;
//        binding.chart.setMaximumViewport(v);
//        binding.chart.setCurrentViewport(v);

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

        binding.chart.setLineChartData(data);
        binding.chart.setZoomEnabled(false);
        binding.chart.setScrollEnabled(false);
        binding.chart.setViewportCalculationEnabled(false);
        final Viewport v = new Viewport(binding.chart.getMaximumViewport());

        v.top += 1;
        v.bottom = 0;

        binding.chart.setMaximumViewport(v);
        binding.chart.setCurrentViewport(v);

        binding.chart.setOnValueTouchListener(new LineChartOnValueSelectListener() {
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

        binding.title.setOnClickListener(view -> {
            Intent intent = new Intent(context, MatterActivity.class);
            intent.putExtra("ID", matter.id);
            intent.putExtra("PAGE", HEADER);
            intent.putExtra("LOOKUP", false);
            context.startActivity(intent);
        });
    }

}
