package com.stvn.nscreen.search;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.Constants;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.http.JYStringRequest;
import com.stvn.nscreen.R;
import com.stvn.nscreen.common.BaseSwipeListViewListener;
import com.stvn.nscreen.common.SearchProgramDataObject;
import com.stvn.nscreen.common.SwipeListView;
import com.stvn.nscreen.util.CMLog;
import com.stvn.nscreen.util.CMUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by leejunghoon on 15. 9. 19..
 */

public class SearchProgramFragment extends SearchBaseFragment implements AbsListView.OnScrollListener{

    private LayoutInflater mInflater;
    private SwipeListView mListView;
    private ArrayList<SearchProgramDataObject> mProgramlist = new ArrayList<SearchProgramDataObject>();
    private SearchProgramAdapter mAdapter;
    private RequestQueue mRequestQueue;
    private ProgressDialog mProgressDialog;
    private String mAreaCode = "0";
    private String mKeyword;
    private int pageNo = 0;
    private int limitCnt = 10;
    private int mTotCnt = 0;

    private JYSharedPreferences mPref;
    private boolean mLockListView = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        return mInflater.inflate(R.layout.fragment_search_program,null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRequestQueue = Volley.newRequestQueue(getActivity());
        mPref = new JYSharedPreferences(getActivity());
        mKeyword = getArguments().getString("KEYWORD");
        initView();
        reqProgramList();
    }

    private void initView()
    {
        mListView = (SwipeListView)getView().findViewById(R.id.programlistview);
        mAdapter = new SearchProgramAdapter(getActivity(),mProgramlist);
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
                Log.d("ljh", "onClickFrontView");
            }

            @Override
            public void onClickBackView(int position) {
                Log.d("ljh", "onClickFrontView");
            }

            @Override
            public int onChangeSwipeMode(int position) {
//                int swipeMode = 0;
//                switch (position%2)
//                {
//                    case 0:// 기본설정된 Swipe모드
//                        swipeMode = SwipeListView.SWIPE_MODE_DEFAULT;
//                        break;
//                    case 1:// Swipe None
//                        swipeMode = SwipeListView.SWIPE_MODE_NONE;
//                        break;
//                }
                return super.onChangeSwipeMode(position);
            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {
//                for (int position : reverseSortedPositions) {
//                    data.remove(position);
//                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onListScrolled() {
                super.onListScrolled();
                mListView.closeOpenedItems();
            }

        });
    }

    public void reqProgramList()
    {
        mLockListView = true;
        mProgressDialog	 = ProgressDialog.show(getActivity(),"",getString(R.string.wait_a_moment));
        String url = Constants.SERVER_URL_AIRCODE_REAL+"/searchSchedule.xml?version=1&areaCode="+mAreaCode+"&searchString="+mKeyword+"&offset="+pageNo+"&limit="+limitCnt;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(tag, response);
                parseGetSearchList(response);
                mAdapter.notifyDataSetChanged();
                mProgressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                CMLog.e("CM", error.getMessage());
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("version", String.valueOf(1));
                params.put("areaCode", String.valueOf(0));
                CMLog.e("CM", params.toString());
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    private void parseGetSearchList(String response) {
        mTotCnt = 0;
        StringBuilder sb = new StringBuilder();
        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new ByteArrayInputStream(response.getBytes("utf-8")), "utf-8");

            int eventType = xpp.getEventType();
            SearchProgramDataObject object = null;
            while(eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;

                switch (eventType)
                {
                    case XmlPullParser.START_TAG:
                        name = xpp.getName();
                        if("totalCount".equals(name)){
                            mTotCnt = CMUtil.parseInt(xpp.nextText());
                        }else if("scheduleItem".equals(name)){
                            object = new SearchProgramDataObject();
                        }else if("channelId".equals(name)){
                            object.setChannelId(xpp.nextText());
                        }else if("channelNumber".equals(name)){
                            object.setChannelNumber(xpp.nextText());
                        }else if("channelName".equals(name)){
                            object.setChannelName(xpp.nextText());
                        }else if("channelInfo".equals(name)){
                            object.setChannelInfo(xpp.nextText());
                        }else if("channelLogoImg".equals(name)){
                            object.setChannelLogoImg(xpp.nextText());
                        }else if("channelProgramID".equals(name)){
                            object.setChannelProgramID(xpp.nextText());
                        }else if("channelProgramTime".equals(name)){
                            object.setChannelProgramTime(xpp.nextText());
                        }else if("channelProgramTitle".equals(name)){
                            object.setChannelProgramTitle(xpp.nextText());
                        }else if("channelProgramSeq".equals(name)){
                            object.setChannelProgramSeq(xpp.nextText());
                        }else if("channelProgramGrade".equals(name)){
                            object.setChannelProgramGrade(xpp.nextText());
                        }else if("channelProgramHD".equals(name)){
                            object.setChannelProgramHD(xpp.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = xpp.getName();
                        if("scheduleItem".equals(name))
                        {
                            if(object!=null)
                            {
                                mProgramlist.add(object);
                            }
                        }
                        break;

                }
                eventType = xpp.next();
            }
            mLockListView = false;
            mAdapter.notifyDataSetChanged();
            ((SearchMainActivity)getActivity()).setSearchCountText(mTotCnt);

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    View.OnClickListener mSwipeButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.watch_tv:         // TV로 시청
                    break;
                case R.id.rec_start:        // 녹화시작
                    break;
                case R.id.rec_stop:         // 녹화중지
                    break;
                case R.id.set_reservation_rec: //예약녹화설정
                    break;
                case R.id.cancel_reservation_rec: // 예약녹화취소
                    break;
                case R.id.set_reservation_watch: //예약시청설정
                    break;
                case R.id.cancel_reservation_watch: //예약시청취소
                    break;
            }
        }
    };


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int count = totalItemCount - visibleItemCount;

        if(firstVisibleItem >= count && totalItemCount != 0
                && mLockListView == false)
        {
            if(mTotCnt>pageNo*limitCnt)
            {
                pageNo++;
                reqProgramList();
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }
}
