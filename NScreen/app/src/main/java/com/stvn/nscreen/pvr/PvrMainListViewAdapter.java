package com.stvn.nscreen.pvr;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
 * Created by limdavid on 15. 9. 15..
 */

public class PvrMainListViewAdapter extends BaseAdapter {

    private static final String                        tag              = PvrMainListViewAdapter.class.getSimpleName();
    private              Context                       mContext         = null;
    private              View.OnClickListener          mOnClickListener = null;
    private              ArrayList<ListViewDataObject> mDatas           = new ArrayList<ListViewDataObject>();

    private              RequestQueue                  mRequestQueue;
    private              ImageLoader                   mImageLoader;

    public PvrMainListViewAdapter(Context c, View.OnClickListener onClickListener) {
        super();

        this.mContext         = c;
        this.mOnClickListener = onClickListener;
        this.mRequestQueue    = Volley.newRequestQueue(mContext);
        this.mImageLoader     = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(100);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });
    }

    /**
     * Swipe menu ListView
     */
    @Override
    public int getViewTypeCount() {
        // menu type count
        return 3;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_pvr_main, parent, false);
        }

        try {
            ListViewDataObject dobj          = (ListViewDataObject)getItem(position);
            JSONObject         jobj          = new JSONObject(dobj.sJson);

            TextView           titleTextView = ViewHolder.get(convertView, R.id.pvr_main_textview_program_title);
            NetworkImageView   channelLogo   = ViewHolder.get(convertView, R.id.pvr_main_imagebutton_channel_logo);

            titleTextView.setText(jobj.getString("ProgramName"));
            channelLogo.setImageUrl(jobj.getString("Channel_logo_img"), mImageLoader);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }
}