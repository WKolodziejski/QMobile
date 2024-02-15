package com.tinf.qmobile.fragment.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.tinf.qmobile.R;

public class PopUpFragment extends BottomSheetDialogFragment {
  private TextView title;
  private TextView message;
  private CheckBox checkBox;
  private String t;
  private String m;

  public void setComponents(String t,
                            String m) {
    this.t = t;
    this.m = m;
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
    View view = inflater.inflate(R.layout.sheet_popup, container, false);
    title = view.findViewById(R.id.title);
    message = view.findViewById(R.id.message);
    checkBox = view.findViewById(R.id.checkBox);
    return view;
  }

  @Override
  public void onViewCreated(
      @NonNull
      View view,
      @Nullable
      Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    title.setText(t);
    message.setText(m);
  }

  @Override
  public void onDismiss(
      @NonNull
      DialogInterface dialog) {
    super.onDismiss(dialog);

    if (checkBox.isChecked()) {
      // TODO: não ver novamente
    }
  }

}
