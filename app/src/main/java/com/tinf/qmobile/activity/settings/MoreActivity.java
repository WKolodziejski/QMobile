package com.tinf.qmobile.activity.settings;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.tinf.qmobile.databinding.ActivityMoreBinding;
import com.tinf.qmobile.network.Client;

public class MoreActivity extends AppCompatActivity {
    private ActivityMoreBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMoreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.url.setText(Client.get().getURL());
    }

}
