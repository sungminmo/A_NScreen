package com.stvn.nscreen.vod;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.JYSharedPreferences;
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
 * Created by swlim on 2015. 9. 11..
 */

public class VodMainOtherListViewAdapter extends BaseAdapter {

    private static final String                        tag              = VodMainOtherListViewAdapter.class.getSimpleName();
    private              Context                       mContext         = null;
    private              JYSharedPreferences           mPref;
    private              View.OnClickListener          mOnClickListener = null;
    private              ArrayList<ListViewDataObject> mDatas           = new ArrayList<ListViewDataObject>();


    public VodMainOtherListViewAdapter(Context c, View.OnClickListener onClickListener) {
        super();

        this.mContext         = c;
        this.mOnClickListener = onClickListener;
        this.mPref            = new JYSharedPreferences(c);
    }


    /**
     * ListView
     * @return
     */
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_vod_main_other, parent, false);
        }

        try {
            ListViewDataObject dobj                    = (ListViewDataObject)getItem(position);
            JSONObject         jo                     = new JSONObject(dobj.sJson);

            String  categoryName     = jo.getString("categoryName");
            boolean leaf             = jo.getBoolean("leaf");
            boolean isOpened         = jo.getBoolean("isOpened");
            boolean is2Depth         = false;
            boolean is3Depth         = false;
            if ( ! jo.isNull("is2Depth") ) {
                is2Depth = jo.getBoolean("is2Depth");
            }
            if ( ! jo.isNull("is3Depth") ) {
                is3Depth = jo.getBoolean("is3Depth");
            }

            ImageView depth1ImageView = (ImageView)ViewHolder.get(convertView, R.id.vod_main_other_caterory_list_depth1_imageview);
            ImageView depth2ImageView = (ImageView)ViewHolder.get(convertView, R.id.vod_main_other_caterory_list_depth2_imageview);
            TextView textview = ViewHolder.get(convertView, R.id.vod_main_other_categoty_list_textview);
            ImageView imageView1 = (ImageView)ViewHolder.get(convertView, R.id.vod_main_other_categoty_list_imageview1);
            ImageView imageView2 = (ImageView)ViewHolder.get(convertView, R.id.vod_main_other_categoty_list_imageview2);

            // 왼쪽의 폴더 접기/펴기 이미지 처리.
            if ( is2Depth == true ) {
                depth1ImageView.setVisibility(View.VISIBLE);
                depth2ImageView.setVisibility(View.GONE);
            } else if ( is3Depth == true ) {
                depth1ImageView.setVisibility(View.GONE);
                depth2ImageView.setVisibility(View.VISIBLE);
            } else {
                depth1ImageView.setVisibility(View.GONE);
                depth2ImageView.setVisibility(View.GONE);
            }

            // 오른쪽의 화살표처리.
            if ( leaf == true ) {
                imageView1.setVisibility(View.GONE);
                imageView2.setVisibility(View.GONE);
            } else {
                if ( is2Depth == true ) {
                    imageView1.setVisibility(View.VISIBLE);
                    imageView2.setVisibility(View.GONE);
                    if ( isOpened == true ) {
                        imageView1.setImageResource(R.mipmap.depth1_focus_arrow);
                    } else {
                        imageView1.setImageResource(R.mipmap.depth1_arrow);
                    }
                } else if ( is3Depth == true ) {
                    imageView1.setVisibility(View.GONE);
                    imageView2.setVisibility(View.GONE);
                    if ( isOpened == true ) {
                        imageView1.setImageResource(R.mipmap.depth2_focus_arrow);
                    } else {
                        imageView1.setImageResource(R.mipmap.depth2_arrow);
                    }
                } else {
                    imageView1.setVisibility(View.GONE);
                    imageView2.setVisibility(View.VISIBLE);
                    if ( isOpened == true ) {
                        imageView2.setImageResource(R.mipmap.depth1_focus_arrow);
                    } else {
                        imageView2.setImageResource(R.mipmap.depth1_arrow);
                    }
                }

            }

            textview.setText(categoryName);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
