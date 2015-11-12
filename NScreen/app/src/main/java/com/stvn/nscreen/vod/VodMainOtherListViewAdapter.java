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

        Date dt = new Date();

        SimpleDateFormat       formatter              = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat       formatter2             = new SimpleDateFormat("HH:mm");

        try {
            ListViewDataObject dobj                    = (ListViewDataObject)getItem(position);
            JSONObject         jo                     = new JSONObject(dobj.sJson);

            String  categoryId       = jo.getString("categoryId");
            String  categoryName     = jo.getString("categoryName");
            boolean leaf             = jo.getBoolean("leaf");
            String  parentCategoryId = jo.getString("parentCategoryId");
            String  viewerType       = jo.getString("viewerType");

//            Log.d("category", position + ": categoryId: " + categoryId + ", categoryName: " + categoryName + ", leaf: " + leaf + ", parentCategoryId: " + parentCategoryId + ", viewerType: " + viewerType);
            // {"adultCategory":false,"categoryId":"1144335","categoryName":"인기영화TOP20","description":"","externalId":"","imageFileName":"",
            // "leaf":true,"linkInterfaceId":"","linkedBannerIdList":[],"menuType":0,"orientationType":"V","packageDescription":"","packageDisplayPrice":-1,
            // "packageId":"","packageLink":false,"packageProductId":"","packageUIName":"","parentCategoryId":"27282","presentationType":"",
            // "seriesId":"","seriesLink":false,"seriesName":"","subCategoryPcgView":"N","subCategoryPresentationType":"T","subCategoryVisible":true,
            // "titleImage":null,"titlePresentationType":"text","viewerType":200,"vodType":0}

//            String             sProgramAge             = jobj.getString("channelProgramGrade");
//            String             sChannelInfo            = jobj.getString("channelInfo");
//            String             ProgramOnAirStartTime   = jobj.getString("channelProgramOnAirStartTime");
//            String             ProgramOnAirEndTime     = jobj.getString("channelProgramOnAirEndTime");
//            String             channelId               = jobj.getString("channelId");
//
//
//            ImageView          bookmarkImageView       = ViewHolder.get(convertView, R.id.epg_main_imagebutton_favorite);

//            String str = position + ": " + categoryId + "|" + categoryName + "|" + parentCategoryId;
//            if ( leaf == false ) {
//                str += " ▼";
//            }

            ImageView depth1ImageView = (ImageView)ViewHolder.get(convertView, R.id.vod_main_other_caterory_list_depth1_imageview);
            ImageView depth2ImageView = (ImageView)ViewHolder.get(convertView, R.id.vod_main_other_caterory_list_depth2_imageview);
            TextView textview = ViewHolder.get(convertView, R.id.vod_main_other_categoty_list_textview);
            ImageView imageView1 = (ImageView)ViewHolder.get(convertView, R.id.vod_main_other_categoty_list_imageview1);
            ImageView imageView2 = (ImageView)ViewHolder.get(convertView, R.id.vod_main_other_categoty_list_imageview2);

            if ( leaf == true ) {
                imageView2.setVisibility(View.GONE);
            } else {
                imageView2.setVisibility(View.VISIBLE);
            }


            textview.setText(categoryName);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
