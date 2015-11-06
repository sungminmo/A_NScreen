package com.stvn.nscreen.epg;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
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

    private ArrayList<ListViewDataObject>     mDatasAll = new ArrayList<ListViewDataObject>();
    private ArrayList<ListViewDataObject>     mDatas0 = new ArrayList<ListViewDataObject>();
    private ArrayList<ListViewDataObject>     mDatas1 = new ArrayList<ListViewDataObject>();
    private ArrayList<ListViewDataObject>     mDatas2 = new ArrayList<ListViewDataObject>();
    private ArrayList<ListViewDataObject>     mDatas3 = new ArrayList<ListViewDataObject>();
    private ArrayList<ListViewDataObject>     mDatas4 = new ArrayList<ListViewDataObject>();
    private ArrayList<ListViewDataObject>     mDatas5 = new ArrayList<ListViewDataObject>();
    private ArrayList<ListViewDataObject>     mDatas6 = new ArrayList<ListViewDataObject>();

    // gui
    private              EpgSubListViewAdapter mAdapter;
    private              ListView              mListView;

    private              String                sChannelNumber, sChannelName, sChannelLogoImg;

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

        mListView = (ListView)findViewById(R.id.epg_sub_listview);
        mListView.setAdapter(mAdapter);

        requestGetChannelList();
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
