package com.example.npucommunity.fragmentPeer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.net.wifi.p2p.*;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.npucommunity.MainActivity;
import com.example.npucommunity.R;
import com.example.npucommunity.message.ChatForm;

import java.util.ArrayList;
import java.util.List;

public class PeerList extends Fragment {

    public static final String TAG = "PeerList";
    public static Intent intent;
    private ListView listView;
    private ProgressBar progressBar;
    private List<MyFriends> myFriendsList = new ArrayList<>();
    public static MyFriends me;
    PeerListAdapter adapter;
    private ConnectionChangeReceiver connectionChangeReceiver;
    private IntentFilter intentFilter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.friend_list, container, false);
        listView = (ListView)view.findViewById(R.id.List_view);
        progressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        me = new MyFriends(R.drawable.timg, MainActivity.wifip2p.wifiP2pDevice);

        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.npucommunity.ACCEPT");
        connectionChangeReceiver = new ConnectionChangeReceiver();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(connectionChangeReceiver, intentFilter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyFriends myFriends = myFriendsList.get(position);
                if (MainActivity.wifip2p.mWifiP2pInfo != null)
                {
                    if (MainActivity.wifip2p.mWifiP2pInfo.groupFormed)
                    {
                        disconnect();
                    }
                }
                connect(myFriends.getDevice());
            }
        });
        return view;
    }



    private void connect(WifiP2pDevice device) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        MainActivity.wifip2p.mWifiP2pManager.connect(MainActivity.wifip2p.mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(MainActivity.TAG,"邀请成功");
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(int reasonCode) {
                Log.d(MainActivity.TAG,"邀请失败" + reasonCode);
            }
        });
    }

    class ConnectionChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            startActivity(new Intent(getActivity(), ChatForm.class));
        }
    }

    private void disconnect() {
        MainActivity.wifip2p.mWifiP2pManager.removeGroup(MainActivity.wifip2p.mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onFailure(int reasonCode) {
                Log.i(TAG, "disconnect fail:" + reasonCode);
            }

            @Override
            public void onSuccess() {
                Log.i(TAG, "disconnect onSuccess");
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void UpdateList() {
        myFriendsList.clear();
        if (MainActivity.wifip2p.wifiP2pDevice != null) {
            myFriendsList.add(me);
            if (MainActivity.wifip2p.wifiP2pDeviceList.size() != 0) {
                for (WifiP2pDevice device : MainActivity.wifip2p.wifiP2pDeviceList) {
                    myFriendsList.add(new MyFriends(R.drawable.timg, device));
                }
            }

            if (adapter == null) {
                adapter = new PeerListAdapter(this.getContext(), R.layout.friend, myFriendsList);
                listView.setAdapter(adapter);
            } else {
                ((PeerListAdapter) listView.getAdapter()).notifyDataSetChanged();
            }
        }

    }



    private class PeerListAdapter extends ArrayAdapter<MyFriends> {

        private int resourceId;

        public PeerListAdapter(Context context, int textViewResourceId, List<MyFriends> objects) {
            super(context, textViewResourceId, objects);
            resourceId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyFriends myFriends = getItem(position);
            View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            ImageView friendImage = (ImageView) view.findViewById(R.id.avatar);
            TextView friendName = (TextView) view.findViewById(R.id.name);
            TextView friendSignature = (TextView) view.findViewById(R.id.signature);
            friendImage.setImageResource(myFriends.getImageId());
            friendName.setText(myFriends.getName());
            friendSignature.setText(myFriends.getSignature());
            return view;
        }
    }

}

