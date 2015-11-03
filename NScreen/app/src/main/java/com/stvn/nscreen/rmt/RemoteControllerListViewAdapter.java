package com.stvn.nscreen.rmt;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.ListViewDataObject;
import com.jjiya.android.common.ViewHolder;
import com.stvn.nscreen.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by limdavid on 15. 10. 26..
 */
public class RemoteControllerListViewAdapter extends BaseAdapter {

    private static final String                        tag              = RemoteControllerListViewAdapter.class.getSimpleName();
    private              Context                       mContext         = null;
    private              View.OnClickListener          mOnClickListener = null;
    private              ArrayList<ListViewDataObject> mDatas           = new ArrayList<ListViewDataObject>();

    private              RequestQueue                  mRequestQueue;
    private              ImageLoader                   mImageLoader;

    private              int                           mSelectedIndex   = -1;

    public RemoteControllerListViewAdapter(Context c, View.OnClickListener onClickListener) {
        super();

        this.mContext         = c;
        this.mOnClickListener = onClickListener;
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
    public int getCount() { return mDatas.size(); }

    @Override
    public Object getItem(int position) { return mDatas.get(position); }

    @Override
    public long getItemId(int position) { return mDatas.get(position).iKey; }

    public void set(int position, ListViewDataObject obj) { mDatas.set(position, obj); }
    public void addItem(ListViewDataObject obj) { mDatas.add(obj); }
    public void remove(int position) { mDatas.remove(position); }
    public void clear() { mDatas.clear(); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_remote_controller, parent, false);
        }

        try {
            ListViewDataObject dobj         = (ListViewDataObject)getItem(position);
            JSONObject         jobj         = new JSONObject(dobj.sJson);

            String sProgramAge  = jobj.getString("channelProgramGrade");
            String sChannelInfo = jobj.getString("channelInfo");

            NetworkImageView channelLogo = (NetworkImageView) ViewHolder.get(convertView, R.id.remote_imageview_channel_logo);

            ImageView programAge  = ViewHolder.get(convertView, R.id.remote_imageview_program_age);
            ImageView Info        = ViewHolder.get(convertView, R.id.remote_imageview_program_hdsd);
            ImageView favoriteImageView     = ViewHolder.get(convertView, R.id.remote_imagebutton_favorite);
            TextView  channelNumberTextView = ViewHolder.get(convertView, R.id.remote_textview_channel_number);
            TextView  titleTextView         = ViewHolder.get(convertView, R.id.remote_textview_program_title);
            TextView programTimeTextView = ViewHolder.get(convertView, R.id.remote_textview_program_time);



            channelNumberTextView.setText(jobj.getString("channelNumber"));
            titleTextView.setText(jobj.getString("channelProgramOnAirTitle"));
            channelLogo.setImageUrl(jobj.getString("channelLogoImg"), mImageLoader);

            if ( "모두 시청".equals(sProgramAge) ) {
                programAge.setImageResource(R.mipmap.btn_age_all);
            } else if ("7세 이상".equals(sProgramAge) ) {
                programAge.setImageResource(R.mipmap.btn_age_7);
            } else if ("12세 이상".equals(sProgramAge) ) {
                programAge.setImageResource(R.mipmap.btn_age_12);
            } else if ("15세 이상".equals(sProgramAge) ) {
                programAge.setImageResource(R.mipmap.btn_age_15);
            } else if ("19세 이상".equals(sProgramAge) ) {
                programAge.setImageResource(R.mipmap.btn_age_19);
            }

            if ( "SD".equals(sChannelInfo) ) {
                Info.setImageResource(R.mipmap.btn_size_sd);
            } else if ("HD".equals(sChannelInfo) || "SD,HD".equals(sChannelInfo) ) {
                Info.setImageResource(R.mipmap.btn_size_hd);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }
    public void setSelectedIndex(int position){ mSelectedIndex = position; }
    public int  getSelectedIndex() { return mSelectedIndex; }
}
