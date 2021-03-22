package com.tinf.qmobile.fragment.matter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.JournalAdapter;

public class GradesFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_grades, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        JournalAdapter adapter = new JournalAdapter(getContext(), getArguments(), position -> {});
        LinearLayoutManager layout = new LinearLayoutManager(getContext());

        RecyclerView recyclerView = view.findViewById(R.id.recycler_materia);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(layout);
        recyclerView.setAdapter(adapter);

        /*if (getArguments() != null) {
            long id = getArguments().getLong("ID2");
            int p = adapter.highlight(id);

            if (p >= 0) {
                layout.scrollToPosition(p);
                Intent intent = new Intent(getContext(), EventViewActivity.class);
                intent.putExtra("ID", id);
                intent.putExtra("TYPE", JOURNAL);
                startActivity(intent);
            }
        }*/
    }

}
