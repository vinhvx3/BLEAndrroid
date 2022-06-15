package com.example.myfirstapplication;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class InfoActivity extends AppCompatActivity {

    private TextView dvName, dvMac, dvUuid, dvTxPowerLevel, dvRssid, dvManufacturerSpecific;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        dvName = findViewById(R.id.deviceName);
        dvMac = findViewById(R.id.deviceMac);
        dvRssid = findViewById(R.id.deviceRssi);
        dvManufacturerSpecific = findViewById(R.id.deviceManufacturerSpecific);

        getInfo();
    }

    private void getInfo() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            BLEDevice device = extras.getParcelable("key");
            dvMac.setText(device.getName());
            dvName.setText(device.getDescription());
            dvRssid.setText(String.valueOf(device.getRssi()));

            if (device.getScanRecord() != null) {
                dvManufacturerSpecific.setText(device.getScanRecord());
            }


        }
    }
}