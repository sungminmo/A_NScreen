package com.stvn.nscreen.my;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by leejunghoon on 15. 10. 31..
 */

public class MyDibListFragment extends Fragment implements View.OnClickListener,AbsListView.OnScrollListener{
    LayoutInflater mInflater;
    private TextView mPurchasecount;
    private TextView mPurchaseEmptyMsg;
    private SwipeListView mListView;
    private MyDibListAdapter mAdapter;
    private ArrayList<ListViewDataObject> mList = new ArrayList<>();
    private boolean mLockListView = true;

    private RequestQueue mRequestQueue;
    private JYSharedPreferences mPref;

    private final int REQUEST_CODE_VOD_DETAIL = 1100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        this.mPref = new JYSharedPreferences(getActivity());
        this.mRequestQueue = Volley.newRequestQueue(getActivity());
        return mInflater.inflate(R.layout.fragment_mycnm_diblist,null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
        requestGetWishList();
    }

    private void initView() {
        mPurchasecount = (TextView)getView().findViewById(R.id.purchasecount);
        mPurchaseEmptyMsg = (TextView)getView().findViewById(R.id.purchase_empty_msg);
        mPurchaseEmptyMsg.setVisibility(View.GONE);
        mListView = (SwipeListView)getView().findViewById(R.id.purchaselistview);
        mAdapter = new MyDibListAdapter(getActivity(),mList);
        mAdapter.setmClicklitener(this);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
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
                String primaryAssetId = "";
                String episodePeerExistence = "";
                String contentGroupId = "";
                try {
                    JSONObject jsonObj = new JSONObject(obj.sJson);
                    JSONObject assetObj = jsonObj.getJSONObject("asset");

                    String rating = assetObj.getString("rating");
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
                        if (assetObj.isNull("assetId") == false) {
                            assetId = assetObj.getString("assetId");
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
                            startActivityForResult(intent, REQUEST_CODE_VOD_DETAIL);
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
            @Override
            public void onDismiss(int[] reverseSortedPositions) {
                for (int position : reverseSortedPositions) {
                    mList.remove(position);
                }
                mAdapter.notifyDataSetChanged();

                int count = mList.size();
                setDibListCountText(count);
            }

            @Override
            public void onListScrolled() {
                super.onListScrolled();
                mListView.closeOpenedItems();
            }
        });
        setDibListCountText(this.mList.size());
    }

    /**
     * 조회 개수 문구 설정
     * */
    private void setDibListCountText(int count) {
        this.mPurchasecount.setText(count + "개의 VOD 찜목록이 있습니다.");
        if (count == 0) {
            this.mPurchaseEmptyMsg.setVisibility(View.VISIBLE);
        } else {
            this.mPurchaseEmptyMsg.setVisibility(View.GONE);
        }
    }

    /**
     * 찜목록 삭제 처리
     * */
    private void deleteDibItem(final int itemIndex) {
        ListViewDataObject obj = mList.get(itemIndex);
        boolean isExpired = false;
        String vodTitle = "";
        final String assetId;
        try {
            JSONObject jsonObj = new JSONObject(obj.sJson);
            JSONObject assetObj = jsonObj.getJSONObject("asset");
            String licenseEnd = assetObj.getString("licenseEnd");
            vodTitle = assetObj.getString("title");
            if (TextUtils.isEmpty(CMDateUtil.getLicenseRemainDate(licenseEnd, new Date()))) {
                isExpired = true;
            }
            assetId = assetObj.getString("assetId");

            if (isExpired == true) {
                String alertTitle = getString(R.string.my_cnm_alert_title_expired);
                String alertMessage1 = getString(R.string.my_cnm_alert_message1_expired);
                String alertMessage2 = getString(R.string.my_cnm_alert_message2_expired);
                CMAlertUtil.Alert(getActivity(), alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestRemoveWishItem(itemIndex, assetId);

                    }
                }, true);
            }
            else {
                String alertTitle = "VOD 찜 목록 삭제";
                String alertMessage = "선택하신 VOD를\n목록에서 삭제하시겠습니까?";

                CMAlertUtil.Alert(getActivity(), alertTitle, alertMessage, vodTitle, "예", "아니오", true, false,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestRemoveWishItem(itemIndex, assetId);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                deleteDibItem((int) v.getTag());
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

    private void requestGetWishList() {
        ((MyMainActivity)getActivity()).showProgressDialog("", getString(R.string.wait_a_moment));

        String terminalKey = mPref.getWebhasTerminalKey();
        String uuid = mPref.getValue(JYSharedPreferences.UUID, "");
        String url = mPref.getWebhasServerUrl() + "/getWishList.json?version=1&terminalKey="+terminalKey+"&userId="+uuid+"&assetProfile=1";
        this.mLockListView = true;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ((MyMainActivity) getActivity()).hideProgressDialog();

                try {
                    JSONObject responseObj = new JSONObject(response);
                    String resultCode  = responseObj.getString("resultCode");

                    if ( Constants.CODE_WEBHAS_OK.equals(resultCode) ) {
                        JSONArray wishItemArray  = responseObj.getJSONArray("wishItemList");

                        for (int i = 0; i < wishItemArray.length(); i++) {
                            JSONObject jsonObj = wishItemArray.getJSONObject(i);
                            ListViewDataObject obj = new ListViewDataObject(i, 0, jsonObj.toString());
                            mList.add(obj);
                        }
                    } else {
                        String errorString = responseObj.getString("errorString");
                        StringBuilder sb = new StringBuilder();
                        sb.append("API: action\nresultCode: ").append(resultCode).append("\nerrorString: ").append(errorString);

                        CMAlertUtil.Alert(getActivity(), "알림", sb.toString());
                    }

                    setDibListCountText(mList.size());
                    mAdapter.notifyDataSetChanged();
                    mLockListView = false;
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

    private void requestRemoveWishItem(final int position, final String assetId) {
        ((MyMainActivity)getActivity()).showProgressDialog("", getString(R.string.wait_a_moment));

        String terminalKey = mPref.getWebhasTerminalKey();
        String uuid = mPref.getValue(JYSharedPreferences.UUID, "");
        String url = mPref.getWebhasServerUrl() + "/removeWishItem.json?version=1&terminalKey="+terminalKey+"&userId="+uuid+"&assetId=" + assetId;

        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ((MyMainActivity) getActivity()).hideProgressDialog();

                try {
                    JSONObject responseObj = new JSONObject(response);
                    String resultCode = responseObj.getString("resultCode");

                    if ( Constants.CODE_WEBHAS_OK.equals(resultCode) ) {
                        mListView.dismiss(position);
                        mPref.removeWWishAsset(assetId);
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
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("version", String.valueOf(1));
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_VOD_DETAIL) {
            this.mList.clear();
            setDibListCountText(this.mList.size());
            this.mAdapter.notifyDataSetChanged();
            requestGetWishList();
        }
    }
}
