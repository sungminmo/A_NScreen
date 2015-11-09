package com.stvn.nscreen.epg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import java.util.Date;

/**
 * Created by limdavid on 15. 9. 11..
 */

public class EpgSubListViewAdapter extends BaseAdapter {

    private static final String                        tag              = EpgSubListViewAdapter.class.getSimpleName();
    private              Context                       mContext         = null;
    private              View.OnClickListener          mOnClickListener = null;
    private              ArrayList<ListViewDataObject> mDatas           = new ArrayList<ListViewDataObject>();

    private              int                           mCurrDateNo;

    // STB status
    private              String                          mStbState;             // GetSetTopStatus API로 가져오는 값.
    private              String                          mStbRecordingchannel1; // GetSetTopStatus API로 가져오는 값.
    private              String                          mStbRecordingchannel2; // GetSetTopStatus API로 가져오는 값.
    private              String                          mStbWatchingchannel;   // GetSetTopStatus API로 가져오는 값.
    private              String                          mStbPipchannel;        // GetSetTopStatus API로 가져오는 값.
    private              ArrayList<JSONObject>           mStbRecordReservelist;

    private NetworkImageView epg_sub_imageview_program_age;

    public EpgSubListViewAdapter(Context c, View.OnClickListener onClickListener) {
        super();

        this.mContext         = c;
        this.mOnClickListener = onClickListener;
    }

    public void setStbState(String state, String recCh1, String recCh2, String watchCh, String pipCh) {
        this.mStbState             = state;
        this.mStbRecordingchannel1 = recCh1;
        this.mStbRecordingchannel2 = recCh2;
        this.mStbWatchingchannel   = watchCh;
        this.mStbPipchannel        = pipCh;
    }

    public void setStbRecordReservelist(ArrayList<JSONObject> list) {
        this.mStbRecordReservelist = list;
    }

    public void setDatas(ArrayList<ListViewDataObject> datas, int currDateNo) {
        mCurrDateNo = currDateNo;
        mDatas.clear();
        for ( int i = 0; i < datas.size(); i++ ) {
            mDatas.add(datas.get(i));
        }
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
        return position % 3;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_epg_sub, parent, false);
        }

        Date dt = new Date();

        SimpleDateFormat       formatter                    = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat       formatter2                   = new SimpleDateFormat("HH:mm");

        try {
            ListViewDataObject dobj                         = (ListViewDataObject)getItem(position);
            JSONObject         jobj                         = new JSONObject(dobj.sJson);

            String             sProgramAge                  = jobj.getString("programGrade");
            String             sChannelInfo                 = jobj.getString("programHD");
            String             ProgramBroadcastingStartTime = jobj.getString("programBroadcastingStartTime");
            String             ProgramBroadcastingEndTime   = jobj.getString("programBroadcastingEndTime");

            ImageView          programAge                   = ViewHolder.get(convertView, R.id.epg_sub_imageview_program_age);
            ImageView          Info                         = ViewHolder.get(convertView, R.id.epg_sub_imageview_program_hdsd);
            TextView           titleTextView                = ViewHolder.get(convertView, R.id.epg_sub_textview_program_title);
            TextView           channelProgramOnAirTime      = ViewHolder.get(convertView, R.id.epg_sub_textview_program_time);
            ProgressBar        progBar                      = ViewHolder.get(convertView, R.id.progressBar);

            Date               dt11                         = formatter.parse(ProgramBroadcastingStartTime);
            Date               dt12                         = formatter.parse(ProgramBroadcastingEndTime);
            String             dt21                         = formatter2.format(dt11).toString();
            String             dt22                         = formatter2.format(dt12).toString();
            String             dt23                         = formatter2.format(dt).toString();

            // 시작시간
            Integer i1 = (Integer.parseInt(dt21.substring(0, 2)) * 60) + (Integer.parseInt(dt21.substring(3)));
            // 끝시간
            Integer i2 = (Integer.parseInt(dt22.substring(0, 2)) * 60) + (Integer.parseInt(dt22.substring(3)));
            // 현재시간
            Integer i3 = (Integer.parseInt(dt23.substring(0, 2)) * 60) + (Integer.parseInt(dt23.substring(3)));

            titleTextView.setText(jobj.getString("programTitle"));
            channelProgramOnAirTime.setText(dt21 + "~" + dt22);

            if ( mCurrDateNo != 0 ) {
                progBar.setProgress(0);
            } else {
                if ( i1 < 360 ) {
                    progBar.setProgress(0);
                } else {
                    if (i1 < i3 && i3 <= i2) {
                        // 현재 방송 중.
                        // menu: TV로 시청은 고정./ 즉시녹화or즉시녹화중지.
                        float f1 = ((float) i3 - (float) i1) / ((float) i2 - (float) i1);
                        progBar.setProgress((int) (f1 * 100));
                    } else if (i2 <= i3) {
                        // 이미 지난 방송.
                        if (Integer.parseInt(dt22.substring(0, 2)) < Integer.parseInt(dt21.substring(0, 2))) {
                            i2 += 1440;
                            float f1 = ((float) i3 - (float) i1) / ((float) i2 - (float) i1);
                            progBar.setProgress((int) (f1 * 100));
                        } else {
                            progBar.setProgress(100);
                        }
                    } else if (i3 <= i1) {
                        // 미래.
                        progBar.setProgress(0);
                    }
                }
            }

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

            if ( "NO".equals(sChannelInfo) ) {
                Info.setImageResource(R.mipmap.btn_size_sd);
            } else if ("YES".equals(sChannelInfo) ) {
                Info.setImageResource(R.mipmap.btn_size_hd);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
