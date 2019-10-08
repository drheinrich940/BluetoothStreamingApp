package com.example.bluetoothstreaming;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;


public class BluetoothStreamingService {
    private static final String TAG = "BluetoothStreamingService";
    private static final String appname = "MyVideoStreamer";
    private static final UUID UUID_INSECURE = UUID.fromString("");

    private final BluetoothAdapter bleAdapter;
    Context context;
    private AcceptThread acceptThread_insecure;


    public BluetoothStreamingService(Context context) {
        this.context = context;
        bleAdapter = BluetoothAdapter.getDefaultAdapter();
        start();
    }

    private class AcceptThread extends Thread{
        private final BluetoothServerSocket serverSocket;
        public AcceptThread(){
            BluetoothServerSocket tmp = null;
            try{
                tmp = bleAdapter.listenUsingInsecureRfcommWithServiceRecord(appname, UUID_INSECURE);
                Log.d(TAG,"Method : AcceptThread ")
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }





}
