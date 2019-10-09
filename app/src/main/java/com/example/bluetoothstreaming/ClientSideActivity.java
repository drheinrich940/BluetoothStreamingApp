package com.example.bluetoothstreaming;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.versionedparcelable.ParcelUtils;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

//TODO : comments
//TODO : refresh
//TODO : call stream from server
public class ClientSideActivity extends AppCompatActivity {

    private Button refreshButton;

    ArrayList<BluetoothDevice> bleDevices_known = new ArrayList<>();
    ArrayList<BluetoothDevice> bleDevices_new= new ArrayList<>();

    ArrayList<BluetoothDevice> bleDevices = new ArrayList<>();
    BluetoothStreamingService bluetoothStreamingService;
    BluetoothAdapter bleAdapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_side);


        bleAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothStreamingService = new BluetoothStreamingService(ClientSideActivity.this);

        if (!bleAdapter.isEnabled()) {
            Intent enableBleIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBleIntent, 1);
        }

        this.refreshButton = (Button) findViewById(R.id.refreshDeviceListButton);

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO : rename listItem in item
                Object listItem = listView.getItemAtPosition(position);
                Log.i("onItemClick", "user selected following item :" + listItem);
                for (BluetoothDevice i : bleDevices) {
                    String s = i.toString();
                    Log.i("onItemClick", "items :" + s);
                    if (s == listItem.toString()) {
                        Log.i("onItemClick", "match :" + s);
                        ParcelUuid[] uuids = i.getUuids();
                        Log.i("onItemClick", "uuids amount :" + uuids.length);
                        Log.i("onItemClick", "uuid :" + uuids[0]);
                        bluetoothStreamingService.startClient(i, uuids[0].getUuid());
                    }
                }
            }
        });

        searchAllBleDevices();
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.i("FOUND NEW DEVICE", device.getName());
                if (!bleDevices_new.contains(device))
                    bleDevices_new.add(device);
            }
        }
    };

    public void searchAllBleDevices(){
        IntentFilter ifilter  = new IntentFilter();
        ifilter.addAction(BluetoothDevice.ACTION_FOUND);
        ifilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        ClientSideActivity.this.registerReceiver(receiver, ifilter);
        getKnownDevices();
        getUnknownDevices();
        rechargeListView();
    }

    public void getKnownDevices(){
        bleDevices_known.clear();
        Set<BluetoothDevice> bondedBleDevicesSet = bleAdapter.getBondedDevices();
        bleDevices_known.addAll(bondedBleDevicesSet);
    }

    public void getUnknownDevices(){
        bleDevices_new.clear();
        if (bleAdapter.isDiscovering()) {
            bleAdapter.cancelDiscovery();
        }
        IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, discoverDevicesIntent);
        bleAdapter.startDiscovery();
    }

    public void rechargeListView() {
        ListView lv = findViewById(R.id.listView);
        this.bleDevices.clear();
        this.bleDevices.addAll(this.bleDevices_known);
        this.bleDevices.addAll(this.bleDevices_new);
        ArrayList deviceList = new ArrayList();
        for(BluetoothDevice currentDevice : this.bleDevices){
            deviceList.add(currentDevice.getName() +" ; " +currentDevice.getAddress());
        }

        ArrayAdapter<BluetoothDevice> arrayAdapter = new ArrayAdapter<BluetoothDevice>(this, android.R.layout.simple_list_item_1, bleDevices_new);
        lv.setAdapter(arrayAdapter);

//
//
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceList);
//        listView.setAdapter(arrayAdapter);
    }

    public void rechargeListView(View view) {
        ListView lv = findViewById(R.id.listView);
        this.bleDevices.clear();
        this.bleDevices.addAll(this.bleDevices_known);
        this.bleDevices.addAll(this.bleDevices_new);
        ArrayList deviceList = new ArrayList();
        for(BluetoothDevice currentDevice : this.bleDevices){
            deviceList.add(currentDevice.getName() +" ; " +currentDevice.getAddress());
        }

        ArrayAdapter<BluetoothDevice> arrayAdapter = new ArrayAdapter<BluetoothDevice>(this, android.R.layout.simple_list_item_1, deviceList);
        lv.setAdapter(arrayAdapter);

//
//
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceList);
//        listView.setAdapter(arrayAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
