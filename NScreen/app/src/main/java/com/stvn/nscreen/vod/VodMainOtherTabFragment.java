package com.stvn.nscreen.vod;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
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
import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.Constants;
import com.jjiya.android.common.IOnBackPressedListener;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.common.ListViewDataObject;
import com.jjiya.android.http.BitmapLruCache;
import com.jjiya.android.http.JYStringRequest;
import com.stvn.nscreen.R;
import com.stvn.nscreen.bean.SubCategoryObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A simple {@link Fragment} subclass.
 */

public class VodMainOtherTabFragment extends VodMainBaseFragment implements View.OnClickListener, IOnBackPressedListener {

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

    //
    private              List<JSONObject>       categorys;
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
                int count = mCategoryAdapter.getCount();
                int count2 = categorys.size();
                int a = mCategoryListView.getVisibility();
                //Log.d("category","");
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
                try {
                    JSONObject first        = new JSONObject(response);
                    int        resultCode   = first.getInt("resultCode");
                    String     errorString  = first.getString("errorString");

                    JSONArray  categoryList = first.getJSONArray("categoryList");
                    for ( int i = 0; i < categoryList.length(); i++ ) {
                        JSONObject category     = (JSONObject)categoryList.get(i);
                        category.put("isOpened", false);
                        String     parentCategoryId = category.getString("parentCategoryId");
                        int        viewerType       = category.getInt("viewerType");
                        if ( ! parentCategoryId.equals("0") || ! (viewerType == 0) || ! (viewerType == 60) ) {
                            categorys.add(category);
                        }
                    }
                    if ( categoryList.length() > 1 ) {
                        categorys.remove(0);
                    }
                    JSONObject category     = (JSONObject)categorys.get(0);
                    mCategoryNameTextView.setText(category.getString("categoryName"));
                    mCurrCategoryObject.setsCategoryId(category.getString("categoryId"));
                    mCurrCategoryObject.setsAdultCategory(category.getString("adultCategory"));
                    mCurrCategoryObject.setsCategoryName(category.getString("categoryName"));
                    mCurrCategoryObject.setsLeaf(category.getString("leaf"));
                    mCurrCategoryObject.setsParentCategoryId(category.getString("parentCategoryId"));
                    mCurrCategoryObject.setsViewerType(category.getString("viewerType"));
                    processRequest();
                    list2treee();
                } catch ( JSONException e ) {
                    e.printStackTrace();
                }
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

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(tag, "mItemClickListener() " + position);
            ListViewDataObject dobj = (ListViewDataObject) mCategoryAdapter.getItem(position);

            try {
                JSONObject jo          = new JSONObject(dobj.sJson);
                String     categoryId       = jo.getString("categoryId");
                String     adultCategory    = jo.getString("adultCategory");
                String     categoryName     = jo.getString("categoryName");
                String     sleaf            = jo.getString("leaf");
                boolean    leaf             = jo.getBoolean("leaf");
                String     parentCategoryId = jo.getString("parentCategoryId");
                String     viewerType       = jo.getString("viewerType");

                if ( leaf == true ) { // 하부카테고리가 없으므로 닫을 것.
                    mCategoryBgFramelayout.setVisibility(View.GONE);
                    mCategoryId = categoryId;
                    //mCategoryAdapter.clear();
                    //mCategoryAdapter.notifyDataSetChanged();
                    mCategoryNameTextView.setText(categoryName);
                    mCurrCategoryObject.setsCategoryId(categoryId);
                    mCurrCategoryObject.setsAdultCategory(adultCategory);
                    mCurrCategoryObject.setsCategoryName(categoryName);
                    mCurrCategoryObject.setsLeaf(sleaf);
                    mCurrCategoryObject.setsParentCategoryId(parentCategoryId);
                    mCurrCategoryObject.setsViewerType(viewerType);
                    mAdapter.clear();
                    mAdapter.notifyDataSetChanged();
                    processRequest();
                } else { // 하부카테고리가 있으므로 닫지 말것.
                    ListViewDataObject obj = (ListViewDataObject)mCategoryAdapter.getItem(position);
                    JSONObject oldJo       = new JSONObject(obj.sJson);
                    boolean isOpened       = oldJo.getBoolean("isOpened");
                    String thiscategoryId  = oldJo.getString("categoryId");
                    JSONObject newJo       = getCategoryWithCategoryId(thiscategoryId);
                    int index              = getCategoryIndxWithCategoryId(thiscategoryId);
                    if ( isOpened == true ) {
                        newJo.put("isOpened", false);
                    } else {
                        newJo.put("isOpened", true);
                    }
                    categorys.set(index, newJo);
                    list2treee();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void list2treee(){
        mCateDepth1.clear();
        mCateDepth2.clear();
        mCateDepth3.clear();
        mCateDepth4.clear();
        mCategoryAdapter.clear();
        try {
            for ( int i = 0; i < categorys.size(); i++ ) {
                JSONObject category         = (JSONObject)categorys.get(i);
                String     categoryId       = category.getString("categoryId");
                String     parentCategoryId = category.getString("parentCategoryId");
                if ( mCategoryId.equals(parentCategoryId) ) {
                    mCateDepth1.put(categoryId, parentCategoryId);
                }
            }
            for ( int i = 0; i < categorys.size(); i++ ) {
                JSONObject category         = (JSONObject)categorys.get(i);
                String     categoryId       = category.getString("categoryId");
                String     parentCategoryId = category.getString("parentCategoryId");
                String aaa = mCateDepth1.get(parentCategoryId);
                if ( aaa != null ) {
                    mCateDepth2.put(categoryId, parentCategoryId);
                }
            }
            for ( int i = 0; i < categorys.size(); i++ ) {
                JSONObject category         = (JSONObject)categorys.get(i);
                String     categoryId       = category.getString("categoryId");
                String     parentCategoryId = category.getString("parentCategoryId");
                String aaa = mCateDepth2.get(parentCategoryId);
                if ( aaa != null ) {
                    mCateDepth3.put(categoryId, parentCategoryId);
                }
            }
            int i = 0;
            TreeMap<String,String> tm = new TreeMap<String,String>(mCateDepth1);
            Iterator<String> iteratorKey = tm.keySet( ).iterator( );   //키값 오름차순 정렬(기본)
            while ( iteratorKey.hasNext() ) {
                String key = iteratorKey.next();
                JSONObject category = getCategoryWithCategoryId(key);
                category.put("is1Depth", true);
                ListViewDataObject obj = new ListViewDataObject(i, i, category.toString());
                mCategoryAdapter.addItem(obj);
                i++;
                boolean isOpened = category.getBoolean("isOpened");
                if ( isOpened == true ) {
                    String categoryId = category.getString("categoryId");
                    i = add2Depth(i, categoryId);
                }
            }
            mCategoryAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private int add2Depth(int start, String cid) {
        int loop = start;
        try {
            for ( int i = 0; i < categorys.size(); i++ ) {
                JSONObject category   = (JSONObject)categorys.get(i);
                String     parentCategoryId = category.getString("parentCategoryId");
                if ( cid.equals(parentCategoryId) ) {
                    category.put("is2Depth", true);
                    ListViewDataObject obj = new ListViewDataObject(loop, loop, category.toString());
                    mCategoryAdapter.addItem(obj);
                    loop++;
                    boolean isOpened = category.getBoolean("isOpened");
                    if ( isOpened == true ) {
                        String categoryId = category.getString("categoryId");
                        loop = add3Depth(loop, categoryId);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return loop;
    }

    private int add3Depth(int start, String cid) {
        int loop = start;
        try {
            for ( int i = 0; i < categorys.size(); i++ ) {
                JSONObject category   = (JSONObject)categorys.get(i);
                String     parentCategoryId = category.getString("parentCategoryId");
                if ( cid.equals(parentCategoryId) ) {
                    category.put("is3Depth", true);
                    ListViewDataObject obj = new ListViewDataObject(loop, loop, category.toString());
                    mCategoryAdapter.addItem(obj);
                    loop++;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return loop;
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

    private int getCategoryIndxWithCategoryId(String cid) {
        try {
            for ( int i = 0; i < categorys.size(); i++ ) {
                JSONObject category         = (JSONObject)categorys.get(i);
                String     categoryId       = category.getString("categoryId");
                if ( cid.equals(categoryId) ) {
                    return i;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void processRequest() {
        /**
         ViewerType = 30, 1031,  getContentGroupList (보통 리스트)
         ViewerType = 200, getPopularityChart (인기순위)
         ViewerType = 41, getBundleProductList (묶음)
         ViewerType = 310, recommendContentGroupByAssetId (연관)
         */
        mTabbar.setVisibility(View.GONE);

        Toast.makeText(mInstance.getActivity(), "ViewerType: " + mCurrCategoryObject.getsViewerType(), Toast.LENGTH_LONG).show();

        if ("200".equals(mCurrCategoryObject.getsViewerType())) {
            mTabbar.setVisibility(View.VISIBLE);
            requestGetPopularityChart();
        } else {
            mTabbar.setVisibility(View.GONE);
            requestGetContentGroupList();
//        if ( "0".equals(mCurrCategoryObject.getsViewerType())
//                || "10".equals(mCurrCategoryObject.getsViewerType())
//                || "20".equals(mCurrCategoryObject.getsViewerType())
//                || "30".equals(mCurrCategoryObject.getsViewerType())
//                || "60".equals(mCurrCategoryObject.getsViewerType())
//                || "1031".equals(mCurrCategoryObject.getsViewerType()) ) {
//            requestGetContentGroupList();
//        } else if ( "200".equals(mCurrCategoryObject.getsViewerType()) ) {
//            mTabbar.setVisibility(View.VISIBLE);
//            requestGetPopularityChart();
//        } else if ( "41".equals(mCurrCategoryObject.getsViewerType()) ) {
//            //
//        } else if ( "310".equals(mCurrCategoryObject.getsViewerType()) ) {
//            //
//        } else {
        //
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


    // http://192.168.40.5:8080/HApplicationServer/getContentGroupList.json?version=1&terminalKey=C5E6DBF75F13A2C1D5B2EFDB2BC940&contentGroupProfile=2&categoryId=723049
    private void requestGetContentGroupList() {
        String url = mPref.getWebhasServerUrl() + "/getContentGroupList.json?version=1&terminalKey="+mPref.getWebhasTerminalKey()+"&contentGroupProfile=2&categoryId="+mCurrCategoryObject.getsCategoryId();
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jo = new JSONObject(response);
                    String resultCode = jo.getString("resultCode");
                    if ( Constants.CODE_WEBHAS_OK.equals(resultCode) ) {
                        JSONArray contentGroupList = jo.getJSONArray("contentGroupList");
                        for ( int i = 0; i < contentGroupList.length(); i++ ) {
                            JSONObject contentGroup = contentGroupList.getJSONObject(i);
                            ListViewDataObject obj = new ListViewDataObject(i, i, contentGroup.toString());
                            mAdapter.addItem(obj);
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
                //mProgressDialog.dismiss();
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

    /**
     * IOnBackPressedListener
     */
    @Override
    public void onBackPressedCallback() {
        mTab1TextView.performClick();
    }
}
