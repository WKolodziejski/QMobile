package com.tinf.qmobile.holder.journal;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import com.tinf.qmobile.activity.MatterActivity;
import com.tinf.qmobile.adapter.JournalAdapter;
import com.tinf.qmobile.databinding.JournalFooterBinding;
import com.tinf.qmobile.model.journal.FooterJournal;

public class JournalFooterViewHolder extends JournalBaseViewHolder<FooterJournal> {
    private final JournalFooterBinding binding;

    public JournalFooterViewHolder(View view) {
        super(view);
        binding = JournalFooterBinding.bind(view);
    }

    @Override
    public void bind(Context context, FooterJournal footer, JournalAdapter adapter) {
        binding.partialGrade.setText(footer.getMatter().getLastPeriod().getGradeSumString());
        binding.absences.setText(footer.getMatter().getAbsences());
        binding.details.setTextColor(footer.getMatter().getColor());
        binding.details.setOnClickListener(view -> {
            Intent intent = new Intent(context, MatterActivity.class);
            intent.putExtra("ID", footer.getMatter().id);
            intent.putExtra("PAGE", MatterActivity.GRADES);
            context.startActivity(intent);
        });
        itemView.setOnClickListener(view -> {
            footer.getMatter().isExpanded = false;
            adapter.collapse(footer.getPosition(), footer.getMatter());
        });
    }

}
