package com.tinf.qmobile.activity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.messages_fragment, new MessagesFragment())
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