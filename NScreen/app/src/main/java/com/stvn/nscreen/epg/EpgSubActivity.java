package com.stvn.nscreen.epg;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.LruCache;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.jjiya.android.common.CMConstants;
import com.jjiya.android.common.Constants;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.common.ListViewDataObject;
import com.jjiya.android.http.JYStringRequest;
import com.stvn.nscreen.R;
import com.stvn.nscreen.util.CMAlertUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by limdavid on 15. 9. 11..
 */

public class EpgSubActivity extends AppCompatActivity {

    private static final String                tag = EpgSubActivity.class.getSimpleName();
    private static       EpgSubActivity        mInstance;
    private              JYSharedPreferences   mPref;

    // network
    private              RequestQueue          mRequestQueue;
    private              ProgressDialog        mProgressDialog;
    private              ImageLoader           mImageLoader;
    private              Map<String, Object>   mNetworkError;
    private              Map<String, Object>   RemoteChannelControl;

    private ArrayList<ListViewDataObject>     mDatasAll = new ArrayList<ListViewDataObject>();
    private ArrayList<ListViewDataObject>     mDatas0 = new ArrayList<ListViewDataObject>();
    private ArrayList<ListViewDataObject>     mDatas1 = new ArrayList<ListViewDataObject>();
    private ArrayList<ListViewDataObject>     mDatas2 = new ArrayList<ListViewDataObject>();
    private ArrayList<ListViewDataObject>     mDatas3 = new ArrayList<ListViewDataObject>();
    private ArrayList<ListViewDataObject>     mDatas4 = new ArrayList<ListViewDataObject>();
    private ArrayList<ListViewDataObject>     mDatas5 = new ArrayList<ListViewDataObject>();
    private ArrayList<ListViewDataObject>     mDatas6 = new ArrayList<ListViewDataObject>();

    // STB status
    private              String                          mStbState;             // GetSetTopStatus API로 가져오는 값.
    private              String                          mStbRecordingchannel1; // GetSetTopStatus API로 가져오는 값.
    private              String                          mStbRecordingchannel2; // GetSetTopStatus API로 가져오는 값.
    private              String                          mStbWatchingchannel;   // GetSetTopStatus API로 가져오는 값.
    private              String                          mStbPipchannel;        // GetSetTopStatus API로 가져오는 값.

    private              ArrayList<JSONObject>           mStbRecordReservelist; // 녹화예약목록.

    // gui
    private              EpgSubListViewAdapter mAdapter;
    private              SwipeMenuListView     mListView;

    private              String                sChannelNumber, sChannelId, sChannelName, sChannelLogoImg, programTitle;

    private              NetworkImageView      epg_sub_channelLogoImg;

    private              TextView              epg_sub_channelNumber, epg_sub_channelName, epg_sub_date1, epg_sub_date2, epg_sub_date3, epg_sub_date4, epg_sub_date5, epg_sub_date6, epg_sub_date7;

    private              Calendar              cal;

    private              LinearLayout          epg_sub_date_linearlayout1, epg_sub_date_linearlayout2, epg_sub_date_linearlayout3, epg_sub_date_linearlayout4, epg_sub_date_linearlayout5, epg_sub_date_linearlayout6, epg_sub_date_linearlayout7;
    private              ImageView             imageView21, imageView22, imageView23, imageView24, imageView25, imageView26, imageView27;

    private              ImageButton           backBtn, bookmarkImageButton, epg_sub_left_arrow, epg_sub_right_arrow;

    private              HorizontalScrollView  mChannelScrollView;

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epg_sub);

        mInstance               = this;
        mPref                   = new JYSharedPreferences(this);
        mRequestQueue           = Volley.newRequestQueue(this);
        this.mImageLoader       = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(100);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });
        mNetworkError = new HashMap<String, Object>();
        RemoteChannelControl = new HashMap<String, Object>();
        mStbRecordReservelist = new ArrayList<JSONObject>();

        Date date = new Date();

        SimpleDateFormat formatter = new SimpleDateFormat("MM.dd");

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String dt21 = formatter.format(cal.getTime());
        cal.add(Calendar.DATE, 1);
        String dt22 = formatter.format(cal.getTime());
        cal.add(Calendar.DATE, 1);
        String dt23 = formatter.format(cal.getTime());
        cal.add(Calendar.DATE, 1);
        String dt24 = formatter.format(cal.getTime());
        cal.add(Calendar.DATE, 1);
        String dt25 = formatter.format(cal.getTime());
        cal.add(Calendar.DATE, 1);
        String dt26 = formatter.format(cal.getTime());
        cal.add(Calendar.DATE, 1);
        String dt27 = formatter.format(cal.getTime());

        sChannelNumber             = getIntent().getExtras().getString("channelNumber");
        sChannelId                 = getIntent().getExtras().getString("channelId");
        sChannelName               = getIntent().getExtras().getString("channelName");
        sChannelLogoImg            = getIntent().getExtras().getString("channelLogoImg");

        if (mPref.isLogging()) { Log.d(tag, "onCreate()"); }

        epg_sub_date_linearlayout1 = (LinearLayout) findViewById(R.id.epg_sub_date_linearlayout1);
        epg_sub_date_linearlayout2 = (LinearLayout) findViewById(R.id.epg_sub_date_linearlayout2);
        epg_sub_date_linearlayout3 = (LinearLayout) findViewById(R.id.epg_sub_date_linearlayout3);
        epg_sub_date_linearlayout4 = (LinearLayout) findViewById(R.id.epg_sub_date_linearlayout4);
        epg_sub_date_linearlayout5 = (LinearLayout) findViewById(R.id.epg_sub_date_linearlayout5);
        epg_sub_date_linearlayout6 = (LinearLayout) findViewById(R.id.epg_sub_date_linearlayout6);
        epg_sub_date_linearlayout7 = (LinearLayout) findViewById(R.id.epg_sub_date_linearlayout7);
        epg_sub_channelNumber      = (TextView) findViewById(R.id.epg_sub_channelNumber);
        epg_sub_channelName        = (TextView) findViewById(R.id.epg_sub_channelName);
        epg_sub_date1              = (TextView) findViewById(R.id.epg_sub_date1);
        epg_sub_date2              = (TextView) findViewById(R.id.epg_sub_date2);
        epg_sub_date3              = (TextView) findViewById(R.id.epg_sub_date3);
        epg_sub_date4              = (TextView) findViewById(R.id.epg_sub_date4);
        epg_sub_date5              = (TextView) findViewById(R.id.epg_sub_date5);
        epg_sub_date6              = (TextView) findViewById(R.id.epg_sub_date6);
        epg_sub_date7              = (TextView) findViewById(R.id.epg_sub_date7);
        imageView21                = (ImageView) findViewById(R.id.imageView21);
        imageView22                = (ImageView) findViewById(R.id.imageView22);
        imageView23                = (ImageView) findViewById(R.id.imageView23);
        imageView24                = (ImageView) findViewById(R.id.imageView24);
        imageView25                = (ImageView) findViewById(R.id.imageView25);
        imageView26                = (ImageView) findViewById(R.id.imageView26);
        imageView27                = (ImageView) findViewById(R.id.imageView27);
        epg_sub_left_arrow         = (ImageButton) findViewById(R.id.epg_sub_left_arrow);
        epg_sub_right_arrow        = (ImageButton) findViewById(R.id.epg_sub_right_arrow);
        epg_sub_channelLogoImg     = (NetworkImageView) findViewById(R.id.epg_sub_imageview_channel_logo);
        backBtn                    = (ImageButton) findViewById(R.id.backBtn);
        bookmarkImageButton        = (ImageButton)findViewById(R.id.epg_sub_bookmark_imagebutton);
        mChannelScrollView         = (HorizontalScrollView) findViewById(R.id.scrollView3);

        mChannelScrollView.post(new Runnable(){
            @Override
            public void run() {
                ViewTreeObserver observer = mChannelScrollView.getViewTreeObserver();
                observer.addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener(){
                    @Override
                    public void onScrollChanged() {
                        int x = mChannelScrollView.getScrollX();
                        int barsize = mChannelScrollView.getScrollBarSize();
                        int width = mChannelScrollView.getWidth();
                        if ( (width-10) <= x ) {
                            //Log.d(tag, "왼쪽에 화살표 찍어라");
                            epg_sub_left_arrow.setImageResource(R.mipmap.series_arrow_01);
                            epg_sub_right_arrow.setImageResource(R.mipmap.series_arrow_02_dim);
                        } else if ( 10 >= x ) {
                            //Log.d(tag, "오른쪽에 화살표 찍어라");
                            epg_sub_left_arrow.setImageResource(R.mipmap.series_arrow_01_dim);
                            epg_sub_right_arrow.setImageResource(R.mipmap.series_arrow_02);
                        } else {
                            epg_sub_left_arrow.setImageResource(R.mipmap.series_arrow_01);
                            epg_sub_right_arrow.setImageResource(R.mipmap.series_arrow_02);
                        }
                        Log.d(tag, "x: " + x + ", width: " + width);
                    }
                });
            }
        });

        epg_sub_channelNumber.setText("CH." + sChannelNumber);
        epg_sub_channelName.setText(sChannelName);

        epg_sub_date1.setText(dt21);
        epg_sub_date2.setText(dt22);
        epg_sub_date3.setText(dt23);
        epg_sub_date4.setText(dt24);
        epg_sub_date5.setText(dt25);
        epg_sub_date6.setText(dt26);
        epg_sub_date7.setText(dt27);

        epg_sub_channelLogoImg.setImageUrl(sChannelLogoImg, mImageLoader);

        mAdapter              = new EpgSubListViewAdapter(this, null);
        mAdapter.setChannelIdChannelNumberChannelName(sChannelId, sChannelNumber, sChannelName);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });

        if ( mPref.isBookmarkChannelWithChannelId(sChannelId) == true ) {
            bookmarkImageButton.setImageResource(R.mipmap.icon_favorite_select);
        } else {
            bookmarkImageButton.setImageResource(R.mipmap.icon_favorite_unselect);
        }
        bookmarkImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( mPref.isBookmarkChannelWithChannelId(sChannelId) == true ) {
                    mPref.removeBookmarkChannelWithChannelId(sChannelId);
                    bookmarkImageButton.setImageResource(R.mipmap.icon_favorite_unselect);
                } else {
                    mPref.addBookmarkChannel(sChannelId, sChannelNumber, sChannelName);
                    bookmarkImageButton.setImageResource(R.mipmap.icon_favorite_select);
                }
            }
        });

        epg_sub_date_linearlayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epg_sub_date_linearlayout1.setSelected(true);
                imageView21.setVisibility(View.VISIBLE);
                epg_sub_date_linearlayout2.setSelected(false);
                imageView22.setVisibility(View.INVISIBLE);
                epg_sub_date_linearlayout3.setSelected(false);
                imageView23.setVisibility(View.INVISIBLE);
                epg_sub_date_linearlayout4.setSelected(false);
                imageView24.setVisibility(View.INVISIBLE);
                epg_sub_date_linearlayout5.setSelected(false);
                imageView25.setVisibility(View.INVISIBLE);
                epg_sub_date_linearlayout6.setSelected(false);
                imageView26.setVisibility(View.INVISIBLE);
                epg_sub_date_linearlayout7.setSelected(false);
                imageView27.setVisibility(View.INVISIBLE);

                mAdapter.setDatas(mDatas0, 0);
                mAdapter.notifyDataSetChanged();
            }
        });

        epg_sub_date_linearlayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epg_sub_date_linearlayout1.setSelected(false);
                imageView21.setVisibility(View.INVISIBLE);
                epg_sub_date_linearlayout2.setSelected(true);
                imageView22.setVisibility(View.VISIBLE);
                epg_sub_date_linearlayout3.setSelected(false);
                imageView23.setVisibility(View.INVISIBLE);
                epg_sub_date_linearlayout4.setSelected(false);
                imageView24.setVisibility(View.INVISIBLE);
                epg_sub_date_linearlayout5.setSelected(false);
                imageView25.setVisibility(View.INVISIBLE);
                epg_sub_date_linearlayout6.setSelected(false);
                imageView26.setVisibility(View.INVISIBLE);
                epg_sub_date_linearlayout7.setSelected(false);
                imageView27.setVisibility(View.INVISIBLE);

                mAdapter.setDatas(mDatas1, 1);
                mAdapter.notifyDataSetChanged();
            }
        });

        epg_sub_date_linearlayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epg_sub_date_linearlayout1.setSelected(false);
                imageView21.setVisibility(View.INVISIBLE);
                epg_sub_date_linearlayout2.setSelected(false);
                imageView22.setVisibility(View.INVISIBLE);
                epg_sub_date_linearlayout3.setSelected(true);
                imageView23.setVisibility(View.VISIBLE);
                epg_sub_date_linearlayout4.setSelected(false);
                imageView24.setVisibility(View.INVISIBLE);
                epg_sub_date_linearlayout5.setSelected(false);
                imageView25.setVisibility(View.INVISIBLE);
                epg_sub_date_linearlayout6.setSelected(false);
                imageView26.setVisibility(View.INVISIBLE);
                epg_sub_date_linearlayout7.setSelected(false);
                imageView27.setVisibility(View.INVISIBLE);

                mAdapter.setDatas(mDatas2, 2);
                mAdapter.notifyDataSetChanged();
            }
        });

        epg_sub_date_linearlayout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epg_sub_date_linearlayout1.setSelected(false);
                imageView21.setVisibility(View.INVISIBLE);
                epg_sub_date_linearlayout2.setSelected(false);
                imageView22.setVisibility(View.INVISIBLE);
                epg_sub_date_linearlayout3.setSelected(false);
                imageView23.setVisibility(View.INVISIBLE);
                epg_sub_date_linearlayout4.setSelected(true);
                imageView24.setVisibility(View.VISIBLE);
                epg_sub_date_linearlayout5.setSelected(false);
                imageView25.setVisibility(View.INVISIBLE);
                epg_sub_date_linearlayout6.setSelected(false);
                imageView26.setVisibility(View.INVISIBLE);
                epg_sub_date_linearlayout7.setSelected(false);
                imageView27.setVisibility(View.INVISIBLE);

                mAdapter.setDatas(mDatas3, 3);
                mAdapter.notifyDataSetChanged();
            }
        });

        epg_sub_date_linearlayout5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epg_sub_date_linearlayout1.setSelected(false);
                imageView21.setVisibility(View.INVISIBLE);
                epg_sub_date_linearlayout2.setSelected(false);
                imageView22.setVisibility(View.INVISIBLE);
                epg_sub_date_linearlayout3.setSelected(false);
                imageView23.setVisibility(View.INVISIBLE);
                epg_sub_date_linearlayout4.setSelected(false);
                imageView24.setVisibility(View.INVISIBLE);
                epg_sub_date_linearlayout5.setSelected(true);
                imageView25.setVisibility(View.VISIBLE);
                epg_sub_date_linearlayout6.setSelected(false);
                imageView26.setVisibility(View.INVISIBLE);
                epg_sub_date_linearlayout7.setSelected(false);
                imageView27.setVisibility(View.INVISIBLE);

                mAdapter.setDatas(mDatas4, 4);
                mAdapter.notifyDataSetChanged();
            }
        });

        epg_sub_date_linearlayout6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epg_sub_date_linearlayout1.setSelected(false);
                imageView21.setVisibility(View.INVISIBLE);
                epg_sub_date_linearlayout2.setSelected(false);
                imageView22.setVisibility(View.INVISIBLE);
                epg_sub_date_linearlayout3.setSelected(false);
                imageView23.setVisibility(View.INVISIBLE);
                epg_sub_date_linearlayout4.setSelected(false);
                imageView24.setVisibility(View.INVISIBLE);
                epg_sub_date_linearlayout5.setSelected(false);
                imageView25.setVisibility(View.INVISIBLE);
                epg_sub_date_linearlayout6.setSelected(true);
                imageView26.setVisibility(View.VISIBLE);
                epg_sub_date_linearlayout7.setSelected(false);
                imageView27.setVisibility(View.INVISIBLE);

                mAdapter.setDatas(mDatas5, 5);
                mAdapter.notifyDataSetChanged();
            }
        });

        epg_sub_date_linearlayout7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epg_sub_date_linearlayout1.setSelected(false);
                imageView21.setVisibility(View.INVISIBLE);
                epg_sub_date_linearlayout2.setSelected(false);
                imageView22.setVisibility(View.INVISIBLE);
                epg_sub_date_linearlayout3.setSelected(false);
                imageView23.setVisibility(View.INVISIBLE);
                epg_sub_date_linearlayout4.setSelected(false);
                imageView24.setVisibility(View.INVISIBLE);
                epg_sub_date_linearlayout5.setSelected(false);
                imageView25.setVisibility(View.INVISIBLE);
                epg_sub_date_linearlayout6.setSelected(false);
                imageView26.setVisibility(View.INVISIBLE);
                epg_sub_date_linearlayout7.setSelected(true);
                imageView27.setVisibility(View.VISIBLE);

                mAdapter.setDatas(mDatas6, 6);
                mAdapter.notifyDataSetChanged();
            }
        });

        mListView = (SwipeMenuListView)findViewById(R.id.epg_sub_listview);
        mListView.setAdapter(mAdapter);
        mListView.setMenuCreator(creator);

        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                ListViewDataObject item = (ListViewDataObject) mAdapter.getItem(position);
                int iMenuType = mAdapter.getItemViewType(position);
                switch (iMenuType) {
                    case 0: { // TV로 시청 / 즉시녹화
                        if (index == 0) { // left button
                            requestSetRemoteChannelControl(sChannelId);
                        } else { // right button
                            requestSetRecord(sChannelId);
                        }
                    }
                    break;
                    case 1: { // TV로 시청 / 녹화중지
                        if (index == 0) { // left button
                            requestSetRemoteChannelControl(sChannelId);
                        } else { // right button
                            requestSetRecordStop(sChannelId);
                        }
                    }
                    break;
                    case 2: { // 시청예약 / 녹화예약
                        if (index == 0) { // left button
                            try {
                                JSONObject jo = new JSONObject(item.sJson);
                                String programId = jo.getString("programId");
                                String seriesId = jo.getString("seriesId");
                                programTitle = jo.getString("programTitle");
                                String programBroadcastingStartTime = jo.getString("programBroadcastingStartTime");
                                mPref.addWatchTvReserveAlarm(programId, seriesId, programTitle, programBroadcastingStartTime);
                                mAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else { // right button
                            try {
                                JSONObject jo = new JSONObject(item.sJson);
                                final String sSeriesId = jo.getString("seriesId");
                                final String programBroadcastingStartTime = jo.getString("programBroadcastingStartTime");
                                if ( "".equals(sSeriesId) ) {
                                    requestSetRecordReserve(sChannelId, programBroadcastingStartTime);
                                } else if ( !"".equals(sSeriesId) ) {
                                    String alertTitle = "녹화예약확인";
                                    String alertMsg1 = programTitle;
                                    String alertMsg2 = getString(R.string.error_not_paring_compleated8);
                                    CMAlertUtil.Alert_series_reserve(mInstance, alertTitle, alertMsg1, alertMsg2, false, true, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestSetRecordSeriesReserve(sChannelId, sSeriesId, programBroadcastingStartTime);
                                        }
                                    }, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestSetRecordReserve(sChannelId, programBroadcastingStartTime);
                                        }
                                    }, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
                    case 3: { // 시청예약 / 녹화예약취소
                        if (index == 0) { // left button
                            try {
                                JSONObject jo = new JSONObject(item.sJson);
                                String programId = jo.getString("programId");
                                String seriesId = jo.getString("seriesId");
                                String programTitle = jo.getString("programTitle");
                                String programBroadcastingStartTime = jo.getString("programBroadcastingStartTime");
                                mPref.addWatchTvReserveAlarm(programId, seriesId, programTitle, programBroadcastingStartTime);
                                mAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else { // right button
                            try {
                                JSONObject reservjo = mAdapter.getStbRecordReserveWithChunnelId(sChannelId, item);
                                String starttime = null;
                                starttime = reservjo.getString("RecordStartTime");
                                String seriesid = reservjo.getString("SeriesId");
                                requestSetRecordCancelReserve(sChannelId, starttime, seriesid);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
                    case 4: { // 시청예약취소 / 녹화예약
                        if (index == 0) { // left button
                            try {
                                JSONObject jo = new JSONObject(item.sJson);
                                String programId = jo.getString("programId");
                                mPref.removeWatchTvReserveAlarm(programId);
                                mAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else { // right button
                            try {
                                JSONObject jo = new JSONObject(item.sJson);
                                final String sSeriesId = jo.getString("seriesId");
                                final String programBroadcastingStartTime = jo.getString("programBroadcastingStartTime");
                                if ( "".equals(sSeriesId) ) {
                                    requestSetRecordReserve(sChannelId, programBroadcastingStartTime);
                                } else if ( !"".equals(sSeriesId) ) {
                                    String alertTitle = "녹화예약확인";
                                    String alertMsg1 = programTitle;
                                    String alertMsg2 = getString(R.string.error_not_paring_compleated8);
                                    CMAlertUtil.Alert_series_delete(mInstance, alertTitle, alertMsg1, alertMsg2, false, true, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestSetRecordSeriesReserve(sChannelId, sSeriesId, programBroadcastingStartTime);
                                        }
                                    }, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestSetRecordReserve(sChannelId, programBroadcastingStartTime);
                                        }
                                    }, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
//                            try {
//                                JSONObject reservjo = mAdapter.getStbRecordReserveWithChunnelId(sChannelId, item);
//                                String starttime = null;
//                                starttime = reservjo.getString("RecordStartTime");
//                                requestSetRecordReserve(sChannelId, starttime);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
                        }
                    }
                    break;
                    case 5: { // 시청예약취소 / 녹화예약취소
                        if (index == 0) { // left button
                            try {
                                JSONObject jo = new JSONObject(item.sJson);
                                String programId = jo.getString("programId");
                                mPref.removeWatchTvReserveAlarm(programId);
                                mAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else { // right button
                            // requestSetRecordCancelReserve(sChannelId);
                            try {
                                JSONObject reservjo = mAdapter.getStbRecordReserveWithChunnelId(sChannelId, item);
                                String starttime = null;
                                starttime = reservjo.getString("RecordStartTime");
                                String seriesid = reservjo.getString("SeriesId");
                                requestSetRecordCancelReserve(sChannelId, starttime, seriesid);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
                    case 6: { // 미 페어링
                        String alertTitle = "셋탑박스 연동 필요";
                        String alertMessage1 = getString(R.string.error_not_paring_compleated3);
                        String alertMessage2 = "";
                        CMAlertUtil.Alert(mInstance, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }, true);
                    }
                    break;
                    case 7: { // TV로 시청
                        requestSetRemoteChannelControl(sChannelId);
                    }
                    break;
                    case 8: { // 시청예약
                        try {
                            JSONObject jo = new JSONObject(item.sJson);
                            String programId = jo.getString("programId");
                            String seriesId = jo.getString("seriesId");
                            String programTitle = jo.getString("programTitle");
                            String programBroadcastingStartTime = jo.getString("programBroadcastingStartTime");
                            mPref.addWatchTvReserveAlarm(programId, seriesId, programTitle, programBroadcastingStartTime);
                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                    case 9: { // 시청예약취소
                        try {
                            JSONObject jo = new JSONObject(item.sJson);
                            String programId = jo.getString("programId");
                            mPref.removeWatchTvReserveAlarm(programId);
                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
                return false;
            }
        });

        if ( mPref.isPairingCompleted() == true ) { // 페어링 안했을 시
            if ( "SMART".equals(mPref.getSettopBoxKind()) ) {
                requestGetChannelSchedule();
            } else {
                requestGetSetTopStatus(); // 셋탑 상태 - 예약녹화물 리스트 - 한 채널 평성표 차례대로 호출.
            }
        } else { // 페어링 했을 시
            requestGetChannelSchedule(); // 한 채널 평성표 호출.
        }
    }

    private void reloadAll() {
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();
        mNetworkError.clear();
        mStbRecordReservelist.clear();
        mDatasAll.clear();
        mDatas0.clear();
        mDatas1.clear();
        mDatas2.clear();
        mDatas3.clear();
        mDatas4.clear();
        mDatas5.clear();
        mDatas6.clear();

        requestGetSetTopStatus();
    }



    /**
     * Swipe Menu for ListView
     */
    SwipeMenuCreator creator = new SwipeMenuCreator() {
        @Override
        public void create(SwipeMenu menu) {
            // Create different menus depending on the view type
            switch (menu.getViewType()) {
                case 0: {
                    createMenu0(menu);  // TV로 시청 / 즉시녹화
                } break;
                case 1: {
                    createMenu1(menu);  // TV로 시청 / 녹화중지
                } break;
                case 2: {
                    createMenu2(menu);  // 시청예약 / 녹화예약
                } break;
                case 3: {
                    createMenu3(menu);  // 시청예약 / 녹화예약취소
                } break;
                case 4: {
                    createMenu4(menu);  // 시청예약취소 / 녹화예약
                } break;
                case 5: {
                    createMenu5(menu);  // 시청예약취소 / 녹화예약취소
                } break;
                case 6: {
                    createMenu6(menu); // 미 페어링 상태
                } break;
                case 7: {
                    createMenu7(menu); // TV로 시청
                } break;
                case 8: {
                    createMenu8(menu); // 시청에약
                } break;
                case 9: {
                    createMenu9(menu); // 시청예약취소
                }
            }
        }

        private int dp2px(int dp) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
        }

        private void createMenu0(SwipeMenu menu) { // TV로 시청 / 즉시녹화
            SwipeMenuItem item1 = new SwipeMenuItem(getApplicationContext());
            item1.setBackground(new ColorDrawable(Color.rgb(0xF7, 0xBD, 0x33)));
            item1.setWidth(dp2px(90));
            item1.setTitle("TV로 시청");
            item1.setTitleSize(12);
            item1.setTitleColor(Color.WHITE);
            menu.addMenuItem(item1);
            SwipeMenuItem item2 = new SwipeMenuItem(getApplicationContext());
            item2.setBackground(new ColorDrawable(Color.rgb(0xC4, 0x5C, 0xC2)));
            item2.setWidth(dp2px(90));
            item2.setTitle("즉시녹화");
            item2.setTitleSize(12);
            item2.setTitleColor(Color.WHITE);
            menu.addMenuItem(item2);
        }

        private void createMenu1(SwipeMenu menu) { // TV로 시청 / 녹화중지
            SwipeMenuItem item1 = new SwipeMenuItem(getApplicationContext());
            item1.setBackground(new ColorDrawable(Color.rgb(0xF7, 0xBD, 0x33)));
            item1.setWidth(dp2px(90));
            item1.setTitle("TV로 시청");
            item1.setTitleSize(12);
            item1.setTitleColor(Color.WHITE);
            menu.addMenuItem(item1);
            SwipeMenuItem item2 = new SwipeMenuItem(getApplicationContext());
            item2.setBackground(new ColorDrawable(Color.rgb(0xEA, 0x55, 0x55)));
            item2.setWidth(dp2px(90));
            item2.setTitle("녹화중지");
            item2.setTitleSize(12);
            item2.setTitleColor(Color.WHITE);
            menu.addMenuItem(item2);
        }

        private void createMenu2(SwipeMenu menu) { // 시청예약 / 녹화예약
            SwipeMenuItem item1 = new SwipeMenuItem(getApplicationContext());
            item1.setBackground(new ColorDrawable(Color.rgb(0xB3, 0xCF, 0x3B)));
            item1.setWidth(dp2px(90));
            item1.setTitle("시청예약");
            item1.setTitleSize(12);
            item1.setTitleColor(Color.WHITE);
            menu.addMenuItem(item1);
            SwipeMenuItem item2 = new SwipeMenuItem(getApplicationContext());
            item2.setBackground(new ColorDrawable(Color.rgb(0xED, 0x72, 0x33)));
            item2.setWidth(dp2px(90));
            item2.setTitle("녹화예약");
            item2.setTitleSize(12);
            item2.setTitleColor(Color.WHITE);
            menu.addMenuItem(item2);
        }

        private void createMenu3(SwipeMenu menu) { // 시청예약 / 녹화예약취소
            SwipeMenuItem item1 = new SwipeMenuItem(getApplicationContext());
            item1.setBackground(new ColorDrawable(Color.rgb(0xB3, 0xCF, 0x3B)));
            item1.setWidth(dp2px(90));
            item1.setTitle("시청예약");
            item1.setTitleSize(12);
            item1.setTitleColor(Color.WHITE);;
            menu.addMenuItem(item1);
            SwipeMenuItem item2 = new SwipeMenuItem(getApplicationContext());
            item2.setBackground(new ColorDrawable(Color.rgb(0xC1, 0x4F, 0x28)));
            item2.setWidth(dp2px(90));
            item2.setTitle("녹화예약취소");
            item2.setTitleSize(12);
            item2.setTitleColor(Color.WHITE);
            menu.addMenuItem(item2);
        }

        private void createMenu4(SwipeMenu menu) { // 시청예약취소 / 녹화예약
            SwipeMenuItem item1 = new SwipeMenuItem(getApplicationContext());
            item1.setBackground(new ColorDrawable(Color.rgb(0x7F, 0x94, 0x24)));
            item1.setWidth(dp2px(90));
            item1.setTitle("시청예약취소");
            item1.setTitleSize(12);
            item1.setTitleColor(Color.WHITE);
            menu.addMenuItem(item1);
            SwipeMenuItem item2 = new SwipeMenuItem(getApplicationContext());
            item2.setBackground(new ColorDrawable(Color.rgb(0xED, 0x72, 0x33)));
            item2.setWidth(dp2px(90));
            item2.setTitle("녹화예약");
            item2.setTitleSize(12);
            item2.setTitleColor(Color.WHITE);
            menu.addMenuItem(item2);
        }

        private void createMenu5(SwipeMenu menu) { // 시청예약취소 / 녹화예약취소
            SwipeMenuItem item1 = new SwipeMenuItem(getApplicationContext());
            item1.setBackground(new ColorDrawable(Color.rgb(0x7F, 0x94, 0x24)));
            item1.setWidth(dp2px(90));
            item1.setTitle("시청예약취소");
            item1.setTitleSize(12);
            item1.setTitleColor(Color.WHITE);
            menu.addMenuItem(item1);
            SwipeMenuItem item2 = new SwipeMenuItem(getApplicationContext());
            item2.setBackground(new ColorDrawable(Color.rgb(0xC1, 0x4F, 0x28)));
            item2.setWidth(dp2px(90));
            item2.setTitle("녹화예약취소");
            item2.setTitleSize(12);
            item2.setTitleColor(Color.WHITE);
            menu.addMenuItem(item2);
        }

        private void createMenu6(SwipeMenu menu) { // 미 페어링 상태
            SwipeMenuItem item1 = new SwipeMenuItem(getApplicationContext());
            item1.setBackground(new ColorDrawable(Color.rgb(0x00, 0x00, 0x00)));
            item1.setWidth(dp2px(180));
            item1.setTitle("셋탑 미 연동");
            item1.setTitleSize(12);
            item1.setTitleColor(Color.WHITE);
            menu.addMenuItem(item1);
        }

        private void createMenu7(SwipeMenu menu) { // TV로 시청
            SwipeMenuItem item1 = new SwipeMenuItem(getApplicationContext());
            item1.setBackground(new ColorDrawable(Color.rgb(0xF7, 0xBD, 0x33)));
            item1.setWidth(dp2px(90));
            item1.setTitle("TV로 시청");
            item1.setTitleSize(12);
            item1.setTitleColor(Color.WHITE);
            menu.addMenuItem(item1);
        }

        private void createMenu8(SwipeMenu menu) { // 시청예약
            SwipeMenuItem item1 = new SwipeMenuItem(getApplicationContext());
            item1.setBackground(new ColorDrawable(Color.rgb(0xB3, 0xCF, 0x3B)));
            item1.setWidth(dp2px(90));
            item1.setTitle("시청예약");
            item1.setTitleSize(12);
            item1.setTitleColor(Color.WHITE);;
            menu.addMenuItem(item1);
        }

        private void createMenu9(SwipeMenu menu) { // 시청예약취소
            SwipeMenuItem item1 = new SwipeMenuItem(getApplicationContext());
            item1.setBackground(new ColorDrawable(Color.rgb(0x7F, 0x94, 0x24)));
            item1.setWidth(dp2px(90));
            item1.setTitle("시청예약취소");
            item1.setTitleSize(12);
            item1.setTitleColor(Color.WHITE);
            menu.addMenuItem(item1);
        }
    };

    // http://58.141.255.80/SMApplicationServer/GetSetTopStatus.asp?deviceId=86713f34-15f4-45ba-b1df-49b32b13d551
    // 7.3.40 GetSetTopStatus
    // 셋탑의 상태 확인용.
    private void requestGetSetTopStatus() {
        if ( mPref.isLogging() ) { Log.d(tag, "requestGetSetTopStatus()"); }
        String uuid        = mPref.getValue(JYSharedPreferences.UUID, "");
        String url         = mPref.getRumpersServerUrl() + "/GetSetTopStatus.asp?deviceId="+uuid;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                parseGetSetTopStatus(response);

                String resultCode = (String) mNetworkError.get("resultCode");
                if ( Constants.CODE_RUMPUS_OK.equals(resultCode) ) {
                    //
                    mStbState             = (String)mNetworkError.get("state");
                    mStbRecordingchannel1 = (String)mNetworkError.get("recordingchannel1");
                    mStbRecordingchannel2 = (String)mNetworkError.get("recordingchannel2");
                    mStbWatchingchannel   = (String)mNetworkError.get("watchingchannel");
                    mStbPipchannel        = (String)mNetworkError.get("pipchannel");

                    if ( "1".equals(mStbState) ) { // VOD 시청중.

                    } else if ( "2".equals(mStbState) ) { // 독립형.

                    } else if ( "5".equals(mStbState) ) { // 개인 미디어 시청중.

                    }
                    mAdapter.setStbState(mStbState, mStbRecordingchannel1, mStbRecordingchannel2, mStbWatchingchannel, mStbPipchannel);

                    requestGetRecordReservelist();
                } else if ( "206".equals(resultCode) ) { // 셋탑박스의 전원을 off하면 이값의 응답을 받지만, 정상처리 해줘야 한다.
                    //
                    mStbState             = "";
                    mStbRecordingchannel1 = "";
                    mStbRecordingchannel2 = "";
                    mStbWatchingchannel   = "";
                    mStbPipchannel        = "";
                    mAdapter.setStbState(mStbState, mStbRecordingchannel1, mStbRecordingchannel2, mStbWatchingchannel, mStbPipchannel);
                    String alertTitle = "씨앤앰";
                    String alertMessage1 = "셋탑박스와 통신이 끊어졌습니다.\n전원을 확인해주세요.";
                    String alertMessage2 = "";
                    CMAlertUtil.Alert(mInstance, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, true);
                } else if ( "028".equals(resultCode) ) { // 셋탑박스의 전원을 off하면 이값의 응답을 받지만, 정상처리 해줘야 한다.
                    //
                    mStbState             = "";
                    mStbRecordingchannel1 = "";
                    mStbRecordingchannel2 = "";
                    mStbWatchingchannel   = "";
                    mStbPipchannel        = "";
                    mAdapter.setStbState(mStbState, mStbRecordingchannel1, mStbRecordingchannel2, mStbWatchingchannel, mStbPipchannel);
                    String alertTitle = "씨앤앰";
                    String alertMessage1 = "셋탑박스와 통신이 끊어졌습니다.\n전원을 확인해주세요.";
                    String alertMessage2 = "";
                    CMAlertUtil.Alert(mInstance, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, true);
                } else {
                    String errorString = (String)mNetworkError.get("errorString");
                    StringBuilder sb   = new StringBuilder();
                    sb.append("API: GetSetTopStatus\nresultCode: ").append(resultCode).append("\nerrorString: ").append(errorString);
                    AlertDialog.Builder alert = new AlertDialog.Builder(mInstance);
                    alert.setPositiveButton("알림", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.setMessage(sb.toString());
                    alert.show();
                }
                requestGetChannelSchedule();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_noconnectionerror), Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_servererror), Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_networkerrorr), Toast.LENGTH_LONG).show();
                }
                if ( mPref.isLogging() ) { VolleyLog.d(tag, "onErrorResponse(): " + error.getMessage()); }
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                if ( mPref.isLogging() ) { Log.d(tag, "getParams()" + params.toString()); }
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    private void parseGetSetTopStatus(String response) {
        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new ByteArrayInputStream(response.getBytes("utf-8")), "utf-8");

            int eventType = xpp.getEventType();
            while ( eventType != XmlPullParser.END_DOCUMENT ) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equalsIgnoreCase("resultCode")) {
                        String resultCode = xpp.nextText();
                        mNetworkError.put("resultCode", resultCode);
                    } else if (xpp.getName().equalsIgnoreCase("errorString")) {
                        String errorString = xpp.nextText();
                        mNetworkError.put("errorString", errorString);

                    } else if (xpp.getName().equalsIgnoreCase("state")) {
                        String errorString = xpp.nextText();
                        mNetworkError.put("state", errorString);
                    } else if (xpp.getName().equalsIgnoreCase("recordingchannel1")) {
                        String errorString = xpp.nextText();
                        mNetworkError.put("recordingchannel1", errorString);
                    } else if (xpp.getName().equalsIgnoreCase("recordingchannel2")) {
                        String errorString = xpp.nextText();
                        mNetworkError.put("recordingchannel2", errorString);
                    } else if (xpp.getName().equalsIgnoreCase("watchingchannel")) {
                        String errorString = xpp.nextText();
                        mNetworkError.put("watchingchannel", errorString);
                    } else if (xpp.getName().equalsIgnoreCase("pipchannel")) {
                        String errorString = xpp.nextText();
                        mNetworkError.put("pipchannel", errorString);
                    }
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /* 녹화 예약 목록 호출 */
    private void requestGetRecordReservelist() {
        mProgressDialog	        = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestGetRecordReservelist()"); }
        String          uuid    = mPref.getValue(JYSharedPreferences.UUID, "");
        String          tk      = JYSharedPreferences.RUMPERS_TERMINAL_KEY;
        // for test
        //String          url     = "http://192.168.44.10/SMApplicationserver/getrecordReservelist.asp?Version=1&terminalKey=C5E6DBF75F13A2C1D5B2EFDB2BC940&deviceId=68590725-3b42-4cea-ab80-84c91c01bad2";
        String          url     = mPref.getRumpersServerUrl() + "/getRecordReservelist.asp?Version=1&terminalKey=" + tk + "&deviceId=" + uuid;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(tag, response);
                mProgressDialog.dismiss();
                String sResultCode = parseGetRecordReservelist(response); // 파싱 결과를 리턴 받는다.
                if ( Constants.CODE_RUMPUS_OK.equals(sResultCode) ) { // 예약목록을 받았을 때
                    // requestGetChannelSchedule();
                } else if ( Constants.CODE_RUMPUS_ERROR_205_Not_Found.equals(sResultCode) ) { // 예약 목록이 없을때도 정상응답 받은 거임.
                    // requestGetChannelSchedule();
                } else if ( "206".equals(sResultCode) ) { // 셋탑박스의 전원을 off하면 이값의 응답을 받지만, 정상처리 해줘야 한다.
                    //
                    mStbState             = "";
                    mStbRecordingchannel1 = "";
                    mStbRecordingchannel2 = "";
                    mStbWatchingchannel   = "";
                    mStbPipchannel        = "";
                    mAdapter.setStbState(mStbState, mStbRecordingchannel1, mStbRecordingchannel2, mStbWatchingchannel, mStbPipchannel);
                    String alertTitle = "씨앤앰";
                    String alertMessage1 = "셋탑박스와 통신이 끊어졌습니다.\n전원을 확인해주세요.";
                    String alertMessage2 = "";
                    CMAlertUtil.Alert(mInstance, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, true);
                } else { // 그외는 error
                    String msg = "getRecordReservelist("+sResultCode+":"+mNetworkError.get("errorString")+")";
                    AlertDialog.Builder ad = new AlertDialog.Builder(mInstance);
                    ad.setTitle("알림").setMessage(msg).setCancelable(false)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = ad.create();
                    alert.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                if (error instanceof TimeoutError) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_noconnectionerror), Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_servererror), Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_networkerrorr), Toast.LENGTH_LONG).show();
                }
                if ( mPref.isLogging() ) { VolleyLog.d(tag, "onErrorResponse(): " + error.getMessage()); }
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("version", String.valueOf(1));
                params.put("areaCode", String.valueOf(0));
                if ( mPref.isLogging() ) { Log.d(tag, "getParams()" + params.toString()); }
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    private String parseGetRecordReservelist(String response) {
        String               sResultCode = "0";   // 응답받은 resultCode
        StringBuilder        sb          = new StringBuilder();
        StringBuilder        sb2         = new StringBuilder(); // for array
        XmlPullParserFactory factory     = null;
        List<String> strings     = new ArrayList<String>();

        response = response.replace("<![CDATA[","");
        response = response.replace("]]>", "");
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new ByteArrayInputStream(response.getBytes("UTF-8")), "UTF-8");

            int eventType = xpp.getEventType();
            while ( eventType != XmlPullParser.END_DOCUMENT ) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equalsIgnoreCase("response")) {
                        //
                    } else if (xpp.getName().equalsIgnoreCase("resultCode")) {
                        sResultCode = xpp.nextText(); mNetworkError.put("resultCode",sResultCode);
                    } else if (xpp.getName().equalsIgnoreCase("errorString")) {
                        mNetworkError.put("errorString",xpp.nextText());
                    } else if (xpp.getName().equalsIgnoreCase("Reserve_Item")) {
                        //
                    } else if (xpp.getName().equalsIgnoreCase("RecordingType")) {
                        // array start -------------------------------------------------------------
                        sb2.append("{\"RecordingType\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("OverlapId")) {
                        sb2.append(",\"OverlapId\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("SeriesId")) {
                        sb2.append(",\"SeriesId\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("ScheduleId")) {
                        sb2.append(",\"ScheduleId\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("ChannelId")) {
                        sb2.append(",\"ChannelId\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("ChannelNo")) {
                        sb2.append(",\"ChannelNo\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("ChannelName")) {
                        sb2.append(",\"ChannelName\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("Program_Grade")) {
                        sb2.append(",\"Program_Grade\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("Channel_logo_img")) {
                        sb2.append(",\"Channel_logo_img\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("ProgramName")) {
                        sb2.append(",\"ProgramName\":\"").append(xpp.nextText()).append("\"");  // <![CDATA[ ]]>  한글 깨짐.
                    } else if (xpp.getName().equalsIgnoreCase("RecordStartTime")) {
                        sb2.append(",\"RecordStartTime\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("RecordEndTime")) {
                        sb2.append(",\"RecordEndTime\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("RecordHD")) {
                        sb2.append(",\"RecordHD\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("RecordPaytype")) {
                        sb2.append(",\"RecordPaytype\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("RecordStatus")) {
                        // array end -----------------------------------------------------------
                        sb2.append(",\"RecordStatus\":\"").append(xpp.nextText()).append("\"}");
                        strings.add(sb2.toString());
                        sb2.setLength(0);
                        //} else if ( bStartedArr == true && xpp.getName().equalsIgnoreCase("Reserve_Item")) {

                    } else if (xpp.getName().equalsIgnoreCase("response")) {
                        //
                    }
                }
                eventType = xpp.next();
            }
            sb.append("}");
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if ( strings.size() > 0 ) {
            try {
                for (int i = 0; i < strings.size(); i++) {
                    String str = strings.get(i);
                    mStbRecordReservelist.add(new JSONObject(str));
                }
            } catch ( JSONException e ) {
                e.printStackTrace();
            }
            mAdapter.setStbRecordReservelist(mStbRecordReservelist);
        }

        return sResultCode;
    }

    /* 채널 편성 정보 호출 */
    private void requestGetChannelSchedule() {
        mProgressDialog = ProgressDialog.show(mInstance, "", getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestGetChannelSchedule()"); }
        String url = mPref.getAircodeServerUrl() + "/getChannelSchedule.xml?version=1&channelId=" + sChannelId + "&dateIndex=7&areaCode=" + mPref.getValue(CMConstants.USER_REGION_CODE_KEY, "17");
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(tag, response);
                parseGetChannelList(response);

                Date date0 = new Date();
                Date date1 = new Date();
                Date date2 = new Date();
                Date date3 = new Date();
                Date date4 = new Date();
                Date date5 = new Date();
                Date date6 = new Date();

                Calendar c = Calendar.getInstance();
                c.setTime(date0);
                c.add(Calendar.DATE, 1);
                date1 = c.getTime();
                c.add(Calendar.DATE, 1);
                date2 = c.getTime();
                c.add(Calendar.DATE, 1);
                date3 = c.getTime();
                c.add(Calendar.DATE, 1);
                date4 = c.getTime();
                c.add(Calendar.DATE, 1);
                date5 = c.getTime();
                c.add(Calendar.DATE, 1);
                date6 = c.getTime();


                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String sDate0 = sdf.format(date0);
                String sDate1 = sdf.format(date1);
                String sDate2 = sdf.format(date2);
                String sDate3 = sdf.format(date3);
                String sDate4 = sdf.format(date4);
                String sDate5 = sdf.format(date5);
                String sDate6 = sdf.format(date6);

                for ( int i = 0; i < mDatasAll.size(); i++ ) {
                    ListViewDataObject obj = (ListViewDataObject)mDatasAll.get(i);
                    try {
                        JSONObject jo =  new JSONObject(obj.sJson);
                        String broadcastingDate = jo.getString("programBroadcastingStartTime");
                        broadcastingDate = broadcastingDate.substring(0, 10);
                        if ( sDate0.equals(broadcastingDate) ) {
                            ListViewDataObject ldo = new ListViewDataObject(mDatas0.size(), 0, obj.sJson);
                            mDatas0.add(ldo);
                        } else if ( sDate1.equals(broadcastingDate) ) {
                            ListViewDataObject ldo = new ListViewDataObject(mDatas0.size(), 0, obj.sJson);
                            mDatas1.add(ldo);
                        } else if ( sDate2.equals(broadcastingDate) ) {
                            ListViewDataObject ldo = new ListViewDataObject(mDatas0.size(), 0, obj.sJson);
                            mDatas2.add(ldo);
                        } else if ( sDate3.equals(broadcastingDate) ) {
                            ListViewDataObject ldo = new ListViewDataObject(mDatas0.size(), 0, obj.sJson);
                            mDatas3.add(ldo);
                        } else if ( sDate4.equals(broadcastingDate) ) {
                            ListViewDataObject ldo = new ListViewDataObject(mDatas0.size(), 0, obj.sJson);
                            mDatas4.add(ldo);
                        } else if ( sDate5.equals(broadcastingDate) ) {
                            ListViewDataObject ldo = new ListViewDataObject(mDatas0.size(), 0, obj.sJson);
                            mDatas5.add(ldo);
                        } else if ( sDate6.equals(broadcastingDate) ) {
                            ListViewDataObject ldo = new ListViewDataObject(mDatas0.size(), 0, obj.sJson);
                            mDatas6.add(ldo);
                        }
                    } catch ( JSONException e ) {
                        e.printStackTrace();
                    }
                }
                mAdapter.setDatas(mDatas0, 0);
                mAdapter.notifyDataSetChanged();
                mProgressDialog.dismiss();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                if ( mPref.isLogging() ) { VolleyLog.d(tag, "onErrorResponse(): " + error.getMessage()); }
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("version", String.valueOf(1));
                params.put("channelId", String.valueOf(0));
                params.put("dateIndex", String.valueOf(6));
                if ( mPref.isLogging() ) { Log.d(tag, "getParams()" + params.toString()); }
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    private void parseGetChannelList(String response) {
        StringBuilder        sb      = new StringBuilder();
        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new ByteArrayInputStream(response.getBytes("utf-8")), "utf-8");

            int eventType = xpp.getEventType();
            while ( eventType != XmlPullParser.END_DOCUMENT ) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equalsIgnoreCase("scheduleSeq")) {
                        sb.append("{\"scheduleSeq\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("broadcastingDate")) {
                        sb.append(",\"broadcastingDate\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("programId")) {
                        sb.append(",\"programId\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("seriesId")) {
                        sb.append(",\"seriesId\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("programTitle")) {
                        sb.append(",\"programTitle\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("programContent")) {
                        sb.append(",\"programContent\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("programBroadcastingStartTime")) {
                        sb.append(",\"programBroadcastingStartTime\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("programBroadcastingEndTime")) {
                        sb.append(",\"programBroadcastingEndTime\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("programHD")) {
                        sb.append(",\"programHD\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("programGrade")) {
                        sb.append(",\"programGrade\":\"").append(xpp.nextText()).append("\"}");
                    } else if (xpp.getName().equalsIgnoreCase("programPVR")) {
                        sb.append(",\"programPVR\":\"").append(xpp.nextText()).append("\"}");
                        ListViewDataObject obj = new ListViewDataObject(mAdapter.getCount(), 0, sb.toString());
                        mDatasAll.add(obj);
                        sb.setLength(0);
                    }
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  리스트뷰의 스와이프 메뉴로 호출되는 통신들 입니다. ******************************************************
     */

    /* TV로 시청 */
    private void requestSetRemoteChannelControl(String channelId) {
        mProgressDialog	 = ProgressDialog.show(mInstance, "", getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestSetRemoteChannelControl()"); }
        String uuid = mPref.getValue(JYSharedPreferences.UUID, "");
        String tk   = mPref.getWebhasTerminalKey();
        String url  = mPref.getRumpersServerUrl() + "/SetRemoteChannelControl.asp?deviceId=" + uuid + "&channelId=" + channelId + "&version=1&terminalKey=" + tk;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                parseSetRemoteChannelControl(response);
                if ( Constants.CODE_RUMPUS_OK.equals(RemoteChannelControl.get("resultCode")) ) {
                    // ok
                } else if ( "014".equals(RemoteChannelControl.get("resultCode")) ) {        // Hold Mode
                    String alertTitle = "채널 변경";
                    String alertMessage1 = "셋탑박스가 꺼져있습니다.";
                    String alertMessage2 = "";
                    CMAlertUtil.Alert(mInstance, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, true);
                } else if ( "021".equals(RemoteChannelControl.get("resultCode")) ) {        // VOD 시청중
                    String alertTitle = "채널 변경";
                    String alertMessage1 = "VOD 시청중엔 채널변경이 불가능합니다.";
                    String alertMessage2 = "";
                    CMAlertUtil.Alert(mInstance, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, true);
                } else if ( "008".equals(RemoteChannelControl.get("resultCode")) ) {        // 녹화물 재생중
                    String alertTitle = "채널 변경";
                    String alertMessage1 = "녹화물 재생중엔 채널변경이 불가능합니다.";
                    String alertMessage2 = "";
                    CMAlertUtil.Alert(mInstance, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();

                if (error instanceof TimeoutError ) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_noconnectionerror), Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_servererror), Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_networkerrorr), Toast.LENGTH_LONG).show();
                }
                if ( mPref.isLogging() ) { VolleyLog.d(tag, "onErrorResponse(): " + error.getMessage()); }
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("version", String.valueOf(1));
                params.put("areaCode", String.valueOf(0));
                if ( mPref.isLogging() ) { Log.d(tag, "getParams()" + params.toString()); }
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    private void parseSetRemoteChannelControl(String response) {
        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new ByteArrayInputStream(response.getBytes("utf-8")), "utf-8");

            int eventType = xpp.getEventType();
            while ( eventType != XmlPullParser.END_DOCUMENT ) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equalsIgnoreCase("resultCode")) {
                        String resultCode = xpp.nextText();
                        RemoteChannelControl.put("resultCode", resultCode);
                    } else if (xpp.getName().equalsIgnoreCase("errorString")) {
                        String errorString = xpp.nextText();
                        RemoteChannelControl.put("errorString", errorString);
                    }
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* 즉시녹화 */
    private void requestSetRecord(String channelId) {
        mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestSetRecord()"); }
        String terminalKey = JYSharedPreferences.RUMPERS_TERMINAL_KEY;
        String uuid = mPref.getValue(JYSharedPreferences.UUID, "");
        String url  = mPref.getRumpersServerUrl() + "/SetRecord.asp?Version=1&terminalKey=" + terminalKey + "&deviceId=" + uuid + "&channelId=" + channelId;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                parseSetRecord(response);
                if ( Constants.CODE_RUMPUS_OK.equals(RemoteChannelControl.get("resultCode")) ) {
                    // ok
                    // reloadAll(); // 기존 들고 있던 데이터 다 초기화 하고 다시 받아온다. 셋탑상태+예약녹화리스트
                } else if ( "002".equals(RemoteChannelControl.get("resultCode")) ) {        // Duplicated Recording Reserve Request
                    String alertTitle = "녹화 불가";
                    String alertMessage1 = "고객님의 셋탑박스는 해당시간에 다른 채널이 녹화예약되어있습니다. 녹화예약을 취소해주세요.";
                    String alertMessage2 = "";
                    CMAlertUtil.Alert(mInstance, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, true);
                } else if ( "003".equals(RemoteChannelControl.get("resultCode")) ) {
                    String alertTitle = "녹화 불가";
                    String alertMessage1 = "셋탑박스의 저장공간이 부족합니다. 녹화물 목록을 확인해주세요.";
                    String alertMessage2 = "";
                    CMAlertUtil.Alert(mInstance, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, true);
                } else if ( "005".equals(RemoteChannelControl.get("resultCode")) ) {
                    String alertTitle = "녹화 불가";
                    String alertMessage1 = "선택 하신 채널은 녹화하실 수 없습니다.";
                    String alertMessage2 = "";
                    CMAlertUtil.Alert(mInstance, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, true);
                } else if ( "009".equals(RemoteChannelControl.get("resultCode")) ) {
                    String alertTitle = "녹화 불가";
                    String alertMessage1 = "고객님의 셋탑박스에서 제공되지 않는 채널입니다.";
                    String alertMessage2 = "";
                    CMAlertUtil.Alert(mInstance, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, true);
                } else if ( "010".equals(RemoteChannelControl.get("resultCode")) ) {
                    String alertTitle = "녹화 불가";
                    String alertMessage1 = "셋탑박스에서 동시화면 기능을 사용중인 경우 즉시 녹화가 불가능합니다.";
                    String alertMessage2 = "";
                    CMAlertUtil.Alert(mInstance, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, true);
                } else if ( "011".equals(RemoteChannelControl.get("resultCode")) ) {
                    String alertTitle = "녹화 불가";
                    String alertMessage1 = "고객님의 셋탑박스는 현재 다른 채널을 녹화중입니다. 녹화를 중지해주세요.";
                    String alertMessage2 = "";
                    CMAlertUtil.Alert(mInstance, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, true);
                }else if ( "012".equals(RemoteChannelControl.get("resultCode")) ) {
                    String alertTitle = "녹화 불가";
                    String alertMessage1 = "고객님의 셋탑박스 설정에 의한 시청제한으로 녹화가 불가합니다. 셋탑박스 설정을 확인해주세요.";
                    String alertMessage2 = "";
                    CMAlertUtil.Alert(mInstance, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, true);
                } else if ( "014".equals(RemoteChannelControl.get("resultCode")) ) {
                    String alertTitle = "녹화 불가";
                    String alertMessage1 = "셋탑박스의 뒷 전원이 꺼져있거나, 통신이 고르지 못해 녹화가 불가합니다. 셋탑박스의 상태를 확인해주세요.";
                    String alertMessage2 = "";
                    CMAlertUtil.Alert(mInstance, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, true);
                } else if ( "023".equals(RemoteChannelControl.get("resultCode")) ) {
                    String alertTitle = "녹화 불가";
                    String alertMessage1 = "고객님의 셋탑박스에서 제공되지 않는 채널입니다.";
                    String alertMessage2 = "";
                    CMAlertUtil.Alert(mInstance, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, true);
                }
                reloadAll(); // 기존 들고 있던 데이터 다 초기화 하고 다시 받아온다. 셋탑상태+예약녹화리스트
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();

                if (error instanceof TimeoutError ) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_noconnectionerror), Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_servererror), Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_networkerrorr), Toast.LENGTH_LONG).show();
                }
                if ( mPref.isLogging() ) { VolleyLog.d(tag, "onErrorResponse(): " + error.getMessage()); }
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("version", String.valueOf(1));
                params.put("areaCode", String.valueOf(0));
                if ( mPref.isLogging() ) { Log.d(tag, "getParams()" + params.toString()); }
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    private void parseSetRecord(String response) {
        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new ByteArrayInputStream(response.getBytes("utf-8")), "utf-8");

            int eventType = xpp.getEventType();
            while ( eventType != XmlPullParser.END_DOCUMENT ) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equalsIgnoreCase("resultCode")) {
                        String resultCode = xpp.nextText();
                        RemoteChannelControl.put("resultCode", resultCode);
                    } else if (xpp.getName().equalsIgnoreCase("errorString")) {
                        String errorString = xpp.nextText();
                        RemoteChannelControl.put("errorString", errorString);
                    }
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* 녹화중지 */
    private void requestSetRecordStop(String channelId) {
        mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestSetRecordStop()"); }
        String terminalKey = JYSharedPreferences.RUMPERS_TERMINAL_KEY;
        String uuid = mPref.getValue(JYSharedPreferences.UUID, "");
        String url  = mPref.getRumpersServerUrl() + "/SetRecordStop.asp?Version=1&terminalKey=" + terminalKey + "&deviceId=" + uuid + "&channelId=" + channelId;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                parseSetRecordStop(response);
                reloadAll(); // 기존 들고 있던 데이터 다 초기화 하고 다시 받아온다. 셋탑상태+예약녹화리스트
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();

                if (error instanceof TimeoutError ) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_noconnectionerror), Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_servererror), Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_networkerrorr), Toast.LENGTH_LONG).show();
                }
                if ( mPref.isLogging() ) { VolleyLog.d(tag, "onErrorResponse(): " + error.getMessage()); }
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("version", String.valueOf(1));
                params.put("areaCode", String.valueOf(0));
                if ( mPref.isLogging() ) { Log.d(tag, "getParams()" + params.toString()); }
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    private void parseSetRecordStop(String response) {
        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new ByteArrayInputStream(response.getBytes("utf-8")), "utf-8");

            int eventType = xpp.getEventType();
            while ( eventType != XmlPullParser.END_DOCUMENT ) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equalsIgnoreCase("resultCode")) {
                        String resultCode = xpp.nextText();
                        RemoteChannelControl.put("resultCode", resultCode);
                    } else if (xpp.getName().equalsIgnoreCase("errorString")) {
                        String errorString = xpp.nextText();
                        RemoteChannelControl.put("errorString", errorString);
                    }
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* 예약녹화 */
    private void requestSetRecordReserve(String channelId, String starttime) {
        mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestSetRecordReserve()"); }
        try {
            starttime = URLEncoder.encode(starttime, "utf-8");
        } catch ( UnsupportedEncodingException e ) {
            e.printStackTrace();
        }
        String terminalKey = JYSharedPreferences.RUMPERS_TERMINAL_KEY;
        String uuid = mPref.getValue(JYSharedPreferences.UUID, "");
        String url  = mPref.getRumpersServerUrl() + "/SetRecordReserve.asp?Version=1&terminalKey=" + terminalKey + "&deviceId=" + uuid + "&channelId="
                + channelId + "&StartTime=" + starttime;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                parseSetRecordReserve(response);
                if ( Constants.CODE_RUMPUS_OK.equals(RemoteChannelControl.get("resultCode")) ) {
                    // ok
                    // reloadAll(); // 기존 들고 있던 데이터 다 초기화 하고 다시 받아온다. 셋탑상태+예약녹화리스트
                } else if ( "002".equals(RemoteChannelControl.get("resultCode")) ) {        // Duplicated Recording Reserve Request
                    String alertTitle = "녹화 불가";
                    String alertMessage1 = "Duplicated Recording Reserve Request";
                    String alertMessage2 = "";
                    CMAlertUtil.Alert(mInstance, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, true);
                } else if ( "003".equals(RemoteChannelControl.get("resultCode")) ) {
                    String alertTitle = "녹화예약 불가";
                    String alertMessage1 = "셋탑박스의 저장공간이 부족합니다. 녹화물 목록을 확인해주세요.";
                    String alertMessage2 = "";
                    CMAlertUtil.Alert(mInstance, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, true);
                } else if ( "005".equals(RemoteChannelControl.get("resultCode")) ) {
                    String alertTitle = "녹화 불가";
                    String alertMessage1 = "선택 하신 채널은 녹화하실 수 없습니다.";
                    String alertMessage2 = "";
                    CMAlertUtil.Alert(mInstance, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, true);
                } else if ( "014".equals(RemoteChannelControl.get("resultCode")) ) {
                    String alertTitle = "녹화예약 불가";
                    String alertMessage1 = "셋탑박스의 뒷 전원이 꺼져있거나, 통신이 고르지 못해 녹화가 불가합니다. 셋탑박스의 상태를 확인해주세요.";
                    String alertMessage2 = "";
                    CMAlertUtil.Alert(mInstance, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, true);
                } else if ( "010".equals(RemoteChannelControl.get("resultCode")) ) {
                    String alertTitle = "녹화예약 불가";
                    String alertMessage1 = "셋탑박스에서 동시화면 기능을 사용중인 경우 즉시 녹화가 불가능합니다.";
                    String alertMessage2 = "";
                    CMAlertUtil.Alert(mInstance, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, true);
                } else if ( "023".equals(RemoteChannelControl.get("resultCode")) ) {
                    String alertTitle = "녹화 불가";
                    String alertMessage1 = "고객님의 셋탑박스에서 제공되지 않는 채널입니다.";
                    String alertMessage2 = "";
                    CMAlertUtil.Alert(mInstance, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, true);
                }
                reloadAll(); // 기존 들고 있던 데이터 다 초기화 하고 다시 받아온다. 셋탑상태+예약녹화리스트
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();

                if (error instanceof TimeoutError ) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_noconnectionerror), Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_servererror), Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_networkerrorr), Toast.LENGTH_LONG).show();
                }
                if ( mPref.isLogging() ) { VolleyLog.d(tag, "onErrorResponse(): " + error.getMessage()); }
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("version", String.valueOf(1));
                params.put("areaCode", String.valueOf(0));
                if ( mPref.isLogging() ) { Log.d(tag, "getParams()" + params.toString()); }
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    private void requestSetRecordSeriesReserve(String channelId, String series, String starttime) {
        mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestSetRecordReserve()"); }
        try {
            starttime = URLEncoder.encode(starttime, "utf-8");
        } catch ( UnsupportedEncodingException e ) {
            e.printStackTrace();
        }
        String terminalKey = JYSharedPreferences.RUMPERS_TERMINAL_KEY;
        String uuid = mPref.getValue(JYSharedPreferences.UUID, "");
        String url  = mPref.getRumpersServerUrl() + "/SetRecordSeriesReserve.asp?Version=1&terminalKey=" + terminalKey + "&deviceId=" + uuid + "&channelId="
                + channelId + "&StartTime=" + starttime + "&SeriesId=" + series;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                parseSetRecordReserve(response);
                if ( Constants.CODE_RUMPUS_OK.equals(RemoteChannelControl.get("resultCode")) ) {
                    // ok
                    // reloadAll(); // 기존 들고 있던 데이터 다 초기화 하고 다시 받아온다. 셋탑상태+예약녹화리스트
                } else if ( "002".equals(RemoteChannelControl.get("resultCode")) ) {        // Duplicated Recording Reserve Request
                    String alertTitle = "녹화 불가";
                    String alertMessage1 = "Duplicated Recording Reserve Request";
                    String alertMessage2 = "";
                    CMAlertUtil.Alert(mInstance, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, true);
                } else if ( "003".equals(RemoteChannelControl.get("resultCode")) ) {
                    String alertTitle = "녹화예약 불가";
                    String alertMessage1 = "셋탑박스의 저장공간이 부족합니다. 녹화물 목록을 확인해주세요.";
                    String alertMessage2 = "";
                    CMAlertUtil.Alert(mInstance, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, true);
                } else if ( "014".equals(RemoteChannelControl.get("resultCode")) ) {
                    String alertTitle = "녹화예약 불가";
                    String alertMessage1 = "셋탑박스의 뒷 전원이 꺼져있거나, 통신이 고르지 못해 녹화가 불가합니다. 셋탑박스의 상태를 확인해주세요.";
                    String alertMessage2 = "";
                    CMAlertUtil.Alert(mInstance, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, true);
                } else if ( "010".equals(RemoteChannelControl.get("resultCode")) ) {
                    String alertTitle = "녹화예약 불가";
                    String alertMessage1 = "셋탑박스에서 동시화면 기능을 사용중인 경우 즉시 녹화가 불가능합니다.";
                    String alertMessage2 = "";
                    CMAlertUtil.Alert(mInstance, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, true);
                }
                reloadAll(); // 기존 들고 있던 데이터 다 초기화 하고 다시 받아온다. 셋탑상태+예약녹화리스트
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();

                if (error instanceof TimeoutError ) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_noconnectionerror), Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_servererror), Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_networkerrorr), Toast.LENGTH_LONG).show();
                }
                if ( mPref.isLogging() ) { VolleyLog.d(tag, "onErrorResponse(): " + error.getMessage()); }
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("version", String.valueOf(1));
                params.put("areaCode", String.valueOf(0));
                if ( mPref.isLogging() ) { Log.d(tag, "getParams()" + params.toString()); }
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    private void parseSetRecordReserve(String response) {
        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new ByteArrayInputStream(response.getBytes("utf-8")), "utf-8");

            int eventType = xpp.getEventType();
            while ( eventType != XmlPullParser.END_DOCUMENT ) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equalsIgnoreCase("resultCode")) {
                        String resultCode = xpp.nextText();
                        RemoteChannelControl.put("resultCode", resultCode);
                    } else if (xpp.getName().equalsIgnoreCase("errorString")) {
                        String errorString = xpp.nextText();
                        RemoteChannelControl.put("errorString", errorString);
                    }
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* 예약 녹화 취소 */
    private void requestSetRecordCancelReserve(String channelId, String starttime, String seriesId) {
        mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestSetRecordCancelReserve()"); }
        try {
            starttime = URLEncoder.encode(starttime, "utf-8");
        } catch ( UnsupportedEncodingException e ) {
            e.printStackTrace();
        }
        String terminalKey = JYSharedPreferences.RUMPERS_TERMINAL_KEY;
        String uuid = mPref.getValue(JYSharedPreferences.UUID, "");
        String url  = mPref.getRumpersServerUrl() + "/SetRecordCancelReserve.asp?Version=1&terminalKey=" + terminalKey + "&deviceId=" + uuid + "&channelId="
                + channelId + "&StartTime=" + starttime + "&seriesId=" + seriesId + "&ReserveCancel=2";
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                parseSetRecordCancelReserve(response);
                if ( Constants.CODE_RUMPUS_OK.equals(RemoteChannelControl.get("resultCode")) ) {
                    // ok
                    // reloadAll(); // 기존 들고 있던 데이터 다 초기화 하고 다시 받아온다. 셋탑상태+예약녹화리스트
                } else if ( "014".equals(RemoteChannelControl.get("resultCode")) ) {        // Hold Mode
                    String alertTitle = "녹화예약취소 불가";
                    String alertMessage1 = "셋탑박스가 꺼져있습니다.";
                    String alertMessage2 = "";
                    CMAlertUtil.Alert(mInstance, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, true);
                } else if ( "021".equals(RemoteChannelControl.get("resultCode")) ) {        // VOD 시청중
                    String alertTitle = "녹화예약취소 불가";
                    String alertMessage1 = "VOD 시청중엔 채널변경이 불가능합니다.";
                    String alertMessage2 = "";
                    CMAlertUtil.Alert(mInstance, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, true);
                } else if ( "008".equals(RemoteChannelControl.get("resultCode")) ) {        // 녹화물 재생중
                    String alertTitle = "녹화예약취소 불가";
                    String alertMessage1 = "녹화물 재생중엔 채널변경이 불가능합니다.";
                    String alertMessage2 = "";
                    CMAlertUtil.Alert(mInstance, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, true);
                }
                reloadAll(); // 기존 들고 있던 데이터 다 초기화 하고 다시 받아온다. 셋탑상태+예약녹화리스트
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();

                if (error instanceof TimeoutError ) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_noconnectionerror), Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_servererror), Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_networkerrorr), Toast.LENGTH_LONG).show();
                }
                if ( mPref.isLogging() ) { VolleyLog.d(tag, "onErrorResponse(): " + error.getMessage()); }
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("version", String.valueOf(1));
                params.put("areaCode", String.valueOf(0));
                if ( mPref.isLogging() ) { Log.d(tag, "getParams()" + params.toString()); }
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    private void parseSetRecordCancelReserve(String response) {
        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new ByteArrayInputStream(response.getBytes("utf-8")), "utf-8");

            int eventType = xpp.getEventType();
            while ( eventType != XmlPullParser.END_DOCUMENT ) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equalsIgnoreCase("resultCode")) {
                        String resultCode = xpp.nextText();
                        RemoteChannelControl.put("resultCode", resultCode);
                    } else if (xpp.getName().equalsIgnoreCase("errorString")) {
                        String errorString = xpp.nextText();
                        RemoteChannelControl.put("errorString", errorString);
                    }
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

