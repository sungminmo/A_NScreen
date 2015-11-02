package com.stvn.nscreen.vod;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.FourVodPosterPagerAdapter;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.common.UiUtil;
import com.jjiya.android.http.BitmapLruCache;
import com.jjiya.android.http.JYStringRequest;
import com.stvn.nscreen.R;
import com.stvn.nscreen.common.CMActionBar;
import com.stvn.nscreen.common.CMBaseActivity;
import com.widevine.sampleplayer.VideoPlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class VodDetailActivity extends Activity {

    private static final String              tag = VodDetailActivity.class.getSimpleName();
    private static       VodDetailActivity   mInstance;
    private              JYSharedPreferences mPref;

    // network
    private RequestQueue mRequestQueue;
    private ProgressDialog mProgressDialog;
    private String sJson;
    private ImageLoader mImageLoader;

    // UI
    private TextView mTitleTextView;
    private ImageView mRatingImageView;
    private ImageView mReviewStar1ImageView;
    private ImageView mReviewStar2ImageView;
    private ImageView mReviewStar3ImageView;
    private ImageView mReviewStar4ImageView;
    private ImageView mReviewStar5ImageView;
    private ImageView mPromotionSticker;
    private ImageView mHdSdImageView;
    private NetworkImageView mMovieImageImageView;
    private TextView mPriceTextView;
    private TextView mGenreTextView;
    private TextView mDirectorTextView;
    private TextView mStarringTextView;
    private TextView mViewableTextView;
    private LinearLayout mSeriesLinearLayout;
    private TextView mSynopsisTextView;
    private LinearLayout mPurchaseLinearLayout;
    private LinearLayout mPlayLinearLayout;
    private LinearLayout mTvOnlyLiearLayout;
    private ViewPager mViewPager;
    private String viewable;

    private Button mPurchaseButton; // 구매하기 버튼
    private Button mPlayButton; // 시청하기 버튼

    // activity
    private String assetId; // intent param
    private List<JSONObject> relationVods;
    private FourVodPosterPagerAdapter mPagerAdapter;
    private String isSeriesLink; //시리즈인지 연부. true/false
    private String mTitle;
    private String sListPrice;
    private String sPrice;


    // for 결재
    private String productId;
    private String goodId;



    // for player
    private String fileName; // M4145902.mpg
    private String contentUri; // http://cjhv.video.toast.com/aaaaaa/5268a42c-5bfe-46ac-b8f0-9c094ee5327b.wvm
    private String drmServerUri; // http://proxy.video.toast.com/widevine/drm/dls.do
    private String drmProtection; // true

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod_detail);

        mInstance     = this;
        mPref         = new JYSharedPreferences(this);
        mRequestQueue = Volley.newRequestQueue(this);
        ImageLoader.ImageCache imageCache = new BitmapLruCache();
        mImageLoader = new ImageLoader(mRequestQueue, imageCache);

        //sJson   = getIntent().getExtras().getString("sJson");
        assetId   = getIntent().getExtras().getString("assetId");
//        try {
//            JSONObject jo = new JSONObject(sJson);
//            assetId = jo.getString("assetId");
//            // (HD)막돼먹은 영애씨 시즌14 02회(08/11
//            // http://192.168.40.5:8080/HApplicationServer/getAssetInfo.xml?version=1&terminalKey=9CED3A20FB6A4D7FF35D1AC965F988D2&assetProfile=9&assetId=www.hchoice.co.kr%7CM4132449LFO281926301&transactionId=200
//            assetId = "www.hchoice.co.kr|M4132449LFO281926301";
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        relationVods = new ArrayList<JSONObject>();
        mPagerAdapter = new FourVodPosterPagerAdapter(this);
        mPagerAdapter.setImageLoader(mImageLoader);
        mPagerAdapter.setVodDetailActivity(this);

        mTitleTextView        = (TextView)findViewById(R.id.vod_detail_title);
        mRatingImageView      = (ImageView)findViewById(R.id.vod_detail_rating_imageview);
        mReviewStar1ImageView = (ImageView)findViewById(R.id.vod_detail_review1);
        mReviewStar2ImageView = (ImageView)findViewById(R.id.vod_detail_review2);
        mReviewStar3ImageView = (ImageView)findViewById(R.id.vod_detail_review3);
        mReviewStar4ImageView = (ImageView)findViewById(R.id.vod_detail_review4);
        mReviewStar5ImageView = (ImageView)findViewById(R.id.vod_detail_review5);
        mHdSdImageView        = (ImageView)findViewById(R.id.vod_detail_hd);
        mPromotionSticker     = (ImageView)findViewById(R.id.imageView14);
        mMovieImageImageView  = (NetworkImageView)findViewById(R.id.vod_detail_imagefilename_imageview);
        mPriceTextView        = (TextView)findViewById(R.id.vod_detail_price_textview);
        mGenreTextView        = (TextView)findViewById(R.id.vod_detail_genre_textview);
        mDirectorTextView     = (TextView)findViewById(R.id.vod_detail_director_textview);
        mStarringTextView     = (TextView)findViewById(R.id.vod_detail_starring_textview);
        mViewableTextView     = (TextView)findViewById(R.id.vod_detail_viewable_textview);
        mSeriesLinearLayout   = (LinearLayout)findViewById(R.id.vod_detail_series_linearlayout);
        mSynopsisTextView     = (TextView)findViewById(R.id.vod_detail_synopsis_textview);
        mPurchaseLinearLayout = (LinearLayout)findViewById(R.id.vod_detail_purchase_linearlayout);
        mPlayLinearLayout     = (LinearLayout)findViewById(R.id.vod_detail_play_linearlayout);
        mTvOnlyLiearLayout    = (LinearLayout)findViewById(R.id.vod_detail_tvonly_linearlayout);
        mViewPager            = (ViewPager)findViewById(R.id.vod_detail_related_viewpager);

        ImageButton backButton = (ImageButton)findViewById(R.id.vod_detail_topmenu_left_imagebutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 미리보기 | 구매하기 | 찜하기
        mPurchaseButton       = (Button)findViewById(R.id.vod_detail_order_button);
        mPurchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(VodMainFragment.this, VodCategoryMainActivity.class);
                //startActivity(i);

                /*
                Bundle param = new Bundle();
                param.putString("assetId", mInstance.assetId);
                param.putString("isSeriesLink", isSeriesLink);
                param.putString("mTitle", mTitle);
                param.putString("sListPrice", sListPrice);
                param.putString("sPrice", sPrice);
                param.putString("productId", productId);
                param.putString("goodId", goodId);


                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                VodBuyFragment vf = new VodBuyFragment();
                vf.setArguments(param);
                ft.replace(R.id.fragment_placeholder, vf);
                ft.addToBackStack("VodBuyFragment");
                ft.commit();
                */

                Intent intent = new Intent(mInstance, VodBuyActivity.class);
                intent.putExtra("assetId", mInstance.assetId);
                intent.putExtra("isSeriesLink", isSeriesLink);
                intent.putExtra("mTitle", mTitle);
                intent.putExtra("sListPrice", sListPrice);
                intent.putExtra("sPrice", sPrice);
                intent.putExtra("productId", productId);
                intent.putExtra("goodId", goodId);
                startActivityForResult(intent, 1000);
            }
        });

        // 시청하기 버튼
        mPlayButton = (Button)findViewById(R.id.vod_detail_play_button);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestContentUri();
            }
        });

        // (HD)막돼먹은 영애씨 시즌14 02회(08/11
        // http://192.168.40.5:8080/HApplicationServer/getAssetInfo.xml?version=1&terminalKey=9CED3A20FB6A4D7FF35D1AC965F988D2&assetProfile=9&assetId=www.hchoice.co.kr%7CM4132449LFO281926301&transactionId=200
        /**
         * VOD 상세정보 요청
         */
        requestGetAssetInfo();

        /**
         * 화면 하단의 연관 VOD 요청
         */
        requestRecommendContentGroupByAssetId();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch(requestCode){
            case 1000: {    // 결제
                if ( resultCode == RESULT_OK ) {
                    /**
                     * VOD 상세정보 요청
                     */
                    requestGetAssetInfo();
                } else if ( resultCode == RESULT_CANCELED ) {
                    // nothing
                }
            } break;
            case 111: {     // player
                if ( resultCode == RESULT_OK ) {
                    // nothing
                } else if ( resultCode == RESULT_CANCELED ) {
                    // nothing
                }
            } break;
        }
    }

    private void requestGetAssetInfo() {
        mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestGetAssetInfo()"); }
        String terminalKey = mPref.getWebhasTerminalKey();
        String encAssetId = null;
        try {
            encAssetId  = URLDecoder.decode(assetId, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = mPref.getWebhasServerUrl() + "/getAssetInfo.json?version=1&terminalKey="+terminalKey+"&assetProfile=9&assetId="+encAssetId+"&transactionId=200";
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(tag, response);
                mProgressDialog.dismiss();

                try {
                    JSONObject jo            = new JSONObject(response);

                    // asset
                    JSONObject asset            = jo.getJSONObject("asset");
                    fileName                    = asset.getString("fileName");
                    String imageFileName        = asset.getString("imageFileName");
                    String rating               = asset.getString("rating");
                    String reviewRatingCount    = asset.getString("reviewRatingCount");
                    String reviewRatingTotal    = asset.getString("reviewRatingTotal");
                    boolean HDContent           = asset.getBoolean("HDContent");
                    String genre                = asset.getString("genre");
                    String runningTime          = asset.getString("runningTime");
                    String director             = asset.getString("director");
                    String starring             = asset.getString("starring");
                    String synopsis             = asset.getString("synopsis");
                    boolean seriesLink          = asset.getBoolean("seriesLink");
                    String promotionSticker     = asset.getString("promotionSticker");
                    String title                = asset.getString("title");

                    JSONArray productLists      = asset.getJSONArray("productList");
                    JSONObject product          = (JSONObject)productLists.get(0);
                    productId                   = product.getString("productId");
                    goodId                      = product.getString("goodId");
                    Integer viewablePeriodState = product.getInt("viewablePeriodState");
                    String viewablePeriod       = product.getString("viewablePeriod");


                    String isNew                = ""; // 0:없음, 1:있음.
                    Object isNewObj             = asset.get("isNew");
                    if ( isNewObj != null ) { isNew = asset.getString("isNew"); }
                    String assetNew          = "0"; // 0:없음, 1:new일부만, 2:new단체
                    if ( ! asset.isNull("assetNew") ) {
                        assetNew = asset.getString("assetNew");
                    }
                    String assetHot          = "0"; // 0:없음, 1:new일부만, 2:new단체
                    if ( ! asset.isNull("assetHot") ) {
                        assetHot = asset.getString("assetHot");
                    }
                    String hot               = ""; // 0:없음, 1:있음.
                    if ( ! asset.isNull("hot") ) {
                        hot = asset.getString("hot");
                    }

                    String runningTimeMinute = String.valueOf((Integer.parseInt(runningTime.substring(0, 2)) * 60) + Integer.parseInt(runningTime.substring(3))) + "분";
                    runningTime = runningTimeMinute;

                    // productList
                    //JSONArray productList    = asset.getJSONArray("productList");
                    //JSONObject product       = productLists.getJSONObject(0);
                    String price             = product.getString("price");
                    String listPrice         = product.getString("listPrice");
                    String purchasedId       = product.getString("purchasedId");
                    String purchasedTime     = product.getString("purchasedTime");

                    // LinearLayout 감추기/보이기 -----------------------------------------------------
                    if ( seriesLink == true ) {      // 시리즈 보여라
                        isSeriesLink = "YES";
                        mSeriesLinearLayout.setVisibility(View.VISIBLE);
                    } else {                         // 시리즈 감춰라.
                        isSeriesLink = "NO";
                        mSeriesLinearLayout.setVisibility(View.GONE);
                    }
                    if ( "".equals(purchasedTime) ) { // 구매하기 보여랴
                        mPurchaseLinearLayout.setVisibility(View.VISIBLE);
                        mPlayLinearLayout.setVisibility(View.GONE);
                    } else {                         // 구매했다. 감쳐라.
                        mPurchaseLinearLayout.setVisibility(View.GONE);
                        mPlayLinearLayout.setVisibility(View.VISIBLE);
                    }
                    // @// TODO: 2015. 10. 21. 지금은 값이 안내려옴. 시청기기 서버작업 되면 수정해야 됨.
                    if ( true ) {                    // TV에서봐 보여랴
                        mTvOnlyLiearLayout.setVisibility(View.GONE);
                    } else {                         // TV에서봐 감쳐라.
                        mTvOnlyLiearLayout.setVisibility(View.GONE);
                    }

                    // 값들 찍어주기. -----------------------------------------------------------------

                    mMovieImageImageView.setImageUrl(imageFileName, mImageLoader);

                    UiUtil.setPromotionSticker(promotionSticker, isNew, hot, assetNew, assetHot, mPromotionSticker);

                    if( viewablePeriodState == 1 ) {
                        mViewableTextView.setText("무제한시청");
                    } else {
                        viewable = String.valueOf((Integer.parseInt(viewablePeriod.substring(0, 4)) * 365) + (Integer.parseInt(viewablePeriod.substring(5, 7)) * 30 ) + Integer.parseInt(viewablePeriod.substring(8, 10))) + "일";
                        viewablePeriod = viewable;
                        mViewableTextView.setText(viewablePeriod);
                    }

                    mTitleTextView.setText(asset.getString("title"));
                    mTitle = title;
                    if ( "00".equals(rating) ) {
                        mRatingImageView.setImageResource(R.mipmap.btn_age_all);
                    } else if ( "07".equals(rating) ) {
                        mRatingImageView.setImageResource(R.mipmap.btn_age_7);
                    } else if ( "12".equals(rating) ) {
                        mRatingImageView.setImageResource(R.mipmap.btn_age_12);
                    } else if ( "15".equals(rating) ) {
                        mRatingImageView.setImageResource(R.mipmap.btn_age_15);
                    } else if ( "19".equals(rating) ) {
                        mRatingImageView.setImageResource(R.mipmap.btn_age_19);
                    }
                    Float lreviewRatingCount = Float.parseFloat(reviewRatingCount);
                    Float lreviewRatingTotal = Float.parseFloat(reviewRatingTotal);
                    Float reviewRating = 0f;
                    if ( lreviewRatingTotal > 0 ) { reviewRating = lreviewRatingTotal / lreviewRatingCount;
                    }
                    UiUtil.setStarRating(reviewRating, mReviewStar1ImageView, mReviewStar2ImageView, mReviewStar3ImageView, mReviewStar4ImageView, mReviewStar5ImageView);

                    if ( HDContent == true ) {
                        mHdSdImageView.setImageResource(R.mipmap.btn_size_hd);
                    } else {
                        mHdSdImageView.setImageResource(R.mipmap.btn_size_sd);
                    }
                    sPrice = price;
                    sListPrice = listPrice;
                    mPriceTextView.setText(UiUtil.stringParserCommafy(price) + "원 [부가세 별도]");
                    mGenreTextView.setText(genre+" / "+runningTime);
                    mDirectorTextView.setText(director);
                    mStarringTextView.setText(starring);
                    mSynopsisTextView.setText(synopsis);


                } catch (JSONException e) {
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
                params.put("version", String.valueOf(1));
                params.put("areaCode", String.valueOf(0));
                if ( mPref.isLogging() ) { Log.d(tag, "getParams()" + params.toString()); }
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    // http://192.168.40.5:8080/HApplicationServer/recommendContentGroupByAssetId.json?
    // version=1&
    // terminalKey=9CED3A20FB6A4D7FF35D1AC965F988D2&
    // assetId=www.hchoice.co.kr|M4154270LSG347422301&
    // contentGroupProfile=2

    /**
     * 연관 VOD
     */
    private void requestRecommendContentGroupByAssetId() {
        //mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestRecommendContentGroupByAssetId()"); }
        String terminalKey = mPref.getWebhasTerminalKey();
        String encAssetId = null;
        try {
            encAssetId  = URLDecoder.decode(assetId, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = mPref.getWebhasServerUrl() + "/recommendContentGroupByAssetId.json?version=1&terminalKey="+terminalKey+"&&assetId="+encAssetId+"&contentGroupProfile=2";
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(tag, response);
                //mProgressDialog.dismiss();
                try {
                    JSONObject jo            = new JSONObject(response);

                    // contentGroupList
                    JSONArray contentGroupLists = jo.getJSONArray("contentGroupList");
                    for ( int i = 0; i < contentGroupLists.length(); i++ ) {
                        if ( i > 19 ) {
                            break;
                        }
                        JSONObject content = contentGroupLists.getJSONObject(i);
                        relationVods.add(content);
                        mPagerAdapter.addVod(content);
                    }
                    mViewPager.setAdapter(mPagerAdapter);
                } catch (JSONException e) {
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
                params.put("version", String.valueOf(1));
                params.put("areaCode", String.valueOf(0));
                if ( mPref.isLogging() ) { Log.d(tag, "getParams()" + params.toString()); }
                return params;
            }
        };
        mRequestQueue.add(request);
    }


//    private String fileName; // M4145902.mpg
//    private String contentUri; // http://cjhv.video.toast.com/aaaaaa/5268a42c-5bfe-46ac-b8f0-9c094ee5327b.wvm
//    private String drmServerUri; // http://proxy.video.toast.com/widevine/drm/dls.do
//    private String drmProtection; // true

    private void requestContentUri() {
        mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestContentUri()"); }
        String terminalKey = mPref.getWebhasTerminalKey();
        String url = "https://api.cablevod.co.kr/api/v1/mso/10/asset/"+fileName+"/play";
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(tag, response);
                mProgressDialog.dismiss();
                try {
                    JSONObject jo     = new JSONObject(response);
                    JSONObject drm    = jo.getJSONObject("drm");
                    JSONObject header = jo.getJSONObject("header");
                    contentUri        = drm.getString("contentUri");
                    drmServerUri      = drm.getString("drmServerUri");
                    boolean bDrm      = drm.getBoolean("drmProtection");
                    if ( bDrm == true ) {
                        drmProtection = "true";
                    } else {
                        drmProtection = "false";
                    }

                    // http://cjhv.video.toast.com/aaaaaa/7916612d-c6cb-752e-2eb8-650e4289e3e2.wvm
//                    Intent intent = new Intent(mInstance.getActivity(), WidevineSamplePlayer.class);
//                    Intent intent = new Intent(mInstance.getActivity(), StreamingActivity.class);

                    String terminalKey = mPref.getWebhasTerminalKey();

                    /*
                    assets.add(new AssetItem("http://cjhv.video.toast.com/aaaaaa/5268a42c-5bfe-46ac-b8f0-9c094ee5327b.wvm", 1));
                    assets.add(new AssetItem("widevine://cnm.video.toast.com/aaaaaa/dc66940e-4e2a-4cb0-b478-b3f6bc7147d6.wvm", 1));
                    투모로우랜드 "widevine://cnm.video.toast.com/aaaaaa/dc66940e-4e2a-4cb0-b478-b3f6bc7147d6.wvm"
                     */
                    contentUri = contentUri.replace("http://","widevine://");

                    Intent intent = new Intent(mInstance, VideoPlayerView.class);
                    //intent.putExtra("com.widevine.demo.Path", "http://cjhv.video.toast.com/aaaaaa/7916612d-c6cb-752e-2eb8-650e4289e3e2.wvm");
                    intent.putExtra("com.widevine.demo.Path", contentUri);
                    //intent.putExtra("currentpage", currentPage);
                    //intent.putExtra("title", title);
                    intent.putExtra("assetId", assetId);
                    intent.putExtra("contentUri", contentUri);
                    intent.putExtra("drmServerUri", drmServerUri);
                    intent.putExtra("drmProtection", drmProtection);
                    intent.putExtra("terminalKey", terminalKey);
                    startActivityForResult(intent, 111);
                    //startActivity(intent);



                } catch (JSONException e) {
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
                params.put("version", String.valueOf(1));
                params.put("areaCode", String.valueOf(0));
                if ( mPref.isLogging() ) { Log.d(tag, "getParams()" + params.toString()); }
                return params;
            }
        };
        mRequestQueue.add(request);
    }
}
