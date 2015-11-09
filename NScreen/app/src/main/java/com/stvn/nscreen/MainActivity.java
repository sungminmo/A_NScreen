package com.stvn.nscreen;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.Constants;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.http.JYStringRequest;
import com.stvn.nscreen.bean.WishObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    private static final String              tag = MainActivity.class.getSimpleName();
    public  static       MainActivity        mInstance;
    private              JYSharedPreferences mPref;
    private              ProgressDialog      mProgressDialog;
    // network
    private              RequestQueue        mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mInstance     = this;
        mPref         = new JYSharedPreferences(this);
        mRequestQueue = Volley.newRequestQueue(this);


        String sWebhasTerminalKey = mPref.getWebhasTerminalKey();
        Log.d(tag, "sWebhasTerminalKey:"+sWebhasTerminalKey);



        if ( mPref.getValue(JYSharedPreferences.UUID, "").equals("") ) {
            // 앱을 설치하고 처음 실행했다면, 여디로 들어온다.
            // UUID 없으면 만들기.
            UUID uuid = UUID.randomUUID();
            mPref.put(JYSharedPreferences.UUID, uuid.toString());
        }

        if ( mPref.isPairingCompleted() ) {
            requestGetWishList();
        }

        /*
        Intent i = new Intent(MainActivity.this, com.stvn.nscreen.epg.EpgMainActivity.class);
        Intent i = new Intent(MainActivity.this, com.stvn.nscreen.my.MyMainActivity.class);
        Intent i = new Intent(MainActivity.this, com.stvn.nscreen.my.MySubActivity.class);
        Intent i = new Intent(MainActivity.this, com.stvn.nscreen.pvr.PvrMainActivity.class);
        Intent i = new Intent(MainActivity.this, com.stvn.nscreen.pvr.PvrSubActivity.class);
        Intent i = new Intent(MainActivity.this, com.stvn.nscreen.search.SearchMainActivity.class);
        Intent i = new Intent(MainActivity.this, com.stvn.nscreen.setting.CMSettingMainActivity.class);
        Intent i = new Intent(MainActivity.this, com.stvn.nscreen.vod.VodMainActivity.class);
        Intent i = new Intent(MainActivity.this, com.stvn.nscreen.vod.VodDetailActivity.class);
        Intent i = new Intent(MainActivity.this, PairingMainActivity.class);
        Intent i = new Intent(MainActivity.this, PairingSubActivity.class);
        Intent i = new Intent(MainActivity.this, PairingCheckActivity.class);
        Intent i = new Intent(MainActivity.this, VodBuyActivity.class);
        Intent i = new Intent(MainActivity.this, RemoteControllerActivity.class);
        */

        /* StatusBarColor ----------------------------------------------------------------------- */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);          // clear FLAG_TRANSLUCENT_STATUS flag:
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);  // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.setStatusBarColor(getResources().getColor(R.color.violet));              // finally change the color
        }

        /* ActionBar TAB mode ------------------------------------------------------------------- */
        /*
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.violet)));

        VodMainFragment  vodMainFragment = new VodMainFragment();
        VodEmptyFragment vodEmptyFragment = new VodEmptyFragment();

        ActionBar.Tab tab1 = actionBar.newTab().setText("추천").setTabListener(new MainTabsListener(vodMainFragment));
        ActionBar.Tab tab2 = actionBar.newTab().setText("영화").setTabListener(new MainTabsListener(vodEmptyFragment));
        ActionBar.Tab tab3 = actionBar.newTab().setText("애니키즈").setTabListener(new MainTabsListener(vodEmptyFragment));
        ActionBar.Tab tab4 = actionBar.newTab().setText("TV다시보기").setTabListener(new MainTabsListener(vodEmptyFragment));
        ActionBar.Tab tab5 = actionBar.newTab().setText("성인").setTabListener(new MainTabsListener(vodEmptyFragment));

        actionBar.addTab(tab1);
        actionBar.addTab(tab2);
        actionBar.addTab(tab3);
        actionBar.addTab(tab4);
        actionBar.addTab(tab5);
        */
    }

    /*
    class MainTabsListener implements ActionBar.TabListener {
        private Fragment fragment;
        public MainTabsListener(Fragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
            //do what you want when tab is reselected, I do nothing
            //Toast.makeText(mInstance, "Reselected()", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            ft.replace(R.id.fragment_placeholder, fragment);
            //Toast.makeText(mInstance, "Selected()", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            ft.remove(fragment);
            //Toast.makeText(mInstance, "Unselected()", Toast.LENGTH_SHORT).show();
        }
    }
    */

    /**
     * 찜하기 리스트 받기
     * http://58.141.255.79:8080/HApplicationServer/getWishList.json?version=1&terminalKey=B2F311C9641A0CCED9C7FE95BE624D9&transactionId=1
     */
    private void requestGetWishList() {
        mProgressDialog	 = ProgressDialog.show(mInstance, "", getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestGetWishList()"); }
        String terminalKey = mPref.getWebhasTerminalKey();
        String url = mPref.getWebhasServerUrl() + "/getWishList.json?version=1&terminalKey="+terminalKey;
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
}
