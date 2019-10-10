package com.example.bluetoothstreaming.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.bluetoothstreaming.Services.ClientReceiver;
import com.example.bluetoothstreaming.Services.videoInterface;
import com.example.bluetoothstreaming.Services.ServerService;
import com.example.bluetoothstreaming.Utils.CheckForSDCard;
import com.example.bluetoothstreaming.R;
import com.example.bluetoothstreaming.Threading.BluetoothStreamingThreads;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Server Side Activity is the main activity for the server mode
 */
public class ServerSideActivity extends AppCompatActivity implements videoInterface {
    private static final String TAG = "ServerSideActivity";
    private static final int WRITE_REQUEST_CODE = 300;
    private String url;
    private boolean bounded = false;
    public static final String OUTPUT_FILE_NAME = "projectVideo.mp4";
    public static final String FILTER = "ServerSideActivity.FILTER";
    private EditText editTextUrl;
    private ServerService serverService;

    BluetoothStreamingThreads bluetoothStreamingThreads;
    private ProgressBar progressBar;
    private TextView downloadTextView;
    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_side);

        /*for change the permission */
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        editTextUrl = findViewById(R.id.editTextUrl);

        progressBar = findViewById(R.id.downloadProgressBar);
        //downloadTextView = findViewById(R.id.downloadStatus);

        //progressBar.setVisibility(View.INVISIBLE);
//        downloadTextView.setVisibility(View.INVISIBLE);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Button downloadButton = findViewById(R.id.downloadButton);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Check if SD card is present or not
                if (CheckForSDCard.isSDCardPresent()) {

                    url = editTextUrl.getText().toString();
                    new DownloadFile().execute(url);

                } else {
                    Toast.makeText(getApplicationContext(), "SD Card not found", Toast.LENGTH_LONG).show();

                }
            }

        });

        Intent bluetoothServiceIntent = new Intent(this, ServerService.class);
        bindService(bluetoothServiceIntent, mConnection, Context.BIND_AUTO_CREATE);

        //broadcast receiver
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        BroadcastReceiver broadcastReceiver = new ClientReceiver(this);
        IntentFilter intentFilter = new IntentFilter(FILTER);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    /**
     * Launches the service to start streaming the video to client
     * @param view
     */
    public void STARTTHISSHITUP(View view){
        Intent bluetoothServiceIntent = new Intent(this, ServerService.class);
        bindService(bluetoothServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
        Intent startServiceIntent = new Intent(this, ServerService.class);
        startServiceIntent.putExtra(ServerService.START_ROUTINE_TAG, true);
        startService(startServiceIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, ServerSideActivity.this);
    }

    /**
     * Performs the retrieval of any file from the internet
     */
    private class DownloadFile extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;
        private String fileName;
        private String folder;
        private boolean isDownloaded;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(ServerSideActivity.this);
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }


        /**
         * Gets the file from direct url download with a background thread.
         * If URL is empty we dowload the default file
         */
        @Override
        protected String doInBackground(String...  f_url) {
            int count;
            try {
                URL url;
                if(!f_url[0].equals("")){
                    url = new URL(f_url[0]);
                    fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1, f_url[0].length());
                }else{
                    url = new URL("https://ia800201.us.archive.org/22/items/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4");
                    fileName = "z.mp4";
                }
                URLConnection connection = url.openConnection();
                connection.connect();
                int lengthOfFile = connection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                //String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                folder = Environment.getExternalStorageDirectory() + "/" + "androiddef/";
                File directory = new File(folder);

                if (!directory.exists()) {
                    directory.mkdirs();
                }
                OutputStream output = new FileOutputStream(folder + fileName);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                    Log.d(TAG, "Progress: " + (int) ((total * 100) / lengthOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
                return "Downloaded at: " + folder + fileName;
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            return "failed to download or store the file";
        }

        /**
         * To update the progress bar with new percentile
         */
        protected void onProgressUpdate(String... progress) {
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String message) {
            this.progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            ServerService.LocalBinder binder = (ServerService.LocalBinder) service;
            serverService = binder.getService();
            bounded = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bounded = false;
        }
    };

    @Override
    public void handleTextReception(String textReceived) {
        if (textReceived != null)
            Toast.makeText(this, textReceived, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void playVideo(String textReceived) {

    }

    @Override
    public void updateProgressBar(int progress) {
        if (progress >= 96) {
            this.progressBar.setVisibility(View.INVISIBLE);
            this.downloadTextView.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            downloadTextView.setVisibility(View.VISIBLE);
            this.progressBar.setProgress(progress);
        }
    }



}