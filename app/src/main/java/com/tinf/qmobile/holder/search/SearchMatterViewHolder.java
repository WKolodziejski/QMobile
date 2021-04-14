package com.tinf.qmobile.holder.search;

import android.content.Context;
import android.view.View;
import androidx.annotation.NonNull;
import com.tinf.qmobile.adapter.SearchAdapter;
import com.tinf.qmobile.databinding.SearchMatterBinding;
import com.tinf.qmobile.model.matter.Matter;

public class SearchMatterViewHolder extends SearchViewHolder<Matter> {
    private final SearchMatterBinding binding;

    public SearchMatterViewHolder(@NonNull View view) {
        super(view);
        binding = SearchMatterBinding.bind(view);
    }

    @Override
    public void bind(Matter matter, Context context, String query, SearchAdapter adapter) {
       /* title.setText(material.getTitle());
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
