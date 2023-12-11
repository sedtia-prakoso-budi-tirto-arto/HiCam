package com.example.fp;

import android.os.Parcel;
import android.os.Parcelable;

public class LocationSaver implements Parcelable {
    private String locName;
    private String locDesc;

    private boolean locStatus = false;

    public LocationSaver(String locName, String locDesc, boolean locStatus){
        this.locName = locName;
        this.locDesc = locDesc;
        this.locStatus = locStatus;
    }
    public String getLocName(){
        return locName;
    }

    public String getLocDesc(){
        return locDesc;
    }

    public boolean getLocStatus(){
        return locStatus;
    }

    public void setLocName(String locName){
        this.locName = locName;
    }

    public void setLocDesc(String locDesc){
        this.locDesc = locDesc;
    }
    protected LocationSaver(Parcel in) {
        locName = in.readString();
        locDesc = in.readString();
        locStatus = in.readByte() != 0;
    }

    public static final Parcelable.Creator<LocationSaver> CREATOR = new Parcelable.Creator<LocationSaver>() {
        @Override
        public LocationSaver createFromParcel(Parcel in) {
            return new LocationSaver(in);
        }

        @Override
        public LocationSaver[] newArray(int size) {
            return new LocationSaver[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(locName);
        dest.writeString(locDesc);
        dest.writeByte((byte) (locStatus ? 1 : 0));
    }
}
