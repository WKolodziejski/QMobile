package com.tinf.qmobile.activity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.tinf.qmobile.R;
import com.tinf.qmobile.fragment.MessagesFragment;
import com.tinf.qmobile.service.DownloadReceiver;

import static android.app.DownloadManager.ACTION_DOWNLOAD_COMPLETE;

public class MessagesActivity extends AppCompatActivity {
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        setSupportActionBar(findViewById(R.id.toolbar_default));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_cancel));

        Fragment fragment = new MessagesFragment();
        fragment.setArguments(getIntent().getExtras());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.messages_fragment, fragment)
                .commit();

        receiver = new DownloadReceiver((DownloadManager) getSystemService(DOWNLOAD_SERVICE), id -> {});

        registerReceiver(receiver, new IntentFilter(ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

}