package com.tinf.qmobile.holder.journal;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.tinf.qmobile.model.ViewType.HEADER;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;

import com.tinf.qmobile.activity.MatterActivity;
import com.tinf.qmobile.databinding.JournalFooterBinding;
import com.tinf.qmobile.model.journal.FooterJournal;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.utility.ColorUtils;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.model.SliceValue;

public class JournalFooterViewHolder extends JournalBaseViewHolder<FooterJournal> {
    private final JournalFooterBinding binding;

    public JournalFooterViewHolder(View view) {
        super(view);
        binding = JournalFooterBinding.bind(view);
    }

    @Override
    public void bind(Context context, FooterJournal footer, boolean lookup) {
        Matter matter = footer.getMatter();
        int classes = matter.getClassesGiven();
        int absences = matter.getAbsences();
        int presences = classes - absences;
//        int percentage = classes <= 0 ? 0 : (int) ((float) presences / classes * 100f);
        int color1 = matter.getColor();
        int color2 = ColorUtils.INSTANCE.contrast(color1, 0.25f);

        binding.partialGrade.setText(matter.getLastGradeSumString());
        binding.absences.setText(matter.getAbsencesString());
//        binding.progress.setIndicatorColor(matter.getColor());
//        binding.progress.setProgress(percentage);
        binding.chartPresence.setVisibility(classes >= 0 ? VISIBLE : INVISIBLE);

        binding.layout.setOnClickListener(view -> {
            Intent intent = new Intent(context, MatterActivity.class);
            intent.putExtra("ID", matter.id);
            intent.putExtra("PAGE", HEADER);
            intent.putExtra("LOOKUP", lookup);
            context.startActivity(intent);
        });

        List<SliceValue> values = new ArrayList<>();

        values.add(new SliceValue(presences > 0 ? presences : 1)
                .setColor(color1)
                .setLabel(""));

        if (absences > 0) {
            values.add(new SliceValue(absences)
                    .setColor(color2)
                    .setLabel(""));
        }

        binding.chartPresence.setPieChartData(new PieChartData(values)
                .setHasCenterCircle(true)
                .setCenterCircleScale(0.75f)
                .setHasLabelsOnlyForSelected(true));
        binding.chartPresence.setChartRotation(-90, false);
        binding.chartPresence.setInteractive(false);

        if (absences > 0) {
            binding.chartPresence.selectValue(
                    new SelectedValue(1, 0, SelectedValue.SelectedValueType.LINE));
        }
    }

}
