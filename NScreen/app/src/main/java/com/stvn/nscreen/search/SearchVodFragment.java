package com.stvn.nscreen.search;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.Constants;
import com.jjiya.android.common.JYSharedPreferences;
import com.stvn.nscreen.R;
import com.stvn.nscreen.common.GsonRequest;
import com.stvn.nscreen.common.SearchVodDataObject;
import com.stvn.nscreen.common.VolleyHelper;

import java.util.ArrayList;

/**
 * Created by leejunghoon on 15. 9. 19..
 */

public class SearchVodFragment extends SearchBaseFragment{

    private LayoutInflater mInflater;
    private GridView mGridView;
    private ArrayList<SearchVodDataObject.ContentGroup> mProgramlist = new ArrayList<SearchVodDataObject.ContentGroup>();
    private SearchVodAdapter mAdapter;

    private String mTerminalKey = "9CED3A20FB6A4D7FF35D1AC965F988D2";
    private String mKeyword;
    private RequestQueue mRequestQueue;
    private ProgressDialog mProgressDialog;

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
        mGridView = (GridView)getView().findViewById(R.id.programgridview);
        mAdapter = new SearchVodAdapter(getActivity(),mProgramlist);
        mGridView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    public void reqVodList()
    {
        mLockListView = true;
        mProgressDialog	 = ProgressDialog.show(getActivity(), "", getString(R.string.wait_a_moment));
        String url = Constants.SERVER_URL_CASTIS_PUBLIC+"/searchContentGroup.json?version=1&terminalKey=8A5D2E45D3874824FF23EC97F78D358&includeAdultCategory=0&searchKeyword="+mKeyword;
        final GsonRequest gsonRequest = new GsonRequest(url, SearchVodDataObject.class,null,new Response.Listener<SearchVodDataObject>(){
            @Override
            public void onResponse(SearchVodDataObject response) {

                mLockListView = false;
                mProgramlist.addAll(response.getSearchResultList().getSearchResult().getContentGroupList().getContentGroup());
                mAdapter.notifyDataSetChanged();
                ((SearchMainActivity)getActivity()).setSearchCountText(mTotCnt);
                mProgressDialog.dismiss();
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
            }
        });

        mVolleyHelper.addToRequestQueue(gsonRequest);

    }



}
