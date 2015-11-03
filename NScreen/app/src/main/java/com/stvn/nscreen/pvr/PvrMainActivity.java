package com.stvn.nscreen.pvr;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by limdavid on 15. 9. 14..
 */

public class PvrMainActivity extends AppCompatActivity {
    private static final String                 tag = PvrMainActivity.class.getSimpleName();
    private static       PvrMainActivity        mInstance;
    private              JYSharedPreferences    mPref;

    // network
    private              RequestQueue           mRequestQueue;
    private              ProgressDialog         mProgressDialog;

    private              PvrMainListViewAdapter mAdapter;
    private              ListView               mListView;

    private              ImageButton            pvr_main_backBtn;
    private              Button                 button1, button2;
    private              TextView               textView2, textView7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pvr_main);

        pvr_main_backBtn = (ImageButton) findViewById(R.id.pvr_main_backBtn);

        pvr_main_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mInstance = this;
        mPref     = new JYSharedPreferences(this);
        mRequestQueue = Volley.newRequestQueue(this);
        if (mPref.isLogging()) { Log.d(tag, "onCreate()"); }

        mAdapter  = new PvrMainListViewAdapter(this, null);

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);

        textView2 = (TextView) findViewById(R.id.textView2);
        textView7 = (TextView) findViewById(R.id.textView7);

        mListView = (ListView)findViewById(R.id.pvr_main_listview);
        mListView.setAdapter(mAdapter);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button1.setSelected(true);
                button2.setSelected(false);
                textView2.setVisibility(View.GONE);
                textView7.setVisibility(View.VISIBLE);
                requestGetRecordReservelist();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button1.setSelected(false);
                button2.setSelected(true);
                textView2.setVisibility(View.VISIBLE);
                textView7.setVisibility(View.GONE);
                requestGetrecordlist();
            }
        });
    }

    private void requestGetRecordReservelist() {
        mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestGetRecordReservelist()"); }
        String uuid = mPref.getValue(JYSharedPreferences.UUID, "");
        String tk   = mPref.getWebhasTerminalKey();
        String url  = mPref.getRumpersServerUrl() + "/getrecordReservelist.asp?Version=1&terminalKey=" + tk + "&deviceId=" + uuid;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(tag, response);
                mProgressDialog.dismiss();
                parseGetRecordReservelist(response);
                mAdapter.notifyDataSetChanged();
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
                params.put("areaCode", String.valueOf(0));
                if ( mPref.isLogging() ) { Log.d(tag, "getParams()" + params.toString()); }
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    private void parseGetRecordReservelist(String response) {
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
                    if (xpp.getName().equalsIgnoreCase("RecordingType")) {
                        sb.append("{\"RecordingType\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("Channel_logo_img")) {
                        sb.append(",\"Channel_logo_img\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("ProgramName")) {
                        sb.append(",\"ProgramName\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("RecordStatus")) {
                        sb.append(",\"RecordStatus\":\"").append(xpp.nextText()).append("\"}");
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

    private void requestGetrecordlist() {
        mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestGetrecordlist()"); }
        String uuid = mPref.getValue(JYSharedPreferences.UUID, "");
        String tk   = mPref.getWebhasTerminalKey();
        String url  = mPref.getRumpersServerUrl() + "/getrecordlist.asp?Version=1&terminalKey=" + tk + "&deviceId=" + uuid;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(tag, response);
                mProgressDialog.dismiss();
                parseGetrecordlist(response);
                mAdapter.notifyDataSetChanged();
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
                params.put("areaCode", String.valueOf(0));
                if ( mPref.isLogging() ) { Log.d(tag, "getParams()" + params.toString()); }
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    private void parseGetrecordlist(String response) {
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
                    if (xpp.getName().equalsIgnoreCase("RecordId")) {
                        sb.append("{\"RecordId\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("RecordingType")) {
                        sb.append(",\"RecordingType\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("Channel_logo_img")) {
                        sb.append(",\"Channel_logo_img\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("ProgramName")) {
                        sb.append(",\"ProgramName\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("RecordHD")) {
                        sb.append(",\"RecordHD\":\"").append(xpp.nextText()).append("\"}");
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
