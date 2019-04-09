package com.example.musicapp;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FavriteSongs extends AppCompatActivity {
    private FavriteAdapter adapter;
    private ArrayList<Music> Data;
    private ArrayList<String> favname;
    private ArrayList<String> favsource;
    private ArrayList<String> favimage;
    private TinyDB mtinydb;
    Music data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favrite_songs);
        Intent i = getIntent();

        mtinydb = new TinyDB(this);
        mtinydb.putString("Down","no");
        favimage = mtinydb.getListString("favimage");
        favname = mtinydb.getListString("favname");
        favsource = mtinydb.getListString("favsource");
        RecyclerView recyclerView = findViewById(R.id.rvAnimals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FavriteAdapter(this, favimage,favsource,favname);
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(adapter);
    }
}
