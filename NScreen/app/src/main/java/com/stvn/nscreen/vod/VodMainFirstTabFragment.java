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
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.stvn.nscreen.bean.MainCategoryObject;
import com.stvn.nscreen.util.CMAlertUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */

public class VodMainFirstTabFragment extends VodMainBaseFragment {

    private static final String                  tag = VodMainFirstTabFragment.class.getSimpleName();
    private static       VodMainFirstTabFragment mInstance;
    private              JYSharedPreferences     mPref;

    // network
    private              RequestQueue           mRequestQueue;
    private              ProgressDialog         mProgressDialog;
    private              ImageLoader            mImageLoader;

    private              BroadcastReceiver      mBroadcastReceiver;
    private              boolean                isNeedReloadData; // 성인인증.

    // gui
    private              int                    mBannerIndicatorIndex;
    private              ViewPager              mBannerViewPager;
    private              LinearLayout           mBannerViewPagerIndicator;

    private              List<JSONObject>       mBanners;

    private              TextView               mSection1TextView;
    private              TextView               mSection2TextView;
    private              TextView               mSection3TextView;
    private              LinearLayout           mSection1Linearlayout;
    private              LinearLayout           mSection2Linearlayout;
    private              LinearLayout           mSection3Linearlayout;

    private              ViewPager                     mPop20ViewPager;
    private              LinearLayout                  mPop20ViewPagerIndicator;
    private              int                           mPop20IndicatorIndex;
    private              EightVodPosterPagerAdapter    mPop20PagerAdapter; // 인기순위 Top 20

    private              ViewPager                     mNewMovieViewPager;
    private              LinearLayout                  mNewMovieViewPagerIndicator;
    private              int                           mNewMovieIndicatorIndex;
    private              VodNewMoviePosterPagerAdapter mNewMoviePagerAdapter; // 금주의 신작 영화

    private              ViewPager                     mThisMonthViewPager;
    private              LinearLayout                  mThisMonthViewPagerIndicator;
    private              int                           mThisMonthIndicatorIndex;
    private              VodNewMoviePosterPagerAdapter mThisMonthPagerAdapter; // 이달의 추천 VOD

    public VodMainFirstTabFragment() {
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
            mTab1TextView.performClick();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vod_main_first_tab, container, false);

        setMyContext(this.getActivity());

        mInstance     = this;
        if ( mPref == null ) {
            mPref = new JYSharedPreferences(this.getActivity());
        }
        mRequestQueue = Volley.newRequestQueue(this.getActivity());
        ImageLoader.ImageCache imageCache = new BitmapLruCache();
        mImageLoader  = new ImageLoader(mRequestQueue, imageCache);
        isNeedReloadData = false;

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
        view = initializeBaseView(view, 0);

        // 공통 뷰 초기화가 끝났으면, 이놈을 위한 초기화를 한다.
        view = initializeView(view);

        return view;
    }

    private View initializeView(View view) {



        // 배너
        ViewPager.SimpleOnPageChangeListener mBannerPagerListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                UiUtil.changePageIndicator(mBannerViewPagerIndicator, mBannerIndicatorIndex, position);
                mBannerIndicatorIndex = position;
            }
        };
        mBannerIndicatorIndex = 0;
        mBannerViewPager = (ViewPager)view.findViewById(R.id.vod_main_event_viewpager);
        mBannerViewPager.addOnPageChangeListener(mBannerPagerListener);
        mBannerViewPagerIndicator = (LinearLayout)view.findViewById(R.id.vod_main_event_viewpager_indicator);
        // 인가 탑20
        ViewPager.SimpleOnPageChangeListener mPop20PagerListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                UiUtil.changePageIndicator(mPop20ViewPagerIndicator, mPop20IndicatorIndex, position);
                mPop20IndicatorIndex = position;
            }
        };
        mPop20IndicatorIndex = 0;
        mSection1TextView = (TextView)view.findViewById(R.id.vod_main_section1_textview);
        mSection1Linearlayout = (LinearLayout)view.findViewById(R.id.vod_main_pop20_more_linearlayout);
        mPop20ViewPager = (ViewPager)view.findViewById(R.id.vod_main_pop20_viewpager);
        mPop20ViewPager.setAdapter(mPop20PagerAdapter);
        mPop20ViewPager.addOnPageChangeListener(mPop20PagerListener);
        mPop20ViewPagerIndicator = (LinearLayout)view.findViewById(R.id.vod_main_pop20_viewpager_indicator);



        // 신작영화
        ViewPager.SimpleOnPageChangeListener mNewMoviePagerListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                UiUtil.changePageIndicator(mNewMovieViewPagerIndicator, mNewMovieIndicatorIndex, position);
                mNewMovieIndicatorIndex = position;
            }
        };
        mNewMovieIndicatorIndex = 0;
        mSection2TextView = (TextView)view.findViewById(R.id.vod_main_section2_textview);
        mSection2Linearlayout = (LinearLayout)view.findViewById(R.id.vod_main_newmovie_more_linearlayout);
        mNewMovieViewPager = (ViewPager)view.findViewById(R.id.vod_main_newmovie_viewpager);
        mNewMovieViewPager.setAdapter(mNewMoviePagerAdapter);
        mNewMovieViewPager.addOnPageChangeListener(mNewMoviePagerListener);
        mNewMovieViewPagerIndicator = (LinearLayout)view.findViewById(R.id.vod_main_newmovie_viewpager_indicator);

        // 이달의 추천 VOD
        ViewPager.SimpleOnPageChangeListener mThisMonthPagerListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                UiUtil.changePageIndicator(mThisMonthViewPagerIndicator, mThisMonthIndicatorIndex, position);
                mThisMonthIndicatorIndex = position;
            }
        };
        mThisMonthIndicatorIndex = 0;
        mSection3TextView = (TextView)view.findViewById(R.id.vod_main_section3_textview);
        mSection3Linearlayout = (LinearLayout)view.findViewById(R.id.vod_main_thismonth_more_linearlayout);
        mThisMonthViewPager = (ViewPager)view.findViewById(R.id.vod_main_thismonth_viewpager);
        mThisMonthViewPager.setAdapter(mThisMonthPagerAdapter);
        mThisMonthViewPager.addOnPageChangeListener(mThisMonthPagerListener);
        mThisMonthViewPagerIndicator = (LinearLayout)view.findViewById(R.id.vod_main_thismonth_viewpager_indicator);
        //
        MainCategoryObject cate1 = mPref.getMainCategoryObject(1);
        MainCategoryObject cate2 = mPref.getMainCategoryObject(2);
        MainCategoryObject cate3 = mPref.getMainCategoryObject(3);
        mSection1TextView.setText(cate1.getsCategoryTitle());
        mSection2TextView.setText(cate2.getsCategoryTitle());
        mSection3TextView.setText(cate3.getsCategoryTitle());


        requestGetServiceBannerList(); // 배너 요청


        mSection1Linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainCategoryObject cate = mPref.getMainCategoryObject(1);
                mPref.put(JYSharedPreferences.VOD_OTHER_TAB_CATEGORY_ID, cate.getsCategoryId());
                textView2.performClick();
            }
        });

        mSection2Linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainCategoryObject cate = mPref.getMainCategoryObject(2);
                mPref.put(JYSharedPreferences.VOD_OTHER_TAB_CATEGORY_ID, cate.getsCategoryId());
                textView2.performClick();
            }
        });

        mSection3Linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainCategoryObject cate = mPref.getMainCategoryObject(3);
                mPref.put(JYSharedPreferences.VOD_OTHER_TAB_CATEGORY_ID, cate.getsCategoryId());
                textView2.performClick();
            }
        });

        return view;
    }

    // 카테고리 요청.
    // 추천
    // http://192.168.40.5:8080/HApplicationServer/getCategoryTree.xml?version=1&categoryProfile=4&categoryId=713228&depth=3&traverseType=DFS
//    private void requestGetCategoryTree() {
//
//        /**
//         * 버젼 체크해서 얼럿 띄우자.
//         */
//        String serverVer  = mPref.getAppVersionForServer();
//        String appVer     = mPref.getAppVersionForApp();
//        Float  fVerServer = Float.valueOf(serverVer);
//        Float  fVerApp    = Float.valueOf(appVer);
//        if ( fVerServer > fVerApp ) {
//            StringBuilder sb   = new StringBuilder();
//            sb.append("지금 사용하시는 버전보다 더 최신버전이 존재합니다. 구글 마켓에서 업데이트 하신 뒤 사용하시기 바랍니다.").append("\n")
//                    .append("사용 중인 버전 : ").append(appVer + " ver.\n")
//            .append("최신 버전 : ").append(serverVer).append(" ver.");
//            AlertDialog.Builder alert = new AlertDialog.Builder(mInstance.getActivity());
//            alert.setPositiveButton("알림", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            });
//            alert.setMessage(sb.toString());
//            alert.show();
//        }
//
//
//        mProgressDialog	 = ProgressDialog.show(mInstance.getActivity(), "", getString(R.string.wait_a_moment));
//        //String url = mPref.getWebhasServerUrl() + "/getCategoryTree.json?version=1&terminalKey="+mPref.getWebhasTerminalKey()+"&categoryProfile=4&categoryId=713228&depth=3&traverseType=DFS";
//        String url = mPref.getWebhasServerUrl() + "/getCategoryTree.json?version=1&terminalKey="+mPref.getWebhasTerminalKey()+"&categoryProfile=4&categoryId=713230&depth=3&traverseType=DFS";
//        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    JSONObject first       = new JSONObject(response);
//                    int        resultCode  = first.getInt("resultCode");
//                    String     errorString = first.getString("errorString");
//
//                    categorys              = new ArrayList<JSONObject>();
//                    JSONArray  categoryList = first.getJSONArray("categoryList");
//                    for ( int i = 0; i < categoryList.length(); i++ ) {
//                        JSONObject category     = (JSONObject)categoryList.get(i);
//                        int        viewerType   = category.getInt("viewerType");
//                        String     categoryId   = category.getString("categoryId");
//                        String     categoryName = category.getString("categoryName");
//
//                        /**
//                         ViewerType = 30, getContentGroupList (보통 리스트)
//                         ViewerType = 200, getPopularityChart (인기순위)
//                         ViewerType = 41, ㅎetBundleProductList (묶음)
//                         ViewerType = 310, recommendContentGroupByAssetId (연관)
//                         */
//                        if ( viewerType == 30 || viewerType == 200 || viewerType == 41 ) {
//                            categorys.add(category);
//                        }
//                    }
//                } catch ( JSONException e ) {
//                    e.printStackTrace();
//                }
//                requestGetServiceBannerList(); // 배너 요청
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                mProgressDialog.dismiss();
//                if (error instanceof TimeoutError) {
//                    Toast.makeText(mInstance.getActivity(), mInstance.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show();
//                } else if (error instanceof NoConnectionError) {
//                    Toast.makeText(mInstance.getActivity(), mInstance.getString(R.string.error_network_noconnectionerror), Toast.LENGTH_LONG).show();
//                } else if (error instanceof ServerError) {
//                    Toast.makeText(mInstance.getActivity(), mInstance.getString(R.string.error_network_servererror), Toast.LENGTH_LONG).show();
//                } else if (error instanceof NetworkError) {
//                    Toast.makeText(mInstance.getActivity(), mInstance.getString(R.string.error_network_networkerrorr), Toast.LENGTH_LONG).show();
//                }
//                if ( mPref.isLogging() ) { VolleyLog.d(tag, "onErrorResponse(): " + error.getMessage()); }
//            }
//        }) {
//            @Override
//            protected Map<String,String> getParams(){
//                Map<String,String> params = new HashMap<String, String>();
//                if ( mPref.isLogging() ) { Log.d(tag, "getParams()" + params.toString()); }
//                return params;
//            }
//        };
//        mRequestQueue.add(request);
//    }

    @Override
    public void onBackPressedCallback() {
        String alertTitle = "딜라이브 모바일TV 종료";
        String alertMsg1 = getString(R.string.app_name)+"를 종료하시겠습니까?";
        String alertMsg2 = "";
        CMAlertUtil.Alert1(getActivity(), alertTitle, alertMsg1, alertMsg2, false, true, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
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

    // 배너 요청
    private void requestGetServiceBannerList() {
        StringBuffer sb = new StringBuffer().append("terminalKey=").append(JYSharedPreferences.RUMPERS_TERMINAL_KEY);
        String url = mPref.getRumpersServerUrl() + "/getservicebannerlist.asp?"+sb.toString();
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseGetServiceBannerList(response);
                mBannerViewPager.setAdapter(new PagerAdapterClass(mInstance.getActivity()));

                int totalCount = mBanners.size();
                UiUtil.initializePageIndicator(getActivity(), totalCount, mBannerViewPagerIndicator, mBannerIndicatorIndex);

                requestGetPopularityChart();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
                JSONObject jo   = mBanners.get(position);
                final String assetId2  = jo.getString("assetId");
                String imageUrl = jo.getString("android_imgurl");
                NetworkImageView niv  = (NetworkImageView)v.findViewById(R.id.vod_main_banner_network_imageview);
                niv.setDefaultImageResId(R.mipmap.banner_empty);
                niv.setImageUrl(imageUrl, mImageLoader);
                niv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), VodDetailActivity.class);
                        intent.putExtra("assetId", assetId2);
                        getActivity().startActivity(intent);
                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }

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
        //String url = mPref.getWebhasServerUrl() + "/getPopularityChart.json?version=1&terminalKey="+mPref.getWebhasTerminalKey()+"&categoryId=713230&requestItems=weekly";
        MainCategoryObject cate = mPref.getMainCategoryObject(1);
        String url = mPref.getWebhasServerUrl() + "/getPopularityChart.json?version=1&terminalKey="+mPref.getWebhasTerminalKey()+"&categoryId="+cate.getsCategoryId()+"&requestItems=weekly";

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

                        // minkyuuuu 2015-12-03 추천 페이지 : 페이징 인디케이터 5개로 제한.
                        // 1 page당 8개의 item : total 40개로 제한
                        int restrictCount = popularityList.length();
                        if( restrictCount >=40 ) restrictCount = 40;

                        for ( int i = 0; i < restrictCount; i++ ) {
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

                int totalCount = mPop20PagerAdapter.getCount();
                UiUtil.initializePageIndicator(getActivity(), totalCount, mPop20ViewPagerIndicator, mPop20IndicatorIndex);

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


    // 금주의 신작영화
    private void requestGetContentGroupList() {
        MainCategoryObject cate = mPref.getMainCategoryObject(2);
        String url = mPref.getWebhasServerUrl() + "/getContentGroupList.json?version=1&terminalKey="+mPref.getWebhasTerminalKey()+"&contentGroupProfile=2&sortType=notSet"
                + "&categoryId="+cate.getsCategoryId();
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject first            = new JSONObject(response);
                    JSONArray  contentGroupList = first.getJSONArray("contentGroupList");

                    // minkyuuuu 2015-12-03 추천 페이지 : 페이징 인디케이터 5개로 제한.
                    // 1 page당 8개의 item : total 40개로 제한
                    int restrictCount = contentGroupList.length();
                    if( restrictCount >=40 ) restrictCount = 40;

                    for ( int i = 0; i < restrictCount; i++ ) {
                        JSONObject jo = (JSONObject)contentGroupList.get(i);
                        mNewMoviePagerAdapter.addVod(jo);
                    }
                    mNewMoviePagerAdapter.notifyDataSetChanged();

                    int totalCount = mNewMoviePagerAdapter.getCount();
                    UiUtil.initializePageIndicator(getActivity(), totalCount, mNewMovieViewPagerIndicator, mNewMovieIndicatorIndex);
                } catch ( JSONException e ) {
                    e.printStackTrace();
                }
                requestGetContentGroupList2(); // 이달의 추천 VOD
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
        MainCategoryObject cate = mPref.getMainCategoryObject(3);
        String url = mPref.getWebhasServerUrl() + "/getContentGroupList.json?version=1&terminalKey="+mPref.getWebhasTerminalKey()+"&contentGroupProfile=2&sortType=notSet"
            + "&categoryId="+cate.getsCategoryId();
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //mProgressDialog.dismiss();
                try {
                    JSONObject first            = new JSONObject(response);
                    JSONArray  contentGroupList = first.getJSONArray("contentGroupList");

                    // minkyuuuu 2015-12-03 추천 페이지 : 페이징 인디케이터 5개로 제한.
                    // 1 page당 8개의 item : total 40개로 제한
                    int restrictCount = contentGroupList.length();
                    if( restrictCount >=40 ) restrictCount = 40;

                    for ( int i = 0; i < restrictCount; i++ ) {
                        JSONObject jo = (JSONObject)contentGroupList.get(i);
                        mThisMonthPagerAdapter.addVod(jo);
                    }
                    mThisMonthPagerAdapter.notifyDataSetChanged();

                    int totalCount = mThisMonthPagerAdapter.getCount();
                    UiUtil.initializePageIndicator(getActivity(), totalCount, mThisMonthViewPagerIndicator, mThisMonthIndicatorIndex);
                } catch ( JSONException e ) {
                    e.printStackTrace();
                }
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



}
