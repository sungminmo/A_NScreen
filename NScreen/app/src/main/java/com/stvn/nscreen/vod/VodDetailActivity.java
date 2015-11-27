package com.stvn.nscreen.vod;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.Constants;
import com.jjiya.android.common.FourVodPosterPagerAdapter;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.common.UiUtil;
import com.jjiya.android.http.BitmapLruCache;
import com.jjiya.android.http.JYStringRequest;
import com.stvn.nscreen.R;
import com.stvn.nscreen.bean.WishObject;
import com.stvn.nscreen.util.CMAlertUtil;
import com.widevine.sampleplayer.VideoPlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

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
    private ImageView mReviewStar1ImageView, mReviewStar2ImageView, mReviewStar3ImageView, mReviewStar4ImageView, mReviewStar5ImageView;
    private ImageView mPromotionSticker;
    private ImageView mHdSdImageView;
    private NetworkImageView mMovieImageImageView;
    private TextView mPriceTextView;
    private TextView mGenreTextView;
    private TextView mDirectorTextView;
    private TextView mStarringTextView;
    private TextView mViewableTextView;
    private ImageView mMobileImageView;
    private LinearLayout mSeriesLinearLayout;
    private ImageView mSeriesLeftImage;
    private ImageView mSeriesRightImage;
    private TextView mSynopsisTextView;
    private LinearLayout mPurchaseLinearLayout;
    private LinearLayout mPurchaseLinearLayout2;
    private LinearLayout mPlayLinearLayout;
    private LinearLayout mTvOnlyLiearLayout;
    private TextView mTvOnlyTextView;
    private ViewPager mViewPager;
    private String viewable;

    private Button mPurchaseButton; // 구매하기 버튼  (버튼 3개 레이아웃)
    private Button mPurchaseButton2; // 구매하기 버튼 (버튼 2개 레이아웃)
    private Button mPrePlayButton;  // 미리보기 버튼
    private Button mPlayButton;     // 시청하기 버튼
    private Button mJimButton;      // 찜하기 버튼
    private Button mJimButton2;      // 찜하기 버튼

    private HorizontalScrollView mSeriesScrollView;


    // activity
    private String assetId; // intent param
    private String episodePeerExistence; // intent param (episodePeerExistence==1일 경우 이값이 넘오온다.)
    private String contentGroupId;       // intent param (episodePeerExistence==1일 경우 이값이 넘오온다.)
    private String primaryAssetId;       // intent param (episodePeerExistence==1일 경우 이값이 넘오온다.)
    private String episodePeerId;        // getEpisodePeerListByContentGroupId
    private JSONArray episodePeerList;
    private String seriesCurIndex;        // getAssetListByEpisodePeerId
    private String seriesEndIndex;        // getAssetListByEpisodePeerId
    private String seriesTotalAssetCount; // getAssetListByEpisodePeerId
    private String mSeriesReleaseDate;    // getEpisodePeerListByContentGroupId


    private List<JSONObject> relationVods;
    //private List<JSONObject> series;
    private FourVodPosterPagerAdapter mPagerAdapter;
    private String isSeriesLink; //시리즈인지 연부. true/false
    private String mTitle;
    private String sListPrice;
    private String sPrice;


    // for 결재
    private JSONArray productList;
    private JSONArray discountCouponMasterIdList;

    private String productType; // RVOD, ....
    private String productId;
    private String goodId;
    private String categoryId;



    // for player
    private String fileName; // M4145902.mpg
    private String contentUri; // http://cjhv.video.toast.com/aaaaaa/5268a42c-5bfe-46ac-b8f0-9c094ee5327b.wvm
    private String drmServerUri; // http://proxy.video.toast.com/widevine/drm/dls.do
    private String drmProtection; // true
    private boolean isPrePlay; // 미리보기? 아니면 전체보기 임.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod_detail);

        mInstance     = this;
        mPref         = new JYSharedPreferences(this);
        mRequestQueue = Volley.newRequestQueue(this);
        ImageLoader.ImageCache imageCache = new BitmapLruCache();
        mImageLoader  = new ImageLoader(mRequestQueue, imageCache);

        isPrePlay     = true;


        //sJson   = getIntent().getExtras().getString("sJson");
        assetId   = getIntent().getExtras().getString("assetId");
        if ( getIntent().getExtras().get("episodePeerExistence") == null ) {
            episodePeerExistence = "";
            contentGroupId       = "";
            primaryAssetId       = "";
        } else {
            episodePeerExistence = getIntent().getExtras().getString("episodePeerExistence");
            contentGroupId       = getIntent().getExtras().getString("contentGroupId");
            primaryAssetId       = getIntent().getExtras().getString("primaryAssetId");
        }
        // (HD)막돼먹은 영애씨 시즌14 02회(08/11
        // assetId = "www.hchoice.co.kr|M4132449LFO281926301";
        // 01회(HD)그녀는 예뻤다(15.09.16)
        // assetId = "www.hchoice.co.kr|M4146149LFO280395301";
//        try {
//            JSONObject jo = new JSONObject(sJson);
//            assetId = jo.getString("assetId");
//            // (HD)막돼먹은 영애씨 시즌14 02회(08/11
//            // http://192.168.40.5:8080/HApplicationServer/getAssetInfo.xml?version=1&terminalKey=9CED3A20FB6A4D7FF35D1AC965F988D2&assetProfile=9&assetId=www.hchoice.co.kr%7CM4132449LFO281926301&transactionId=200
              // assetId = "www.hchoice.co.kr|M4132449LFO281926301";
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        relationVods = new ArrayList<JSONObject>();
//        series = new ArrayList<JSONObject>();
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
        mSeriesLinearLayout   = (LinearLayout)findViewById(R.id.vod_detail_series_linearlayout);    // 시리즈 회차 버튼
        mSeriesLeftImage      = (ImageView)findViewById(R.id.vod_detail_series_left_image);
        mSeriesRightImage      = (ImageView)findViewById(R.id.vod_detail_series_right_image);
        mSynopsisTextView     = (TextView)findViewById(R.id.vod_detail_synopsis_textview);
        mPurchaseLinearLayout = (LinearLayout)findViewById(R.id.vod_detail_purchase_linearlayout);  // 미리보기/구매하기/찜하기
        mPurchaseLinearLayout2 = (LinearLayout)findViewById(R.id.vod_detail_purchase_linearlayout2);  // 구매하기/찜하기
        mPlayLinearLayout     = (LinearLayout)findViewById(R.id.vod_detail_play_linearlayout);      // 시청하기
        mTvOnlyLiearLayout    = (LinearLayout)findViewById(R.id.vod_detail_tvonly_linearlayout);    // TV에서 시청가능합니다.
        mTvOnlyTextView       = (TextView)findViewById(R.id.vod_detail_tvonly_textview);
        mMobileImageView      = (ImageView)findViewById(R.id.vod_detail_device_mobile_imageview);
        mViewPager            = (ViewPager)findViewById(R.id.vod_detail_related_viewpager);
        mSeriesScrollView     = (HorizontalScrollView)findViewById(R.id.mSeriesScrollView);
        mSeriesScrollView.post(new Runnable(){
            @Override
            public void run() {
                ViewTreeObserver observer = mSeriesScrollView.getViewTreeObserver();
                observer.addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener(){
                    @Override
                    public void onScrollChanged() {
                        int x = mSeriesScrollView.getScrollX();
                        int barsize = mSeriesScrollView.getScrollBarSize();
                        int width = mSeriesScrollView.getWidth();
                        if ( (width-10) <= x ) {
                            //Log.d(tag, "왼쪽에 화살표 찍어라");
                            mSeriesLeftImage.setImageResource(R.mipmap.series_arrow_01);
                            mSeriesRightImage.setImageResource(R.mipmap.series_arrow_02_dim);
                        } else if ( 10 >= x ) {
                            //Log.d(tag, "오른쪽에 화살표 찍어라");
                            mSeriesLeftImage.setImageResource(R.mipmap.series_arrow_01_dim);
                            mSeriesRightImage.setImageResource(R.mipmap.series_arrow_02);
                        } else {
                            mSeriesLeftImage.setImageResource(R.mipmap.series_arrow_01);
                            mSeriesRightImage.setImageResource(R.mipmap.series_arrow_02);
                        }
                        Log.d(tag, "x: " + x + ", width: " + width);
                    }
                });
            }
        });

        ImageButton backButton = (ImageButton)findViewById(R.id.vod_detail_topmenu_left_imagebutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 미리보기 버튼
        mPrePlayButton = (Button)findViewById(R.id.vod_detail_preplay_button);
        mPrePlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( mPref.isPairingCompleted() == false ) {
                        String alertTitle = "셋탑박스 연동 필요";
                        String alertMsg1  = mTitle;
                        String alertMsg2  = getString(R.string.error_not_paring_compleated3);
                        CMAlertUtil.Alert1(mInstance, alertTitle, alertMsg1, alertMsg2, true, false, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }, true);
                } else {
                    isPrePlay = true;
                    requestContentUri();
                }
            }
        });

        // 미리보기 | 구매하기 | 찜하기
                mPurchaseButton       = (Button)findViewById(R.id.vod_detail_order_button);
                mPurchaseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                if ( mPref.isPairingCompleted() == false ) {
                    String alertTitle = "셋탑박스 연동 필요";
                    String alertMsg1  = mTitle;
                    String alertMsg2  = getString(R.string.error_not_paring_compleated3);
                    CMAlertUtil.Alert1(mInstance, alertTitle, alertMsg1, alertMsg2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }, true);
                } else {
                    Intent intent = new Intent(mInstance, VodBuyActivity.class);
                    intent.putExtra("assetId",      mInstance.assetId);
                    intent.putExtra("isSeriesLink", isSeriesLink);
                    intent.putExtra("mTitle",       mTitle);
                    intent.putExtra("sListPrice",   sListPrice);
                    intent.putExtra("sPrice",       sPrice);
                    intent.putExtra("productId",    productId);
                    intent.putExtra("goodId",       goodId);
                    intent.putExtra("categoryId",   categoryId);
                    intent.putExtra("productType",  productType);  // RVOD, ....
                    intent.putExtra("productList",  productList.toString());  // RVOD, ....
                    intent.putExtra("viewable",     viewable);  // 시청기간
                    intent.putExtra("discountCouponMasterIdList",  discountCouponMasterIdList.toString());

                    startActivityForResult(intent, 1000);
                }
            }
        });

        mPurchaseButton2       = (Button)findViewById(R.id.vod_detail_order_button2);
        mPurchaseButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( mPref.isPairingCompleted() == false ) {
                    String alertTitle = "셋탑박스 연동 필요";
                    String alertMsg1  = mTitle;
                    String alertMsg2  = getString(R.string.error_not_paring_compleated3);
                    CMAlertUtil.Alert1(mInstance, alertTitle, alertMsg1, alertMsg2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }, true);
                } else {
                    Intent intent = new Intent(mInstance, VodBuyActivity.class);
                    intent.putExtra("assetId",      mInstance.assetId);
                    intent.putExtra("isSeriesLink", isSeriesLink);
                    intent.putExtra("mTitle",       mTitle);
                    intent.putExtra("sListPrice",   sListPrice);
                    intent.putExtra("sPrice",       sPrice);
                    intent.putExtra("productId",    productId);
                    intent.putExtra("goodId",       goodId);
                    intent.putExtra("categoryId",   categoryId);
                    intent.putExtra("productType",  productType);  // RVOD, ....
                    intent.putExtra("productList",  productList.toString());
                    intent.putExtra("viewable",     viewable);  // 시청기간
                    intent.putExtra("discountCouponMasterIdList",  discountCouponMasterIdList.toString());
                    startActivityForResult(intent, 1000);
                }
            }
        });

        // 시청하기 버튼
        mPlayButton = (Button)findViewById(R.id.vod_detail_play_button);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( mPref.isPairingCompleted() == false ) {
                    String alertTitle = "셋탑박스 연동 필요";
                    String alertMsg1  = mTitle;
                    String alertMsg2  = getString(R.string.error_not_paring_compleated3);
                    CMAlertUtil.Alert1(mInstance, alertTitle, alertMsg1, alertMsg2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }, true);
                } else {
                    isPrePlay = false;
                    requestContentUri();
                }
            }
        });

        // 찜하기 버튼
        mJimButton = (Button)findViewById(R.id.vod_detail_jjim_button);
        mJimButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( mPref.isWishAsset(assetId) == false ) {
                    // 찜 안한 VOD
                    if ( mPref.isPairingCompleted() == false ) {
                        String alertTitle = "셋탑박스 연동 필요";
                        String alertMsg1  = mTitle;
                        String alertMsg2  = getString(R.string.error_not_paring_compleated3);
                        CMAlertUtil.Alert1(mInstance, alertTitle, alertMsg1, alertMsg2, true, false, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }, true);
                    } else {
                        requestAddRemoveWishItem("addWishItem");
                        Drawable img = getResources().getDrawable(R.mipmap.v_pick);
                        img.setBounds(0, 0, 35, 35);
                        mJimButton.setCompoundDrawables(null, null, img, null);
                        mJimButton.setText("찜해제");

                        Toast.makeText(VodDetailActivity.this, "찜 하기가 완료되었습니다. '마이 씨앤앰 > VOD 찜 목록'에서 확인하실 수 있습니다.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // 찜 한 VOD

                    requestAddRemoveWishItem("removeWishItem");
                    Drawable img = getResources().getDrawable(R.mipmap.v_unpick);
                    img.setBounds(0, 0, 35, 35);
                    mJimButton.setCompoundDrawables(null, null, img, null);
                    mJimButton.setText("찜하기");

                    Toast.makeText(VodDetailActivity.this, "찜 하기가 해제 되었습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });

        mJimButton2 = (Button)findViewById(R.id.vod_detail_jjim_button2);
        mJimButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( mPref.isWishAsset(assetId) == false ) {
                    // 찜 안한 VOD
                        if ( mPref.isPairingCompleted() == false ) {
                            String alertTitle = "셋탑박스 연동 필요";
                            String alertMsg1  = mTitle;
                            String alertMsg2  = getString(R.string.error_not_paring_compleated3);
                            CMAlertUtil.Alert1(mInstance, alertTitle, alertMsg1, alertMsg2, true, false, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }, true);
                        } else {
                            requestAddRemoveWishItem("addWishItem");
                            Drawable img = getResources().getDrawable(R.mipmap.v_pick);
                            img.setBounds(0, 0, 35, 35);
                            mJimButton2.setCompoundDrawables(null, null, img, null);
                            mJimButton2.setText("찜해제");

                            Toast.makeText(VodDetailActivity.this, "찜 하기가 완료되었습니다. '마이 씨앤앰 > VOD 찜 목록'에서 확인하실 수 있습니다.", Toast.LENGTH_LONG).show();
                        }
                } else {
                    // 찜 한 VOD
                    if ( mPref.isPairingCompleted() == false ) {
                        String alertTitle = "셋탑박스 연동 필요";
                        String alertMsg1  = mTitle;
                        String alertMsg2  = getString(R.string.error_not_paring_compleated3);
                        CMAlertUtil.Alert1(mInstance, alertTitle, alertMsg1, alertMsg2, true, false, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }, true);
                    }
                    requestAddRemoveWishItem("removeWishItem");
                    Drawable img = getResources().getDrawable(R.mipmap.v_unpick);
                    img.setBounds( 0, 0, 35, 35 );
                    mJimButton2.setCompoundDrawables(null, null, img, null);
                    mJimButton2.setText("찜하기");

                    Toast.makeText(VodDetailActivity.this, "찜 하기가 해제 되었습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });

        if ( mPref.isWishAsset(assetId) == false ) {
            // 찜 안한 VOD
            Drawable img = getResources().getDrawable(R.mipmap.v_unpick);
            img.setBounds(0, 0, 35, 35);
            mJimButton.setCompoundDrawables(null, null, img, null);
            mJimButton.setText("찜하기");
            mJimButton2.setCompoundDrawables(null, null, img, null);
            mJimButton2.setText("찜하기");
        } else {
            // 찜 한 VOD
            Drawable img = getResources().getDrawable(R.mipmap.v_pick);
            img.setBounds( 0, 0, 35, 35 );
            mJimButton.setCompoundDrawables( null, null, img, null );
            mJimButton.setText("찜해제");
            mJimButton2.setCompoundDrawables( null, null, img, null );
            mJimButton2.setText("찜해제");
        }


        // (HD)막돼먹은 영애씨 시즌14 02회(08/11
        // http://192.168.40.5:8080/HApplicationServer/getAssetInfo.xml?version=1&terminalKey=9CED3A20FB6A4D7FF35D1AC965F988D2&assetProfile=9&assetId=www.hchoice.co.kr%7CM4132449LFO281926301
        /**
         * VOD 상세정보 요청
         * episodePeerExistence
         */
        if ( "1".equals(episodePeerExistence) ) {
            // episodePeerExistence
            // contentGroupId
            isSeriesLink = "YES";
            mSeriesLinearLayout.setVisibility(View.VISIBLE);
            requestGetEpisodePeerListByContentGroupId();
        } else {
            isSeriesLink = "NO";
            mSeriesLinearLayout.setVisibility(View.GONE);
            requestGetAssetInfo();
        }



        /**
         * 화면 하단의 연관 VOD 요청
         */
        requestRecommendContentGroupByAssetId();
    }

    /**
     * 화면 전체 새로 고침.
     */
    /**
     *
     * @param aid assetId (getAssetInfo case)
     * @param cgid contentGroupId (getEpisodePeerListByContentGroupId case)
     */
    public void refreshAll(String aid, String cgid, String epid){
        assetId = aid;
        if ( cgid == null ) {
            contentGroupId = "";
        } else {
            contentGroupId = cgid;
        }
        if ( epid != null ) {
            episodePeerId = epid;
        }
        mSeriesReleaseDate = null;
        isPrePlay = true;
        relationVods.clear();
        mPagerAdapter.clear();
        mPagerAdapter.notifyDataSetChanged();

        LinearLayout ll = (LinearLayout)findViewById(R.id.vod_detail_series_linearlayout2);
        ll.removeAllViewsInLayout();

        Drawable img = getResources().getDrawable(R.mipmap.v_unpick);
        img.setBounds(0, 0, 35, 35);
        mJimButton.setCompoundDrawables(null, null, img, null);
        mJimButton.setText("찜하기");
        mJimButton2.setCompoundDrawables(null, null, img, null);
        mJimButton2.setText("찜하기");

        /**
         * VOD 상세정보 요청
         */
        if ( "1".equals(episodePeerExistence) ) {
            isSeriesLink = "YES";
            mSeriesLinearLayout.setVisibility(View.VISIBLE);
            requestGetAssetListByEpisodePeerId();
        } else {
            isSeriesLink = "NO";
            mSeriesLinearLayout.setVisibility(View.GONE);
            requestGetAssetInfo();
        }
    }

    /**
     * 시리즈 회차 변경(점프)
     * @param newAssetId
     */
    public void changeSeries(String newAssetId, String newepisodePeerId) {
        assetId = newAssetId;
        episodePeerId = newepisodePeerId;
        //contentGroupId = "";
        mSeriesReleaseDate = null;
        isPrePlay = true;
        relationVods.clear();
        mPagerAdapter.clear();
        mPagerAdapter.notifyDataSetChanged();
        //mTvOnlyTextView.setText("[] 은 (는)");
        //mTvOnlyLiearLayout.setVisibility(View.GONE);

        Drawable img = getResources().getDrawable(R.mipmap.v_unpick);
        img.setBounds(0, 0, 35, 35);
        mJimButton.setCompoundDrawables(null, null, img, null);
        mJimButton.setText("찜하기");
        mJimButton2.setCompoundDrawables(null, null, img, null);
        mJimButton2.setText("찜하기");

        requestGetAssetListByEpisodePeerId();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch(requestCode){
            case 1000: {    // 결제
                if ( resultCode == RESULT_OK ) {
                    // 결제가 완료됐으니, 전부 새로 고침.
                    String oldAssetId = assetId;
                    String oldContentGroupId = contentGroupId;
                    refreshAll(oldAssetId, contentGroupId, episodePeerId);
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

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    /**
     * productList중에 결재한적이 있는 결제시간 가져오기.
     * @return
     */
    private String getPurchasedTime(){
        try {
            for (int i = 0; i < productList.length(); i++) {
                JSONObject product = (JSONObject) productList.get(i);
                String purchasedTime = product.getString("purchasedTime");
                if ( purchasedTime.length() > 0 ) {
                    return purchasedTime;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void setUIAsset(JSONObject asset) {
        try {
            fileName                    = asset.getString("fileName");
            categoryId                  = asset.getString("categoryId");
            String imageFileName        = asset.getString("imageFileName");
            String rating               = asset.getString("rating");
            String reviewRatingCount    = asset.getString("reviewRatingCount");
            String reviewRatingTotal    = asset.getString("reviewRatingTotal");
            boolean HDContent           = asset.getBoolean("HDContent");
            String genre                = asset.getString("genre");
            String runningTime          = asset.getString("runningTime");
            String releaseDate          = "";
            if ( mSeriesReleaseDate != null && mSeriesReleaseDate.length() > 0) {
                releaseDate = " / " + mSeriesReleaseDate + " 방영";
            }
            String director             = asset.getString("director");
            String starring             = asset.getString("starring");
            String synopsis             = asset.getString("synopsis");
            String seriesId             = asset.getString("seriesId");
            boolean seriesLink          = asset.getBoolean("seriesLink");
            String promotionSticker     = asset.getString("promotionSticker");
            String title                = asset.getString("title");
            if ( mPref.isLogging() ) {
                Log.d(tag, "title: "+title);
            }
            String publicationRight     = asset.getString("publicationRight"); // 1: TV ONLY, 2 MOBILE

            discountCouponMasterIdList  = asset.getJSONArray("discountCouponMasterIdList");
            productList                 = asset.getJSONArray("productList");
            JSONObject product          = (JSONObject)productList.get(0);
            productType                 = product.getString("productType");
            productId                   = product.getString("productId");
            goodId                      = product.getString("goodId");
            Integer viewablePeriodState = product.getInt("viewablePeriodState");
            String viewablePeriod       = product.getString("viewablePeriod");


            Boolean isNew                = false; // 0:없음, 1:있음.
            Object isNewObj             = asset.get("isNew");
            if ( isNewObj != null ) { isNew = asset.getBoolean("isNew"); }
            String assetNew          = "0"; // 0:없음, 1:new일부만, 2:new단체
            if ( ! asset.isNull("assetNew") ) {
                assetNew = asset.getString("assetNew");
            }
            String assetHot          = "0"; // 0:없음, 1:new일부만, 2:new단체
            if ( ! asset.isNull("assetHot") ) {
                assetHot = asset.getString("assetHot");
            }
            Boolean hot               = false; // 0:없음, 1:있음.
            if ( ! asset.isNull("hot") ) {
                hot = asset.getBoolean("hot");
            }

            String runningTimeMinute = String.valueOf((Integer.parseInt(runningTime.substring(0, 2)) * 60) + Integer.parseInt(runningTime.substring(3))) + "분";
            runningTime = runningTimeMinute;

            String previewPeriod     = asset.getString("previewPeriod");      // 미리보기 시간.

            // productList
            //JSONArray productList    = asset.getJSONArray("productList");
            //JSONObject product       = productLists.getJSONObject(0);
            String price             = product.getString("price");
            String listPrice         = product.getString("listPrice");
            String purchasedId       = product.getString("purchasedId");

            // LinearLayout 감추기/보이기 -----------------------------------------------------
            // mSeriesLinearLayout   // 시리즈 회차 버튼
            // mPurchaseLinearLayout // 미리비기/구매하기/찜하기
            // mPlayLinearLayout     // 시청하기
            // mTvOnlyLiearLayout    // TV에서 시청가능합니다.
            /***************************************************************************************
            if ( seriesLink == true ) {      // 시리즈 보여라
                isSeriesLink = "YES";
                mSeriesLinearLayout.setVisibility(View.VISIBLE);

                //if ( episodePeerExistence.length() > 0 ) {
                    //
                //} else {
                    // 기존의 getAssetInfo를 통해서 시리즈 표시하는 방법.
                    String sCategoryId = asset.getString("categoryId");
                    String sSeriesId   = asset.getString("seriesId");
                    requestGetSeriesAssetList(sSeriesId, sCategoryId);
                //}
            } else {                         // 시리즈 감춰라.
                isSeriesLink = "NO";
                mSeriesLinearLayout.setVisibility(View.GONE);
            }
             **************************************************************************************/
            if ( "".equals(getPurchasedTime()) ) { // 구매하기 보여랴
                mPlayLinearLayout.setVisibility(View.GONE);
                mTvOnlyLiearLayout.setVisibility(View.GONE);
                if ( ! "2".equals(publicationRight) ) {
                    mPurchaseLinearLayout2.setVisibility(View.VISIBLE);
                    mPurchaseLinearLayout.setVisibility(View.GONE);
                } else {
                    mPurchaseLinearLayout.setVisibility(View.VISIBLE);
                    mPurchaseLinearLayout2.setVisibility(View.GONE);
                }
                // 에외처리. productType 이 FOD(무료시청)일 경우는 구매하지 않았더라도, 시청하기 보여라.
                if ( "FOD".equals(productType) ) {
                    mPurchaseLinearLayout.setVisibility(View.GONE);
                    mPurchaseLinearLayout2.setVisibility(View.GONE);
                    if ( ! "2".equals(publicationRight) ) { // 1: TV ONLY, 2 MOBILE
                        mTvOnlyTextView.setText("["+title+"] 은 (는)");
                        mTvOnlyLiearLayout.setVisibility(View.VISIBLE);
                    } else {
                        mPlayLinearLayout.setVisibility(View.VISIBLE);
                    }
                }
            } else {                         // 구매했다. 감쳐라.
                mPurchaseLinearLayout2.setVisibility(View.GONE);
                mPurchaseLinearLayout.setVisibility(View.GONE);
                if ( ! "2".equals(publicationRight) ) { // 1: TV ONLY, 2 MOBILE
                    mTvOnlyTextView.setText("["+title+"] 은 (는)");
                    mTvOnlyLiearLayout.setVisibility(View.VISIBLE);
                } else {
                    mPlayLinearLayout.setVisibility(View.VISIBLE);
                }
            }


            // 값들 찍어주기. -----------------------------------------------------------------

            mMovieImageImageView.setImageUrl(imageFileName, mImageLoader);

            UiUtil.setPromotionSticker(promotionSticker, isNew, hot, assetNew, assetHot, mPromotionSticker);

            if( viewablePeriodState == 1 ) {
                mViewableTextView.setText("무제한시청");
                viewable = "무제한시청";
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
            if ( "".equals(getPurchasedTime()) ) {
                mPriceTextView.setText(UiUtil.stringParserCommafy(price) + "원 [부가세 별도]");
                String strColor = "#000000";
                mPriceTextView.setTextColor(Color.parseColor(strColor));
            } else {
                mPriceTextView.setText("이미 구매하셨습니다.");
                String strColor = "#7b5aa3";
                mPriceTextView.setTextColor(Color.parseColor(strColor));
            }
            // 에외처리. productType 이 FOD(무료시청)일 경우는 구매하지 않았더라도, 시청하기 보여라.
            if ( "FOD".equals(productType) || "0".equals(price) ) {
                mPriceTextView.setText("무료시청");
            }

            mGenreTextView.setText(genre+" / "+runningTime + releaseDate);
            mDirectorTextView.setText(director);
            mStarringTextView.setText(starring);
            mSynopsisTextView.setText(synopsis);
            if ( "2".equals(publicationRight) ) {
                mMobileImageView.setVisibility(View.VISIBLE);
            }
        } catch ( JSONException e ) {
            e.printStackTrace();
        }
    }

    private void setUISeriesButton(JSONArray assetList) {
        LinearLayout lll = (LinearLayout)findViewById(R.id.vod_detail_series_linearlayout2);
        lll.removeAllViewsInLayout();
        try {
//            int iLoop;
//            if ( seriesTotalAssetCount.equals(seriesEndIndex) ) { // 종료된 시리즈.
//                iLoop = assetList.length()-1;
//            } else { // 종료되지 않은 시리즈.
//                iLoop = 0;
//            }
            for ( int i = 0; i < assetList.length(); i++ ) {
                JSONObject asset = (JSONObject)assetList.get(i);
                final String thisAssetId;
                String thisContentGroupId = null;
                int thisSeriesIndex = 0;
                if ( asset.isNull("assetId") ) {
                    thisAssetId        = asset.getString("primaryAssetId");
                    thisContentGroupId = asset.getString("contentGroupId");
                    thisSeriesIndex    = asset.getInt("seriesIndex");
                } else {
                    thisAssetId           = asset.getString("assetId");
                    thisSeriesIndex       = asset.getInt("seriesCurIndex");
                    seriesEndIndex        = asset.getString("seriesEndIndex");
                    seriesTotalAssetCount = asset.getString("seriesTotalAssetCount");
                }

                Button seriesButton = (Button) getLayoutInflater().inflate(R.layout.series_button_style, null);
                seriesButton.setText(thisSeriesIndex + "회");

                if ( assetId.equals(thisAssetId) ) {
                    seriesButton.setSelected(true);
                    seriesButton.setFocusable(true);
                }

                final String finalThisContentGroupId = thisContentGroupId;
                final String episodePeerId = asset.getString("episodePeerId");
                seriesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        if ( isSeriesLink.equals("YES") ) {
//                            refreshAll(thisAssetId, finalThisContentGroupId);
//                        } else {
//                            Intent intent = new Intent(mInstance, VodDetailActivity.class);
//                            intent.putExtra("assetId", thisAssetId);
//                            startActivity(intent);
//                        }
                        changeSeries(thisAssetId,episodePeerId);
                    }
                });

                LinearLayout ll = (LinearLayout)findViewById(R.id.vod_detail_series_linearlayout2);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(dp2px(8),0,dp2px(8),0);
                ll.addView(seriesButton, lp);
                seriesButton.getLayoutParams().width  = dp2px(42);
                seriesButton.getLayoutParams().height = dp2px(28);

//                if ( seriesTotalAssetCount.equals(seriesEndIndex) ) { // 종료된 시리즈.
//                    iLoop--;
//                    mSeriesScrollView.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            mSeriesScrollView.fullScroll(ScrollView.FOCUS_LEFT); // 1회를 표시한다.
//                        }
//                    });
//                } else { // 종료되지 않은 시리즈.
//                    iLoop++;
//                    mSeriesScrollView.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            mSeriesScrollView.fullScroll(ScrollView.FOCUS_RIGHT); // 가장 최근 회를 표시한다.
//                        }
//                    });
//                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
        String url = mPref.getWebhasServerUrl() + "/getAssetInfo.json?version=1&terminalKey="+terminalKey+"&assetProfile=9&assetId="+encAssetId;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(tag, response);
                mProgressDialog.dismiss();
                try {
                    JSONObject jo         = new JSONObject(response);
                    // asset
                    JSONObject asset      = jo.getJSONObject("asset");

                    seriesCurIndex        = asset.getString("seriesCurIndex");
                    seriesEndIndex        = asset.getString("seriesEndIndex");
                    seriesTotalAssetCount = asset.getString("seriesTotalAssetCount");

                    setUIAsset(asset);
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
        String url = mPref.getWebhasServerUrl() + "/recommendContentGroupByAssetId.json?version=1&terminalKey="+terminalKey+"&assetId="+encAssetId+"&contentGroupProfile=2&recommendField=contentsCF";
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
                    if ( mViewPager.getAdapter() == null ) {
                        mViewPager.setAdapter(mPagerAdapter);
                    }
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

    /**
     * 1. getEpisodePeerListByContentGroupId
     */
    private void requestGetEpisodePeerListByContentGroupId() {
        //mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestGetEpisodePeerListByContentGroupId()"); }
        String terminalKey = mPref.getWebhasTerminalKey();
        // /getEpisodePeerListByContentGroupId.xml?version=1&terminalKey=598C9A357473F582E49EB8389FFBC0&contentGroupId=497612&episodePeerProfile=2
        String url = mPref.getWebhasServerUrl() + "/getEpisodePeerListByContentGroupId.json?"
                + "version=1"
                + "&terminalKey="+terminalKey
                + "&sortType=notSet"
                + "&contentGroupId="+contentGroupId
                + "&episodePeerProfile=2";
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(tag, response);
                //mProgressDialog.dismiss();
                try {
                    JSONObject jo = new JSONObject(response);
                    episodePeerList = jo.getJSONArray("episodePeerList");
                    //setUISeriesButton(episodePeerList);
                    for ( int i = 0; i < episodePeerList.length(); i++ ) {
                        JSONObject episodePeer = episodePeerList.getJSONObject(i);
                        String loopPrimaryAssetId  = episodePeer.getString("primaryAssetId");
                        if ( primaryAssetId.equals(loopPrimaryAssetId) ) {
                            mSeriesReleaseDate = episodePeer.getString("releaseDate");
                            episodePeerId      = episodePeer.getString("episodePeerId");
                            requestGetAssetListByEpisodePeerId();
                        }
                    }
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

    /**
     * getAssetListByEpisodePeerId으로 받아온 assetList중에 HD어셋을 뽑아낸다.
     * 만약 HD가 없으면, SD
     * @param assetList
     * @return
     */
    private JSONObject getHDAsset(JSONArray assetList) {
        JSONObject rtn = null;
        try {
            for (int i = 0; i < assetList.length(); i++) {
                JSONObject jo = (JSONObject)assetList.get(i);
                if ( jo.getBoolean("HDContent") == true ) {
                    rtn = jo;
                    break;
                }
            }
            if ( rtn == null ) { // HD 못찾았으면 그냥 처음꺼 쓰자.
                rtn = (JSONObject)assetList.get(0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rtn;
    }

    private void requestGetAssetListByEpisodePeerId() {
        //mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestGetAssetListByEpisodePeerId()"); }
        String terminalKey = mPref.getWebhasTerminalKey();
        // /getEpisodePeerListByContentGroupId.xml?version=1&terminalKey=598C9A357473F582E49EB8389FFBC0&contentGroupId=497612&episodePeerProfile=2
        String url = mPref.getWebhasServerUrl() + "/getAssetListByEpisodePeerId.json?"
                + "version=1"
                + "&terminalKey="+terminalKey
                + "&sortType=notSet"
                + "&episodePeerId="+episodePeerId
                + "&assetProfile=9";
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(tag, response);
                //mProgressDialog.dismiss();
                try {
                    JSONObject jo        = new JSONObject(response);
                    JSONArray  assetList = jo.getJSONArray("assetList");
                    JSONObject asset     = getHDAsset(assetList);

                    seriesCurIndex        = asset.getString("seriesCurIndex");
                    seriesEndIndex        = asset.getString("seriesEndIndex");
                    seriesTotalAssetCount = asset.getString("seriesTotalAssetCount");

                    setUIAsset(asset);
                    setUISeriesButton(episodePeerList);

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

    /**
     * 찜하기에 추가/제거
     * http://58.141.255.79:8080/HApplicationServer/addWishItem.json?version=1&terminalKey=B2F311C9641A0CCED9C7FE95BE624D9&transactionId=1&assetId=www.hchoice.co.kr|M4166179LSG353388601
     * http://58.141.255.79:8080/HApplicationServer/removeWishItem.json?version=1&terminalKey=B2F311C9641A0CCED9C7FE95BE624D9&transactionId=1&assetId=www.hchoice.co.kr|M4166179LSG353388601
     */
    private void requestAddRemoveWishItem(String action) {
        // mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestAddRemoveWishItem()"); }
        String terminalKey = mPref.getWebhasTerminalKey();
        String uuid = mPref.getValue(JYSharedPreferences.UUID, "");
        String encAssetId  = null;
        try {
            encAssetId  = URLDecoder.decode(assetId, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = mPref.getWebhasServerUrl() + "/"+action+".json?version=1&terminalKey="+terminalKey+"&assetId="+encAssetId + "&userId=" + uuid;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // mProgressDialog.dismiss();
                try {
                    JSONObject jo      = new JSONObject(response);
                    String resultCode  = jo.getString("resultCode");

                    if ( Constants.CODE_WEBHAS_OK.equals(resultCode) ) {
                        requestGetWishList();
                    } else {
                        String errorString = jo.getString("errorString");
                        StringBuilder sb   = new StringBuilder();
                        sb.append("API: action\nresultCode: ").append(resultCode).append("\nerrorString: ").append(errorString);
                        AlertDialog.Builder alert = new AlertDialog.Builder(mInstance);
                        alert.setPositiveButton("알림", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alert.setMessage(sb.toString());
                        alert.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mProgressDialog.dismiss();
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
     * 찜하기 리스트 받기
     * http://58.141.255.79:8080/HApplicationServer/getWishList.json?version=1&terminalKey=B2F311C9641A0CCED9C7FE95BE624D9&transactionId=1
     */
    private void requestGetWishList() {
        // mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestGetWishList()"); }
        String terminalKey = mPref.getWebhasTerminalKey();
        String url = mPref.getWebhasServerUrl() + "/getWishList.json?version=1&terminalKey="+terminalKey;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // mProgressDialog.dismiss();
                try {
                    JSONObject jo      = new JSONObject(response);
                    String resultCode  = jo.getString("resultCode");

                    if ( Constants.CODE_WEBHAS_OK.equals(resultCode) ) {
                        JSONArray arr  = jo.getJSONArray("wishItemList");
                        mPref.setAllWishList(arr);
                    } else {
                        String errorString = jo.getString("errorString");
                        StringBuilder sb   = new StringBuilder();
                        sb.append("API: action\nresultCode: ").append(resultCode).append("\nerrorString: ").append(errorString);
                        AlertDialog.Builder alert = new AlertDialog.Builder(mInstance);
                        alert.setPositiveButton("알림", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alert.setMessage(sb.toString());
                        alert.show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mProgressDialog.dismiss();
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
        String action = "preview";
        if ( isPrePlay == true ) {
            action = "preview";
        } else {
            action = "play";
        }
        String url = "https://api.cablevod.co.kr/api/v1/mso/10/asset/"+fileName+"/"+action;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(tag, response);
                mProgressDialog.dismiss();
                try {
                    JSONObject jo     = new JSONObject(response);
                    JSONObject header = jo.getJSONObject("header");
                    int resultCode    = header.getInt("resultCode");
                    if ( resultCode != 0 ) {
                        String showMessage = header.getString("showMessage");
                        String resultMessages = header.getString("resultMessages");

                        String alertTitle = "VOD 시청 안내";
                        String alertMsg1 = getString(R.string.error_not_play_vod);
                        String alertMsg2 = "";
                        CMAlertUtil.Alert1(mInstance, alertTitle, alertMsg1, alertMsg2, true, false, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }, true);
                    } else {
                        JSONObject drm    = jo.getJSONObject("drm");
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

                        // VOD시청목록 남기기
                        Date watchDate = new Date();
                        mPref.addWatchVod(watchDate, assetId, mTitle);

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
                        if ( isPrePlay == true ) {
                            intent.putExtra("isPrePlay", "YES");
                        } else {
                            intent.putExtra("isPrePlay", "NO");
                        }
                        intent.putExtra("title", mTitle);
                        startActivityForResult(intent, 111);
                        //startActivity(intent);
                    }

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

    /***********************************************************************************************
    // 기존의 getAssetInfo를 통해서 시리즈 표시하는 방법.
    private void requestGetSeriesAssetList(String seriesId, String categoryId) {
        mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestGetSeriesAssetList()"); }
        String terminalKey = mPref.getWebhasTerminalKey();
        try {
            seriesId = URLDecoder.decode(seriesId, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = mPref.getWebhasServerUrl() + "/getSeriesAssetList.json?version=1&terminalKey="+terminalKey
                + "&sortType=notSet"
                + "&seriesId="+seriesId
                + "&categoryId="+categoryId
                + "&assetProfile=3";

        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                try {
                    JSONObject jo            = new JSONObject(response);
                    JSONArray  assetList     = jo.getJSONArray("assetList");

                    setUISeriesButton(assetList);

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
     **********************************************************************************************/
}
