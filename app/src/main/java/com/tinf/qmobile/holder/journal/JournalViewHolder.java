package com.tinf.qmobile.holder.journal;

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
import com.tinf.qmobile.adapter.JournalAdapter;
import com.tinf.qmobile.data.DataBase;
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

    public JournalViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    @Override
    public void bind(Context context, Journal journal, JournalAdapter adapter) {
        title.setText(journal.getTitle());
        weight.setText(journal.getWeight());
        date.setText(journal.formatDate());
        max.setText(journal.getMax());
        grade.setText(journal.getGrade());
        itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, EventViewActivity.class);
            intent.putExtra("ID", journal.id);
            intent.putExtra("TYPE", CalendarBase.ViewType.JOURNAL);
            context.startActivity(intent);

            itemView.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        });

        if (journal.isSeen_()) {
            itemView.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        } else {
            itemView.setBackgroundColor(context.getResources().getColor(R.color.notificationBackground));
            journal.see();
            DataBase.get().getBoxStore().boxFor(Journal.class).put(journal);
        }

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.item_slide_down);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        itemView.setAnimation(animation);
        animation.start();
    }

}
