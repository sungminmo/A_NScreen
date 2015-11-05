package com.stvn.nscreen.pvr;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.Constants;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.common.ListViewDataObject;
import com.jjiya.android.http.JYStringRequest;
import com.stvn.nscreen.R;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private              Map<String, Object>    mNetworkError;

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

        mInstance     = this;
        mPref         = new JYSharedPreferences(this);
        mRequestQueue = Volley.newRequestQueue(this);
        mNetworkError = new HashMap<String, Object>();
        if (mPref.isLogging()) { Log.d(tag, "onCreate()"); }

        mAdapter      = new PvrMainListViewAdapter(this, null);

        button1       = (Button) findViewById(R.id.button1);
        button2       = (Button) findViewById(R.id.button2);

        textView2     = (TextView) findViewById(R.id.textView2);
        textView7     = (TextView) findViewById(R.id.textView7);

        mListView     = (ListView)findViewById(R.id.pvr_main_listview);
        mListView.setAdapter(mAdapter);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.clear();
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
                mAdapter.clear();
                button1.setSelected(false);
                button2.setSelected(true);
                textView2.setVisibility(View.VISIBLE);
                textView7.setVisibility(View.GONE);
                requestGetrecordlist();
            }
        });

        button1.setSelected(true);
        requestGetRecordReservelist();
    }

    private void requestGetRecordReservelist() {
        mProgressDialog	        = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestGetRecordReservelist()"); }
        String          uuid    = mPref.getValue(JYSharedPreferences.UUID, "");
        String          tk      = mPref.getWebhasTerminalKey();
        // for test
        //String          url     = mPref.getRumpersServerUrl() + "/getRecordReservelist.asp?Version=1&terminalKey=" + tk + "&deviceId=" + uuid;
        String          url     = "http://192.168.44.10/SMApplicationserver/getrecordReservelist.asp?Version=1&terminalKey=C5E6DBF75F13A2C1D5B2EFDB2BC940&deviceId=68590725-3b42-4cea-ab80-84c91c01bad2";
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
                    mAdapter.notifyDataSetChanged();
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
        List<String>         strings     = new ArrayList<String>();

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
            for ( int i = 0; i < strings.size(); i++ ) {
                String str = strings.get(i);
                ListViewDataObject obj = new ListViewDataObject(mAdapter.getCount(), 0, str);
                mAdapter.addItem(obj);
            }
        }

        return sResultCode;
    }

    private void requestGetrecordlist() {
        mProgressDialog	        = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestGetrecordlist()"); }
        String          uuid    = mPref.getValue(JYSharedPreferences.UUID, "");
        String          tk      = mPref.getWebhasTerminalKey();
        String          url     = mPref.getRumpersServerUrl() + "/getrecordlist.asp?Version=1&terminalKey=" + tk + "&deviceId=" + uuid;
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
