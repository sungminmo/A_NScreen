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
                niv11.setImageUrl(imageFileName1, mImageLoader);
                niv11.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( rating1.startsWith("19") && mPref.isAdultVerification() == false ) {
                            AlertDialog.Builder ad = new AlertDialog.Builder(mFragment.getActivity());
                            ad.setTitle("알림").setMessage(mFragment.getActivity().getResources().getString(R.string.adult_auth_message)).setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(mFragment.getActivity(), CMSettingMainActivity.class);
                                    mFragment.getActivity().startActivity(intent);
                                }
                            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {// 'No'
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = ad.create();
                            alert.show();
                        } else {
                            Intent intent = new Intent(mFragment.getActivity(), VodDetailActivity.class);
                            intent.putExtra("assetId", assetId1);
                            intent.putExtra("jstr", jo1.toString());
                            mFragment.getActivity().startActivity(intent);
                        }
                    }
                });

                setPromotionSticker(promotionSticker1, eight_vod_poster_promotionsticker_imageview1);
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
                niv21.setImageUrl(imageFileName2, mImageLoader);
                niv21.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( rating2.startsWith("19") && mPref.isAdultVerification() == false ) {
                            AlertDialog.Builder ad = new AlertDialog.Builder(mFragment.getActivity());
                            ad.setTitle("알림").setMessage(mFragment.getActivity().getResources().getString(R.string.adult_auth_message)).setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(mFragment.getActivity(), CMSettingMainActivity.class);
                                    mFragment.getActivity().startActivity(intent);
                                }
                            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {// 'No'
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = ad.create();
                            alert.show();
                        } else {
                            Intent intent = new Intent(mFragment.getActivity(), VodDetailActivity.class);
                            intent.putExtra("assetId", assetId2);
                            intent.putExtra("jstr", jo2.toString());
                            mFragment.getActivity().startActivity(intent);
                        }
                    }
                });

                setPromotionSticker(promotionSticker2, eight_vod_poster_promotionsticker_imageview2);
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
                niv31.setImageUrl(imageFileName3, mImageLoader);
                niv31.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( rating3.startsWith("19") && mPref.isAdultVerification() == false ) {
                            AlertDialog.Builder ad = new AlertDialog.Builder(mFragment.getActivity());
                            ad.setTitle("알림").setMessage(mFragment.getActivity().getResources().getString(R.string.adult_auth_message)).setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(mFragment.getActivity(), CMSettingMainActivity.class);
                                    mFragment.getActivity().startActivity(intent);
                                }
                            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {// 'No'
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = ad.create();
                            alert.show();
                        } else {
                            Intent intent = new Intent(mFragment.getActivity(), VodDetailActivity.class);
                            intent.putExtra("assetId", assetId3);
                            intent.putExtra("jstr", jo3.toString());
                            mFragment.getActivity().startActivity(intent);
                        }
                    }
                });

                setPromotionSticker(promotionSticker3, eight_vod_poster_promotionsticker_imageview3);
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
                niv41.setImageUrl(imageFileName4, mImageLoader);
                niv41.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( rating4.startsWith("19") && mPref.isAdultVerification() == false ) {
                            AlertDialog.Builder ad = new AlertDialog.Builder(mFragment.getActivity());
                            ad.setTitle("알림").setMessage(mFragment.getActivity().getResources().getString(R.string.adult_auth_message)).setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(mFragment.getActivity(), CMSettingMainActivity.class);
                                    mFragment.getActivity().startActivity(intent);
                                }
                            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {// 'No'
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = ad.create();
                            alert.show();
                        } else {
                            Intent intent = new Intent(mFragment.getActivity(), VodDetailActivity.class);
                            intent.putExtra("assetId", assetId4);
                            intent.putExtra("jstr", jo4.toString());
                            mFragment.getActivity().startActivity(intent);
                        }
                    }
                });

                setPromotionSticker(promotionSticker4, eight_vod_poster_promotionsticker_imageview4);
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
                niv51.setImageUrl(imageFileName5, mImageLoader);
                niv51.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( rating5.startsWith("19") && mPref.isAdultVerification() == false ) {
                            AlertDialog.Builder ad = new AlertDialog.Builder(mFragment.getActivity());
                            ad.setTitle("알림").setMessage(mFragment.getActivity().getResources().getString(R.string.adult_auth_message)).setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(mFragment.getActivity(), CMSettingMainActivity.class);
                                    mFragment.getActivity().startActivity(intent);
                                }
                            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {// 'No'
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = ad.create();
                            alert.show();
                        } else {
                            Intent intent = new Intent(mFragment.getActivity(), VodDetailActivity.class);
                            intent.putExtra("assetId", assetId5);
                            intent.putExtra("jstr", jo5.toString());
                            mFragment.getActivity().startActivity(intent);
                        }
                    }
                });

                setPromotionSticker(promotionSticker5, eight_vod_poster_promotionsticker_imageview5);
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
                niv61.setImageUrl(imageFileName6, mImageLoader);
                niv61.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( rating6.startsWith("19") && mPref.isAdultVerification() == false ) {
                            AlertDialog.Builder ad = new AlertDialog.Builder(mFragment.getActivity());
                            ad.setTitle("알림").setMessage(mFragment.getActivity().getResources().getString(R.string.adult_auth_message)).setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(mFragment.getActivity(), CMSettingMainActivity.class);
                                    mFragment.getActivity().startActivity(intent);
                                }
                            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {// 'No'
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = ad.create();
                            alert.show();
                        } else {
                            Intent intent = new Intent(mFragment.getActivity(), VodDetailActivity.class);
                            intent.putExtra("assetId", assetId6);
                            intent.putExtra("jstr", jo6.toString());
                            mFragment.getActivity().startActivity(intent);
                        }
                    }
                });

                setPromotionSticker(promotionSticker6, eight_vod_poster_promotionsticker_imageview6);
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
                niv71.setImageUrl(imageFileName7, mImageLoader);
                niv71.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( rating7.startsWith("19") && mPref.isAdultVerification() == false ) {
                            AlertDialog.Builder ad = new AlertDialog.Builder(mFragment.getActivity());
                            ad.setTitle("알림").setMessage(mFragment.getActivity().getResources().getString(R.string.adult_auth_message)).setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(mFragment.getActivity(), CMSettingMainActivity.class);
                                    mFragment.getActivity().startActivity(intent);
                                }
                            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {// 'No'
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = ad.create();
                            alert.show();
                        } else {
                            Intent intent = new Intent(mFragment.getActivity(), VodDetailActivity.class);
                            intent.putExtra("assetId", assetId7);
                            intent.putExtra("jstr", jo7.toString());
                            mFragment.getActivity().startActivity(intent);
                        }
                    }
                });

                setPromotionSticker(promotionSticker7, eight_vod_poster_promotionsticker_imageview7);
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
                niv81.setImageUrl(imageFileName8, mImageLoader);
                niv81.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( rating8.startsWith("19") && mPref.isAdultVerification() == false ) {
                            AlertDialog.Builder ad = new AlertDialog.Builder(mFragment.getActivity());
                            ad.setTitle("알림").setMessage(mFragment.getActivity().getResources().getString(R.string.adult_auth_message)).setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(mFragment.getActivity(), CMSettingMainActivity.class);
                                    mFragment.getActivity().startActivity(intent);
                                }
                            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {// 'No'
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = ad.create();
                            alert.show();
                        } else {
                            Intent intent = new Intent(mFragment.getActivity(), VodDetailActivity.class);
                            intent.putExtra("assetId", assetId8);
                            intent.putExtra("jstr", jo8.toString());
                            mFragment.getActivity().startActivity(intent);
                        }
                    }
                });

                setPromotionSticker(promotionSticker8, eight_vod_poster_promotionsticker_imageview8);
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
