package com.stvn.nscreen.setting;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jjiya.android.common.ListViewDataObject;
import com.stvn.nscreen.R;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 설정화면 > 지역설정 > 리스트 어댑터
 * Created by kimwoodam on 2015. 9. 30..
 */

public class CMSettingRegionAdapter extends ArrayAdapter<ListViewDataObject> {
    LayoutInflater mInflater;
    private Context mContext;
    private String mSelectedAreaCode;
    private String mSelectedAreaName;

    public CMSettingRegionAdapter(Context context, ArrayList<ListViewDataObject> items) {
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
            holder.selectedImage = (ImageView)convertView.findViewById(R.id.setting_region_item_check_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ListViewDataObject info = getItem(position);

        try {
            JSONObject jsonObj = new JSONObject(info.sJson);
            String areaName = jsonObj.getString("areaName");
            String areaCode = jsonObj.getString("areaCode");
            holder.regionName.setText(areaName);

            if (TextUtils.isEmpty(this.mSelectedAreaCode) == false && areaCode.equals(this.mSelectedAreaCode)) {
                holder.selectedImage.setVisibility(View.VISIBLE);

                this.mSelectedAreaName = areaName;
                this.mSelectedAreaCode = areaCode;

            } else {
                holder.selectedImage.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private class ViewHolder {
        TextView regionName;
        ImageView selectedImage;
    }

    public void changeSelectedRegion(String areaCode, String areaName) {
        this.mSelectedAreaCode = areaCode;
        this.mSelectedAreaName = areaName;
        notifyDataSetChanged();
    }

    public String getRegionName() {
        if (TextUtils.isEmpty(this.mSelectedAreaName)) {
            this.mSelectedAreaName = "";
        }
        return this.mSelectedAreaName;
    }

    public String getRegionCode() {
        if (TextUtils.isEmpty(this.mSelectedAreaCode)) {
            this.mSelectedAreaCode = "";
        }
        return this.mSelectedAreaCode;
    }
}
