package com.tinf.qmobile.holder;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.adapter.LoginCampusAdapter;
import com.tinf.qmobile.databinding.LoginCampusItemBinding;

public class LoginCampusViewHolder extends RecyclerView.ViewHolder {
  private final LoginCampusItemBinding binding;

  public LoginCampusViewHolder(View view) {
    super(view);
    binding = LoginCampusItemBinding.bind(view);
  }

  public void bind(String campus, LoginCampusAdapter.OnClick onClick) {
    binding.campusTxt.setText(campus);
    binding.getRoot().setOnClickListener(view -> onClick.onClick(getAdapterPosition()));
  }

}
