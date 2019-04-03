package com.example.musicapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailsScreen extends AppCompatActivity {
      TextView musicTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_screen);

        String img = getIntent().getStringExtra("image");
        String title = getIntent().getStringExtra("title");
        musicTitle  =   findViewById(R.id.musict);
        Log.d("img",img);
        musicTitle.setText(title);
        CircleImageView image = (CircleImageView) findViewById(R.id.musicimage);
        Glide.with(this).load(img).into(image);
    }
}
