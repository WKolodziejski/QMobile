package com.tinf.qmobile.holder.search;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.SearchAdapter;
import com.tinf.qmobile.model.message.Message;

import butterknife.BindView;

public class SearchEventViewHolder extends SearchViewHolder<Message> {
    @BindView(R.id.search_material_title)        TextView title;
    @BindView(R.id.search_material_subtitle)     TextView subtitle;
    @BindView(R.id.search_material_date)         TextView date;
    @BindView(R.id.search_material_icon)         ImageView icon;

    public SearchEventViewHolder(@NonNull View view) {
        super(view);
    }

    @Override
    public void bind(Message message, Context context, String query, SearchAdapter adapter) {
        /*title.setText(material.getTitle());
        subtitle.setText(material.getMatter());
        date.setText(material.getDateString());
        icon.setImageDrawable(context.getDrawable(material.getIcon()));

        itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, EventViewActivity.class);
            intent.putExtra("ID", material.id);
            intent.putExtra("TYPE", MATERIAL);
            context.startActivity(intent);
        });*/
    }

}
