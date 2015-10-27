package com.stvn.nscreen.pairing;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.Constants;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.common.ListViewDataObject;
import com.jjiya.android.http.JYStringRequest;
import com.stvn.nscreen.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by limdavid on 15. 10. 23..
 */
public class PairingSubActivity extends AppCompatActivity {

    private static final String              tag = PairingSubActivity.class.getSimpleName();
    private              PairingSubActivity  mInstance;
    private              JYSharedPreferences mPref;

    // network
    private              RequestQueue        mRequestQueue;
    private              ProgressDialog      mProgressDialog;

    private String mPurchasePassword;

    private EditText mAuthCodeEditText;
    private Button   mCancleButton;
    private Button   mNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing_sub);

        mInstance         = this;
        mPref             = new JYSharedPreferences(this);
        mRequestQueue     = Volley.newRequestQueue(this);

        if ( getIntent().getExtras() != null ) {
            mPurchasePassword = getIntent().getExtras().getString("purchasePassword", "");
            if ( mPurchasePassword.length() < 4 ) {
                // ?
            }
        }

        mAuthCodeEditText = (EditText)findViewById(R.id.pairing_sub_authcode_edittext);
        mCancleButton = (Button)findViewById(R.id.pairing_sub_cancle_button);
        mNextButton   = (Button)findViewById(R.id.pairing_sub_next_button);

        mCancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( mAuthCodeEditText.getText().toString().length() < 6  ) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(mInstance);
                    alert.setPositiveButton("알림", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.setMessage("6자리의 인증번호를 입력해 주십시오.");
                    alert.show();
                } else {
                    mCancleButton.setEnabled(false);
                    mNextButton.setEnabled(false);
                    requestAddUser();
                }

            }
        });
    }

    // 5.20.4 AddUser
    // (모바일 찜하기) 발급된 인증번호를 이용하여 셋탑에 사용자(스마트폰)를 등록하도록 요청한다
    private void requestAddUser() {
        if ( mPref.isLogging() ) { Log.d(tag, "requestAddUser()"); }
        mProgressDialog	 = ProgressDialog.show(mInstance, "", getString(R.string.wait_a_moment));
        String authCode    = mAuthCodeEditText.getText().toString();
        String terminalKey = mPref.getWebhasTerminalKey();
        String uuid        = mPref.getValue(JYSharedPreferences.UUID, "");
        String url = mPref.getWebhasServerUrl() + "/addUser.json?version=1&terminalKey="+terminalKey+"&userId="+uuid+"&authCode="+authCode;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(tag, response);
                try {
                    JSONObject jo = new JSONObject(response);
                    String resultCode = jo.getString("resultCode");
                    if ( Constants.CODE_WEBHAS_OK.equals(resultCode) ) {
                        // addUser 성공 했으면, 바로 5.1.2 AuthenticateDevice 호출해서 webhas private tk 받기.
                        requestAuthenticateDevice();
                    } else {
                        mProgressDialog.dismiss();
                        mCancleButton.setEnabled(true);
                        mNextButton.setEnabled(true);
                        String errorString = jo.getString("errorString");
                        AlertDialog.Builder alert = new AlertDialog.Builder(mInstance);
                        alert.setPositiveButton("알림", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alert.setMessage("(" + resultCode + ")\n" + errorString);
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
                mCancleButton.setEnabled(true);
                mNextButton.setEnabled(true);
                if ( mPref.isLogging() ) { VolleyLog.d(tag, "onErrorResponse(): " + error.getMessage()); }
                AlertDialog.Builder alert = new AlertDialog.Builder(mInstance);
                alert.setPositiveButton("알림", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.setMessage(error.getMessage());
                alert.show();
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                if ( mPref.isLogging() ) { Log.d(tag, "getParams()" + params.toString()); }
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    // 5.1.2 authenticateDevice
    // 2nd 단말 클라이언트를 인증하고 TerminalKey를 얻는다
    private void requestAuthenticateDevice() {
        if ( mPref.isLogging() ) { Log.d(tag, "requestAddUser()"); }
        String uuid        = mPref.getValue(JYSharedPreferences.UUID, "");
        String url = mPref.getWebhasServerUrl() + "/authenticateDevice.json?version=1&secondDeviceId="+uuid;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(tag, response);
                mProgressDialog.dismiss();
                mCancleButton.setEnabled(true);
                mNextButton.setEnabled(true);
                try {
                    JSONObject jo = new JSONObject(response);
                    String resultCode = jo.getString("resultCode");
                    if ( Constants.CODE_WEBHAS_OK.equals(resultCode) ) {
                        String terminalKey = jo.getString("terminalKey");
                        mPref.put(JYSharedPreferences.WEBHAS_PRIVATE_TERMINAL_KEY, terminalKey); // 터미널키 저장.
                        mPref.put(JYSharedPreferences.PURCHASE_PASSWORD, mPurchasePassword);     // 구매비번 저장.
                        // 그다음은 페어링 완료 UI 처리 해야 됨.
                    } else {
                        String errorString = jo.getString("errorString");
                        AlertDialog.Builder alert = new AlertDialog.Builder(mInstance);
                        alert.setPositiveButton("알림", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alert.setMessage(errorString + "(" + resultCode+")");
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
                mCancleButton.setEnabled(true);
                mNextButton.setEnabled(true);
                if ( mPref.isLogging() ) { VolleyLog.d(tag, "onErrorResponse(): " + error.getMessage()); }
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                if ( mPref.isLogging() ) { Log.d(tag, "getParams()" + params.toString()); }
                return params;
            }
        };
        mRequestQueue.add(request);
    }


}