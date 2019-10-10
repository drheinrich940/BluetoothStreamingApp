package com.example.bluetoothstreaming.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ClientReceiver extends BroadcastReceiver {
    private videoInterface videoInterface;

    public ClientReceiver(videoInterface videoInterface) {
        super();
        this.videoInterface = videoInterface;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getStringExtra("PLAY") != null)
            this.videoInterface.playVideo(intent.getStringExtra("PLAY"));
        else if (intent.getIntExtra("UPDATE", -1) > 0) {
            this.videoInterface.updateProgressBar(intent.getIntExtra("UPDATE", -1));
        } else
            this.videoInterface.handleTextReception(intent.getStringExtra(ClientService.SEND_MESSAGE_TAG));
    }
}
