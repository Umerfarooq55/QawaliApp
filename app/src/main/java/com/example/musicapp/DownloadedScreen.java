package com.example.musicapp;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class DownloadedScreen extends AppCompatActivity {
    private Downloadadapter adapter;
    private ArrayList<String> list;
    private TinyDB mtinydb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloaded_screen);
        String path = Environment.getExternalStoragePublicDirectory("MusicApp/").toString();
        list = new ArrayList<>();
        mtinydb = new TinyDB(this);
        Log.d("Files", "Path: " + path);
        mtinydb.putString("Down","yes");

        File directory = new File(path);
        if(directory.exists()) {
            File[] files = directory.listFiles();
            for (int i = 0; i < files.length; i++) {
                list.add(files[i].getName());
            }
        }
        RecyclerView recyclerView = findViewById(R.id.rvAnimals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Downloadadapter(this, list);
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(adapter);
    }
}
