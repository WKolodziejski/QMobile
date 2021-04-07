package com.tinf.qmobile.holder.calendar.vertical;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.card.MaterialCardView;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.holder.calendar.CalendarViewHolder;
import com.tinf.qmobile.model.matter.Clazz;
import butterknife.BindView;
import static com.tinf.qmobile.model.ViewType.CLASS;

public class EventClazzVerticalViewHolder extends CalendarViewHolder<Clazz> {
    @BindView(R.id.calendar_class_title)    TextView title;
    @BindView(R.id.calendar_class_matter)   TextView matter;
    @BindView(R.id.calendar_class_card)     MaterialCardView card;

    @BindView(R.id.calendar_header_simple_day_week)     TextView day;
    @BindView(R.id.calendar_header_simple_day_number)   TextView number;
    @BindView(R.id.calendar_header_simple_layout)       LinearLayout layout;

    public EventClazzVerticalViewHolder(View view) {
        super(view);
    }

    @Override
    public void bind(Clazz clazz, Context context) {
        title.setText(clazz.getTitle());
        matter.setText(clazz.getMatter());
        card.setStrokeColor(clazz.getColor());
        title.setTextColor(clazz.getColor());
        matter.setTextColor(clazz.getColor());

        card.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventViewActivity.class);
            intent.putExtra("TYPE", CLASS);
            intent.putExtra("ID", clazz.id);
            context.startActivity(intent);
        });

        if (clazz.isHeader) {
            day.setText(clazz.getWeekString());
            number.setText(clazz.getDayString());
            layout.setVisibility(View.VISIBLE);
        } else {
            layout.setVisibility(View.INVISIBLE);
        }
    }

}
