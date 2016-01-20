package com.stvn.nscreen.my;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.common.ListViewDataObject;
import com.stvn.nscreen.R;
import com.stvn.nscreen.common.BaseSwipeListViewListener;
import com.stvn.nscreen.common.SwipeListView;
import com.stvn.nscreen.util.CMAlertUtil;
import com.stvn.nscreen.vod.VodDetailActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by leejunghoon on 15. 10. 31..
 */

public class MyWatchListFragment extends Fragment implements AbsListView.OnScrollListener {
    LayoutInflater                mInflater;
    private TextView              mPurchasecount;
    private TextView              mPurchaseEmptyMsg;
    private SwipeMenuListView     mListView;
    private MyWatchListAdapter    mAdapter;
    private ArrayList<JSONObject> mList;
    private boolean               mLockListView = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        return mInflater.inflate(R.layout.fragment_mycnm_watchlist,null);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        JYSharedPreferences preferences = new JYSharedPreferences(getActivity());
        mList = preferences.getAllWatchVodObject();
        initView();
        initData(mList.size());
    }

    private void initView()
    {
        mPurchasecount = (TextView)getView().findViewById(R.id.purchasecount);
        mPurchaseEmptyMsg = (TextView)getView().findViewById(R.id.purchase_empty_msg);
        mPurchaseEmptyMsg.setVisibility(View.GONE);

        mListView = (SwipeMenuListView)getView().findViewById(R.id.purchaselistview);
        mAdapter = new MyWatchListAdapter(getActivity(), mList);
        mListView.setAdapter(mAdapter);
        mListView.setMenuCreator(creator);
        mListView.setOnScrollListener(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    JSONObject jo = mList.get(position);
                    String assetId = jo.getString("sAssetId");
                    Intent intent = new Intent(getActivity(), VodDetailActivity.class);
                    intent.putExtra("assetId", assetId);
                    getActivity().startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
         });
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                deleteWatchItem(position);
                return false;
            }
        });
    }

    private void initData(int iCountOfArray)
    {
        mAdapter.notifyDataSetChanged();
        mLockListView = false;
        setWatchListCountText(iCountOfArray);
    }

    /**
     * Swipe Menu for ListView
     */
    SwipeMenuCreator creator = new SwipeMenuCreator() {
        @Override
        public void create(SwipeMenu menu) {
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, getResources().getDisplayMetrics());
            if (menu.getViewType() == 0) {
                SwipeMenuItem item1 = new SwipeMenuItem(getActivity());
                item1.setBackground(R.color.red);
                item1.setWidth(width);
                item1.setTitle("삭제");
                item1.setTitleSize(12);
                item1.setTitleColor(getResources().getColor(R.color.white));
                menu.addMenuItem(item1);
            }
        }
    };

    /**
     * 조회 개수 문구 설정
     * */
    private void setWatchListCountText(int count) {
        mPurchasecount.setText(count + "개의 VOD 시청목록이 있습니다.");
        if (count == 0) {
            this.mPurchaseEmptyMsg.setVisibility(View.VISIBLE);
        } else {
            this.mPurchaseEmptyMsg.setVisibility(View.GONE);
        }
    }

    /**
     * 시청목록 삭제 처리
     * TODO:해당 시청목록정보에서 유효기간 확인 후 해당 내용에 대한 처리를 한다.
     * */
    private void deleteWatchItem(final int itemIndex) {
//        mList.get(itemIndex);
        // TODO:유효기간 만료 일 때
//        if (itemIndex %2 == 0) {
//            String alertTitle = getString(R.string.my_cnm_alert_title_expired);
//            String alertMessage1 = getString(R.string.my_cnm_alert_message1_expired);
//            String alertMessage2 = getString(R.string.my_cnm_alert_message2_expired);
//            CMAlertUtil.Alert(getActivity(), alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    mListView.dismiss(itemIndex);
//                }
//            }, true);
//        }
//        else {
        JSONObject jo = mList.get(itemIndex);
        try {
            final int iSeq = jo.getInt("iSeq");
            String programTitle = jo.getString("sTitle");
            String alertTitle = "VOD 시청목록 삭제";
            String alertMessage = "선택하신 VOD를\n시청목록에서 삭제하시겠습니까?";

            CMAlertUtil.Alert(getActivity(), alertTitle, alertMessage,  programTitle, "예", "아니오", true, false,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            JYSharedPreferences pref = new JYSharedPreferences(getActivity());
                            pref.removeWatchVod(iSeq);

                            mList.remove(itemIndex);
                            mAdapter.notifyDataSetChanged();

                            int count = mList.size();
                            setWatchListCountText(count);
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAdapter.notifyDataSetChanged();
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
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
