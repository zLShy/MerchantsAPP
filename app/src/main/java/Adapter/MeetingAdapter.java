package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import VO.MeetingVO;
import cn.thinkorange.merchantsapp.R;

/**
 * Created by Administrator on 2016/6/27.
 */
public class MeetingAdapter extends BaseAdapter {
    private List<MeetingVO> mArrayList;
    private LayoutInflater mInflater;
    private ListItemClick callback;

    public MeetingAdapter(Context context, List<MeetingVO> list, ListItemClick callback) {
        this.mInflater = LayoutInflater.from(context);
        this.mArrayList = list;
        this.callback = callback;
    }

    @Override
    public int getCount() {
        return mArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        MeetingVO meetingVO = mArrayList.get(position);
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.message_list_item, null);
            holder.title = (TextView) convertView.findViewById(R.id.meeting_item_tv);
            holder.body = (TextView) convertView.findViewById(R.id.meeting_body);
            holder.mRelativeLayout = (RelativeLayout) convertView.findViewById(R.id.meeting_list_rl);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(meetingVO.getTitle());
        holder.body.setText(meetingVO.getBody());

        final View view = convertView;
        final int p = position;
        final int one = holder.mRelativeLayout.getId();
        holder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                callback.onClick(view, parent, p, one);
            }
        });
        return convertView;
    }


    static class ViewHolder {
        TextView title;
        TextView body;
        RelativeLayout mRelativeLayout;
    }

    public interface ListItemClick {
        void onClick(View item, View widget, int position, int which);
    }
}
