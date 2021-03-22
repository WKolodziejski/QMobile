package com.tinf.qmobile.holder.search;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.adapter.SearchAdapter;
import com.tinf.qmobile.model.matter.Clazz;

import butterknife.BindView;

import static com.tinf.qmobile.model.ViewType.CLASS;

public class SearchClassViewHolder extends SearchViewHolder<Clazz> {
    @BindView(R.id.search_class_title)        TextView title;
    @BindView(R.id.search_class_subtitle)     TextView subtitle;
    @BindView(R.id.search_class_date)         TextView date;
    @BindView(R.id.search_class_icon)         ImageView icon;

    public SearchClassViewHolder(@NonNull View view) {
        super(view);
    }

    @Override
    public void bind(Clazz clazz, Context context, String query, SearchAdapter adapter) {
        title.setText(clazz.getContent());
        subtitle.setText(clazz.getMatter());
        date.setText(clazz.formatDate());
        icon.setImageTintList(ColorStateList.valueOf(clazz.getColor()));

        itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, EventViewActivity.class);
            intent.putExtra("ID", clazz.id);
            intent.putExtra("TYPE", CLASS);
            context.startActivity(intent);

            saveQuery(query);
        });
    }

}
