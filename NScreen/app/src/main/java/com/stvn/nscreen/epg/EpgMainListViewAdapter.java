package com.stvn.nscreen.epg;

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

import java.util.ArrayList;

/**
 * Created by swlim on 2015. 9. 11..
 */

public class EpgMainListViewAdapter extends BaseAdapter {

    private static final String                        tag              = EpgMainListViewAdapter.class.getSimpleName();
    private              Context                       mContext         = null;
    private              View.OnClickListener          mOnClickListener = null;
    private              ArrayList<ListViewDataObject> mDatas           = new ArrayList<ListViewDataObject>();

    private              RequestQueue                  mRequestQueue;
    private              ImageLoader                   mImageLoader;

    public EpgMainListViewAdapter(Context c, View.OnClickListener onClickListener) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_epg_main, parent, false);
        }

        try {
            ListViewDataObject dobj         = (ListViewDataObject)getItem(position);
            JSONObject         jobj         = new JSONObject(dobj.sJson);

            // ImageView channelLogo = (NetworkImageView)convertView.findViewById(R.id.epg_main_imagebutton_channel_logo);
            ImageView channelLogo           = (NetworkImageView) ViewHolder.get(convertView, R.id.epg_main_imagebutton_channel_logo);

            ImageView favoriteImageView     = ViewHolder.get(convertView, R.id.epg_main_imagebutton_favorite);
            TextView  channelNumberTextView = ViewHolder.get(convertView, R.id.epg_main_textview_channel_number);
            TextView  titleTextView         = ViewHolder.get(convertView, R.id.epg_main_textview_program_title);

            ViewHolder.channelLogo.setImageUrl(jobj.getString("channelLogoImg"), mImageLoader);

            channelNumberTextView.setText(jobj.getString("channelNumber"));
            titleTextView.setText(jobj.getString("channelProgramOnAirTitle"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
