package cn.thinkorange.merchantsapp;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import Adapter.MeetingAdapter;
import VO.MeetingVO;

public class MeetingMessageActivity extends Activity implements MeetingAdapter.ListItemClick {

    private Button btn;
    private boolean isStart = false;
    private ListView mListView;
    private MeetingAdapter mAdapter;
    private List<MeetingVO> mList = new ArrayList<MeetingVO>();
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting_message);

        this.mListView = (ListView) findViewById(R.id.meeting_message);
        this.mImageView = (ImageView) findViewById(R.id.getback);

        this.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        for (int i = 0; i <= 3; i++) {
//            MeetingVO meetingVO = new MeetingVO("会议通知", "    5.30开会");
//            mList.add(meetingVO);
//        }

        SetInfo();

        mAdapter = new MeetingAdapter(this, mList, this);
        mListView.setAdapter(mAdapter);

    }

    private void SetInfo() {
        SQLiteDatabase db = getDB();
        Cursor cursor = db.query("meeting",null,null,null,null,null,null);
        while (cursor.moveToNext()) {
            String info = cursor.getString(cursor.getColumnIndex("info"));
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            MeetingVO meetingVO = new MeetingVO(title,info);
            mList.add(meetingVO);
        }
    }

    @Override
    public void onClick(View item, View widget, int position, int which) {
        TextView tv = (TextView) item.findViewById(R.id.meeting_body);
        if (tv.getVisibility() == View.VISIBLE) {
            tv.setVisibility(View.GONE);
        } else if (tv.getVisibility() == View.GONE) {
            tv.setVisibility(View.VISIBLE);
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
}