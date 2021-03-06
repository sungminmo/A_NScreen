package com.stvn.nscreen;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.Constants;
import com.jjiya.android.common.IOnBackPressedListener;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.http.JYStringRequest;
import com.stvn.nscreen.vod.VodMainBaseFragment;
import com.stvn.nscreen.vod.VodMainFirstTabFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String                 tag = MainActivity.class.getSimpleName();
    public static final int Result_LeftMenu_Code = 1200;
    public  static       MainActivity           mInstance;
    private              IOnBackPressedListener mIOnBackPressedListener;
    private              JYSharedPreferences    mPref;
    private              ProgressDialog         mProgressDialog;
    // network
    private              RequestQueue           mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInstance     = this;
        mPref         = new JYSharedPreferences(this);
        mRequestQueue = Volley.newRequestQueue(this);

        String sWebhasTerminalKey = mPref.getWebhasTerminalKey();
        Log.d(tag, "sWebhasTerminalKey:"+sWebhasTerminalKey);

        if ( mPref.isPairingCompleted() ) {
            requestGetWishList();
        }

        /* StatusBarColor ----------------------------------------------------------------------- */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);          // clear FLAG_TRANSLUCENT_STATUS flag:
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);  // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.setStatusBarColor(getResources().getColor(R.color.violet));              // finally change the color
        }

        VodMainFirstTabFragment firstTabFragment = new VodMainFirstTabFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_placeholder, firstTabFragment, "CurrentFragment");
        fragmentTransaction.commit();

        //mPref.addWatchTvAlarm("S321387639", "", "국악무대5000", "2015-11-10 20:50:00");
        //mPref.addWatchTvReserveAlarm("S321387639", "", "1 42", "2015-11-29 17:42:00");
        //mPref.addWatchTvReserveAlarm("S321387639", "", "2 44", "2015-11-29 17:44:00");
        //mPref.addWatchTvReserveAlarm("S321387639", "", "3 46", "2015-11-29 17:46:00");
        //mPref.addWatchTvReserveAlarm("S321387639", "", "밥먹자", "2015-11-29 17:50:00");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if ( mIOnBackPressedListener == null ) {
            super.onBackPressed();
        } else {
            mIOnBackPressedListener.onBackPressedCallback();
        }
    }

    public void setOnBackPressedListener(IOnBackPressedListener listener) {
        mIOnBackPressedListener = listener;
    }

    /**
     * 찜하기 리스트 받기
     * http://58.141.255.79:8080/HApplicationServer/getWishList.json?version=1&terminalKey=B2F311C9641A0CCED9C7FE95BE624D9&transactionId=1
     */
    private void requestGetWishList() {
        mProgressDialog	 = ProgressDialog.show(mInstance, "", getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestGetWishList()"); }
        String terminalKey = mPref.getWebhasTerminalKey();
        String uuid = mPref.getValue(JYSharedPreferences.UUID, "");
        String url = mPref.getWebhasServerUrl() + "/getWishList.json?version=1&terminalKey="+terminalKey+"&userId="+uuid;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
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

                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MainActivity.Result_LeftMenu_Code) {
            if (resultCode == Activity.RESULT_OK) {
                boolean isRemovePairing = data.getBooleanExtra("isRemovePairing", false);
                if (isRemovePairing) {

                    VodMainBaseFragment myFragment = (VodMainBaseFragment)getFragmentManager().findFragmentByTag("CurrentFragment");
                    if (myFragment != null && myFragment.isVisible()) {
                        myFragment.mTab1TextView.performClick();
                    }
                }
            }
        }
    }
}
