package com.tinf.qmobile.holder.search;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.MatterActivity;
import com.tinf.qmobile.model.material.Material;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.User;

import butterknife.BindView;

import static com.tinf.qmobile.activity.MatterActivity.MATERIALS;

public class SearchMaterialViewHolder extends SearchViewHolder<Material> {
    @BindView(R.id.search_material_title)        TextView title;
    @BindView(R.id.search_material_subtitle)     TextView subtitle;
    @BindView(R.id.search_material_date)         TextView date;
    @BindView(R.id.search_material_icon)         ImageView icon;

    public SearchMaterialViewHolder(@NonNull View view) {
        super(view);
    }

    @Override
    public void bind(Material material, Context context) {
        title.setText(material.getTitle());
        subtitle.setText(material.getMatter());
        date.setText(material.getDateString());
        icon.setImageDrawable(context.getDrawable(material.getIcon()));

        //itemView.setOnClickListener(view -> onQuery.onQuery(material.matter.getTargetId(), material.id, MATERIALS));
        itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, MatterActivity.class);
            intent.putExtra("ID", material.matter.getTargetId());
            intent.putExtra("ID2", material.id);
            intent.putExtra("PAGE", MATERIALS);
            context.startActivity(intent);

            Matter m = material.matter.getTarget();
            int i = User.getPos(m.getYear_(), m.getPeriod_());

            if (i >= 0)
                Client.get().changeDateWithBackup(i);
        });
    }

}
