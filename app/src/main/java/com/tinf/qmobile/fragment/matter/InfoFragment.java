package com.tinf.qmobile.fragment.matter;

import static com.tinf.qmobile.model.ViewType.JOURNAL;
import static com.tinf.qmobile.utility.ChartUtils.getChartData;

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
import com.tinf.qmobile.activity.MatterActivity;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.databinding.FragmentInfoBinding;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.utility.ColorUtils;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.android.AndroidScheduler;
import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscription;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.model.SliceValue;
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

        DataObserver observer = data -> setText();

        sub1 = DataBase.get().getBoxStore().subscribe(Journal.class)
                .on(AndroidScheduler.mainThread())
                .onlyChanges()
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(observer);

        sub2 = DataBase.get().getBoxStore().subscribe(Matter.class)
                .on(AndroidScheduler.mainThread())
                .onlyChanges()
                .onError(th -> Log.e(th.getMessage(), th.toString()))
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

        setText();
    }

    private void setText() {
        Matter matter = DataBase.get().getBoxStore().boxFor(Matter.class).get(id);

        if (matter == null)
            return;

        int color = matter.getColor();
        int color2 = ColorUtils.INSTANCE.lighten(color, 0.25f);
        int classes = matter.getClassesGiven_();
        int absences = matter.getAbsences_();
        int presences = classes - absences;

        List<SliceValue> values = new ArrayList<>();

        values.add(new SliceValue(presences)
                .setColor(color)
                .setLabel(String.valueOf(((float) presences / classes) * 100f) + "%"));
        values.add(new SliceValue(matter.getAbsences_())
                .setColor(color2)
                .setLabel(""));

        binding.chartPresence.setPieChartData(new PieChartData(values)
                .setHasCenterCircle(true)
                .setCenterCircleScale(0.35f)
                .setHasLabelsOnlyForSelected(true));
        binding.chartPresence.selectValue(new SelectedValue(1, 0, SelectedValue.SelectedValueType.LINE));
        binding.chartPresence.setChartRotation(-90, true);

        binding.presenceIc.setImageTintList(ColorStateList.valueOf(color));
        binding.absenceIc.setImageTintList(ColorStateList.valueOf(color2));

        binding.presenceTxt.setText(matter.getPresences());
        binding.absenceTxt.setText(matter.getAbsences());

        binding.hoursTxt.setText(String.valueOf(matter.getGivenHours()));
        binding.teacherTxt.setText(matter.getTeacher_());

        binding.classesTxtL.setText(String.valueOf(matter.getClassesGiven_()));
        binding.classesTxtR.setText(String.valueOf(matter.getClassesTotal_()));
        binding.classesProgress.setIndicatorColor(color);
        binding.classesProgress.setTrackColor(color2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.classesProgress.setProgress(matter.getClassesGiven_() / matter.getClassesTotal_() * 100, true);
        } else {
            binding.classesProgress.setProgress(matter.getClassesGiven_() / matter.getClassesTotal_() * 100);
        }

        binding.averageTxtL.setText(String.valueOf(matter.getAllGradesSum()));
        binding.averageTxtR.setText(String.valueOf(matter.getAllMaxGradesSum()));
        binding.averageProgress.setIndicatorColor(color);
        binding.averageProgress.setTrackColor(color2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.averageProgress.setProgress((int) (matter.getAllGradesSum() / matter.getAllMaxGradesSum() * 100), true);
        } else {
            binding.averageProgress.setProgress((int) (matter.getAllGradesSum() / matter.getAllMaxGradesSum() * 100));
        }

        binding.chartGrades.setLineChartData(getChartData(matter));
        binding.chartGrades.setZoomEnabled(false);
        binding.chartGrades.setScrollEnabled(false);
        binding.chartGrades.setViewportCalculationEnabled(false);
        final Viewport v = new Viewport(binding.chartGrades.getMaximumViewport());
        v.top = 20;
        v.bottom = -10;
        binding.chartGrades.setMaximumViewport(v);
        binding.chartGrades.setCurrentViewport(v);
    }

}
