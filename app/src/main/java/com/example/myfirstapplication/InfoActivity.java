package com.example.myfirstapplication;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class InfoActivity extends AppCompatActivity {

    private TextView dvName, dvMac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        dvName = findViewById(R.id.deviceName);
        dvMac = findViewById(R.id.deviceMac);

        getInfo();
    }

    private void getInfo() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            BLEDevice device = extras.getParcelable("key");
            dvName.setText(device.getName());
            dvMac.setText(device.getDescription());
        }
    }
}