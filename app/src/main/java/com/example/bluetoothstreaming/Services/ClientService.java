package com.example.bluetoothstreaming.Services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.bluetoothstreaming.Activities.ClientSideActivity;
import com.example.bluetoothstreaming.Activities.PlayVideoActivity;
import com.example.bluetoothstreaming.Activities.ServerSideActivity;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Client Service class.
 * Performs the backends operations to connect, retrieve and stream to screen the video
 */
public class ClientService extends Service {
    public static final String TAG = "CLIENT_SERVICE";
    public static final String TAG_INTENT = "CLIENT_INTENT";
    public static final String SEND_MESSAGE_TAG = "ClientService.SEND_MESSAGE";
    private final IBinder mBinder = new ClientService.LocalBinder();
    private Thread clientThread;
    private LocalBroadcastManager localBroadcastManager;


    public ClientService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        BluetoothDevice bd = intent.getExtras().getParcelable(TAG_INTENT);
        clientThread = new ConnectThread(bd);
        clientThread.start();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Binds the clientService
     */
    public class LocalBinder extends Binder {
        public ClientService getService() {
            return ClientService.this;
        }
    }

    /**
     * Connection thread.
     * Manages connection and retrieval of the datas send by the server
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private InputStream inputStream;
        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            mmDevice = device;
            try {
                tmp = device.createRfcommSocketToServiceRecord(ServerService.APP_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }
            manageMyConnectedSocket(mmSocket);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }

        /**
         * Retrieves the video from the server.
         * TODO : Read video from buffer inside the loop
         * @param socket
         */
        private void manageMyConnectedSocket(BluetoothSocket socket) {
            Log.d(TAG, "Connected to the server !");
            Log.d(TAG, "Connected to the server !");
            Log.d(TAG, "Connected to the server !");
            Log.d(TAG, "Connected to the server !");
            Log.d(TAG, "Connected to the server !");
            Log.d(TAG, "Connected to the server !");
            Intent intent = new Intent(PlayVideoActivity.FILTER);
            intent.putExtra(SEND_MESSAGE_TAG, "Connected to Server");
            localBroadcastManager.sendBroadcast(intent);
            try {
                inputStream = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                String rootDir = Environment.getExternalStorageDirectory()
                        + File.separator + "Video";
                File rootFile = new File(rootDir);
                rootFile.mkdir();
                File localFile = new File(rootFile, ServerSideActivity.OUTPUT_FILE_NAME);
                String output = "PATH OF LOCAL FILE : " + localFile.getPath();
                if (rootFile.exists()) Log.d("SERVICE_ACTIVITY", output);
                if (!localFile.exists()) {
                    localFile.createNewFile();
                } else {
                    localFile.delete();
                    localFile.createNewFile();
                }
                FileOutputStream f = new FileOutputStream(localFile);
                byte[] buffer = new byte[1024];
                int len1 = 0;
                int nbOfPaquetsReceived = 0;
                FileDescriptor fileDescriptor = f.getFD();
                try {
                    while (true) {
                        if ((len1 = inputStream.read(buffer)) > 0) {
                            nbOfPaquetsReceived++;
                            f.write(buffer, 0, len1);
                            f.flush();
                            fileDescriptor.sync();
                            Intent i = new Intent(PlayVideoActivity.FILTER);
                            //i.putExtra("UPDATE", (int) (((float) nbOfPaquetsReceived / FILE_SIZE) * 100));
                            localBroadcastManager.sendBroadcast(i);
                        } else break;
                    }
                    Log.d(TAG, "paquets received  : " + nbOfPaquetsReceived);
                } catch (IOException se) {
                    Log.d(TAG, "Connexion CLOSED by SERVER ");
                }
                Log.d(TAG, "Sending Intent ");
                f.close();
                Intent i = new Intent(PlayVideoActivity.FILTER);
                i.putExtra("PLAY", localFile.toString());
                localBroadcastManager.sendBroadcast(i);
                Log.d(TAG, "Intent Sended ");
            } catch (IOException e) {
                Log.d("IOException", e.toString());
            }

        }
    }


}