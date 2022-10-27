package com.tinf.qmobile.holder.journal;

import static com.tinf.qmobile.model.ViewType.JOURNAL;
import static com.tinf.qmobile.utility.Design.getColorForGrade;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.databinding.JournalItemBinding;
import com.tinf.qmobile.model.journal.Journal;

public class JournalViewHolder extends JournalBaseViewHolder<Journal> {
    private final JournalItemBinding binding;

    public JournalViewHolder(View view) {
        super(view);
        binding = JournalItemBinding.bind(view);
    }

    @Override
    public void bind(Context context, Journal journal, boolean lookup, boolean isHeader) {
        binding.title.setText(journal.getTitle());
        binding.weight.setText(journal.getWeight());
        binding.date.setText(journal.formatDate());
        binding.max.setText(journal.getMax());
        binding.grade.setText(journal.getGrade());
        itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, EventViewActivity.class);
            intent.putExtra("ID", journal.id);
            intent.putExtra("TYPE", JOURNAL);
            intent.putExtra("LOOKUP", lookup);
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

        /*Animation animation = AnimationUtils.loadAnimation(context, R.anim.item_slide_down);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        itemView.setAnimation(animation);
        animation.start();*/

        /*if (journal.highlight) {
            itemView.setBackgroundColor(context.getResources().getColor(R.color.notificationBackground));
        }*/

        if (journal.getGrade().equals("-")) {
            binding.grade.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            binding.color.setCardBackgroundColor(context.getResources().getColor(R.color.transparent));
        } else {
            binding.grade.setTextColor(context.getResources().getColor(R.color.white));
            binding.color.setCardBackgroundColor(getColorForGrade(context, journal.getGrade_() / journal.getMax_() * 10));
        }
    }

}
