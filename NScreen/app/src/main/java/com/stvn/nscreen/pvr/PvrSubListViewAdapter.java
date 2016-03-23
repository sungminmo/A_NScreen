package com.stvn.nscreen.pvr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
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

/**
 * Created by limdavid on 15. 9. 15..
 */

public class PvrSubListViewAdapter extends BaseAdapter {

    private static final String                        tag              = PvrSubListViewAdapter.class.getSimpleName();
    private              Context                       mContext         = null;
    private              View.OnClickListener          mOnClickListener = null;
    private              ArrayList<ListViewDataObject> mDatas           = new ArrayList<ListViewDataObject>();

    private              RequestQueue                  mRequestQueue;
    private              ImageLoader                   mImageLoader;

    public PvrSubListViewAdapter(Context c, View.OnClickListener onClickListener) {
        super();

        this.mContext         = c;
        this.mOnClickListener = onClickListener;

//        this.mImageLoader     = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
//            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(100);
//            public void putBitmap(String url, Bitmap bitmap) {
//                mCache.put(url, bitmap);
//            }
//            public Bitmap getBitmap(String url) {
//                return mCache.get(url);
//            }
//        });
    }

    @Override
    public int getViewTypeCount() {
        // menu type count
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        // current menu type
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

    /**
     * RecordStartTime : Asc sort
     *
     * 2016-03-23 녹화예약관리 목록 : 방영시간별로 오름차순으로 정렬하기.
     */
    public void sortDatas() {
        Collections.sort(mDatas, new RecordStartTimeAscCompare());
    }

    private class RecordStartTimeAscCompare implements Comparator<ListViewDataObject> {
        @Override
        public int compare(ListViewDataObject arg0, ListViewDataObject arg1) {
            JSONObject jsonObject;
            String recordStartTime0 = "", recordStartTime1 = "";

            try {
                jsonObject = new JSONObject(arg0.sJson);
                recordStartTime0 = jsonObject.getString("RecordStartTime");

                jsonObject = new JSONObject(arg1.sJson);
                recordStartTime1 = jsonObject.getString("RecordStartTime");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            //return recordStartTime1.compareTo(recordStartTime0);    // 내림차순
            return recordStartTime0.compareTo(recordStartTime1);    // 오름차순
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_pvr_sub, parent, false);
        }

        try {
            ListViewDataObject dobj           = (ListViewDataObject)getItem(position);
            JSONObject         jobj           = new JSONObject(dobj.sJson);

            TextView           ProgramNameTextView = ViewHolder.get(convertView, R.id.pvr_sub_textview_program_title);
            TextView           ProgramDateTextView = ViewHolder.get(convertView, R.id.pvr_sub_textview_date);
            TextView           ProgramTimeTextView = ViewHolder.get(convertView, R.id.pvr_sub_textview_time);
            // NetworkImageView   channelLogo         = (NetworkImageView) ViewHolder.get(convertView, R.id.pvr_sub_imagebutton_channel_logo);

            SimpleDateFormat       formatter              = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat       formatter2             = new SimpleDateFormat("HH:mm");
            SimpleDateFormat       formatter3             = new SimpleDateFormat("MM.dd (E)");

            String RecordStartTime = jobj.getString("RecordStartTime");

            Date   dt11 = formatter.parse(RecordStartTime);
            String dt21 = formatter2.format(dt11).toString();
            String dt31 = formatter3.format(dt11).toString();

            ProgramNameTextView.setText(jobj.getString("ProgramName"));
            ProgramDateTextView.setText(dt31.toString());
            ProgramTimeTextView.setText(dt21.toString());

            // channelLogo.setImageUrl(jobj.getString("Channel_logo_img"), mImageLoader);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
