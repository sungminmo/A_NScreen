package com.stvn.nscreen.rmt;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by limdavid on 15. 10. 26..
 */

public class RemoteControllerListViewAdapter extends BaseAdapter {

    private static final String                        tag              = RemoteControllerListViewAdapter.class.getSimpleName();
    private              Context                       mContext         = null;
    private              JYSharedPreferences           mPref;
    private              View.OnClickListener          mOnClickListener = null;
    private              ArrayList<ListViewDataObject> mDatas           = new ArrayList<ListViewDataObject>();

    private              RequestQueue                  mRequestQueue;
    private              ImageLoader                   mImageLoader;

    private              String                 mStbState;             // GetSetTopStatus API로 가져오는 값.
    private              String                 mStbRecordingchannel1; // GetSetTopStatus API로 가져오는 값.
    private              String                 mStbRecordingchannel2; // GetSetTopStatus API로 가져오는 값.
    private              String                 mStbWatchingchannel;   // GetSetTopStatus API로 가져오는 값.
    private              String                 mStbPipchannel;        // GetSetTopStatus API로 가져오는 값.
    private String mGenreCode;

    private              int                           mSelectedIndex   = -1;

    public RemoteControllerListViewAdapter(Context c, View.OnClickListener onClickListener) {
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

    public void setGenreCode(String code) {
        this.mGenreCode = code;
    }

    public String getChannelNumberWithChannelId(String cid) {
        if ( cid == null ) {
            return "";
        }
        String rtn = "";
        for ( int i = 0; i < mDatas.size(); i++ ) {
            ListViewDataObject obj = mDatas.get(i);
            try {
                JSONObject jo = new JSONObject(obj.sJson);
                if ( cid == null || jo.isNull("channelId") ) {
                    Log.d(tag, "getChannelNumberWithChannelId cid: " + cid);
                }
                String channelId = jo.getString("channelId");
                if ( cid.equals(channelId) == true ) {
                    String channelNumber = jo.getString("channelNumber");
                    return channelNumber;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return rtn;
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_remote_controller, parent, false);
        }

        Date dt = new Date();

        SimpleDateFormat       formatter              = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat       formatter2             = new SimpleDateFormat("HH:mm");

        try {
            ListViewDataObject dobj                  = (ListViewDataObject)getItem(position);
            JSONObject         jobj                  = new JSONObject(dobj.sJson);

            String             sProgramAge           = jobj.getString("channelProgramGrade");
            String             sChannelOnAirHD       = jobj.getString("channelOnAirHD");
            String             ProgramOnAirStartTime = jobj.getString("channelProgramOnAirStartTime");
            String             ProgramOnAirEndTime   = jobj.getString("channelProgramOnAirEndTime");

            NetworkImageView   channelLogo           = (NetworkImageView) ViewHolder.get(convertView, R.id.remote_imageview_channel_logo);

            ImageView          programAge            = ViewHolder.get(convertView, R.id.remote_imageview_program_age);
            ImageView          Info                  = ViewHolder.get(convertView, R.id.remote_imageview_program_hdsd);
            ImageView          favoriteImageView     = ViewHolder.get(convertView, R.id.remote_imagebutton_favorite);
            TextView           channelNumberTextView = ViewHolder.get(convertView, R.id.remote_textview_channel_number);
            TextView           titleTextView         = ViewHolder.get(convertView, R.id.remote_textview_program_title);
            TextView           programTimeTextView   = ViewHolder.get(convertView, R.id.remote_textview_program_time);
            final ImageButton        bookmarkImageButton   = ViewHolder.get(convertView, R.id.remote_imagebutton_favorite);
            ProgressBar progBar = ViewHolder.get(convertView, R.id.progressBar);

            Date               dt11                     = formatter.parse(ProgramOnAirStartTime);
            Date               dt12                     = formatter.parse(ProgramOnAirEndTime);
            String             dt21                     = formatter2.format(dt11).toString();
            String             dt22                     = formatter2.format(dt12).toString();
            String             dt23                     = formatter2.format(dt).toString();

            Integer i1 = (Integer.parseInt(dt21.substring(0, 2)) * 60) + (Integer.parseInt(dt21.substring(3)));
            Integer i2 = (Integer.parseInt(dt22.substring(0, 2)) * 60) + (Integer.parseInt(dt22.substring(3)));
            Integer i3 = (Integer.parseInt(dt23.substring(0, 2)) * 60) + (Integer.parseInt(dt23.substring(3)));

            if ( i1 > i2 ) {
                i2 += 1440;
                if ( i1 > i3 ) {
                    i3 += 1440;
                }
            }

            if ( dt.compareTo(dt11) > 0 && dt.compareTo(dt12) <= 0 ) {
                float f1 = ((float)i3 - (float)i1) / ((float)i2 - (float)i1);
                progBar.setProgress((int) (f1 * 100));
            } else if ( dt.compareTo(dt11) < 0 ) {
                progBar.setProgress(0);
            } else if ( dt.compareTo(dt12) > 0 ) {
                progBar.setProgress(100);
            }

            channelNumberTextView.setText(jobj.getString("channelNumber"));
            titleTextView.setText(jobj.getString("channelProgramOnAirTitle"));
            channelLogo.setImageUrl(jobj.getString("channelLogoImg"), mImageLoader);
            programTimeTextView.setText(dt21 + "~" + dt22);

            final String channelId = jobj.getString("channelId");
            final String channelNumber = jobj.getString("channelNumber");
            final String channelName = jobj.getString("channelName");

            if ( mPref.isBookmarkChannelWithChannelNumber(channelNumber) == true ) {
                bookmarkImageButton.setImageResource(R.mipmap.icon_list_favorite_select);
            } else {
                bookmarkImageButton.setImageResource(R.mipmap.icon_list_favorite_unselect);
            }
            bookmarkImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ( mPref.isBookmarkChannelWithChannelNumber(channelNumber) == true ) {
                        mPref.removeBookmarkChannelWithChannelNumber(channelNumber);
                        bookmarkImageButton.setImageResource(R.mipmap.icon_list_favorite_unselect);

                        // 현재 선택된 장르가 선호채널의 경우 선호채널에서 제외된 채널을 리스트에서 제거한다.
                        if ("&genreCode=0".equals(mGenreCode)) {
                            remove(position);
                            notifyDataSetChanged();
                        }

                    } else {
                        mPref.addBookmarkChannel(channelId, channelNumber, channelName);
                        bookmarkImageButton.setImageResource(R.mipmap.icon_list_favorite_select);
                    }
                }
            });

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

            if ( "NO".equals(sChannelOnAirHD) ) {
                Info.setImageResource(R.mipmap.btn_size_sd);
            } else if ("YES".equals(sChannelOnAirHD) ) {
                Info.setImageResource(R.mipmap.btn_size_hd);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertView;
    }
    public void setSelectedIndex(int position){ mSelectedIndex = position; }
    public int  getSelectedIndex() { return mSelectedIndex; }

    /*
    public void sortWithChannelNumber(){
        Collections.sort(mDatas, new Comparator(ListViewDataObject first, ListViewDataObject second) {
            @Override
            public int compare(ListViewDataObject first, ListViewDataObject second) {
                int firstValue = Integer.valueOf(first.get("no"));
                int secondValue = Integer.valueOf(second.get("no"));
                return first.get("no").compareTo(second.get("no"));
            }
        });
    }
    */
}
