package com.example.fp;

public class LocationSaver {
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
}
