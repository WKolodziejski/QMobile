package com.tinf.qmobile.holder.Journal;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.adapter.journal.JournalAdapter3;
import com.tinf.qmobile.model.calendar.base.CalendarBase;
import com.tinf.qmobile.model.journal.Journal;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JournalViewHolder extends JournalBaseViewHolder<Journal> {
    @BindView(R.id.journal_view_title_text) public TextView title;
    @BindView(R.id.journal_view_weight)     public TextView weight;
    @BindView(R.id.journal_view_date)       public TextView date;
    @BindView(R.id.journal_view_max)        public TextView max;
    @BindView(R.id.journal_view_grade)      public TextView grade;
    @BindView(R.id.journal_view_layout)     public LinearLayout layout;

    public JournalViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    @Override
    public void bind(Context context, Journal journal, JournalAdapter3 adapter) {
        title.setText(journal.getTitle());
        weight.setText(journal.getWeight());
        date.setText(journal.formatDate());
        max.setText(journal.getMax());
        grade.setText(journal.getGrade());
        layout.setOnClickListener(view -> {
            Intent intent = new Intent(context, EventViewActivity.class);
            intent.putExtra("ID", journal.id);
            intent.putExtra("TYPE", CalendarBase.ViewType.JOURNAL);
            context.startActivity(intent);
        });

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.item_slide_down);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        layout.setAnimation(animation);
        animation.start();
    }

}
