package com.jjiya.android.http;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.jjiya.android.ApplicationClass;
import com.jjiya.android.common.JYSharedPreferences;
import com.stvn.nscreen.CMExitActivity;
import com.stvn.nscreen.util.CMAlertUtil;
import com.stvn.nscreen.util.CMUtil;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by swlim
 */
public class JYStringRequest extends StringRequest {

    private static final String tag = JYStringRequest.class.getSimpleName();
    private static boolean isCloseAlertShow = false;
    private String mUrl = null;
    private JYSharedPreferences mPref = null;

    public JYStringRequest(JYSharedPreferences pref, int iMethod, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(iMethod, url, listener, errorListener);
        mUrl = url;
        mPref = pref;

        //int socketTimeout = 30000;//30 seconds - change to what you want
        int socketTimeout = 10000;//10 seconds - change to what you want
        //int socketTimeout = 5000;//50 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        setRetryPolicy(policy);

        CMUtil.CMNetworkType type = CMUtil.isNetworkConnectedType(ApplicationClass.getInstance());
        if (type.compareTo(CMUtil.CMNetworkType.NotConnected) == 0 && JYStringRequest.isCloseAlertShow == false) {

            JYStringRequest.isCloseAlertShow = true;
            CMAlertUtil.Alert(mPref.getContext(), "네트워크 오류", "네트워크에 접속할 수 없습니다. 앱을 종료하시겠습니까?", "", "확인", "취소", false, false , new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    JYStringRequest.isCloseAlertShow = false;
                    Intent exitIntent = new Intent(mPref.getContext(), CMExitActivity.class);
                    exitIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mPref.getContext().startActivity(exitIntent);
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    JYStringRequest.isCloseAlertShow = false;
                }
            }, null, false);
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if ( mPref.isLogging() ) { Log.d(tag, "JYStringRequest() getHeaders() url:"+mUrl); }
        Map<String, String> map = new HashMap<String, String>();
        String cookie = mPref.getValue("Cookie", "");
        if ( cookie.length() > 0 ) {
            map.put("Cookie", cookie);
            if ( mPref.isLogging() ) { Log.d(tag, "JYStringRequest() getHeaders() cookie:" + cookie); }
        }
        return map;
    }

    /*
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        Map<String, String> headers = response.headers;
        if (headers.containsKey("session")) {
            //
        }
        if (headers.containsKey("Set-Cookie")) {
            String cookies = headers.get("Set-Cookie").toString();
            String[] words = cookies.split(";");
            String cookie = words[0];
            mPref.put("Cookie", cookie);
            if ( mPref.isLogging() ) {
                Log.d(tag, "JYStringRequest() parseNetworkResponse() cookie:" + cookie);
            }
        }
        return super.parseNetworkResponse(response);
//        String responseUtf8 = null;
//        try {
//            responseUtf8 = new String(response.data,"UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        return super.parseNetworkResponse(responseUtf8);
    }
    */

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String utf8String;
        try {
            utf8String = new String(response.data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            utf8String = new String(response.data);
        }
        return Response.success(utf8String, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        //return super.parseNetworkError(volleyError);
        if ( volleyError.networkResponse != null && volleyError.networkResponse.data != null ) {
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
        }

        return volleyError;
    }
}
