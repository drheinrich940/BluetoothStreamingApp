package com.example.bluetoothstreaming;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PlayVideoActivity extends AppCompatActivity {


    private String videoPath = Environment.getExternalStorageState() + "/" + "androiddeft/" + "512.mp4";

    private static ProgressDialog progressDialog;
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        videoView = (VideoView) findViewById(R.id.video);


        progressDialog = ProgressDialog.show(PlayVideoActivity.this, "", "Buffering video...", true);
        progressDialog.setCancelable(true);


        PlayVideo();

    }

    public File findMostRecentFiles(String directoryPath) {
        File directory = new File(directoryPath);
        File[] tmpfiles = directory.listFiles();

        File moreRecentFile = null;
        Date moreRecentDate = null;

        for (int i = 0; i < tmpfiles.length; i++) {
            File fileTmp = tmpfiles[i];
            Date dateTmp = new Date(fileTmp.lastModified());
            if (moreRecentDate == null) {
                moreRecentFile = fileTmp;
                moreRecentDate = dateTmp;
            } else {
                if (dateTmp.after(moreRecentDate)) {
                    moreRecentFile = fileTmp;
                    moreRecentDate = dateTmp;
                }
            }
        }
        return moreRecentFile;
    }

    private void PlayVideo() {
        try {

            getWindow().setFormat(PixelFormat.TRANSLUCENT);
            MediaController mediaController = new MediaController(PlayVideoActivity.this);
            mediaController.setAnchorView(videoView);

            String vidAddress = Environment.getExternalStorageDirectory() + "/" + "androiddeft/";

            File file = findMostRecentFiles(vidAddress);
            Uri video = Uri.fromFile(file);

            videoView.setMediaController(mediaController);
            videoView.setVideoURI(video);
            videoView.requestFocus();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                public void onPrepared(MediaPlayer mp) {
                    progressDialog.dismiss();
                    videoView.start();
                }
            });


        } catch (Exception e) {
            progressDialog.dismiss();
            System.out.println("Video Play Error :" + e.toString());
            finish();
        }
    }
}