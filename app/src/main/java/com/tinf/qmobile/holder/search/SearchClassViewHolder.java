package com.tinf.qmobile.holder.search;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.View;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.adapter.SearchAdapter;
import com.tinf.qmobile.databinding.SearchClassBinding;
import com.tinf.qmobile.model.matter.Clazz;
import static com.tinf.qmobile.model.ViewType.CLASS;

public class SearchClassViewHolder extends SearchViewHolder<Clazz> {
    private SearchClassBinding binding;

    public SearchClassViewHolder(View view) {
        super(view);
        binding = SearchClassBinding.bind(view);
    }

    @Override
    public void bind(Clazz clazz, Context context, String query, SearchAdapter adapter) {
        binding.title.setText(clazz.getContent());
        binding.subtitle.setText(clazz.getMatter());
        binding.date.setText(clazz.formatDate());
        binding.icon.setImageTintList(ColorStateList.valueOf(clazz.getColor()));

        itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, EventViewActivity.class);
            intent.putExtra("ID", clazz.id);
            intent.putExtra("TYPE", CLASS);
            context.startActivity(intent);

            saveQuery(query);
        });
    }

}
