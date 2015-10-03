package com.stvn.nscreen.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.stvn.nscreen.R;

import java.util.ArrayList;

/**
 * Created by leejunghoon on 15. 9. 19..
 */
public class SearchVodFragment extends SearchBaseFragment{

    private LayoutInflater mInflater;
    private GridView mGridView;
    private ArrayList<Integer> mProgramlist = new ArrayList<Integer>();
    private SearchVodAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        return mInflater.inflate(R.layout.fragment_search_vod,null);
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView()
    {
        mGridView = (GridView)getView().findViewById(R.id.programgridview);
        mAdapter = new SearchVodAdapter(getActivity(),mProgramlist);
        mGridView.setAdapter(mAdapter);
        for(int i=0;i<100;i++)
        {
            mProgramlist.add(i);
        }
        mAdapter.notifyDataSetChanged();
    }
}
