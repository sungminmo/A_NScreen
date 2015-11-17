package com.stvn.nscreen.setting.notice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.jjiya.android.common.ListViewDataObject;
import com.stvn.nscreen.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 설정화면 > 공지사항 > 리스트 어댑터
 * Created by kimwoodam on 2015. 11. 15..
 */
public class CMSettingNoticeAdapter extends ArrayAdapter<ListViewDataObject> {
    LayoutInflater mInflater;
    private Context mContext;

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    public CMSettingNoticeAdapter(Context context, ArrayList<ListViewDataObject> items) {
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
            convertView = this.mInflater.inflate(R.layout.listview_setting_notice_item, null);
            holder = new ViewHolder();
            holder.noticeName = (TextView)convertView.findViewById(R.id.setting_notice_item_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ListViewDataObject info = getItem(position);

        try {
            JSONObject jsonObj = new JSONObject(info.sJson);
            holder.noticeName.setText(jsonObj.getString("notice_Title"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    private class ViewHolder {
        TextView noticeName;
    }
}