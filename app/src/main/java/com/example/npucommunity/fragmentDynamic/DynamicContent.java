package com.example.npucommunity.fragmentDynamic;


import android.graphics.Bitmap;
import android.net.wifi.p2p.WifiP2pDevice;

import com.example.npucommunity.fragmentPeer.MyFriends;

import java.util.ArrayList;

public class DynamicContent {
    private String name;
    private int imageId;
    private String signature;
    private String text;
    private ArrayList<Bitmap> imageList;
    private WifiP2pDevice device;

    public DynamicContent(int imageId, WifiP2pDevice wifiP2pDevice, String text, ArrayList<Bitmap> imageList) {
        this.name = wifiP2pDevice.deviceName;
        this.imageId = imageId;
        this.signature = wifiP2pDevice.deviceAddress;
        this.text = text;
        this.imageList = imageList;
        this.device = wifiP2pDevice;
    }

    public DynamicContent(MyFriends myFriends, String text, ArrayList<Bitmap> imageList) {
        this.name = myFriends.getName();
        this.imageId = myFriends.getImageId();
        this.signature = myFriends.getSignature();
        this.text = text;
        this.imageList = imageList;
        this.device = myFriends.getDevice();
    }

    public String getName() {
        return name;
    }

    public int getImageId() {
        return imageId;
    }

    public String getSignature() {
        return signature;
    }

    public String getText() { return text; }

    public ArrayList<Bitmap> getImageList() { return imageList; }

    public WifiP2pDevice getDevice() {
        return device;
    }

}
