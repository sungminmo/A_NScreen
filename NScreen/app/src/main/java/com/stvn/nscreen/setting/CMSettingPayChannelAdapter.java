package com.stvn.nscreen.setting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.stvn.nscreen.R;

import java.util.ArrayList;

/**
 * 설정화면 > 유료채널 안내 > 리스트 어댑터
 * Created by kimwoodam on 2015. 9. 30..
 */
public class CMSettingPayChannelAdapter extends ArrayAdapter<Object> {
    LayoutInflater mInflater;
    private Context mContext;

    public CMSettingPayChannelAdapter(Context context, ArrayList<Object> items) {
        super(context, 0, items);
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = this.mInflater.inflate(R.layout.listview_setting_pay_channel_item, null);
            holder = new ViewHolder();
            holder.channelImage = (ImageView)convertView.findViewById(R.id.setting_pay_channel_item_image);
            holder.channelName = (TextView)convertView.findViewById(R.id.setting_pay_channel_item_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        Object info = getItem(position);
        if (info != null) {
            holder.channelName.setText((String)info);
        }

        return convertView;
    }

    private class ViewHolder {
        ImageView channelImage;
        TextView channelName;
    }
}