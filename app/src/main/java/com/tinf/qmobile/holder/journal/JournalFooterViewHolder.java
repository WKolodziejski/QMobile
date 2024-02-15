package com.tinf.qmobile.holder.journal;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.tinf.qmobile.model.ViewType.JOURNAL;
import static com.tinf.qmobile.model.ViewType.MATTER;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.google.android.material.color.ColorRoles;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.MatterActivity;
import com.tinf.qmobile.databinding.JournalFooterBinding;
import com.tinf.qmobile.model.journal.FooterJournal;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.utility.ColorUtils;
import com.tinf.qmobile.utility.ColorsUtils;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;

public class JournalFooterViewHolder extends JournalBaseViewHolder<FooterJournal> {
  private final JournalFooterBinding binding;

  public JournalFooterViewHolder(View view) {
    super(view);
    binding = JournalFooterBinding.bind(view);
  }

  @Override
  public void bind(Context context, FooterJournal footer, boolean lookup, boolean isHeader) {
    Matter matter = footer.getMatter();
    ColorRoles colorRoles = ColorsUtils.harmonizeWithPrimary(context, matter.getColor());

    int classesGiven = matter.getClassesGiven();
    int classesTotal = matter.getClassesTotal();
    int classesLeft = classesTotal - classesGiven;
    int absences = matter.getAbsences();
    int presences = Math.max(0, classesGiven - absences);

    if (classesGiven == 0 && absences > 0) {
      classesLeft -= absences;
    }

    binding.partialGrade.setText(matter.getLastGradeSumString());
    binding.partialAverage.setText(matter.getLastAverageString());
    binding.absences.setText(matter.getAbsencesString());
    binding.chartPresence.setVisibility(matter.getAbsences_() >= 0 ? VISIBLE : INVISIBLE);
    binding.absences.setTextColor(colorRoles.getOnAccentContainer());

    binding.layout.setOnClickListener(view -> {
      Intent intent = new Intent(context, MatterActivity.class);
      intent.putExtra("ID", matter.id);
      intent.putExtra("PAGE", JOURNAL);
      intent.putExtra("LOOKUP", lookup);
      context.startActivity(intent);
    });

    List<SliceValue> values = new ArrayList<>();

    values.add(new SliceValue(classesLeft)
                   .setColor(context.getColor(R.color.colorPrimaryDark))
                   .setLabel(""));

    if (presences > 0) {
      values.add(new SliceValue(presences)
                     .setColor(colorRoles.getAccentContainer())
                     .setLabel(""));
    }

    if (absences > 0) {
      values.add(new SliceValue(absences)
                     .setColor(colorRoles.getAccent())
                     .setLabel(""));
    }

    binding.chartPresence.setPieChartData(new PieChartData(values)
                                              .setHasCenterCircle(true)
                                              .setCenterCircleScale(0.75f)
                                              .setHasLabelsOnlyForSelected(true));
    binding.chartPresence.setChartRotation(-90, false);
    binding.chartPresence.setInteractive(false);
  }

}
