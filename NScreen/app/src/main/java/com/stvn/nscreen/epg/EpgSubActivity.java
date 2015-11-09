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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.jjiya.android.common.Constants;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.common.ListViewDataObject;
import com.jjiya.android.http.JYStringRequest;
import com.stvn.nscreen.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

    private              ArrayList<JSONObject>           mStbRecordReservelist;

    // gui
    private              EpgSubListViewAdapter mAdapter;
    private              SwipeMenuListView     mListView;

    private              String                sChannelNumber, sChannelId, sChannelName, sChannelLogoImg;

    private              NetworkImageView      epg_sub_channelLogoImg;

    private              TextView              epg_sub_channelNumber, epg_sub_channelName, epg_sub_date1, epg_sub_date2, epg_sub_date3, epg_sub_date4, epg_sub_date5, epg_sub_date6, epg_sub_date7;

    private              Calendar              cal;

    private              LinearLayout          epg_sub_date_linearlayout1, epg_sub_date_linearlayout2, epg_sub_date_linearlayout3, epg_sub_date_linearlayout4, epg_sub_date_linearlayout5, epg_sub_date_linearlayout6, epg_sub_date_linearlayout7;
    private              ImageView             imageView21, imageView22, imageView23, imageView24, imageView25, imageView26, imageView27;

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
        epg_sub_channelLogoImg     = (NetworkImageView) findViewById(R.id.epg_sub_imageview_channel_logo);

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

        requestGetChannelList();

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
                    createMenu1(menu);
                } break;
                case 1: {
                    createMenu2(menu);
                } break;
                case 2: {
                    createMenu3(menu);
                } break;
            }
        }

        private int dp2px(int dp) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
        }

        private void createMenu1(SwipeMenu menu) {
            SwipeMenuItem item1 = new SwipeMenuItem(getApplicationContext());
            item1.setBackground(new ColorDrawable(Color.rgb(0xE5, 0x18, 0x5E)));
            item1.setWidth(dp2px(90));
            //item1.setIcon(R.drawable.ic_action_favorite);
            menu.addMenuItem(item1);
            SwipeMenuItem item2 = new SwipeMenuItem(getApplicationContext());
            item2.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
            item2.setWidth(dp2px(90));
            //item2.setIcon(R.drawable.ic_action_good);
            menu.addMenuItem(item2);
        }

        private void createMenu2(SwipeMenu menu) {
            SwipeMenuItem item1 = new SwipeMenuItem(getApplicationContext());
            item1.setBackground(new ColorDrawable(Color.rgb(0xE5, 0xE0, 0x3F)));
            item1.setWidth(dp2px(90));
            //item1.setIcon(R.drawable.ic_action_important);
            menu.addMenuItem(item1);
            SwipeMenuItem item2 = new SwipeMenuItem(getApplicationContext());
            item2.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
            item2.setWidth(dp2px(90));
            //item2.setIcon(R.drawable.ic_action_discard);
            menu.addMenuItem(item2);
        }

        private void createMenu3(SwipeMenu menu) {
            SwipeMenuItem item1 = new SwipeMenuItem(getApplicationContext());
            item1.setBackground(new ColorDrawable(Color.rgb(0x30, 0xB1, 0xF5)));
            item1.setWidth(dp2px(90));
            //item1.setIcon(R.drawable.ic_action_about);
            menu.addMenuItem(item1);
            SwipeMenuItem item2 = new SwipeMenuItem(getApplicationContext());
            item2.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
            item2.setWidth(dp2px(90));
            //item2.setIcon(R.drawable.ic_action_share);
            menu.addMenuItem(item2);
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

                requestGetRecordReservelist();
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
                if ( ! Constants.CODE_RUMPUS_OK.equals(sResultCode) ) {
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
                } else {
                    // ok case
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
        response = response.replace("]]>","");
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




    private void requestGetChannelList() {
        mProgressDialog = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestGetChannelSchedule()"); }
        String url = mPref.getAircodeServerUrl() + "/getChannelSchedule.xml?version=1&channelId=" + sChannelNumber + "&dateIndex=7&areaCode=0";
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


                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
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
                        String broadcastingDate = jo.getString("broadcastingDate");
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

                // 7.3.40 GetSetTopStatus
                // 셋탑의 상태 확인용.
                requestGetSetTopStatus();
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
}
