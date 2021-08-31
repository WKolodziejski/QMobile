package com.tinf.qmobile.holder.journal;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.View;

import com.tinf.qmobile.activity.MatterActivity;
import com.tinf.qmobile.adapter.JournalAdapter;
import com.tinf.qmobile.databinding.JournalHeaderBinding;
import com.tinf.qmobile.model.matter.Matter;

public class JournalHeaderViewHolder extends JournalBaseViewHolder<Matter> {
    private final JournalHeaderBinding binding;

    public JournalHeaderViewHolder(View view) {
        super(view);
        binding = JournalHeaderBinding.bind(view);
    }

    @Override
    public void bind(Context context, Matter matter, JournalAdapter adapter) {
        binding.title.setText(matter.getTitle());
        binding.badge.setBackgroundTintList(ColorStateList.valueOf(matter.getColor()));
        binding.header.setVisibility(/*matter.isExpanded &&*/ matter.hasJournals() ? View.VISIBLE : View.GONE);
        //binding.arrow.setRotation(matter.isExpanded ? 180 : 0);
        itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, MatterActivity.class);
            intent.putExtra("ID", matter.id);
            intent.putExtra("PAGE", MatterActivity.GRADES);
            context.startActivity(intent);
            /*if (matter.isExpanded) {
                adapter.collapse(getAdapterPosition(), matter);
            } else {
                adapter.expand(getAdapterPosition(), matter, true);
            }*/
        });

        int n = matter.getJournalNotSeenCount();
        binding.badge.setText(n > 0 ? String.valueOf(n) : "");
    }

}
