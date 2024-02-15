package com.tinf.qmobile.fragment.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.tinf.qmobile.R;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.UserUtils;

public class UserFragment extends DialogFragment {
  private TextView name;
  private TextView reg;
  private TextView last;
  private MaterialCardView logout;
  private MaterialCardView alerts;
  private ImageView image;
  private Button policy;
  private OnButton onButton;
  private ConstraintLayout offline;

  public void setListener(OnButton onButton) {
    this.onButton = onButton;
  }

  @Nullable
  @Override
  public View onCreateView(
      @NonNull
      LayoutInflater inflater,
      @Nullable
      ViewGroup container,
      @Nullable
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.sheet_user, container, false);

    name = view.findViewById(R.id.name);
    reg = view.findViewById(R.id.reg);
    logout = view.findViewById(R.id.logout);
    image = view.findViewById(R.id.image);
    policy = view.findViewById(R.id.policy);
    alerts = view.findViewById(R.id.alerts);
    last = view.findViewById(R.id.last);
    offline = view.findViewById(R.id.offline);

    name.setText(UserUtils.getName());
    reg.setText(UserUtils.getCredential(UserUtils.REGISTRATION));

    logout.setOnClickListener(v -> new MaterialAlertDialogBuilder(getContext())
        .setTitle(getResources().getString(R.string.dialog_quit))
        .setMessage(R.string.dialog_quit_msg)
        .setPositiveButton(R.string.dialog_quit, (dialog, which) -> onButton.onLogout())
        .setNegativeButton(R.string.dialog_cancel, null)
        .create()
        .show());

    alerts.setOnClickListener(v -> onButton.onAlerts());

    if (UserUtils.hasImg()) {
      try {
        Glide.with(getContext())
             .load(UserUtils.getImgUrl())
             .circleCrop()
             .placeholder(R.drawable.ic_account)
             .into(image);
      } catch (Exception ignore) {
      }
    }

    policy.setOnClickListener(view1 -> {
      Intent intent = new Intent(Intent.ACTION_VIEW,
                                 Uri.parse(
                                     "https://sites.google" +
                                     ".com/view/qmobileapp/política-de-privacidade"));
      startActivity(intent);
    });

    if (!Client.isConnected() || (!Client.get().isValid() && !Client.get().isLogging())) {
      offline.setVisibility(View.VISIBLE);
      last.setText(String.format(getResources().getString(R.string.home_last_login),
                                 UserUtils.getLastLogin()));
    } else {
      offline.setVisibility(View.GONE);
    }

    return view;
  }

  public interface OnButton {
    void onLogout();

    void onAlerts();
  }

  @Override
  public void onResume() {
    super.onResume();

    Window window = getDialog().getWindow();
    WindowManager.LayoutParams params = window.getAttributes();

    params.width = WindowManager.LayoutParams.MATCH_PARENT;
    params.height = WindowManager.LayoutParams.WRAP_CONTENT;
    params.gravity = Gravity.TOP;
//        params.y = Design.dpiToPixels(60);

    window.setAttributes(params);
  }

  View theDialogView;

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
    theDialogView = onCreateView(LayoutInflater.from(requireContext()), null, savedInstanceState);
    builder.setView(theDialogView);

    return builder.create();
  }

  @Override
  public View getView() {
    return theDialogView;
  }
}
