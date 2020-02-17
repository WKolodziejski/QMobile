package com.tinf.qmobile.activity;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.App;
import com.tinf.qmobile.R;
import com.tinf.qmobile.data.DataBase;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.journal.Journal_;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;

public class SearchResultsActivity extends ListActivity {
    private List<Queryable> list;
    private Box<Journal> journalBox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        journalBox = DataBase.get().getBoxStore().boxFor(Journal.class);

        Intent intent = getIntent();
        list = new ArrayList<>();

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }

    }

    private void doMySearch(String query) {
        list.addAll(journalBox.query().contains(Journal_.title, query).build().find());
        setListAdapter(new SearchAdapter());
    }

    private class SearchAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            //View v = getLayoutInflater().inflate(R.layout.list_diarios, viewGroup, false);

            return null;
        }
    }

}
