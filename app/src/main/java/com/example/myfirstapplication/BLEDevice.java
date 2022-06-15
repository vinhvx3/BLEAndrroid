package com.example.myfirstapplication;

import android.os.Parcel;
import android.os.Parcelable;

public class BLEDevice implements Parcelable {
    private String Name;
    private String Description;
    private int Rssi;
    private String ScanRecord;

    public BLEDevice(String name, String description, int rssi, String scanRecord) {
        Name = name;
        Description = description;
        Rssi = rssi;
        ScanRecord = scanRecord;
    }

    protected BLEDevice(Parcel in) {
        Name = in.readString();
        Description = in.readString();
        Rssi = in.readInt();
        ScanRecord = in.readString();
    }

    public static final Creator<BLEDevice> CREATOR = new Creator<BLEDevice>() {
        @Override
        public BLEDevice createFromParcel(Parcel in) {
            return new BLEDevice(in);
        }

        @Override
        public BLEDevice[] newArray(int size) {
            return new BLEDevice[size];
        }
    };


    public int getRssi() {
        return Rssi;
    }

    public void setRssi(int rssi) {
        Rssi = rssi;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getScanRecord() {
        return ScanRecord;
    }

    public void setScanRecord(String scanRecord) {
        ScanRecord = scanRecord;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Name);
        dest.writeString(Description);
        dest.writeInt(Rssi);
        dest.writeString(ScanRecord);
    }
}
