package com.qacademico.qacademico;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.List;

public class MateriaisActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_materiais);

        Bundle bundle = getIntent().getExtras();
        List<Materiais> materiaisList = (List<Materiais>) bundle.getSerializable("Materiais");

        RecyclerView recyclerViewMateriais = (RecyclerView) findViewById(R.id.recycler_materiais);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        AdapterMateriais adapter = new AdapterMateriais(materiaisList, getApplicationContext());
        recyclerViewMateriais.setAdapter(adapter);
        recyclerViewMateriais.setLayoutManager(layout);
    }
}
