package com.tinf.qmobile.holder.journal;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.MatterActivity;
import com.tinf.qmobile.adapter.JournalAdapter;
import com.tinf.qmobile.model.journal.FooterJournal;

import butterknife.BindView;

public class JournalFooterViewHolder extends JournalBaseViewHolder<FooterJournal> {
    @BindView(R.id.journal_partial_grade_text)  public TextView partial;
    @BindView(R.id.journal_absences_text)       public TextView absences;
    @BindView(R.id.journal_exp_details)         public TextView details;

    public JournalFooterViewHolder(@NonNull View view) {
        super(view);
    }

    @Override
    public void bind(Context context, FooterJournal footer, JournalAdapter adapter) {
        partial.setText(footer.getMatter().getLastPeriod().getGradeSumString());
        absences.setText(footer.getMatter().getAbsences());
        details.setTextColor(footer.getMatter().getColor());
        details.setOnClickListener(view -> {
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
