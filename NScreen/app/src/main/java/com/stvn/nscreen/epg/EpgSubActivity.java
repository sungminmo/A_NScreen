package com.stvn.nscreen.epg;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;
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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

    // gui
    private              EpgSubListViewAdapter mAdapter;
    private              ListView              mListView;

    private              String                sChannelNumber, sChannelName, sChannelLogoImg;

    private              NetworkImageView      epg_sub_channelLogoImg;

    private              TextView              epg_sub_channelNumber, epg_sub_channelName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epg_sub);

        mInstance = this;
        mPref     = new JYSharedPreferences(this);
        mRequestQueue = Volley.newRequestQueue(this);
        this.mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(100);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });

        sChannelNumber = getIntent().getExtras().getString("channelNumber");
        sChannelName = getIntent().getExtras().getString("channelName");
        sChannelLogoImg = getIntent().getExtras().getString("channelLogoImg");

        if (mPref.isLogging()) { Log.d(tag, "onCreate()"); }

        epg_sub_channelNumber = (TextView) findViewById(R.id.epg_sub_channelNumber);
        epg_sub_channelName   = (TextView) findViewById(R.id.epg_sub_channelName);
        epg_sub_channelLogoImg = (NetworkImageView) findViewById(R.id.epg_sub_imageview_channel_logo);

        epg_sub_channelNumber.setText("CH." + sChannelNumber);
        epg_sub_channelName.setText(sChannelName);

        epg_sub_channelLogoImg.setImageUrl(sChannelLogoImg, mImageLoader);

        mAdapter = new EpgSubListViewAdapter(this, null);
          // for test
//        for (int i = 0; i < 1000; i++) {
//            String sChannel        = String.format("%02d", i);
//            ListViewDataObject obj = new ListViewDataObject(0, 0, "{\"channelNumber\":\"" + sChannel + "\",\"title\":\"전국 노래자랑 광진구편 초대가수 임석원 사회 송해\"}");
//            mAdapter.addItem(obj);
//        }

        mListView = (ListView)findViewById(R.id.epg_sub_listview);
        mListView.setAdapter(mAdapter);

        requestGetChannelList();
    }

    private void requestGetChannelList() {
        mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestGetChannelSchedule()"); }
        String url = mPref.getAircodeServerUrl() + "/getChannelSchedule.xml?version=1&channelId=" + sChannelNumber + "&dateIndex=6";
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(tag, response);
                parseGetChannelList(response);
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
        StringBuilder sb = new StringBuilder();
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
                        mAdapter.addItem(obj);
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
