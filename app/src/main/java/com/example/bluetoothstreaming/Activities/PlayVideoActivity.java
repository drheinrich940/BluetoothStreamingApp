package com.example.bluetoothstreaming.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.bluetoothstreaming.R;

import java.io.File;
import java.util.Date;

/**
 * Performs the video display to the user
 */
public class PlayVideoActivity extends AppCompatActivity {


    private String videoPath = Environment.getExternalStorageState() + "/" + "androiddeft/" + "512.mp4";

    private static ProgressDialog progressDialog;
    VideoView videoView;

    public final static String FILTER = "PlayVideoActivity.FILTER";

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
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "androiddeft/" +"z.mp4");
        File moreRecentFile = null;
        Date moreRecentDate = null;
        return file;
        /*for (int i = 0; i < tmpfiles.length; i++) {
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
        }*/
    }

    private void PlayVideo() {
        try {

            getWindow().setFormat(PixelFormat.TRANSLUCENT);
            MediaController mediaController = new MediaController(PlayVideoActivity.this);
            mediaController.setAnchorView(videoView);

            String vidAddress = Environment.getExternalStorageDirectory() + File.separator + "androiddeft/";

            //File file = findMostRecentFiles(vidAddress);
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "androiddeft/" +"z.mp4");
            boolean fileExist = file.exists();
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