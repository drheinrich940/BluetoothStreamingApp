package com.example.bluetoothstreaming;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


/**
 * TODO : Create new activities and design main one
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



    }
}

