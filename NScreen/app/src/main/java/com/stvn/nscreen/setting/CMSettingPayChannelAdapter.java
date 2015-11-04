package com.stvn.nscreen.setting;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.ListViewDataObject;
import com.stvn.nscreen.R;
import com.stvn.nscreen.util.CMLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 설정화면 > 유료채널 안내 > 리스트 어댑터
 * Created by kimwoodam on 2015. 9. 30..
 */

public class CMSettingPayChannelAdapter extends ArrayAdapter<ListViewDataObject> {
    LayoutInflater mInflater;
    private Context mContext;

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    public CMSettingPayChannelAdapter(Context context, ArrayList<ListViewDataObject> items) {
        super(context, 0, items);
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mRequestQueue = Volley.newRequestQueue(mContext);
        this.mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(100);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });
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
            holder.channelImage = (NetworkImageView)convertView.findViewById(R.id.setting_pay_channel_item_image);
            holder.channelName = (TextView)convertView.findViewById(R.id.setting_pay_channel_item_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ListViewDataObject info = getItem(position);

        try {
            JSONObject jsonObj = new JSONObject(info.sJson);

            holder.channelName.setText(jsonObj.getString("channelName"));
            holder.channelImage.setImageUrl(jsonObj.getString("channelLogoImg"), mImageLoader);

            CMLog.d("Pay Channel Data", "[ " + position + " ]---------");
            CMLog.d("Pay Channel Data", jsonObj.getString("channelId"));
            CMLog.d("Pay Channel Data", jsonObj.getString("channelNumber"));
            CMLog.d("Pay Channel Data", jsonObj.getString("channelName"));
            CMLog.d("Pay Channel Data", jsonObj.getString("channelInfo"));
            CMLog.d("Pay Channel Data", jsonObj.getString("channelOnAirHD"));
            CMLog.d("Pay Channel Data", jsonObj.getString("channelLogoImg"));
            CMLog.d("Pay Channel Data", jsonObj.getString("channelProgramOnAirID"));
            CMLog.d("Pay Channel Data", jsonObj.getString("channelProgramOnAirTime"));
            CMLog.d("Pay Channel Data", jsonObj.getString("channelView"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    private class ViewHolder {
        NetworkImageView channelImage;
        TextView channelName;
    }
}