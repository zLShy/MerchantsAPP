package cn.thinkorange.merchantsapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Administrator on 2016/6/21.
 */
public class MyRevice extends BroadcastReceiver {

    NetworkInfo.State wifiState = null;
    NetworkInfo.State mobileState = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        HomeActivity activity = new HomeActivity();

        if (wifiState != null && mobileState != null && NetworkInfo.State.CONNECTED != wifiState && NetworkInfo.State.CONNECTED == mobileState) {
            System.out.println("手机网络连接成功！");

            activity.OpenWebSocket();
//            MainActivity.ShowMessage();
//            Toast.makeText(context, "手机网络连接成功！", Toast.LENGTH_SHORT).show();
        } else if (wifiState != null && mobileState != null && NetworkInfo.State.CONNECTED == wifiState && NetworkInfo.State.CONNECTED != mobileState) {
//            Toast.makeText(context, "无线网络连接成功！", Toast.LENGTH_SHORT).show();
            System.out.println("无线网络连接成功！");
            activity.OpenWebSocket();
        } else if (wifiState != null && mobileState != null && NetworkInfo.State.CONNECTED != wifiState && NetworkInfo.State.CONNECTED != mobileState) {
//            Toast.makeText(context, "手机没有任何网络...", Toast.LENGTH_SHORT).show();
            System.out.println("手机没有任何网络...");
            activity.OpenWebSocket();
        }
    }
}
