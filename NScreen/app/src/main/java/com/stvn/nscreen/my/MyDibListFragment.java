package com.stvn.nscreen.my;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.stvn.nscreen.R;
import com.stvn.nscreen.common.BaseSwipeListViewListener;
import com.stvn.nscreen.common.SwipeListView;

import java.util.ArrayList;

/**
 * Created by leejunghoon on 15. 10. 31..
 */
public class MyDibListFragment extends Fragment implements View.OnClickListener,AbsListView.OnScrollListener{
    LayoutInflater mInflater;
    private TextView mPurchasecount;
    private SwipeListView mListView;
    private MyDibListAdapter mAdapter;
    private ArrayList<String> mList = new ArrayList<>();
    private boolean mLockListView = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        return mInflater.inflate(R.layout.fragment_mycnm_diblist,null);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
        initData();
    }

    private void initView()
    {
        mPurchasecount = (TextView)getView().findViewById(R.id.purchasecount);
        mPurchasecount.setText("19개의 VOD구매목록이 있습니다.");

        mListView = (SwipeListView)getView().findViewById(R.id.purchaselistview);
        mAdapter = new MyDibListAdapter(getActivity(),mList);
        mAdapter.setmClicklitener(this);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
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
                Log.d("ljh", "onStartOpen");
            }

            @Override
            public void onStartClose(int position, boolean right) {
                Log.d("ljh", "onStartClose");
            }

            @Override
            public void onClickFrontView(int position) {
                Log.d("ljh", "onClickFrontView");
            }

            @Override
            public void onClickBackView(int position) {
                Log.d("ljh", "onClickFrontView");
            }

            @Override
            public int onChangeSwipeMode(int position) {


                return super.onChangeSwipeMode(position);
            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {
//                for (int position : reverseSortedPositions) {
//                    data.remove(position);
//                }
                mAdapter.notifyDataSetChanged();
            }

        });
    }

    private void initData()
    {
        for(int i=0;i<20;i++)
        {
            mList.add(""+i);
        }
        mAdapter.notifyDataSetChanged();
        mLockListView = false;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn1:
                Toast.makeText(getActivity(), "삭제버튼" + v.getTag().toString(), Toast.LENGTH_SHORT).show();
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