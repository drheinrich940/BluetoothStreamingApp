package com.example.bluetoothstreaming.Services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.bluetoothstreaming.Activities.ServerSideActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Server service running the backend server tasks.
 * With the threads we were working on dynamic UUIDS. Here they are statics.
 */
public class ServerService extends Service {
    public static final String TAG = "SERVER_SERVICE";
    public static final String START_ROUTINE_TAG = "START_ROUTINE_TAG";
    public static final UUID APP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9BFFFF");

    private final IBinder mBinder = new LocalBinder();
    private BluetoothServerSocket mmServerSocket;
    private LocalBroadcastManager localBroadcastManager;
    private Thread serverThread;

    public ServerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("SERVER SERVICE", "Service created");
        this.serverThread = new AcceptThread();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("SERVER SERVICE", "Service started");

        if (intent.getBooleanExtra(START_ROUTINE_TAG, false)) {
            this.startRoutine();
        } else {
            this.stopRoutine();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void startRoutine() {
        this.serverThread.start();

    }

    private void stopRoutine() {
        Log.d(TAG, "Stopping routine");
        cancelConnect();
        this.serverThread.interrupt();
        stopSelf();
    }

    public class LocalBinder extends Binder {
        public ServerService getService() {
            return ServerService.this;
        }
    }


    private class AcceptThread extends Thread {
        private OutputStream mmOutStream;
        private Handler handler;


        public AcceptThread() {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothServerSocket tmp = null;
            try {
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(TAG, APP_UUID);
                Log.d(TAG, "Thread Accepted");
            } catch (IOException e) {
                Log.e(TAG, "Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            OutputStream mmOutStream = null;
            while (true) {
                Log.d(TAG, "Trying to accept");
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Socket's accept() method failed", e);
                    break;
                }
                if (socket != null) {
                    Log.d(TAG, "A connection was accepted");
                    manageMyConnectedSocket(socket);
                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }

            }
        }

        private void manageMyConnectedSocket(BluetoothSocket socket) {
            Log.d(TAG, "Client connected");
            Log.d(TAG, "Connected to the server !");
            Intent intent = new Intent(ServerSideActivity.FILTER);
            intent.putExtra(ClientService.SEND_MESSAGE_TAG, "A client is connected to you");
            localBroadcastManager.sendBroadcast(intent);

            try {
                mmOutStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // READ LOCAL FILE
            File localFile = new File(Environment.getExternalStorageDirectory() + "/" + "androiddef/" + "z.mp4");
            int size = (int) localFile.length();
            byte[] bytes = new byte[size];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(localFile));
                buf.read(bytes, 0, bytes.length);
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                mmOutStream.write(bytes);
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mmOutStream.close();
                Intent endIntent = new Intent("ServerSideActivity");
                endIntent.putExtra(ClientService.SEND_MESSAGE_TAG, "File transfer complete");
                localBroadcastManager.sendBroadcast(endIntent);

            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);
            }

        }


    }

    public void cancelConnect() {
        try {
            mmServerSocket.close();
            Log.d(TAG, "Socket closed");
        } catch (IOException e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }


}