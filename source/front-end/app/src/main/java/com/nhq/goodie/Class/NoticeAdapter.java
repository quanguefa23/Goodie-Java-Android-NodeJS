package com.nhq.goodie.Class;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nhq.goodie.Class.Notice;
import com.nhq.goodie.R;

import java.util.List;

public class NoticeAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<Notice> listNotice;

    public NoticeAdapter(Context context, int layout, List<Notice> listNotice) {
        this.context = context;
        this.layout = layout;
        this.listNotice = listNotice;
    }

    @Override
    public int getCount() {
        return listNotice.size();
    }

    @Override
    public Object getItem(int i) {
        return listNotice.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    private class ViewHolder {
        ImageView typeIV;
        TextView contentTV;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout, null);
            holder = new ViewHolder();

            //map
            holder.typeIV = view.findViewById(R.id.type);
            holder.contentTV = view.findViewById(R.id.content);

            view.setTag(holder);
        }
        else {
            holder = (ViewHolder) view.getTag();
        }

        //get value
        Notice temp = listNotice.get(i);
        boolean mark = temp.getMark();
        if (mark) {
            view.setBackgroundResource(R.color.blue_background);
        }
        else {
            view.setBackgroundResource(R.color.white);
        }

        int type = temp.getType();
        switch (type) {
            case 0: {
                holder.typeIV.setImageResource(R.drawable.comment);
                holder.contentTV.setText(temp.getFrom() + " đã bình luận về sản phẩm của bạn");
                break;
            }
            case 1: {
                holder.typeIV.setImageResource(R.drawable.comment);
                holder.contentTV.setText(temp.getFrom() + " đã trả lời bình luận của bạn");
                break;
            }
            case 2: {
                holder.typeIV.setImageResource(R.drawable.no);
                holder.contentTV.setText("Sản phẩm rao bán của bạn đã bị xóa");
                break;
            }
            case 3: {
                holder.typeIV.setImageResource(R.drawable.yes);
                holder.contentTV.setText("Sản phẩm của bạn đã được duyệt");
                break;
            }
        }

        return view;
    }
}
