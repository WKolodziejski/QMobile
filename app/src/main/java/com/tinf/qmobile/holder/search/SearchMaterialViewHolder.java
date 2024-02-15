package com.tinf.qmobile.holder.search;

import static com.tinf.qmobile.model.ViewType.MATERIAL;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.tinf.qmobile.activity.MatterActivity;
import com.tinf.qmobile.adapter.SearchAdapter;
import com.tinf.qmobile.databinding.SearchMaterialBinding;
import com.tinf.qmobile.model.material.Material;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.DesignUtils;
import com.tinf.qmobile.utility.UserUtils;

public class SearchMaterialViewHolder extends SearchViewHolder<Material> {
  private final SearchMaterialBinding binding;

  public SearchMaterialViewHolder(
      @NonNull
      View view) {
    super(view);
    binding = SearchMaterialBinding.bind(view);
  }

  @Override
  public void bind(Material material, Context context, String query, SearchAdapter adapter) {
    binding.title.setText(material.getTitle());
    binding.subtitle.setText(material.getMatter());
    binding.date.setText(material.getDateString());
    binding.icon.setImageDrawable(DesignUtils.getDrawable(context, material.getIcon()));

    //itemView.setOnClickListener(view -> onQuery.onQuery(material.matter.getTargetId(), material
    // .id, MATERIALS));
    itemView.setOnClickListener(view -> {
      Intent intent = new Intent(context, MatterActivity.class);
      intent.putExtra("ID", material.matter.getTargetId());
      intent.putExtra("ID2", material.id);
      intent.putExtra("PAGE", MATERIAL);
      intent.putExtra("LOOKUP", false);
      context.startActivity(intent);

      Matter m = material.matter.getTarget();
      int i = UserUtils.getPos(m.getYear_(), m.getPeriod_());

      if (i >= 0)
        Client.get().changeDateWithBackup(i);

      saveQuery(query);
    });
  }

}
