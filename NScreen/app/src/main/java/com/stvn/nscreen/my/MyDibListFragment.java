package com.stvn.nscreen.my;

import android.content.DialogInterface;
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

import com.stvn.nscreen.R;
import com.stvn.nscreen.common.BaseSwipeListViewListener;
import com.stvn.nscreen.common.SwipeListView;
import com.stvn.nscreen.util.CMAlertUtil;

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
                CMAlertUtil.ToastShort(getActivity(), position + "번째 리스트 클릭");
            }

            @Override
            public void onClickBackView(int position) {
                Log.d("ljh", "onClickFrontView");
            }

            @Override
            public int onChangeSwipeMode(int position) {
                int swipeMode = 0;
                switch (position%2)
                {
                    case 0:// 기본설정된 Swipe모드
                        swipeMode = SwipeListView.SWIPE_MODE_DEFAULT;
                        break;
                    case 1:// Swipe None
                        swipeMode = SwipeListView.SWIPE_MODE_NONE;
                        break;
                }
                return swipeMode;
            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {
                for (int position : reverseSortedPositions) {
                    mList.remove(position);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onListScrolled() {
                super.onListScrolled();
                mListView.closeOpenedItems();
            }
        });
    }

    private void initData()
    {
        int count = 20;
        for(int i=0;i<count;i++)
        {
            mList.add(""+i);
        }
        mAdapter.notifyDataSetChanged();
        mLockListView = false;

        setDibListCountText(count);
    }

    /**
     * 조회 개수 문구 설정
     * */
    private void setDibListCountText(int count) {
        mPurchasecount.setText(count + "개의 VOD 찜목록이 있습니다.");
    }

    /**
     * 찜목록 삭제 처리
     * TODO:해당 찜목록정보에서 유효기간 확인 후 해당 내용에 대한 처리를 한다.
     * */
    private void deleteDibItem(final int itemIndex) {
//        mList.get(itemIndex);
        // TODO:유효기간 만료 일 때
        if (itemIndex %2 == 0) {
            String alertTitle = getString(R.string.my_cnm_alert_title_expired);
            String alertMessage = getString(R.string.my_cnm_alert_message_expired);
            CMAlertUtil.Alert(getActivity(), alertTitle, alertMessage, "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    removeWatchList(itemIndex);
                }
            }, true);
        }
        // TODO:유효기간 만료가 아닐 때
        else {
            String programTitle = "프로그램타이틀";

            String alertTitle = "VOD 찜 목록 삭제";
            String alertMessage1 = "선택하신 VOD를 목록에서 삭제하시겠습니까?\n" + programTitle;

            CMAlertUtil.Alert(getActivity(), alertTitle, alertMessage1, "", "예", "아니오", true, false,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            removeWatchList(itemIndex);
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
     * 조회 목록 리스트 제거 및 화면 갱신
     * */
    private void removeWatchList(int itemIndex) {
        mListView.dismiss(itemIndex);

        int count = mList.size();
        setDibListCountText(count);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
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
}
