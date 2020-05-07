package com.example.npucommunity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.app.Activity;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.*;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.npucommunity.fragmentPeer.PeerList;
import com.qmuiteam.qmui.widget.dialog.*;
import com.example.npucommunity.fragmentUserProfile.UserProfile;
import com.stephentuso.welcome.*;
import com.example.npucommunity.fragmentDynamic.*;
import com.yalantis.phoenix.PullToRefreshView;
import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageNavigationView;

import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Method;


public class MainActivity extends AppCompatActivity implements PeerListListener, ChannelListener{

    Menu menu;
    private ViewPager viewPager;
    private List<Fragment> fragmentList;
    public static final String TAG = "MainActivity";
    public static PeerList peerList = new PeerList();
    public Dynamic dynamic = new Dynamic();
    public static InetAddress localHost;
    public static int intLocalHost;
    public class Wifip2p {
        public WifiP2pInfo mWifiP2pInfo;
        public WifiP2pManager mWifiP2pManager;
        public WifiP2pManager.Channel mChannel;
        public Wifip2pReceiver mWifip2pReceiver;
        public IntentFilter intentFilter = new IntentFilter();
        public List<WifiP2pDevice> wifiP2pDeviceList = new ArrayList<WifiP2pDevice>();
        public WifiP2pDevice wifiP2pDevice;
    }
    public static Wifip2p wifip2p;

    WelcomeHelper welcomeScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity)this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        //生成WifiP2pManager
        wifip2p = new Wifip2p();
        wifip2p.mWifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        wifip2p.mChannel = wifip2p.mWifiP2pManager.initialize(this, getMainLooper(), null);
        wifip2p.intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        wifip2p.intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        wifip2p.intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        wifip2p.intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        wifip2p.mWifip2pReceiver = new Wifip2pReceiver(wifip2p.mWifiP2pManager, wifip2p.mChannel, this);
        registerReceiver(wifip2p.mWifip2pReceiver, wifip2p.intentFilter);


        // 创建ViewList
        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(peerList);
        fragmentList.add(dynamic);
        fragmentList.add(new UserProfile());
        MyFragmentViewAdapter ma = new MyFragmentViewAdapter(getSupportFragmentManager(),fragmentList);
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPager.setAdapter(ma);
        PageNavigationView tab = (PageNavigationView) findViewById(R.id.tab);
        NavigationController navigationController = tab.material()
                .addItem(android.R.drawable.ic_menu_search, "好友")
                .addItem(android.R.drawable.ic_menu_compass, "动态")
                .addItem(android.R.drawable.ic_menu_camera, "信息")
                .build();
        navigationController.setupWithViewPager(viewPager);


        // pull to refresh
        final PullToRefreshView mPullToRefreshView;
        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefreshView.setRefreshing(false);
                    }
                }, 2000);
            }
        });

        welcomeScreen = new WelcomeHelper(this, MyWelcomeActivity.class);
        welcomeScreen.show(savedInstanceState);
        //welcomeScreen.forceShow();

        try {
            localHost = InetAddress.getByName(getLocalIpAddress());
            //Toast.makeText(MainActivity.this, localHost.toString(), Toast.LENGTH_SHORT).show();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    public String getLocalIpAddress() {
        try {

            WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            intLocalHost = wifiInfo.getIpAddress();
            return int2ip(intLocalHost);
        } catch (Exception ex) {
            return " 获取IP出错！请保证是WIFI,或者请重新打开网络!\n" + ex.getMessage();
        }
        // return null;
    }

    public static String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        welcomeScreen.onSaveInstanceState(outState);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (wifip2p.mWifip2pReceiver == null) {
//            wifip2p.mWifip2pReceiver = new Wifip2pReceiver(wifip2p.mWifiP2pManager, wifip2p.mChannel, this);
//        }
//        registerReceiver(wifip2p.mWifip2pReceiver, wifip2p.intentFilter);
//    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.add_item:
                final  QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(this);
                builder.setTitle("新建动态")
                        .setPlaceholder("请输入你的新动态")
                        .addAction("取消", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                dialog.dismiss();
                            }
                        })
                        . addAction("确定", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                dialog.dismiss();
                                //dynamic.showS();
                            }
                        }).show();

                Toast.makeText(MainActivity.this, "Added", Toast.LENGTH_SHORT).show();
                break;
            case R.id.remove_item:
                Toast.makeText(MainActivity.this, "Removed", Toast.LENGTH_SHORT).show();
                break;
            case R.id.search_item:
                    wifip2p.mWifiP2pManager.discoverPeers(wifip2p.mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this, "Discovery Initiated", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(MainActivity.this, "Discovery Failed : " + reasonCode, Toast.LENGTH_SHORT).show();
                    }
                });
            default:
        }
        return true;
    }


    @Override
    public void onPeersAvailable(WifiP2pDeviceList peersList) {
        Log.d(TAG, "onPeersAvailable" + "调用了");
        wifip2p.wifiP2pDeviceList = new ArrayList<WifiP2pDevice>();
        wifip2p.wifiP2pDeviceList.clear();
        wifip2p.wifiP2pDeviceList.addAll(peersList.getDeviceList());
        if (wifip2p.wifiP2pDeviceList.size() == 0) {
            Log.d(TAG, "Sorry No Peers Found");
        }
    }


//    @Override
//    public void onPeersInfo(Collection<WifiP2pDevice> wifiP2pDeviceList) {
//        for (WifiP2pDevice device : wifiP2pDeviceList) {
//            Toast.makeText(MainActivity.this, "连接的设备信息：" + device.deviceName + "--------" + device.deviceAddress, Toast.LENGTH_SHORT).show();
//        }
//        this.wifip2p.wifiP2pDeviceList = wifiP2pDeviceList;
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

    }


    @Override
    public void onChannelDisconnected() {
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifip2p.mWifip2pReceiver);
        wifip2p.mWifip2pReceiver = null;
    }

    public void onDisconnection() {
        Log.e(TAG, "连接断开");
    }

    private void sendThisDeviceChangedBroadcast(String devName) {
        try {
            Class[] paramTypes = new Class[3];
            paramTypes[0] = WifiP2pManager.Channel.class;
            paramTypes[1] = String.class;
            paramTypes[2] = WifiP2pManager.ActionListener.class;
            Method setDeviceName = wifip2p.mWifiP2pManager.getClass().getMethod(
                    "setDeviceName", paramTypes);
            setDeviceName.setAccessible(true);

            Object arglist[] = new Object[3];
            arglist[0] = wifip2p.mChannel;
            arglist[1] = devName;
            arglist[2] = new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    Log.d("setDeviceName succeeded", "true");
                }

                @Override
                public void onFailure(int reason) {
                    Log.d("setDeviceName failed", "true");
                }
            };
            setDeviceName.invoke(wifip2p.mWifiP2pManager, arglist);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}