package com.tinf.qmobile.holder.journal;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.JournalAdapter;
import com.tinf.qmobile.model.journal.FooterPeriod;
import java.util.Locale;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PeriodFooterViewHolder extends JournalBaseViewHolder<FooterPeriod> {
    @BindView(R.id.period_partial_grade_text)   public TextView partial;
    @BindView(R.id.period_final_grade_text)     public TextView grade;
    @BindView(R.id.period_absences_text)        public TextView absences;

    public PeriodFooterViewHolder(@NonNull View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    @Override
    public void bind(Context context, FooterPeriod footer, JournalAdapter adapter) {
        partial.setText(footer.period.getGradeSumString());
        grade.setText(footer.period.getGrade());
        absences.setText(footer.period.getAbsences());
    }

}
