package com.tinf.qmobile.holder.search;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.tinf.qmobile.R;
import com.tinf.qmobile.model.search.Header;
import butterknife.BindView;

public class SearchHeaderViewHolder extends SearchViewHolder<Header> {
    @BindView(R.id.search_header_title)       TextView title;

    public SearchHeaderViewHolder(@NonNull View view) {
        super(view);
    }

    @Override
    public void bind(Header header, Context context) {
        title.setText(header.getTitle());
    }

}
