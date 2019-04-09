package com.example.musicapp;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.DeadSystemException;
import android.os.Environment;
import android.os.Handler;
import android.provider.SyncStateContract;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailsScreen extends AppCompatActivity {
    TextView musicTitle;
    ImageView play;
    public static MediaPlayer mediaPlayer;
    SeekBar determinateBar;
    TextView duration_start;
    String url = "";
    Music data;
    ProgressBar loading;
    TextView duration_end;
    Boolean fav = false;
    ImageView favicon;
    private Utilities utils;
    private Handler mHandler = new Handler();
    ;
    Button download;
    ArrayList<String> songsList, titlelist;
    ArrayList<String> favname;
    ArrayList<String> favsource;
    ArrayList<String> favimage;
    TinyDB mtinydb;
    int position, actualposition;
    ImageView forword, backword, shuffle, repeat;
    boolean mMediaPlayerPrepared = false;
    private NotificationManager notifManager;
    private Notification.Builder mNotificationBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_screen);
        Intent i = getIntent();
        if (i != null) {
            data = (Music) i.getSerializableExtra("data");
            position = i.getIntExtra("position", 0);
            actualposition = i.getIntExtra("position", 0);
        }

        songsList = new ArrayList<>();
        titlelist = new ArrayList<>();
        mtinydb = new TinyDB(this);
        favname = new ArrayList<>();
        favsource = new ArrayList<>();
        favimage = new ArrayList<>();
        songsList = mtinydb.getListString("songsList");
        titlelist = mtinydb.getListString("titleList");
        Log.d("songsList", String.valueOf(songsList.size()));
       mtinydb.putString("Details","yes");
        mtinydb.putString("playfav","no");
        utils = new Utilities();
        InitUiComponets();
        ShuffleAndRepeat();
        FavriteIcon();
        Download(data.track, data.title);
            setMediaPlayer(data.track);
        playsong();
        Playclick();
        ShowImage();

        forword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NextSong();

            }
        });
        backword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIsMediaPlayerPrepared(false);
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
                loading.setVisibility(View.VISIBLE);
                if (position > 0) {
                    position = position - 1;
                }
                setMediaPlayer(songsList.get(position));
                musicTitle.setText(titlelist.get(position));
                mediaPlayer.prepareAsync();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        TinyDB mtinydb = new TinyDB(this);
        mtinydb.putString("Details","yes");
        mtinydb.putString("playfav","no");
    }
    private void showNotification() {



            Intent notificationIntent = new Intent(DetailsScreen.this, List.class);

            //With this settings you can open the same Activity without recreate.
            //But you have to put in your AndroidManifest.xml the next line: to your Activity
            //activity android:name=".MainActivity" android:launchMode="singleInstance"

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            //Intent for Play



            //Icon for your notification
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

            notifManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


            PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                    new Intent(getApplicationContext(), DetailsScreen.class),
                    PendingIntent.FLAG_UPDATE_CURRENT);

            // Build the notification object.
            mNotificationBuilder = new Notification.Builder(this)
                    .setContentTitle(data.singer)
                    .setContentText(data.title)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .setOngoing(false)
                    .addAction(R.drawable.ic_play_arrow_black_24dp, "PLAY", null) //you can set a specific icon
                    .addAction(R.drawable.ic_pause_black_24dp, "PAUSE", null) //you can set a specific icon
                    .addAction(R.drawable.ic_close_black_24dp, "CLOSE", null);//you can set a specific icon

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mNotificationBuilder.build());


    }


    private void NextSong() {
        setIsMediaPlayerPrepared(false);
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        loading.setVisibility(View.VISIBLE);
        int size = songsList.size() - 1;
        if (position < size) {
            position = position + 1;
        }

        setMediaPlayer(songsList.get(position));
        musicTitle.setText(titlelist.get(position));
        mediaPlayer.prepareAsync();
    }

    private void playsong() {
        if (!mediaPlayer.isPlaying()) {
            play.setImageResource(R.drawable.ic_pause_black_24dp);
            loading.setVisibility(View.VISIBLE);
            if (!mMediaPlayerPrepared) {
                mediaPlayer.prepareAsync();
            } else {
                mediaPlayer.start();
                loading.setVisibility(View.GONE);
                currentduration();
            }
        } else {
            play.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            mediaPlayer.pause();
        }
    }

    private void InitUiComponets() {
        determinateBar = findViewById(R.id.determinateBar);
        duration_start = findViewById(R.id.duration_start);
        duration_end = findViewById(R.id.duration_end);
        loading = findViewById(R.id.loading);
        play = findViewById(R.id.play);
        forword = findViewById(R.id.forword);
        backword = findViewById(R.id.backword);
        shuffle = findViewById(R.id.shuffle);
        repeat = findViewById(R.id.repeat);
        musicTitle = findViewById(R.id.musict);
        favicon = findViewById(R.id.fav);
        download = findViewById(R.id.download);
        musicTitle.setText(data.getTitle());
    }

    private void Download(final String SongSource, final String number) {
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File mDirectory = new File(Environment.getExternalStorageDirectory() + "/" + "MusicAPP");

                if (mDirectory.exists()) {
                    // have the object build the directory structure, if needed.
                    mDirectory.mkdirs();
                }

                File file = new File(String.valueOf(Environment.getExternalStoragePublicDirectory("MusicAPP" + "/" + "Song" + number)));
                if (!file.exists()) {
                    startDownload(SongSource, data.title);
                } else {
                    Toast.makeText(DetailsScreen.this, "File is already downloaded", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void FavriteIcon() {
        favname = mtinydb.getListString("favname");
        for (int i = 0; i < favname.size(); i++) {
            if (favname.get(i).contains(data.title)) {
                favicon.setImageResource(R.drawable.ic_favorite_black_24dp);
                fav = true;
                break;
            }
        }

        favicon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                favimage = mtinydb.getListString("favimage");
                favname = mtinydb.getListString("favname");
                favsource = mtinydb.getListString("favsource");


                if (!fav) {
                    favimage.add(data.image);
                    mtinydb.putListString("favimage", favimage);
                    favname.add(data.title);
                    mtinydb.putListString("favname", favname);
                    favsource.add(data.track);
                    mtinydb.putListString("favsource", favsource);
                    favicon.setImageResource(R.drawable.ic_favorite_black_24dp);
                    fav = true;
                } else {
                    favimage.remove(data.image);
                    mtinydb.putListString("favimage", favimage);
                    favname.remove(data.title);
                    mtinydb.putListString("favname", favname);
                    favsource.remove(data.image);
                    mtinydb.putListString("favsource", favsource);
                    favicon.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    fav = false;
                }
            }
        });
    }

    private void ShuffleAndRepeat() {
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isLooping()) {
                    shuffle.setImageResource(R.drawable.ic_shuffle_black_24dp);
                    mediaPlayer.setLooping(false);
                } else {
                    shuffle.setImageResource(R.drawable.ic_shuffle_yellow_24dp);
                    mediaPlayer.setLooping(true);

                }
            }
        });
        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isLooping()) {
                    repeat.setImageResource(R.drawable.ic_repeat_black_24dp);
                    mediaPlayer.setLooping(false);
                } else {
                    repeat.setImageResource(R.drawable.ic_repeat_yellow_24dp);
                    mediaPlayer.setLooping(true);

                }

            }
        });
    }



    private void setMediaPlayer(String track) {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        Log.d("url", url);
        try {
            mediaPlayer.setDataSource(track);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Download(track, data.title);
        OnPrepared();
    }

    private void ShowImage() {
        CircleImageView image = (CircleImageView) findViewById(R.id.musicimage);
        Glide.with(this).load(data.image).into(image);
    }

    private void OnPrepared() {
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                setIsMediaPlayerPrepared(true);
                showNotification();
                mediaPlayer.start();
                loading.setVisibility(View.GONE);
                play.setImageResource(R.drawable.ic_pause_black_24dp);
                updateProgressBar();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        NextSong();
                    }
                });
                determinateBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        mHandler.removeCallbacks(mUpdateTimeTask);
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mHandler.removeCallbacks(mUpdateTimeTask);
                        int totalDuration = mediaPlayer.getDuration();
                        loading.setVisibility(View.VISIBLE);
                        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

                        // forward or backward to certain seconds
                        mediaPlayer.seekTo(seekBar.getProgress());
                        int progress = (int) (utils.getProgressPercentage(currentPosition, totalDuration));
                        //Log.d("Progress", ""+progress);
                        mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                            @Override
                            public void onSeekComplete(MediaPlayer mp) {
                                loading.setVisibility(View.GONE);
                                updateProgressBar();
                            }
                        });
                        // update timer progress again
//                        updateProgressBar();
                    }
                });

            }

        });
    }
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private void Playclick() {
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playsong();
            }
        });
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if (mediaPlayer.isPlaying()) {

                long totalDuration = mediaPlayer.getDuration();
                long currentDuration = mediaPlayer.getCurrentPosition();

                // Displaying Total Duration time
                duration_end.setText("" + utils.milliSecondsToTimer(totalDuration));
                // Displaying time completed playing
                duration_start.setText("" + utils.milliSecondsToTimer(currentDuration));

                // Updating progress bar
                int progress = (int) (utils.getProgressPercentage(currentDuration, totalDuration));
                //Log.d("Progress", ""+progress);
                Log.d("progress", String.valueOf(progress));
                determinateBar.setMax(mediaPlayer.getDuration());
                determinateBar.setProgress(mediaPlayer.getCurrentPosition());


                // Running this thread after 100 milliseconds
                mHandler.postDelayed(this, 100);
            }
        }
    };
    private void currentduration() {
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity

                currentduration();
            }
        }, 500);
    }

    public void setIsMediaPlayerPrepared(boolean prepared) {
        mMediaPlayerPrepared = prepared;
    }

    private String getTimeString(long millis) {
        StringBuffer buf = new StringBuffer();

        int hours = (int) (millis / (1000 * 60 * 60));
        int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);

        buf
                .append(String.format("%02d", minutes))
                .append(":")
                .append(String.format("%02d", seconds));

        return buf.toString();
    }

    private void startDownload(String downloadPath, String number) {
        Uri uri = Uri.parse(downloadPath); // Path where you want to download file.
        Toast.makeText(this, "" + uri.getLastPathSegment(), Toast.LENGTH_SHORT).show();
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);  // Tell on which network you want to download file.
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);  // This will show notification on top when downloading the file.
        request.setTitle("Downloading a file"); // Title for notification.
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir("MusicAPP", number);  // Storage directory path
        ((DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request);
        Toast.makeText(DetailsScreen.this, "Start downloading...", Toast.LENGTH_SHORT).show();// This will start downloading
    }


}
