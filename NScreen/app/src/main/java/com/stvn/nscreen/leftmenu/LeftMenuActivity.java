package com.stvn.nscreen.leftmenu;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.jjiya.android.http.JYStringRequest;
import com.stvn.nscreen.R;
import com.stvn.nscreen.epg.EpgMainActivity;
import com.stvn.nscreen.my.MyMainActivity;
import com.stvn.nscreen.pairing.PairingMainActivity;
import com.stvn.nscreen.pvr.PvrMainActivity;
import com.stvn.nscreen.rmt.RemoteControllerActivity;
import com.stvn.nscreen.setting.CMSettingCustomerCenterActivity;
import com.stvn.nscreen.setting.CMSettingMainActivity;
import com.stvn.nscreen.util.CMAlertUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by limdavid on 15. 10. 30..
 */

public class LeftMenuActivity extends Activity {
    private static final String              tag = LeftMenuActivity.class.getSimpleName();
    private static       LeftMenuActivity    mInstance;
    private              JYSharedPreferences mPref;
    private RequestQueue mRequestQueue;

    private LinearLayout leftmenu_tv_linearLayout, leftmenu_remote_linearLayout, leftmenu_pvr_linearLayout, leftmenu_my_linearLayout, leftmenu_setting_linearLayout, leftmenu_right;

    private Button leftmenu_pairing_button1, leftmenu_pairing_button2, leftmenu_agreement_button;

    private ImageButton imageButton2, leftmenu_whatBtn;

    private TextView leftmenu_version_textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        setContentView(R.layout.activity_leftmenu);

        // getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams wmlp = getWindow().getAttributes();
        wmlp.gravity = Gravity.LEFT;

        mInstance     = this;
        mPref         = new JYSharedPreferences(this);
        mRequestQueue = Volley.newRequestQueue(this);
        if (mPref.isLogging()) {
            Log.d(tag, "onCreate()");
        }
        String appVer = mPref.getAppVersionForApp();

        leftmenu_tv_linearLayout = (LinearLayout) findViewById(R.id.leftmenu_tv_linearLayout);
        leftmenu_remote_linearLayout = (LinearLayout) findViewById(R.id.leftmenu_remote_linearLayout);
        leftmenu_pvr_linearLayout = (LinearLayout) findViewById(R.id.leftmenu_pvr_linearLayout);
        leftmenu_my_linearLayout = (LinearLayout) findViewById(R.id.leftmenu_my_linearLayout);
        leftmenu_setting_linearLayout = (LinearLayout) findViewById(R.id.leftmenu_setting_linearLayout);
        leftmenu_right = (LinearLayout) findViewById(R.id.leftmenu_right);
        leftmenu_pairing_button1 = (Button) findViewById(R.id.leftmenu_pairing_button1);
        leftmenu_pairing_button2 = (Button) findViewById(R.id.leftmenu_pairing_button2);
        imageButton2 = (ImageButton) findViewById(R.id.imageButton2);
        leftmenu_whatBtn = (ImageButton) findViewById(R.id.leftmenu_whatBtn);
        leftmenu_agreement_button = (Button) findViewById(R.id.button5);
        leftmenu_version_textview = (TextView) findViewById(R.id.leftmenu_version_textview);
        leftmenu_version_textview.setText("현재 버전 " + appVer);

        reloadPairingUI();

        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        leftmenu_pairing_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInstance, PairingMainActivity.class);
                startActivity(intent);
//                finish();
            }
        });

        leftmenu_pairing_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String alertTitle = "셋탑박스 재 연동";
                String alertMsg1 = getString(R.string.error_not_paring_compleated4);
                String alertMsg2 = getString(R.string.error_not_paring_compleated5);
                CMAlertUtil.Alert1(mInstance, alertTitle, alertMsg1, alertMsg2, false, true, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestRemoveUser();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
            }
        });

        leftmenu_tv_linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInstance, EpgMainActivity.class);
                startActivity(intent);
//                finish();
            }
        });
        leftmenu_remote_linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String SetTopBoxKind = mPref.getValue(JYSharedPreferences.RUMPERS_SETOPBOX_KIND, "").toLowerCase();
                if (mPref.isPairingCompleted() == false) {
                    String alertTitle = "리모컨 미 지원 상품";
                    String alertMsg1 = getString(R.string.error_not_paring_compleated2);
                    String alertMsg2 = "";
                    CMAlertUtil.Alert1(mInstance, alertTitle, alertMsg1, alertMsg2, false, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }, true);

                } else {
                    // 페어링은 했지만, HD/PVR이 아니면 화면 진입 못함.
                    if ("HD".toLowerCase().equals(SetTopBoxKind) || "PVR".toLowerCase().equals(SetTopBoxKind)) {
                        Intent intent = new Intent(mInstance, RemoteControllerActivity.class);
                        startActivity(intent);
//                        finish();
                    } else {
                        String alertTitle = "녹화 미 지원 상품";
                        String alertMsg1 = getString(R.string.error_not_paring_compleated2);
                        String alertMsg2 = "";
                        CMAlertUtil.Alert1(mInstance, alertTitle, alertMsg1, alertMsg2, false, false, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }, true);
                    }
                }
            }
        });
        leftmenu_pvr_linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String SetTopBoxKind = mPref.getValue(JYSharedPreferences.RUMPERS_SETOPBOX_KIND, "").toLowerCase();
                if (mPref.isPairingCompleted() == false) {
                    String alertTitle = "녹화 미 지원 상품";
                    String alertMsg1 = getString(R.string.error_not_paring_compleated);
                    String alertMsg2 = "";
                    CMAlertUtil.Alert1(mInstance, alertTitle, alertMsg1, alertMsg2, false, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }, true);
                } else {
                    // 페어링은 했지만, PVR이 아니면 화면 진입 못함.
                    if ("PVR".toLowerCase().equals(SetTopBoxKind)) {
                        Intent intent = new Intent(mInstance, PvrMainActivity.class);
                        startActivity(intent);
//                        finish();
                    } else {
                        String alertTitle = "녹화 미 지원 상품";
                        String alertMsg1 = getString(R.string.error_not_paring_compleated);
                        String alertMsg2 = "";
                        CMAlertUtil.Alert1(mInstance, alertTitle, alertMsg1, alertMsg2, false, false, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }, true);
                    }
                }
            }
        });
        leftmenu_my_linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPref.isPairingCompleted() == false) {
                    String alertTitle = "마이 씨앤앰 미 지원 상품";
                    String alertMsg1 = getString(R.string.error_not_paring_compleated1);
                    String alertMsg2 = "";
                    CMAlertUtil.Alert1(mInstance, alertTitle, alertMsg1, alertMsg2, false, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }, true);

                } else {
                    Intent intent = new Intent(mInstance, MyMainActivity.class);
                    startActivity(intent);
//                    finish();
                }
            }
        });

        leftmenu_setting_linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mInstance, CMSettingMainActivity.class);
                startActivity(intent);
//                finish();
            }
        });

        leftmenu_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        leftmenu_whatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(mInstance, LeftMenuDialogActivity.class);
//                startActivity(intent);
                String alertTitle = "셋탑박스 연동이란?";
                String alertMsg = getString(R.string.whatSetTop);
                CMAlertUtil.Alert(mInstance, alertTitle, alertMsg);
            }
        });

        leftmenu_agreement_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextIntent = new Intent(LeftMenuActivity.this, CMSettingCustomerCenterActivity.class);
                nextIntent.putExtra("GUIDE_ID", "1");
                startActivity(nextIntent);
//                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadPairingUI();
    }

    private void reloadPairingUI() {
        if (mPref.isPairingCompleted() == true) {
            ((TextView) findViewById(R.id.leftmenu_pairing_textview1)).setText("");
            ((TextView) findViewById(R.id.leftmenu_pairing_textview2)).setText("셋탑박스와 연동중입니다.");
            leftmenu_pairing_button1.setVisibility(View.GONE);
            leftmenu_pairing_button2.setVisibility(View.VISIBLE);
        } else {
            ((TextView) findViewById(R.id.leftmenu_pairing_textview1)).setText("원활한 서비스 이용을 위해");
            ((TextView) findViewById(R.id.leftmenu_pairing_textview2)).setText("셋탑박스를 연동해주세요.");
            leftmenu_pairing_button1.setVisibility(View.VISIBLE);
            leftmenu_pairing_button2.setVisibility(View.GONE);
        }
    }

    private void requestRemoveUser() {
        if ( mPref.isLogging() ) { Log.d(tag, "requestRemoveUser()"); }
        String terminalKey = mPref.getWebhasTerminalKey();
        String userId      = mPref.getValue(JYSharedPreferences.UUID,"");
        String url = mPref.getWebhasServerUrl() + "/removeUser.json?version=1&terminalKey="+terminalKey+"&userId="+userId;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject root    = new JSONObject(response);
                    String resultCode  = root.getString("resultCode");
                    if ( Constants.CODE_WEBHAS_OK.equals(resultCode) ) {
                        mPref.removePairingInfo();
                        mPref.makeUUID();   // 사용자가 일부러 재등록을 했다면, UUID를 새로 만들어 줘야 한다.
                        // 성인인증 관련 정보가 변경되어 메인 페이지의 reload 처리를 한다.
                        sendBroadcast(new Intent(JYSharedPreferences.I_AM_ADULT));

                        reloadPairingUI();

                        Intent intent = new Intent(mInstance, PairingMainActivity.class);
                        startActivity(intent);
//                        finish();
                    } else {
                        //
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ( mPref.isLogging() ) { VolleyLog.d(tag, "onErrorResponse(): " + error.getMessage()); }
                if (error instanceof TimeoutError) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_noconnectionerror), Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_servererror), Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_networkerrorr), Toast.LENGTH_LONG).show();
                }
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