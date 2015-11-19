package com.stvn.nscreen.my;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.stvn.nscreen.R;
import com.stvn.nscreen.common.CMActionBar;
import com.stvn.nscreen.common.CMBaseActivity;

import java.util.ArrayList;

/**
 * Created by limdavid on 15. 9. 15..
 */

public class MyMainActivity extends CMBaseActivity implements View.OnClickListener {

    private Fragment mFragment;
    private int mTabSelectIdx = -1;
    private ArrayList<View> mTabList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_main);
        setActionBarStyle(CMActionBar.CMActionBarStyle.BACK);
        setActionBarTitle("마이 C&M");

        initView();
    }

    private void initView()
    {
        mTabList.add((findViewById(R.id.tab1)));
        mTabList.add((findViewById(R.id.tab2)));
        mTabList.add((findViewById(R.id.tab3)));
        for(View v:mTabList)
            v.setOnClickListener(this);

        showFragment(0);
    }

    public void showFragment(int idx)
    {
        if(idx == mTabSelectIdx)
            return;

        mTabSelectIdx = idx;
        setTabSelect(mTabSelectIdx);

        switch (mTabSelectIdx)
        {
            case 0:
                mFragment = new MyPurchaseListFragment();
                break;
            case 1:
                mFragment = new MyWatchListFragment();
                break;
            case 2:
                mFragment = new MyDibListFragment();
                break;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mycnmFragment, mFragment);
        ft.commit();
    }

    private void setTabSelect(int idx)
    {
        for(int i=0;i<mTabList.size();i++)
        {
            if(i==idx)
                mTabList.get(i).setSelected(true);
            else
                mTabList.get(i).setSelected(false);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.tab1:
                showFragment(0);
                break;
            case R.id.tab2:
                showFragment(1);
                break;
            case R.id.tab3:
                showFragment(2);
                break;
        }
    }

    @Override
    public void onBackEventPressed() {
        finish();
    }
}
