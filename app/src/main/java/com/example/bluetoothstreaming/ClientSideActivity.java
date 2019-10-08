package com.example.bluetoothstreaming;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Set;

public class ClientSideActivity extends AppCompatActivity {

    ArrayList<BluetoothDevice> bleDevices = new ArrayList<>();
    BluetoothAdapter bleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_side);
        listAvaliableBluetoothPeriphericals();
    }

    public void listAvaliableBluetoothPeriphericals(){
        bleAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bleAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
        Set<BluetoothDevice> bondedBleDevicesSet = bleAdapter.getBondedDevices();

        boolean discovery = bleAdapter.startDiscovery();

        if (bondedBleDevicesSet.size() > 0) {
            for (BluetoothDevice device : bondedBleDevicesSet) {
                bleDevices.add(device);
            }
        }
    }
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(!bleDevices.contains(device))
                    bleDevices.add(device);
            }
        }
    };

    public void refreshList(View view) {
        // TODO : Give ID to component list
        ListView listView = findViewById(R.id.listView);
        ArrayAdapter<BluetoothDevice> arrayAdapter = new ArrayAdapter<BluetoothDevice>(this, android.R.layout.simple_list_item_1, bleDevices);
        listView.setAdapter(arrayAdapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

}
