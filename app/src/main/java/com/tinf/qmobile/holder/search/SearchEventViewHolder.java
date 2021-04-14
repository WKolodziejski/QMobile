package com.tinf.qmobile.holder.search;

import android.content.Context;
import android.view.View;
import com.tinf.qmobile.adapter.SearchAdapter;
import com.tinf.qmobile.databinding.SearchEventBinding;
import com.tinf.qmobile.model.message.Message;

public class SearchEventViewHolder extends SearchViewHolder<Message> {
    private final SearchEventBinding binding;

    public SearchEventViewHolder(View view) {
        super(view);
        binding = SearchEventBinding.bind(view);
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
