package com.tinf.qmobile.fragment.matter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.tinf.qmobile.model.ViewType.JOURNAL;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.databinding.FragmentInfoBinding;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Period;
import com.tinf.qmobile.utility.ColorUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import io.objectbox.android.AndroidScheduler;
import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscription;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
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

public class InfoFragment extends Fragment {
    private FragmentInfoBinding binding;
    private DataSubscription sub1, sub2;
    private long id;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        if (bundle != null) {
            id = bundle.getLong("ID");
        }

        DataObserver observer = data -> setText(false);

        sub1 = DataBase.get().getBoxStore().subscribe(Journal.class)
                .on(AndroidScheduler.mainThread())
                .onlyChanges()
                .onError(Throwable::printStackTrace)
                .observer(observer);

        sub2 = DataBase.get().getBoxStore().subscribe(Matter.class)
                .on(AndroidScheduler.mainThread())
                .onlyChanges()
                .onError(Throwable::printStackTrace)
                .observer(observer);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        binding = FragmentInfoBinding.bind(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (id == 0)
            return;

        setText(true);
    }

    private void setText(boolean onCreate) {
        Matter matter = DataBase.get().getBoxStore().boxFor(Matter.class).get(id);

        if (matter == null)
            return;

        int color1 = matter.getColor();
        int color2 = ColorUtils.INSTANCE.contrast(color1, 0.25f);
        int classesGiven = matter.getClassesGiven();
        int classesTotal = matter.getClassesTotal();
        int classesLeft = classesTotal - classesGiven;
        int absences = matter.getAbsences();
        int presences = Math.max(0, classesGiven - absences);
        float maxSum = matter.getAllMaxGradesSum();
        int classesProgress = classesTotal == 0 || classesGiven == 0 ? 0 : (int) (((float) classesGiven / classesTotal) * 100f);
        int averageProgress = maxSum == 0 ? 0 : (int) ((matter.getAllGradesSum() / maxSum) * 100f);

        if (classesGiven == 0 && absences > 0) {
            classesLeft -= absences;
        }

        List<SliceValue> values = new ArrayList<>();

        values.add(new SliceValue(classesLeft)
                .setColor(getResources().getColor(R.color.colorPrimaryDark))
                .setLabel(""));

        if (presences > 0) {
            values.add(new SliceValue(presences)
                    .setColor(color1)
                    .setLabel(""));
        }

        if (absences > 0) {
            values.add(new SliceValue(absences)
                    .setColor(color2)
                    .setLabel(""));
        }

        binding.chartPresence.setPieChartData(new PieChartData(values)
                .setHasCenterCircle(true)
                .setCenterCircleScale(0.35f)
                .setHasLabelsOnlyForSelected(true));
        binding.chartPresence.setChartRotation(-90, true);
        binding.chartPresence.setInteractive(false);

        if (absences > 0) {
            binding.chartPresence.selectValue(
                    new SelectedValue(presences > 0 ? 2 : 1, 0, SelectedValue.SelectedValueType.LINE));
        }

        binding.totalHoursTxt.setText(matter.getHoursString());
        binding.totalClassesTxt.setText(matter.getClassesTotalString());
        binding.situationTxt.setText(matter.getSituation());
        binding.clazzTxt.setText(matter.getClazz());
        binding.teacherTxt.setText(matter.getTeacher());

        binding.classesTxtL.setText(matter.getClassesGivenString());
        binding.classesTxtR.setText(matter.getClassesTotalString());
        binding.classesProgress.setIndicatorColor(color1);
        binding.classesProgress.setTrackColor(getResources().getColor(R.color.colorPrimaryDark));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.classesProgress.setProgress(classesProgress, true);
        } else {
            binding.classesProgress.setProgress(classesProgress);
        }

        binding.averageTxtL.setText(String.format(Locale.getDefault(), "%.1f", matter.getAllGradesSum()));
        binding.averageTxtR.setText(String.format(Locale.getDefault(), "%.1f", matter.getAllMaxGradesSum()));
        binding.averageProgress.setIndicatorColor(color1);
        binding.averageProgress.setTrackColor(getResources().getColor(R.color.colorPrimaryDark));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.averageProgress.setProgress(averageProgress, true);
        } else {
            binding.averageProgress.setProgress(averageProgress);
        }

        binding.presenceIc.setImageTintList(ColorStateList.valueOf(color1));
        binding.absenceIc.setImageTintList(ColorStateList.valueOf(color2));

        binding.presenceTxt.setText(matter.getPresencesString());
        binding.absenceTxt.setText(matter.getAbsencesString());

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

        if (points.size() > 1) {
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

            //binding.layoutGrades.setVisibility(points.isEmpty() ? View.GONE : View.VISIBLE);
            binding.chartGrades.setLineChartData(data);
            binding.chartGrades.setZoomEnabled(false);
            binding.chartGrades.setScrollEnabled(false);
            binding.chartGrades.setViewportCalculationEnabled(false);
//            binding.chartGrades.setZoomType(ZoomType.VERTICAL);
//            binding.chartGrades.setZoomLevel(0, 0, 10);
//            binding.chartGrades.setContainerScrollEnabled(true, ContainerScrollType.VERTICAL);

            if (onCreate) {
                final Viewport v = new Viewport(binding.chartGrades.getMaximumViewport());

                v.top += 1;
                v.bottom = 0;

                binding.chartGrades.setMaximumViewport(v);
                binding.chartGrades.setCurrentViewport(v);

                //binding.chartGrades.setZoomLevelWithAnimation(0f, 0f, 1);
            } //else {
               // binding.chartGrades.setZoomLevel(0f, 0f, 1);
            //}

            binding.chartGrades.setOnValueTouchListener(new LineChartOnValueSelectListener() {
                @Override
                public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
                    Intent intent = new Intent(getContext(), EventViewActivity.class);
                    intent.putExtra("ID", journals.get(pointIndex).id);
                    intent.putExtra("TYPE", JOURNAL);
                    intent.putExtra("LOOKUP", false);
                    startActivity(intent);
                }

                @Override
                public void onValueDeselected() {

                }
            });
            binding.layoutGrades.setVisibility(VISIBLE);
        } else {
            binding.layoutGrades.setVisibility(GONE);
        }

//        Radar radar = AnyChart.radar();
//        //radar.title("WoW base stats comparison radar chart: Shaman vs Warrior vs Priest");
//
//        //radar.yScale().minimum(0d);
//        //radar.yScale().minimumGap(0d);
//        //radar.yScale().ticks().interval(50d);
//
//        //radar.xAxis().labels().padding(5d, 5d, 5d, 5d);
//
//        radar.legend(false);
//        radar.title(false);
//        radar.fullScreen(true);
//
//        List<DataEntry> data = new ArrayList<>();
//        for (Journal journal : matter.getLastJournals()) {
//            data.add(new ValueDataEntry(journal.getTitle().substring(0, 3), journal.getGrade_()));
//        }
//
//        Set set = Set.instantiate();
//        set.data(data);
//        Mapping map = set.mapAs("{ x: 'x', value: 'value' }");
//
//        Area area = radar.area(data);
//        area.color(ColorUtils.INSTANCE.string(color));
//        //shamanLine.name("Shaman");
//        //shamanLine.markers()
//                //.enabled(true)
//                //.type(MarkerType.CIRCLE)
//                //.size(3d);
//
//        //radar.tooltip().format("Value: {%Value}");
//
//        binding.radar.setChart(radar);
//        binding.radar.setBackgroundColor("RED");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sub1.cancel();
        sub2.cancel();
    }

}
