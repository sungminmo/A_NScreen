package com.stvn.nscreen.search;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.http.JYStringRequest;
import com.stvn.nscreen.R;
import com.stvn.nscreen.common.SearchDataObject;
import com.stvn.nscreen.util.CMLog;
import com.stvn.nscreen.util.CMUtil;

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
 * Created by leejunghoon on 15. 9. 19..
 */

public class SearchVodFragment extends SearchBaseFragment{

    private LayoutInflater mInflater;
    private GridView mGridView;
    private ArrayList<SearchDataObject> mProgramlist = new ArrayList<SearchDataObject>();
    private SearchVodAdapter mAdapter;

    private String mTerminalKey = "9CED3A20FB6A4D7FF35D1AC965F988D2";
    private String mKeyword;
    private RequestQueue mRequestQueue;
    private ProgressDialog mProgressDialog;

    private JYSharedPreferences mPref;
    private boolean mLockListView = true;
    private int mTotCnt;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        return mInflater.inflate(R.layout.fragment_search_vod,null);
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRequestQueue = Volley.newRequestQueue(getActivity());
        mPref = new JYSharedPreferences(getActivity());
        mKeyword = getArguments().getString("KEYWORD");
        initView();
        reqVodList();
    }

    private void initView()
    {
        mGridView = (GridView)getView().findViewById(R.id.programgridview);
        mAdapter = new SearchVodAdapter(getActivity(),mProgramlist);
        mGridView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    public void reqVodList()
    {
        mLockListView = true;
        mProgressDialog	 = ProgressDialog.show(getActivity(), "", getString(R.string.wait_a_moment));
        String url = mPref.getWebhasServerUrl()+"/searchContentGroup.xml?version=1&terminalKey="+mTerminalKey+"&searchKeyword="+mKeyword;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(tag, response);
                parseGetVodList(response);
                mAdapter.notifyDataSetChanged();
                mProgressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                CMLog.e("CM", error.getMessage());
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("version", String.valueOf(1));
                params.put("areaCode", String.valueOf(0));
                CMLog.e("CM", params.toString());
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    private void parseGetVodList(String response) {

        StringBuilder sb = new StringBuilder();
        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new ByteArrayInputStream(response.getBytes("utf-8")), "utf-8");

            int eventType = xpp.getEventType();
            SearchDataObject object = null;
            while(eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;

                switch (eventType)
                {
                    case XmlPullParser.START_TAG:
                        name = xpp.getName();
                        if("totalCount".equals(name)){
                            mTotCnt = CMUtil.parseInt(xpp.nextText());
                        }else if("scheduleItem".equals(name)){
                            object = new SearchDataObject();
                        }else if("channelId".equals(name)){
                            object.setChannelId(xpp.nextText());
                        }else if("channelNumber".equals(name)){
                            object.setChannelNumber(xpp.nextText());
                        }else if("channelName".equals(name)){
                            object.setChannelName(xpp.nextText());
                        }else if("channelInfo".equals(name)){
                            object.setChannelInfo(xpp.nextText());
                        }else if("channelLogoImg".equals(name)){
                            object.setChannelLogoImg(xpp.nextText());
                        }else if("channelProgramID".equals(name)){
                            object.setChannelProgramID(xpp.nextText());
                        }else if("channelProgramTime".equals(name)){
                            object.setChannelProgramTime(xpp.nextText());
                        }else if("channelProgramTitle".equals(name)){
                            object.setChannelProgramTitle(xpp.nextText());
                        }else if("channelProgramSeq".equals(name)){
                            object.setChannelProgramSeq(xpp.nextText());
                        }else if("channelProgramGrade".equals(name)){
                            object.setChannelProgramGrade(xpp.nextText());
                        }else if("channelProgramHD".equals(name)){
                            object.setChannelProgramHD(xpp.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = xpp.getName();
                        if("scheduleItem".equals(name))
                        {
                            if(object!=null)
                            {
                                mProgramlist.add(object);
                            }
                        }
                        break;

                }
                eventType = xpp.next();
            }
            mLockListView = false;
            mAdapter.notifyDataSetChanged();
            ((SearchMainActivity)getActivity()).setSearchCountText(mTotCnt);

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
