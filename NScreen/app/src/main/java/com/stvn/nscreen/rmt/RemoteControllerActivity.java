package com.stvn.nscreen.rmt;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.common.ListViewDataObject;
import com.jjiya.android.http.JYStringRequest;
import com.stvn.nscreen.R;
import com.stvn.nscreen.epg.EpgChoiceActivity;
import com.stvn.nscreen.epg.EpgSubListViewAdapter;

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
 * Created by limdavid on 15. 10. 26..
 */

public class RemoteControllerActivity extends AppCompatActivity{

    private static final String                          tag = RemoteControllerActivity.class.getSimpleName();
    private static       RemoteControllerActivity        mInstance;
    private              JYSharedPreferences             mPref;

    // network
    private              RequestQueue                    mRequestQueue;
    private              ProgressDialog                  mProgressDialog;

    // gui
    private              RemoteControllerListViewAdapter mAdapter;
    private              ListView                        mListView;

    private              ImageButton                     remote_controller_genre_choice_imageButton, remote_controller_backBtn;

    private              TextView                        remote_controller_genre_name, remote_controller_channel_textview;
    private              String                          sChannel, sPower, sVolume;
    private              Button                          remote_controller_power_button, remote_controller_volume_up_button, remote_controller_volume_down_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_controller);

        mInstance = this;
        mPref     = new JYSharedPreferences(this);
        mRequestQueue = Volley.newRequestQueue(this);

        if (mPref.isLogging()) { Log.d(tag, "onCreate()"); }

        mAdapter = new RemoteControllerListViewAdapter(this, null);

        mListView = (ListView)findViewById(R.id.remote_controller_listview);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mItemClickListener);

        remote_controller_genre_choice_imageButton = (ImageButton) findViewById(R.id.remote_controller_genre_choice_imageButton);
        remote_controller_backBtn                  = (ImageButton) findViewById(R.id.remote_controller_backBtn);

        remote_controller_genre_name               = (TextView) findViewById(R.id.remote_controller_genre_name);
        remote_controller_channel_textview         = (TextView) findViewById(R.id.remote_controller_channel_textview);

        remote_controller_power_button             = (Button) findViewById(R.id.remote_controller_power_button);
        remote_controller_volume_up_button         = (Button) findViewById(R.id.remote_controller_volume_up_button);
        remote_controller_volume_down_button       = (Button) findViewById(R.id.remote_controller_volume_down_button);

        try {
            sChannel = getIntent().getExtras().getString("Channel");
            remote_controller_channel_textview.setText(sChannel + "번");
            remote_controller_genre_name.setText(getIntent().getExtras().getString("sGenreName"));
        } catch (NullPointerException e) {
            sChannel = "";
            remote_controller_channel_textview.setText("번");
            remote_controller_genre_name.setText("전체채널");
        }

        remote_controller_genre_choice_imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RemoteControllerActivity.this, RemoteControllerChoiceActivity.class);
                i.putExtra("Channel", sChannel);
                startActivity(i);
            }
        });

        remote_controller_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        requestGetChannelList();

        remote_controller_power_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSetRemotePowerControl(sPower);
            }
        });

        remote_controller_volume_down_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sVolume = "DN";
                requestSetRemoteVolumeControl(sVolume);
            }
        });

        remote_controller_volume_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sVolume = "UP";
                requestSetRemoteVolumeControl(sVolume);
            }
        });
    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(tag, "mItemClickListener() " + position);
            ListViewDataObject dobj = (ListViewDataObject) mAdapter.getItem(position);
            try {
                JSONObject jo = new JSONObject(dobj.sJson);
                String sChannelNumber = jo.getString("channelNumber");
                requestSetRemoteChannelControl(sChannelNumber);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void requestSetRemoteChannelControl(String sChannelNumber) {
        mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestSetRemoteChannelControl()"); }
        String uuid = mPref.getValue(JYSharedPreferences.UUID, "");
        String tk   = mPref.getWebhasTerminalKey();
        String url  = mPref.getRumpersServerUrl() + "/SetRemoteChannelControl.asp?deviceId=" + uuid + "&channelId=" + sChannelNumber + "&version=1&terminalKey=" + tk;
        sChannel = sChannelNumber;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(tag, response);
                mProgressDialog.dismiss();

                remote_controller_channel_textview.setText(sChannel + "번");

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

    private void requestGetChannelList() {
        mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestGetChannelList()"); }
        String sGenreCode = "";
        try {
            sGenreCode = getIntent().getExtras().getString("sGenreCode");
        } catch (NullPointerException e) {
            sGenreCode = "";
        }
        if ( "".equals(sGenreCode) ) {
            sGenreCode = "";
        }
        String url = mPref.getAircodeServerUrl() + "/getChannelList.xml?version=1&areaCode=0" + sGenreCode;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(tag, response);
                mProgressDialog.dismiss();
                parseGetChannelList(response);
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
                    if (xpp.getName().equalsIgnoreCase("channelId")) {
                        sb.append("{\"channelId\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("channelNumber")) {
                        sb.append(",\"channelNumber\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("channelName")) {
                        sb.append(",\"channelName\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("channelProgramOnAirTitle")) {
                        sb.append(",\"channelProgramOnAirTitle\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("channelInfo")) {
                        sb.append(",\"channelInfo\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("channelOnAirHD")) {
                        sb.append(",\"channelOnAirHD\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("channelLogoImg")) {
                        sb.append(",\"channelLogoImg\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("channelProgramOnAirID")) {
                        sb.append(",\"channelProgramOnAirID\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("channelProgramOnAirStartTime")) {
                        sb.append(",\"channelProgramOnAirStartTime\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("channelProgramOnAirEndTime")) {
                        sb.append(",\"channelProgramOnAirEndTime\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("channelProgramGrade")) {
                        sb.append(",\"channelProgramGrade\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("channelView")) {
                        sb.append(",\"channelView\":\"").append(xpp.nextText()).append("\"}");
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

    private void requestSetRemotePowerControl(String power) {
        mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestSetRemotePowerControl()"); }
        String uuid  = mPref.getValue(JYSharedPreferences.UUID, "");
        String tk    = mPref.getWebhasTerminalKey();
        String url   = mPref.getRumpersServerUrl() + "/SetRemotePowerControl.asp?Version=1&deviceId=" + uuid + "&terminalKey=" + tk + "&power=" + power;

        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(tag, response);
                mProgressDialog.dismiss();
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

    private void requestSetRemoteVolumeControl(String volume) {

        mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestSetRemoteVolumeControl()"); }
        String uuid = mPref.getValue(JYSharedPreferences.UUID, "");
        String tk   = mPref.getWebhasTerminalKey();
        String url  = mPref.getRumpersServerUrl() + "/SetRemoteVolumeControl.asp?deviceId=" + uuid + "&version=1&terminalKey=" + tk + "&volume=" + volume;

        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(tag, response);
                mProgressDialog.dismiss();
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
}
