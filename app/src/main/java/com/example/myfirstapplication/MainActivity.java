package com.example.myfirstapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
//                    GetListDevice();
                    scanLeDevice();
                } else {
                    ClearListDevice();
                }

//                ShowListDeviceConnected();
//                ShowListDevice();
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


//                    if (Objects.equals(result.getDevice().getName(), "") || result.getDevice().getName() == null) {
//                        return;
//                    }

                    if(result.getRssi() < -50) {
                        return;
                    }

                    BLEDevice device = new BLEDevice(result.getDevice().getAddress(), result.getDevice().getName(), result.getRssi());
                    arrDevice.add(device);

                    bleDeviceList.add(result.getDevice());

                    ShowListDevice();
//                    leDeviceListAdapter.addDevice(result.getDevice());
//                    leDeviceListAdapter.notifyDataSetChanged();
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

    // ----------------------------------------

    // ----------------------------------



    private void GetListDevice() {
//        arrDevice = new ArrayList<>();

//        arrDevice.add(new BLEDevice("IPhone XR", "XYJSKIDU9832J"));
//        arrDevice.add(new BLEDevice("SamSung Note 5", "JFHDSJFHI3"));
//        arrDevice.add(new BLEDevice("IPhone 12", "T56TGW43FE"));
//        arrDevice.add(new BLEDevice("Xiaomi", "RETR6464EE"));
//        arrDevice.add(new BLEDevice("Nokia", "RER4W5456465J"));
//
//        arrDevice.add(new BLEDevice("IPhone XR", "XYJSKIDU9832J"));
//        arrDevice.add(new BLEDevice("SamSung Note 5", "JFHDSJFHI3"));
//        arrDevice.add(new BLEDevice("IPhone 12", "T56TGW43FE"));
//        arrDevice.add(new BLEDevice("Xiaomi", "RETR6464EE"));
//        arrDevice.add(new BLEDevice("Nokia", "RER4W5456465J"));

    }

    private void ClearListDevice () {
        arrDevice = new ArrayList<>();
        bleDeviceList = new ArrayList<>();
        posConnected = -1;
        ShowListDevice();
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
//        if (position < posConnected) {
//            posConnected = position;
//        } else {
//            posConnected = position + 1;
//        }

        Log.d("POS: ", String.valueOf(position));
        Log.d("SIZE: ", String.valueOf(bleDeviceList.size()));

        Log.d("CHECK", bleDeviceList.get(position).getAddress().toString());

        deviceAddress = bleDeviceList.get(position).getAddress();

//        ShowListDevice();
//        ShowListDeviceConnected();
    }

    private void openDeviceInfo(int position){
        Intent intent = new Intent(this, InfoActivity.class);

        intent.putExtra("key", arrDevice.get(position));
        startActivity(intent);
    }

    private BluetoothLeService bluetoothService;


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bluetoothService = ((BluetoothLeService.LocalBinder) service).getService();
            if (bluetoothService != null) {
                if (!bluetoothService.initialize()) {
                    Log.e("TAG", "Unable to initialize Bluetooth");
                    finish();
                }
                // perform device connection
                bluetoothService.connect(deviceAddress);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bluetoothService = null;
        }
    };


}