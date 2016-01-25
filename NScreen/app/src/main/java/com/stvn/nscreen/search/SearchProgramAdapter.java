package com.stvn.nscreen.search;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

    public void setStbState(String state, String recCh1, String recCh2, String watchCh, String pipCh) {
        this.mStbState             = state;
        this.mStbRecordingchannel1 = recCh1; // ID
        this.mStbRecordingchannel2 = recCh2; // ID
        this.mStbWatchingchannel   = watchCh;
        this.mStbPipchannel        = pipCh;
    }

    /**
     * 0 : 셋탑미연동 1: 시청예약:녹화예약, 2: 시청예약:녹화예약취소, 3: 시청예약취소:녹화예약, 4: 시청예약취소:녹화예약취소
     * */
    @Override
    public int getItemViewType(int position) {
        mToday = new Date();

        SearchProgramDataObject item = getItem(position);
        if (item == null) {
            return 0;
        }
        if (!mPref.isPairingCompleted()) {
            return 0;
        } else {
            // 오늘이 아니라면...
            if (!mTodayFormat.format(mToday).equals(CMUtil.getConverDateString(item.getChannelProgramTime().trim(), "yyyy-MM-ddHH:mm:ss", "yyyyMMdd"))) {
                JSONObject reservItem = getStbRecordReserveWithChunnelId(item.getChannelId(),item);
                // 예약녹화 걸려있지 않은 방송
                if (reservItem == null) {
                    // 시청예약 걸려있음
                    if (mPref.isWatchTvReserveWithProgramId(item.getChannelProgramID()) == true) {
                        return 3;
                    }
                    // 시청예약 없음
                    else {
                        return 1;
                    }
                }
                // 예약녹화 걸린방송
                else {
                    // 시청예약 걸려 있음
                    if (mPref.isWatchTvReserveWithProgramId(item.getChannelProgramID()) == true) {
                        return 4;
                    }
                    // 시청예약 없음
                    else {
                        return 2;
                    }
                }
            }
            // 오늘일경우
            else {
                try {
                    Date date = mCompareFormat.parse(item.getChannelProgramTime());
                    //현재방송중이거나 과거방송
                    if (mToday.compareTo(date) > 0) {
                        // 정책변경으로 인해 현재 방송중인 프로그램은 레이어 노출 불가
                        return 0;
//                        item.setSwipeMode(SwipeListView.SWIPE_MODE_NONE);
//                        ((SwipeListView) mParent).setSwipeMode(SwipeListView.SWIPE_MODE_NONE);
                    } else {
                        JSONObject reservItem = getStbRecordReserveWithChunnelId(item.getChannelId(), item);
                        if (reservItem == null) { // 예약녹화 걸려있지 않은 방송.
                            if (mPref.isWatchTvReserveWithProgramId(item.getChannelProgramID()) == true) {
                                return 3;
                            } else {
                                return 1;
                            }
                        } else {    //  예약 녹화 걸린 방송.
                            if (mPref.isWatchTvReserveWithProgramId(item.getChannelProgramID()) == true) {
                                return 4;
                            } else {
                                return 2;
                            }
                        }
                    }

                }catch(Exception e)
                {}
            }

        }

        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 5;
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

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

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

        if (mPref.isBookmarkChannelWithChannelNumber(item.getChannelNumber())) {
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
                    mPref.removeBookmarkChannelWithChannelNumber(channelNO); // 선호채널 제거.
                }
                notifyDataSetChanged();
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

        return convertView;
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
    }

    public JSONObject getStbRecordReserveWithChunnelId(String channelId, SearchProgramDataObject item) {
        try {
            String str = item.getChannelProgramTime();
            for(int i=0;i<mStbRecordReservelist.size();i++)
            {
                JSONObject reservjo = mStbRecordReservelist.get(i);
                String RecordStartTime = reservjo.getString("RecordStartTime");
                String strChannelID = reservjo.getString("ChannelId");
                if (str.equals(RecordStartTime) && channelId.equals(strChannelID)) {  // epg의 시작간과 예약의 시작 시간이 같다면 동일 채널 동일 프로그램.
                    return reservjo;
                }
            }
        } catch ( JSONException e ) {
            e.printStackTrace();
        }
        return null;
    }

}
