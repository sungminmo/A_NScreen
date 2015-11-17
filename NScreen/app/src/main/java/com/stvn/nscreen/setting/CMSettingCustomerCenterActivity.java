package com.stvn.nscreen.setting;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.http.JYStringRequest;
import com.stvn.nscreen.R;
import com.stvn.nscreen.common.CMActionBar;
import com.stvn.nscreen.common.CMBaseActivity;
import com.stvn.nscreen.util.CMLog;

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
 * 설정화면 > 고객센터 안내
 * Created by kimwoodam on 2015. 11. 15..
 */
public class CMSettingCustomerCenterActivity extends CMBaseActivity implements View.OnClickListener {

    private TextView mContentTitle;
    private TextView mContentText;

    private String mGuideID;
    // network
    private RequestQueue mRequestQueue;

    private JYSharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_customer_center);

        this.mPref = new JYSharedPreferences(this);
        this.mRequestQueue = Volley.newRequestQueue(this);

        if (getIntent().hasExtra("GUIDE_ID")) {
            this.mGuideID = getIntent().getStringExtra("GUIDE_ID");
        } else {
            this.mGuideID = "1";
        }

        String strTitle = this.mGuideID.equals("1")?"서비스 이용약관":"고객센터 안내";
        setActionBarInfo(strTitle, CMActionBar.CMActionBarStyle.BACK);

        initializeView();
        requestServiceGuide();
    }

    @Override
    public void onBackEventPressed() {
        finish();
    }

    /**
     * 설정 메인 화면 UI 설정
     * */
    private void initializeView() {
        this.mContentTitle = (TextView)findViewById(R.id.setting_customer_center_title);
        this.mContentText = (TextView)findViewById(R.id.setting_customer_center_content);
        findViewById(R.id.setting_customer_center_confirm).setOnClickListener(this);
    }

    private void requestServiceGuide() {
        showProgressDialog("", getString(R.string.wait_a_moment));

        String url = mPref.getRumpersServerUrl() + "/GetServiceguideInfo.asp?guideID=" + mGuideID;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                CMLog.d("wd", response);
                String jString = parseServiceGuide(response);

               try {
                    JSONObject noticeData = new JSONObject(jString);

                    String title = noticeData.getString("guide_title");
                    mContentTitle.setText(title);

                    String contents = noticeData.getString("guide_Content");
                    mContentText.setText(Html.fromHtml(contents));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                hideProgressDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                CMLog.e("CMSettingCustomerCenterActivity", error.getMessage());
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("guideID", mGuideID);
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    private String parseServiceGuide(String response) {
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

                    if (xpp.getName().equalsIgnoreCase("guideId")) {
                        sb.append("{\"guideId\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("guide_title")) {
                        sb.append(",\"guide_title\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("guide_Content")) {

                        String strContent = xpp.nextText().replaceAll("\"", "'");
                        sb.append(",\"guide_Content\":\"").append(strContent).append("\"}");
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

        return sb.toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_customer_center_confirm: {
                finish();
                break;
            }
        }
    }
}