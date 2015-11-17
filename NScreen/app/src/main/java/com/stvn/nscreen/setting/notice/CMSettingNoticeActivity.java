package com.stvn.nscreen.setting.notice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.common.ListViewDataObject;
import com.jjiya.android.http.JYStringRequest;
import com.stvn.nscreen.R;
import com.stvn.nscreen.common.CMActionBar;
import com.stvn.nscreen.common.CMBaseActivity;
import com.stvn.nscreen.util.CMLog;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 설정화면 > 공지사항
 * Created by kimwoodam on 2015. 11. 15..
 */
public class CMSettingNoticeActivity extends CMBaseActivity implements AdapterView.OnItemClickListener {

    private ListView mListView;
    private CMSettingNoticeAdapter mAdapter;
    private ArrayList<ListViewDataObject> mNoticeList;

    // network
    private RequestQueue mRequestQueue;
    private ProgressDialog mProgressDialog;

    private JYSharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_notice);
        setActionBarInfo("공지사항", CMActionBar.CMActionBarStyle.BACK);

        this.mPref = new JYSharedPreferences(this);
        this.mRequestQueue = Volley.newRequestQueue(this);
        this.mNoticeList = new ArrayList<ListViewDataObject>();
        initializeView();

        requestNoticeList();
    }

    @Override
    public void onBackEventPressed() {
        finish();
    }

    /**
     * 지역설정 화면 UI 설정
     * */
    private void initializeView() {

        this.mAdapter = new CMSettingNoticeAdapter(this, mNoticeList);
        this.mListView = (ListView)findViewById(R.id.setting_notice_listview);
        this.mListView.setOnItemClickListener(this);
        this.mListView.setAdapter(this.mAdapter);
    }

    private void requestNoticeList() {
        mProgressDialog	 = ProgressDialog.show(this, "", getString(R.string.wait_a_moment));

        String url = mPref.getRumpersServerUrl() + "/GetServiceNoticeInfo.asp";
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseNoticeList(response);
                mAdapter.notifyDataSetChanged();
                mProgressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                CMLog.e("CMSettingPayChannelActivity", error.getMessage());
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    private void parseNoticeList(String response) {
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

                    if (xpp.getName().equalsIgnoreCase("noticeId")) {
                        sb.append("{\"noticeId\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("notice_Location")) {
                        sb.append(",\"notice_Location\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("notice_area")) {
                        sb.append(",\"notice_area\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("notice_product")) {
                        sb.append(",\"notice_product\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("notice_StartDate")) {
                        sb.append(",\"notice_StartDate\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("notice_EndDate")) {
                        sb.append(",\"notice_EndDate\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("notice_Title")) {
                        sb.append(",\"notice_Title\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("notice_Content")) {
                        String strContent = xpp.nextText().replaceAll("\"", "'");
                        strContent = strContent.replaceAll("\\n", "<br/>");
                        strContent = strContent.replaceAll("\\r", "<br/>");

                        sb.append(",\"notice_Content\":\"").append(strContent).append("\"}");
                        ListViewDataObject obj = new ListViewDataObject(mAdapter.getCount(), 0, sb.toString());
                        mNoticeList.add(obj);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListViewDataObject info = (ListViewDataObject)parent.getItemAtPosition(position);
        Intent nextIntent = new Intent(CMSettingNoticeActivity.this, CMSettingNoticeDetailActivity.class);
        nextIntent.putExtra("Notice_Info", info.sJson);
        startActivity(nextIntent);
    }
}