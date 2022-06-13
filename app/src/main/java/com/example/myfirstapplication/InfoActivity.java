package com.example.myfirstapplication;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class InfoActivity extends AppCompatActivity {

    private TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        tvInfo = findViewById(R.id.tv_info);
        getInfo();
    }

    private void getInfo() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            BLEDevice device = extras.getParcelable("key");
            tvInfo.setText(device.getName() + " " + device.getDescription());
        }
    }
}