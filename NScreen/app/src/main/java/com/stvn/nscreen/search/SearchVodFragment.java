package com.stvn.nscreen.search;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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
import com.stvn.nscreen.util.CMAlertUtil;
import com.stvn.nscreen.util.CMUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by leejunghoon on 15. 9. 19..
 */

public class SearchVodFragment extends SearchBaseFragment implements AdapterView.OnItemClickListener{

    private LayoutInflater mInflater;
    private GridView mGridView;
    private ArrayList<SearchVodDataObject> mProgramlist = new ArrayList<SearchVodDataObject>();
    private SearchVodAdapter mAdapter;

    private String mTerminalKey = "8A5D2E45D3874824FF23EC97F78D358";
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
        mGridView.setOnItemClickListener(this);
        mAdapter.notifyDataSetChanged();
    }

    public void reqVodList()
    {
        mLockListView = true;
        mProgressDialog	 = ProgressDialog.show(getActivity(), "", getString(R.string.wait_a_moment));
        String url = Constants.SERVER_URL_CASTIS_PUBLIC+"/searchContentGroup.json?version=1&terminalKey="+JYSharedPreferences.WEBHAS_PUBLIC_TERMINAL_KEY+"&includeAdultCategory=0&searchKeyword="+mKeyword+"&contentGroupProfile=2";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        mProgressDialog.dismiss();
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

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
            }
        });

        mRequestQueue.add(jsonObjectRequest);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

}
