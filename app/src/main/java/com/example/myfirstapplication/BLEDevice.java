package com.example.myfirstapplication;

import android.os.Parcel;
import android.os.Parcelable;

public class BLEDevice implements Parcelable {
    private String Name;
    private String Description;

    public BLEDevice(String name, String description) {
        Name = name;
        Description = description;
    }

    protected BLEDevice(Parcel in) {
        Name = in.readString();
        Description = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Name);
        dest.writeString(Description);
    }
}
