package com.tinf.qmobile.fragment.matter;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.content.Context.RECEIVER_EXPORTED;
import static com.tinf.qmobile.network.OnResponse.PG_MATERIALS;
import static com.tinf.qmobile.utility.PermissionsUtils.hasPermission;
import static com.tinf.qmobile.utility.PermissionsUtils.requestPermission;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.App;
import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.OnMaterialInteractListener;
import com.tinf.qmobile.adapter.SuppliesAdapter;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.service.DownloadReceiver;
import com.tinf.qmobile.widget.divider.CustomItemDivider;

public class SuppliesFragment extends Fragment {
  private BroadcastReceiver receiver;
  private SuppliesAdapter adapter;
  private ActionMode action;
  private ActivityResultLauncher<String[]> launcher;

  @SuppressLint("UnspecifiedRegisterReceiverFlag")
  @Override
  public void onCreate(
      @Nullable
      Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    launcher = registerForActivityResult(
        new ActivityResultContracts.RequestMultiplePermissions(),
        results -> {
          if (!results.isEmpty()) {
            Client.get().load(PG_MATERIALS);
          } else {
            Toast.makeText(App.getContext(), getResources()
                .getString(R.string.text_permission_denied), Toast.LENGTH_LONG).show();
          }
        });

    receiver =
        new DownloadReceiver((DownloadManager) requireActivity().getSystemService(DOWNLOAD_SERVICE),
                             id -> adapter.notifyItemDownloaded(id));

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      requireActivity().registerReceiver(receiver,
                                         new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                                         RECEIVER_EXPORTED);
    } else {
      requireActivity().registerReceiver(receiver,
                                         new IntentFilter(
                                             DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    if (!hasPermission(getContext()) && launcher != null) {
      requestPermission(requireActivity(), launcher);
    }
  }

  @Override
  public View onCreateView(
      @NonNull
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_supplies, container, false);
  }

  @Override
  public void onViewCreated(
      @NonNull
      View view,
      @Nullable
      @org.jetbrains.annotations.Nullable
      Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    adapter = new SuppliesAdapter(getContext(), getArguments(), new OnMaterialInteractListener() {

      @Override
      public boolean isSelectionMode() {
        return action != null;
      }

      @Override
      public void setSelectionMode(ActionMode.Callback callback) {
        action = requireActivity().startActionMode(callback);
      }

      @Override
      public void onSelectedCount(int size) {
        if (size > 0) {
          action.setTitle(String.valueOf(size));
        } else {
          action.finish();
          action = null;
        }
      }

      @Override
      public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        MenuInflater menuInflater = requireActivity().getMenuInflater();
        menuInflater.inflate(R.menu.materials, menu);
        return true;
      }

      @Override
      public void onDestroyActionMode(ActionMode actionMode) {
        action = null;
      }
    });

    RecyclerView recycler = view.findViewById(R.id.recycler);
    recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
    recycler.addItemDecoration(new CustomItemDivider(requireContext()));
    recycler.setItemAnimator(null);
    recycler.setAdapter(adapter);
  }

  @Override
  public void onDetach() {
    super.onDetach();
    if (action != null) {
      action.finish();
      action = null;
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    launcher = null;
    requireActivity().unregisterReceiver(receiver);
  }

}
