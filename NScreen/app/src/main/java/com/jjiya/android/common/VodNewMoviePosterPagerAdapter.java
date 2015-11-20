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
                niv11.setImageUrl(imageFileName1, mImageLoader);
                niv11.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( rating1.startsWith("19") && mPref.isAdultVerification() == false ) {
                            String alertTitle = "C&M NScreen";
                            String alertMsg1 = mFragment.getActivity().getResources().getString(R.string.adult_auth_message);
                            String alertMsg2 = "";
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

                setPromotionSticker(promotionSticker1, eight_vod_poster_promotionsticker_imageview1);
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
                niv21.setImageUrl(imageFileName2, mImageLoader);
                niv21.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( rating2.startsWith("19") && mPref.isAdultVerification() == false ) {
                            String alertTitle = "C&M NScreen";
                            String alertMsg1 = mFragment.getActivity().getResources().getString(R.string.adult_auth_message);
                            String alertMsg2 = "";
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

                setPromotionSticker(promotionSticker2, eight_vod_poster_promotionsticker_imageview2);
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
                niv31.setImageUrl(imageFileName3, mImageLoader);
                niv31.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( rating3.startsWith("19") && mPref.isAdultVerification() == false ) {
                            String alertTitle = "C&M NScreen";
                            String alertMsg1 = mFragment.getActivity().getResources().getString(R.string.adult_auth_message);
                            String alertMsg2 = "";
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

                setPromotionSticker(promotionSticker3, eight_vod_poster_promotionsticker_imageview3);
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
                niv41.setImageUrl(imageFileName4, mImageLoader);
                niv41.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( rating4.startsWith("19") && mPref.isAdultVerification() == false ) {
                            String alertTitle = "C&M NScreen";
                            String alertMsg1 = mFragment.getActivity().getResources().getString(R.string.adult_auth_message);
                            String alertMsg2 = "";
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

                setPromotionSticker(promotionSticker4, eight_vod_poster_promotionsticker_imageview4);
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
                niv51.setImageUrl(imageFileName5, mImageLoader);
                niv51.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( rating5.startsWith("19") && mPref.isAdultVerification() == false ) {
                            String alertTitle = "C&M NScreen";
                            String alertMsg1 = mFragment.getActivity().getResources().getString(R.string.adult_auth_message);
                            String alertMsg2 = "";
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

                setPromotionSticker(promotionSticker5, eight_vod_poster_promotionsticker_imageview5);
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
                niv61.setImageUrl(imageFileName6, mImageLoader);
                niv61.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( rating6.startsWith("19") && mPref.isAdultVerification() == false ) {
                            String alertTitle = "C&M NScreen";
                            String alertMsg1 = mFragment.getActivity().getResources().getString(R.string.adult_auth_message);
                            String alertMsg2 = "";
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

                setPromotionSticker(promotionSticker6, eight_vod_poster_promotionsticker_imageview6);
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
                niv71.setImageUrl(imageFileName7, mImageLoader);
                niv71.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (rating7.startsWith("19") && mPref.isAdultVerification() == false) {
                            String alertTitle = "C&M NScreen";
                            String alertMsg1 = mFragment.getActivity().getResources().getString(R.string.adult_auth_message);
                            String alertMsg2 = "";
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

                setPromotionSticker(promotionSticker7, eight_vod_poster_promotionsticker_imageview7);
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
                niv81.setImageUrl(imageFileName8, mImageLoader);
                niv81.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( rating8.startsWith("19") && mPref.isAdultVerification() == false ) {
                            String alertTitle = "C&M NScreen";
                            String alertMsg1 = mFragment.getActivity().getResources().getString(R.string.adult_auth_message);
                            String alertMsg2 = "";
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

                setPromotionSticker(promotionSticker8, eight_vod_poster_promotionsticker_imageview8);
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


            ImageView indi1 = (ImageView)v.findViewById(R.id.eight_vod_poster_indicator_imageview1);
            ImageView indi2 = (ImageView)v.findViewById(R.id.eight_vod_poster_indicator_imageview2);
            ImageView indi3 = (ImageView)v.findViewById(R.id.eight_vod_poster_indicator_imageview3);
            ImageView indi4 = (ImageView)v.findViewById(R.id.eight_vod_poster_indicator_imageview4);
            ImageView indi5 = (ImageView)v.findViewById(R.id.eight_vod_poster_indicator_imageview5);
            UiUtil.setIndicatorImage(position, getCount(), indi1, indi2, indi3, indi4, indi5, null, null, null, null, null);



        } catch (JSONException e) {
            e.printStackTrace();
        }


        ((ViewPager)pager).addView(v); // ((ViewPager)pager).addView(v, position);

        return v;
    }
}
