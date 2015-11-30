package com.stvn.nscreen.my;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.jjiya.android.common.Constants;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.common.ListViewDataObject;
import com.jjiya.android.http.JYStringRequest;
import com.stvn.nscreen.R;
import com.stvn.nscreen.common.BaseSwipeListViewListener;
import com.stvn.nscreen.common.SwipeListView;
import com.stvn.nscreen.setting.CMSettingMainActivity;
import com.stvn.nscreen.util.CMAlertUtil;
import com.stvn.nscreen.vod.VodDetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
    private TextView mPurchaseEmptyMsg;
    private SwipeListView mListView;
    private MyPurchaseListAdapter mAdapter;
    private ArrayList<ListViewDataObject> mMoblieList = new ArrayList<>();
    private ArrayList<ListViewDataObject> mTVList     = new ArrayList<>();
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

        this.mMoblieList.clear();
        this.mTVList.clear();
        requestGetPurchasedProductList();
    }

    private void initView()
    {
        mPurchasetab1 = (View)getView().findViewById(R.id.purchasetab1);
        mPurchasetab2 = (View)getView().findViewById(R.id.purchasetab2);
        mPurchasecount = (TextView)getView().findViewById(R.id.purchasecount);

        mPurchaseEmptyMsg = (TextView)getView().findViewById(R.id.purchase_empty_msg);
        mPurchaseEmptyMsg.setVisibility(View.GONE);

        mPurchasetab1.setOnClickListener(this);
        mPurchasetab2.setOnClickListener(this);

        mListView = (SwipeListView)getView().findViewById(R.id.purchaselistview);
        mAdapter = new MyPurchaseListAdapter(getActivity());
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
                ListViewDataObject obj = getCurrentTabObjectWithIndex(position);

                if (obj.remainTime < 0) {
                    String alertTitle = getString(R.string.my_cnm_alert_title_expired);
                    String alertMessage1 = getString(R.string.my_cnm_alert_message1_expired);
                    CMAlertUtil.Alert(getActivity(), alertTitle, alertMessage1, "", true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    }, true);
                    return;
                }

                String assetId = "";
                String primaryAssetId = "";
                String episodePeerExistence = "";
                String contentGroupId = "";
                try {
                    JSONObject jsonObj = new JSONObject(obj.sJson);
                    String rating = jsonObj.getString("rating");
                    if (rating.startsWith("19") && mPref.isAdultVerification() == false) {
                        String alertTitle = "성인인증 필요";
                        String alertMsg1 = getActivity().getString(R.string.error_not_adult1);
                        String alertMsg2 = getActivity().getString(R.string.error_not_adult2);
                        CMAlertUtil.Alert1(getActivity(), alertTitle, alertMsg1, alertMsg2, false, true, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getActivity(), CMSettingMainActivity.class);
                                getActivity().startActivity(intent);
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                    } else {
                        if (jsonObj.isNull("assetId") == false) {
                            assetId = jsonObj.getString("assetId");
                        } else if (jsonObj.isNull("primaryAssetId") == false) {
                            assetId = jsonObj.getString("primaryAssetId");
                        }

                        if (jsonObj.isNull("primaryAssetId") == false) {
                            primaryAssetId = jsonObj.getString("primaryAssetId");
                        }

                        if (jsonObj.isNull("episodePeerExistence") == false) {
                            episodePeerExistence = jsonObj.getString("episodePeerExistence");
                        }

                        if (jsonObj.isNull("contentGroupId") == false) {
                            contentGroupId = jsonObj.getString("contentGroupId");
                        }

                        if (TextUtils.isEmpty(assetId) == false) {
                            Intent intent = new Intent(getActivity(), VodDetailActivity.class);
                            intent.putExtra("assetId", assetId);

                            if (TextUtils.isEmpty(episodePeerExistence) == false && "1".equalsIgnoreCase(episodePeerExistence) == true) {
                                intent.putExtra("episodePeerExistence", episodePeerExistence);
                                intent.putExtra("contentGroupId", contentGroupId);
                                intent.putExtra("primaryAssetId", primaryAssetId);
                            }
                            startActivity(intent);
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
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
                    removeCurrentTabObjectWithIndex(position);
                }
                changeListData();
            }

            @Override
            public void onListScrolled() {
                super.onListScrolled();
                mListView.closeOpenedItems();
            }

        });

        changeTabWithIndex(TAB_MOBILE);
    }

    /**
     * 조회 개수 문구 설정
     * */
    private void setPurchaseListCountText(int count) {
        mPurchasecount.setText(count + "개의 VOD 구매목록이 있습니다.");
        if (count == 0) {
            this.mPurchaseEmptyMsg.setVisibility(View.VISIBLE);
        } else {
            this.mPurchaseEmptyMsg.setVisibility(View.GONE);
        }
    }

    /**
     * 구매목록 삭제 처리
     * */
    private void deletePurchaseItem(final int itemIndex) {

        boolean isExpired = false;
        String vodTitle = "";
        final String purchasedId;
        ListViewDataObject obj = getCurrentTabObjectWithIndex(itemIndex);
        try {
            JSONObject jsonObj = new JSONObject(obj.sJson);
            vodTitle = jsonObj.getString("productName");
            purchasedId = jsonObj.getString("purchasedId");
//            String licenseEnd = jsonObj.getString("licenseEnd");
//            if (TextUtils.isEmpty(CMDateUtil.getLicenseRemainDate(licenseEnd, new Date()))) {
//                isExpired = true;
//            }

            if (obj.remainTime < 0 && "0".equals(obj.viewablePeriodState)) {
                String alertTitle = getString(R.string.my_cnm_alert_title_expired);
                String alertMessage1 = getString(R.string.my_cnm_alert_message1_expired);
                String alertMessage2 = getString(R.string.my_cnm_alert_message2_expired);
                CMAlertUtil.Alert(getActivity(), alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        mListView.dismiss(itemIndex);
                        requestDisablePurchaseLog(purchasedId, itemIndex);
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
//                                mListView.dismiss(itemIndex);
                                requestDisablePurchaseLog(purchasedId, itemIndex);
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mListView.closeOpenedItems();
                            }
                        });
            }
        } catch (JSONException e) {
            e.printStackTrace();
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

        changeListData();
    }

    private ListViewDataObject getCurrentTabObjectWithIndex(int itemIndex) {
        ListViewDataObject returnObj = null;
        if (this.mTabIndex == TAB_MOBILE) {
            returnObj = this.mMoblieList.get(itemIndex);
        } else if (this.mTabIndex == TAB_TV) {
            returnObj = this.mTVList.get(itemIndex);
        }
        return returnObj;
    }

    private void removeCurrentTabObjectWithIndex(int itemIndex) {
        if (this.mTabIndex == TAB_MOBILE) {
            this.mMoblieList.remove(itemIndex);
        } else if (this.mTabIndex == TAB_TV) {
            this.mTVList.remove(itemIndex);
        }
    }

    /**
     * VOD 구매목록 조회
     * */
    private void requestGetPurchasedProductList() {
        ((MyMainActivity)getActivity()).showProgressDialog("", getString(R.string.wait_a_moment));
        String terminalKey = mPref.getWebhasTerminalKey();

        String logStartDate = null;
        String logEndDate = null;
        try {
            logStartDate = URLEncoder.encode(CMDateUtil.getBeforeTodayWithFormat(-60, "yyyy-MM-dd HH:mm:ss"), "utf-8");
            logEndDate = URLEncoder.encode(CMDateUtil.getDateWithFormat(new Date(), "yyyy-MM-dd HH:mm:ss"), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = mPref.getWebhasServerUrl() + "/getPurchasedProductList.json?version=1&terminalKey="+terminalKey+"&purchaseLogProfile=4";

        if (TextUtils.isEmpty(logStartDate) == false && TextUtils.isEmpty(logEndDate) == false) {
            url += "&expiredLogStartTime=" + logStartDate + "&expiredLogEndTime=" + logEndDate;
        }

        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // 무제한
                ArrayList<ListViewDataObject> moblieList_1 = new ArrayList<>();
                ArrayList<ListViewDataObject> tvList_1 = new ArrayList<>();
                // 기간 완료
                ArrayList<ListViewDataObject> moblieList_2 = new ArrayList<>();
                ArrayList<ListViewDataObject> tvList_2 = new ArrayList<>();
                // 기간 만료
                ArrayList<ListViewDataObject> moblieList_3 = new ArrayList<>();
                ArrayList<ListViewDataObject> tvList_3 = new ArrayList<>();

                try {
                    Date compareDate = new Date();
                    JSONObject responseObject = new JSONObject(response);
                    JSONArray puchaseArray = responseObject.getJSONArray("purchaseLogList");
                    for (int i = 0; i < puchaseArray.length(); i++) {
                        JSONObject jsonObj = puchaseArray.getJSONObject(i);
                        ListViewDataObject obj = new ListViewDataObject(i, 0, jsonObj.toString());

                        String productType = jsonObj.getString("productType").toLowerCase();
                        String paymentType = jsonObj.getString("paymentType").toLowerCase();
                        if (checkAddListWithPaymentType(paymentType) && checkAddListWithProductType(productType)) {
                            String purchaseDeviceType = jsonObj.getString("purchaseDeviceType");   // 1:TV, 2:MOBILE
                            String purchasedTime = jsonObj.getString("purchasedTime");
                            String viewablePeriod = jsonObj.getString("viewablePeriod");

                            obj.puchaseSecond = CMDateUtil.changeSecondToDate(purchasedTime);
                            obj.viewablePeriodState = jsonObj.getString("viewablePeriodState");
                            if ("1".equals(obj.viewablePeriodState) == false) {
                                obj.remainTime = CMDateUtil.getRemainWatchingTime(viewablePeriod, purchasedTime, compareDate);

                                if (obj.remainTime < 0) {
                                    if ( "1".equals(purchaseDeviceType) ) {
                                        tvList_3.add(obj);
                                    } else if ( "2".equals(purchaseDeviceType) ) {
                                        moblieList_3.add(obj);
                                    }
                                } else {
                                    if ( "1".equals(purchaseDeviceType) ) {
                                        tvList_2.add(obj);
                                    } else if ( "2".equals(purchaseDeviceType) ) {
                                        moblieList_2.add(obj);
                                    }
                                }
                            } else {
                                if ( "1".equals(purchaseDeviceType) ) {
                                    tvList_1.add(obj);
                                } else if ( "2".equals(purchaseDeviceType) ) {
                                    moblieList_1.add(obj);
                                }
                            }
                        }
                    }

                    ((MyMainActivity) getActivity()).hideProgressDialog();

                    sortPurchaseList_1(moblieList_1);
                    sortPurchaseList_2(moblieList_2);
                    sortPurchaseList_3(moblieList_3);

                    sortPurchaseList_1(tvList_1);
                    sortPurchaseList_2(tvList_2);
                    sortPurchaseList_3(tvList_3);

                    mMoblieList.addAll(moblieList_1);
                    mMoblieList.addAll(moblieList_2);
                    mMoblieList.addAll(moblieList_3);

                    mTVList.addAll(tvList_1);
                    mTVList.addAll(tvList_2);
                    mTVList.addAll(tvList_3);

                    changeListData();
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

    private void changeListData() {
        this.mAdapter.clear();
        if (mTabIndex == TAB_MOBILE) {
            this.mAdapter.addAll(this.mMoblieList);
            setPurchaseListCountText(this.mMoblieList.size());
        } else if (mTabIndex == TAB_TV) {
            this.mAdapter.addAll(this.mTVList);
            setPurchaseListCountText(this.mTVList.size());
        }
        this.mAdapter.notifyDataSetChanged();
    }

    private void sortPurchaseList_1(ArrayList<ListViewDataObject> list) {
        Collections.sort(list, new Comparator<ListViewDataObject>() {
            public int compare(ListViewDataObject left, ListViewDataObject right) {
                if ("1".equals(left.viewablePeriodState)) {
                    if ("1".equals(right.viewablePeriodState)) {
                        if (left.puchaseSecond > right.puchaseSecond) {
                            return -1;
                        } else {
                            if (left.puchaseSecond > right.puchaseSecond) {
                                return 1;
                            } else {
                                return 0;
                            }
                        }
                    } else {
                        return 1;
                    }
                } else {
                    if ("1".equals(right.viewablePeriodState)) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }
        });
    }

    private void sortPurchaseList_2(ArrayList<ListViewDataObject> list) {
        Collections.sort(list, new Comparator<ListViewDataObject>() {
            public int compare(ListViewDataObject left, ListViewDataObject right) {
                if (left.remainTime == right.remainTime) {
                    if (left.puchaseSecond > right.puchaseSecond) {
                        return -1;
                    } else {
                        if (left.puchaseSecond > right.puchaseSecond) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                } else {
                    if (left.remainTime > right.remainTime) {
                        return -1;
                    } else {
                        if (left.remainTime > right.remainTime) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                }
            }
        });
    }

    private void sortPurchaseList_3(ArrayList<ListViewDataObject> list) {
        Collections.sort(list, new Comparator<ListViewDataObject>() {
            public int compare(ListViewDataObject left, ListViewDataObject right) {
                if (left.puchaseSecond > right.puchaseSecond) {
                    return -1;
                } else {
                    if (left.puchaseSecond > right.puchaseSecond) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }
        });
    }
    /**
     * payment type에 따른 리스트 추가 여부 반환
     * */
    private boolean checkAddListWithPaymentType(String paymentType) {
        if ("normal".equals(paymentType) || "coupon".equals(paymentType) || "point".equals(paymentType) || "complex".equals(paymentType)) {
            return true;
        }
        return false;
    }

    /**
     * product type에 따른 리스트 추가 여부 반환
     * */
    private boolean checkAddListWithProductType(String productType) {
        if("rvod".equals(productType) || "package".equals(productType) || "bundle".equals(productType)) {
            return true;
        }
        return false;
    }

    /**
     * VOD 구매목록 조회
     * */
    private void requestDisablePurchaseLog(String purchaseID, final int position) {
        ((MyMainActivity)getActivity()).showProgressDialog("", getString(R.string.wait_a_moment));
        String terminalKey = mPref.getWebhasTerminalKey();

        String url = mPref.getWebhasServerUrl() + "/disablePurchaseLog.json?version=1&terminalKey="+terminalKey+"&purchaseEventId="+purchaseID;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ((MyMainActivity) getActivity()).hideProgressDialog();

                try {
                    JSONObject responseObj = new JSONObject(response);
                    String resultCode = responseObj.getString("resultCode");
                    if ( Constants.CODE_WEBHAS_OK.equals(resultCode) ) {
                        mListView.dismiss(position);
                    } else {
                        String errorString = responseObj.getString("errorString");
                        StringBuilder sb = new StringBuilder();
                        sb.append("API: action\nresultCode: ").append(resultCode).append("\nerrorString: ").append(errorString);

                        CMAlertUtil.Alert(getActivity(), "알림", sb.toString());

                        mAdapter.notifyDataSetChanged();
                    }

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
