package com.example.npucommunity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.example.npucommunity.fragmentPeer.PeerList;

import java.util.ArrayList;
import java.util.Collection;
import java.net.InetAddress;
/**
 * date：2018/2/24 on 11:10
 * description: 客户端监听连接服务端信息的变化，以回调的形式把信息传递给发送文件界面
 */

public class Wifip2pReceiver extends BroadcastReceiver {

    public static final String TAG  = "Wifip2pReceiver";
    private WifiP2pManager wifiP2pManager;
    private Channel channel;
    private WifiP2pManager.PeerListListener peerListListener;

    public Wifip2pReceiver(WifiP2pManager wifiP2pManager, WifiP2pManager.Channel channel, WifiP2pManager.PeerListListener peerListListener) {
        this.wifiP2pManager = wifiP2pManager;
        this.channel = channel;
        this.peerListListener = peerListListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "接收到广播： " + intent.getAction());
        switch (intent.getAction()) {
            //WiFi P2P是否可用
            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                Log.d(TAG, "t" + state);
                break;

            // peers列表发生变化
            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                wifiP2pManager.requestPeers(channel, new WifiP2pManager.PeerListListener() {
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList peers) {
                        MainActivity.wifip2p.wifiP2pDeviceList.clear();
                        MainActivity.wifip2p.wifiP2pDeviceList.addAll(peers.getDeviceList());
                    }
                });
                MainActivity.peerList.UpdateList();
                break;

            // WiFi P2P连接发生变化
            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                if (networkInfo.isConnected()) {
                    wifiP2pManager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
                        @Override
                        public void onConnectionInfoAvailable(WifiP2pInfo info) {
                            if (info != null) {
                                Log.i(TAG, "确实获取到WiFip2pinfo");
                            } else {
                                Log.i(TAG, "WiFip2pinfo 为null");
                            }
                            MainActivity.wifip2p.mWifiP2pInfo = info;
                        }
                    });
                    Intent localBroadcastManagerIntent = new Intent("com.example.npucommunity.ACCEPT");
                    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
                    localBroadcastManager.sendBroadcast(localBroadcastManagerIntent);

                } else {
                    //mWifiDirectActionListener.onDisconnection();
                    Log.i(TAG, "与P2P设备已断开连接");
                }
                break;

            // WiFi P2P设备信息发生变化
            case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
                MainActivity.wifip2p.wifiP2pDevice = device;
                break;

            default:
        }
    }
}
