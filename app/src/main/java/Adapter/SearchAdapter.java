package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import VO.SearchVO;
import cn.thinkorange.merchantsapp.R;

/**
 * Created by Administrator on 2016/6/27.
 */
public class SearchAdapter extends BaseAdapter {
    private List<SearchVO> mArrayList;
    private LayoutInflater mInflater;
    private ListItemClick callback;

    public SearchAdapter(Context context, List<SearchVO> list,ListItemClick callback) {
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
        SearchVO searchVO = mArrayList.get(position);
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.search_list_item, null);
            holder.count = (TextView) convertView.findViewById(R.id.shop_count);
            holder.time = (TextView) convertView.findViewById(R.id.datetv);
            holder.shopName = (TextView) convertView.findViewById(R.id.shop_name_tv);
            holder.mRelativeLayout = (RelativeLayout) convertView.findViewById(R.id.search_list_rl);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.count.setText(searchVO.getCount());
        holder.time.setText(searchVO.getTime());
        holder.shopName.setText(searchVO.getType());
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
//133380+36000+16000

    static class ViewHolder {
        TextView count;
        TextView time;
        TextView shopName;
        RelativeLayout mRelativeLayout;
    }

    public interface ListItemClick {
        void onClick(View item, View widget, int position, int which);
    }
}
