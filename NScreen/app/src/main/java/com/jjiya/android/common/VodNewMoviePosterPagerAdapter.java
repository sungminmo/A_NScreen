package com.jjiya.android.common;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.stvn.nscreen.R;
import com.stvn.nscreen.setting.CMSettingMainActivity;
import com.stvn.nscreen.util.CMAlertUtil;
import com.stvn.nscreen.vod.VodDetailActivity;
import com.stvn.nscreen.vod.VodMainFirstTabFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.jjiya.android.common.UiUtil.setPromotionSticker;

/**
 * Created by limdavid on 15. 10. 22..
 */
public class VodNewMoviePosterPagerAdapter extends PagerAdapter {

    private LayoutInflater   mLayoutInflater;
    private JYSharedPreferences mPref;

    private ImageLoader      mImageLoader;
    private List<JSONObject> mVods;

    private Fragment         mFragment;

    public VodNewMoviePosterPagerAdapter(Context c){
        super();
        mLayoutInflater = LayoutInflater.from(c);
        mVods           = new ArrayList<JSONObject>();
        mPref           = new JYSharedPreferences(c);
    }

    public void clear() {
        mVods.clear();
    }

    public void setFragment(Fragment f) {
        mFragment = f;
    }

    /**
     * 디폴트 메소드 구현 부.
     */

    @Override
    public int getCount() {
        int iCount = mVods.size() / 8;
        if ( mVods.size() % 8 > 0 ) {
            iCount++;
        }
        return iCount;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        //return false;
        return view == object;
    }

    @Override
    public void destroyItem(View pager, int position, Object view) {
        ((ViewPager)pager).removeView((View)view);
    }

    public void setImageLoader(ImageLoader il){
        this.mImageLoader = il;
    }

    public void addVod(JSONObject jo){
        mVods.add(jo);
    }

    /**
     * swlim
     */

    @Override
    public Object instantiateItem(View pager, int position) {
        View v = null;

        v = mLayoutInflater.inflate(R.layout.viewpager_vod_eight_vod_poster, null);
        try {
            int index1 = position*8+0;
            int index2 = position*8+1;
            int index3 = position*8+2;
            int index4 = position*8+3;
            int index5 = position*8+4;
            int index6 = position*8+5;
            int index7 = position*8+6;
            int index8 = position*8+7;

            if ( mVods.size() > index1 ) {
                TextView title1 =  (TextView)v.findViewById(R.id.eight_vod_poster_title_textview1);
                ImageView eight_vod_poster_promotionsticker_imageview1 = (ImageView)v.findViewById(R.id.eight_vod_poster_promotionsticker_imageview1);
                NetworkImageView niv11 = (NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview1);
                niv11.setDefaultImageResId(R.mipmap.posterlist_default);
                ImageView eight_vod_poster_tvonly_imageview1 = (ImageView)v.findViewById(R.id.eight_vod_poster_tvonly_imageview1);
                ImageView eight_vod_poster_19_imageview1 = (ImageView)v.findViewById(R.id.eight_vod_poster_19_imageview1);
                ((ImageView)v.findViewById(R.id.eight_vod_poster_rank_imageview1)).setVisibility(View.INVISIBLE);
                ((TextView)v.findViewById(R.id.eight_vod_poster_rank_textview1)).setVisibility(View.INVISIBLE);

                final JSONObject jo1 = mVods.get(index1);
                final String assetId1 = jo1.getString("primaryAssetId");
                title1.setText(jo1.getString("title"));
                String imageFileName1 = jo1.getString("imageFileName");
                String promotionSticker1 = jo1.getString("promotionSticker");

                boolean isMobilePublicationRight = false;
                String easyMobilePublicationRight = "";
                if ( jo1.isNull("mobilePublicationRight") ) {
                    easyMobilePublicationRight = jo1.getString("publicationRight");
                    if ( "2".equals(easyMobilePublicationRight) ) {  // 모바일 판권 있다.
                        isMobilePublicationRight = true;
                    }
                } else {
                    isMobilePublicationRight = jo1.getBoolean("mobilePublicationRight");
                }

                final String rating1 = jo1.getString("rating");
                Boolean isNew1                = false; // 0:없음, 1:있음.
                if ( ! jo1.isNull("isNew") ) {
                    isNew1 = jo1.getBoolean("isNew");
                }
                String assetNew1             = "0"; // 0:없음, 1:new일부만, 2:new단체
                if ( ! jo1.isNull("assetNew") ) {
                    assetNew1 = jo1.getString("assetNew");
                }
                String assetHot1             = "0"; // 0:없음, 1:new일부만, 2:new단체
                if ( ! jo1.isNull("assetHot") ) {
                    assetHot1 = jo1.getString("assetHot");
                }
                Boolean hot1                  = false; // 0:없음, 1:있음.
                if ( ! jo1.isNull("hot") ) {
                    hot1 = jo1.getBoolean("hot");
                }
                niv11.setImageUrl(imageFileName1, mImageLoader);
                niv11.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( rating1.startsWith("19") && mPref.isAdultVerification() == false ) {
                            String alertTitle = "성인인증 필요";
                            String alertMsg1 = mFragment.getActivity().getResources().getString(R.string.error_not_adult1);
                            String alertMsg2 = mFragment.getActivity().getResources().getString(R.string.error_not_adult2);
                            CMAlertUtil.Alert1(mFragment.getActivity(), alertTitle, alertMsg1, alertMsg2, false, true, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(mFragment.getActivity(), CMSettingMainActivity.class);
                                    mFragment.getActivity().startActivity(intent);
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                        } else {
                            if ( jo1.isNull("episodePeerExistence") ) {
                                Intent intent = new Intent(mFragment.getActivity(), VodDetailActivity.class);
                                intent.putExtra("assetId", assetId1);
                                intent.putExtra("jstr", jo1.toString());
                                mFragment.getActivity().startActivity(intent);
                            } else {
                                try {
                                    int     assetBundle          = jo1.getInt("assetBundle");
                                    String  episodePeerExistence = jo1.getString("episodePeerExistence");
                                    if ( assetBundle == 1 ) {  // 번들(묶음상품)이면 묶음상품이면, getAssetInfo로 구매여부를 알아낸다.
                                        ((VodMainFirstTabFragment)mFragment).onClickBundulPoster(assetId1);
                                    } else {  // 이외는 번들(묶음상품)이 아니다.
                                        Intent intent = new Intent(mFragment.getActivity(), VodDetailActivity.class);
                                        if ( "1".equals(episodePeerExistence) ) {
                                            String contentGroupId = jo1.getString("contentGroupId");
                                            String primaryAssetId = jo1.getString("primaryAssetId");
                                            intent.putExtra("episodePeerExistence", episodePeerExistence);
                                            intent.putExtra("contentGroupId", contentGroupId);
                                            intent.putExtra("primaryAssetId", primaryAssetId);
                                            intent.putExtra("assetId", assetId1);
                                            intent.putExtra("jstr", jo1.toString());
                                            mFragment.getActivity().startActivity(intent);
                                        } else {
                                            intent.putExtra("assetId", assetId1);
                                            intent.putExtra("jstr", jo1.toString());
                                            mFragment.getActivity().startActivity(intent);
                                        }
                                    }
                                } catch ( JSONException e ) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });

                setPromotionSticker(promotionSticker1, isNew1, hot1, assetNew1, assetHot1, eight_vod_poster_promotionsticker_imageview1);
                if ( isMobilePublicationRight == false ) {  // 모바일 판권이 없으니, TV전용 이미지를 보여라.
                    eight_vod_poster_tvonly_imageview1.setVisibility(View.VISIBLE);
                }
                if ( rating1.startsWith("19") && mPref.isAdultVerification() == false ) {   //if ( "19".equals(rating2) ) {
                    eight_vod_poster_19_imageview1.setVisibility(View.VISIBLE);
                }
            } else {
                ((TextView)v.findViewById(R.id.eight_vod_poster_title_textview1)).setVisibility(View.INVISIBLE);
                ((NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview1)).setVisibility(View.INVISIBLE);
                ((ImageView)v.findViewById(R.id.eight_vod_poster_rank_imageview1)).setVisibility(View.INVISIBLE);
                ((TextView)v.findViewById(R.id.eight_vod_poster_rank_textview1)).setVisibility(View.INVISIBLE);
                ((ImageView)v.findViewById(R.id.eight_vod_poster_promotionsticker_imageview1)).setVisibility(View.INVISIBLE);
                ((ImageView)v.findViewById(R.id.eight_vod_poster_tvonly_imageview1)).setVisibility(View.INVISIBLE);
                ((ImageView)v.findViewById(R.id.eight_vod_poster_19_imageview1)).setVisibility(View.INVISIBLE);
            }

            if ( mVods.size() > index2 ) {
                TextView title2 =  (TextView)v.findViewById(R.id.eight_vod_poster_title_textview2);
                ImageView eight_vod_poster_promotionsticker_imageview2 = (ImageView)v.findViewById(R.id.eight_vod_poster_promotionsticker_imageview2);
                NetworkImageView niv21 = (NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview2);
                niv21.setDefaultImageResId(R.mipmap.posterlist_default);
                ImageView eight_vod_poster_tvonly_imageview2 = (ImageView)v.findViewById(R.id.eight_vod_poster_tvonly_imageview2);
                ImageView eight_vod_poster_19_imageview2 = (ImageView)v.findViewById(R.id.eight_vod_poster_19_imageview2);

                final JSONObject jo2 = mVods.get(index2);
                final String assetId2 = jo2.getString("primaryAssetId");
                ((ImageView)v.findViewById(R.id.eight_vod_poster_rank_imageview2)).setVisibility(View.INVISIBLE);
                ((TextView)v.findViewById(R.id.eight_vod_poster_rank_textview2)).setVisibility(View.INVISIBLE);
                title2.setText(jo2.getString("title"));
                String imageFileName2 = jo2.getString("imageFileName");
                String promotionSticker2 = jo2.getString("promotionSticker");

                boolean isMobilePublicationRight = false;
                String easyMobilePublicationRight = "";
                if ( jo2.isNull("mobilePublicationRight") ) {
                    easyMobilePublicationRight = jo2.getString("publicationRight");
                    if ( "2".equals(easyMobilePublicationRight) ) {  // 모바일 판권 있다.
                        isMobilePublicationRight = true;
                    }
                } else {
                    isMobilePublicationRight = jo2.getBoolean("mobilePublicationRight");
                }

                final String rating2 = jo2.getString("rating");
                Boolean isNew2                = false; // 0:없음, 1:있음.
                if ( ! jo2.isNull("isNew") ) {
                    isNew2 = jo2.getBoolean("isNew");
                }
                String assetNew2             = "0"; // 0:없음, 1:new일부만, 2:new단체
                if ( ! jo2.isNull("assetNew") ) {
                    assetNew2 = jo2.getString("assetNew");
                }
                String assetHot2             = "0"; // 0:없음, 1:new일부만, 2:new단체
                if ( ! jo2.isNull("assetHot") ) {
                    assetHot2 = jo2.getString("assetHot");
                }
                Boolean hot2                  = false; // 0:없음, 1:있음.
                if ( ! jo2.isNull("hot") ) {
                    hot2 = jo2.getBoolean("hot");
                }
                niv21.setImageUrl(imageFileName2, mImageLoader);
                niv21.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( rating2.startsWith("19") && mPref.isAdultVerification() == false ) {
                            String alertTitle = "성인인증 필요";
                            String alertMsg1 = mFragment.getActivity().getResources().getString(R.string.error_not_adult1);
                            String alertMsg2 = mFragment.getActivity().getResources().getString(R.string.error_not_adult2);
                            CMAlertUtil.Alert1(mFragment.getActivity(), alertTitle, alertMsg1, alertMsg2, false, true, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(mFragment.getActivity(), CMSettingMainActivity.class);
                                    mFragment.getActivity().startActivity(intent);
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                        } else {
                            if ( jo2.isNull("episodePeerExistence") ) {
                                Intent intent = new Intent(mFragment.getActivity(), VodDetailActivity.class);
                                intent.putExtra("assetId", assetId2);
                                intent.putExtra("jstr", jo2.toString());
                                mFragment.getActivity().startActivity(intent);
                            } else {
                                try {
                                    int     assetBundle          = jo2.getInt("assetBundle");
                                    String  episodePeerExistence = jo2.getString("episodePeerExistence");
                                    if ( assetBundle == 1 ) {  // 번들(묶음상품)이면 묶음상품이면, getAssetInfo로 구매여부를 알아낸다.
                                        ((VodMainFirstTabFragment)mFragment).onClickBundulPoster(assetId2);
                                    } else {  // 이외는 번들(묶음상품)이 아니다.
                                        Intent intent = new Intent(mFragment.getActivity(), VodDetailActivity.class);
                                        if ( "1".equals(episodePeerExistence) ) {
                                            String contentGroupId = jo2.getString("contentGroupId");
                                            String primaryAssetId = jo2.getString("primaryAssetId");
                                            intent.putExtra("episodePeerExistence", episodePeerExistence);
                                            intent.putExtra("contentGroupId", contentGroupId);
                                            intent.putExtra("primaryAssetId", primaryAssetId);
                                            intent.putExtra("assetId", assetId2);
                                            intent.putExtra("jstr", jo2.toString());
                                            mFragment.getActivity().startActivity(intent);
                                        } else {
                                            intent.putExtra("assetId", assetId2);
                                            intent.putExtra("jstr", jo2.toString());
                                            mFragment.getActivity().startActivity(intent);
                                        }
                                    }
                                } catch ( JSONException e ) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });

                setPromotionSticker(promotionSticker2, isNew2, hot2, assetNew2, assetHot2, eight_vod_poster_promotionsticker_imageview2);
                if ( isMobilePublicationRight == false ) {
                    eight_vod_poster_tvonly_imageview2.setVisibility(View.VISIBLE);
                }
                if ( rating2.startsWith("19") && mPref.isAdultVerification() == false ) {   //if ( "19".equals(rating2) ) {
                    eight_vod_poster_19_imageview2.setVisibility(View.VISIBLE);
                }
            } else {
                ((TextView)v.findViewById(R.id.eight_vod_poster_title_textview2)).setVisibility(View.INVISIBLE);
                ((NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview2)).setVisibility(View.INVISIBLE);
                ((ImageView)v.findViewById(R.id.eight_vod_poster_rank_imageview2)).setVisibility(View.INVISIBLE);
                ((TextView)v.findViewById(R.id.eight_vod_poster_rank_textview2)).setVisibility(View.INVISIBLE);
                ((ImageView)v.findViewById(R.id.eight_vod_poster_promotionsticker_imageview2)).setVisibility(View.INVISIBLE);
                ((ImageView)v.findViewById(R.id.eight_vod_poster_tvonly_imageview2)).setVisibility(View.INVISIBLE);
                ((ImageView)v.findViewById(R.id.eight_vod_poster_19_imageview2)).setVisibility(View.INVISIBLE);
            }

            if ( mVods.size() > index3 ) {
                TextView title3 =  (TextView)v.findViewById(R.id.eight_vod_poster_title_textview3);
                ImageView eight_vod_poster_promotionsticker_imageview3 = (ImageView)v.findViewById(R.id.eight_vod_poster_promotionsticker_imageview3);
                NetworkImageView niv31 = (NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview3);
                niv31.setDefaultImageResId(R.mipmap.posterlist_default);
                ImageView eight_vod_poster_tvonly_imageview3 = (ImageView)v.findViewById(R.id.eight_vod_poster_tvonly_imageview3);
                ImageView eight_vod_poster_19_imageview3 = (ImageView)v.findViewById(R.id.eight_vod_poster_19_imageview3);

                final JSONObject jo3 = mVods.get(index3);
                final String assetId3 = jo3.getString("primaryAssetId");
                ((ImageView)v.findViewById(R.id.eight_vod_poster_rank_imageview3)).setVisibility(View.INVISIBLE);
                ((TextView)v.findViewById(R.id.eight_vod_poster_rank_textview3)).setVisibility(View.INVISIBLE);
                title3.setText(jo3.getString("title"));
                String imageFileName3 = jo3.getString("imageFileName");
                String promotionSticker3 = jo3.getString("promotionSticker");

                boolean isMobilePublicationRight = false;
                String easyMobilePublicationRight = "";
                if ( jo3.isNull("mobilePublicationRight") ) {
                    easyMobilePublicationRight = jo3.getString("publicationRight");
                    if ( "2".equals(easyMobilePublicationRight) ) {  // 모바일 판권 있다.
                        isMobilePublicationRight = true;
                    }
                } else {
                    isMobilePublicationRight = jo3.getBoolean("mobilePublicationRight");
                }

                final String rating3 = jo3.getString("rating");
                Boolean isNew3                = false; // 0:없음, 1:있음.
                if ( ! jo3.isNull("isNew") ) {
                    isNew3 = jo3.getBoolean("isNew");
                }
                String assetNew3             = "0"; // 0:없음, 1:new일부만, 2:new단체
                if ( ! jo3.isNull("assetNew") ) {
                    assetNew3 = jo3.getString("assetNew");
                }
                String assetHot3             = "0"; // 0:없음, 1:new일부만, 2:new단체
                if ( ! jo3.isNull("assetHot") ) {
                    assetHot3 = jo3.getString("assetHot");
                }
                Boolean hot3                  = false; // 0:없음, 1:있음.
                if ( ! jo3.isNull("hot") ) {
                    hot3 = jo3.getBoolean("hot");
                }
                niv31.setImageUrl(imageFileName3, mImageLoader);
                niv31.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( rating3.startsWith("19") && mPref.isAdultVerification() == false ) {
                            String alertTitle = "성인인증 필요";
                            String alertMsg1 = mFragment.getActivity().getResources().getString(R.string.error_not_adult1);
                            String alertMsg2 = mFragment.getActivity().getResources().getString(R.string.error_not_adult2);
                            CMAlertUtil.Alert1(mFragment.getActivity(), alertTitle, alertMsg1, alertMsg2, false, true, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(mFragment.getActivity(), CMSettingMainActivity.class);
                                    mFragment.getActivity().startActivity(intent);
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                        } else {
                            if ( jo3.isNull("episodePeerExistence") ) {
                                Intent intent = new Intent(mFragment.getActivity(), VodDetailActivity.class);
                                intent.putExtra("assetId", assetId3);
                                intent.putExtra("jstr", jo3.toString());
                                mFragment.getActivity().startActivity(intent);
                            } else {
                                try {
                                    int     assetBundle          = jo3.getInt("assetBundle");
                                    String  episodePeerExistence = jo3.getString("episodePeerExistence");
                                    if ( assetBundle == 1 ) {  // 번들(묶음상품)이면 묶음상품이면, getAssetInfo로 구매여부를 알아낸다.
                                        ((VodMainFirstTabFragment)mFragment).onClickBundulPoster(assetId3);
                                    } else {  // 이외는 번들(묶음상품)이 아니다.
                                        Intent intent = new Intent(mFragment.getActivity(), VodDetailActivity.class);
                                        if ( "1".equals(episodePeerExistence) ) {
                                            String contentGroupId = jo3.getString("contentGroupId");
                                            String primaryAssetId = jo3.getString("primaryAssetId");
                                            intent.putExtra("episodePeerExistence", episodePeerExistence);
                                            intent.putExtra("contentGroupId", contentGroupId);
                                            intent.putExtra("primaryAssetId", primaryAssetId);
                                            intent.putExtra("assetId", assetId3);
                                            intent.putExtra("jstr", jo3.toString());
                                            mFragment.getActivity().startActivity(intent);
                                        } else {
                                            intent.putExtra("assetId", assetId3);
                                            intent.putExtra("jstr", jo3.toString());
                                            mFragment.getActivity().startActivity(intent);
                                        }
                                    }
                                } catch ( JSONException e ) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });

                setPromotionSticker(promotionSticker3, isNew3, hot3, assetNew3, assetHot3, eight_vod_poster_promotionsticker_imageview3);
                if ( isMobilePublicationRight == false ) {
                    eight_vod_poster_tvonly_imageview3.setVisibility(View.VISIBLE);
                }
                if ( rating3.startsWith("19") && mPref.isAdultVerification() == false ) {   //if ( "19".equals(rating2) ) {
                    eight_vod_poster_19_imageview3.setVisibility(View.VISIBLE);
                }
            } else {
                ((TextView)v.findViewById(R.id.eight_vod_poster_title_textview3)).setVisibility(View.INVISIBLE);
                ((NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview3)).setVisibility(View.INVISIBLE);
                ((ImageView)v.findViewById(R.id.eight_vod_poster_rank_imageview3)).setVisibility(View.INVISIBLE);
                ((TextView)v.findViewById(R.id.eight_vod_poster_rank_textview3)).setVisibility(View.INVISIBLE);
                ((ImageView)v.findViewById(R.id.eight_vod_poster_promotionsticker_imageview3)).setVisibility(View.INVISIBLE);
                ((ImageView)v.findViewById(R.id.eight_vod_poster_tvonly_imageview3)).setVisibility(View.INVISIBLE);
                ((ImageView)v.findViewById(R.id.eight_vod_poster_19_imageview3)).setVisibility(View.INVISIBLE);
            }

            if ( mVods.size() > index4 ) {
                TextView title4 =  (TextView)v.findViewById(R.id.eight_vod_poster_title_textview4);
                ImageView eight_vod_poster_promotionsticker_imageview4 = (ImageView)v.findViewById(R.id.eight_vod_poster_promotionsticker_imageview4);
                NetworkImageView niv41 = (NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview4);
                niv41.setDefaultImageResId(R.mipmap.posterlist_default);
                ImageView eight_vod_poster_tvonly_imageview4 = (ImageView)v.findViewById(R.id.eight_vod_poster_tvonly_imageview4);
                ImageView eight_vod_poster_19_imageview4 = (ImageView)v.findViewById(R.id.eight_vod_poster_19_imageview4);

                final JSONObject jo4 = mVods.get(index4);
                final String assetId4 = jo4.getString("primaryAssetId");
                ((ImageView)v.findViewById(R.id.eight_vod_poster_rank_imageview4)).setVisibility(View.INVISIBLE);
                ((TextView)v.findViewById(R.id.eight_vod_poster_rank_textview4)).setVisibility(View.INVISIBLE);
                title4.setText(jo4.getString("title"));
                String imageFileName4 = jo4.getString("imageFileName");
                String promotionSticker4 = jo4.getString("promotionSticker");

                boolean isMobilePublicationRight = false;
                String easyMobilePublicationRight = "";
                if ( jo4.isNull("mobilePublicationRight") ) {
                    easyMobilePublicationRight = jo4.getString("publicationRight");
                    if ( "2".equals(easyMobilePublicationRight) ) {  // 모바일 판권 있다.
                        isMobilePublicationRight = true;
                    }
                } else {
                    isMobilePublicationRight = jo4.getBoolean("mobilePublicationRight");
                }

                final String rating4 = jo4.getString("rating");
                Boolean isNew4                = false; // 0:없음, 1:있음.
                if ( ! jo4.isNull("isNew") ) {
                    isNew4 = jo4.getBoolean("isNew");
                }
                String assetNew4             = "0"; // 0:없음, 1:new일부만, 2:new단체
                if ( ! jo4.isNull("assetNew") ) {
                    assetNew4 = jo4.getString("assetNew");
                }
                String assetHot4             = "0"; // 0:없음, 1:new일부만, 2:new단체
                if ( ! jo4.isNull("assetHot") ) {
                    assetHot4 = jo4.getString("assetHot");
                }
                Boolean hot4                  = false; // 0:없음, 1:있음.
                if ( ! jo4.isNull("hot") ) {
                    hot4 = jo4.getBoolean("hot");
                }
                niv41.setImageUrl(imageFileName4, mImageLoader);
                niv41.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( rating4.startsWith("19") && mPref.isAdultVerification() == false ) {
                            String alertTitle = "성인인증 필요";
                            String alertMsg1 = mFragment.getActivity().getResources().getString(R.string.error_not_adult1);
                            String alertMsg2 = mFragment.getActivity().getResources().getString(R.string.error_not_adult2);
                            CMAlertUtil.Alert1(mFragment.getActivity(), alertTitle, alertMsg1, alertMsg2, false, true, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(mFragment.getActivity(), CMSettingMainActivity.class);
                                    mFragment.getActivity().startActivity(intent);
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                        } else {
                            if ( jo4.isNull("episodePeerExistence") ) {
                                Intent intent = new Intent(mFragment.getActivity(), VodDetailActivity.class);
                                intent.putExtra("assetId", assetId4);
                                intent.putExtra("jstr", jo4.toString());
                                mFragment.getActivity().startActivity(intent);
                            } else {
                                try {
                                    int     assetBundle          = jo4.getInt("assetBundle");
                                    String  episodePeerExistence = jo4.getString("episodePeerExistence");
                                    if ( assetBundle == 1 ) {  // 번들(묶음상품)이면 묶음상품이면, getAssetInfo로 구매여부를 알아낸다.
                                        ((VodMainFirstTabFragment)mFragment).onClickBundulPoster(assetId4);
                                    } else {  // 이외는 번들(묶음상품)이 아니다.
                                        Intent intent = new Intent(mFragment.getActivity(), VodDetailActivity.class);
                                        if ( "1".equals(episodePeerExistence) ) {
                                            String contentGroupId = jo4.getString("contentGroupId");
                                            String primaryAssetId = jo4.getString("primaryAssetId");
                                            intent.putExtra("episodePeerExistence", episodePeerExistence);
                                            intent.putExtra("contentGroupId", contentGroupId);
                                            intent.putExtra("primaryAssetId", primaryAssetId);
                                            intent.putExtra("assetId", assetId4);
                                            intent.putExtra("jstr", jo4.toString());
                                            mFragment.getActivity().startActivity(intent);
                                        } else {
                                            intent.putExtra("assetId", assetId4);
                                            intent.putExtra("jstr", jo4.toString());
                                            mFragment.getActivity().startActivity(intent);
                                        }
                                    }
                                } catch ( JSONException e ) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });

                setPromotionSticker(promotionSticker4, isNew4, hot4, assetNew4, assetHot4, eight_vod_poster_promotionsticker_imageview4);
                if ( isMobilePublicationRight == false ) {
                    eight_vod_poster_tvonly_imageview4.setVisibility(View.VISIBLE);
                }
                if ( rating4.startsWith("19") && mPref.isAdultVerification() == false ) {   //if ( "19".equals(rating2) ) {
                    eight_vod_poster_19_imageview4.setVisibility(View.VISIBLE);
                }
            } else {
                ((TextView)v.findViewById(R.id.eight_vod_poster_title_textview4)).setVisibility(View.INVISIBLE);
                ((NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview4)).setVisibility(View.INVISIBLE);
                ((ImageView)v.findViewById(R.id.eight_vod_poster_rank_imageview4)).setVisibility(View.INVISIBLE);
                ((TextView)v.findViewById(R.id.eight_vod_poster_rank_textview4)).setVisibility(View.INVISIBLE);
                ((ImageView)v.findViewById(R.id.eight_vod_poster_promotionsticker_imageview4)).setVisibility(View.INVISIBLE);
                ((ImageView)v.findViewById(R.id.eight_vod_poster_tvonly_imageview4)).setVisibility(View.INVISIBLE);
                ((ImageView)v.findViewById(R.id.eight_vod_poster_19_imageview4)).setVisibility(View.INVISIBLE);
            }

            if ( mVods.size() > index5 ) {
                TextView title5 =  (TextView)v.findViewById(R.id.eight_vod_poster_title_textview5);
                ImageView eight_vod_poster_promotionsticker_imageview5 = (ImageView)v.findViewById(R.id.eight_vod_poster_promotionsticker_imageview5);
                NetworkImageView niv51 = (NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview5);
                niv51.setDefaultImageResId(R.mipmap.posterlist_default);
                ImageView eight_vod_poster_tvonly_imageview5 = (ImageView)v.findViewById(R.id.eight_vod_poster_tvonly_imageview5);
                ImageView eight_vod_poster_19_imageview5 = (ImageView)v.findViewById(R.id.eight_vod_poster_19_imageview5);

                final JSONObject jo5 = mVods.get(index5);
                final String assetId5 = jo5.getString("primaryAssetId");
                ((ImageView)v.findViewById(R.id.eight_vod_poster_rank_imageview5)).setVisibility(View.INVISIBLE);
                ((TextView)v.findViewById(R.id.eight_vod_poster_rank_textview5)).setVisibility(View.INVISIBLE);
                title5.setText(jo5.getString("title"));
                String imageFileName5 = jo5.getString("imageFileName");
                String promotionSticker5 = jo5.getString("promotionSticker");

                boolean isMobilePublicationRight = false;
                String easyMobilePublicationRight = "";
                if ( jo5.isNull("mobilePublicationRight") ) {
                    easyMobilePublicationRight = jo5.getString("publicationRight");
                    if ( "2".equals(easyMobilePublicationRight) ) {  // 모바일 판권 있다.
                        isMobilePublicationRight = true;
                    }
                } else {
                    isMobilePublicationRight = jo5.getBoolean("mobilePublicationRight");
                }

                final String rating5 = jo5.getString("rating");
                Boolean isNew5                = false; // 0:없음, 1:있음.
                if ( ! jo5.isNull("isNew") ) {
                    isNew5 = jo5.getBoolean("isNew");
                }
                String assetNew5             = "0"; // 0:없음, 1:new일부만, 2:new단체
                if ( ! jo5.isNull("assetNew") ) {
                    assetNew5 = jo5.getString("assetNew");
                }
                String assetHot5             = "0"; // 0:없음, 1:new일부만, 2:new단체
                if ( ! jo5.isNull("assetHot") ) {
                    assetHot5 = jo5.getString("assetHot");
                }
                Boolean hot5                  = false; // 0:없음, 1:있음.
                if ( ! jo5.isNull("hot") ) {
                    hot5 = jo5.getBoolean("hot");
                }
                niv51.setImageUrl(imageFileName5, mImageLoader);
                niv51.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( rating5.startsWith("19") && mPref.isAdultVerification() == false ) {
                            String alertTitle = "성인인증 필요";
                            String alertMsg1 = mFragment.getActivity().getResources().getString(R.string.error_not_adult1);
                            String alertMsg2 = mFragment.getActivity().getResources().getString(R.string.error_not_adult2);
                            CMAlertUtil.Alert1(mFragment.getActivity(), alertTitle, alertMsg1, alertMsg2, false, true, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(mFragment.getActivity(), CMSettingMainActivity.class);
                                    mFragment.getActivity().startActivity(intent);
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                        } else {
                            if ( jo5.isNull("episodePeerExistence") ) {
                                Intent intent = new Intent(mFragment.getActivity(), VodDetailActivity.class);
                                intent.putExtra("assetId", assetId5);
                                intent.putExtra("jstr", jo5.toString());
                                mFragment.getActivity().startActivity(intent);
                            } else {
                                try {
                                    int     assetBundle          = jo5.getInt("assetBundle");
                                    String  episodePeerExistence = jo5.getString("episodePeerExistence");
                                    if ( assetBundle == 1 ) {  // 번들(묶음상품)이면 묶음상품이면, getAssetInfo로 구매여부를 알아낸다.
                                        ((VodMainFirstTabFragment)mFragment).onClickBundulPoster(assetId5);
                                    } else {  // 이외는 번들(묶음상품)이 아니다.
                                        Intent intent = new Intent(mFragment.getActivity(), VodDetailActivity.class);
                                        if ( "1".equals(episodePeerExistence) ) {
                                            String contentGroupId = jo5.getString("contentGroupId");
                                            String primaryAssetId = jo5.getString("primaryAssetId");
                                            intent.putExtra("episodePeerExistence", episodePeerExistence);
                                            intent.putExtra("contentGroupId", contentGroupId);
                                            intent.putExtra("primaryAssetId", primaryAssetId);
                                            intent.putExtra("assetId", assetId5);
                                            intent.putExtra("jstr", jo5.toString());
                                            mFragment.getActivity().startActivity(intent);
                                        } else {
                                            intent.putExtra("assetId", assetId5);
                                            intent.putExtra("jstr", jo5.toString());
                                            mFragment.getActivity().startActivity(intent);
                                        }
                                    }
                                } catch ( JSONException e ) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });

                setPromotionSticker(promotionSticker5, isNew5, hot5, assetNew5, assetHot5, eight_vod_poster_promotionsticker_imageview5);
                if ( isMobilePublicationRight == false ) {
                    eight_vod_poster_tvonly_imageview5.setVisibility(View.VISIBLE);
                }
                if ( rating5.startsWith("19") && mPref.isAdultVerification() == false ) {   //if ( "19".equals(rating2) ) {
                    eight_vod_poster_19_imageview5.setVisibility(View.VISIBLE);
                }
            } else {
                ((TextView)v.findViewById(R.id.eight_vod_poster_title_textview5)).setVisibility(View.INVISIBLE);
                ((NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview5)).setVisibility(View.INVISIBLE);
                ((ImageView)v.findViewById(R.id.eight_vod_poster_rank_imageview5)).setVisibility(View.INVISIBLE);
                ((TextView)v.findViewById(R.id.eight_vod_poster_rank_textview5)).setVisibility(View.INVISIBLE);
                ((ImageView)v.findViewById(R.id.eight_vod_poster_promotionsticker_imageview5)).setVisibility(View.INVISIBLE);
                ((ImageView)v.findViewById(R.id.eight_vod_poster_tvonly_imageview5)).setVisibility(View.INVISIBLE);
                ((ImageView)v.findViewById(R.id.eight_vod_poster_19_imageview5)).setVisibility(View.INVISIBLE);
            }

            if ( mVods.size() > index6 ) {
                TextView title6 =  (TextView)v.findViewById(R.id.eight_vod_poster_title_textview6);
                ImageView eight_vod_poster_promotionsticker_imageview6 = (ImageView)v.findViewById(R.id.eight_vod_poster_promotionsticker_imageview6);
                NetworkImageView niv61 = (NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview6);
                niv61.setDefaultImageResId(R.mipmap.posterlist_default);
                ImageView eight_vod_poster_tvonly_imageview6 = (ImageView)v.findViewById(R.id.eight_vod_poster_tvonly_imageview6);
                ImageView eight_vod_poster_19_imageview6 = (ImageView)v.findViewById(R.id.eight_vod_poster_19_imageview6);

                final JSONObject jo6 = mVods.get(index6);
                final String assetId6 = jo6.getString("primaryAssetId");
                ((ImageView)v.findViewById(R.id.eight_vod_poster_rank_imageview6)).setVisibility(View.INVISIBLE);
                ((TextView)v.findViewById(R.id.eight_vod_poster_rank_textview6)).setVisibility(View.INVISIBLE);
                title6.setText(jo6.getString("title"));
                String imageFileName6 = jo6.getString("imageFileName");
                String promotionSticker6 = jo6.getString("promotionSticker");

                boolean isMobilePublicationRight = false;
                String easyMobilePublicationRight = "";
                if ( jo6.isNull("mobilePublicationRight") ) {
                    easyMobilePublicationRight = jo6.getString("publicationRight");
                    if ( "2".equals(easyMobilePublicationRight) ) {  // 모바일 판권 있다.
                        isMobilePublicationRight = true;
                    }
                } else {
                    isMobilePublicationRight = jo6.getBoolean("mobilePublicationRight");
                }

                final String rating6 = jo6.getString("rating");
                Boolean isNew6                = false; // 0:없음, 1:있음.
                if ( ! jo6.isNull("isNew") ) {
                    isNew6 = jo6.getBoolean("isNew");
                }
                String assetNew6             = "0"; // 0:없음, 1:new일부만, 2:new단체
                if ( ! jo6.isNull("assetNew") ) {
                    assetNew6 = jo6.getString("assetNew");
                }
                String assetHot6             = "0"; // 0:없음, 1:new일부만, 2:new단체
                if ( ! jo6.isNull("assetHot") ) {
                    assetHot6 = jo6.getString("assetHot");
                }
                Boolean hot6                  = false; // 0:없음, 1:있음.
                if ( ! jo6.isNull("hot") ) {
                    hot6 = jo6.getBoolean("hot");
                }
                niv61.setImageUrl(imageFileName6, mImageLoader);
                niv61.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( rating6.startsWith("19") && mPref.isAdultVerification() == false ) {
                            String alertTitle = "성인인증 필요";
                            String alertMsg1 = mFragment.getActivity().getResources().getString(R.string.error_not_adult1);
                            String alertMsg2 = mFragment.getActivity().getResources().getString(R.string.error_not_adult2);
                            CMAlertUtil.Alert1(mFragment.getActivity(), alertTitle, alertMsg1, alertMsg2, false, true, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(mFragment.getActivity(), CMSettingMainActivity.class);
                                    mFragment.getActivity().startActivity(intent);
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                        } else {
                            if ( jo6.isNull("episodePeerExistence") ) {
                                Intent intent = new Intent(mFragment.getActivity(), VodDetailActivity.class);
                                intent.putExtra("assetId", assetId6);
                                intent.putExtra("jstr", jo6.toString());
                                mFragment.getActivity().startActivity(intent);
                            } else {
                                try {
                                    int     assetBundle          = jo6.getInt("assetBundle");
                                    String  episodePeerExistence = jo6.getString("episodePeerExistence");
                                    if ( assetBundle == 1 ) {  // 번들(묶음상품)이면 묶음상품이면, getAssetInfo로 구매여부를 알아낸다.
                                        ((VodMainFirstTabFragment)mFragment).onClickBundulPoster(assetId6);
                                    } else {  // 이외는 번들(묶음상품)이 아니다.
                                        Intent intent = new Intent(mFragment.getActivity(), VodDetailActivity.class);
                                        if ( "1".equals(episodePeerExistence) ) {
                                            String contentGroupId = jo6.getString("contentGroupId");
                                            String primaryAssetId = jo6.getString("primaryAssetId");
                                            intent.putExtra("episodePeerExistence", episodePeerExistence);
                                            intent.putExtra("contentGroupId", contentGroupId);
                                            intent.putExtra("primaryAssetId", primaryAssetId);
                                            intent.putExtra("assetId", assetId6);
                                            intent.putExtra("jstr", jo6.toString());
                                            mFragment.getActivity().startActivity(intent);
                                        } else {
                                            intent.putExtra("assetId", assetId6);
                                            intent.putExtra("jstr", jo6.toString());
                                            mFragment.getActivity().startActivity(intent);
                                        }
                                    }
                                } catch ( JSONException e ) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });

                setPromotionSticker(promotionSticker6, isNew6, hot6, assetNew6, assetHot6, eight_vod_poster_promotionsticker_imageview6);
                if ( isMobilePublicationRight == false ) {
                    eight_vod_poster_tvonly_imageview6.setVisibility(View.VISIBLE);
                }
                if ( rating6.startsWith("19") && mPref.isAdultVerification() == false ) {   //if ( "19".equals(rating2) ) {
                    eight_vod_poster_19_imageview6.setVisibility(View.VISIBLE);
                }
            } else {
                ((TextView)v.findViewById(R.id.eight_vod_poster_title_textview6)).setVisibility(View.INVISIBLE);
                ((NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview6)).setVisibility(View.INVISIBLE);
                ((ImageView)v.findViewById(R.id.eight_vod_poster_rank_imageview6)).setVisibility(View.INVISIBLE);
                ((TextView)v.findViewById(R.id.eight_vod_poster_rank_textview6)).setVisibility(View.INVISIBLE);
                ((ImageView)v.findViewById(R.id.eight_vod_poster_promotionsticker_imageview6)).setVisibility(View.INVISIBLE);
                ((ImageView)v.findViewById(R.id.eight_vod_poster_tvonly_imageview6)).setVisibility(View.INVISIBLE);
                ((ImageView)v.findViewById(R.id.eight_vod_poster_19_imageview6)).setVisibility(View.INVISIBLE);
            }

            if ( mVods.size() > index7 ) {
                TextView title7 =  (TextView)v.findViewById(R.id.eight_vod_poster_title_textview7);
                ImageView eight_vod_poster_promotionsticker_imageview7 = (ImageView)v.findViewById(R.id.eight_vod_poster_promotionsticker_imageview7);
                NetworkImageView niv71 = (NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview7);
                niv71.setDefaultImageResId(R.mipmap.posterlist_default);
                ImageView eight_vod_poster_tvonly_imageview7 = (ImageView)v.findViewById(R.id.eight_vod_poster_tvonly_imageview7);
                ImageView eight_vod_poster_19_imageview7 = (ImageView)v.findViewById(R.id.eight_vod_poster_19_imageview7);

                final JSONObject jo7 = mVods.get(index7);
                final String assetId7 = jo7.getString("primaryAssetId");
                ((ImageView)v.findViewById(R.id.eight_vod_poster_rank_imageview7)).setVisibility(View.INVISIBLE);
                ((TextView)v.findViewById(R.id.eight_vod_poster_rank_textview7)).setVisibility(View.INVISIBLE);
                title7.setText(jo7.getString("title"));
                String imageFileName7 = jo7.getString("imageFileName");
                String promotionSticker7 = jo7.getString("promotionSticker");

                boolean isMobilePublicationRight = false;
                String easyMobilePublicationRight = "";
                if ( jo7.isNull("mobilePublicationRight") ) {
                    easyMobilePublicationRight = jo7.getString("publicationRight");
                    if ( "2".equals(easyMobilePublicationRight) ) {  // 모바일 판권 있다.
                        isMobilePublicationRight = true;
                    }
                } else {
                    isMobilePublicationRight = jo7.getBoolean("mobilePublicationRight");
                }

                final String rating7 = jo7.getString("rating");
                Boolean isNew7                = false; // 0:없음, 1:있음.
                if ( ! jo7.isNull("isNew") ) {
                    isNew7 = jo7.getBoolean("isNew");
                }
                String assetNew7             = "0"; // 0:없음, 1:new일부만, 2:new단체
                if ( ! jo7.isNull("assetNew") ) {
                    assetNew7 = jo7.getString("assetNew");
                }
                String assetHot7             = "0"; // 0:없음, 1:new일부만, 2:new단체
                if ( ! jo7.isNull("assetHot") ) {
                    assetHot7 = jo7.getString("assetHot");
                }
                Boolean hot7                  = false; // 0:없음, 1:있음.
                if ( ! jo7.isNull("hot") ) {
                    hot7 = jo7.getBoolean("hot");
                }
                niv71.setImageUrl(imageFileName7, mImageLoader);
                niv71.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (rating7.startsWith("19") && mPref.isAdultVerification() == false) {
                            String alertTitle = "성인인증 필요";
                            String alertMsg1 = mFragment.getActivity().getResources().getString(R.string.error_not_adult1);
                            String alertMsg2 = mFragment.getActivity().getResources().getString(R.string.error_not_adult2);
                            CMAlertUtil.Alert1(mFragment.getActivity(), alertTitle, alertMsg1, alertMsg2, false, true, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(mFragment.getActivity(), CMSettingMainActivity.class);
                                    mFragment.getActivity().startActivity(intent);
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                        } else {
                            if ( jo7.isNull("episodePeerExistence") ) {
                                Intent intent = new Intent(mFragment.getActivity(), VodDetailActivity.class);
                                intent.putExtra("assetId", assetId7);
                                intent.putExtra("jstr", jo7.toString());
                                mFragment.getActivity().startActivity(intent);
                            } else {
                                try {
                                    int     assetBundle          = jo7.getInt("assetBundle");
                                    String  episodePeerExistence = jo7.getString("episodePeerExistence");
                                    if ( assetBundle == 1 ) {  // 번들(묶음상품)이면 묶음상품이면, getAssetInfo로 구매여부를 알아낸다.
                                        ((VodMainFirstTabFragment)mFragment).onClickBundulPoster(assetId7);
                                    } else {  // 이외는 번들(묶음상품)이 아니다.
                                        Intent intent = new Intent(mFragment.getActivity(), VodDetailActivity.class);
                                        if ( "1".equals(episodePeerExistence) ) {
                                            String contentGroupId = jo7.getString("contentGroupId");
                                            String primaryAssetId = jo7.getString("primaryAssetId");
                                            intent.putExtra("episodePeerExistence", episodePeerExistence);
                                            intent.putExtra("contentGroupId", contentGroupId);
                                            intent.putExtra("primaryAssetId", primaryAssetId);
                                            intent.putExtra("assetId", assetId7);
                                            intent.putExtra("jstr", jo7.toString());
                                            mFragment.getActivity().startActivity(intent);
                                        } else {
                                            intent.putExtra("assetId", assetId7);
                                            intent.putExtra("jstr", jo7.toString());
                                            mFragment.getActivity().startActivity(intent);
                                        }
                                    }
                                } catch ( JSONException e ) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });

                setPromotionSticker(promotionSticker7, isNew7, hot7, assetNew7, assetHot7, eight_vod_poster_promotionsticker_imageview7);
                if ( isMobilePublicationRight == false ) {
                    eight_vod_poster_tvonly_imageview7.setVisibility(View.VISIBLE);
                }
                if ( rating7.startsWith("19") && mPref.isAdultVerification() == false ) {   //if ( "19".equals(rating2) ) {
                    eight_vod_poster_19_imageview7.setVisibility(View.VISIBLE);
                }
            } else {
                ((TextView)v.findViewById(R.id.eight_vod_poster_title_textview7)).setVisibility(View.INVISIBLE);
                ((NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview7)).setVisibility(View.INVISIBLE);
                ((ImageView)v.findViewById(R.id.eight_vod_poster_rank_imageview7)).setVisibility(View.INVISIBLE);
                ((TextView)v.findViewById(R.id.eight_vod_poster_rank_textview7)).setVisibility(View.INVISIBLE);
                ((ImageView)v.findViewById(R.id.eight_vod_poster_promotionsticker_imageview7)).setVisibility(View.INVISIBLE);
                ((ImageView)v.findViewById(R.id.eight_vod_poster_tvonly_imageview7)).setVisibility(View.INVISIBLE);
                ((ImageView)v.findViewById(R.id.eight_vod_poster_19_imageview7)).setVisibility(View.INVISIBLE);
            }

            if ( mVods.size() > index8 ) {
                TextView title8 =  (TextView)v.findViewById(R.id.eight_vod_poster_title_textview8);
                ImageView eight_vod_poster_promotionsticker_imageview8 = (ImageView)v.findViewById(R.id.eight_vod_poster_promotionsticker_imageview8);
                NetworkImageView niv81 = (NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview8);
                niv81.setDefaultImageResId(R.mipmap.posterlist_default);
                ImageView eight_vod_poster_tvonly_imageview8 = (ImageView)v.findViewById(R.id.eight_vod_poster_tvonly_imageview8);
                ImageView eight_vod_poster_19_imageview8 = (ImageView)v.findViewById(R.id.eight_vod_poster_19_imageview8);

                final JSONObject jo8 = mVods.get(index8);
                final String assetId8 = jo8.getString("primaryAssetId");
                ((ImageView)v.findViewById(R.id.eight_vod_poster_rank_imageview8)).setVisibility(View.INVISIBLE);
                ((TextView)v.findViewById(R.id.eight_vod_poster_rank_textview8)).setVisibility(View.INVISIBLE);
                title8.setText(jo8.getString("title"));
                String imageFileName8 = jo8.getString("imageFileName");
                String promotionSticker8 = jo8.getString("promotionSticker");

                boolean isMobilePublicationRight = false;
                String easyMobilePublicationRight = "";
                if ( jo8.isNull("mobilePublicationRight") ) {
                    easyMobilePublicationRight = jo8.getString("publicationRight");
                    if ( "2".equals(easyMobilePublicationRight) ) {  // 모바일 판권 있다.
                        isMobilePublicationRight = true;
                    }
                } else {
                    isMobilePublicationRight = jo8.getBoolean("mobilePublicationRight");
                }

                final String rating8 = jo8.getString("rating");
                Boolean isNew8                = false; // 0:없음, 1:있음.
                if ( ! jo8.isNull("isNew") ) {
                    isNew8 = jo8.getBoolean("isNew");
                }
                String assetNew8             = "0"; // 0:없음, 1:new일부만, 2:new단체
                if ( ! jo8.isNull("assetNew") ) {
                    assetNew8 = jo8.getString("assetNew");
                }
                String assetHot8             = "0"; // 0:없음, 1:new일부만, 2:new단체
                if ( ! jo8.isNull("assetHot") ) {
                    assetHot8 = jo8.getString("assetHot");
                }
                Boolean hot8                  = false; // 0:없음, 1:있음.
                if ( ! jo8.isNull("hot") ) {
                    hot8 = jo8.getBoolean("hot");
                }
                niv81.setImageUrl(imageFileName8, mImageLoader);
                niv81.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( rating8.startsWith("19") && mPref.isAdultVerification() == false ) {
                            String alertTitle = "성인인증 필요";
                            String alertMsg1 = mFragment.getActivity().getResources().getString(R.string.error_not_adult1);
                            String alertMsg2 = mFragment.getActivity().getResources().getString(R.string.error_not_adult2);
                            CMAlertUtil.Alert1(mFragment.getActivity(), alertTitle, alertMsg1, alertMsg2, false, true, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(mFragment.getActivity(), CMSettingMainActivity.class);
                                    mFragment.getActivity().startActivity(intent);
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                        } else {
                            if ( jo8.isNull("episodePeerExistence") ) {
                                Intent intent = new Intent(mFragment.getActivity(), VodDetailActivity.class);
                                intent.putExtra("assetId", assetId8);
                                intent.putExtra("jstr", jo8.toString());
                                mFragment.getActivity().startActivity(intent);
                            } else {
                                try {
                                    int     assetBundle          = jo8.getInt("assetBundle");
                                    String  episodePeerExistence = jo8.getString("episodePeerExistence");
                                    if ( assetBundle == 1 ) {  // 번들(묶음상품)이면 묶음상품이면, getAssetInfo로 구매여부를 알아낸다.
                                        ((VodMainFirstTabFragment)mFragment).onClickBundulPoster(assetId8);
                                    } else {  // 이외는 번들(묶음상품)이 아니다.
                                        Intent intent = new Intent(mFragment.getActivity(), VodDetailActivity.class);
                                        if ( "1".equals(episodePeerExistence) ) {
                                            String contentGroupId = jo8.getString("contentGroupId");
                                            String primaryAssetId = jo8.getString("primaryAssetId");
                                            intent.putExtra("episodePeerExistence", episodePeerExistence);
                                            intent.putExtra("contentGroupId", contentGroupId);
                                            intent.putExtra("primaryAssetId", primaryAssetId);
                                            intent.putExtra("assetId", assetId8);
                                            intent.putExtra("jstr", jo8.toString());
                                            mFragment.getActivity().startActivity(intent);
                                        } else {
                                            intent.putExtra("assetId", assetId8);
                                            intent.putExtra("jstr", jo8.toString());
                                            mFragment.getActivity().startActivity(intent);
                                        }
                                    }
                                } catch ( JSONException e ) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });

                setPromotionSticker(promotionSticker8, isNew8, hot8, assetNew8, assetHot8, eight_vod_poster_promotionsticker_imageview8);
                if ( isMobilePublicationRight == false ) {
                    eight_vod_poster_tvonly_imageview8.setVisibility(View.VISIBLE);
                }
                if ( rating8.startsWith("19") && mPref.isAdultVerification() == false ) {   //if ( "19".equals(rating2) ) {
                    eight_vod_poster_19_imageview8.setVisibility(View.VISIBLE);
                }
            } else {
                ((TextView)v.findViewById(R.id.eight_vod_poster_title_textview8)).setVisibility(View.INVISIBLE);
                ((NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview8)).setVisibility(View.INVISIBLE);
                ((ImageView)v.findViewById(R.id.eight_vod_poster_rank_imageview8)).setVisibility(View.INVISIBLE);
                ((TextView)v.findViewById(R.id.eight_vod_poster_rank_textview8)).setVisibility(View.INVISIBLE);
                ((ImageView)v.findViewById(R.id.eight_vod_poster_promotionsticker_imageview8)).setVisibility(View.INVISIBLE);
                ((ImageView)v.findViewById(R.id.eight_vod_poster_tvonly_imageview8)).setVisibility(View.INVISIBLE);
                ((ImageView)v.findViewById(R.id.eight_vod_poster_19_imageview8)).setVisibility(View.INVISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        ((ViewPager)pager).addView(v); // ((ViewPager)pager).addView(v, position);

        return v;
    }
}
