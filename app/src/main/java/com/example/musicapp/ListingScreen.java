package com.example.musicapp;

import android.content.Intent;
import android.graphics.Movie;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ListingScreen extends AppCompatActivity {

    private ListingAdapter adapter;
    private ArrayList<Music> Data;
    private ArrayList<String> songs;

    @Override
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing);

       Data = new ArrayList<>();
        songs= new ArrayList<>();

        GetTitlesList();
        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rvAnimals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ListingAdapter(this, Data);
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Music data = Data.get(position);

                Intent i = new Intent(ListingScreen.this,DetailsScreen.class);
                i.putExtra("title",data.title);
                i.putExtra("image",data.image);
                i.putExtra("name",data.singer);
                i.putExtra("source",data.track);
                startActivity(i);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        recyclerView.setAdapter(adapter);
    }

    private void GetTitlesList() {
        try {
            JSONObject obj = new JSONObject(readJSONFromAsset());
            Log.d("JSON", String.valueOf(obj));

            JSONArray music = obj.getJSONArray("music");

            for (int i=0;i<music.length();i++){
                JSONObject musicItem = music.getJSONObject(i);
                Data.add(
                        new Music(musicItem.getString("title"),
                                musicItem.getString("source"),
                                musicItem.getString("catimg"),
                                musicItem.getString("catname")
                        )
                );

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String readJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
