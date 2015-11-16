package com.stvn.nscreen.pvr;

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
 * Created by limdavid on 15. 9. 15..
 */

public class PvrMainListViewAdapter extends BaseAdapter {

    private static final String                        tag              = PvrMainListViewAdapter.class.getSimpleName();
    private              Context                       mContext         = null;
    private              View.OnClickListener          mOnClickListener = null;
    private              ArrayList<ListViewDataObject> mDatas           = new ArrayList<ListViewDataObject>();

    private              RequestQueue                  mRequestQueue;
    private              ImageLoader                   mImageLoader;

    private              ArrayList<JSONObject>           mStbRecordReservelist;

    private int iTabNumber; // 1이면 녹화예약목록, 2면 녹화물 목록

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

        iTabNumber = 1; // 1이면 녹화예약목록, 2면 녹화물 목록
    }

    public void setStbRecordReservelist(int tabNumber, ArrayList<JSONObject> list) {
        this.iTabNumber = tabNumber;
        this.mStbRecordReservelist = list;
    }

    public void setTabNumber(int tabNumber) {
        this.iTabNumber = tabNumber;
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
    public int getItemViewType(int position) {
        // current menu type
        Date dt = new Date();

        SimpleDateFormat       formatter                    = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat       formatter2                   = new SimpleDateFormat("HH:mm");
        try {
            ListViewDataObject dobj = (ListViewDataObject) getItem(position);
            JSONObject jobj = new JSONObject(dobj.sJson);

            String RecordStartTime = jobj.getString("RecordStartTime");
            String RecordEndTime = jobj.getString("RecordEndTime");

            Date dt11 = formatter.parse(RecordStartTime);
            Date dt12 = formatter.parse(RecordEndTime);
            String dt21 = formatter2.format(dt11).toString();
            String dt22 = formatter2.format(dt12).toString();
            String dt23 = formatter2.format(dt).toString();

            Integer i1 = (Integer.parseInt(dt21.substring(0, 2)) * 60) + (Integer.parseInt(dt21.substring(3))); // 시작시간
            Integer i2 = (Integer.parseInt(dt22.substring(0, 2)) * 60) + (Integer.parseInt(dt22.substring(3))); // 끝시간
            Integer i3 = (Integer.parseInt(dt23.substring(0, 2)) * 60) + (Integer.parseInt(dt23.substring(3))); // 현재시간

            if (i1 <= i3 && i3 <= i2 ) { // 예약녹화 걸려있지 않은 방송.
                return 0; // 녹화중지
            } else {
                return 1; // 녹화 예약 취소
            }
        } catch ( JSONException e ) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
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

        Date dt = new Date();

        SimpleDateFormat       formatter              = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat       formatter2             = new SimpleDateFormat("HH:mm");
        SimpleDateFormat       formatter3             = new SimpleDateFormat("MM.dd (E)");

        try {
            ListViewDataObject dobj            = (ListViewDataObject)getItem(position);
            JSONObject         jobj            = new JSONObject(dobj.sJson);

            String             RecordStartTime = jobj.getString("RecordStartTime");
            String             RecordEndTime   = jobj.getString("RecordEndTime");

            TextView           titleTextView          = ViewHolder.get(convertView, R.id.pvr_main_textview_program_title);
            TextView           pvr_main_textview_time = ViewHolder.get(convertView, R.id.pvr_main_textview_time);
            TextView           pvr_main_textview_date = ViewHolder.get(convertView, R.id.pvr_main_textview_date);
            NetworkImageView   channelLogo            = ViewHolder.get(convertView, R.id.pvr_main_imagebutton_channel_logo);
            ImageView          pvr_main_pvr           = ViewHolder.get(convertView, R.id.pvr_main_pvr);
            ProgressBar        progBar                = ViewHolder.get(convertView, R.id.progressBar1);

            if ( iTabNumber == 1 ) {
                progBar.setVisibility(View.VISIBLE);
                pvr_main_pvr.setVisibility(View.VISIBLE);
            } else {
                progBar.setVisibility(View.INVISIBLE);
                pvr_main_pvr.setVisibility(View.INVISIBLE);
            }

            titleTextView.setText(jobj.getString("ProgramName"));
            channelLogo.setImageUrl(jobj.getString("Channel_logo_img"), mImageLoader);

            Date               dt11                     = formatter.parse(RecordStartTime);
            Date               dt12                     = formatter.parse(RecordEndTime);
            String             dt21                     = formatter2.format(dt11).toString();
            String             dt22                     = formatter2.format(dt12).toString();
            String             dt23                     = formatter2.format(dt).toString();
            String             dt31                     = formatter3.format(dt11).toString();

            pvr_main_textview_time.setText(dt21);
            pvr_main_textview_date.setText(dt31);

            Integer i1 = (Integer.parseInt(dt21.substring(0, 2)) * 60) + (Integer.parseInt(dt21.substring(3)));
            Integer i2 = (Integer.parseInt(dt22.substring(0, 2)) * 60) + (Integer.parseInt(dt22.substring(3)));
            Integer i3 = (Integer.parseInt(dt23.substring(0, 2)) * 60) + (Integer.parseInt(dt23.substring(3)));

            if (i1 <= i3 && i3 <= i2 ) { // 예약녹화 걸려있지 않은 방송.
                float f1 = ((float)i3 - (float)i1) / ((float)i2 - (float)i1);
                progBar.setProgress((int)(f1 * 100));
                pvr_main_pvr.setVisibility(View.VISIBLE);
            } else if ( dt.compareTo(dt11) < 0 ) {
                progBar.setProgress(0);
                pvr_main_pvr.setVisibility(View.INVISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertView;
    }
}