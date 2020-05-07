package com.example.npucommunity.message;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.example.npucommunity.MainActivity;
import com.example.npucommunity.R;

public class ChatForm extends AppCompatActivity {

    public static final String TAG  = "chatform";
    private Button send;
    private DatagramSocket mSocket;
    private DatagramSocket ySocket;
    private String textSend;
    private boolean stopReceiver;
    private final int listenPort = 9020;
    private final int sendPort = 9019;
    private InetAddress tarAddress;


    static private MHandler mHandler;
    static private EditText inputText;
    static private RecyclerView msgRecyclerView;
    static private MsgAdapter adapter;
    static private List<Msg> msgList = new ArrayList<Msg>();


    private class MHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message message) {
            if (message.arg1 == 1) {
                Msg msg = new Msg((String) message.obj, Msg.TYPE_SEND);
                textSend = (String) message.obj;
                msgList.add(msg);
                adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新ListView中的显示
                msgRecyclerView.scrollToPosition(msgList.size() - 1); // 将ListView定位到最后一行
                inputText.setText(""); // 清空输入框中的内容
                SendMessage();
            } else if (message.arg1 == 2) {
                Msg msg = new Msg((String) message.obj, Msg.TYPE_RECEIVED);
                msgList.add(msg);
                adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新ListView中的显示
                msgRecyclerView.scrollToPosition(msgList.size() - 1); // 将ListView定位到最后一行
            }
        }
    }

    private void SendMessage() {
        new Thread() {
            public void run() {
                try {
                    ySocket = new DatagramSocket(sendPort);
                } catch (SocketException e) {
                    e.printStackTrace();
                    Log.i(TAG, e.getMessage());
                }

                try {
                    byte data[] = textSend.getBytes();
                    byte ip[] = intToByteArray(MainActivity.intLocalHost);
                    byte finalData[] = byteMerger(ip, data);
                    DatagramPacket packet = new DatagramPacket(finalData, finalData.length, tarAddress, listenPort);
                    ySocket.send(packet);
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ySocket.disconnect();
                ySocket.close();
            }
        }.start();
    }

    private void ReceiveMessage() {
        new Thread() {
            public void run() {
                try {
                    mSocket = new DatagramSocket(listenPort);
                } catch (SocketException e) {
                    e.printStackTrace();
                }
                byte[] receBuf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(receBuf, receBuf.length);
                while (!stopReceiver) {
                    try {
                        mSocket.receive(packet);
                        Message message = mHandler.obtainMessage();
                        message.obj = new String(packet.getData(), 4, packet.getLength() - 4);
                        int ip = byteArrayToInt(packet.getData());
                        tarAddress = InetAddress.getByName(MainActivity.int2ip(ip));
                        message.arg1 = 2;
                        ChatForm.receive(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_form);
        mHandler = new MHandler();


        inputText = (EditText) findViewById(R.id.input_text);
        send = (Button) findViewById(R.id.send);
        msgRecyclerView = (RecyclerView) findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = inputText.getText().toString();
                if (tarAddress == null)
                {
                    tarAddress = MainActivity.wifip2p.mWifiP2pInfo.groupOwnerAddress;
                }
                if (!"".equals(s) && MainActivity.wifip2p.mWifiP2pInfo != null) {
                    Message message2 = mHandler.obtainMessage();
                    message2.obj = s;
                    message2.arg1 = 1;
                    mHandler.sendMessage(message2);
                }
            }
        });


        if (MainActivity.wifip2p.mWifiP2pInfo != null) {
            // 通过传递进来的WifiP2pInfo參数获取变化后的地址信息
            InetAddress groupOwnerAddress = MainActivity.wifip2p.mWifiP2pInfo.groupOwnerAddress;
            // 通过协商，决定一个小组的组长
            if (MainActivity.wifip2p.mWifiP2pInfo.groupFormed) {
                ReceiveMessage();
                if (MainActivity.wifip2p.mWifiP2pInfo.isGroupOwner) {
                    initMsgServer();    // 初始化消息数据
                    Toast.makeText(ChatForm.this, "i am go", Toast.LENGTH_SHORT).show();
                } else {
                    initMsgClient();
                    Toast.makeText(ChatForm.this, "i am gc", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ChatForm.this, "Disconnected", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(ChatForm.this, "WIFIDirect Disconnected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    static public void receive(Message message) {
        mHandler.sendMessage(message);
    }

    private void initMsgClient() {
        Msg msg1 = new Msg("Hello guy.", Msg.TYPE_SEND);
        msgList.add(msg1);
        Msg msg2 = new Msg("Hello. Who is that?", Msg.TYPE_RECEIVED);
        msgList.add(msg2);
        Msg msg3 = new Msg("This is Tom. Nice talking to you. ", Msg.TYPE_SEND);
        msgList.add(msg3);
        adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新ListView中的显示
    }

    private void initMsgServer() {
        Msg msg1 = new Msg("Hello guy.", Msg.TYPE_RECEIVED);
        msgList.add(msg1);
        Msg msg2 = new Msg("Hello. Who is that?", Msg.TYPE_SEND);
        msgList.add(msg2);
        Msg msg3 = new Msg("This is Tom. Nice talking to you. ", Msg.TYPE_RECEIVED);
        msgList.add(msg3);
        adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新ListView中的显示
    }

    public static byte[] byteMerger(byte[] bt1, byte[] bt2) {
        byte[] bt3 = new byte[4 + bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, 4, bt2.length);
        return bt3;
    }

    public static byte[] intToByteArray(int a) {
        return new byte[]{
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    public static int byteArrayToInt(byte[] a) {
        return (int)(a[0] & 0xff) * 16777216 + (int)(a[1] & 0xff) * 65536 + (int)(a[2] & 0xff) * 256 + (int)(a[3] & 0xff);
    }

}
