package com.example.bluetoothstreaming.Activities;


import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.bluetoothstreaming.R;
import com.example.bluetoothstreaming.Services.ClientService;
import com.example.bluetoothstreaming.Services.ClientReceiver;
import com.example.bluetoothstreaming.Services.videoInterface;

public class BondingActivity extends AppCompatActivity implements videoInterface {

    VideoView videoView;
    public final static String FILTER = "BondingActivity.FILTER";
    private LocalBroadcastManager localBroadcastManager;
    private ProgressBar progressBar;
    private TextView downloadTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("OnCreate", "BOndingAct");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_server_pairing);
        videoView = findViewById(R.id.videoView);
        progressBar = findViewById(R.id.downloadProgressBar);
        downloadTextView = findViewById(R.id.downloadStatusText);

        progressBar.setVisibility(View.VISIBLE);
        downloadTextView.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.INVISIBLE);

        BluetoothDevice bd = getIntent().getExtras().getParcelable("BLUETOOTH_SELECTED_DEVICE");
        if (bd == null) {
            Log.e("transmission", "FAIL");
        } else {
            Log.e("transmission", "OK");

        }
        Intent startBluetoothCLientIntent = new Intent(this, ClientService.class);

        startBluetoothCLientIntent.putExtra(ClientService.TAG_INTENT, bd);
        startService(startBluetoothCLientIntent);


        //broadcast receiver
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        BroadcastReceiver broadcastReceiver = new ClientReceiver(this);
        IntentFilter intentFilter = new IntentFilter(FILTER);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);


    }

    @Override
    public void handleTextReception(String textReceived) {
        if (textReceived != null)
            Toast.makeText(this, textReceived, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void playVideo(String textReceived) {
        videoView.setVisibility(View.VISIBLE);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        Uri uri = Uri.parse(textReceived);

        videoView.setVideoURI(uri);

        videoView.start();
    }

    @Override
    public void updateProgressBar(int progress) {
        if (progress >= 96) {
            this.progressBar.setVisibility(View.INVISIBLE);
            this.downloadTextView.setVisibility(View.INVISIBLE);
        } else {
            this.progressBar.setProgress(progress);
        }
    }


}
