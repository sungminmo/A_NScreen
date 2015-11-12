package com.stvn.nscreen.vod;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.Constants;
import com.jjiya.android.common.EightVodPosterPagerAdapter;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.common.ListViewDataObject;
import com.jjiya.android.common.UiUtil;
import com.jjiya.android.common.VodNewMoviePosterPagerAdapter;
import com.jjiya.android.http.BitmapLruCache;
import com.jjiya.android.http.JYStringRequest;
import com.stvn.nscreen.R;
import com.stvn.nscreen.bean.SubCategoryObject;
import com.stvn.nscreen.epg.EpgMainListViewAdapter;
import com.stvn.nscreen.epg.EpgSubActivity;

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A simple {@link Fragment} subclass.
 */

public class VodMainOtherTabFragment extends VodMainBaseFragment implements View.OnClickListener {

    private static final String                  tag = VodMainOtherTabFragment.class.getSimpleName();
    private static       VodMainOtherTabFragment mInstance;
    private              JYSharedPreferences     mPref;

    // network
    private              String                 mCategoryId;
    private              RequestQueue           mRequestQueue;
    private              ProgressDialog         mProgressDialog;
    private              ImageLoader            mImageLoader;
    private              SubCategoryObject      mCurrCategoryObject;

    // gui
    private              GridView               mGridView;
    private              VodMainGridViewAdapter mAdapter;
    private              TextView               mCategoryNameTextView;

    private              LinearLayout           mTabbar;
    private              LinearLayout           mTab1;  // 실시간 인기순위. requestItems=daily
    private              LinearLayout           mTab2;  // 주간 인기순위. requestItems=weekly
    private              String                 mRequestItems; // default = daily

    private              FrameLayout            mCategoryBgFramelayout;
    private              ImageButton            mCategoryButton;

    private              VodMainOtherListViewAdapter mCategoryAdapter;
    private              ListView                    mCategoryListView;

    //
    private              Map<String, String> mCateDepth1;
    private              Map<String, String> mCateDepth2;
    private              Map<String, String> mCateDepth3;
    private              Map<String, String> mCateDepth4;



    public VodMainOtherTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vod_main_order_tab, container, false);

        setMyContext(this.getActivity());

        Bundle param  = getArguments();
        String tabId  = param.getString("tabId");
        int    iTabId = Integer.valueOf(tabId);
        mCategoryId   = param.getString("categoryId");
        categorys     = new ArrayList<JSONObject>();
        mCurrCategoryObject = new SubCategoryObject();

        mInstance     = this;
        mPref         = new JYSharedPreferences(this.getActivity());
        mRequestQueue = Volley.newRequestQueue(this.getActivity());
        ImageLoader.ImageCache imageCache = new BitmapLruCache();
        mImageLoader  = new ImageLoader(mRequestQueue, imageCache);



        // 먼저 공통 뷰 초기화 부터 해준다. (Left버튼, Right버튼, GNB)
        view = initializeBaseView(view, iTabId);

        // 공통 뷰 초기화가 끝났으면, 이놈을 위한 초기화를 한다.
        view = initializeView(view);

        return view;
    }

    private View initializeView(View view) {

        mCategoryNameTextView = (TextView) view.findViewById(R.id.vod_main_orther_category_choice_textview);

        mTabbar = (LinearLayout)view.findViewById(R.id.vod_main_other_tabbar_linearlayout);
        mTab1 = (LinearLayout)view.findViewById(R.id.vod_main_other_tab1_linearlayout);
        mTab2 = (LinearLayout)view.findViewById(R.id.vod_main_other_tab2_linearlayout);
        mTab1.setSelected(true);
        mTab2.setSelected(false);
        mTab1.setOnClickListener(this);
        mTab2.setOnClickListener(this);
        mRequestItems = "daily";


        mAdapter  = new VodMainGridViewAdapter(mInstance.getActivity(), null);
        mGridView = (GridView)view.findViewById(R.id.vod_main_gridview);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(assetItemClickListener);

        mCategoryBgFramelayout = (FrameLayout)view.findViewById(R.id.vod_main_other_category_bg_framelayout);
        mCategoryBgFramelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCategoryBgFramelayout.setVisibility(View.GONE);
            }
        });
        mCategoryButton        = (ImageButton)view.findViewById(R.id.vod_main_orther_category_choice_imageButton);
        mCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCategoryBgFramelayout.setVisibility(View.VISIBLE);
            }
        });

        mCategoryAdapter      = new VodMainOtherListViewAdapter(mInstance.getActivity(), null);

        mCategoryListView     = (ListView)view.findViewById(R.id.vod_main_other_category_listview);
        mCategoryListView.setAdapter(mCategoryAdapter);
        mCategoryListView.setOnItemClickListener(mItemClickListener);


        mCateDepth1 = new  HashMap<String, String>();
        mCateDepth2 = new  HashMap<String, String>();
        mCateDepth3 = new  HashMap<String, String>();
        mCateDepth4 = new  HashMap<String, String>();

        // 카테고리 요청. 추천.
        requestGetCategoryTree();

        return view;
    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(tag, "mItemClickListener() " + position);
            ListViewDataObject dobj = (ListViewDataObject) mCategoryAdapter.getItem(position);

            try {
                mCategoryBgFramelayout.setVisibility(View.GONE);
                JSONObject jo          = new JSONObject(dobj.sJson);
                String     categoryId        = jo.getString("categoryId");
                String     adultCategory     = jo.getString("adultCategory");
                String     categoryName      = jo.getString("categoryName");
                String     leaf              = jo.getString("leaf");
                String     parentCategoryId  = jo.getString("parentCategoryId");
                String     viewerType        = jo.getString("viewerType");

                mCategoryId            = categoryId;

                mAdapter.clear();
                mAdapter.notifyDataSetChanged();

                mCategoryNameTextView.setText(categoryName);
                mCurrCategoryObject.setsCategoryId(categoryId);
                mCurrCategoryObject.setsAdultCategory(adultCategory);
                mCurrCategoryObject.setsCategoryName(categoryName);
                mCurrCategoryObject.setsLeaf(leaf);
                mCurrCategoryObject.setsParentCategoryId(parentCategoryId);
                mCurrCategoryObject.setsViewerType(viewerType);
                processRequest();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch ( v.getId() ) {
            case R.id.vod_main_other_tab1_linearlayout: { // 실시간 인기순위.
                mTab1.setSelected(true);
                mTab2.setSelected(false);
                mAdapter.clear();
                mRequestItems = "daily";
                requestGetPopularityChart();
            } break;
            case R.id.vod_main_other_tab2_linearlayout: { // 주간 인기순위.
                mTab1.setSelected(false);
                mTab2.setSelected(true);
                mAdapter.clear();
                mRequestItems = "weekly";
                requestGetPopularityChart();
            } break;
        }
    }

    private AdapterView.OnItemClickListener assetItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListViewDataObject item = (ListViewDataObject)mAdapter.getItem(position);
            String assetId = "";
            try {
                JSONObject jo = new JSONObject(item.sJson);
                assetId = jo.getString("assetId");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(mInstance.getActivity(), com.stvn.nscreen.vod.VodDetailActivity.class);
            intent.putExtra("assetId", assetId);
            startActivity(intent);
        }
    };


    // 카테고리 요청.
    // 추천
    // http://192.168.40.5:8080/HApplicationServer/getCategoryTree.xml?version=1&categoryProfile=4&categoryId=713228&depth=3&traverseType=DFS
    private void requestGetCategoryTree() {
        mProgressDialog	 = ProgressDialog.show(mInstance.getActivity(), "", getString(R.string.wait_a_moment));
        //String url = mPref.getWebhasServerUrl() + "/getCategoryTree.json?version=1&terminalKey="+mPref.getWebhasTerminalKey()+"&categoryProfile=4&categoryId=713228&depth=3&traverseType=DFS";
        String url = mPref.getWebhasServerUrl() + "/getCategoryTree.json?version=1&terminalKey="+mPref.getWebhasTerminalKey()+"&categoryProfile=4&categoryId="+mCategoryId+"&depth=4&traverseType=DFS";
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                boolean isNeedReCallGetCategoryTree = false;
                try {
                    JSONObject first        = new JSONObject(response);
                    int        resultCode   = first.getInt("resultCode");
                    String     errorString  = first.getString("errorString");
                    boolean    isGotitCateName = false;

                    JSONArray  categoryList = first.getJSONArray("categoryList");
                    int iLoop1Depth = 0; //
                    for ( int i = 0; i < categoryList.length(); i++ ) {
                        JSONObject category     = (JSONObject)categoryList.get(i);
                        //CATEGORY_ID_MOVIE
                        //private String sCategoryId;
                        //private String sAdultCategory;
                        //private String sCategoryName;
                        //private String sLeaf;
                        //private String sParentCategoryId;
                        //private String sViewerType;
                        String     categoryId       = category.getString("categoryId");
                        boolean    adultCategory    = category.getBoolean("adultCategory");
                        String     categoryName     = category.getString("categoryName");
                        boolean    leaf             = category.getBoolean("leaf");
                        String     parentCategoryId = category.getString("parentCategoryId");
                        int        viewerType       = category.getInt("viewerType");

                        Log.d("category", i + ": categoryId: " + categoryId + ", categoryName: " + categoryName + ", leaf: " + leaf + ", parentCategoryId: " + parentCategoryId + ", viewerType: " + viewerType);



//                        if ( ! mCategoryId.equals(categoryId) && viewerType != 60 ) {
                        if ( true ) {
                            /**
                             ViewerType = 30, getContentGroupList (보통 리스트)
                             ViewerType = 200, getPopularityChart (인기순위)
                             ViewerType = 41, ㅎetBundleProductList (묶음)
                             ViewerType = 310, recommendContentGroupByAssetId (연관)
                             */
                            //if ( viewerType == 30 || viewerType == 200 || viewerType == 41 ) {
                            //    categorys.add(category);
                            //}
                            //if ( mCategoryId.equals(parentCategoryId) ) {
                            //    categorys.add(iLoop1Depth, category);
                            //    iLoop1Depth++;
                            //} else {
                                categorys.add(category);
                            //}

                            if ( isGotitCateName == false ) {
                                isGotitCateName = true;
                                mCategoryNameTextView.setText(categoryName);
                                mCurrCategoryObject.setsCategoryId(categoryId);
                                mCurrCategoryObject.setsAdultCategory(category.getString("adultCategory"));
                                mCurrCategoryObject.setsCategoryName(categoryName);
                                mCurrCategoryObject.setsLeaf(category.getString("leaf"));
                                mCurrCategoryObject.setsParentCategoryId(parentCategoryId);
                                mCurrCategoryObject.setsViewerType(category.getString("viewerType"));
                                processRequest();
                            }
                        }
                    }
                } catch ( JSONException e ) {
                    e.printStackTrace();
                }
                list2treee();
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

    private void list2treee(){
        //
        //CATEGORY_ID_MOVIE
        //private String sCategoryId;
        //private String sAdultCategory;
        //private String sCategoryName;
        //private String sLeaf;
        //private String sParentCategoryId;
        //private String sViewerType;
        try {
            for ( int i = 0; i < categorys.size(); i++ ) {
                JSONObject category         = (JSONObject)categorys.get(i);
                String     categoryId       = category.getString("categoryId");
                boolean    adultCategory    = category.getBoolean("adultCategory");
                String     categoryName     = category.getString("categoryName");
                boolean    leaf             = category.getBoolean("leaf");
                String     parentCategoryId = category.getString("parentCategoryId");
                int        viewerType       = category.getInt("viewerType");
                Log.d("category", i + ": categoryId: " + categoryId + ", categoryName: " + categoryName + ", leaf: " + leaf + ", parentCategoryId: " + parentCategoryId + ", viewerType: " + viewerType);
                if ( mCategoryId.equals(parentCategoryId) ) {
                    mCateDepth1.put(categoryId, parentCategoryId);
                }
            }
            //
            for ( int i = 0; i < categorys.size(); i++ ) {
                JSONObject category         = (JSONObject)categorys.get(i);
                String     categoryId       = category.getString("categoryId");
                boolean    adultCategory    = category.getBoolean("adultCategory");
                String     categoryName     = category.getString("categoryName");
                boolean    leaf             = category.getBoolean("leaf");
                String     parentCategoryId = category.getString("parentCategoryId");
                int        viewerType       = category.getInt("viewerType");
                Log.d("category", i + ": categoryId: " + categoryId + ", categoryName: " + categoryName + ", leaf: " + leaf + ", parentCategoryId: " + parentCategoryId + ", viewerType: " + viewerType);
                String aaa = mCateDepth1.get(parentCategoryId);
                if ( aaa != null ) {
                    mCateDepth2.put(categoryId, parentCategoryId);
                }
            }
            Log.d("category","aaa");

            int i = 0;
            TreeMap<String,String> tm = new TreeMap<String,String>(mCateDepth1);
            Iterator<String> iteratorKey = tm.keySet( ).iterator( );   //키값 오름차순 정렬(기본)
            while ( iteratorKey.hasNext() ) {
                String key = iteratorKey.next();
                JSONObject category = getCategoryWithCategoryId(key);
                ListViewDataObject obj = new ListViewDataObject(i, i, category.toString());
                mCategoryAdapter.addItem(obj);
                i++;
            }

//            int i = 0;
//            for( String key : mCateDepth1.keySet() ){
//                JSONObject category = getCategoryWithCategoryId(key);
//                ListViewDataObject obj = new ListViewDataObject(i, i, category.toString());
//                mCategoryAdapter.addItem(obj);
//                i++;
//            }
            mCategoryAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject getCategoryWithCategoryId(String cid) {
        try {
            for ( int i = 0; i < categorys.size(); i++ ) {
                JSONObject category         = (JSONObject)categorys.get(i);
                String     categoryId       = category.getString("categoryId");
                if ( cid.equals(categoryId) ) {
                    return category;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void processRequest() {
        /**
         ViewerType = 30, getContentGroupList (보통 리스트)
         ViewerType = 200, getPopularityChart (인기순위)
         ViewerType = 41, ㅎetBundleProductList (묶음)
         ViewerType = 310, recommendContentGroupByAssetId (연관)
         */
        if ( "30".equals(mCurrCategoryObject.getsViewerType()) ) {

        } else if ( "200".equals(mCurrCategoryObject.getsViewerType()) ) {
            requestGetPopularityChart();
        } else if ( "41".equals(mCurrCategoryObject.getsViewerType()) ) {

        } else if ( "310".equals(mCurrCategoryObject.getsViewerType()) ) {

        }
    }



    /**
     * http://192.168.40.5:8080/HApplicationServer/getPopularityChart.xml?version=1&terminalKey=9CED3A20FB6A4D7FF35D1AC965F988D2&categoryId=713230&requestItems=weekly
     *
     * */
    // 인기 TOP 20
    private void requestGetPopularityChart() {
        //String url = mPref.getWebhasServerUrl() + "/getPopularityChart.xml?version=1&terminalKey="+mPref.getWebhasTerminalKey()+"&categoryId=713230&requestItems=weekly";
        String url = mPref.getWebhasServerUrl() + "/getPopularityChart.json?version=1&terminalKey="+mPref.getWebhasTerminalKey()+"&categoryId="+mCurrCategoryObject.getsCategoryId()+"&requestItems="+mRequestItems;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //parseGetPopularityChart(response);
                try {
                    JSONObject jo = new JSONObject(response);
                    String resultCode = jo.getString("resultCode");
                    if ( Constants.CODE_WEBHAS_OK.equals(resultCode) ) {
                        if ( "daily".equals(mRequestItems) ) {
                            JSONObject dailyChart = jo.getJSONObject("dailyChart");
                            JSONArray popularityList = dailyChart.getJSONArray("popularityList");
                            for ( int i = 0; i < popularityList.length(); i++ ) {
                                JSONObject popularity = popularityList.getJSONObject(i);
                                ListViewDataObject obj = new ListViewDataObject(i, i, popularity.toString());
                                mAdapter.addItem(obj);
                            }
                        } else if ( "weekly".equals(mRequestItems) ) {
                            JSONObject weeklyChart = jo.getJSONObject("weeklyChart");
                            JSONArray popularityList = weeklyChart.getJSONArray("popularityList");
                            for ( int i = 0; i < popularityList.length(); i++ ) {
                                JSONObject popularity = popularityList.getJSONObject(i);
                                ListViewDataObject obj = new ListViewDataObject(i, i, popularity.toString());
                                mAdapter.addItem(obj);
                            }
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
                mAdapter.notifyDataSetChanged();
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
//                        mPop20PagerAdapter.addVod(content);
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
//                        mNewMoviePagerAdapter.addVod(jo);
                    }
//                    mNewMoviePagerAdapter.notifyDataSetChanged();
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
//                        mThisMonthPagerAdapter.addVod(jo);
                    }
//                    mThisMonthPagerAdapter.notifyDataSetChanged();
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
