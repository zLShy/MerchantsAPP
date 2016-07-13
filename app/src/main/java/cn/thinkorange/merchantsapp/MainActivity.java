package cn.thinkorange.merchantsapp;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import Model.GlobalModel;
import cn.thinkorange.merchantsapp.com.example.qr_codescan.MipcaActivityCapture;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class MainActivity extends Activity {

    private GlobalModel globalModel = GlobalModel.get_Instanst();
    public static Activity mActivity;
    NotificationManager nm;
    private Button scan;
    static final int NOTIFICATION_ID = 0x123;
    private final static int SCANNIN_GREQUEST_CODE = 1;

    private io.socket.client.Socket mSocket;
    {
        try {
            mSocket = IO.socket(globalModel.SocketUri);
        } catch (Exception e) {
            if (e != null) {

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        mActivity = this;
        this.scan = (Button) findViewById(R.id.scan);

        this.scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, MipcaActivityCapture.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
            }
        });
    }

    public void ShowMessage() {

        Toast.makeText(getApplicationContext(), "ShowMessage", Toast.LENGTH_LONG).show();
    }


    public void OpenWebSocket() {
        if (mSocket != null && (!mSocket.hasListeners("new_msg") || !mSocket.hasListeners("event"))) {
            mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                }
            }).on("event", new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                }
            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                }
            }).on("new_msg", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (args[0] != null) {
                        try {
                            JSONObject object = new JSONObject(args[0].toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        mSocket.connect();
    }


    @Override
    protected void onResume() {
        super.onResume();

        ShowNotify();
        OpenWebSocket();
    }


    private void ShowNotify() {

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(MainActivity.this,
                MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(
                MainActivity.this, 0, intent, 0);
        Notification notify = new Notification.Builder(
                MainActivity.this)
                // 设置打开该通知，该通知自动消失
                .setAutoCancel(true)
                        // 设置显示在状态栏的通知提示信息
                .setTicker("有新消息")
                        // 设置通知的图标
                .setSmallIcon(R.drawable.ic_launcher)
                        // 设置通知内容的标题
                .setContentTitle("一条新通知")
                        // 设置通知内容
                .setContentText("有人要买东西")
                        // // 设置使用系统默认的声音、默认LED灯
                .setDefaults(
                        Notification.DEFAULT_SOUND
                                | Notification.DEFAULT_LIGHTS)
                        // 设置通知的自定义声音

                .setWhen(System.currentTimeMillis())
                        // 设改通知将要启动程序的Intent
                .setContentIntent(pi).build();
        // 发送通知
        nm.notify(NOTIFICATION_ID, notify);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    System.out.println("=================================================");

                    Bundle bundle = data.getExtras();
                    String result = bundle.getString("result");
                    System.out.println("result"+result+"");
                    Intent it = new Intent(MainActivity.this,WebviewActivity.class);
                    it.putExtra("url",result);
                    startActivity(it);
                }
                break;
        }
    }
}
