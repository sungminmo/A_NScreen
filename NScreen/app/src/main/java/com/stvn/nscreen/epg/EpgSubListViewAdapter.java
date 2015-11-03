package com.stvn.nscreen.epg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
import java.util.Date;

/**
 * Created by limdavid on 15. 9. 11..
 */

public class EpgSubListViewAdapter extends BaseAdapter {

    private static final String                        tag              = EpgSubListViewAdapter.class.getSimpleName();
    private              Context                       mContext         = null;
    private              View.OnClickListener          mOnClickListener = null;
    private              ArrayList<ListViewDataObject> mDatas           = new ArrayList<ListViewDataObject>();

    private NetworkImageView epg_sub_imageview_program_age;

    public EpgSubListViewAdapter(Context c, View.OnClickListener onClickListener) {
        super();

        this.mContext         = c;
        this.mOnClickListener = onClickListener;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_epg_sub, parent, false);
        }

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

            Date               dt1                          = formatter.parse(ProgramBroadcastingStartTime);
            Date               dt2                          = formatter.parse(ProgramBroadcastingEndTime);
            String             str1                         = formatter2.format(dt1).toString();
            String             str2                         = formatter2.format(dt2).toString();

            titleTextView.setText(jobj.getString("programTitle"));
            channelProgramOnAirTime.setText(str1 + "~" + str2);

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
