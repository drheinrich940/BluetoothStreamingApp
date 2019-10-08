package com.example.bluetoothstreaming;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;


public class BluetoothStreamingService {
    private static final String TAG = "BleStreamingService";
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

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket serverSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = bleAdapter.listenUsingInsecureRfcommWithServiceRecord(appname, UUID_INSECURE);
                Log.d(TAG, "Method : AcceptThread() | server mode is insecure. \r\n Will be set up with following UUID : " + UUID_INSECURE);
            } catch (IOException e) {
                Log.e(TAG, "Method : AcceptThread() | IOException occured : " + e.getMessage());
            }
            this.serverSocket = tmp;
        }

        public void run(){
            Log.d(TAG, "Method : AcceptThread.run() | Entered run method.");
            BluetoothSocket bleSocket = null;
            try {
                Log.d(TAG, "Method : AcceptThread.run() | Server socket start. Now waiting for connection");
                bleSocket = serverSocket.accept();
                Log.d(TAG, "Method : AcceptThread.run() | Server socket accepted a connection.");
            } catch (IOException e) {
                Log.e(TAG, "Method : AcceptThread.run() | IOException occured : " + e.getMessage());
            }
            if (bleSocket != null) {
                Log.d(TAG, "Method : AcceptThread.run() | Server socket is not null. Will now connect");
                connected(bleSocket, device);
            }
        }

        public void cancel(){
            Log.d(TAG, "Method : AcceptThread.cancel() | Trying to cancel Accept");
            try{
                serverSocket.close();
            }catch (IOException e){
                Log.e(TAG, "Method : AcceptThread.close() | IOException occured while trying to close server socket : " + e.getMessage());
            }
        }
    }


}
