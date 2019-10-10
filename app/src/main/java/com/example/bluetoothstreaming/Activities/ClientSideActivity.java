package com.example.bluetoothstreaming.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.bluetoothstreaming.R;
import com.example.bluetoothstreaming.Threading.BluetoothStreamingThreads;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

//TODO : comments
//TODO : refresh
//TODO : call stream from server

/**
 * Clients main activity. Allows the user to subscribe to a given server feed via bluetooth interface
 */
public class ClientSideActivity extends AppCompatActivity {

    private Button refreshButton;

    ArrayList<BluetoothDevice> bleDevices_known = new ArrayList<>();
    ArrayList<BluetoothDevice> bleDevices_new= new ArrayList<>();

    ArrayList<BluetoothDevice> bleDevices = new ArrayList<>();
    BluetoothStreamingThreads bluetoothStreamingThreads;
    BluetoothAdapter bleAdapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_side);


        bleAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothStreamingThreads = new BluetoothStreamingThreads(ClientSideActivity.this);

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
                    String s = i.getName() +" ; " +i.toString();
                    Log.i("onItemClick", "items :" + s);
                    if (s.equals(listItem.toString())) {
                        Log.i("onItemClick", "match :" + s);
                        ParcelUuid[] uuids = i.getUuids();
                        Log.i("onItemClick", "uuids amount :" + uuids.length);
                        Log.i("onItemClick", "uuid :" + uuids[0]);
                        //bluetoothStreamingThreads.startClient(i, UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66"));
                        String a = "OKOKOOK";
                        //bluetoothStreamingThreads.write(a.getBytes());
                        goToClientServerPairingActivity(i);
                    }
                }

            }
        });

        searchAllBleDevices();
    }

    /**
     * Will perform pairing between current and selected device from the client activity view
     * @param bluetoothDevice
     */
    public void goToClientServerPairingActivity(BluetoothDevice bluetoothDevice) {
        Intent intent = new Intent(this, BondingActivity.class);
        intent.putExtra("BLUETOOTH_SELECTED_DEVICE",bluetoothDevice);
        startActivity(intent);
    }

    /**
     * To retrieve unknown avaliable bluetooth devices
     */
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

    /**
     * Performs general search
     */
    public void searchAllBleDevices(){
        IntentFilter ifilter  = new IntentFilter();
        ifilter.addAction(BluetoothDevice.ACTION_FOUND);
        ifilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        ClientSideActivity.this.registerReceiver(receiver, ifilter);
        getKnownDevices();
        getUnknownDevices();
        rechargeListView();
    }

    /**
     * Gets all the paired/known devices
     */
    public void getKnownDevices(){
        bleDevices_known.clear();
        Set<BluetoothDevice> bondedBleDevicesSet = bleAdapter.getBondedDevices();
        bleDevices_known.addAll(bondedBleDevicesSet);
    }

    /**
     * Gets all the unknown avaliable devices
     */
    public void getUnknownDevices(){
        bleDevices_new.clear();
        if (bleAdapter.isDiscovering()) {
            bleAdapter.cancelDiscovery();
        }
        IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, discoverDevicesIntent);
        bleAdapter.startDiscovery();
    }

    /**
     * Reloads the list object with refreshed data
     */
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

    /**
     * Reload the view to display refreshed data
     * @param view
     */
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
