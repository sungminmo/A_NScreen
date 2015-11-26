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
import java.util.Date;

/**
 * Created by limdavid on 15. 9. 11..
 */

public class EpgSubListViewAdapter extends BaseAdapter {

    private static final String                        tag              = EpgSubListViewAdapter.class.getSimpleName();
    private              Context                       mContext         = null;
    private              JYSharedPreferences           mPref;
    private              View.OnClickListener          mOnClickListener = null;
    private              ArrayList<ListViewDataObject> mDatas           = new ArrayList<ListViewDataObject>();

    private              int                           mCurrDateNo;

    // STB status
    private              String                          mChannelNumber;        // intent param
    private              String                          mChannelId;            // intent param
    private              String                          mChannelName;          // intent param
    private              String                          mStbState;             // GetSetTopStatus API로 가져오는 값.
    private              String                          mStbRecordingchannel1; // GetSetTopStatus API로 가져오는 값.
    private              String                          mStbRecordingchannel2; // GetSetTopStatus API로 가져오는 값.
    private              String                          mStbWatchingchannel;   // GetSetTopStatus API로 가져오는 값.
    private              String                          mStbPipchannel;        // GetSetTopStatus API로 가져오는 값.
    private              ArrayList<JSONObject>           mStbRecordReservelist;

    private NetworkImageView epg_sub_imageview_program_age;

    public EpgSubListViewAdapter(Context c, View.OnClickListener onClickListener) {
        super();

        this.mContext              = c;
        this.mOnClickListener      = onClickListener;
        this.mPref                 = new JYSharedPreferences(c);
        this.mStbRecordReservelist = new ArrayList<JSONObject>();
    }

    public void setChannelIdChannelNumberChannelName(String ChannelId, String ChannelNumber, String ChannelName) {
        this.mChannelId = ChannelId;
        this.mChannelNumber = ChannelNumber;
        this.mChannelName = ChannelName;
    }

    public void setStbState(String state, String recCh1, String recCh2, String watchCh, String pipCh) {
        this.mStbState             = state;
        this.mStbRecordingchannel1 = recCh1; // ID
        this.mStbRecordingchannel2 = recCh2; // ID
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
        return 10;
    }

    @Override
    public int getItemViewType(int position) {
        // current menu type
        Date dt = new Date();

        SimpleDateFormat       formatter                    = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat       formatter2                   = new SimpleDateFormat("HH:mm");
        try {
            ListViewDataObject dobj                         = (ListViewDataObject)getItem(position);
            JSONObject         jobj                         = new JSONObject(dobj.sJson);

            String             programId                    = jobj.getString("programId");
            String             ProgramBroadcastingStartTime = jobj.getString("programBroadcastingStartTime");
            String             ProgramBroadcastingEndTime   = jobj.getString("programBroadcastingEndTime");

            Date               dt11                         = formatter.parse(ProgramBroadcastingStartTime);
            Date               dt12                         = formatter.parse(ProgramBroadcastingEndTime);
            String             dt21                         = formatter2.format(dt11).toString();
            String             dt22                         = formatter2.format(dt12).toString();
            String             dt23                         = formatter2.format(dt).toString();

            Integer i1 = (Integer.parseInt(dt21.substring(0, 2)) * 60) + (Integer.parseInt(dt21.substring(3))); // 시작시간
            Integer i2 = (Integer.parseInt(dt22.substring(0, 2)) * 60) + (Integer.parseInt(dt22.substring(3))); // 끝시간
            Integer i3 = (Integer.parseInt(dt23.substring(0, 2)) * 60) + (Integer.parseInt(dt23.substring(3))); // 현재시간

            // case 0: { // TV로 시청 / 즉시녹화
            // case 1: { // TV로 시청 / 녹화중지
            // case 2: { // 시청예약 / 녹화예약
            // case 3: { // 시청예약 / 녹화예약취소
            // case 4: { // 시청예약취소 / 녹화예약
            // case 5: { // 시청예약취소 / 녹화예약취소
            // case 6: { // 미페어링
            // case 7: { // TV로 시청
            // case 8: { // 시청예약
            // case 9: { // 시청예약취소

            if ( mPref.isPairingCompleted() == false ) {
                return 6;
            }

            if ( "PVR".equals(mPref.getValue(JYSharedPreferences.RUMPERS_SETOPBOX_KIND, "")) ) {
                if (mCurrDateNo != 0) { // 오늘이 아니라면...
                    JSONObject reservItem = getStbRecordReserveWithChunnelId(mChannelId, dobj);
                    if (reservItem == null) { // 예약녹화 걸려있지 않은 방송.
                        if (mPref.isWatchTvReserveWithProgramId(programId) == true) { // 시청예약 걸려 있음.
                            return 4; // 시청예약취소 / 녹화예약
                        } else {  // 시청예약 없음.
                            return 2; // 시청예약 / 녹화예약
                        }
                    } else {      //  예약 녹화 걸린 방송.
                        if (mPref.isWatchTvReserveWithProgramId(programId) == true) { // 시청예약 걸려 있음.
                            return 5; // 시청예약취소 / 녹화예약취소
                        } else {  // 시청예약 없음.
                            return 3; // 시청예약 / 녹화예약취소
                        }
                    }
                } else {
                    if ( dt.compareTo(dt11) > 0 && dt.compareTo(dt12) <= 0 ) {
                        // 현재 방송 중.
                        // menu: TV로 시청 / 즉시녹화 or 즉시녹화중지.
                        if (mChannelId.equals(mStbRecordingchannel1) || mChannelId.equals(mStbRecordingchannel2)) {
                            return 1; // TV로 시청 / 녹화중지
                        } else {
                            return 0; // TV로 시청 / 즉시녹화
                        }
                    } else if ( dt.compareTo(dt11) <= 0) {
                        // 미래 방송.
                        JSONObject reservItem = getStbRecordReserveWithChunnelId(mChannelId, dobj);
                        if ( mPref.isWatchTvReserveWithProgramId(programId) == false ) {
                            if (reservItem == null) { // 예약녹화 걸려있지 않은 방송.
                                return 2; // 시청예약 / 녹화예약
                            } else {    //  예약 녹화 걸린 방송.
                                return 3; // 시청예약 / 녹화예약취소
                            }
                        } else {
                            if ( reservItem == null) { // 예약녹화 걸려있지 않은 방송.
                                return 4; // 시청예약취소 / 녹화예약
                            } else { // 예약 녹화 걸린 방송
                                return 5; // 시청예약취소 / 녹화에약취소
                            }
                        }
                    } else if (dt.compareTo(dt12) > 0) {
                            // 과거.
                    }
                }
            } else if ( "HD".equals(mPref.getValue(JYSharedPreferences.RUMPERS_SETOPBOX_KIND, "")) || "SMART".equals(mPref.getValue(JYSharedPreferences.RUMPERS_SETOPBOX_KIND, "")) ) {
                if (mCurrDateNo != 0) { // 오늘이 아니라면...
                        if (mPref.isWatchTvReserveWithProgramId(programId) == true) { // 시청예약 걸려 있음.
                            return 9; // 시청예약취소
                        } else {  // 시청예약 없음.
                            return 8; // 시청예약
                        }
                } else {
                    if ( dt.compareTo(dt11) > 0 && dt.compareTo(dt12) <= 0 ) {
                    // 현재 방송 중.
                    // menu: TV로 시청
                        return 7; // TV로 시청
                    } else if ( dt12.compareTo(dt11) > 0 ) {
                        // 미래 방송.
                        if (mPref.isWatchTvReserveWithProgramId(programId) == true) {
                            return 9; // 시청예약취소
                        } else {
                            return 8; // 시청예약
                        }
                    }
                }
            }
        } catch ( JSONException e ) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
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
    public void clear() { mDatas.clear(); mStbRecordReservelist.clear(); }

    public JSONObject getStbRecordReserveWithChunnelId(String channelId, ListViewDataObject epgitem) {
        try {
            String str = epgitem.sJson;
            JSONObject epgjo = new JSONObject(str);
            String programBroadcastingStartTime = epgjo.getString("programBroadcastingStartTime");
            for ( int i = 0; i < mStbRecordReservelist.size(); i++ ) {
                JSONObject reservjo = mStbRecordReservelist.get(i);
                String RecordStartTime = reservjo.getString("RecordStartTime");
                if ( programBroadcastingStartTime.equals(RecordStartTime) ) {  // epg의 시작간과 예약의 시작 시간이 같다면 동일 채널 동일 프로그램.
                    return reservjo;
                }
            }
        } catch ( JSONException e ) {
            e.printStackTrace();
        }
        return null;
    }

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
                // 미래 방송.
                progBar.setProgress(0);
            } else {
                // 오늘 방송.
                if ( i2 < i1 ) {
                    if ( 0 <= i3 && i3 < i2 ) {
                        i2 += 1440;
                        i3 += 1440;
                        if ( dt.compareTo(dt11) > 0 && dt.compareTo(dt12) < 0 ) {
                            // 현재 방송 중.
                            float f1 = ((float) i3 - (float) i1) / ((float) i2 - (float) i1);
                            progBar.setProgress((int) (f1 * 100));
                        }
                    } else {
                        i2 += 1440;
                        if ( dt.compareTo(dt11) > 0 && dt.compareTo(dt12) < 0 ) {
                            // 현재 방송 중.
                            float f1 = ((float) i3 - (float) i1) / ((float) i2 - (float) i1);
                            progBar.setProgress((int) (f1 * 100));
                        } else if ( dt.compareTo(dt11) < 0 ) {
                            // 미래 방송 중.
                            progBar.setProgress(0);
                        }
                    }
                } else {
                    if ( dt.compareTo(dt11) < 0 ) {
                        // 미래 방송.
                        progBar.setProgress(0);
                    } else if ( dt.compareTo(dt12) > 0 ) {
                        // 과거 방송.
                        progBar.setProgress(100);
                    }  else if ( dt.compareTo(dt11) > 0 && dt.compareTo(dt12) < 0 ) {
                        // 현재 방송.
                        float f1 = ((float) i3 - (float) i1) / ((float) i2 - (float) i1);
                        progBar.setProgress((int) (f1 * 100));
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
