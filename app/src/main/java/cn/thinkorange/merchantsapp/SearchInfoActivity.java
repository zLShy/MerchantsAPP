package cn.thinkorange.merchantsapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Adapter.SearchAdapter;
import CoreService.BasicNameValuePartner;
import CoreService.CallBacks;
import CoreService.CallServiceAsyncTask;
import Model.GlobalModel;
import VO.SearchVO;

public class SearchInfoActivity extends Activity implements SearchAdapter.ListItemClick {


    private Button goBack;
    private ListView mListView;
    private SearchAdapter mAdapter;
    private Dialog pd;
    private List<SearchVO> mList = new ArrayList<SearchVO>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        initViews();
        GetBuyInfo();


//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(SearchInfoActivity.this, DetailsActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("Item", mList.get(position));
//                intent.putExtras(bundle);
//                startActivity(intent);
//            }
//        });

    }

    private void initViews() {
        this.goBack = (Button) findViewById(R.id.goback_btn);
        this.mListView = (ListView) findViewById(R.id.search_lv);

        this.goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public void onClick(View item, View widget, int position, int which) {
        Intent intent = new Intent(SearchInfoActivity.this, DetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("Item", mList.get(position));
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    private void SetShopInfo() {
        SQLiteDatabase db = getDB();
        Cursor cursor = db.query("shoping", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String msg = cursor.getString(cursor.getColumnIndex("info"));
            try {
                JSONObject object = new JSONObject(msg);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 得到数据库
     *
     * @return
     */
    public SQLiteDatabase getDB() {
        DBHelper dh = new DBHelper(this, "message.db", null, 1);
        SQLiteDatabase db = dh.getReadableDatabase();

        return db;

    }


    private void GetBuyInfo() {

//        AsyncHttpClient client = new AsyncHttpClient();
//        RequestParams params = new RequestParams();
//        params.put("map_polygon_category_id", GlobalModel.get_Instanst().map_polygon_category_id);
//        params.put("timestamp",MilliTime_Ten().toString());
//        params.put("from","android");
//        params.put("sign","app");
//        client.post("http://192.168.16.86:8091/users/buyingleads", params, new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                String body = new String(responseBody);
//                if (body != null) {
//                    System.out.println(body);
//                    try {
//                        JSONObject object = new JSONObject(body);
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                Toast.makeText(getApplication(), "网络不给力~", Toast.LENGTH_SHORT).show();
//            }
//        });
        ArrayList<BasicNameValuePartner> data = new ArrayList<BasicNameValuePartner>();
        data.add(new BasicNameValuePartner("map_polygon_category_id", GlobalModel.get_Instanst().map_polygon_category_id));
        CallServiceAsyncTask getBuyTask = new CallServiceAsyncTask(SearchInfoActivity.this, data, "users/buyingleads", new CallBacks() {
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
                            JSONArray  array = object1.getJSONArray("list");
                            for (int i = 0;i<array.length();i++){
                                JSONObject item = array.getJSONObject(i);
                                String amount = item.getString("amount");
                                String buyer_tel = item.getString("buyer_tel");
                                String buyer_goods_name = item.getString("buyer_goods_name");
                                String buyer_email = item.getString("buyer_email");
                                String buyer_name = item.getString("buyer_name");
                                String buyer_intro = item.getString("buyer_intro");
                                String goods_create_at = item.getString("goods_create_at");
                                SearchVO searchVO = new SearchVO(amount, goods_create_at, buyer_goods_name, buyer_intro, buyer_name, buyer_tel, buyer_email);
                                mList.add(searchVO);
                            }
                            mAdapter = new SearchAdapter(SearchInfoActivity.this, mList, SearchInfoActivity.this);
                            mListView.setAdapter(mAdapter);

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
        pd = new Dialog(SearchInfoActivity.this, R.style.new_circle_progress);
        pd.setContentView(R.layout.layout_progressbar);
        pd.setCancelable(true);
        pd.show();
        getBuyTask.doAsyncTask();
//
    }

    public static String MilliTime_Ten() {
        // 获取系统的时间
        long time = System.currentTimeMillis();
        String re = (time + "").substring(0, 10);
        return re;
    }
}