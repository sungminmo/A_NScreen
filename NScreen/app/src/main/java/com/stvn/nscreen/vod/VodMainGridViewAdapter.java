package com.stvn.nscreen.vod;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.common.ListViewDataObject;
import com.jjiya.android.common.ViewHolder;
import com.jjiya.android.common.VolleySingleton;
import com.stvn.nscreen.R;
import com.stvn.nscreen.setting.CMSettingMainActivity;
import com.stvn.nscreen.util.CMAlertUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.jjiya.android.common.UiUtil.setPromotionSticker;

/**
 * Created by swlim on 2015. 9. 11..
 */

public class VodMainGridViewAdapter extends BaseAdapter {

    private static final String                        tag              = VodMainGridViewAdapter.class.getSimpleName();
    private              Context                       mContext         = null;
    private              View.OnClickListener          mOnClickListener = null;
    private              JYSharedPreferences           mPref;
    private              ArrayList<ListViewDataObject> mDatas           = new ArrayList<ListViewDataObject>();

    private              ImageLoader                   mImageLoader;
    private              VodMainOtherTabFragment       mVodMainOtherTabFragment;

    public VodMainGridViewAdapter(VodMainOtherTabFragment fragment, Context c, View.OnClickListener onClickListener) {
        super();

        this.mContext         = c;
        this.mOnClickListener = onClickListener;
        this.mImageLoader     = VolleySingleton.getInstance().getImageLoader();
        this.mVodMainOtherTabFragment = fragment;
        mPref                 = new JYSharedPreferences(c);
    }

    @Override
    public int getCount() { return mDatas.size(); }

    @Override
    public Object getItem(int position) { return mDatas.get(position); }

    @Override
    public long getItemId(int position) { return mDatas.get(position).iKey; }

    public void set(int position, ListViewDataObject obj) { mDatas.set(position, obj); }
    public void addItem(ListViewDataObject obj) { mDatas.add(obj); }
    public void remove(int position) { mDatas.remove(position); }
    public void clear() { mDatas.clear(); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.gridview_vod_main, parent, false);
        }

        try {
            ListViewDataObject obj        = (ListViewDataObject)getItem(position);
            final JSONObject         jo         = new JSONObject(obj.sJson);

            NetworkImageView vodImageView = ViewHolder.get(convertView, R.id.volleyImageView);
            vodImageView.setDefaultImageResId(R.mipmap.posterlist_default);
            TextView titleTextView        = ViewHolder.get(convertView, R.id.vod_main_textview_title);
            TextView rankTextView         = ViewHolder.get(convertView, R.id.gridview_vod_main_ranking_textview);
            TextView gridview_vod_main_ranking_textview = ViewHolder.get(convertView, R.id.gridview_vod_main_ranking_textview);
            ImageView gridview_vod_main_ranking_imageview = ViewHolder.get(convertView, R.id.gridview_vod_main_ranking_imageview);
            ImageView gridview_vod_main_promotionsticker_imageview = ViewHolder.get(convertView, R.id.gridview_vod_main_promotionsticker_imageview);
            ImageView gridview_vod_main_19_imageview = ViewHolder.get(convertView, R.id.gridview_vod_main_19_imageview);
            ImageView gridview_vod_main_tvonly_imageview = ViewHolder.get(convertView, R.id.gridview_vod_main_tvonly_imageview);

            if ( jo.isNull("ranking") ) {
                // 랭킹 없음.
                gridview_vod_main_ranking_imageview.setVisibility(View.GONE);
                gridview_vod_main_ranking_textview.setVisibility(View.GONE);
            } else {
                // 랭킹 있음.
                rankTextView.setText(jo.getString("ranking"));
            }

            String publicationRight = null;
            if ( ! jo.isNull("publicationRight") ) {
                publicationRight = jo.getString("publicationRight");
            } else if ( ! jo.isNull("mobilePublicationRight") ) {
                publicationRight = jo.getString("mobilePublicationRight");
            }
            String publicationRight1 = publicationRight;

            titleTextView.setText(jo.getString("title"));
            String promotionSticker = jo.getString("promotionSticker");
            final String rating = jo.getString("rating");

            Boolean isNew                = false; // 0:없음, 1:있음.
            if ( ! jo.isNull("isNew") ) {
                isNew = jo.getBoolean("isNew");
            }
            String assetNew             = "0"; // 0:없음, 1:new일부만, 2:new단체
            if ( ! jo.isNull("assetNew") ) {
                assetNew = jo.getString("assetNew");
            }
            String assetHot             = "0"; // 0:없음, 1:new일부만, 2:new단체
            if ( ! jo.isNull("assetHot") ) {
                assetHot = jo.getString("assetHot");
            }
            Boolean hot                  = false; // 0:없음, 1:있음.
            if ( ! jo.isNull("hot") ) {
                hot = jo.getBoolean("hot");
            }

            setPromotionSticker(promotionSticker, isNew, hot, assetNew, assetHot, gridview_vod_main_promotionsticker_imageview);
            if ( "1".equals(publicationRight1) || "false".equals(publicationRight1) ) {
                gridview_vod_main_tvonly_imageview.setVisibility(View.VISIBLE);
            }else {
                gridview_vod_main_tvonly_imageview.setVisibility(View.INVISIBLE);
            }
            if ( rating.startsWith("19") && mPref.isAdultVerification() == false ) {
                gridview_vod_main_19_imageview.setVisibility(View.VISIBLE);
            } else {
                gridview_vod_main_19_imageview.setVisibility(View.INVISIBLE);
            }
            String assetId = null;
            if ( ! jo.isNull("assetId") ) {
                assetId = jo.getString("assetId");
            } else if ( ! jo.isNull("primaryAssetId") ) {
                assetId = jo.getString("primaryAssetId");
            }
            final String assetId1 = assetId;
            vodImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ( rating.startsWith("19") && mPref.isAdultVerification() == false ) {
                        String alertTitle = "성인인증 필요";
                        String alertMsg1 = mContext.getResources().getString(R.string.error_not_adult1);
                        String alertMsg2 = mContext.getResources().getString(R.string.error_not_adult2);
                        CMAlertUtil.Alert1(mContext, alertTitle, alertMsg1, alertMsg2, false, true, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(mContext, CMSettingMainActivity.class);
                                mContext.startActivity(intent);
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                    } else {
                        if ( jo.isNull("episodePeerExistence") ) {
                            Intent intent = new Intent(mContext, VodDetailActivity.class);
                            intent.putExtra("assetId", assetId1);
                            intent.putExtra("jstr", jo.toString());
                            mContext.startActivity(intent);
                        } else {
                            try {
                                int     assetBundle          = jo.getInt("assetBundle");
                                String  episodePeerExistence = jo.getString("episodePeerExistence");
                                if ( assetBundle == 1 ) {  // 번들(묶음상품)이면 묶음상품이면, getAssetInfo로 구매여부를 알아낸다.
                                    mVodMainOtherTabFragment.onClickBundulPoster(assetId1);
                                } else {  // 이외는 번들(묶음상품)이 아니다.
                                    Intent intent = new Intent(mContext, VodDetailActivity.class);
                                    if ( "1".equals(episodePeerExistence) ) {
                                        String contentGroupId = jo.getString("contentGroupId");
                                        String primaryAssetId = jo.getString("primaryAssetId");
                                        intent.putExtra("episodePeerExistence", episodePeerExistence);
                                        intent.putExtra("contentGroupId", contentGroupId);
                                        intent.putExtra("primaryAssetId", primaryAssetId);
                                        intent.putExtra("assetId", assetId1);
                                        intent.putExtra("jstr", jo.toString());
                                        mContext.startActivity(intent);
                                    } else {
                                        intent.putExtra("assetId", assetId1);
                                        intent.putExtra("jstr", jo.toString());
                                        mContext.startActivity(intent);
                                    }
                                }
                            } catch ( JSONException e ) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
            String imageFileName = jo.getString("imageFileName");
            vodImageView.setImageUrl(imageFileName, mImageLoader);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }
    // 지정된 범위의 정수 1개를 램덤하게 반환하는 메서드
    // n1 은 "하한값", n2 는 상한값
    public static int randomRange(int n1, int n2) {
        return (int) (Math.random() * (n2 - n1 + 1)) + n1;
    }
}
