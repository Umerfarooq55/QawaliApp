package com.example.musicapp;


import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FavriteAdapter extends RecyclerView.Adapter<FavriteAdapter.ViewHolder> {


    private LayoutInflater mInflater;
    private  Context  c;
    private ArrayList<String> image;
    private ArrayList<String> source;
    private ArrayList<String> title;
    List<String> list ;
    List<Integer>  img;
    ArrayList<Music> musicdata  = new ArrayList<>();
    TinyDB tinyd;
    ArrayList<Music> mdata;
    // data is passed into the constructor
    FavriteAdapter(Context context, ArrayList<String> image, ArrayList<String> source , ArrayList<String> title ) {
        this.mInflater = LayoutInflater.from(context);

        this.c=context;
        this.image = image;
        this.source = source;
        this.title = title;

    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.fav__down_items, parent, false);
        img =new ArrayList<>();
        tinyd = new TinyDB(c);
        list = new ArrayList<>();
        list.add("");
        list.add("Delete");
        img.add(R.drawable.white_shape);
        img.add(R.drawable.ic_delete_black_24dp);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.song.setText(title.get(position));
        SpinnerFavAdapter adapter = new SpinnerFavAdapter((FavriteSongs) c,list,img);
// Specify the layout to use when the list of choices appears

// Apply the adapter to the spinner
        holder.spinner .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {

                if(position!=0){

                }
                if(position==1){
                    Log.d("pos", String.valueOf(holder.getAdapterPosition()));
                    Log.d("pos", String.valueOf(title.size()));
                  title.remove(holder.getAdapterPosition());
                    source.remove(holder.getAdapterPosition());
                  tinyd.putListString("favname",title);

                  notifyDataSetChanged();
                }

            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        holder.spinner.setAdapter(adapter);
        holder.frame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tinyd.getString("Details").equals("yes")) {
                    if (DetailsScreen.mediaPlayer.isPlaying()) {
                        DetailsScreen.mediaPlayer.stop();
                    }
                }
                if(tinyd.getString("playfav").equals("yes")){
                    if (PlayFavritesongs.mediaPlayer.isPlaying()) {
                        PlayFavritesongs.mediaPlayer.stop();
                    }
                }
                Intent i = new Intent(c,PlayFavritesongs.class);
                i.putExtra("title", title.get(position));
                i.putExtra("source", source.get(position));

                c.startActivity(i);
            }
        });
// Create an ArrayAdapter using the string array and a default spinner layout
// Create an ArrayAdapter using the string array and a default spinner layout
        if(position<10) {
            holder.postion.setText("0" + position+".");
        }else{
            holder.postion.setText(""+position+".");
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return title.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView song , postion;
        Spinner spinner;
        RelativeLayout frame;
        ViewHolder(View itemView) {
            super(itemView);
            song = itemView.findViewById(R.id.song);
            postion = itemView.findViewById(R.id.postion);
            frame = itemView.findViewById(R.id.frame);
            spinner = (Spinner)itemView.findViewById(R.id.spinner);

        }


    }


    private void Download(final String SongSource , String number) {


        File mDirectory = new File(Environment.getExternalStorageDirectory()+"/"+"MusicAPP");

        if(mDirectory.exists()){
            // have the object build the directory structure, if needed.
            mDirectory.mkdirs();
        }
        Uri uri = Uri.parse(mDirectory.getPath());
        File file = new File(String.valueOf(Environment.getExternalStoragePublicDirectory("MusicAPP"+"/"+"Song"+number)));
        if(!file.exists()) {
            startDownload(SongSource,number);
        }else{
            Toast.makeText(c, "File is already downloaded", Toast.LENGTH_SHORT).show();
        }

    }
    private void startDownload(String downloadPath,String number) {
        Uri uri = Uri.parse(downloadPath); // Path where you want to download file.
        Toast.makeText(c, ""+ uri.getLastPathSegment(), Toast.LENGTH_SHORT).show();
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);  // Tell on which network you want to download file.
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);  // This will show notification on top when downloading the file.
        request.setTitle("Downloading a file"); // Title for notification.
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir("MusicAPP", "Song"+number);  // Storage directory path
        ((DownloadManager) c.getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request);
        Toast.makeText(c, "Start downloading...", Toast.LENGTH_SHORT).show();// This will start downloading
    }
}