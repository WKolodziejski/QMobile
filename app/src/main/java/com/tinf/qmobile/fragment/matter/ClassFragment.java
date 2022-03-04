package com.tinf.qmobile.fragment.matter;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kodmap.library.kmrecyclerviewstickyheader.KmHeaderItemDecoration;
import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.ClassAdapter;
import com.tinf.qmobile.adapter.ClassesAdapter;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.widget.divider.ClassItemDivider;

public class ClassFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_classes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Client.get().load(getArguments().getLong("ID"));

        Log.d("CLASS", String.valueOf(getArguments().getLong("ID")));

        ClassesAdapter adapter = new ClassesAdapter(getContext(), getArguments());

        RecyclerView recyclerView = view.findViewById(R.id.recycler_class);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        //recyclerView.addItemDecoration(new ClassItemDivider(getContext(), 52));
        recyclerView.addItemDecoration(new KmHeaderItemDecoration(adapter));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        /*if (getArguments() != null) {
            long id = getArguments().getLong("ID2");
            int p = adapter.highlight(id);

            if (p >= 0) {
                layout.scrollToPosition(p);
                Intent intent = new Intent(getContext(), EventViewActivity.class);
                intent.putExtra("ID", id);
                intent.putExtra("TYPE", CLASS);
                startActivity(intent);
            }
        }*/
    }

}
