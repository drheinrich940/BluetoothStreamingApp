package com.example.bluetoothstreaming;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;


public class BluetoothStreamingService {
    private static final String TAG = "BleStreamingService";
    private static final String appname = "MyVideoStreamer";
    private static final UUID UUID_INSECURE = UUID.fromString("");

    private final BluetoothAdapter bleAdapter;
    ProgressDialog progressDialog;
    Context context;
    private AcceptThread acceptThread_insecure;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private BluetoothDevice device;
    private UUID deviceUUID;


    public BluetoothStreamingService(Context context) {
        this.context = context;
        bleAdapter = BluetoothAdapter.getDefaultAdapter();
        startServer();
    }

    /**
     * Starts our streaming service by launching the AcceptThread to wait for incoming connctions.
     * This method is the 'Server side" service starting method.
     */
    public synchronized void startServer(){
        // First we cancel and free any active connectThread
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        // Then we initialise and start a brand new acceptThread
        if (acceptThread_insecure == null) {
            acceptThread_insecure = new AcceptThread();
            acceptThread_insecure.start();
        }
    }

    /**
     * Starts the client with an attempt to connect with an avaliable server device.
     * @param bleDevice
     * @param uuid
     */
    public void startClient(BluetoothDevice bleDevice, UUID uuid){
        progressDialog = ProgressDialog.show(context,"Connecting Bluetooth"
                ,"You.. Shall.. (not ?) Wait...",true);
        connectThread = new ConnectThread(device, uuid);
        connectThread.start();
    }

    /**
     * This thread runs indefinitely, waiting for incoming connections.
     * When a connection request occurs, it will try to accept it in order to establish it.
     */
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


    /**
     * This Thread runs when trying to establish a connection between local and distant device.
     * TODO : logs
     */
    private class ConnectThread extends Thread {
        private BluetoothSocket bleSocket;

        public ConnectThread(BluetoothDevice device, UUID uuid) {
            device = device;
            deviceUUID = uuid;
        }

        public void run(){
            BluetoothSocket tmp = null;
            try {
                tmp = device.createInsecureRfcommSocketToServiceRecord(deviceUUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            bleSocket = tmp;
            bleAdapter.cancelDiscovery();
            try {
                bleSocket.connect();
            } catch (IOException e) {
                try {
                    bleSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            connected(bleSocket, device);
        }

        public void cancel() {
            try {
                bleSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ConnectedThread maintains the connection between the devices.
     * TODO : comments
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket bleSocket;
        private final InputStream inStream;
        private final OutputStream outStream;

        public ConnectedThread(BluetoothSocket socket){
            this.bleSocket = socket;
            InputStream inStream_tmp = null;
            OutputStream outStream_tmp = null;
            progressDialog.dismiss();
            try {
                inStream_tmp = this.bleSocket.getInputStream();
                outStream_tmp = this.bleSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inStream = inStream_tmp;
            outStream = outStream_tmp;
        }

        public void cancel() {
            try {
                this.bleSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run(){
            byte[] buffer = new byte[1024];
            int bytes;
            while (true) {
                try {
                    bytes = inStream.read(buffer);
                    /*
                    Video shit here
                     */
                } catch (IOException e) {
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                outStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void connected(BluetoothSocket bleSocket, BluetoothDevice bleDevice) {
        Log.d(TAG, "connected: Starting.");

        // Start the thread to manage the connection and perform transmissions
        connectedThread = new ConnectedThread(bleSocket);
        connectedThread.start();
    }

    public void write(byte[] out) {
        ConnectedThread r;
        Log.d(TAG, "write: Write Called.");
        connectedThread.write(out);
    }
}
