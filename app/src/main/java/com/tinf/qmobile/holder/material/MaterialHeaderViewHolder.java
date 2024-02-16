package com.tinf.qmobile.holder.material;

import android.content.Context;
import android.view.ActionMode;
import android.view.View;

import androidx.annotation.NonNull;

import com.tinf.qmobile.adapter.MaterialsBaseAdapter;
import com.tinf.qmobile.adapter.OnMaterialInteractListener;
import com.tinf.qmobile.model.journal.Header;

public class MaterialHeaderViewHolder extends MaterialBaseViewHolder<Header> {


  public MaterialHeaderViewHolder(
      @NonNull
      View view) {
    super(view);
//    binding = MaterialHeaderBinding.bind(view);
  }

  @Override
  public void bind(Context context, OnMaterialInteractListener listener,
                   MaterialsBaseAdapter adapter,
                   ActionMode.Callback callback, Header matter, boolean isHeader) {
//    ColorRoles colorRoles = ColorsUtils.harmonizeWithPrimary(context, matter.getColor());
//
//    binding.title.setText(matter.getTitle());
//    binding.badge.setVisibility(isHeader ? View.INVISIBLE : View.VISIBLE);
//    binding.badge.setBackgroundTintList(ColorStateList.valueOf(colorRoles.getAccentContainer()));
//    binding.badge.setTextColor(colorRoles.getOnAccentContainer());
//
//    int n = matter.getMaterialNotSeenCount();
//
//    if (n > 0) {
//      binding.badge.setText(String.valueOf(n));
//    } else {
//      binding.badge.setText("");
//    }
//
//    itemView.setOnClickListener(view -> {
//      if (!listener.isSelectionMode()) {
//        Intent intent = new Intent(context, MatterActivity.class);
//        intent.putExtra("ID", matter.id);
//        intent.putExtra("PAGE", MATERIAL);
//        context.startActivity(intent);
//      }
//    });
  }

}
