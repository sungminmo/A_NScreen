package com.stvn.nscreen.search;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.Constants;
import com.jjiya.android.common.JYSharedPreferences;
import com.stvn.nscreen.R;
import com.stvn.nscreen.common.SearchVodDataObject;
import com.stvn.nscreen.common.VolleyHelper;
import com.stvn.nscreen.setting.CMSettingData;
import com.stvn.nscreen.setting.CMSettingMainActivity;
import com.stvn.nscreen.util.CMAlertUtil;
import com.stvn.nscreen.util.CMUtil;
import com.stvn.nscreen.vod.VodDetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by leejunghoon on 15. 9. 19..
 */

public class SearchVodFragment extends SearchBaseFragment implements AdapterView.OnItemClickListener{

    private LayoutInflater mInflater;
    private TextView mEmptyMessage;
    private GridView mGridView;
    private ArrayList<SearchVodDataObject> mProgramlist = new ArrayList<SearchVodDataObject>();
    private SearchVodAdapter mAdapter;

    private String mTerminalKey = "8A5D2E45D3874824FF23EC97F78D358";
    private String mKeyword;
    private RequestQueue mRequestQueue;

    private JYSharedPreferences mPref;
    private boolean mLockListView = true;
    private int mTotCnt;
    private VolleyHelper mVolleyHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        return mInflater.inflate(R.layout.fragment_search_vod,null);
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRequestQueue = Volley.newRequestQueue(getActivity());
        mPref = new JYSharedPreferences(getActivity());
        mKeyword = getArguments().getString("KEYWORD");
        mVolleyHelper = VolleyHelper.getInstance(getActivity());
        initView();
        reqVodList();
    }

    private void initView()
    {
        mEmptyMessage = (TextView)getView().findViewById(R.id.search_empty_msg);
        mGridView = (GridView)getView().findViewById(R.id.programgridview);
        mAdapter = new SearchVodAdapter(getActivity(),mProgramlist);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
        mAdapter.notifyDataSetChanged();
    }

    public void reqVodList()
    {
        mLockListView = true;
        ((SearchMainActivity)getActivity()).showProgressDialog("", getString(R.string.wait_a_moment));

		String includeAdultCategory = CMSettingData.getInstance().getAdultSearchRestriction(getActivity())?"0":"1";
        // swlim. "무한도전 Classic"은 검색이 안되서 아래의 URLEncoder 적용.
        String searchWord = mKeyword;
        try {
            searchWord = URLEncoder.encode(searchWord, "UTF-8");
        } catch ( UnsupportedEncodingException e ) {
            e.printStackTrace();
        }
        // String url = Constants.SERVER_URL_CASTIS_PUBLIC+"/searchContentGroup.json?version=1&terminalKey="+JYSharedPreferences.WEBHAS_PUBLIC_TERMINAL_KEY+"&includeAdultCategory="+includeAdultCategory+"&searchKeyword="+mKeyword+"&contentGroupProfile=2";
        String url = Constants.SERVER_URL_CASTIS_PUBLIC+"/searchContentGroup.json?version=1&terminalKey="+mPref.getWebhasTerminalKey()+"&includeAdultCategory="+includeAdultCategory+"&searchKeyword="+searchWord+"&contentGroupProfile=2&noCache=";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ((SearchMainActivity)getActivity()).hideProgressDialog();
                        try {
                            JSONArray object = response.getJSONArray("searchResultList");
                            if(object.length()>0)
                            {
                                JSONObject groupobject = object.getJSONObject(0);
                                JSONArray array = groupobject.getJSONArray("contentGroupList");
                                String msg = "";
                                for(int i=0;i<array.length();i++)
                                {
                                    JSONObject listitem = array.getJSONObject(i);
                                    SearchVodDataObject data = new SearchVodDataObject();
                                    CMUtil.autoMappingJsonToObject(listitem, data);
                                    JSONObject obj = listitem.getJSONObject("cgEventEx");
                                    data.eventTargetId = obj.getString("eventTargetId");
                                    data.eventTargetType = obj.getString("eventTargetType");
                                    msg = msg+data.toString();
                                    mProgramlist.add(data);
                                }

//                                CMAlertUtil.Alert(getActivity(), "리스트", "size : " + msg);
                                mTotCnt = mProgramlist.size();
                                mAdapter.notifyDataSetChanged();
                                ((SearchMainActivity)getActivity()).setSearchCountText(mTotCnt);
                            }

                        } catch (JSONException e) {
                            CMAlertUtil.Alert(getActivity(), "Json", ""+e.getMessage());
                        }
                        if (mProgramlist.size() == 0) {
                            mEmptyMessage.setVisibility(View.VISIBLE);
                        } else {
                            mEmptyMessage.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ((SearchMainActivity)getActivity()).hideProgressDialog();
            }
        });

        mRequestQueue.add(jsonObjectRequest);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String rating = mProgramlist.get(position).rating;

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
            String assetId = mProgramlist.get(position).primaryAssetId;
            String episodePeerExistence = mProgramlist.get(position).episodePeerExistence;
            String contentGroupId = mProgramlist.get(position).contentGroupId;

            if(TextUtils.isEmpty(assetId) == false) {
                Intent intent = new Intent(getActivity(), VodDetailActivity.class);
                intent.putExtra("assetId", assetId);
                // episodePeerExistence 값이 있으면서 1인 경우에는 아래 3가지 데이터를 추가로 VOD 상세 화면에 전달 한다.
                if (TextUtils.isEmpty(episodePeerExistence) == false && "1".equalsIgnoreCase(episodePeerExistence) == true) {
                    intent.putExtra("episodePeerExistence", episodePeerExistence);
                    intent.putExtra("contentGroupId", contentGroupId);
                    intent.putExtra("primaryAssetId", assetId);
                }
                startActivity(intent);
            }
        }

    }

}
