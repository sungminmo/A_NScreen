package com.stvn.nscreen.setting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.stvn.nscreen.R;

import java.util.ArrayList;

/**
 * 설정화면 > 지역설정 > 리스트 어댑터
 * Created by kimwoodam on 2015. 9. 30..
 */
public class CMSettingRegionAdapter extends ArrayAdapter<Object> {
    LayoutInflater mInflater;
    private Context mContext;

    public CMSettingRegionAdapter(Context context, ArrayList<Object> items) {
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
            convertView = this.mInflater.inflate(R.layout.listview_setting_region_item, null);
            holder = new ViewHolder();
            holder.regionName = (TextView)convertView.findViewById(R.id.setting_region_item_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        Object info = getItem(position);
        if (info != null) {
            holder.regionName.setText((String)info);
        }

        return convertView;
    }

    private class ViewHolder {
        TextView regionName;
    }
}
