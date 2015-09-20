package com.stvn.nscreen.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.stvn.nscreen.R;

import java.util.ArrayList;

/**
 * Created by leejunghoon on 15. 9. 19..
 */
public class SearchProgramFragment extends SearchBaseFragment{

    private LayoutInflater mInflater;
    private ListView mListView;
    private ArrayList<Integer> mProgramlist = new ArrayList<Integer>();
    private SearchProgramAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        return mInflater.inflate(R.layout.fragment_search_program,null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView()
    {
        mListView = (ListView)getView().findViewById(R.id.programlistview);
        mAdapter = new SearchProgramAdapter(getActivity(),mProgramlist);
        mListView.setAdapter(mAdapter);
        Log.d("ljh ","찍히냐");
        for(int i=0;i<100;i++)
        {
            mProgramlist.add(i);
        }
        mAdapter.notifyDataSetChanged();
    }
}
