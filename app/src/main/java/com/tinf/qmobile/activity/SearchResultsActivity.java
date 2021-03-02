package com.tinf.qmobile.activity;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

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
import com.tinf.qmobile.parser.SearchParser;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;

import static com.tinf.qmobile.model.ViewType.JOURNAL;

public class SearchResultsActivity extends ListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            new SearchParser(list -> setListAdapter(new SearchAdapter(getApplicationContext(), list))).execute(query);
        }
    }

}
