package cn.thinkorange.merchantsapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import CoreService.BasicNameValuePartner;
import CoreService.CallBacks;
import CoreService.CallServiceAsyncTask;
import Model.GlobalModel;


public class ChangePassActivity extends Activity {

    private EditText firstEt, secEt;
    private Button submit;

    private String frtPass, secPass,count;
    private Dialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_pass);
        Intent intent = getIntent();
        count = intent.getStringExtra("count");
        initViews();

    }

    private void initViews() {
        this.firstEt = (EditText) findViewById(R.id.firstpass);
        this.secEt = (EditText) findViewById(R.id.secpass);
        this.submit = (Button) findViewById(R.id.pass_change_submit);


        this.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frtPass = firstEt.getText().toString().trim();
                secPass = secEt.getText().toString().trim();

                if (frtPass.equals(secPass)) {
                    System.out.println(GlobalModel.get_Instanst().project_name);
                    ChangePsaa();
//                    Intent it = new Intent(ChangePassActivity.this, HomeActivity.class);
//                    startActivity(it);
                }else {
                    Toast.makeText(ChangePassActivity.this,"两次密码输入不一致!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void ChangePsaa() {
        ArrayList<BasicNameValuePartner> data = new ArrayList<BasicNameValuePartner>();
        data.add(new BasicNameValuePartner("username", count));
        data.add(new BasicNameValuePartner("password", frtPass));
        CallServiceAsyncTask ChangeTask = new CallServiceAsyncTask(ChangePassActivity.this, data, "uesrs/changepass", new CallBacks() {
            @Override
            public void OnResult(Object o) {
                if(o != null) {
                    pd.cancel();
                    if(o.toString().equals("")) {
                        Toast.makeText(getApplicationContext(),"网络不给力~",Toast.LENGTH_SHORT).show();

                    }else {
                        System.out.println(o.toString());
                        try {
                            JSONObject object = new JSONObject(o.toString());
                            if("0".equals(object.getString("errcode"))) {
                                SharedPreferences mSharedPreferences = getSharedPreferences("LoginState",MODE_PRIVATE);
                                SharedPreferences.Editor editor = mSharedPreferences.edit();
                                editor.putBoolean("IsFrt", false);
                                if(mSharedPreferences.getBoolean("isChecked",false)) {
                                    editor.putString("pass",frtPass);
                                }

                                editor.commit();
                                Intent intent = new Intent(ChangePassActivity.this,HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void OnFault(Object o) {

            }
        });

        pd = new Dialog(ChangePassActivity.this, R.style.new_circle_progress);
        pd.setContentView(R.layout.layout_progressbar);
        pd.setCancelable(true);
        pd.show();
        ChangeTask.doAsyncTask();
    }
}




