package com.tinf.qmobile.activity;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.JournalAdapter;
import com.tinf.qmobile.adapter.SearchAdapter;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.holder.journal.JournalBaseViewHolder;
import com.tinf.qmobile.holder.journal.JournalViewHolder;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.calendar.EventSimple;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.journal.Journal_;
import com.tinf.qmobile.model.material.Material;
import com.tinf.qmobile.model.matter.Clazz;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Period;
import com.tinf.qmobile.model.matter.Schedule;
import com.tinf.qmobile.model.message.Attachment;
import com.tinf.qmobile.model.message.Message;
import com.tinf.qmobile.model.message.Sender;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.parser.SearchParser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.Box;

import static com.tinf.qmobile.model.ViewType.JOURNAL;

public class SearchResultsActivity extends AppCompatActivity {
    @BindView(R.id.recycler_search) RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        String query = getIntent().getStringExtra("QUERY");

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(new SearchAdapter(this, query));
    }

}
