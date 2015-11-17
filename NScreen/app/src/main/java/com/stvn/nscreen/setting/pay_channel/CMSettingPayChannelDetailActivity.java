package com.stvn.nscreen.setting.pay_channel;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.util.LruCache;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
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
 * 설정화면 > 유료채널 안내 > 채널 상세 화면
 * Created by kimwoodam on 2015. 9. 30..
 */

public class CMSettingPayChannelDetailActivity extends CMBaseActivity implements View.OnClickListener {

    private NetworkImageView mChannelImage;
    private TextView mContentText;

    private ImageLoader mImageLoader;

    private String mJoyID;
    private RequestQueue mRequestQueue;
    private JYSharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_pay_channel_detail);

        Intent recvIntent = getIntent();
        String title = recvIntent.getStringExtra("Joy_Title");
        this.mJoyID = recvIntent.getStringExtra("Joy_ID");

        setActionBarInfo(title, CMActionBar.CMActionBarStyle.BACK);

        this.mPref = new JYSharedPreferences(this);
        this.mRequestQueue = Volley.newRequestQueue(this);

        this.mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(100);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });

        initializeView();

        requestPayChannelInfo();
    }

    @Override
    public void onBackEventPressed() {
        finish();
    }

    /**
     * 설정 메인 화면 UI 설정
     * */
    private void initializeView() {
        this.mChannelImage = (NetworkImageView)findViewById(R.id.setting_pay_channel_detail_image);
        this.mContentText = (TextView)findViewById(R.id.setting_pay_channel_detail_content);
        findViewById(R.id.setting_pay_channel_detail_confirm).setOnClickListener(this);
    }

//    GetServiceJoyNInfo
    private void requestPayChannelInfo() {
        showProgressDialog("", getString(R.string.wait_a_moment));
    //        String terminalKey = JYSharedPreferences.RUMPERS_TERMINAL_KEY;
        String url = mPref.getRumpersServerUrl() + "/GetServiceJoyNInfo.asp?joyNId=" + this.mJoyID;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parsePayChannelInfo(response);
                hideProgressDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                CMLog.e("CMSettingPayChannelDetailActivity", error.getMessage());
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("version", String.valueOf(1));
                params.put("joyNId", mJoyID);
                CMLog.d("CMSettingPayChannelDetailActivity", params.toString());
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    private void parsePayChannelInfo(String response) {
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

                    if (xpp.getName().equalsIgnoreCase("Joy_ID")) {
                        sb.append("{\"Joy_ID\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("Joy_Title")) {
                        sb.append(",\"Joy_Title\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("Joy_SubTitle")) {
                        sb.append(",\"Joy_SubTitle\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("Joy_Img")) {
                        sb.append(",\"Joy_Img\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("Joy_Content")) {
                        String strContent = xpp.nextText().replaceAll("\"", "'");
                        sb.append(",\"Joy_Content\":\"").append(strContent).append("\"}");
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

        try {
            JSONObject jsonObj = new JSONObject(sb.toString());
            String strContent = jsonObj.getString("Joy_Content");
            String strImgPath = jsonObj.getString("Joy_Img");
            this.mChannelImage.setImageUrl(strImgPath, mImageLoader);
            this.mContentText.setText(Html.fromHtml(strContent));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_pay_channel_detail_confirm: {
                finish();
                break;
            }
        }
    }
}
