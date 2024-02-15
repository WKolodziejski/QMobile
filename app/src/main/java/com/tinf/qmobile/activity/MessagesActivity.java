package com.tinf.qmobile.activity;

import static android.app.DownloadManager.ACTION_DOWNLOAD_COMPLETE;
import static com.tinf.qmobile.model.ViewType.MESSAGE;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.MessagesAdapter;
import com.tinf.qmobile.databinding.ActivityMessagesBinding;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.network.OnResponse;
import com.tinf.qmobile.service.DownloadReceiver;
import com.tinf.qmobile.utility.ColorsUtils;
import com.tinf.qmobile.utility.DesignUtils;

public class MessagesActivity extends AppCompatActivity implements OnResponse {
  private BroadcastReceiver receiver;
  private ActivityMessagesBinding binding;

  ActivityResultLauncher<Intent> launcher = registerForActivityResult(
      new ActivityResultContracts.StartActivityForResult(), result -> finish());

  @Override
  protected void onCreate(
      @Nullable
      Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ColorsUtils.setSystemBarColor(this, com.google.android.material.R.attr.colorSurface);
    binding = ActivityMessagesBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    setSupportActionBar(findViewById(R.id.toolbar_default));
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeAsUpIndicator(
        DesignUtils.getDrawable(this, R.drawable.ic_cancel));

    Client.get()
          .load(PG_MESSAGES);

    binding.refresh.setRefreshing(true);

    receiver = new DownloadReceiver((DownloadManager) getSystemService(DOWNLOAD_SERVICE), id -> {});

    registerReceiver(receiver, new IntentFilter(ACTION_DOWNLOAD_COMPLETE));

    binding.recycler.setLayoutManager(new LinearLayoutManager(this));
    binding.recycler.setAdapter(new MessagesAdapter(this));

    Bundle arguments = getIntent().getExtras();

    // TODO: remover necessidade de repasse do intent
    if (arguments != null && arguments.containsKey("ID2")) {
      long id = arguments.getLong("ID2");

      Log.d("MESSAGE", arguments.toString());
            /*int p = adapter.highlight(id);

            if (p >= 0) {
                layout.scrollToPosition(p);
                messenger.openMessage(p);

                Intent intent = new Intent(getContext(), EventViewActivity.class);
                intent.putExtra("TYPE", MESSAGE);
                intent.putExtra("ID", id);
                launcher.launch(intent);
            }*/

      Intent intent = new Intent(getBaseContext(), EventViewActivity.class);
      intent.putExtra("TYPE", MESSAGE);
      intent.putExtra("ID", id);
      launcher.launch(intent);
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    unregisterReceiver(receiver);
  }

  @Override
  public void onStart(int pg) {
    if (pg == PG_MESSAGES || pg == PG_MESSAGES_FORM || pg == PG_MESSAGE_FIND)
      binding.refresh.setRefreshing(true);
  }

  @Override
  public void onFinish(int pg,
                       int year,
                       int period) {
    if (pg == PG_MESSAGES || pg == PG_MESSAGES_FORM || pg == PG_MESSAGE_FIND)
      binding.refresh.setRefreshing(false);
  }

  @Override
  public void onError(int pg,
                      int year,
                      int period,
                      String error) {
    if (pg == PG_MESSAGES || pg == PG_MESSAGES_FORM || pg == PG_MESSAGE_FIND)
      binding.refresh.setRefreshing(false);
  }

  @Override
  public void onAccessDenied(int pg,
                             String message) {
    binding.refresh.setRefreshing(false);
  }

  @Override
  public void onStart() {
    super.onStart();
    Client.get()
          .addOnResponseListener(this);
    binding.refresh.setOnRefreshListener(() -> Client.get()
                                                     .load(PG_MESSAGES));
  }

  @Override
  protected void onStop() {
    super.onStop();
    Client.get()
          .removeOnResponseListener(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    Client.get()
          .addOnResponseListener(this);
  }

  @Override
  protected void onPause() {
    super.onPause();
    Client.get()
          .removeOnResponseListener(this);
  }
}