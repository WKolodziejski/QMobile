package com.tinf.qmobile.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.tinf.qmobile.R;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.databinding.FragmentPerformanceBinding;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import java.util.ArrayList;
import java.util.List;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;

public class PerformanceFragment extends Fragment {
    private FragmentPerformanceBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_performance, container, false);
        binding = FragmentPerformanceBinding.bind(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //List<Line> lines = new ArrayList<>();
        List<Matter> matters = DataBase.get().getBoxStore()
                .boxFor(Matter.class)
                .query()
                .order(Matter_.title_)
                .equal(Matter_.year_, 2019)
                .and()
                .equal(Matter_.period_, 2)
                .build()
                .find();

        List<AxisValue> axisMatter = new ArrayList<>();
        List<Column> columns = new ArrayList<>();

        for (int i = 0; i < matters.size(); i++) {
            Matter matter = matters.get(i);

            axisMatter.add(new AxisValue(i).setLabel(matter.getLabel()));

            List<SubcolumnValue> values = new ArrayList<>();
            values.add(new SubcolumnValue(matter.getLastPeriod().getPlotGrade(),
                    matter.getColor())
                    .setLabel(matter.getLastPeriod().getLabel()));
            columns.add(new Column(values)
                    .setHasLabels(true)
                    .setHasLabelsOnlyForSelected(true));
            /*List<PointValue> points = new ArrayList<>();
            List<Journal> journals = matter.getLastPeriod().journals;

            for (int i = 0; i < journals.size(); i++) {
                points.add(new PointValue(i, journals.get(i).getPlotGrade()));
            }

            lines.add(new Line(points).setColor(matter.getColor()));*/
        }

        /*LineChartData ld = new LineChartData();
        ld.setLines(lines);
        ld.setValueLabelBackgroundEnabled(false);
        ld.setValueLabelsTextColor(Color.CYAN);*/

        List<AxisValue> axisValues = new ArrayList<>();
        axisValues.add(new AxisValue(6).setLabel(""));
        ColumnChartData data = new ColumnChartData();
        data.setColumns(columns);
        data.setAxisYLeft(new Axis(axisValues)
                .setHasLines(true)
                .setLineColor(ContextCompat.getColor(getContext(), R.color.error))
                .setHasSeparationLine(false)
                .setHasTiltedLabels(false));
        data.setAxisXBottom(new Axis(axisMatter));
        binding.columnChart.setColumnChartData(data);
        binding.columnChart.setZoomEnabled(false);
    }

}
