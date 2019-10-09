package com.example.bluetoothstreaming;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

//TODO : comments
//TODO : refresh
//TODO : call stream from server
public class ClientSideActivity extends AppCompatActivity {

    ArrayList<BluetoothDevice> bleDevices = new ArrayList<>();
    BluetoothAdapter bleAdapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_side);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        bleAdapter = BluetoothAdapter.getDefaultAdapter();
        Log.i("AAA", "AAAAA");

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

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object listItem = listView.getItemAtPosition(position);
                Log.i("onItemClick", "user selected following item :"+ listItem);
            }

        });

        rechargeListView();
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!bleDevices.contains(device))
                    bleDevices.add(device);
            }
        }
    };

    public void rechargeList(View view) {
        rechargeListView();
    }

    public void rechargeListView() {
        ArrayList deviceList = new ArrayList(bleDevices);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceList);
        listView.setAdapter(arrayAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

}
