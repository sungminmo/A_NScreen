package com.stvn.nscreen.vod;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.Constants;
import com.jjiya.android.common.EightVodPosterPagerAdapter;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.common.UiUtil;
import com.jjiya.android.common.VodNewMoviePosterPagerAdapter;
import com.jjiya.android.http.BitmapLruCache;
import com.jjiya.android.http.JYStringRequest;
import com.stvn.nscreen.R;

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

/**
 * A simple {@link Fragment} subclass.
 */

public class VodMainFirstTabFragment extends VodMainBaseFragment {

    private static final String                  tag = VodMainFragment.class.getSimpleName();
    private static       VodMainFirstTabFragment mInstance;
    private              JYSharedPreferences     mPref;

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

    public VodMainFirstTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vod_main_first_tab, container, false);


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


        // 먼저 공통 뷰 초기화 부터 해준다. (Left버튼, Right버튼, GNB)
        view = initializeBaseView(view);

        // 공통 뷰 초기화가 끝났으면, 이놈을 위한 초기화를 한다.
        view = initializeView(view);

        return view;
    }

    private View initializeView(View view) {



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

        // 카테고리 요청. 추천.
        requestGetCategoryTree();


        ((LinearLayout)view.findViewById(R.id.vod_main_pop20_more_linearlayout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(VodMainFragment.this, VodCategoryMainActivity.class);
                //startActivity(i);
            }
        });

        return view;
    }

    // 카테고리 요청.
    // 추천
    // http://192.168.40.5:8080/HApplicationServer/getCategoryTree.xml?version=1&categoryProfile=4&categoryId=713228&depth=3&traverseType=DFS
    private void requestGetCategoryTree() {
        mProgressDialog	 = ProgressDialog.show(mInstance.getActivity(),"",getString(R.string.wait_a_moment));
        String url = mPref.getWebhasServerUrl() + "/getCategoryTree.json?version=1&terminalKey="+mPref.getWebhasTerminalKey()+"&categoryProfile=4&categoryId=713228&depth=3&traverseType=DFS";
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject first       = new JSONObject(response);
                    int        resultCode  = first.getInt("resultCode");
                    String     errorString = first.getString("errorString");

                    categorys              = new ArrayList<JSONObject>();
                    JSONArray  categoryList = first.getJSONArray("categoryList");
                    for ( int i = 0; i < categoryList.length(); i++ ) {
                        JSONObject category     = (JSONObject)categoryList.get(i);
                        int        viewerType   = category.getInt("viewerType");
                        String     categoryId   = category.getString("categoryId");
                        String     categoryName = category.getString("categoryName");

                        /**
                         ViewerType = 30, getContentGroupList (보통 리스트)
                         ViewerType = 200, getPopularityChart (인기순위)
                         ViewerType = 41, ㅎetBundleProductList (묶음)
                         ViewerType = 310, recommendContentGroupByAssetId (연관)
                         */
                        if ( viewerType == 30 || viewerType == 200 || viewerType == 41 ) {
                            categorys.add(category);
                        }
                    }
                } catch ( JSONException e ) {
                    e.printStackTrace();
                }
                requestGetServiceBannerList(); // 배너 요청
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                if (error instanceof TimeoutError) {
                    Toast.makeText(mInstance.getActivity(), mInstance.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(mInstance.getActivity(), mInstance.getString(R.string.error_network_noconnectionerror), Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(mInstance.getActivity(), mInstance.getString(R.string.error_network_servererror), Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(mInstance.getActivity(), mInstance.getString(R.string.error_network_networkerrorr), Toast.LENGTH_LONG).show();
                }
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


    // 배너 요청
    private void requestGetServiceBannerList() {
        StringBuffer sb = new StringBuffer().append("terminalKey=").append(JYSharedPreferences.RUMPERS_TERMINAL_KEY);
        String url = mPref.getRumpersServerUrl() + "/getservicebannerlist.asp?"+sb.toString();
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
                if (error instanceof TimeoutError) {
                    Toast.makeText(mInstance.getActivity(), mInstance.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(mInstance.getActivity(), mInstance.getString(R.string.error_network_noconnectionerror), Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(mInstance.getActivity(), mInstance.getString(R.string.error_network_servererror), Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(mInstance.getActivity(), mInstance.getString(R.string.error_network_networkerrorr), Toast.LENGTH_LONG).show();
                }
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
    /*
    <BannerList_Item>
<vbId>1</vbId>
<assetId>151006.20</assetId>
<Banner_Version><![CDATA[ffefsfdfdsfasd]]></Banner_Version>
<Banner_Title><![CDATA[ffefsfdfdsfasd]]></Banner_Title>
<android_imgurl><![CDATA[http://58.141.255.80/data/vodbanner//1442457556268654.jpg]]></android_imgurl>
<iphone_imgurl><![CDATA[http://58.141.255.80/data/vodbanner//1442457561612438.png]]></iphone_imgurl>
<sortno>1</sortno>
</BannerList_Item>

     */
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
                        //String imsi = sb.toString();
                        //String re   = imsi.replace("http://58.141.255.80", "http://192.168.44.10"); // 삼성동 C&M에서는 공인망 럼퍼스 접속이 안되서, 임시로 리플레이스 처리 함.
                        JSONObject jo = new JSONObject(sb.toString()); //JSONObject jo = new JSONObject(sb.toString());
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
            JSONArray popularityList = weeklyChart.getJSONArray("popularityList");
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
        //String url = mPref.getWebhasServerUrl() + "/getPopularityChart.xml?version=1&terminalKey="+mPref.getWebhasTerminalKey()+"&categoryId=713230&requestItems=weekly";
        String url = mPref.getWebhasServerUrl() + "/getPopularityChart.json?version=1&terminalKey="+mPref.getWebhasTerminalKey()+"&categoryId=713230&requestItems=weekly";
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //parseGetPopularityChart(response);
                try {
                    JSONObject jo = new JSONObject(response);
                    String resultCode = jo.getString("resultCode");
                    if ( Constants.CODE_WEBHAS_OK.equals(resultCode) ) {
                        JSONObject weeklyChart = jo.getJSONObject("weeklyChart");
                        JSONArray popularityList = weeklyChart.getJSONArray("popularityList");
                        for ( int i = 0; i < popularityList.length(); i++ ) {
                            JSONObject popularity = popularityList.getJSONObject(i);
                            mPop20PagerAdapter.addVod(popularity);
                        }
                    } else {
                        String errorString = jo.getString("errorString");
                        StringBuilder sb   = new StringBuilder();
                        sb.append("API: action\nresultCode: ").append(resultCode).append("\nerrorString: ").append(errorString);
                        AlertDialog.Builder alert = new AlertDialog.Builder(mInstance.getActivity());
                        alert.setPositiveButton("알림", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alert.setMessage(sb.toString());
                        alert.show();
                    }

                } catch ( JSONException e ) {
                    e.printStackTrace();
                }
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
