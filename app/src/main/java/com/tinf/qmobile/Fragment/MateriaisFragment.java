package com.tinf.qmobile.Fragment;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.tinf.qmobile.Activity.MainActivity;
import com.tinf.qmobile.Adapter.Materiais.MateriaisListAdapter;
import com.tinf.qmobile.Class.Materiais.Materiais;
import com.tinf.qmobile.Class.Materiais.MateriaisList;
import com.tinf.qmobile.Interfaces.OnUpdate;
import com.tinf.qmobile.Network.OnMateriaisLoad;
import com.tinf.qmobile.Network.Client;
import com.tinf.qmobile.R;
import com.tinf.qmobile.Utilities.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import static android.content.Context.DOWNLOAD_SERVICE;
import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.tinf.qmobile.BuildConfig.APPLICATION_ID;
import static com.tinf.qmobile.Network.OnResponse.PG_MATERIAIS;
import static com.tinf.qmobile.Utilities.User.REGISTRATION;

public class MateriaisFragment extends Fragment implements OnMateriaisLoad, OnUpdate {
    private static String TAG = "MateriaisFragment";
    private List<MateriaisList> materiaisList;
    private MateriaisListAdapter adapter;
    private String name, mime;
    private boolean isLoading;
    @BindView(R.id.recycler_materiais) RecyclerView recyclerView;
    @BindView(R.id.materiais_empty) View empty;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        OnDownloadListener onDownloadListener = material -> {
            if (checkPermission()) {
                DownloadManager manager = (DownloadManager) getContext().getSystemService(DOWNLOAD_SERVICE);
                name = material.getNomeConteudo() + material.getExtension();
                long lastDownloadL = manager.enqueue(Client.get().download(material));
                mime = manager.getMimeTypeForDownloadedFile(lastDownloadL);
                Toast.makeText(getContext(), getResources().getString(R.string.text_no_handler), Toast.LENGTH_LONG).show();

            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
        };

        adapter = new MateriaisListAdapter(getContext(), new ArrayList<>(), onDownloadListener);
        //adapter.setHasStableIds(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_materiais, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.v(TAG, "View created");

        view.post(() -> {

            RecyclerView.LayoutManager layout = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
            DividerItemDecoration decoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);

            recyclerView.setLayoutManager(layout);
            recyclerView.addItemDecoration(decoration);
            recyclerView.setAdapter(adapter);

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    int p = (recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                    ((MainActivity) getActivity()).refreshLayout.setEnabled(p == 0);
                }

                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }
            });
        });
    }

    private void listFilesForFolder() {
        File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/QMobile/2018");

        if (!path.exists()) {
            path.mkdir();
        }

        Log.d("Files", "Path: " + path);
        File[] files = path.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (File file : files) {
            Log.d("Files", "FileName:" + file.getName());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).addOnUpdateListener(this);
        Client.get().setOnMateriaisLoadListener(this);
        if (materiaisList == null && !isLoading) {
            Client.get().load(PG_MATERIAIS);
            isLoading = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).addOnUpdateListener(this);
        Client.get().setOnMateriaisLoadListener(this);
        if (materiaisList == null && !isLoading) {
            Client.get().load(PG_MATERIAIS);
            isLoading = true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).removeOnUpdateListener(this);
        Client.get().setOnMateriaisLoadListener(null);
        isLoading = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        ((MainActivity) getActivity()).removeOnUpdateListener(this);
        Client.get().setOnMateriaisLoadListener(null);
        isLoading = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(onComplete);
        isLoading = false;
    }

    @Override
    public void onUpdate(int pg) {
        if (pg == UPDATE_REQUEST) {
            isLoading = true;
            Client.get().load(PG_MATERIAIS);
        }
    }

    @Override
    public void onScrollRequest() {
        if (recyclerView != null) {
            recyclerView.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onMateriaisLoad(List<MateriaisList> list) {
        isLoading = false;
        if (!list.isEmpty()) {
            materiaisList = list;
            adapter.update(materiaisList);
            recyclerView.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        }
    }

    private boolean checkPermission() {
        int write = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if (write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            Intent i = new Intent(ACTION_VIEW);
            i.setFlags(FLAG_ACTIVITY_NEW_TASK);
            i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    + "/QMobile/" + User.getCredential(REGISTRATION) + "/2018/" + name);

            Uri uri = FileProvider.getUriForFile(getContext(), APPLICATION_ID, file);

            i.setDataAndType(uri, mime);

            try {
                startActivity(i);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getContext(), getResources().getString(R.string.text_no_handler), Toast.LENGTH_LONG).show();
            }
        }
    };

    public interface OnDownloadListener {
        void onDownload(Materiais material);
    }

}
