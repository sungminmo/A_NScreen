package com.jjiya.android.common;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.jjiya.android.common.UiUtil.setPromotionSticker;

/**
 * Created by limdavid on 15. 10. 22..
 */
public class EightVodPosterPagerAdapter extends PagerAdapter {

    private LayoutInflater   mLayoutInflater;
    private JYSharedPreferences mPref;

    private ImageLoader      mImageLoader;
    private List<JSONObject> mVods;

    private Fragment         mFragment;

    public EightVodPosterPagerAdapter(Context c){
        super();
        mLayoutInflater = LayoutInflater.from(c);
        mVods           = new ArrayList<JSONObject>();
        mPref           = new JYSharedPreferences(c);
    }

    public void setFragment(Fragment f) {
        mFragment = f;
    }

    public void clear(){
        mVods.clear();
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
                TextView ranking1 = (TextView)v.findViewById(R.id.eight_vod_poster_rank_textview1);
                ImageView eight_vod_poster_promotionsticker_imageview1 = (ImageView)v.findViewById(R.id.eight_vod_poster_promotionsticker_imageview1);
                NetworkImageView niv11 = (NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview1);
                ImageView eight_vod_poster_tvonly_imageview1 = (ImageView)v.findViewById(R.id.eight_vod_poster_tvonly_imageview1);
                ImageView eight_vod_poster_19_imageview1 = (ImageView)v.findViewById(R.id.eight_vod_poster_19_imageview1);

                final JSONObject jo1 = mVods.get(index1);
                final String assetId1 = jo1.getString("assetId");
                title1.setText(jo1.getString("title"));
                String imageFileName1 = jo1.getString("imageFileName");
                ranking1.setText(jo1.getString("ranking"));
                String promotionSticker1 = jo1.getString("promotionSticker");
                String publicationRight1 = jo1.getString("publicationRight");
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
                            Intent intent = new Intent(mFragment.getActivity(), VodDetailActivity.class);
                            intent.putExtra("assetId", assetId1);
                            intent.putExtra("jstr", jo1.toString());
                            mFragment.getActivity().startActivity(intent);
                        }
                    }
                });

                setPromotionSticker(promotionSticker1, isNew1, hot1, assetNew1, assetHot1, eight_vod_poster_promotionsticker_imageview1);
                if ( "1".equals(publicationRight1) ) {
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
                TextView ranking2 = (TextView)v.findViewById(R.id.eight_vod_poster_rank_textview2);
                ImageView eight_vod_poster_promotionsticker_imageview2 = (ImageView)v.findViewById(R.id.eight_vod_poster_promotionsticker_imageview2);
                NetworkImageView niv21 = (NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview2);
                ImageView eight_vod_poster_tvonly_imageview2 = (ImageView)v.findViewById(R.id.eight_vod_poster_tvonly_imageview2);
                ImageView eight_vod_poster_19_imageview2 = (ImageView)v.findViewById(R.id.eight_vod_poster_19_imageview2);

                final JSONObject jo2 = mVods.get(index2);
                final String assetId2 = jo2.getString("assetId");
                title2.setText(jo2.getString("title"));
                String imageFileName2 = jo2.getString("imageFileName");
                ranking2.setText(jo2.getString("ranking"));
                String promotionSticker2 = jo2.getString("promotionSticker");
                String publicationRight2 = jo2.getString("publicationRight");
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
                            Intent intent = new Intent(mFragment.getActivity(), VodDetailActivity.class);
                            intent.putExtra("assetId", assetId2);
                            intent.putExtra("jstr", jo2.toString());
                            mFragment.getActivity().startActivity(intent);
                        }
                    }
                });

                setPromotionSticker(promotionSticker2, isNew2, hot2, assetNew2, assetHot2, eight_vod_poster_promotionsticker_imageview2);
                if ( "1".equals(publicationRight2) ) {
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
                TextView ranking3 = (TextView)v.findViewById(R.id.eight_vod_poster_rank_textview3);
                ImageView eight_vod_poster_promotionsticker_imageview3 = (ImageView)v.findViewById(R.id.eight_vod_poster_promotionsticker_imageview3);
                NetworkImageView niv31 = (NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview3);
                ImageView eight_vod_poster_tvonly_imageview3 = (ImageView)v.findViewById(R.id.eight_vod_poster_tvonly_imageview3);
                ImageView eight_vod_poster_19_imageview3 = (ImageView)v.findViewById(R.id.eight_vod_poster_19_imageview3);

                final JSONObject jo3 = mVods.get(index3);
                final String assetId3 = jo3.getString("assetId");
                title3.setText(jo3.getString("title"));
                String imageFileName3 = jo3.getString("imageFileName");
                ranking3.setText(jo3.getString("ranking"));
                String promotionSticker3 = jo3.getString("promotionSticker");
                String publicationRight3 = jo3.getString("publicationRight");
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
                            Intent intent = new Intent(mFragment.getActivity(), VodDetailActivity.class);
                            intent.putExtra("assetId", assetId3);
                            intent.putExtra("jstr", jo3.toString());
                            mFragment.getActivity().startActivity(intent);
                        }
                    }
                });

                setPromotionSticker(promotionSticker3, isNew3, hot3, assetNew3, assetHot3, eight_vod_poster_promotionsticker_imageview3);
                if ( "1".equals(publicationRight3) ) {
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
                TextView ranking4 = (TextView)v.findViewById(R.id.eight_vod_poster_rank_textview4);
                ImageView eight_vod_poster_promotionsticker_imageview4 = (ImageView)v.findViewById(R.id.eight_vod_poster_promotionsticker_imageview4);
                NetworkImageView niv41 = (NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview4);
                ImageView eight_vod_poster_tvonly_imageview4 = (ImageView)v.findViewById(R.id.eight_vod_poster_tvonly_imageview4);
                ImageView eight_vod_poster_19_imageview4 = (ImageView)v.findViewById(R.id.eight_vod_poster_19_imageview4);

                final JSONObject jo4 = mVods.get(index4);
                final String assetId4 = jo4.getString("assetId");
                title4.setText(jo4.getString("title"));
                String imageFileName4 = jo4.getString("imageFileName");
                ranking4.setText(jo4.getString("ranking"));
                String promotionSticker4 = jo4.getString("promotionSticker");
                String publicationRight4 = jo4.getString("publicationRight");
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
                            Intent intent = new Intent(mFragment.getActivity(), VodDetailActivity.class);
                            intent.putExtra("assetId", assetId4);
                            intent.putExtra("jstr", jo4.toString());
                            mFragment.getActivity().startActivity(intent);
                        }
                    }
                });

                setPromotionSticker(promotionSticker4, isNew4, hot4, assetNew4, assetHot4, eight_vod_poster_promotionsticker_imageview4);
                if ( "1".equals(publicationRight4) ) {
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
                TextView ranking5 = (TextView)v.findViewById(R.id.eight_vod_poster_rank_textview5);
                ImageView eight_vod_poster_promotionsticker_imageview5 = (ImageView)v.findViewById(R.id.eight_vod_poster_promotionsticker_imageview5);
                NetworkImageView niv51 = (NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview5);
                ImageView eight_vod_poster_tvonly_imageview5 = (ImageView)v.findViewById(R.id.eight_vod_poster_tvonly_imageview5);
                ImageView eight_vod_poster_19_imageview5 = (ImageView)v.findViewById(R.id.eight_vod_poster_19_imageview5);

                final JSONObject jo5 = mVods.get(index5);
                final String assetId5 = jo5.getString("assetId");
                title5.setText(jo5.getString("title"));
                String imageFileName5 = jo5.getString("imageFileName");
                ranking5.setText(jo5.getString("ranking"));
                String promotionSticker5 = jo5.getString("promotionSticker");
                String publicationRight5 = jo5.getString("publicationRight");
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
                            Intent intent = new Intent(mFragment.getActivity(), VodDetailActivity.class);
                            intent.putExtra("assetId", assetId5);
                            intent.putExtra("jstr", jo5.toString());
                            mFragment.getActivity().startActivity(intent);
                        }
                    }
                });

                setPromotionSticker(promotionSticker5, isNew5, hot5, assetNew5, assetHot5, eight_vod_poster_promotionsticker_imageview5);
                if ( "1".equals(publicationRight5) ) {
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
                TextView ranking6 = (TextView)v.findViewById(R.id.eight_vod_poster_rank_textview6);
                ImageView eight_vod_poster_promotionsticker_imageview6 = (ImageView)v.findViewById(R.id.eight_vod_poster_promotionsticker_imageview6);
                NetworkImageView niv61 = (NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview6);
                ImageView eight_vod_poster_tvonly_imageview6 = (ImageView)v.findViewById(R.id.eight_vod_poster_tvonly_imageview6);
                ImageView eight_vod_poster_19_imageview6 = (ImageView)v.findViewById(R.id.eight_vod_poster_19_imageview6);

                final JSONObject jo6 = mVods.get(index6);
                final String assetId6 = jo6.getString("assetId");
                title6.setText(jo6.getString("title"));
                String imageFileName6 = jo6.getString("imageFileName");
                ranking6.setText(jo6.getString("ranking"));
                String promotionSticker6 = jo6.getString("promotionSticker");
                String publicationRight6 = jo6.getString("publicationRight");
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
                            Intent intent = new Intent(mFragment.getActivity(), VodDetailActivity.class);
                            intent.putExtra("assetId", assetId6);
                            intent.putExtra("jstr", jo6.toString());
                            mFragment.getActivity().startActivity(intent);
                        }
                    }
                });

                setPromotionSticker(promotionSticker6, isNew6, hot6, assetNew6, assetHot6, eight_vod_poster_promotionsticker_imageview6);
                if ( "1".equals(publicationRight6) ) {
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
                TextView ranking7 = (TextView)v.findViewById(R.id.eight_vod_poster_rank_textview7);
                ImageView eight_vod_poster_promotionsticker_imageview7 = (ImageView)v.findViewById(R.id.eight_vod_poster_promotionsticker_imageview7);
                NetworkImageView niv71 = (NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview7);
                ImageView eight_vod_poster_tvonly_imageview7 = (ImageView)v.findViewById(R.id.eight_vod_poster_tvonly_imageview7);
                ImageView eight_vod_poster_19_imageview7 = (ImageView)v.findViewById(R.id.eight_vod_poster_19_imageview7);

                final JSONObject jo7 = mVods.get(index7);
                final String assetId7 = jo7.getString("assetId");
                title7.setText(jo7.getString("title"));
                String imageFileName7 = jo7.getString("imageFileName");
                ranking7.setText(jo7.getString("ranking"));
                String promotionSticker7 = jo7.getString("promotionSticker");
                String publicationRight7 = jo7.getString("publicationRight");
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
                        if ( rating7.startsWith("19") && mPref.isAdultVerification() == false ) {
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
                            Intent intent = new Intent(mFragment.getActivity(), VodDetailActivity.class);
                            intent.putExtra("assetId", assetId7);
                            intent.putExtra("jstr", jo7.toString());
                            mFragment.getActivity().startActivity(intent);
                        }
                    }
                });

                setPromotionSticker(promotionSticker7, isNew7, hot7, assetNew7, assetHot7, eight_vod_poster_promotionsticker_imageview7);
                if ( "1".equals(publicationRight7) ) {
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
                TextView ranking8 = (TextView)v.findViewById(R.id.eight_vod_poster_rank_textview8);
                ImageView eight_vod_poster_promotionsticker_imageview8 = (ImageView)v.findViewById(R.id.eight_vod_poster_promotionsticker_imageview8);
                NetworkImageView niv81 = (NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview8);
                ImageView eight_vod_poster_tvonly_imageview8 = (ImageView)v.findViewById(R.id.eight_vod_poster_tvonly_imageview8);
                ImageView eight_vod_poster_19_imageview8 = (ImageView)v.findViewById(R.id.eight_vod_poster_19_imageview8);

                final JSONObject jo8 = mVods.get(index8);
                final String assetId8 = jo8.getString("assetId");
                title8.setText(jo8.getString("title"));
                String imageFileName8 = jo8.getString("imageFileName");
                ranking8.setText(jo8.getString("ranking"));
                String promotionSticker8 = jo8.getString("promotionSticker");
                String publicationRight8 = jo8.getString("publicationRight");
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
                            Intent intent = new Intent(mFragment.getActivity(), VodDetailActivity.class);
                            intent.putExtra("assetId", assetId8);
                            intent.putExtra("jstr", jo8.toString());
                            mFragment.getActivity().startActivity(intent);
                        }
                    }
                });

                setPromotionSticker(promotionSticker8, isNew8, hot8, assetNew8, assetHot8, eight_vod_poster_promotionsticker_imageview8);
                if ( "1".equals(publicationRight8) ) {
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
            ImageView indi1 = (ImageView)v.findViewById(R.id.eight_vod_poster_indicator_imageview1);
            ImageView indi2 = (ImageView)v.findViewById(R.id.eight_vod_poster_indicator_imageview2);
            ImageView indi3 = (ImageView)v.findViewById(R.id.eight_vod_poster_indicator_imageview3);
            ImageView indi4 = (ImageView)v.findViewById(R.id.eight_vod_poster_indicator_imageview4);
            ImageView indi5 = (ImageView)v.findViewById(R.id.eight_vod_poster_indicator_imageview5);
            UiUtil.setIndicatorImage(position, getCount(), indi1, indi2, indi3, indi4, indi5, null, null, null, null, null);


//            if ( position == 0 ) {
//                prev.setVisibility(View.INVISIBLE);
//            }
//            if ( position == (mVods.size()-1) ) {
//                next.setVisibility(View.INVISIBLE);
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        ((ViewPager)pager).addView(v); // ((ViewPager)pager).addView(v, position);

        return v;
    }
}
