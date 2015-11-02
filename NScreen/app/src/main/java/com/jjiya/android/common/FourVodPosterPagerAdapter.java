package com.jjiya.android.common;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.stvn.nscreen.R;
import com.stvn.nscreen.vod.VodDetailFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by swlim on 2015. 10. 22..
 */
public class FourVodPosterPagerAdapter extends PagerAdapter {

    private LayoutInflater   mLayoutInflater;

    private ImageLoader      mImageLoader;
    private List<JSONObject> mVods;

    private Fragment         mFragment;

    public FourVodPosterPagerAdapter(Context c){
        super();
        mLayoutInflater = LayoutInflater.from(c);
        mVods = new ArrayList<JSONObject>();
    }

    public void setFragment(Fragment f) {
        mFragment = f;
    }

    /**
     * 디폴트 메소드 구현 부.
     */
    @Override
    public int getCount() {
        int iCount = mVods.size() / 4;
        if ( mVods.size() % 4 > 0 ) {
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

        v = mLayoutInflater.inflate(R.layout.viewpager_vod_four_vod_poster, null);
        try {
            int index1 = position*4;
            int index2 = position*4+1;
            int index3 = position*4+2;
            int index4 = position*4+3;

            if ( mVods.size() > index1 ) {
                JSONObject jo1 = mVods.get(index1);
                final String assetId1;
                if ( jo1.isNull("primaryAssetId") ) {
                    assetId1 = jo1.getString("assetId");
                } else {
                    assetId1 = jo1.getString("primaryAssetId");
                }
                TextView title1 =  (TextView)v.findViewById(R.id.eight_vod_poster_title_textview1);
                title1.setText(jo1.getString("title"));
                String imageFileName1 = jo1.getString("imageFileName");
                NetworkImageView niv1 = (NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview1);
                niv1.setImageUrl(imageFileName1, mImageLoader);
                niv1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle param = new Bundle();
                        param.putString("assetId", assetId1);
                        FragmentManager fm = mFragment.getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        VodDetailFragment df = new VodDetailFragment();
                        df.setArguments(param);
                        ft.replace(R.id.fragment_placeholder, df);
                        ft.addToBackStack("DetailFragment");
                        ft.commit();
                    }
                });
            } else {
                ((TextView)v.findViewById(R.id.eight_vod_poster_title_textview1)).setVisibility(View.INVISIBLE);
                ((NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview1)).setVisibility(View.INVISIBLE);
            }
            if ( mVods.size() > index2 ) {
                JSONObject jo2 = mVods.get(index2);
                final String assetId2;
                if ( jo2.isNull("primaryAssetId") ) {
                    assetId2 = jo2.getString("assetId");
                } else {
                    assetId2 = jo2.getString("primaryAssetId");
                }
                TextView title2 =  (TextView)v.findViewById(R.id.eight_vod_poster_title_textview2);
                title2.setText(jo2.getString("title"));
                String imageFileName2 = jo2.getString("imageFileName");
                NetworkImageView niv2 = (NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview2);
                niv2.setImageUrl(imageFileName2, mImageLoader);
                niv2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle param = new Bundle();
                        param.putString("assetId", assetId2);
                        FragmentManager fm = mFragment.getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        VodDetailFragment df = new VodDetailFragment();
                        df.setArguments(param);
                        ft.replace(R.id.fragment_placeholder, df);
                        ft.addToBackStack("DetailFragment");
                        ft.commit();
                    }
                });
            } else {
                ((TextView)v.findViewById(R.id.eight_vod_poster_title_textview2)).setVisibility(View.INVISIBLE);
                ((NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview2)).setVisibility(View.INVISIBLE);
            }
            if ( mVods.size() > index3 ) {
                JSONObject jo3 = mVods.get(index3);
                final String assetId3;
                if ( jo3.isNull("primaryAssetId") ) {
                    assetId3 = jo3.getString("assetId");
                } else {
                    assetId3 = jo3.getString("primaryAssetId");
                }
                TextView title3 = (TextView) v.findViewById(R.id.eight_vod_poster_title_textview3);
                title3.setText(jo3.getString("title"));
                String imageFileName3 = jo3.getString("imageFileName");
                NetworkImageView niv3 = (NetworkImageView) v.findViewById(R.id.eight_vod_poster_netwokr_imageview3);
                niv3.setImageUrl(imageFileName3, mImageLoader);
                niv3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle param = new Bundle();
                        param.putString("assetId", assetId3);
                        FragmentManager fm = mFragment.getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        VodDetailFragment df = new VodDetailFragment();
                        df.setArguments(param);
                        ft.replace(R.id.fragment_placeholder, df);
                        ft.addToBackStack("DetailFragment");
                        ft.commit();
                    }
                });
            } else {
                ((TextView)v.findViewById(R.id.eight_vod_poster_title_textview3)).setVisibility(View.INVISIBLE);
                ((NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview3)).setVisibility(View.INVISIBLE);
            }
            if ( mVods.size() > index4 ) {
                JSONObject jo4 = mVods.get(index4);
                final String assetId4;
                if ( jo4.isNull("primaryAssetId") ) {
                    assetId4 = jo4.getString("assetId");
                } else {
                    assetId4 = jo4.getString("primaryAssetId");
                }
                TextView title4 =  (TextView)v.findViewById(R.id.eight_vod_poster_title_textview4);
                title4.setText(jo4.getString("title"));
                String imageFileName4 = jo4.getString("imageFileName");
                NetworkImageView niv4 = (NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview4);
                niv4.setImageUrl(imageFileName4, mImageLoader);
                niv4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle param = new Bundle();
                        param.putString("assetId", assetId4);
                        FragmentManager fm = mFragment.getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        VodDetailFragment df = new VodDetailFragment();
                        df.setArguments(param);
                        ft.replace(R.id.fragment_placeholder, df);
                        ft.addToBackStack("DetailFragment");
                        ft.commit();
                    }
                });
            } else {
                ((TextView)v.findViewById(R.id.eight_vod_poster_title_textview4)).setVisibility(View.INVISIBLE);
                ((NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview4)).setVisibility(View.INVISIBLE);
            }

            ImageView indi1 = (ImageView)v.findViewById(R.id.eight_vod_poster_indicator_imageview1);
            ImageView indi2 = (ImageView)v.findViewById(R.id.eight_vod_poster_indicator_imageview2);
            ImageView indi3 = (ImageView)v.findViewById(R.id.eight_vod_poster_indicator_imageview3);
            ImageView indi4 = (ImageView)v.findViewById(R.id.eight_vod_poster_indicator_imageview4);
            ImageView indi5 = (ImageView)v.findViewById(R.id.eight_vod_poster_indicator_imageview5);
            UiUtil.setIndicatorImage(position, getCount(), indi1, indi2, indi3, indi4, indi5, null, null, null, null, null);




//            if ( position == 0 ) {
//                prev.setVisibility(View.GONE);
//            }
//            if ( position == (mVods.size()-1) ) {
//                next.setVisibility(View.GONE);
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        ((ViewPager)pager).addView(v); //((ViewPager)pager).addView(v, position);

        return v;
    }
}
