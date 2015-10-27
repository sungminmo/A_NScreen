package com.jjiya.android.common;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.stvn.nscreen.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by limdavid on 15. 10. 22..
 */
public class VodNewMoviePosterPagerAdapter extends PagerAdapter {

    private LayoutInflater   mLayoutInflater;

    private ImageLoader      mImageLoader;
    private List<JSONObject> mVods;

    public VodNewMoviePosterPagerAdapter(Context c){
        super();
        mLayoutInflater = LayoutInflater.from(c);
        mVods           = new ArrayList<JSONObject>();
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
                JSONObject jo1 = mVods.get(index1);
                TextView title1 =  (TextView)v.findViewById(R.id.eight_vod_poster_title_textview1);
                title1.setText(jo1.getString("title"));
                String imageFileName1 = jo1.getString("imageFileName");
                NetworkImageView niv1 = (NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview1);
                niv1.setImageUrl(imageFileName1, mImageLoader);
            } else {
                ((TextView)v.findViewById(R.id.eight_vod_poster_title_textview1)).setVisibility(View.INVISIBLE);
                ((NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview1)).setVisibility(View.INVISIBLE);
            }
            if ( mVods.size() > index2 ) {
                JSONObject jo2 = mVods.get(index2);
                TextView title2 =  (TextView)v.findViewById(R.id.eight_vod_poster_title_textview2);
                title2.setText(jo2.getString("title"));
                String imageFileName2 = jo2.getString("imageFileName");
                NetworkImageView niv2 = (NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview2);
                niv2.setImageUrl(imageFileName2, mImageLoader);
            } else {
                ((TextView)v.findViewById(R.id.eight_vod_poster_title_textview2)).setVisibility(View.INVISIBLE);
                ((NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview2)).setVisibility(View.INVISIBLE);
            }
            if ( mVods.size() > index3 ) {
                JSONObject jo3 = mVods.get(index3);
                TextView title3 =  (TextView)v.findViewById(R.id.eight_vod_poster_title_textview3);
                title3.setText(jo3.getString("title"));
                String imageFileName3 = jo3.getString("imageFileName");
                NetworkImageView niv3 = (NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview3);
                niv3.setImageUrl(imageFileName3, mImageLoader);
            } else {
                ((TextView)v.findViewById(R.id.eight_vod_poster_title_textview3)).setVisibility(View.INVISIBLE);
                ((NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview3)).setVisibility(View.INVISIBLE);
            }
            if ( mVods.size() > index4 ) {
                JSONObject jo4 = mVods.get(index4);
                TextView title4 =  (TextView)v.findViewById(R.id.eight_vod_poster_title_textview4);
                title4.setText(jo4.getString("title"));
                String imageFileName4 = jo4.getString("imageFileName");
                NetworkImageView niv4 = (NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview4);
                niv4.setImageUrl(imageFileName4, mImageLoader);
            } else {
                ((TextView)v.findViewById(R.id.eight_vod_poster_title_textview4)).setVisibility(View.INVISIBLE);
                ((NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview4)).setVisibility(View.INVISIBLE);
            }
            if ( mVods.size() > index5 ) {
                JSONObject jo5 = mVods.get(index5);
                TextView title5 =  (TextView)v.findViewById(R.id.eight_vod_poster_title_textview5);
                title5.setText(jo5.getString("title"));
                String imageFileName5 = jo5.getString("imageFileName");
                NetworkImageView niv5 = (NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview5);
                niv5.setImageUrl(imageFileName5, mImageLoader);
            } else {
                ((TextView)v.findViewById(R.id.eight_vod_poster_title_textview5)).setVisibility(View.INVISIBLE);
                ((NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview5)).setVisibility(View.INVISIBLE);
            }
            if ( mVods.size() > index6 ) {
                JSONObject jo6 = mVods.get(index6);
                TextView title6 =  (TextView)v.findViewById(R.id.eight_vod_poster_title_textview6);
                title6.setText(jo6.getString("title"));
                String imageFileName6 = jo6.getString("imageFileName");
                NetworkImageView niv6 = (NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview6);
                niv6.setImageUrl(imageFileName6, mImageLoader);
            } else {
                ((TextView)v.findViewById(R.id.eight_vod_poster_title_textview6)).setVisibility(View.INVISIBLE);
                ((NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview6)).setVisibility(View.INVISIBLE);
            }
            if ( mVods.size() > index7 ) {
                JSONObject jo7 = mVods.get(index7);
                TextView title7 =  (TextView)v.findViewById(R.id.eight_vod_poster_title_textview7);
                title7.setText(jo7.getString("title"));
                String imageFileName7 = jo7.getString("imageFileName");
                NetworkImageView niv7 = (NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview7);
                niv7.setImageUrl(imageFileName7, mImageLoader);
            } else {
                ((TextView)v.findViewById(R.id.eight_vod_poster_title_textview7)).setVisibility(View.INVISIBLE);
                ((NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview7)).setVisibility(View.INVISIBLE);
            }
            if ( mVods.size() > index8 ) {
                JSONObject jo8 = mVods.get(index8);
                TextView title8 =  (TextView)v.findViewById(R.id.eight_vod_poster_title_textview8);
                title8.setText(jo8.getString("title"));
                String imageFileName8 = jo8.getString("imageFileName");
                NetworkImageView niv8 = (NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview8);
                niv8.setImageUrl(imageFileName8, mImageLoader);
            } else {
                ((TextView)v.findViewById(R.id.eight_vod_poster_title_textview8)).setVisibility(View.INVISIBLE);
                ((NetworkImageView)v.findViewById(R.id.eight_vod_poster_netwokr_imageview8)).setVisibility(View.INVISIBLE);
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
