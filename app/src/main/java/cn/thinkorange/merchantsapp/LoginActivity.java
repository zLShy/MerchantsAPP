package cn.thinkorange.merchantsapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import CoreService.BasicNameValuePartner;
import CoreService.CallBacks;
import CoreService.CallServiceAsyncTask;
import Model.GlobalModel;

public class LoginActivity extends Activity implements View.OnClickListener {

    private CheckBox keepPass;
    private Button Submit;
    private EditText userName, passWord;
    private String count, pass;
    private SharedPreferences mSharedPreferences;
    private boolean isChecked;

    boolean IsFrt = true;
    private Dialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mSharedPreferences = getSharedPreferences("LoginState", MODE_PRIVATE);
        count = mSharedPreferences.getString("count", "");
        pass = mSharedPreferences.getString("pass", "");
        isChecked = mSharedPreferences.getBoolean("isChecked", false);
        IsFrt = mSharedPreferences.getBoolean("IsFrt", true);
        initViews();

        initEvents();
    }

    private void initEvents() {

        this.Submit.setOnClickListener(this);
    }

    private void initViews() {
        this.keepPass = (CheckBox) findViewById(R.id.keeppass);
        this.Submit = (Button) findViewById(R.id.submit);
        this.userName = (EditText) findViewById(R.id.username);
        this.passWord = (EditText) findViewById(R.id.pass);

        userName.setText(count);
        passWord.setText(pass);

        if (isChecked) {
            keepPass.setChecked(true);
        } else {
            keepPass.setChecked(false);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.submit:
                LoginTask();
//                if (keepPass.isChecked()) {
//
//                    count = userName.getText().toString().trim();
//                    pass = passWord.getText().toString().trim();
//                    Toast.makeText(getApplicationContext(), "记住密码", Toast.LENGTH_LONG).show();
//                    Intent it = new Intent(LoginActivity.this, ChangePassActivity.class);
//                    startActivity(it);
//                } else {
//                    Toast.makeText(getApplicationContext(), "不记住密码", Toast.LENGTH_LONG).show();
//                }
                break;
        }
    }


    private void LoginTask() {
        ArrayList<BasicNameValuePartner> data = new ArrayList<BasicNameValuePartner>();
        count = userName.getText().toString().trim();
        pass = passWord.getText().toString().trim();
        data.add(new BasicNameValuePartner("username", count));
        data.add(new BasicNameValuePartner("password", pass));
        CallServiceAsyncTask loginTask = new CallServiceAsyncTask(LoginActivity.this, data, "uesrs/login", new CallBacks() {
            @Override
            public void OnResult(Object o) {
                if (o != null) {
                    pd.cancel();
                    if (o.toString().equals("")) {
                        Toast.makeText(getApplication(), "网络不给力~", Toast.LENGTH_SHORT).show();
                    } else {
                        System.out.println(o.toString());
                        try {
                            JSONObject object = new JSONObject(o.toString());
                            String data = object.getString("data");
                            JSONObject object1 = new JSONObject(data);
                            GlobalModel.get_Instanst().project_name = object1.getString("project_name");
                           String imageList = object1.getString("imglist");
                            if(!imageList.equals("")) {
                                GlobalModel.get_Instanst().imageUrls.clear();
                                String str = imageList.replace("[","");
                                String str1 = str.replace("]","");
                                String imageLists[] = str1.split(",");
                                for(int i = 0;i<imageLists.length;i++) {
                                    String images = imageLists[i];
                                    String image = images.replace("\\","");
                                    String im = image.replace("\"","");
                                    System.out.println(im);
                                    GlobalModel.get_Instanst().imageUrls.add(im);
                                }
                            }
                            String Message = object1.getString("message");
                            if (!Message.equals("")) {
                                JSONObject MessageObject = new JSONObject(Message);
                                GlobalModel.get_Instanst().hasMessage = true;
                                GlobalModel.get_Instanst().company_email = MessageObject.getString("company_email");
                                GlobalModel.get_Instanst().company_web = MessageObject.getString("company_web");
                                GlobalModel.get_Instanst().company_tel = MessageObject.getString("company_name");
                                GlobalModel.get_Instanst().company_name = MessageObject.getString("company_tel");
                                GlobalModel.get_Instanst().company_fax = MessageObject.getString("company_fax");
                                GlobalModel.get_Instanst().company_add = MessageObject.getString("company_add");
                                String intro = MessageObject.getString("company_intro");
                                GlobalModel.get_Instanst().company_intro = intro.replace("<br>","");
                                GlobalModel.get_Instanst().map_polygon_category_id = MessageObject.getString("map_polygon_category_id");
                            }
                            JSONArray array = object1.getJSONArray("category");
                            GlobalModel.get_Instanst().types = new String[array.length()];
                            GlobalModel.get_Instanst().typesId = new String[array.length()];
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object2 = array.getJSONObject(i);
                                GlobalModel.get_Instanst().types[i] = object2.getString("map_polygon_category_name");
                                GlobalModel.get_Instanst().typesId[i] = object2.getString("map_polygon_category_id");
                            }
                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                            editor.putString("count", count);
                            if(keepPass.isChecked()) {
                                editor.putString("pass", pass);
                            }
                            editor.putBoolean("isChecked", keepPass.isChecked());
                            editor.commit();
                            if (IsFrt) {
                                Intent intent = new Intent(LoginActivity.this, ChangePassActivity.class);
                                GlobalModel.get_Instanst().userName = count;
                                intent.putExtra("count", count);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                GlobalModel.get_Instanst().userName = count;
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

        pd = new Dialog(LoginActivity.this, R.style.new_circle_progress);
        pd.setContentView(R.layout.layout_progressbar);
        pd.setCancelable(true);
        pd.show();
        loginTask.doAsyncTask();
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        GlobalModel.get_Instanst().imageUrls.clear();
//    }
}