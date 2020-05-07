package com.example.npucommunity.fragmentPeer;

import android.net.wifi.p2p.WifiP2pDevice;

public class MyFriends {
    private String name;
    private int imageId;
    private String signature;
    private WifiP2pDevice device;

    public MyFriends(int imageId, WifiP2pDevice wifiP2pDevice) {
        this.name = wifiP2pDevice.deviceName;
        this.imageId = imageId;
        this.signature = wifiP2pDevice.deviceAddress;
        this.device = wifiP2pDevice;
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

    public WifiP2pDevice getDevice() {
        return device;
    }
}

