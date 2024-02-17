package com.tinf.qmobile.holder.material;

import static com.tinf.qmobile.model.ViewType.MATERIAL;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.ActionMode;
import android.view.View;

import androidx.annotation.NonNull;

import com.tinf.qmobile.activity.MatterActivity;
import com.tinf.qmobile.adapter.MaterialsBaseAdapter;
import com.tinf.qmobile.adapter.OnMaterialInteractListener;
import com.tinf.qmobile.databinding.MaterialHeaderBinding;
import com.tinf.qmobile.model.matter.Matter;

public class MaterialMatterViewHolder extends MaterialBaseViewHolder<Matter> {

  private final MaterialHeaderBinding binding;

  public MaterialMatterViewHolder(
      @NonNull
      View view) {
    super(view);
    binding = MaterialHeaderBinding.bind(view);
  }

  @Override
  public void bind(Context context, OnMaterialInteractListener listener,
                   MaterialsBaseAdapter adapter, ActionMode.Callback callback, Matter matter,
                   boolean isHeader) {
    binding.title.setText(matter.getTitle());
    binding.badge.setVisibility(isHeader ? View.INVISIBLE : View.VISIBLE);
    binding.badge.setBackgroundTintList(ColorStateList.valueOf(matter.getColor()));

    int n = matter.getMaterialNotSeenCount();

    if (n > 0) {
      binding.badge.setText(String.valueOf(n));
    } else {
      binding.badge.setText("");
    }

    itemView.setOnClickListener(view -> {
      if (!listener.isSelectionMode()) {
        Intent intent = new Intent(context, MatterActivity.class);
        intent.putExtra("ID", matter.id);
        intent.putExtra("PAGE", MATERIAL);
        context.startActivity(intent);
      }
    });
  }
}