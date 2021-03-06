package com.example.musicapp;

import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.telecom.Call;
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

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ViewHolder> {

    private List<Music> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private  Context  c;
    List<String> list ;
    List<Integer>  img;
    TinyDB mtinydb;
 ArrayList   favname = new ArrayList<>();
    ArrayList favsource = new ArrayList<>();
    ArrayList favimage = new ArrayList<>();
    // data is passed into the constructor
    ListingAdapter(Context context, List<Music> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.c=context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.listing_row_item, parent, false);
        mtinydb = new TinyDB(c);
        img =new ArrayList<>();
        list = new ArrayList<>();
        list.add("");
        list.add("Add to Favorite");
        list.add("Download");
        img.add(R.drawable.white_shape);
        img.add(R.drawable.favrite);
        img.add(R.drawable.ic_file_download_black_24dp);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Music data = mData.get(position);
        holder.song.setText(data.getTitle());

// Create an ArrayAdapter using the string array and a default spinner layout
// Create an ArrayAdapter using the string array and a default spinner layout

        SpinnerFavAdapter adapter = new SpinnerFavAdapter((ListingScreen) c,list,img);
// Specify the layout to use when the list of choices appears

// Apply the adapter to the spinner
       holder.spinner .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {

              if(position!=0){

              }
              if(position==1){
                  favimage = mtinydb.getListString("favimage");
                  favname = mtinydb.getListString("favname");
                  favsource = mtinydb.getListString("favsource");
                      favimage.add(data.image);
                      mtinydb.putListString("favimage", favimage);
                      favname.add(data.title);
                      mtinydb.putListString("favname", favname);
                      favsource.add(data.track);
                      mtinydb.putListString("favsource", favsource);
                  Toast.makeText(c, "Selected as Favorite", Toast.LENGTH_SHORT).show();
            }
              if (position==2){
                  Download(data.track,data.title);
              }
            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
       holder.frame.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                if(mtinydb.getString("Details").equals("yes")) {
                    if (DetailsScreen.mediaPlayer.isPlaying()) {
                        DetailsScreen.mediaPlayer.stop();
                    }
                }
               if(mtinydb.getString("playfav").equals("yes")){
                   if (PlayFavritesongs.mediaPlayer.isPlaying()) {
                       PlayFavritesongs.mediaPlayer.stop();
                   }
               }
               Intent i = new Intent(c,DetailsScreen.class);
               i.putExtra("data", data);
               i.putExtra("position", position);
               c.startActivity(i);
           }
       });
        holder.spinner.setAdapter(adapter);
        if(position<10) {
            holder.postion.setText("0" + position+".");
        }else{
            holder.postion.setText(""+position+".");
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView song , postion;
        Spinner spinner;
        RelativeLayout frame;
        ViewHolder(View itemView) {
            super(itemView);
            song = itemView.findViewById(R.id.song);
            postion = itemView.findViewById(R.id.postion);
            frame = itemView.findViewById(R.id.frame);
            spinner = (Spinner)itemView.findViewById(R.id.spinner);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Music getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
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
        request.setDestinationInExternalPublicDir("MusicAPP", number);  // Storage directory path
        ((DownloadManager) c.getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request);
        Toast.makeText(c, "Start downloading...", Toast.LENGTH_SHORT).show();// This will start downloading
    }
}