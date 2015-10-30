package com.stvn.nscreen.vod;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.EightVodPosterPagerAdapter;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.common.UiUtil;
import com.jjiya.android.common.VodNewMoviePosterPagerAdapter;
import com.jjiya.android.http.BitmapLruCache;
import com.jjiya.android.http.JYStringRequest;
import com.stvn.nscreen.R;
import com.stvn.nscreen.common.CMBaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VodMainFragment extends Fragment {

    private static final String                 tag = VodMainFragment.class.getSimpleName();
    private static       VodMainFragment        mInstance;
    private              JYSharedPreferences    mPref;

    // network
    private              RequestQueue           mRequestQueue;
    private              ProgressDialog         mProgressDialog;
    private              ImageLoader            mImageLoader;

    // gui
    private              ViewPager              mBannerViewPager;
    private              List<JSONObject>       mBanners;

    private              ViewPager                     mPop20ViewPager;
    private              EightVodPosterPagerAdapter    mPop20PagerAdapter; // 인기순위 Top 20

    private              ViewPager                     mNewMovieViewPager;
    private              VodNewMoviePosterPagerAdapter mNewMoviePagerAdapter; // 금주의 신작 영화

    private              ViewPager                     mThisMonthViewPager;
    private              VodNewMoviePosterPagerAdapter mThisMonthPagerAdapter; // 이달의 추천 VOD


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vod_main, container, false);

        mInstance     = this;
        mPref         = new JYSharedPreferences(this.getActivity());
        mRequestQueue = Volley.newRequestQueue(this.getActivity());
        ImageLoader.ImageCache imageCache = new BitmapLruCache();
        mImageLoader  = new ImageLoader(mRequestQueue, imageCache);

        mBanners               = new ArrayList<JSONObject>();
        mPop20PagerAdapter     = new EightVodPosterPagerAdapter(this.getActivity());
        mNewMoviePagerAdapter  = new VodNewMoviePosterPagerAdapter(this.getActivity());
        mThisMonthPagerAdapter = new VodNewMoviePosterPagerAdapter(this.getActivity());

        mPop20PagerAdapter.setImageLoader(mImageLoader);
        mNewMoviePagerAdapter.setImageLoader(mImageLoader);
        mThisMonthPagerAdapter.setImageLoader(mImageLoader);

        mPop20PagerAdapter.setFragment(mInstance);
        mNewMoviePagerAdapter.setFragment(mInstance);
        mThisMonthPagerAdapter.setFragment(mInstance);

        // 배너
        mBannerViewPager = (ViewPager)view.findViewById(R.id.vod_main_event_viewpager);

        // 인가 탑20
        mPop20ViewPager = (ViewPager)view.findViewById(R.id.vod_main_pop20_viewpager);
        mPop20ViewPager.setAdapter(mPop20PagerAdapter);

        // 신작영화
        mNewMovieViewPager = (ViewPager)view.findViewById(R.id.vod_main_newmovie_viewpager);
        mNewMovieViewPager.setAdapter(mNewMoviePagerAdapter);

        // 이달의 추천 VOD
        mThisMonthViewPager = (ViewPager)view.findViewById(R.id.vod_main_thismonth_viewpager);
        mThisMonthViewPager.setAdapter(mThisMonthPagerAdapter);

        // 배너 요청.
        requestGetServiceBannerList();


        ((LinearLayout)view.findViewById(R.id.vod_main_pop20_more_linearlayout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(VodMainFragment.this, VodCategoryMainActivity.class);
                //startActivity(i);
            }
        });

        return view;
    }

    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_vod_main);

        mInstance     = this;
        mPref         = new JYSharedPreferences(this);
        mRequestQueue = Volley.newRequestQueue(this);
        ImageLoader.ImageCache imageCache = new BitmapLruCache();
        mImageLoader  = new ImageLoader(mRequestQueue, imageCache);

        mBanners               = new ArrayList<JSONObject>();
        mPop20PagerAdapter     = new EightVodPosterPagerAdapter(this);
        mNewMoviePagerAdapter  = new VodNewMoviePosterPagerAdapter(this);
        mThisMonthPagerAdapter = new VodNewMoviePosterPagerAdapter(this);

        mPop20PagerAdapter.setImageLoader(mImageLoader);
        mNewMoviePagerAdapter.setImageLoader(mImageLoader);
        mThisMonthPagerAdapter.setImageLoader(mImageLoader);

        if ( mPref.isLogging() ) { Log.d(tag, "onCreate()"); }

//        setActionBarStyle(CMActionBar.CMActionBarStyle.MAIN);
//        setActionBarTitle(getString(R.string.title_activity_main));
        useActionBar(false);


        // 배너
        mBannerViewPager = (ViewPager) findViewById(R.id.vod_main_event_viewpager);

        // 인가 탑20
        mPop20ViewPager = (ViewPager) findViewById(R.id.vod_main_pop20_viewpager);
        mPop20ViewPager.setAdapter(mPop20PagerAdapter);

        // 신작영화
        mNewMovieViewPager = (ViewPager) findViewById(R.id.vod_main_newmovie_viewpager);
        mNewMovieViewPager.setAdapter(mNewMoviePagerAdapter);

        // 이달의 추천 VOD
        mThisMonthViewPager = (ViewPager) findViewById(R.id.vod_main_thismonth_viewpager);
        mThisMonthViewPager.setAdapter(mThisMonthPagerAdapter);

        // 배너 요청.
        requestGetServiceBannerList();


        ((LinearLayout)findViewById(R.id.vod_main_pop20_more_linearlayout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VodMainFragment.this, VodCategoryMainActivity.class);
                startActivity(i);
            }
        });

    }
    */

    // vod_main_pop20_more_linearlayout

    /*
    private AdapterView.OnItemClickListener assetItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //ListViewDataObject item = (ListViewDataObject)mAdapter.getItem(position);
            Intent intent = new Intent(VodMainFragment.this, VodDetailActivity.class);
            //intent.putExtra("sJson", item.sJson);
            startActivity(intent);
        }
    };
    */

    // 배너 요청
    private void requestGetServiceBannerList() {
        mProgressDialog	 = ProgressDialog.show(mInstance.getActivity(),"",getString(R.string.wait_a_moment));
        String url = mPref.getRumpersServerUrl() + "/getservicebannerlist.asp";
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseGetServiceBannerList(response);
                mBannerViewPager.setAdapter(new PagerAdapterClass(mInstance.getActivity()));
                requestGetPopularityChart();
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
                if ( mPref.isLogging() ) { Log.d(tag, "getParams()" + params.toString()); }
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    // 배너 파싱
    private void parseGetServiceBannerList(String response) {
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
                    if (xpp.getName().equalsIgnoreCase("vbId")) {
                        sb.append("{\"vbId\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("assetId")) {
                        sb.append(",\"assetId\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("android_imgurl")) {
                        sb.append(",\"android_imgurl\":\"").append(xpp.nextText()).append("\"}");
                        String imsi = sb.toString();
                        String re   = imsi.replace("http://58.141.255.80", "http://192.168.44.10"); // 삼성동 C&M에서는 공인망 럼퍼스 접속이 안되서, 임시로 리플레이스 처리 함.
                        JSONObject jo = new JSONObject(re); //JSONObject jo = new JSONObject(sb.toString());
                        mBanners.add(jo);
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * PagerAdapter
     */

    private class PagerAdapterClass extends PagerAdapter {

        private LayoutInflater mInflater;

        public PagerAdapterClass(Context c){
            super();
            mInflater = LayoutInflater.from(c);
        }

        @Override
        public int getCount() {
            return mBanners.size();
        }

        @Override
        public Object instantiateItem(View pager, int position) {
            View v = null;

            v = mInflater.inflate(R.layout.viewpager_void_main_banner, null);
            try {
                JSONObject       jo   = mBanners.get(position);
                //final String           num  = jo.getString("num");
                //String           iurl = mPref.getServerUrl() + jo.get("bImg2");
                String imageUrl = jo.getString("android_imgurl");
                NetworkImageView niv  = (NetworkImageView)v.findViewById(R.id.vod_main_banner_network_imageview);
                niv.setImageUrl(imageUrl, mImageLoader);
                niv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Intent intent = new Intent(mInstance, EventMainActivity.class);
//                        intent.putExtra("num", num);
//                        v = TopMainActivityGroup.topMainActivityGroup.getLocalActivityManager().startActivity("EventMainActivity", intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)).getDecorView();
//                        TopMainActivityGroup.topMainActivityGroup.replaceView(v);

                        //MainActivity.mainActivity.changeTabAndChangeActivity("event", "EventSubActivity", EventSubActivity.class);
                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }

            ImageView indi1 = (ImageView)v.findViewById(R.id.vod_main_banner_indicator_imageview1);
            ImageView indi2 = (ImageView)v.findViewById(R.id.vod_main_banner_indicator_imageview2);
            ImageView indi3 = (ImageView)v.findViewById(R.id.vod_main_banner_indicator_imageview3);
            ImageView indi4 = (ImageView)v.findViewById(R.id.vod_main_banner_indicator_imageview4);
            ImageView indi5 = (ImageView)v.findViewById(R.id.vod_main_banner_indicator_imageview5);
            ImageView indi6 = (ImageView)v.findViewById(R.id.vod_main_banner_indicator_imageview6);
            ImageView indi7 = (ImageView)v.findViewById(R.id.vod_main_banner_indicator_imageview7);
            ImageView indi8 = (ImageView)v.findViewById(R.id.vod_main_banner_indicator_imageview8);
            ImageView indi9 = (ImageView)v.findViewById(R.id.vod_main_banner_indicator_imageview9);
            ImageView indi10 = (ImageView)v.findViewById(R.id.vod_main_banner_indicator_imageview10);
            UiUtil.setIndicatorImage(position, getCount(), indi1, indi2, indi3, indi4, indi5, indi6, indi7, indi8, indi9, indi10);

            ((ViewPager)pager).addView(v);

            return v;
        }

        @Override
        public void destroyItem(View pager, int position, Object view) {
            ((ViewPager)pager).removeView((View)view);
        }

        @Override public boolean isViewFromObject(View pager, Object obj) {
            return pager == obj;
        }
        @Override public void    restoreState(Parcelable arg0, ClassLoader arg1) {}
        @Override public         Parcelable saveState() { return null; }
        @Override public void    startUpdate(View arg0) {}
        @Override public void    finishUpdate(View arg0) {}
    }



    private void loadVpnData_getPopularityChart() {

        String response = getResources().getString(R.string.get_populaity_chart);

        try {
            JSONObject jo  = new JSONObject(response);
            JSONObject weeklyChart = jo.getJSONObject("weeklyChart");
            JSONArray  popularityList = weeklyChart.getJSONArray("popularityList");
            for ( int i = 0; i < popularityList.length(); i++ ) {
                JSONObject popularity = (JSONObject)popularityList.get(i);
                // {\"assetId\":\"www.hchoice.co.kr|M4154270LSG347422301\",\"categoryId\":713230,\"comparision\":\"0\",\"hitCount\":838,\"hot\":false,\"isNew\":true,\"new\":true,\"ranking\":1,\"title\":\"(HD)극장동시-베테랑\"}
                /*
                String assetId     = popularity.getString("popularity");
                String categoryId  = popularity.getString("categoryId");
                String comparision = popularity.getString("comparision");
                String hitCount    = popularity.getString("hitCount");
                String hot         = popularity.getString("hot");
                String isNew       = popularity.getString("isNew");
                String isNewOri    = popularity.getString("new");
                String ranking     = popularity.getString("ranking");
                String title       = popularity.getString("title");
                */
                //ListViewDataObject obj = new ListViewDataObject(mAdapter.getCount(), 0, popularity.toString());
                //mAdapter.addItem(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * http://192.168.40.5:8080/HApplicationServer/getPopularityChart.xml?version=1&terminalKey=9CED3A20FB6A4D7FF35D1AC965F988D2&categoryId=713230&requestItems=weekly
     *
     * */
    // 인기 TOP 20
    private void requestGetPopularityChart() {
        String url = mPref.getWebhasServerUrl() + "/getPopularityChart.xml?version=1&terminalKey="+mPref.getWebhasTerminalKey()+"&categoryId=713230&requestItems=weekly";
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseGetPopularityChart(response);
                mPop20PagerAdapter.notifyDataSetChanged();
                requestGetContentGroupList(); // 금주의 신작영화 요청.
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

    /**
     *
     * @param response
     */
    private void parseGetPopularityChart(String response) {
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
                    if (xpp.getName().equalsIgnoreCase("assetId")) {
                        sb.append("{\"assetId\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("categoryId")) {
                        sb.append(",\"categoryId\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("comparision")) {
                        sb.append(",\"comparision\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("hitCount")) {
                        sb.append(",\"hitCount\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("hot")) {
                        sb.append(",\"hot\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("isNew")) {
                        sb.append(",\"isNew\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("new")) {
                        sb.append(",\"new\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("ranking")) {
                        sb.append(",\"ranking\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("title")) {
                        sb.append(",\"title\":\"").append(xpp.nextText()).append("\"}");
                        JSONObject content = new JSONObject(sb.toString());
                        mPop20PagerAdapter.addVod(content);
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // http://192.168.40.5:8080/HApplicationServer/getContentGroupList.json?version=1&terminalKey=C5E6DBF75F13A2C1D5B2EFDB2BC940&contentGroupProfile=2&categoryId=723049
    // 금주의 신작영화
    private void requestGetContentGroupList() {
        String url = mPref.getWebhasServerUrl() + "/getContentGroupList.json?version=1&terminalKey="+mPref.getWebhasTerminalKey()+"&contentGroupProfile=2&&categoryId=723049";
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject first            = new JSONObject(response);
                    JSONArray  contentGroupList = first.getJSONArray("contentGroupList");
                    for ( int i = 0; i < contentGroupList.length(); i++ ) {
                        JSONObject jo = (JSONObject)contentGroupList.get(i);
                        mNewMoviePagerAdapter.addVod(jo);
                    }
                    mNewMoviePagerAdapter.notifyDataSetChanged();
                } catch ( JSONException e ) {
                    e.printStackTrace();
                }
                requestGetContentGroupList2(); // 이달의 추천 VOD
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
                if ( mPref.isLogging() ) { Log.d(tag, "getParams()" + params.toString()); }
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    // 이달의 추천 VOD
    private void requestGetContentGroupList2() {
        String url = mPref.getWebhasServerUrl() + "/getContentGroupList.json?version=1&terminalKey="+mPref.getWebhasTerminalKey()+"&contentGroupProfile=2&&categoryId=713229";
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                try {
                    JSONObject first            = new JSONObject(response);
                    JSONArray  contentGroupList = first.getJSONArray("contentGroupList");
                    for ( int i = 0; i < contentGroupList.length(); i++ ) {
                        JSONObject jo = (JSONObject)contentGroupList.get(i);
                        mThisMonthPagerAdapter.addVod(jo);
                    }
                    mThisMonthPagerAdapter.notifyDataSetChanged();
                } catch ( JSONException e ) {
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
                if ( mPref.isLogging() ) { Log.d(tag, "getParams()" + params.toString()); }
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    private void requestGetChannelList() {
        mProgressDialog	 = ProgressDialog.show(mInstance.getActivity(),"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestGetChannelList()"); }
        String url = mPref.getWebhasServerUrl() + "/getChannelList.xml?version=1&areaCode=0";
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(tag, response);
                //parseGetChannelList(response);
                //mAdapter.notifyDataSetChanged();
                mProgressDialog.dismiss();
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