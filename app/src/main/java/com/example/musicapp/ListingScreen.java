package com.example.musicapp;

import android.content.Intent;
import android.graphics.Movie;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
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
    private ArrayList<String> titltes;
    private TinyDB mtinydb;
    private DrawerLayout drawerLayout;

    @Override
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing);
        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
       Data = new ArrayList<>();
        songs= new ArrayList<>();
        titltes= new ArrayList<>();
        mtinydb = new TinyDB(this);
        GetTitlesList();
        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rvAnimals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ListingAdapter(this, Data);
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(adapter);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView fav = (TextView) headerView.findViewById(R.id.fav);
        TextView download = (TextView) headerView.findViewById(R.id.download);
        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ListingScreen.this,FavriteSongs.class);

                startActivity(i);
            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ListingScreen.this,DownloadedScreen.class);
                startActivity(i);
            }
        });


    }

    private void GetTitlesList() {
        ArrayList<String> songsList = new ArrayList<>();
        ArrayList<String> titleList = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(readJSONFromAsset());
            Log.d("JSON", String.valueOf(obj));
            JSONArray music = obj.getJSONArray("music");

            for (int i=0;i<music.length();i++){
                JSONObject musicItem = music.getJSONObject(i);


                songsList.add(musicItem.getString("source"));
                titleList.add(musicItem.getString("title"));
                mtinydb.putListString("songsList",songsList);
                mtinydb.putListString("titleList",titleList);
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
