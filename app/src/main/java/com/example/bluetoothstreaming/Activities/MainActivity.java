package com.example.bluetoothstreaming.Activities;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.bluetoothstreaming.R;


/**
 * Allow the user to choose between server, client and diplay video if downloaded
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button downloadButton = findViewById(R.id.serverButton);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serverActivity;
                serverActivity = new Intent(MainActivity.this, ServerSideActivity.class);
                startActivity(serverActivity);
            }

        });

        Button clientButton = findViewById(R.id.clientButton);
        clientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent clientIntent;
                clientIntent = new Intent(MainActivity.this, ClientSideActivity.class);
                startActivity(clientIntent);
            }

        });
        /*Desactivate it's juste for test the media player*/
        Button playButton = findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent clientIntent;
                clientIntent = new Intent(MainActivity.this, PlayVideoActivity.class);
                startActivity(clientIntent);
            }

        });



    }
}

