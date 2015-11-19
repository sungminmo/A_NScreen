package com.stvn.nscreen.search;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.JYSharedPreferences;
import com.stvn.nscreen.R;
import com.stvn.nscreen.common.SearchProgramDataObject;
import com.stvn.nscreen.common.SwipeListView;
import com.stvn.nscreen.util.CMUtil;

import java.util.List;

public class SearchProgramAdapter extends ArrayAdapter<SearchProgramDataObject> {

    LayoutInflater mInflater;
    private Context mContext;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private View.OnClickListener clickListener;
    private JYSharedPreferences mPref;

    private String mStbState;             // GetSetTopStatus API로 가져오는 값.
    private String mStbRecordingchannel1; // GetSetTopStatus API로 가져오는 값.
    private String mStbRecordingchannel2; // GetSetTopStatus API로 가져오는 값.
    private String mStbWatchingchannel;   // GetSetTopStatus API로 가져오는 값.
    private String mStbPipchannel;        // GetSetTopStatus API로 가져오는 값.

    public SearchProgramAdapter(Context context, List<SearchProgramDataObject> items) {
        super(context, 0, items);
        // TODO Auto-generated constructor stub
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mPref = new JYSharedPreferences(mContext);
        mRequestQueue = Volley.newRequestQueue(mContext);
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(100);

            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }

            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });
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
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

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
            holder.progress = (ProgressBar) convertView.findViewById(R.id.ch_progress);
            holder.notPairing = (Button) convertView.findViewById(R.id.not_pairing);
            holder.recstart = (Button) convertView.findViewById(R.id.rec_start);
            holder.recstop = (Button) convertView.findViewById(R.id.rec_stop);
            holder.watchtv = (Button) convertView.findViewById(R.id.watch_tv);
            holder.setreservationrec = (Button) convertView.findViewById(R.id.set_reservation_rec);
            holder.cancelreservationrec = (Button) convertView.findViewById(R.id.cancel_reservation_rec);
            holder.setreservationwatch = (Button) convertView.findViewById(R.id.set_reservation_watch);
            holder.cancelreservationwatch = (Button) convertView.findViewById(R.id.cancel_reservation_watch);
            holder.recstart.setOnClickListener(clickListener);
            holder.recstop.setOnClickListener(clickListener);
            holder.watchtv.setOnClickListener(clickListener);
            holder.setreservationrec.setOnClickListener(clickListener);
            holder.cancelreservationrec.setOnClickListener(clickListener);
            holder.setreservationwatch.setOnClickListener(clickListener);
            holder.cancelreservationwatch.setOnClickListener(clickListener);

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
        holder.channeltext2.setText(CMUtil.getConverDateString(datetime, "yyyy-MM-ddHH:mm:ss", "yyyy.MM.dd") + " 방송예정");
        holder.chhd.setVisibility(View.VISIBLE);
        if ("HD".equals(item.getChannelProgramHD())) {            // HD방송
            holder.chhd.setImageResource(R.mipmap.btn_size_hd);
        } else if ("SD".equals(item.getChannelProgramHD())) {        // SD방송
            holder.chhd.setImageResource(R.mipmap.btn_size_sd);
        } else                                                    // 둘다 아닐때
            holder.chhd.setVisibility(View.GONE);

        // 연령제한
        if (!TextUtils.isEmpty(item.getChannelProgramGrade())) {
            if (item.getChannelProgramGrade().indexOf("12") > -1) {
                holder.chage.setImageResource(R.mipmap.btn_age_all);
            } else if (item.getChannelProgramGrade().indexOf("15") > -1) {
                holder.chage.setImageResource(R.mipmap.btn_age_15);
            } else if (item.getChannelProgramGrade().indexOf("19") > -1) {
                holder.chage.setImageResource(R.mipmap.btn_age_19);
            } else {
                holder.chage.setImageResource(R.mipmap.btn_age_all);
            }
        }

        if (mPref.isPairingCompleted()) {
            getSwipeLayout(null, position, holder);
        }



        holder.progress.setProgress(position);

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

        if (item == null) {
            holder.notPairing.setVisibility(View.VISIBLE);
        }


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

    class ViewHolder {
        ImageView favoriteimg;
        TextView chaaneltext;
        TextView programname;
        NetworkImageView chlogo;
        TextView chtimetext;
        TextView channeltext2;
        ImageView chage;
        ImageView chhd;
        ProgressBar progress;

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

}
