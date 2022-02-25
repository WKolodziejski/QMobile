package com.tinf.qmobile.fragment.matter;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.tinf.qmobile.utility.PermissionsUtils.hasPermission;
import static com.tinf.qmobile.utility.PermissionsUtils.requestPermission;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.MaterialsAdapter;
import com.tinf.qmobile.adapter.OnInteractListener;
import com.tinf.qmobile.adapter.SuppliesAdapter;
import com.tinf.qmobile.databinding.FragmentMaterialBinding;
import com.tinf.qmobile.service.DownloadReceiver;
import com.tinf.qmobile.utility.Design;
import com.tinf.qmobile.widget.divider.MaterialItemDivider;

public class SuppliesFragment extends Fragment {
    private BroadcastReceiver receiver;
    private SuppliesAdapter adapter;
    private ActionMode action;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        receiver = new DownloadReceiver((DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE),
                id -> adapter.notifyItemDownloaded(id));

        getActivity().registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        if (!hasPermission(getContext()))
            requestPermission(getActivity());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_supplies, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new SuppliesAdapter(getContext(), getArguments(), new OnInteractListener() {

            @Override
            public boolean isSelectionMode() {
                return action != null;
            }

            @Override
            public void setSelectionMode(ActionMode.Callback callback) {
                action = getActivity().startActionMode(callback);
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
                MenuInflater menuInflater = getActivity().getMenuInflater();
                menuInflater.inflate(R.menu.materials, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                action = null;
            }
        });

        RecyclerView recycler = view.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        recycler.setItemViewCacheSize(20);
        recycler.setDrawingCacheEnabled(true);
        recycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.addItemDecoration(new MaterialItemDivider(getContext(), 70));
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
        getActivity().unregisterReceiver(receiver);
    }

}
