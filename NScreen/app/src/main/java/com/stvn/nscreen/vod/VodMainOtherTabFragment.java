package com.stvn.nscreen.vod;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.stvn.nscreen.util.CMAlertUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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

    private              BroadcastReceiver      mBroadcastReceiver;
    private              boolean                isNeedReloadData; // 성인인증.

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
////    private              Map<String, String> mCateDepth1;
////    private              Map<String, String> mCateDepth2;
////    private              Map<String, String> mCateDepth3;
//    private              Map<String, String> mCateDepth4;

    //
    private              ArrayList<JSONObject> mCateDepths1;
    private              ArrayList<JSONObject> mCateDepths2;
    private              ArrayList<JSONObject> mCateDepths3;



    public VodMainOtherTabFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                isNeedReloadData = true;
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("I_AM_ADULT");
        getActivity().registerReceiver(mBroadcastReceiver, intentFilter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if ( mBroadcastReceiver != null ) {
            getActivity().unregisterReceiver(mBroadcastReceiver);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ( isNeedReloadData == true ) {
            isNeedReloadData = false;
            if ( getiMyTabNumber() == 1 ) {
                textView2.performClick();
            } else if ( getiMyTabNumber() == 2 ) {
                textView3.performClick();
            } else if ( getiMyTabNumber() == 3 ) {
                textView4.performClick();
            } else if ( getiMyTabNumber() == 4 ) {
                if (mPref.isAdultVerification() == false) {
                    mTab1TextView.performClick();
                } else {
                    textView5.performClick();
                }
            }
        }
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
        if ( mPref == null ) {
            mPref = new JYSharedPreferences(this.getActivity());
        }
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


        mAdapter  = new VodMainGridViewAdapter(this, mInstance.getActivity(), null);
        mGridView = (GridView)view.findViewById(R.id.vod_main_gridview);
        mGridView.setAdapter(mAdapter);
        //mGridView.setOnItemClickListener(assetItemClickListener);

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
                int count = mCategoryAdapter.getCount();
                if ( count == 0 ) {
                    categorys.clear();
                    requestGetCategoryTree(true);
                } else {
                    mCategoryBgFramelayout.setVisibility(View.VISIBLE);
                }
            }
        });

        mCategoryAdapter      = new VodMainOtherListViewAdapter(mInstance.getActivity(), null);

        mCategoryListView     = (ListView)view.findViewById(R.id.vod_main_other_category_listview);
        mCategoryListView.setAdapter(mCategoryAdapter);
        mCategoryListView.setOnItemClickListener(mCategoryItemClickListener);


        mCateDepths1 = new ArrayList<JSONObject>();
        mCateDepths2 = new ArrayList<JSONObject>();
        mCateDepths3 = new ArrayList<JSONObject>();

        // 카테고리 요청. 추천.
        requestGetCategoryTree(false);

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

//    private AdapterView.OnItemClickListener assetItemClickListener = new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            ListViewDataObject item = (ListViewDataObject)mAdapter.getItem(position);
//            String assetId = "";
//            try {
//                JSONObject jo = new JSONObject(item.sJson);
//                assetId = jo.getString("assetId");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            Intent intent = new Intent(mInstance.getActivity(), com.stvn.nscreen.vod.VodDetailActivity.class);
//            intent.putExtra("assetId", assetId);
//            startActivity(intent);
//        }
//    };


    // 카테고리 요청.
    // 추천
    // http://192.168.40.5:8080/HApplicationServer/getCategoryTree.xml?version=1&categoryProfile=4&categoryId=713228&depth=3&traverseType=DFS
    private void requestGetCategoryTree(final boolean showCategoryView) {
        final String thisTurnCategoriId;
        if ( showCategoryView == false ) {
            thisTurnCategoriId = mCategoryId;
        } else {
            thisTurnCategoriId = mPref.getValue(Constants.CATEGORY_ID_TAB2, "");
            mCategoryId = thisTurnCategoriId;
        }
        mProgressDialog	 = ProgressDialog.show(mInstance.getActivity(), "", getString(R.string.wait_a_moment));
        String url = mPref.getWebhasServerUrl() + "/getCategoryTree.json?version=1&terminalKey="+mPref.getWebhasTerminalKey()
                +"&categoryProfile=4&categoryId="
                //+mCategoryId+"&depth=4&traverseType=DFS";
                +thisTurnCategoriId+"&depth=4&traverseType=DFS";
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
                        JSONObject category = (JSONObject)categoryList.get(i);
                        category.put("isOpened", false);
                        // viewerType : 20 이면 에셋인데, 트리에 나온다.
                        categorys.add(category);
                    }
                    if ( categoryList.length() > 1 ) {
                        categorys.remove(0);
                    }
                    JSONObject category = (JSONObject)categorys.get(0);
                    if ( showCategoryView == false ) {
                        mCategoryNameTextView.setText(category.getString("categoryName"));
                        mCurrCategoryObject.setsCategoryId(category.getString("categoryId"));
                        mCurrCategoryObject.setsAdultCategory(category.getString("adultCategory"));
                        mCurrCategoryObject.setsCategoryName(category.getString("categoryName"));
                        mCurrCategoryObject.setsLeaf(category.getString("leaf"));
                        mCurrCategoryObject.setsParentCategoryId(category.getString("parentCategoryId"));
                        mCurrCategoryObject.setsViewerType(category.getString("viewerType"));
                    }
                    processRequest();
                    for ( int i = 0; i < categorys.size(); i++ ) {
                        JSONObject loopCategory     = (JSONObject)categorys.get(i);
                        String     categoryId       = loopCategory.getString("categoryId");
                        String     parentCategoryId = loopCategory.getString("parentCategoryId");
                        boolean    leaf             = loopCategory.getBoolean("leaf");
                        //if ( mCategoryId.equals(parentCategoryId) ) {
                        if ( thisTurnCategoriId.equals(parentCategoryId) ) {
                            loopCategory.put("isOpened", false);
                            mCateDepths1.add(loopCategory);
                            ListViewDataObject obj = new ListViewDataObject(0, 1, loopCategory.toString());
                            mCategoryAdapter.addItem(obj);
                        }
                    }
                    //list2treee();
                    list2tree();
                    if ( showCategoryView == true ) {
                        mCategoryBgFramelayout.setVisibility(View.VISIBLE);
                    }
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

    private AdapterView.OnItemClickListener mCategoryItemClickListener = new AdapterView.OnItemClickListener() {
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

                if ( leaf == true || ( leaf == false && "30".equals(viewerType)) ) { // 하부카테고리가 없으므로 닫을 것.
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
                    boolean isOpened       = jo.getBoolean("isOpened");
                    String thisCategoryId  = jo.getString("categoryId");
                    JSONObject newJo       = new JSONObject(jo.toString());
                    if ( isOpened == true ) {
                        newJo.put("isOpened", false);
                        ListViewDataObject obj = new ListViewDataObject(0, dobj.iKey, newJo.toString());
                        mCategoryAdapter.set(position, obj);
                        if ( dobj.iKey == 1 ) {
                            // close tree. remove 2 depth
                            int iFindedPosi = getPositionWithCategoryId(categoryId);
                            while ( iFindedPosi != -1 ) {
                                mCategoryAdapter.remove(iFindedPosi);
                                iFindedPosi = getPositionWithCategoryId(categoryId);
                            }
                        } else if ( dobj.iKey == 2 ) {
                            // close tree. remove 3 depth
                            int iFindedPosi = getPositionWithCategoryId(categoryId);
                            while ( iFindedPosi != -1 ) {
                                mCategoryAdapter.remove(iFindedPosi);
                                iFindedPosi = getPositionWithCategoryId(categoryId);
                            }
                        }
                    } else {
                        newJo.put("isOpened", true);
                        ListViewDataObject obj = new ListViewDataObject(0, dobj.iKey, newJo.toString());
                        mCategoryAdapter.set(position, obj);
                        if ( dobj.iKey == 1 ) {
                            // append 2 depth
                            int iLoop = position+1;
                            for ( int i = 0; i < mCateDepths2.size(); i++ ) {
                                JSONObject loopJo = mCateDepths2.get(i);
                                String loopParentCategoryId = loopJo.getString("parentCategoryId");
                                if ( thisCategoryId.equals(loopParentCategoryId) ) {
                                    ListViewDataObject new2Depth = new ListViewDataObject(0, 2, loopJo.toString());
                                    mCategoryAdapter.addItem(iLoop, new2Depth);
                                    iLoop++;
                                }
                            }
                        } else if ( dobj.iKey == 2 ) {
                            // append 3 depth
                            int iLoop = position+1;
                            for ( int i = 0; i < mCateDepths3.size(); i++ ) {
                                JSONObject loopJo = mCateDepths3.get(i);
                                String loopParentCategoryId = loopJo.getString("parentCategoryId");
                                if ( thisCategoryId.equals(loopParentCategoryId) ) {
                                    ListViewDataObject new2Depth = new ListViewDataObject(0, 3, loopJo.toString());
                                    mCategoryAdapter.addItem(iLoop, new2Depth);
                                    iLoop++;
                                }
                            }
                        }
                    }
                    //list2tree();
                    mCategoryAdapter.notifyDataSetInvalidated();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private int getPositionWithCategoryId(String categoryId){
        int rtn = -1;
        try {
            for (int i = 0; i < mCategoryAdapter.getCount(); i++ ) {
                ListViewDataObject loopObj = (ListViewDataObject) mCategoryAdapter.getItem(i);
                JSONObject loopJo = new JSONObject(loopObj.sJson);
                String loopParentCategoryId = loopJo.getString("parentCategoryId");
                if (categoryId.equals(loopParentCategoryId)) {
                    return i;
                }
            }
        } catch ( JSONException e ) {
            e.printStackTrace();
        }
        return rtn;
    }

    // 이건 한번만 호출하자.
    private void list2tree(){
        try {
            for ( int i = 0; i < mCateDepths1.size(); i++ ) {
                JSONObject category = mCateDepths1.get(i);
                if ( category.getBoolean("leaf") == false) {
                    String categoryId = category.getString("categoryId");
                    append2Depth(categoryId);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void append2Depth(String categoryId){
        try {
            for ( int i = 0; i < categorys.size(); i++ ) {
                JSONObject category         = (JSONObject)categorys.get(i);
                String     parentCategoryId = category.getString("parentCategoryId");
                if ( categoryId.equals(parentCategoryId) ) {
                    mCateDepths2.add(category);
                    boolean leaf = category.getBoolean("leaf");
                    if ( category.getBoolean("leaf") == false ) {
                        String thisCategoryId = category.getString("categoryId");
                        append3Depth(thisCategoryId);
                    }
                }
            }
        } catch ( JSONException e ) {
            e.printStackTrace();
        }
    }

    private void append3Depth(String categoryId){
        try {
            for ( int i = 0; i < categorys.size(); i++ ) {
                JSONObject category         = (JSONObject)categorys.get(i);
                String     parentCategoryId = category.getString("parentCategoryId");
                if ( categoryId.equals(parentCategoryId) ) {
                    mCateDepths3.add(category);
                }
            }
        } catch ( JSONException e ) {
            e.printStackTrace();
        }
    }

    private JSONObject getCategoryWithCategoryId(String cid) {
        try {
            for ( int i = 0; i < categorys.size(); i++ ) {
                JSONObject category   = (JSONObject)categorys.get(i);
                String     categoryId = category.getString("categoryId");
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

        //Toast.makeText(mInstance.getActivity(), "ViewerType: " + mCurrCategoryObject.getsViewerType(), Toast.LENGTH_LONG).show();

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


    //
    private void requestGetContentGroupList() {
        String url = mPref.getWebhasServerUrl() + "/getContentGroupList.json?version=1&terminalKey="+mPref.getWebhasTerminalKey()+"&contentGroupProfile=2&sortType=notSet"
            + "&categoryId="+mCurrCategoryObject.getsCategoryId();
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

//                        StringBuilder sb   = new StringBuilder();
//                        sb.append("API: action\nresultCode: ").append(resultCode).append("\nerrorString: ").append(errorString);
//                        AlertDialog.Builder alert = new AlertDialog.Builder(mInstance.getActivity());
//                        alert.setPositiveButton("알림", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        });
//                        alert.setMessage(sb.toString());
//                        alert.show();

                        String alertTitle = "안내";
                        String alertMsg1 = "목록이 없습니다.";
                        String alertMsg2 = "";
                        CMAlertUtil.Alert1(mInstance.getActivity(), alertTitle, alertMsg1, alertMsg2, true, false, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }, true);

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


    //
    private void startActivityAssetOrBundle(String assetId, JSONObject product){
        try {
            String purchasedTime = product.getString("purchasedTime");
            if ( purchasedTime.length() > 0 ) {
                String productId = product.getString("productId");
                Intent intent    = new Intent(getActivity(), VodDetailBundleActivity.class);
                intent.putExtra("productType", "Bundle");
                intent.putExtra("productId", productId);
                intent.putExtra("assetId", assetId);
                getActivity().startActivity(intent);
            } else {
                Intent intent = new Intent(this.getActivity(), VodDetailActivity.class);
                intent.putExtra("assetId", assetId);
                getActivity().startActivity(intent);
            }
        } catch ( JSONException e ) {
            e.printStackTrace();
        }
    }

    // VodMainGridViewAdapter에서 포스터를 클릭했을때 타는 메소드.
    // assetInfo를 요청해서, 구매한 VOD인지를 알아낸다.
    // 만약 구매 했다면, VodDetailBundleActivity로 이동.
    // 만약 구매 안했다면, VodDetailActivity로 이동.
    public void onClickBundulPoster(String primaryAssetId) {
        mProgressDialog	 = ProgressDialog.show(mInstance.getActivity(), "", getString(R.string.wait_a_moment));
        String terminalKey = mPref.getWebhasTerminalKey();
        String encAssetId = null;
        try {
            encAssetId  = URLDecoder.decode(primaryAssetId, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = mPref.getWebhasServerUrl() + "/getAssetInfo.json?version=1&terminalKey="+terminalKey+"&assetProfile=9&assetId="+encAssetId;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                boolean needJumpAsset = true;
                try {
                    JSONObject jo          = new JSONObject(response);
                    JSONObject asset       = jo.getJSONObject("asset");
                    JSONArray  productList = asset.getJSONArray("productList");
                    for ( int i = 0; i < productList.length(); i++ ) {
                        JSONObject product   = (JSONObject)productList.get(i);
                        String productType   = product.getString("productType");
                        if ( "Bundle".equals(productType) ) {
                            String assetId   = asset.getString("assetId");
                            startActivityAssetOrBundle(assetId,product);
                            needJumpAsset = false;
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if ( needJumpAsset == true ) {
                    // 예외 처리임. 원래 번들(묶음)이면, 여기까지 오면 안되고 위에서 startActivityAssetOrBundle()로 가야된다.
                    // 묶음상품이였다가, 묶음상품이 아니라고 풀리는 경우가 있다고 해서 아래의 예외 처리 함.
                    try {
                        JSONObject jo    = new JSONObject(response);
                        JSONObject asset = jo.getJSONObject("asset");
                        String assetId   = asset.getString("assetId");
                        Intent intent    = new Intent(mInstance.getActivity(), VodDetailActivity.class);
                        intent.putExtra("assetId", assetId);
                        getActivity().startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
                params.put("version", String.valueOf(1));
                params.put("areaCode", String.valueOf(0));
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
