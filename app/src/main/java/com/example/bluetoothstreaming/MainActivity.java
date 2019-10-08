package com.example.bluetoothstreaming;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

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


    }
}

