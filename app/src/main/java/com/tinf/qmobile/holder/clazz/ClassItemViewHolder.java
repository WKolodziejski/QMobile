package com.tinf.qmobile.holder.clazz;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.model.matter.Clazz;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.tinf.qmobile.model.ViewType.CLASS;
import static com.tinf.qmobile.model.ViewType.JOURNAL;

public class ClassItemViewHolder extends ClassBaseViewHolder<Clazz> {
    @BindView(R.id.class_date)          public TextView date;
    @BindView(R.id.class_content)       public TextView content;
    @BindView(R.id.class_absence)       public ImageView absence;

    public ClassItemViewHolder(@NonNull View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    @Override
    public void bind(Context context, Clazz clazz) {
        date.setText(clazz.formatDate());
        content.setText(clazz.getContent_());
        absence.setVisibility(clazz.getAbsences_() > 0 ? View.VISIBLE : View.INVISIBLE);

        itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, EventViewActivity.class);
            intent.putExtra("ID", clazz.id);
            intent.putExtra("TYPE", CLASS);
            context.startActivity(intent);
        });
    }

}
