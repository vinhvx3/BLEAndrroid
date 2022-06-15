package com.example.myfirstapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity extends AppCompatActivity {

    public final static int REQUEST_CODE_PERMISSION = 0;
    public final static int REQUEST_CODE_BLUETOOTH_ENABLE = 1;


    // VIEW
    ArrayList<BLEDevice> arrDevice = new ArrayList<>();
    int posConnected = -1;

    ListView lvDevice, lvDeviceConnected;
    BLEDeviceAdapter adapter, adapterConnected;

    Switch switchBtn;

    // BLE
    List<BluetoothDevice> bleDeviceList = new ArrayList<>();

    String deviceAddress;

    BluetoothManager bluetoothManager;
    BluetoothAdapter bluetoothAdapter;



    private BluetoothLeService bluetoothService;

    private BluetoothGatt bluetoothGatt;

    private BluetoothLeScanner bluetoothLeScanner;
    private boolean scanning;
    private Handler handler = new Handler();

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 30000;

    // Create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindBluetooth();
        switchBtn = (Switch) findViewById(R.id.switchButton);

        switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    scanLeDevice();
                } else {
                    ClearListDevice();
                }
            }
        });

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void bindBluetooth() {
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        checkBt();
    }

    private void checkBt() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            return;
        }
        if (bluetoothAdapter != null) {
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_BLUETOOTH_ENABLE) {
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "please_enable_bluetooth", Toast.LENGTH_LONG).show();
            }
        }
    }


//    private LeDeviceListAdapter leDeviceListAdapter = new LeDeviceListAdapter();

    // Device scan callback.
    private ScanCallback leScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);


                    for(int i = 0; i < arrDevice.size(); i++) {
                        if(Objects.equals(arrDevice.get(i).getName(), result.getDevice().getAddress())) {
                            return;
                        }
                    }

                    ScanRecord scanRecord = result.getScanRecord();

                    if(result.getRssi() < -50 || scanRecord == null) {
                        return;
                    }

                    Log.d("TAG", scanRecord.toString());


                    BLEDevice device = new BLEDevice(
                            result.getDevice().getAddress(),
                            result.getDevice().getName(),
                            result.getRssi(),
                            scanRecord.toString());


                    arrDevice.add(device);

                    bleDeviceList.add(result.getDevice());

                    ShowListDevice();
                }
            };

    @SuppressLint("MissingPermission")
    private void scanLeDevice() {
        if (!scanning) {
            // Stops scanning after a predefined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanning = false;
                    bluetoothLeScanner.stopScan(leScanCallback);
                    ShowListDevice();
                }
            }, SCAN_PERIOD);

            scanning = true;

            bluetoothLeScanner.startScan(leScanCallback);
        } else {
            scanning = false;
            bluetoothLeScanner.stopScan(leScanCallback);
        }
    }


    private void ClearListDevice () {
        arrDevice = new ArrayList<>();
        bleDeviceList = new ArrayList<>();
        posConnected = -1;
        ShowListDevice();
        ShowListDeviceConnected();
    }

    private void ShowListDevice () {
        lvDevice = (ListView) findViewById(R.id.listviewDevice);

        ArrayList<BLEDevice> arr = new ArrayList<>();

        arr.addAll(arrDevice);

        if (posConnected >= 0) {
            arr.remove(posConnected);
        }

        adapter = new BLEDeviceAdapter(this, R.layout.device_row, arr);
        lvDevice.setAdapter(adapter);

        // Item listener
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                switch (view.getId()) {
                    case R.id.imageviewicon:
                        openDeviceInfo(position);
                        break;
                    case R.id.textviewname:
                        connectDevice(position);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void ShowListDeviceConnected () {
        lvDeviceConnected = (ListView) findViewById(R.id.listviewDeviceConnected);

        ArrayList<BLEDevice> arr = new ArrayList<>();

        if (posConnected >= 0) {
            arr.add(arrDevice.get(posConnected));
        }

        adapterConnected = new BLEDeviceAdapter(this, R.layout.device_row, arr);
        lvDeviceConnected.setAdapter(adapterConnected);

        // Item listener
        adapterConnected.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                switch (view.getId()) {
                    case R.id.imageviewicon:
                        openDeviceInfo(position);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void connectDevice(int position) {
        deviceAddress = bleDeviceList.get(position).getAddress();

        if(deviceAddress != null) {
            bluetoothService.connect(deviceAddress);
        }

    }

    private void openDeviceInfo(int position){
        Intent intent = new Intent(this, InfoActivity.class);

        intent.putExtra("key", arrDevice.get(position));
        startActivity(intent);
    }


    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("TAG", "On service connected");
            bluetoothService = ((BluetoothLeService.LocalBinder) service).getService();
            if (bluetoothService != null) {
                if (!bluetoothService.initialize()) {
                    Log.e("TAG", "Unable to initialize Bluetooth");
                    finish();
                }
                // perform device connection
                Log.e("TAG", "initialize Bluetooth success");
                bluetoothService.connect(deviceAddress);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bluetoothService = null;
        }
    };

    Boolean connected = false;

    private final BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                connected = true;
                updateConnectionState("connected");
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                connected = false;
                updateConnectionState("disconnect");

            }
        }
    };

    private void updateConnectionState (String message) {
        if (message == "connected") {
            Log.d("CONNECT", "Connected Success");
            for(int i = 0; i < arrDevice.size(); i++) {
                if(Objects.equals(arrDevice.get(i).getName(), deviceAddress)) {
                    posConnected = i;
                    ShowListDevice();
                    ShowListDeviceConnected();
                    return;
                }
            }
        } else {
            Log.d("CONNECT", "Disconected");

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter());
        if (bluetoothService != null) {
            final boolean result = bluetoothService.connect(deviceAddress);
            Log.d("TAG", "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(gattUpdateReceiver);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        return intentFilter;
    }
}