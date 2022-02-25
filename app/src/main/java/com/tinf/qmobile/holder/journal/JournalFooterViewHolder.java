package com.tinf.qmobile.holder.journal;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.tinf.qmobile.model.ViewType.HEADER;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import com.tinf.qmobile.activity.MatterActivity;
import com.tinf.qmobile.databinding.JournalFooterBinding;
import com.tinf.qmobile.model.journal.FooterJournal;
import com.tinf.qmobile.model.matter.Matter;

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
        int percentage = classes <= 0 ? 0 : (int) ((float) presences / classes * 100f);

        binding.partialGrade.setText(matter.getLastGradeSumString());
        binding.absences.setText(matter.getAbsencesString());
        binding.progress.setIndicatorColor(matter.getColor());
        binding.progress.setProgress(percentage);
        binding.progress.setVisibility(classes > 0 ? VISIBLE : INVISIBLE);

        binding.layout.setOnClickListener(view -> {
            Intent intent = new Intent(context, MatterActivity.class);
            intent.putExtra("ID", matter.id);
            intent.putExtra("PAGE", HEADER);
            intent.putExtra("LOOKUP", lookup);
            context.startActivity(intent);
        });
    }

}
