package com.stvn.nscreen.epg;

import android.content.Context;
import android.graphics.Bitmap;
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

public class EpgMainListViewAdapter extends BaseAdapter {

    private static final String                        tag              = EpgMainListViewAdapter.class.getSimpleName();
    private              Context                       mContext         = null;
    private              JYSharedPreferences           mPref;
    private              View.OnClickListener          mOnClickListener = null;
    public               ArrayList<ListViewDataObject> mDatas           = new ArrayList<ListViewDataObject>();

    private              RequestQueue                  mRequestQueue;
    private              ImageLoader                   mImageLoader;

    private              String                 mStbState;             // GetSetTopStatus API로 가져오는 값.
    private              String                 mStbRecordingchannel1; // GetSetTopStatus API로 가져오는 값.
    private              String                 mStbRecordingchannel2; // GetSetTopStatus API로 가져오는 값.
    private              String                 mStbWatchingchannel;   // GetSetTopStatus API로 가져오는 값.
    private              String                 mStbPipchannel;        // GetSetTopStatus API로 가져오는 값.

    public EpgMainListViewAdapter(Context c, View.OnClickListener onClickListener) {
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
        this.mPref            = new JYSharedPreferences(c);
    }

    public void setStbState(String state, String recCh1, String recCh2, String watchCh, String pipCh) {
        this.mStbState             = state;
        this.mStbRecordingchannel1 = recCh1; // ID
        this.mStbRecordingchannel2 = recCh2; // ID
        this.mStbWatchingchannel   = watchCh;
        this.mStbPipchannel        = pipCh;
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
    public void clear() {
        mDatas.clear();
        mStbState = "";             // GetSetTopStatus API로 가져오는 값.
        mStbRecordingchannel1 = ""; // GetSetTopStatus API로 가져오는 값.
        mStbRecordingchannel2 = ""; // GetSetTopStatus API로 가져오는 값.
        mStbWatchingchannel = "";   // GetSetTopStatus API로 가져오는 값.
        mStbPipchannel = "";        // GetSetTopStatus API로 가져오는 값.
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_epg_main, parent, false);
        }

        Date dt = new Date();

        SimpleDateFormat       formatter              = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat       formatter2             = new SimpleDateFormat("HH:mm");

        try {
            ListViewDataObject dobj                    = (ListViewDataObject)getItem(position);
            JSONObject         jobj                    = new JSONObject(dobj.sJson);

            String             sProgramAge             = jobj.getString("channelProgramGrade");
            String             sChannelInfo            = jobj.getString("channelInfo");
            String             ProgramOnAirStartTime   = jobj.getString("channelProgramOnAirStartTime");
            String             ProgramOnAirEndTime     = jobj.getString("channelProgramOnAirEndTime");
            String             channelId               = jobj.getString("channelId");

            NetworkImageView   channelLogo             = (NetworkImageView) ViewHolder.get(convertView, R.id.epg_main_imageview_channel_logo);

            ImageView          bookmarkImageView       = ViewHolder.get(convertView, R.id.epg_main_imagebutton_favorite);
            ImageView          programAge              = ViewHolder.get(convertView, R.id.epg_main_imageview_program_age);
            ImageView          Info                    = ViewHolder.get(convertView, R.id.epg_main_imageview_program_hdsd);
            ImageView          favoriteImageView       = ViewHolder.get(convertView, R.id.epg_main_imagebutton_favorite);
            TextView           channelNumberTextView   = ViewHolder.get(convertView, R.id.epg_main_textview_channel_number);
            TextView           titleTextView           = ViewHolder.get(convertView, R.id.epg_main_textview_program_title);
            TextView           channelProgramOnAirTime = ViewHolder.get(convertView, R.id.epg_main_textview_program_time);
            ImageView           recImageView           = ViewHolder.get(convertView, R.id.epg_main_rec_imageview);
            ProgressBar        progBar = ViewHolder.get(convertView, R.id.progressBar);

            Date               dt11                     = formatter.parse(ProgramOnAirStartTime);
            Date               dt12                     = formatter.parse(ProgramOnAirEndTime);
            String             dt21                     = formatter2.format(dt11).toString();
            String             dt22                     = formatter2.format(dt12).toString();
            String             dt23                     = formatter2.format(dt).toString();

            Integer i1 = (Integer.parseInt(dt21.substring(0, 2)) * 60) + (Integer.parseInt(dt21.substring(3)));
            Integer i2 = (Integer.parseInt(dt22.substring(0, 2)) * 60) + (Integer.parseInt(dt22.substring(3)));
            Integer i3 = (Integer.parseInt(dt23.substring(0, 2)) * 60) + (Integer.parseInt(dt23.substring(3)));

            if ( i1 < 360 ) {
                float f1 = ((float)i3 - (float)i1) / ((float)i2 - (float)i1);
                progBar.setProgress((int)(f1 * 100));
            } else if ( i2 < 360 ) {
                i2 += 1440;
                if ( i3 < 360 ) {
                    i3 += 1440;
                    float f1 = ((float)i3 - (float)i1) / ((float)i2 - (float)i1);
                    progBar.setProgress((int)(f1 * 100));
                }
            }

            if ( mPref.isBookmarkChannelWithChannelId(channelId) == true ) {
                bookmarkImageView.setImageResource(R.mipmap.icon_list_favorite_select);
            } else {
                bookmarkImageView.setImageResource(R.mipmap.icon_list_favorite_unselect);
            }

            channelNumberTextView.setText(jobj.getString("channelNumber"));
            titleTextView.setText(jobj.getString("channelProgramOnAirTitle"));
            channelLogo.setImageUrl(jobj.getString("channelLogoImg"), mImageLoader);
            channelProgramOnAirTime.setText(dt21 + "~" + dt22);

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

            if ( channelId.equals(mStbRecordingchannel1) || channelId.equals(mStbRecordingchannel2) ) {
                recImageView.setVisibility(View.VISIBLE);
            } else {
                recImageView.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
