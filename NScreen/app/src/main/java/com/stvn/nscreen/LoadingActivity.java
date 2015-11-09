package com.stvn.nscreen;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.Constants;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.http.JYStringRequest;
import com.widevine.sampleplayer.VideoPlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoadingActivity extends AppCompatActivity {

    private static final String              tag = LoadingActivity.class.getSimpleName();
    public  static       LoadingActivity     mInstance;
    private              JYSharedPreferences mPref;

    // network
    private              RequestQueue        mRequestQueue;

    // ui
    private              ProgressBar         mProgressBar;
    private              TextView            mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);


        mInstance     = this;
        mPref         = new JYSharedPreferences(this);
        mRequestQueue = Volley.newRequestQueue(this);


        mProgressBar  = (ProgressBar)findViewById(R.id.loading_progressbar);
        mTextView     = (TextView)findViewById(R.id.loading_textview);


        //mProgressBar.getProgressDrawable().setColorFilter(Color.MAGENTA, PorterDuff.Mode.SRC_IN);
        //mProgressBar.getProgressDrawable().setColorFilter(0xFFFF0000, PorterDuff.Mode.SRC_IN);
        mProgressBar.getProgressDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);

        mProgressBar.setProgress(10);
        mTextView.setText("초기화 중입니다.");
        if ( mPref.isPairingCompleted() ) {
            mProgressBar.setProgress(20);
            requestGetWishList();
        } else {
            mTextView.setText("초기화를 완료 했습니다.");
            mProgressBar.setProgress(100);
            Intent intent = new Intent(mInstance, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * 찜하기 리스트 받기
     * http://58.141.255.79:8080/HApplicationServer/getWishList.json?version=1&terminalKey=B2F311C9641A0CCED9C7FE95BE624D9&transactionId=1
     */
    private void requestGetWishList() {
        mProgressBar.setProgress(30);
        if ( mPref.isLogging() ) { Log.d(tag, "requestGetWishList()"); }
        String terminalKey = mPref.getWebhasTerminalKey();
        String url = mPref.getWebhasServerUrl() + "/getWishList.json?version=1&terminalKey="+terminalKey;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressBar.setProgress(50);
                try {
                    JSONObject jo      = new JSONObject(response);
                    String resultCode  = jo.getString("resultCode");

                    if ( Constants.CODE_WEBHAS_OK.equals(resultCode) ) {
                        JSONArray arr  = jo.getJSONArray("wishItemList");
                        mPref.setAllWishList(arr);
                    } else {
                        String errorString = jo.getString("errorString");
                        StringBuilder sb   = new StringBuilder();
                        sb.append("API: action\nresultCode: ").append(resultCode).append("\nerrorString: ").append(errorString);
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

                    mTextView.setText("초기화를 완료 했습니다.");
                    mProgressBar.setProgress(100);
                    Intent intent = new Intent(mInstance, MainActivity.class);
                    startActivity(intent);
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
        mProgressBar.setProgress(40);
    }
}
