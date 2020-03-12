package com.tinf.qmobile.holder.materials;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.MatterActivity;
import com.tinf.qmobile.adapter.MaterialsAdapter;
import com.tinf.qmobile.model.matter.Matter;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MaterialHeaderViewHolder extends MaterialBaseViewHolder<Matter> {
    @BindView(R.id.material_title)           public TextView title;
    @BindView(R.id.material_color_badge)     public TextView badge;

    public MaterialHeaderViewHolder(@NonNull View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    @Override
    public void bind(Context context, Matter matter, MaterialsAdapter.OnInteractListener listener, MaterialsAdapter adapter) {
        title.setText(matter.getTitle());
        badge.setBackgroundTintList(ColorStateList.valueOf(matter.getColor()));

        int n = matter.getMaterialNotSeenCount();

        if (n > 0) {
            badge.setText(String.valueOf(n));
        } else {
            badge.setText("");
        }

        itemView.setOnClickListener(view -> listener.onClick(matter));
    }

}
