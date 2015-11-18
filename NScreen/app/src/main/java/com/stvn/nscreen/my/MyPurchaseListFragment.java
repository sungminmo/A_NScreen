package com.stvn.nscreen.my;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.CMDateUtil;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.common.ListViewDataObject;
import com.jjiya.android.http.JYStringRequest;
import com.stvn.nscreen.R;
import com.stvn.nscreen.common.BaseSwipeListViewListener;
import com.stvn.nscreen.common.SwipeListView;
import com.stvn.nscreen.util.CMAlertUtil;
import com.stvn.nscreen.vod.VodDetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by leejunghoon on 15. 10. 31..
 */

public class MyPurchaseListFragment extends Fragment implements View.OnClickListener,AbsListView.OnScrollListener {

    private final int TAB_MOBILE = 0;
    private final int TAB_TV = 1;

    private LayoutInflater mInflater;
    private View mPurchasetab1;
    private View mPurchasetab2;
    private TextView mPurchasecount;
    private SwipeListView mListView;
    private MyPurchaseListAdapter mAdapter;
    private ArrayList<ListViewDataObject> mList = new ArrayList<>();
    private boolean mLockListView = true;
    private int mTabIndex;

    private RequestQueue mRequestQueue;
    private JYSharedPreferences mPref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        this.mPref = new JYSharedPreferences(getActivity());
        this.mRequestQueue = Volley.newRequestQueue(getActivity());
        return mInflater.inflate(R.layout.fragment_mycnm_purchaselist,null);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
    }

    private void initView()
    {
        mPurchasetab1 = (View)getView().findViewById(R.id.purchasetab1);
        mPurchasetab2 = (View)getView().findViewById(R.id.purchasetab2);
        mPurchasecount = (TextView)getView().findViewById(R.id.purchasecount);

        mPurchasetab1.setOnClickListener(this);
        mPurchasetab2.setOnClickListener(this);

        mListView = (SwipeListView)getView().findViewById(R.id.purchaselistview);
        mAdapter = new MyPurchaseListAdapter(getActivity(), mList);
        mAdapter.setmClicklitener(this);

        mListView.setAdapter(mAdapter);
        mListView.setOnScrollListener(this);
        mListView.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onOpened(int position, boolean toRight) {
                Log.d("ljh", "onOpend");
            }

            @Override
            public void onClosed(int position, boolean fromRight) {
                Log.d("ljh", "onClosed");
            }

            @Override
            public void onListChanged() {
                Log.d("ljh", "onListChanged");
            }

            @Override
            public void onMove(int position, float x) {
                Log.d("ljh", "onMove");

            }

            @Override
            public void onStartOpen(int position, int action, boolean right) {
                mListView.closeOpenedItems();
                Log.d("ljh", "onStartOpen");
            }

            @Override
            public void onStartClose(int position, boolean right) {
                Log.d("ljh", "onStartClose");
            }

            @Override
            public void onClickFrontView(int position) {
                ListViewDataObject obj = mList.get(position);

                String assetId = "";
                try {
                    JSONObject jsonObj = new JSONObject(obj.sJson);
                    assetId = jsonObj.getString("assetId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (TextUtils.isEmpty(assetId) == false) {
                    Intent intent = new Intent(getActivity(), VodDetailActivity.class);
                    intent.putExtra("assetId", assetId);
                    startActivity(intent);
                }
            }

            @Override
            public void onClickBackView(int position) {
                Log.d("ljh", "onClickFrontView");
            }

            /**
             * Swipe 처리 유
             * Default Swipe : SwipeListView.SWIPE_MODE_DEFAULT
             * Swipe None : SwipeListView.SWIPE_MODE_NONE
             * */
            @Override
            public int onChangeSwipeMode(int position) {
                return super.onChangeSwipeMode(position);
            }

            /**
             * 삭제 처리
             * */
            public void onDismiss(int[] reverseSortedPositions) {
                for (int position : reverseSortedPositions) {
                    mList.remove(position);
                }
                mAdapter.notifyDataSetChanged();

                int count = mList.size();
                setPurchaseListCountText(count);
            }

            @Override
            public void onListScrolled() {
                super.onListScrolled();
                mListView.closeOpenedItems();
            }

        });

        changeTabWithIndex(TAB_MOBILE);
    }


//    private void initData() {
//
//        int count = 20;
//        if (mTabIndex == TAB_TV) {
//            count = 7;
//        }
//
//        for(int i=0;i<count;i++) {
////            mList.add(""+i);
//        }
//
//        mLockListView = false;
//        mAdapter.notifyDataSetChanged();
//        mListView.closeOpenedItems();;
//        setPurchaseListCountText(count);
//    }

    /**
     * 조회 개수 문구 설정
     * */
    private void setPurchaseListCountText(int count) {
        mPurchasecount.setText(count + "개의 VOD 구매목록이 있습니다.");
    }

    /**
     * 구매목록 삭제 처리
     * TODO:해당 구매목록정보에서 유효기간 확인 후 해당 내용에 대한 처리를 한다.
     * */
    private void deletePurchaseItem(final int itemIndex) {

        boolean isExpired = false;
        String vodTitle = "";
        ListViewDataObject obj = mList.get(itemIndex);
        try {
            JSONObject jsonObj = new JSONObject(obj.sJson);
            String licenseEnd = jsonObj.getString("licenseEnd");
            vodTitle = jsonObj.getString("productName");
            if (TextUtils.isEmpty(CMDateUtil.getLicenseRemainDate(licenseEnd))) {
                isExpired = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (isExpired == true) {
            String alertTitle = getString(R.string.my_cnm_alert_title_expired);
            String alertMessage1 = getString(R.string.my_cnm_alert_message1_expired);
            String alertMessage2 = getString(R.string.my_cnm_alert_message2_expired);
            CMAlertUtil.Alert(getActivity(), alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mListView.dismiss(itemIndex);
                }
            }, true);
        }
        else {
            String alertTitle = "VOD 구매목록 삭제";
            String alertMessage1 = "선택하신 VOD를 구매목록에서 삭제하시겠습니까?";
            Spannable alertMessage2 = (Spannable)Html.fromHtml(vodTitle + "<br/><font color=\"red\">삭제하신 VOD는 복구가 불가능합니다.</font>");
            CMAlertUtil.Alert(getActivity(), alertTitle, alertMessage1, alertMessage2, "예", "아니오", true, false,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mListView.dismiss(itemIndex);
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mListView.closeOpenedItems();
                        }
                    });
        }
    }

    /**
     * 모바일 구매목록/TV 구매목록 탭 이벤트
     * */
    private void changeTabWithIndex(int index) {
        mTabIndex = index;
        if (TAB_MOBILE == index) {
            mPurchasetab1.setSelected(true);
            mPurchasetab2.setSelected(false);
        } else if (TAB_TV == index) {
            mPurchasetab1.setSelected(false);
            mPurchasetab2.setSelected(true);
        }

        mListView.closeOpenedItems();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mList.clear();
                setPurchaseListCountText(mList.size());
                requestGetValidPurchaseLogList();
            }
        }, 300);
    }

    private void requestGetValidPurchaseLogList() {
        ((MyMainActivity)getActivity()).showProgressDialog("", getString(R.string.wait_a_moment));
        String terminalKey = mPref.getWebhasTerminalKey();

        String url = mPref.getWebhasServerUrl() + "/getValidPurchaseLogList.json?version=1&terminalKey="+terminalKey+"&purchaseLogProfile=2";
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ((MyMainActivity) getActivity()).hideProgressDialog();

                try {

                    /**
                     * *테스트용 샘플 데이터
                    response = "{\"transactionId\":null,\"totalCount\":7,\"purchaseLogList\":[\n" +
                            "{\"purchaseDeviceType\" : \"1\",  \"purchasedId\" : \"171742\", \"purchasedTime\" : \"2013-01-01 17:26:49\", assetId\" : \"cjc|T00000000000000A97D6\",\"assetTitle\" : \"토마스와친구들11 03화\", \"productId\" : \"476\",\"goodId\" : \"99506\",\"productType\" : \"RVOD\",\"productName\" : \"토마스와친구들11 03화\",\"price\" : 1500,\"paymentType\" : \"normal\",\"purchaseMethod\" : 2,\"categoryId\" : \"184939\",\"director\" : \"미상\", \"writer\" : \"\",\"starring\" : \"\",\"production\" : \"\",\"synopsis\" : \"오늘은 소도어섬에 매우 중요한 날이에요. 새로운 도서관이 문을 열거에요. 모두들 매우 기대하고 있었죠. 모든 아이들은 물론 어른들도 책 읽는 것과 이야기 듣는 것을 매우 좋아했어요. 새로운 도서관에서 매우 유명한 이야기꾼이 모두에게 이야기를 읽어준다는...\", \"genre\" : \"Animation\",\"runningTime\" : \"00:40\",\"rating\" : \"All\",\"soundMix\" : \"Dolby Digital\", \"copyProtection\" : true,\"encryption\" : true,\"HDContent\" : false,\"threeDimIndicator\" : \"00\",\"licenseStart\" : \"2012-12-05 00:00:00\", \"licenseEnd\" : \"2015-01-31 23:59:59\", \"353ilename\" : \"M0068191.mpg.cim2t.mpg\", \"imageFileName\" : \"ALYS000000008359412.jpg\", \"reviewRatingTotal\" : 0,\"reviewRatingCount\" : 0,\"seriesLink\" : 0,\"seriesId\" : \"\",\"seriesName\" : \"\",\"seriesCurIndex\" : 0,\"viewablePeriod\" : \"0000-00-01 00:00:00\"},\n" +
                            "{\"purchaseDeviceType\" : \"0\",  \"purchasedId\" : \"171743\", \"purchasedTime\" : \"2013-08-07 18:45:49\", assetId\" : \"cjc|T00000000000000A97D6\",\"assetTitle\" : \"토마스와친구들11 03화\", \"productId\" : \"477\",\"goodId\" : \"99506\",\"productType\" : \"RVOD\",\"productName\" : \"토마스와친구들11 03화\",\"price\" : 5500,\"paymentType\" : \"normal\",\"purchaseMethod\" : 2,\"categoryId\" : \"184939\",\"director\" : \"미상\", \"writer\" : \"\",\"starring\" : \"\",\"production\" : \"\",\"synopsis\" : \"오늘은 소도어섬에 매우 중요한 날이에요. 새로운 도서관이 문을 열거에요. 모두들 매우 기대하고 있었죠. 모든 아이들은 물론 어른들도 책 읽는 것과 이야기 듣는 것을 매우 좋아했어요. 새로운 도서관에서 매우 유명한 이야기꾼이 모두에게 이야기를 읽어준다는...\", \"genre\" : \"Animation\",\"runningTime\" : \"00:40\",\"rating\" : \"All\",\"soundMix\" : \"Dolby Digital\", \"copyProtection\" : true,\"encryption\" : true,\"HDContent\" : false,\"threeDimIndicator\" : \"00\",\"licenseStart\" : \"2012-12-05 00:00:00\", \"licenseEnd\" : \"2014-12-31 23:59:59\", \"353ilename\" : \"M0068191.mpg.cim2t.mpg\", \"imageFileName\" : \"ALYS000000008359412.jpg\", \"reviewRatingTotal\" : 0,\"reviewRatingCount\" : 0,\"seriesLink\" : 0,\"seriesId\" : \"\",\"seriesName\" : \"\",\"seriesCurIndex\" : 0,\"viewablePeriod\" : \"0000-00-01 00:00:00\"},\n" +
                            "{\"purchaseDeviceType\" : \"1\",  \"purchasedId\" : \"171744\", \"purchasedTime\" : \"2013-09-19 17:36:49\", assetId\" : \"cjc|T00000000000000A97D6\",\"assetTitle\" : \"토마스와친구들11 03화\", \"productId\" : \"478\",\"goodId\" : \"99506\",\"productType\" : \"RVOD\",\"productName\" : \"토마스와친구들11 03화\",\"price\" : 6500,\"paymentType\" : \"normal\",\"purchaseMethod\" : 2,\"categoryId\" : \"184939\",\"director\" : \"미상\", \"writer\" : \"\",\"starring\" : \"\",\"production\" : \"\",\"synopsis\" : \"오늘은 소도어섬에 매우 중요한 날이에요. 새로운 도서관이 문을 열거에요. 모두들 매우 기대하고 있었죠. 모든 아이들은 물론 어른들도 책 읽는 것과 이야기 듣는 것을 매우 좋아했어요. 새로운 도서관에서 매우 유명한 이야기꾼이 모두에게 이야기를 읽어준다는...\", \"genre\" : \"Animation\",\"runningTime\" : \"00:40\",\"rating\" : \"All\",\"soundMix\" : \"Dolby Digital\", \"copyProtection\" : true,\"encryption\" : true,\"HDContent\" : false,\"threeDimIndicator\" : \"00\",\"licenseStart\" : \"2012-12-05 00:00:00\", \"licenseEnd\" : \"2015-11-31 23:59:59\", \"353ilename\" : \"M0068191.mpg.cim2t.mpg\", \"imageFileName\" : \"ALYS000000008359412.jpg\", \"reviewRatingTotal\" : 0,\"reviewRatingCount\" : 0,\"seriesLink\" : 0,\"seriesId\" : \"\",\"seriesName\" : \"\",\"seriesCurIndex\" : 0,\"viewablePeriod\" : \"0000-00-01 00:00:00\"},\n" +
                            "{\"purchaseDeviceType\" : \"0\",  \"purchasedId\" : \"171745\", \"purchasedTime\" : \"2013-12-11 04:28:49\", assetId\" : \"cjc|T00000000000000A97D6\",\"assetTitle\" : \"토마스와친구들11 03화\", \"productId\" : \"479\",\"goodId\" : \"99506\",\"productType\" : \"RVOD\",\"productName\" : \"토마스와친구들11 03화\",\"price\" : 2500,\"paymentType\" : \"normal\",\"purchaseMethod\" : 2,\"categoryId\" : \"184939\",\"director\" : \"미상\", \"writer\" : \"\",\"starring\" : \"\",\"production\" : \"\",\"synopsis\" : \"오늘은 소도어섬에 매우 중요한 날이에요. 새로운 도서관이 문을 열거에요. 모두들 매우 기대하고 있었죠. 모든 아이들은 물론 어른들도 책 읽는 것과 이야기 듣는 것을 매우 좋아했어요. 새로운 도서관에서 매우 유명한 이야기꾼이 모두에게 이야기를 읽어준다는...\", \"genre\" : \"Animation\",\"runningTime\" : \"00:40\",\"rating\" : \"All\",\"soundMix\" : \"Dolby Digital\", \"copyProtection\" : true,\"encryption\" : true,\"HDContent\" : false,\"threeDimIndicator\" : \"00\",\"licenseStart\" : \"2012-12-05 00:00:00\", \"licenseEnd\" : \"2015-12-31 23:59:59\", \"353ilename\" : \"M0068191.mpg.cim2t.mpg\", \"imageFileName\" : \"ALYS000000008359412.jpg\", \"reviewRatingTotal\" : 0,\"reviewRatingCount\" : 0,\"seriesLink\" : 0,\"seriesId\" : \"\",\"seriesName\" : \"\",\"seriesCurIndex\" : 0,\"viewablePeriod\" : \"0000-00-01 00:00:00\"},\n" +
                            "{\"purchaseDeviceType\" : \"0\",  \"purchasedId\" : \"171746\", \"purchasedTime\" : \"2013-04-23 20:19:49\", assetId\" : \"cjc|T00000000000000A97D6\",\"assetTitle\" : \"토마스와친구들11 03화\", \"productId\" : \"480\",\"goodId\" : \"99506\",\"productType\" : \"RVOD\",\"productName\" : \"토마스와친구들11 03화\",\"price\" : 3500,\"paymentType\" : \"normal\",\"purchaseMethod\" : 2,\"categoryId\" : \"184939\",\"director\" : \"미상\", \"writer\" : \"\",\"starring\" : \"\",\"production\" : \"\",\"synopsis\" : \"오늘은 소도어섬에 매우 중요한 날이에요. 새로운 도서관이 문을 열거에요. 모두들 매우 기대하고 있었죠. 모든 아이들은 물론 어른들도 책 읽는 것과 이야기 듣는 것을 매우 좋아했어요. 새로운 도서관에서 매우 유명한 이야기꾼이 모두에게 이야기를 읽어준다는...\", \"genre\" : \"Animation\",\"runningTime\" : \"00:40\",\"rating\" : \"All\",\"soundMix\" : \"Dolby Digital\", \"copyProtection\" : true,\"encryption\" : true,\"HDContent\" : false,\"threeDimIndicator\" : \"00\",\"licenseStart\" : \"2012-12-05 00:00:00\", \"licenseEnd\" : \"2015-11-18 23:59:59\", \"353ilename\" : \"M0068191.mpg.cim2t.mpg\", \"imageFileName\" : \"ALYS000000008359412.jpg\", \"reviewRatingTotal\" : 0,\"reviewRatingCount\" : 0,\"seriesLink\" : 0,\"seriesId\" : \"\",\"seriesName\" : \"\",\"seriesCurIndex\" : 0,\"viewablePeriod\" : \"0000-00-01 00:00:00\"},\n" +
                            "{\"purchaseDeviceType\" : \"1\",  \"purchasedId\" : \"171747\", \"purchasedTime\" : \"2013-06-30 11:11:49\", assetId\" : \"cjc|T00000000000000A97D6\",\"assetTitle\" : \"토마스와친구들11 03화\", \"productId\" : \"481\",\"goodId\" : \"99506\",\"productType\" : \"RVOD\",\"productName\" : \"토마스와친구들11 03화\",\"price\" : 2800,\"paymentType\" : \"normal\",\"purchaseMethod\" : 2,\"categoryId\" : \"184939\",\"director\" : \"미상\", \"writer\" : \"\",\"starring\" : \"\",\"production\" : \"\",\"synopsis\" : \"오늘은 소도어섬에 매우 중요한 날이에요. 새로운 도서관이 문을 열거에요. 모두들 매우 기대하고 있었죠. 모든 아이들은 물론 어른들도 책 읽는 것과 이야기 듣는 것을 매우 좋아했어요. 새로운 도서관에서 매우 유명한 이야기꾼이 모두에게 이야기를 읽어준다는...\", \"genre\" : \"Animation\",\"runningTime\" : \"00:40\",\"rating\" : \"All\",\"soundMix\" : \"Dolby Digital\", \"copyProtection\" : true,\"encryption\" : true,\"HDContent\" : false,\"threeDimIndicator\" : \"00\",\"licenseStart\" : \"2012-12-05 00:00:00\", \"licenseEnd\" : \"2015-11-16 20:59:59\", \"353ilename\" : \"M0068191.mpg.cim2t.mpg\", \"imageFileName\" : \"ALYS000000008359412.jpg\", \"reviewRatingTotal\" : 0,\"reviewRatingCount\" : 0,\"seriesLink\" : 0,\"seriesId\" : \"\",\"seriesName\" : \"\",\"seriesCurIndex\" : 0,\"viewablePeriod\" : \"0000-00-01 00:00:00\"},\n" +
                            "{\"purchaseDeviceType\" : \"1\",  \"purchasedId\" : \"171748\", \"purchasedTime\" : \"2013-02-29 17:08:49\", assetId\" : \"cjc|T00000000000000A97D6\",\"assetTitle\" : \"토마스와친구들11 03화\", \"productId\" : \"482\",\"goodId\" : \"99506\",\"productType\" : \"RVOD\",\"productName\" : \"토마스와친구들11 03화\",\"price\" : 2900,\"paymentType\" : \"normal\",\"purchaseMethod\" : 2,\"categoryId\" : \"184939\",\"director\" : \"미상\", \"writer\" : \"\",\"starring\" : \"\",\"production\" : \"\",\"synopsis\" : \"오늘은 소도어섬에 매우 중요한 날이에요. 새로운 도서관이 문을 열거에요. 모두들 매우 기대하고 있었죠. 모든 아이들은 물론 어른들도 책 읽는 것과 이야기 듣는 것을 매우 좋아했어요. 새로운 도서관에서 매우 유명한 이야기꾼이 모두에게 이야기를 읽어준다는...\", \"genre\" : \"Animation\",\"runningTime\" : \"00:40\",\"rating\" : \"All\",\"soundMix\" : \"Dolby Digital\", \"copyProtection\" : true,\"encryption\" : true,\"HDContent\" : false,\"threeDimIndicator\" : \"00\",\"licenseStart\" : \"2012-12-05 00:00:00\", \"licenseEnd\" : \"2015-11-16 23:59:59\", \"353ilename\" : \"M0068191.mpg.cim2t.mpg\", \"imageFileName\" : \"ALYS000000008359412.jpg\", \"reviewRatingTotal\" : 0,\"reviewRatingCount\" : 0,\"seriesLink\" : 0,\"seriesId\" : \"\",\"seriesName\" : \"\",\"seriesCurIndex\" : 0,\"viewablePeriod\" : \"0000-00-01 00:00:00\"}\n" +
                            "],\"resultCode\":100,\"totalPage\":0,\"errorString\":\"\",\"version\":\"1\"}";
                    */
                    JSONObject responseObject = new JSONObject(response);

                    JSONArray puchaseArray = responseObject.getJSONArray("purchaseLogList");
                    for (int i = 0; i < puchaseArray.length(); i++) {
                        JSONObject jsonObj = puchaseArray.getJSONObject(i);
                        ListViewDataObject obj = new ListViewDataObject(i, 0, jsonObj.toString());

                        String purchaseDeviceType = jsonObj.getString("purchaseDeviceType");
                        if (TAB_MOBILE == mTabIndex && "1".equals(purchaseDeviceType)) {
                            mList.add(obj);
                        } else if (TAB_TV == mTabIndex && "1".equals(purchaseDeviceType) == false) {
                            mList.add(obj);
                        }
                    }

                    setPurchaseListCountText(mList.size());
                    mAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ((MyMainActivity)getActivity()).hideProgressDialog();
                if ( mPref.isLogging() ) { VolleyLog.d("", "onErrorResponse(): " + error.getMessage()); }
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("version", String.valueOf(1));
                if ( mPref.isLogging() ) { Log.d("", "getParams()" + params.toString()); }
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.purchasetab1:                 // 모바일 구매목록
                changeTabWithIndex(TAB_MOBILE);
                break;
            case R.id.purchasetab2:                 // TV구매목록
                changeTabWithIndex(TAB_TV);
                break;
            case R.id.btn1:
                deletePurchaseItem((int)v.getTag());
                break;
        }

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int count = totalItemCount - visibleItemCount;
        if(firstVisibleItem >= count && totalItemCount != 0
                && mLockListView == false)
        {
            // 리스트 페이징 처리

        }
    }
}
