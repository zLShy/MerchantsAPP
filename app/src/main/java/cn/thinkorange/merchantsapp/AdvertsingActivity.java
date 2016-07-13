package cn.thinkorange.merchantsapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

public class AdvertsingActivity extends Activity {

    private Button btn;
    private boolean isStart = false;
    SharedPreferences mSharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_ad);

        this.btn = (Button) findViewById(R.id.btn);

        mSharedPreferences = getSharedPreferences("LoginState", MODE_PRIVATE);

    }


    @Override
    protected void onResume() {
        super.onResume();

        MyTimer myTimer = new MyTimer(AdvertsingActivity.this, 5000, 1000,
                btn);
        myTimer.start();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
//                if (IsFrt) {
                    startToLogin();
//                } else {
//                    startTohome();
//                }

            }
        }, 5000);

        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
//                if (IsFrt) {
                    startToLogin();
//                } else {
//                    startTohome();
//                }
            }
        });

    }

    private void startToLogin() {
        if (!isStart) {
            isStart = true;
            Intent it = new Intent();
            it.setClass(AdvertsingActivity.this, LoginActivity.class);
            startActivity(it);
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }

    }

    private void startTohome() {
        if (!isStart) {
            isStart = true;
            Intent it = new Intent();
            it.setClass(AdvertsingActivity.this, LoginActivity.class);
            startActivity(it);
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }

    }
}