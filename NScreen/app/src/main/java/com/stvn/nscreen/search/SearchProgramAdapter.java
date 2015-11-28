package com.stvn.nscreen.search;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.CMDateUtil;
import com.jjiya.android.common.JYSharedPreferences;
import com.stvn.nscreen.R;
import com.stvn.nscreen.common.SearchProgramDataObject;
import com.stvn.nscreen.common.SwipeListView;
import com.stvn.nscreen.util.CMUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchProgramAdapter extends ArrayAdapter<SearchProgramDataObject> {

    LayoutInflater mInflater;
    private Context mContext;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private View.OnClickListener mSwipeClickListener;
    private JYSharedPreferences mPref;

    private String mStbState;             // GetSetTopStatus API로 가져오는 값.
    private String mStbRecordingchannel1; // GetSetTopStatus API로 가져오는 값.
    private String mStbRecordingchannel2; // GetSetTopStatus API로 가져오는 값.
    private String mStbWatchingchannel;   // GetSetTopStatus API로 가져오는 값.
    private String mStbPipchannel;        // GetSetTopStatus API로 가져오는 값.
    private ArrayList<JSONObject> mStbRecordReservelist;
    private              int                           mCurrDateNo;
    private SimpleDateFormat mTodayFormat;
    private SimpleDateFormat mCompareFormat;
    private Date mToday;


    public SearchProgramAdapter(Context context, List<SearchProgramDataObject> items) {
        super(context, 0, items);
        // TODO Auto-generated constructor stub
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mPref = new JYSharedPreferences(mContext);
        mRequestQueue = Volley.newRequestQueue(mContext);
        mTodayFormat = new SimpleDateFormat("yyyyMMdd");
        mCompareFormat = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
        mToday = new Date();
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(100);

            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }

            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });
        mStbRecordReservelist = new ArrayList<JSONObject>();
    }

    public void setSwipeClickListener(View.OnClickListener l) {
        this.mSwipeClickListener = l;
    }

    public void setStbState(String state, String recCh1, String recCh2, String watchCh, String pipCh) {
        this.mStbState             = state;
        this.mStbRecordingchannel1 = recCh1; // ID
        this.mStbRecordingchannel2 = recCh2; // ID
        this.mStbWatchingchannel   = watchCh;
        this.mStbPipchannel        = pipCh;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_search_program, null);
            holder = new ViewHolder();
            holder.favoriteimg = (ImageView) convertView.findViewById(R.id.favorite_img);
            holder.chaaneltext = (TextView) convertView.findViewById(R.id.channel_text);
            holder.programname = (TextView) convertView.findViewById(R.id.programname);
            holder.chlogo = (NetworkImageView) convertView.findViewById(R.id.ch_logo);
            holder.chtimetext = (TextView) convertView.findViewById(R.id.ch_time_text);
            holder.channeltext2 = (TextView) convertView.findViewById(R.id.ch_text2);
            holder.chage = (ImageView) convertView.findViewById(R.id.ch_age);
            holder.chhd = (ImageView) convertView.findViewById(R.id.ch_hd);
            holder.notPairing = (Button) convertView.findViewById(R.id.not_pairing);
            holder.recstart = (Button) convertView.findViewById(R.id.rec_start);
            holder.recstop = (Button) convertView.findViewById(R.id.rec_stop);
            holder.watchtv = (Button) convertView.findViewById(R.id.watch_tv);
            holder.setreservationrec = (Button) convertView.findViewById(R.id.set_reservation_rec);
            holder.cancelreservationrec = (Button) convertView.findViewById(R.id.cancel_reservation_rec);
            holder.setreservationwatch = (Button) convertView.findViewById(R.id.set_reservation_watch);
            holder.cancelreservationwatch = (Button) convertView.findViewById(R.id.cancel_reservation_watch);
            holder.recstart.setOnClickListener(this.mSwipeClickListener);
            holder.recstop.setOnClickListener(this.mSwipeClickListener);
            holder.watchtv.setOnClickListener(this.mSwipeClickListener);
            holder.setreservationrec.setOnClickListener(this.mSwipeClickListener);
            holder.cancelreservationrec.setOnClickListener(this.mSwipeClickListener);
            holder.setreservationwatch.setOnClickListener(this.mSwipeClickListener);
            holder.cancelreservationwatch.setOnClickListener(this.mSwipeClickListener);

            holder.notPairing.setBackground(new ColorDrawable(Color.rgb(0x00, 0x00, 0x00)));
            holder.recstart.setBackground(new ColorDrawable(Color.rgb(0xC4, 0x5C, 0xC2)));
            holder.recstop.setBackground(new ColorDrawable(Color.rgb(0xEA, 0x55, 0x55)));
            holder.watchtv.setBackground(new ColorDrawable(Color.rgb(0xF7, 0xBD, 0x33)));
            holder.setreservationrec.setBackground(new ColorDrawable(Color.rgb(0xED, 0x72, 0x33)));
            holder.cancelreservationrec.setBackground(new ColorDrawable(Color.rgb(0xC1, 0x4F, 0x28)));
            holder.setreservationwatch.setBackground(new ColorDrawable(Color.rgb(0xB3, 0xCF, 0x3B)));
            holder.cancelreservationwatch.setBackground(new ColorDrawable(Color.rgb(0x7F, 0x94, 0x24)));

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ((SwipeListView) parent).recycle(convertView, position);
        SearchProgramDataObject item = getItem(position);
        holder.chlogo.setImageUrl(item.getChannelLogoImg(), mImageLoader);
        holder.chaaneltext.setText(item.getChannelNumber());
        holder.programname.setText(item.getChannelProgramTitle());
        String datetime = item.getChannelProgramTime().trim();
//		2015-10-28 04:30:00

        holder.chtimetext.setText(CMUtil.getConverDateString(datetime, "yyyy-MM-ddHH:mm:ss", "HH:mm"));

        long remainTime = CMDateUtil.getLicenseRemainMinute(datetime, new Date());
        if (remainTime > 0) {
            holder.channeltext2.setText(CMUtil.getConverDateString(datetime, "yyyy-MM-ddHH:mm:ss", "yyyy.MM.dd") + " 방송예정");
        } else {
            holder.channeltext2.setText("현재 방송 중");
        }

        holder.chhd.setVisibility(View.VISIBLE);

        if ("SD".equals(item.getChannelInfo())) {        // SD방송
            holder.chhd.setImageResource(R.mipmap.btn_size_sd);
        } else if ("HD".equals(item.getChannelInfo()) || "SD,HD".equals(item.getChannelInfo())) {            // HD방송
            holder.chhd.setImageResource(R.mipmap.btn_size_hd);
        } else { // 둘다 아닐때
            holder.chhd.setVisibility(View.GONE);
        }

        if (mPref.isBookmarkChannelWithChannelId(item.getChannelId())) {
            holder.favoriteimg.setSelected(true);
        }
        else {
            holder.favoriteimg.setSelected(false);
        }

        holder.favoriteimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSelected = !v.isSelected();
                v.setSelected(isSelected);

                SearchProgramDataObject programItem = getItem(position);
                String channelID = programItem.getChannelId();
                String channelNO = programItem.getChannelNumber();
                String channelName = programItem.getChannelName();
                if (isSelected) {
                    mPref.addBookmarkChannel(channelID, channelNO, channelName); // 선호채널 추가.
                } else {
                    mPref.removeBookmarkChannelWithChannelId(channelID); // 선호채널 제거.
                }
            }
        });

        // 연령제한
        if (!TextUtils.isEmpty(item.getChannelProgramGrade())) {
            if (item.getChannelProgramGrade().indexOf("7") > -1) {
                holder.chage.setImageResource(R.mipmap.btn_age_7);
            } else if (item.getChannelProgramGrade().indexOf("12") > -1) {
                holder.chage.setImageResource(R.mipmap.btn_age_12);
            } else if (item.getChannelProgramGrade().indexOf("15") > -1) {
                holder.chage.setImageResource(R.mipmap.btn_age_15);
            } else if (item.getChannelProgramGrade().indexOf("19") > -1) {
                holder.chage.setImageResource(R.mipmap.btn_age_19);
            } else {
                holder.chage.setImageResource(R.mipmap.btn_age_all);
            }
        }
        getSwipeLayout(item, position,holder);
//        if (mPref.isPairingCompleted()) {
//            getSwipeLayout(null, position, holder);
//        }

        return convertView;
    }

    public void getSwipeLayout(SearchProgramDataObject item, int position, ViewHolder holder) {
        holder.recstart.setVisibility(View.GONE);
        holder.recstop.setVisibility(View.GONE);
        holder.watchtv.setVisibility(View.GONE);
        holder.setreservationrec.setVisibility(View.GONE);
        holder.cancelreservationrec.setVisibility(View.GONE);
        holder.setreservationwatch.setVisibility(View.GONE);
        holder.cancelreservationwatch.setVisibility(View.GONE);
        holder.notPairing.setVisibility(View.GONE);
        holder.recstart.setTag(position);
        holder.recstop.setTag(position);
        holder.watchtv.setTag(position);
        holder.setreservationrec.setTag(position);
        holder.cancelreservationrec.setTag(position);
        holder.setreservationwatch.setTag(position);
        holder.cancelreservationwatch.setTag(position);
        item.setSwipeMode(SwipeListView.SWIPE_MODE_DEFAULT);
        if (item == null) {
            holder.notPairing.setVisibility(View.VISIBLE);
        }
        if(!mPref.isPairingCompleted())
        {
            holder.notPairing.setVisibility(View.VISIBLE);
        }else
        {
            if(!mTodayFormat.format(mToday).equals(CMUtil.getConverDateString(item.getChannelProgramTime().trim(), "yyyy-MM-ddHH:mm:ss", "yyyyMMdd")))// 오늘이 아니라면...
            {
                JSONObject reservItem = getStbRecordReserveWithChunnelId(item.getChannelId(),item);
                if(reservItem == null)  // 예약녹화 걸려있지 않은 방송
                {
                    if ( mPref.isWatchTvReserveWithProgramId(item.getChannelProgramID()) == true ) // 시청예약 걸려있음
                    {
                        holder.setreservationrec.setVisibility(View.VISIBLE);               // 녹화예약 시작
                        holder.cancelreservationwatch.setVisibility(View.VISIBLE);          // 시청예약 취소
                    }else // 시청예약 없음
                    {
                        holder.setreservationrec.setVisibility(View.VISIBLE);           // 녹화예약 시작
                        holder.setreservationwatch.setVisibility(View.VISIBLE);         // 시청예약 시작
                    }
                }else // 예약녹화 걸린방송
                {
                    if ( mPref.isWatchTvReserveWithProgramId(item.getChannelProgramID()) == true ) { // 시청예약 걸려 있음
                        holder.cancelreservationrec.setVisibility(View.VISIBLE);            // 녹화예약 취소
                        holder.cancelreservationwatch.setVisibility(View.VISIBLE);          // 시청예약 취소
                    } else {  // 시청예약 없음.
                        holder.cancelreservationrec.setVisibility(View.VISIBLE);            // 녹화예약 취소
                        holder.setreservationwatch.setVisibility(View.VISIBLE);         // 시청예약 시작
                    }
                }
            }else       // 오늘일경우
            {
                try
                {
                    Date date = mCompareFormat.parse(item.getChannelProgramTime());
                    if(mToday.compareTo(date)>0) //현재방송중이거나 과거방송
                    {
                        // 정책변경으로 인해 현재 방송중인 프로그램은 레이어 노출 불가
                        item.setSwipeMode(SwipeListView.SWIPE_MODE_NONE);
//                        ((SwipeListView) mParent).setSwipeMode(SwipeListView.SWIPE_MODE_NONE);
                    }else
                    {
                        JSONObject reservItem = getStbRecordReserveWithChunnelId(item.getChannelId(), item);
                        if (reservItem == null) { // 예약녹화 걸려있지 않은 방송.
                            holder.setreservationrec.setVisibility(View.VISIBLE);           // 녹화예약
                            holder.setreservationwatch.setVisibility(View.VISIBLE);         // 시청예약
                        } else {    //  예약 녹화 걸린 방송.
                            holder.setreservationwatch.setVisibility(View.VISIBLE);         // 시청예약
                            holder.cancelreservationrec.setVisibility(View.VISIBLE);            // 녹화예약 취소
                        }
                    }

                }catch(Exception e)
                {}
            }

        }


//        if ( mPref.isPairingCompleted() == false ) {
//            return 6;
//        }
//
//        if ( mCurrDateNo != 0 ) { // 오늘이 아니라면...
//            JSONObject reservItem = getStbRecordReserveWithChunnelId(mChannelId, dobj);
//            if (reservItem == null) { // 예약녹화 걸려있지 않은 방송.
//                if ( mPref.isWatchTvReserveWithProgramId(programId) == true ) { // 시청예약 걸려 있음.
//                    return 4; // 시청예약취소 / 녹화예약
//                } else {  // 시청예약 없음.
//                    return 2; // 시청예약 / 녹화예약
//                }
//            } else {      //  예약 녹화 걸린 방송.
//                if ( mPref.isWatchTvReserveWithProgramId(programId) == true ) { // 시청예약 걸려 있음.
//                    return 5; // 시청예약취소 / 녹화예약취소
//                } else {  // 시청예약 없음.
//                    return 3; // 시청예약 / 녹화예약취소
//                }
//            }
//        } else {
//            if ( i2 < i1) {
//                i2 += 1440;
//                if ( i3 < i1 ) {
//                    i3 += 1440;
//                    if (i1 < i3 && i3 <= i2) {
//                        // 현재 방송 중.
//                        // menu: TV로 시청 / 즉시녹화 or 즉시녹화중지.
//                        if (mChannelId.equals(mStbRecordingchannel1) || mChannelId.equals(mStbRecordingchannel2)) {
//                            return 1; // TV로 시청 / 녹화중지
//                        } else {
//                            return 0; // TV로 시청 / 즉시녹화
//                        }
//                    } else if (i3 <= i1) {
//                        // 미래 방송.
//                        JSONObject reservItem = getStbRecordReserveWithChunnelId(mChannelId, dobj);
//                        if (reservItem == null) { // 예약녹화 걸려있지 않은 방송.
//                            return 2; // 시청예약 / 녹화예약
//                        } else {    //  예약 녹화 걸린 방송.
//                            return 3; // 시청예약 / 녹화예약취소
//                        }
//                    }
//                }
//            } else {
//                if (i1 < i3 && i3 <= i2) {
//                    // 현재 방송 중.
//                    // menu: TV로 시청은 고정./ 즉시녹화or즉시녹화중지.
//                    if (mChannelId.equals(mStbRecordingchannel1) || mChannelId.equals(mStbRecordingchannel2)) {
//                        return 1; // TV로 시청 / 녹화중지
//                    } else {
//                        return 0; // TV로 시청 / 즉시녹화
//                    }
//                } else if ( i3 <= i1 ) {
//                    // 미래.
//                    JSONObject reservItem = getStbRecordReserveWithChunnelId(mChannelId, dobj);
//                    if (reservItem == null) { // 예약녹화 걸려있지 않은 방송.
//                        return 2; // 시청예약 / 녹화예약
//                    } else {    //  예약 녹화 걸린 방송.
//                        return 3; // 시청예약 / 녹화예약취소
//                    }
//                } else if ( i3 > i2 ) {
//                    // 과거.
//
//                }
//            }
//        }




//        switch (position % 9) {
//            case 0:                // PVR STB > 현재방송중 : 즉시녹화,TV로시청
//                holder.recstart.setVisibility(View.VISIBLE);
//                holder.watchtv.setVisibility(View.VISIBLE);
//                break;
//            case 1:                // PVR STB > 현재방송중 > 녹화중 : 즉시녹화중지,TV로시청
//                holder.recstop.setVisibility(View.VISIBLE);
//                holder.watchtv.setVisibility(View.VISIBLE);
//                break;
//            case 2:                // PVR STB > 미래방송 >  : 녹화예약설정,시청예약설정
//                holder.setreservationrec.setVisibility(View.VISIBLE);
//                holder.setreservationwatch.setVisibility(View.VISIBLE);
//                break;
//            case 3:                // PVR STB > 미래방송 > 녹화예약중  : 녹화예약취소,시청예약설정
//                holder.cancelreservationrec.setVisibility(View.VISIBLE);
//                holder.setreservationwatch.setVisibility(View.VISIBLE);
//                break;
//            case 4:                // PVR STB > 미래방송 > 시청예약중  : 녹화예약설정,시청예약취소
//                holder.setreservationrec.setVisibility(View.VISIBLE);
//                holder.cancelreservationwatch.setVisibility(View.VISIBLE);
//                break;
//            case 5:                // PVR STB > 미래방송 > 시청예약중/녹화예약중  : 녹화예약취소,시청예약취소
//                holder.cancelreservationrec.setVisibility(View.VISIBLE);
//                holder.cancelreservationwatch.setVisibility(View.VISIBLE);
//                break;
//            case 6:                // HD STB > 현재방송 > TV로 시청
//                holder.watchtv.setVisibility(View.VISIBLE);
//                break;
//            case 7:                // HD STB > 미래방송 > 시청예약설정
//                holder.setreservationwatch.setVisibility(View.VISIBLE);
//                break;
//            case 8:                // HD STB > 미래방송 > 시청예약중 >  시청예약취소
//                holder.cancelreservationwatch.setVisibility(View.VISIBLE);
//                break;
//        }
    }


    public void setStbRecordReservelist(ArrayList<JSONObject> list) {
        this.mStbRecordReservelist = list;
    }

    class ViewHolder {
        ImageView favoriteimg;
        TextView chaaneltext;
        TextView programname;
        NetworkImageView chlogo;
        TextView chtimetext;
        TextView channeltext2;
        ImageView chage;
        ImageView chhd;

        //Swipe 영역 버튼
        Button notPairing;//셋탑미연동
        Button recstart;//즉시녹화
        Button recstop;//즉시녹화취소
        Button watchtv;//TV로시청
        Button setreservationrec;// 녹화예약설정
        Button cancelreservationrec;//녹화예약취소
        Button setreservationwatch; // 시청예약설정
        Button cancelreservationwatch;// 시청예약취소

    }

    public JSONObject getStbRecordReserveWithChunnelId(String channelId, SearchProgramDataObject item) {
        try {
//            String str = epgitem.sJson;
//            JSONObject epgjo = new JSONObject(str);
//            String programBroadcastingStartTime = epgjo.getString("programBroadcastingStartTime");
//            for ( int i = 0; i < mStbRecordReservelist.size(); i++ ) {
//                JSONObject reservjo = mStbRecordReservelist.get(i);
//                String RecordStartTime = reservjo.getString("RecordStartTime");
//                if ( programBroadcastingStartTime.equals(RecordStartTime) ) {  // epg의 시작간과 예약의 시작 시간이 같다면 동일 채널 동일 프로그램.
//                    return reservjo;
//                }
//            }
            String str = item.getChannelProgramTime();
            for(int i=0;i<mStbRecordReservelist.size();i++)
            {
                JSONObject reservjo = mStbRecordReservelist.get(i);
                String RecordStartTime = reservjo.getString("RecordStartTime");
                if (str.equals(RecordStartTime) ) {  // epg의 시작간과 예약의 시작 시간이 같다면 동일 채널 동일 프로그램.
                    return reservjo;
                }
            }
        } catch ( JSONException e ) {
            e.printStackTrace();
        }
        return null;
    }

}
