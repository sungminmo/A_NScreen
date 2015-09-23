package com.jjiya.android.http;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.jjiya.android.common.JYSharedPreferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by swlim
 */
public class JYStringRequest extends StringRequest {

    private static final String tag = JYStringRequest.class.getSimpleName();
    private String mUrl = null;
    private JYSharedPreferences mPref = null;

    public JYStringRequest(JYSharedPreferences pref, int iMethod, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(iMethod, url, listener, errorListener);
        mUrl = url;
        mPref = pref;
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
    }
}
