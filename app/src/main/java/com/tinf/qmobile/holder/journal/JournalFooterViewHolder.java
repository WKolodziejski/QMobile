package com.tinf.qmobile.holder.journal;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.MateriaActivity;
import com.tinf.qmobile.adapter.JournalAdapter;
import com.tinf.qmobile.model.journal.Footer;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JournalFooterViewHolder extends JournalBaseViewHolder<Footer> {
    @BindView(R.id.journal_partial_grade_text)  public TextView partial;
    @BindView(R.id.journal_absences_text)       public TextView absences;
    @BindView(R.id.exp_details)                 public TextView details;
    @BindView(R.id.journal_footer_layout)       public ConstraintLayout layout;

    public JournalFooterViewHolder(@NonNull View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    @Override
    public void bind(Context context, Footer footer, JournalAdapter adapter) {
        float p = footer.getMatter().getGradeSum();

        partial.setText(p == -1 ? "-" : String.format("%.1f", p));
        absences.setText(footer.getMatter().getAbsences());
        details.setTextColor(footer.getMatter().getColor());
        details.setOnClickListener(view -> {
            Intent intent = new Intent(context, MateriaActivity.class);
            intent.putExtra("ID", footer.getMatter().id);
            intent.putExtra("PAGE", MateriaActivity.GRADES);
            context.startActivity(intent);
        });
        layout.setOnClickListener(view -> {
            footer.getMatter().isExpanded = false;
            adapter.collapse(footer.getPosition(), footer.getMatter());
        });
    }

}
