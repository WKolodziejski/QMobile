package com.tinf.qmobile.holder.calendar.vertical;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.databinding.CalendarEventClazzVBinding;
import com.tinf.qmobile.holder.calendar.CalendarViewHolder;
import com.tinf.qmobile.model.matter.Clazz;
import static com.tinf.qmobile.model.ViewType.CLASS;

public class EventClazzVerticalViewHolder extends CalendarViewHolder<Clazz> {
    private final CalendarEventClazzVBinding binding;

    public EventClazzVerticalViewHolder(View view) {
        super(view);
        binding = CalendarEventClazzVBinding.bind(view);
    }

    @Override
    public void bind(Clazz clazz, Context context) {
        binding.title.setText(clazz.getTitle());
        binding.matter.setText(clazz.getMatter());
        binding.card.setStrokeColor(clazz.getColor());
        binding.title.setTextColor(clazz.getColor());
        binding.matter.setTextColor(clazz.getColor());

        binding.card.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventViewActivity.class);
            intent.putExtra("TYPE", CLASS);
            intent.putExtra("ID", clazz.id);
            context.startActivity(intent);
        });

        if (clazz.isHeader) {
            binding.header.day.setText(clazz.getWeekString());
            binding.header.number.setText(clazz.getDayString());
            binding.header.layout.setVisibility(View.VISIBLE);
        } else {
            binding.header.layout.setVisibility(View.INVISIBLE);
        }
    }

}
