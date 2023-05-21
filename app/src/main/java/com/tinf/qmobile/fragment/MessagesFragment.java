package com.tinf.qmobile.fragment;

import static com.tinf.qmobile.model.ViewType.MESSAGE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.App;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.adapter.MessagesAdapter;
import com.tinf.qmobile.databinding.FragmentMessagesBinding;
import com.tinf.qmobile.network.OnResponse;
import com.tinf.qmobile.network.message.Messenger;
import com.tinf.qmobile.widget.divider.MessageItemDivider;

public class MessagesFragment extends Fragment implements OnResponse {
  private FragmentMessagesBinding binding;

  ActivityResultLauncher<Intent> launcher = registerForActivityResult(
      new ActivityResultContracts.StartActivityForResult(), result -> getActivity().finish());

  @Nullable
  @Override
  public View onCreateView(
      @NonNull
      LayoutInflater inflater,
      @Nullable
      ViewGroup container,
      @Nullable
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_messages, container, false);
    binding = FragmentMessagesBinding.bind(view);
    return view;
  }

  @Override
  public void onViewCreated(
      @NonNull
      View view,
      @Nullable
      Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

        /*prv.setOnClickListener(v -> messenger.loadPage(21));
        nxt.setOnClickListener(v -> {
            DataBase.get().getBoxStore().boxFor(Message.class).removeAll();
        });*/

    Messenger messenger = new Messenger(getContext(), this);
    LinearLayoutManager layout = new LinearLayoutManager(getContext());
    RecyclerView.Adapter adapter = new MessagesAdapter(getContext(), messenger);

    binding.recycler.setItemViewCacheSize(20);
    binding.recycler.setDrawingCacheEnabled(true);
    binding.recycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    binding.recycler.setLayoutManager(layout);
    binding.recycler.setAdapter(adapter);
    binding.recycler.addItemDecoration(new MessageItemDivider(getContext()));
    binding.recycler.setItemAnimator(null);
    binding.recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(
          @NonNull
          RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int p = (recyclerView.getChildCount() == 0) ?
                0 :
                recyclerView.getChildAt(0).getTop();
        binding.refresh.setEnabled(p == 0);

        int j = layout.findLastCompletelyVisibleItemPosition();

        if (j == adapter.getItemCount() - 1)
          messenger.loadPage((j / 20) + 2);
      }
    });

    binding.refresh.setOnRefreshListener(messenger::loadFirstPage);

    if (getArguments() != null && getArguments().containsKey("ID2")) {
      long id = getArguments().getLong("ID2");

      Log.d("MESSAGE", getArguments().toString());
            /*int p = adapter.highlight(id);

            if (p >= 0) {
                layout.scrollToPosition(p);
                messenger.openMessage(p);

                Intent intent = new Intent(getContext(), EventViewActivity.class);
                intent.putExtra("TYPE", MESSAGE);
                intent.putExtra("ID", id);
                launcher.launch(intent);
            }*/

      Intent intent = new Intent(getContext(), EventViewActivity.class);
      intent.putExtra("TYPE", MESSAGE);
      intent.putExtra("ID", id);
      launcher.launch(intent);
    }
  }

  @Override
  public void onStart(int pg) {
    binding.refresh.setRefreshing(true);
  }

  @Override
  public void onFinish(int pg, int year, int period) {
    binding.refresh.setRefreshing(false);
  }

  @Override
  public void onError(int pg, String error) {
    binding.refresh.setRefreshing(false);
    ContextCompat.getMainExecutor(App.getContext()).execute(() ->
                                                                Toast.makeText(App.getContext(),
                                                                               error,
                                                                               Toast.LENGTH_LONG)
                                                                     .show());
  }

  @Override
  public void onAccessDenied(int pg, String message) {
    binding.refresh.setRefreshing(false);
    ContextCompat.getMainExecutor(App.getContext()).execute(() ->
                                                                Toast.makeText(App.getContext(),
                                                                               App.getContext()
                                                                                  .getString(
                                                                                      R.string.dialog_access_denied),
                                                                               Toast.LENGTH_LONG)
                                                                     .show());
  }

}
