package com.tinf.qmobile.activity.settings;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tinf.qmobile.databinding.ActivityMoreBinding;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.ColorsUtils;

public class MoreActivity extends AppCompatActivity {
  private ActivityMoreBinding binding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ColorsUtils.setSystemBarColor(this, com.google.android.material.R.attr.colorSurface);
    binding = ActivityMoreBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    binding.url.setText(Client.get().getURL());

    binding.logo.setOnLongClickListener(v -> {
      Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
      vibrator.vibrate(1000);
      Toast.makeText(getBaseContext(), "\ud83d\udc03", Toast.LENGTH_SHORT).show();

      return true;
    });
  }

}
