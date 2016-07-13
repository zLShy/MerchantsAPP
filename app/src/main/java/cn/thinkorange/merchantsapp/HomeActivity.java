package cn.thinkorange.merchantsapp;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Model.GlobalModel;
import Tools.ImageCycleView;
import Tools.TimeFormat;
import cn.thinkorange.merchantsapp.com.example.qr_codescan.MipcaActivityCapture;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class HomeActivity extends Activity implements View.OnClickListener {

    private DrawerLayout mDrawerLayout = null;
    private GlobalModel globalModel = GlobalModel.get_Instanst();
    public static Activity mActivity;

    NotificationManager nm;
    private Button scan, exit;
    static final int NOTIFICATION_ID = 0x123;
    private final static int SCANNIN_GREQUEST_CODE = 1;

    private String[] mPlanetTitles;
    private TextView Lefttv, titleName;
    private ImageCycleView mAdView;
    public static ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
    private ArrayList<String> bitmapsUrl = new ArrayList<String>();
    private ImageView card, mChangguan, mQrcode, mMessage, leftMenu;
    private Button mUpImage, mSerchImfo;
    private ImageView compangyInfo, changePass, help, Logout, About;
    private RelativeLayout back;
    private SharedPreferences mSharedPreferences;
    private String imageUrl1 = "http://192.168.16.86:8094/upload/img_icon/5d2a4264bad4e72e3bd7281bfe6d5366.jpg";
    private String imageUrl2 = "http://192.168.16.86:8094/upload/img_icon/d1fb5696b4b6fa5235e5808a29a3b89b.jpg";

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
        setContentView(R.layout.content_left);
        Drawable drawable = getResources().getDrawable(R.drawable.pic);
        bitmapsUrl.add(imageUrl1);
        bitmapsUrl.add(imageUrl2);
        System.out.println(imageUrl1);
        bitmaps.add(GetDrable.drawableToBitmap(drawable));
        bitmaps.add(GetDrable.drawableToBitmap(getResources().getDrawable(R.drawable.pic2)));
        mSharedPreferences = getSharedPreferences("LoginState", MODE_PRIVATE);
        initViews();
        initEvents();

//        ShowNotify("1");



    }


    private void initViews() {
        Lefttv = (TextView) findViewById(R.id.left_tv);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mAdView = (ImageCycleView) findViewById(R.id.ad_view);
        titleName = (TextView) findViewById(R.id.titlename);
        card = (ImageView) findViewById(R.id.mycard);
        mChangguan = (ImageView) findViewById(R.id.cg);
        mMessage = (ImageView) findViewById(R.id.messageinfo);
        mUpImage = (Button) findViewById(R.id.upimage);
        mSerchImfo = (Button) findViewById(R.id.serchinfo);
        leftMenu = (ImageView) findViewById(R.id.openbtn);
        mQrcode = (ImageView) findViewById(R.id.qrcode);

        this.compangyInfo = (ImageView) findViewById(R.id.qiye_iv);
        this.changePass = (ImageView) findViewById(R.id.change_pass);
        this.help = (ImageView) findViewById(R.id.help_iv);
        this.Logout = (ImageView) findViewById(R.id.logout_iv);
        this.About = (ImageView) findViewById(R.id.about_iv);
        this.exit = (Button) findViewById(R.id.exit_btn);

        Lefttv.setText(GlobalModel.get_Instanst().project_name);
        titleName.setText(GlobalModel.get_Instanst().project_name);
        this.back = (RelativeLayout) findViewById(R.id.rl_back);
        back.getBackground().setAlpha(80);
    }

    private void initEvents() {
        card.setOnClickListener(this);
        mChangguan.setOnClickListener(this);
        mMessage.setOnClickListener(this);
        mUpImage.setOnClickListener(this);
        mSerchImfo.setOnClickListener(this);
        leftMenu.setOnClickListener(this);
        mQrcode.setOnClickListener(this);
        exit.setOnClickListener(this);
        help.setOnClickListener(this);
        this.About.setOnClickListener(this);
        this.Logout.setOnClickListener(this);
        this.changePass.setOnClickListener(this);

        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                card.setClickable(false);
                mChangguan.setClickable(false);
                mMessage.setClickable(false);
                mUpImage.setClickable(false);
                mSerchImfo.setClickable(false);
                mQrcode.setClickable(false);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                card.setClickable(true);
                mChangguan.setClickable(true);
                mMessage.setClickable(true);
                mUpImage.setClickable(true);
                mSerchImfo.setClickable(true);
                mQrcode.setClickable(true);

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }


    private ImageCycleView.ImageCycleViewListener mAdCycleViewListener = new ImageCycleView.ImageCycleViewListener() {
        @Override
        public void onImageClick(int position, View imageView) {
            // TODO 单击图片处理事件

        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.serchinfo:
                Intent searchIntent = new Intent(HomeActivity.this, SearchInfoActivity.class);
                startActivity(searchIntent);
                break;
            case R.id.mycard:
                Intent it = new Intent(HomeActivity.this, MyQrCodeActivity.class);
                startActivity(it);
                break;
            case R.id.cg:
                Intent webIntent = new Intent(HomeActivity.this, WebviewActivity.class);
                webIntent.putExtra("url", "http://f.thinkorange.cn/3641");
                startActivity(webIntent);
                break;
            case R.id.messageinfo:
                Intent messageIntent = new Intent(HomeActivity.this, MeetingMessageActivity.class);
                startActivity(messageIntent);
                break;
            case R.id.upimage:
                Intent itImage = new Intent(HomeActivity.this, UpLoadActivity.class);
                startActivity(itImage);
                break;
            case R.id.qrcode:
                Intent intent = new Intent();
                intent.setClass(HomeActivity.this, MipcaActivityCapture.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
                break;
            case R.id.openbtn:
                mDrawerLayout.openDrawer(Gravity.LEFT);

                break;
            case R.id.exit_btn:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                break;
            case R.id.help_iv:
                Intent helpIntent = new Intent(HomeActivity.this, HelpActivity.class);
                startActivity(helpIntent);
                break;
            case R.id.logout_iv:
                SharedPreferences mPreferences = getSharedPreferences("LoginState",MODE_PRIVATE);
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.remove("pass");
                editor.remove("isChecked");
                editor.commit();
                Intent intent1 = new Intent(HomeActivity.this,LoginActivity.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.change_pass:
                Intent changeIntent = new Intent(HomeActivity.this,ChangePassWordActivity.class);
                startActivity(changeIntent);
                finish();
                break;
        }
    }

    public void OpenWebSocket() {
        if (mSocket != null && (!mSocket.hasListeners("new_msg") || !mSocket.hasListeners("event"))) {
            mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    mSocket.emit("login", "8|app" + "|" + mSharedPreferences.getString("count", ""));
                }
            }).on("event", new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                }
            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    mSocket.emit("login", "8|app" + "|" + mSharedPreferences.getString("count", ""));
                }
            }).on("new_msg", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (args[0] != null) {
                        System.out.println(args[0].toString());
                        try {
                            JSONObject object = new JSONObject(args[0].toString());
                            String cmd = object.getString("cmd");
                            if (cmd.equals("notice")) {
                                String time = TimeFormat.getStringDate();
                                ContentValues values = new ContentValues();
                                values.put("info", object.getString("content"));
                                values.put("title",time+" "+object.getString("title"));
                                SQLiteDatabase db = getDB();
                                db.insert("meeting", "info,title", values);

                                ShowNotify("有一条会议通知！",cmd);
                            } else if (cmd.equals("buylistupdate")) {
                                String category_id = object.getString("category_id");
                                if (category_id.equals(GlobalModel.get_Instanst().map_polygon_category_id)) {
                                    ShowNotify("有一条购物信息！",cmd);
                                }
//                                ContentValues values = new ContentValues();
//                                values.put("info", object.getString("shop"));
//                                SQLiteDatabase db = getDB();
//                                db.insert("shoping", "info", values);

                            }

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
        OpenWebSocket();

        mAdView.setImageResources(bitmaps, GlobalModel.get_Instanst().imageUrls, mAdCycleViewListener);
    }

    private void ShowNotify(String message,String cmd) {


        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent;
        if (cmd.equals("notice")) {
            intent = new Intent(HomeActivity.this,
                    MeetingMessageActivity.class);
        } else {
            intent = new Intent(HomeActivity.this,
                    SearchInfoActivity.class);
        }
        PendingIntent pi = PendingIntent.getActivity(
                HomeActivity.this, 0, intent, 0);

        Notification notify = new Notification.Builder(
                HomeActivity.this)
                // 设置打开该通知，该通知自动消失
                .setAutoCancel(true)
                        // 设置显示在状态栏的通知提示信息
                .setTicker("有新消息")
                        // 设置通知的图标
                .setSmallIcon(R.drawable.applogo)
                        // 设置通知内容的标题
                .setContentTitle("一条新通知")
                        // 设置通知内容
                .setContentText(message)
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
                System.out.println("result" + result + "");
                Intent it = new Intent(HomeActivity.this, WebviewActivity.class);
                it.putExtra("url", result);
                startActivity(it);
            }
                break;
        }
    }

    /**
     * 得到数据库
     *
     * @return SQLiteDatabase
     */
    public SQLiteDatabase getDB() {
        DBHelper dh = new DBHelper(this, "message.db", null, 1);
        SQLiteDatabase db = dh.getReadableDatabase();

        return db;

    }
}




