package com.tinf.qmobile.holder.performance;

import static com.tinf.qmobile.model.ViewType.JOURNAL;
import static com.tinf.qmobile.utility.ChartUtils.getChartData;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

        binding.chart.setLineChartData(getChartData(matter));
        binding.chart.setZoomEnabled(false);
        binding.chart.setScrollEnabled(false);
        binding.chart.setViewportCalculationEnabled(false);
        final Viewport v = new Viewport(binding.chart.getMaximumViewport());
        v.top = 20;
        v.bottom = -10;
        binding.chart.setMaximumViewport(v);
        binding.chart.setCurrentViewport(v);

        binding.title.setOnClickListener(view -> {
            Intent intent = new Intent(context, MatterActivity.class);
            intent.putExtra("ID", matter.id);
            intent.putExtra("PAGE", JOURNAL);
            context.startActivity(intent);
        });
    }

}
