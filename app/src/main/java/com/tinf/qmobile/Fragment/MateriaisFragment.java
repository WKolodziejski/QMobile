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
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.tinf.qmobile.Activity.MainActivity;
import com.tinf.qmobile.Adapter.Materiais.MateriaisListAdapter;
import com.tinf.qmobile.Class.Materiais.MateriaisList;
import com.tinf.qmobile.Interfaces.OnUpdate;
import com.tinf.qmobile.Interfaces.OnMateriaisLoad;
import com.tinf.qmobile.Network.Client;
import com.tinf.qmobile.R;
import com.tinf.qmobile.Utilities.User;
import java.io.File;
import java.util.List;
import java.util.Objects;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.tinf.qmobile.BuildConfig.APPLICATION_ID;
import static com.tinf.qmobile.Network.Client.PG_MATERIAIS;

public class MateriaisFragment extends Fragment implements OnMateriaisLoad, OnUpdate {
    private RecyclerView recyclerViewMateriais;
    private List<MateriaisList> materiaisList;
    private String name, mime;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        //listFilesForFolder();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_materiais, container, false);
    }

    private void showMateriais(View view) {

        ((MainActivity) getActivity()).setTitle(String.valueOf(Client.getYear()));

        recyclerViewMateriais = (RecyclerView) view.findViewById(R.id.recycler_materiais);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewMateriais.getContext(),
                LinearLayoutManager.VERTICAL);

        MateriaisListAdapter adapter = new MateriaisListAdapter(materiaisList, getActivity());

        recyclerViewMateriais.setAdapter(adapter);
        recyclerViewMateriais.setLayoutManager(layout);
        recyclerViewMateriais.addItemDecoration(dividerItemDecoration);

        recyclerViewMateriais.addOnScrollListener(new RecyclerView.OnScrollListener(){
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

        adapter.setOnDowloadListener(material -> {
            if (checkPermission()) {
                DownloadManager manager = (DownloadManager) getContext().getSystemService(DOWNLOAD_SERVICE);
                name = material.getNomeConteudo() + material.getExtension();
                long lastDownloadL = manager.enqueue(Client.get().download(material));
                mime = manager.getMimeTypeForDownloadedFile(lastDownloadL);

            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
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
        ((MainActivity) Objects.requireNonNull(getActivity())).setOnUpdateListener(this);
        Client.get().setOnMateriaisLoadListener(this);
        if (materiaisList == null) {
            Client.get().load(PG_MATERIAIS);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) Objects.requireNonNull(getActivity())).setOnUpdateListener(this);
        Client.get().setOnMateriaisLoadListener(this);
        if (materiaisList == null) {
            Client.get().load(PG_MATERIAIS);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) Objects.requireNonNull(getActivity())).setOnUpdateListener(null);
        Client.get().setOnMateriaisLoadListener(null);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((MainActivity) Objects.requireNonNull(getActivity())).setOnUpdateListener(null);
        Client.get().setOnMateriaisLoadListener(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(onComplete);
    }

    @Override
    public void onUpdate(int pg) {
        Client.get().load(PG_MATERIAIS);
    }

    @Override
    public void requestScroll() {
        if (recyclerViewMateriais != null) {
            recyclerViewMateriais.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onMateriaisLoad(List<MateriaisList> list) {
        if (!list.isEmpty()) {
            materiaisList = list;
            showMateriais(getView());
        } else {
            getLayoutInflater().inflate(R.layout.layout_empty,  null);
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
                    + "/QMobile/2018/" + name);

            Uri uri = FileProvider.getUriForFile(getContext(), APPLICATION_ID, file);

            Log.d("URI", uri.toString());

            i.setDataAndType(uri, mime);

            try {
                startActivity(i);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getContext(), "No handler for this type of file.", Toast.LENGTH_LONG).show();
            }
        }
    };

}
