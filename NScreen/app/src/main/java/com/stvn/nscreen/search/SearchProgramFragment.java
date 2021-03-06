package com.stvn.nscreen.search;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.jjiya.android.common.Constants;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.common.UiUtil;
import com.jjiya.android.http.JYStringRequest;
import com.stvn.nscreen.R;
import com.stvn.nscreen.common.SearchProgramDataObject;
import com.stvn.nscreen.util.CMAlertUtil;
import com.stvn.nscreen.util.CMLog;
import com.stvn.nscreen.util.CMUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by leejunghoon on 15. 9. 19..
 */

public class SearchProgramFragment extends SearchBaseFragment implements AbsListView.OnScrollListener{

    private LayoutInflater mInflater;
    private TextView mEmptyMessage;
    private SwipeMenuListView mListView;
    private ArrayList<SearchProgramDataObject> mProgramlist = new ArrayList<SearchProgramDataObject>();
    private SearchProgramAdapter mAdapter;
    private RequestQueue mRequestQueue;
    private String mAreaCode = "0";
    private String mKeyword;
    private int pageNo = 0;
    private int limitCnt = 20;
    private int mTotCnt = 0;

    private Map<String, Object> mStbStateMap;
    private String mStbState;             // GetSetTopStatus API로 가져오는 값.
    private String mStbRecordingchannel1; // GetSetTopStatus API로 가져오는 값.
    private String mStbRecordingchannel2; // GetSetTopStatus API로 가져오는 값.
    private String mStbWatchingchannel;   // GetSetTopStatus API로 가져오는 값.
    private String mStbPipchannel;        // GetSetTopStatus API로 가져오는 값.

    private JYSharedPreferences mPref;
    private boolean mLockListView = true;
    private ArrayList<JSONObject> mStbRecordReservelist = new ArrayList<JSONObject>();
    private HashMap<String, Object>   RemoteChannelControl = new HashMap<String,Object>();

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
        mStbStateMap = new HashMap<String, Object>();
        initView();

        if (mPref.isPairingCompleted() == true) { // 페어링 했을 시
            requestGetSetTopStatus(); // 셋탑 상태 - 예약녹화물 리스트 - 한 채널 평성표 차례대로 호출.
        } else { // 페어링 안했을 시
            requestGetRecordReservelist();
        }
//            reqProgramList();
    }

    private void initView()
    {
        mEmptyMessage = (TextView)getView().findViewById(R.id.search_empty_msg);
        mListView = (SwipeMenuListView)getView().findViewById(R.id.programlistview);
        mAdapter = new SearchProgramAdapter(getActivity(),mProgramlist);
        mListView.setAdapter(mAdapter);
        mListView.setMenuCreator(creator);
        mListView.setOnScrollListener(this);
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                CMLog.d("wd", "menu.getViewType() : " + menu.getViewType() + ", Index : " + index);
                // 셋탑 미연동
                if (menu.getViewType() == 0) {
                    String alertTitle = "셋탑박스 연동 필요";
                    String alertMessage1 = getString(R.string.error_not_paring_compleated3);
                    String alertMessage2 = "";
                    CMAlertUtil.Alert(getActivity(), alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }, true);
                }

                // 현재 방송 중
                if (menu.getViewType() == 5) {
                    // 2016-03-29 "현재 방송 중"인 목록의 타이틀을 "셋탑 미 연동"에서 "현재 방송 중"으로 변경
                    // 그래서 5번 타입 추가함.
                    // do nothing!!! and comment out below!!!
                } else {
                    if (index == 0) {
                        // 시청예약
                        // 2016-03-30 HD, Google 셋탑박스에서는 녹화예약버튼이 없어야 함.
                        // 그래서 6, 7번 타입 추가.
                        if (menu.getViewType() == 1 || menu.getViewType() == 2 || menu.getViewType() == 6) {
                            String programId = mProgramlist.get(position).getChannelProgramID();
                            String seriesId = "";
                            String programTitle = mProgramlist.get(position).getChannelProgramTitle();
                            String programTime = mProgramlist.get(position).getChannelProgramTime();

                            mPref.addWatchTvReserveAlarm(programId, seriesId, programTitle, programTime);
                            mAdapter.notifyDataSetChanged();
                        }
                        // 시청예약취소
                        // 2016-03-30 HD, Google 셋탑박스에서는 녹화예약버튼이 없어야 함.
                        // 그래서 6, 7번 타입 추가.
                        else if (menu.getViewType() == 3 || menu.getViewType() == 4 || menu.getViewType() == 7) {
                            // 2016-03-29 시청예약시 데이타베이스 처리를 programId 로 하는데 서버에서 programId를 중복으로 보내고 있다
                            // 그래서 시청시간을 추가함.
                            mPref.removeWatchTvReserveAlarmWithProgramIdAndStartTime(mProgramlist.get(position).getChannelProgramID(), mProgramlist.get(position).getChannelProgramTime());
                            mAdapter.notifyDataSetChanged();
                        }

                    } else {
                        // 녹화예약
                        if (menu.getViewType() == 1 || menu.getViewType() == 3) {
                            JSONObject reservjo = mAdapter.getStbRecordReserveWithChunnelId(mProgramlist.get(position).getChannelId(), mProgramlist.get(position));
                            if (reservjo == null) {
                                String programTime = mProgramlist.get(position).getChannelProgramTime();
                                requestSetRecordReserve(mProgramlist.get(position).getChannelId(), programTime);
                            }
                        }
                        // 녹화예약취소
                        else if (menu.getViewType() == 2 || menu.getViewType() == 4) {
                            try {
                                JSONObject reservjo = mAdapter.getStbRecordReserveWithChunnelId(mProgramlist.get(position).getChannelId(), mProgramlist.get(position));
                                if (reservjo != null) {
                                    String starttime = reservjo.getString("RecordStartTime");
                                    String seriesid = reservjo.getString("SeriesId");
                                    requestSetRecordCancelReserve(mProgramlist.get(position).getChannelId(), starttime, seriesid);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                return false;
            }
        });
    }

    /**
     * Swipe Menu for ListView
     */
    SwipeMenuCreator creator = new SwipeMenuCreator() {
        @Override
        public void create(SwipeMenu menu) {
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, getResources().getDisplayMetrics());

            // 셋탑 미 연동
            if (menu.getViewType() == 0) {
                SwipeMenuItem item1 = new SwipeMenuItem(getActivity());
                item1.setBackground(R.color.red);
                item1.setWidth(width*2);
                item1.setTitle("셋탑 미 연동");
                item1.setTitleSize(12);
                item1.setTitleColor(getResources().getColor(R.color.white));
                menu.addMenuItem(item1);
            }

            // 현재 방송 중
            if (menu.getViewType() == 5) {
                SwipeMenuItem item1 = new SwipeMenuItem(getActivity());
                item1.setBackground(R.color.red);
                item1.setWidth(width*2);

                // 2016-03-29 "현재 방송 중"인 목록의 타이틀을 "셋탑 미 연동"에서 "현재 방송 중"으로 변경
                // 그래서 5번 타입 추가함.
                item1.setTitle("현재 방송 중");

                item1.setTitleSize(12);
                item1.setTitleColor(getResources().getColor(R.color.white));
                menu.addMenuItem(item1);
            }
            // 둘다 가능
            else if (menu.getViewType() == 1) {
                SwipeMenuItem item1 = new SwipeMenuItem(getActivity());
                item1.setBackground(new ColorDrawable(Color.rgb(0xB3, 0xCF, 0x3B)));
                item1.setWidth(width);
                item1.setTitle("시청예약");
                item1.setTitleSize(12);
                item1.setTitleColor(Color.WHITE);
                menu.addMenuItem(item1);
                SwipeMenuItem item2 = new SwipeMenuItem(getActivity());
                item2.setBackground(new ColorDrawable(Color.rgb(0xED, 0x72, 0x33)));
                item2.setWidth(width);
                item2.setTitle("녹화예약");
                item2.setTitleSize(12);
                item2.setTitleColor(Color.WHITE);
                menu.addMenuItem(item2);
            }
            // 시청만 가능
            else if (menu.getViewType() == 2) {
                SwipeMenuItem item1 = new SwipeMenuItem(getActivity());
                item1.setBackground(new ColorDrawable(Color.rgb(0xB3, 0xCF, 0x3B)));
                item1.setWidth(width);
                item1.setTitle("시청예약");
                item1.setTitleSize(12);
                item1.setTitleColor(Color.WHITE);;
                menu.addMenuItem(item1);
                SwipeMenuItem item2 = new SwipeMenuItem(getActivity());
                item2.setBackground(new ColorDrawable(Color.rgb(0xC1, 0x4F, 0x28)));
                item2.setWidth(width);
                item2.setTitle("녹화예약취소");
                item2.setTitleSize(12);
                item2.setTitleColor(Color.WHITE);
                menu.addMenuItem(item2);
            }
            // 녹화만 가능
            else if (menu.getViewType() == 3) {
                SwipeMenuItem item1 = new SwipeMenuItem(getActivity());
                item1.setBackground(new ColorDrawable(Color.rgb(0x7F, 0x94, 0x24)));
                item1.setWidth(width);
                item1.setTitle("시청예약취소");
                item1.setTitleSize(12);
                item1.setTitleColor(Color.WHITE);
                menu.addMenuItem(item1);
                SwipeMenuItem item2 = new SwipeMenuItem(getActivity());
                item2.setBackground(new ColorDrawable(Color.rgb(0xED, 0x72, 0x33)));
                item2.setWidth(width);
                item2.setTitle("녹화예약");
                item2.setTitleSize(12);
                item2.setTitleColor(Color.WHITE);
                menu.addMenuItem(item2);
            }
            // 둘다 불가능
            else if (menu.getViewType() == 4) {
                SwipeMenuItem item1 = new SwipeMenuItem(getActivity());
                item1.setBackground(new ColorDrawable(Color.rgb(0x7F, 0x94, 0x24)));
                item1.setWidth(width);
                item1.setTitle("시청예약취소");
                item1.setTitleSize(12);
                item1.setTitleColor(Color.WHITE);
                menu.addMenuItem(item1);
                SwipeMenuItem item2 = new SwipeMenuItem(getActivity());
                item2.setBackground(new ColorDrawable(Color.rgb(0xC1, 0x4F, 0x28)));
                item2.setWidth(width);
                item2.setTitle("녹화예약취소");
                item2.setTitleSize(12);
                item2.setTitleColor(Color.WHITE);
                menu.addMenuItem(item2);
            }
            // 시청예약
            // 2016-03-30 HD, Google 셋탑박스에서는 녹화예약버튼이 없어야 함.
            // 그래서 6, 7번 타입 추가.
            else if (menu.getViewType() == 6) {
                SwipeMenuItem item1 = new SwipeMenuItem(getActivity());
                item1.setBackground(new ColorDrawable(Color.rgb(0xB3, 0xCF, 0x3B)));
                item1.setWidth(width*2);
                item1.setTitle("시청예약");
                item1.setTitleSize(12);
                item1.setTitleColor(getResources().getColor(R.color.white));
                menu.addMenuItem(item1);
            }
            // 시청예약취소
            // 2016-03-30 HD, Google 셋탑박스에서는 녹화예약버튼이 없어야 함.
            // 그래서 6, 7번 타입 추가.
            else if (menu.getViewType() == 7) {
                SwipeMenuItem item1 = new SwipeMenuItem(getActivity());
                item1.setBackground(new ColorDrawable(Color.rgb(0x7F, 0x94, 0x24)));
                item1.setWidth(width*2);
                item1.setTitle("시청예약취소");
                item1.setTitleSize(12);
                item1.setTitleColor(getResources().getColor(R.color.white));
                menu.addMenuItem(item1);
            }
        }
    };

    private void reloadAll()
    {
        pageNo = 0;
        limitCnt = 20;
        mTotCnt = 0;
        mProgramlist.clear();
        mStbRecordReservelist.clear();
        if (mPref.isPairingCompleted() == true) { // 페어링 했을 시
            requestGetSetTopStatus(); // 셋탑 상태 - 예약녹화물 리스트 - 한 채널 평성표 차례대로 호출.
        } else { // 페어링 안했을 시
            requestGetRecordReservelist();
        }

    }

    private void requestGetSetTopStatus() {
        String uuid = mPref.getValue(JYSharedPreferences.UUID, "");
        String url = mPref.getRumpersServerUrl() + "/GetSetTopStatus.asp?deviceId="+uuid;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                parseGetSetTopStatus(response);

                String resultCode = (String) mStbStateMap.get("resultCode");
                if ( Constants.CODE_RUMPUS_OK.equals(resultCode) ) {
                    mStbState             = (String) mStbStateMap.get("state");
                    mStbRecordingchannel1 = (String) mStbStateMap.get("recordingchannel1");
                    mStbRecordingchannel2 = (String) mStbStateMap.get("recordingchannel2");
                    mStbWatchingchannel   = (String) mStbStateMap.get("watchingchannel");
                    mStbPipchannel        = (String) mStbStateMap.get("pipchannel");
                    mAdapter.setStbState(mStbState, mStbRecordingchannel1, mStbRecordingchannel2, mStbWatchingchannel, mStbPipchannel);

                    requestGetRecordReservelist();
                } else {
                    // 구글 셋탑 이외의 셋탑에서는 에러 코드에 대한 팝업 처리를 한다.
                    if ( "HD".equals(mPref.getSettopBoxKind()) || "PVR".equals(mPref.getSettopBoxKind()) ) {
                        if (UiUtil.checkSTBStateCode(resultCode, getActivity()) == false) {
                            mStbState             = "";
                            mStbRecordingchannel1 = "";
                            mStbRecordingchannel2 = "";
                            mStbWatchingchannel   = "";
                            mStbPipchannel        = "";
                            mAdapter.setStbState(mStbState, mStbRecordingchannel1, mStbRecordingchannel2, mStbWatchingchannel, mStbPipchannel);
                        }
                    }
                }

                reqProgramList();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    CMAlertUtil.ToastShort(getActivity(), getActivity().getString(R.string.error_network_timeout));
                } else if (error instanceof NoConnectionError) {
                    CMAlertUtil.ToastShort(getActivity(), getActivity().getString(R.string.error_network_noconnectionerror));
                } else if (error instanceof ServerError) {
                    CMAlertUtil.ToastShort(getActivity(), getActivity().getString(R.string.error_network_servererror));
                } else if (error instanceof NetworkError) {
                    CMAlertUtil.ToastShort(getActivity(), getActivity().getString(R.string.error_network_networkerrorr));
                }
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    private void parseGetSetTopStatus(String response) {
        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new ByteArrayInputStream(response.getBytes("utf-8")), "utf-8");

            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equalsIgnoreCase("resultCode")) {
                        String resultCode = xpp.nextText();
                        mStbStateMap.put("resultCode", resultCode);
                    } else if (xpp.getName().equalsIgnoreCase("errorString")) {
                        String errorString = xpp.nextText();
                        mStbStateMap.put("errorString", errorString);

                    } else if (xpp.getName().equalsIgnoreCase("state")) {
                        String errorString = xpp.nextText();
                        mStbStateMap.put("state", errorString);
                    } else if (xpp.getName().equalsIgnoreCase("recordingchannel1")) {
                        String errorString = xpp.nextText();
                        mStbStateMap.put("recordingchannel1", errorString);
                    } else if (xpp.getName().equalsIgnoreCase("recordingchannel2")) {
                        String errorString = xpp.nextText();
                        mStbStateMap.put("recordingchannel2", errorString);
                    } else if (xpp.getName().equalsIgnoreCase("watchingchannel")) {
                        String errorString = xpp.nextText();
                        mStbStateMap.put("watchingchannel", errorString);
                    } else if (xpp.getName().equalsIgnoreCase("pipchannel")) {
                        String errorString = xpp.nextText();
                        mStbStateMap.put("pipchannel", errorString);
                    }
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* 녹화 예약 목록 호출 */
    private void requestGetRecordReservelist() {
        ((SearchMainActivity)getActivity()).showProgressDialog("", getString(R.string.wait_a_moment));
        String          uuid    = mPref.getValue(JYSharedPreferences.UUID, "");
        String          tk      = JYSharedPreferences.RUMPERS_TERMINAL_KEY;
        // for test
        //String          url     = "http://192.168.44.10/SMApplicationserver/getrecordReservelist.asp?Version=1&terminalKey=C5E6DBF75F13A2C1D5B2EFDB2BC940&deviceId=68590725-3b42-4cea-ab80-84c91c01bad2";
        String          url     = mPref.getRumpersServerUrl() + "/getRecordReservelist.asp?Version=1&terminalKey=" + tk + "&deviceId=" + uuid;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(tag, response);
                ((SearchMainActivity)getActivity()).hideProgressDialog();
                String sResultCode = parseGetRecordReservelist(response); // 파싱 결과를 리턴 받는다.
                if ( Constants.CODE_RUMPUS_OK.equals(sResultCode) ) { // 예약목록을 받았을 때
                    // reqProgramList();7
                } else if ( Constants.CODE_RUMPUS_ERROR_205_Not_Found.equals(sResultCode) ) { // 예약 목록이 없을때도 정상응답 받은 거임.
                    // reqProgramList();
                } else if ( "241".equals(sResultCode) ) { // 미 페어링 상태에도 정상응답 받은 거임.
                    // reqProgramList();
                } else { // 그외는 error
                    String msg = "getRecordReservelist("+sResultCode+":"+mStbStateMap.get("errorString")+")";
                    CMAlertUtil.Alert(getActivity(), "알림", msg);
                }

                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ((SearchMainActivity)getActivity()).hideProgressDialog();
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("version", String.valueOf(1));
                params.put("areaCode", String.valueOf(0));
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    private String parseGetRecordReservelist(String response) {
        String               sResultCode = "0";   // 응답받은 resultCode
        StringBuilder        sb          = new StringBuilder();
        StringBuilder        sb2         = new StringBuilder(); // for array
        XmlPullParserFactory factory     = null;
        List<String> strings     = new ArrayList<String>();

        //response = response.replace("<![CDATA[","");
        //response = response.replace("]]>", "");
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new ByteArrayInputStream(response.getBytes("UTF-8")), "UTF-8");

            int eventType = xpp.getEventType();
            while ( eventType != XmlPullParser.END_DOCUMENT ) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equalsIgnoreCase("response")) {
                        //
                    } else if (xpp.getName().equalsIgnoreCase("resultCode")) {
                        sResultCode = xpp.nextText(); mStbStateMap.put("resultCode",sResultCode);
                    } else if (xpp.getName().equalsIgnoreCase("errorString")) {
                        mStbStateMap.put("errorString",xpp.nextText());
                    } else if (xpp.getName().equalsIgnoreCase("Reserve_Item")) {
                        //
                    } else if (xpp.getName().equalsIgnoreCase("RecordingType")) {
                        // array start -------------------------------------------------------------
                        sb2.append("{\"RecordingType\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("OverlapId")) {
                        sb2.append(",\"OverlapId\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("SeriesId")) {
                        sb2.append(",\"SeriesId\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("ScheduleId")) {
                        sb2.append(",\"ScheduleId\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("ChannelId")) {
                        sb2.append(",\"ChannelId\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("ChannelNo")) {
                        sb2.append(",\"ChannelNo\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("ChannelName")) {
                        sb2.append(",\"ChannelName\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("Program_Grade")) {
                        sb2.append(",\"Program_Grade\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("Channel_logo_img")) {
                        sb2.append(",\"Channel_logo_img\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("ProgramName")) {
                        sb2.append(",\"ProgramName\":\"").append(xpp.nextText()).append("\"");  // <![CDATA[ ]]>  한글 깨짐.
                    } else if (xpp.getName().equalsIgnoreCase("RecordStartTime")) {
                        sb2.append(",\"RecordStartTime\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("RecordEndTime")) {
                        sb2.append(",\"RecordEndTime\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("RecordHD")) {
                        sb2.append(",\"RecordHD\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("RecordPaytype")) {
                        sb2.append(",\"RecordPaytype\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("RecordStatus")) {
                        // array end -----------------------------------------------------------
                        sb2.append(",\"RecordStatus\":\"").append(xpp.nextText()).append("\"}");
                        strings.add(sb2.toString());
                        sb2.setLength(0);
                        //} else if ( bStartedArr == true && xpp.getName().equalsIgnoreCase("Reserve_Item")) {

                    } else if (xpp.getName().equalsIgnoreCase("response")) {
                        //
                    }
                }
                eventType = xpp.next();
            }
            sb.append("}");
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if ( strings.size() > 0 ) {
            try {
                for (int i = 0; i < strings.size(); i++) {
                    String str = strings.get(i);
                    mStbRecordReservelist.add(new JSONObject(str));
                }
            } catch ( JSONException e ) {
                e.printStackTrace();
            }
            mAdapter.setStbRecordReservelist(mStbRecordReservelist);
        }

        return sResultCode;
    }


    public void reqProgramList()
    {
        mLockListView = true;
        // ((SearchMainActivity)getActivity()).showProgressDialog("", getString(R.string.wait_a_moment));
        // swlim. "아빠를 부탁해" 처럼 공백이 있으면 검색 결과가 안나와서 아래의 URLEncoder 적용.
        String searchWord = mKeyword;
        try {
            searchWord = URLEncoder.encode(searchWord, "UTF-8");
        } catch ( UnsupportedEncodingException e ) {
            e.printStackTrace();
        }
        //String url = Constants.SERVER_URL_AIRCODE_REAL+"/searchSchedule.xml?version=1&areaCode="+mAreaCode+"&searchString="+mKeyword+"&offset="+pageNo+"&limit="+limitCnt;
        String url = Constants.SERVER_URL_AIRCODE_REAL+"/searchSchedule.xml?version=1&areaCode="+mAreaCode+"&searchString="+searchWord+"&offset="+pageNo+"&limit="+limitCnt;
        CMLog.d("ljh","url : "+url);
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(tag, response);
                parseGetSearchList(response);
                mAdapter.notifyDataSetChanged();
                mLockListView = false;
                // ((SearchMainActivity)getActivity()).hideProgressDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // ((SearchMainActivity)getActivity()).hideProgressDialog();
                mLockListView = false;
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

            mAdapter.notifyDataSetChanged();
            ((SearchMainActivity)getActivity()).setSearchCountText(mTotCnt);

            if (mProgramlist.size() == 0) {
                mEmptyMessage.setVisibility(View.VISIBLE);
            } else {
                mEmptyMessage.setVisibility(View.GONE);
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /* 예약녹화 */
    private void requestSetRecordReserve(String channelId, String starttime) {
        ((SearchMainActivity)getActivity()).showProgressDialog("", getString(R.string.wait_a_moment));
        try {
            starttime = URLEncoder.encode(starttime, "utf-8");
        } catch ( UnsupportedEncodingException e ) {
            e.printStackTrace();
        }
        String terminalKey = JYSharedPreferences.RUMPERS_TERMINAL_KEY;
        String uuid = mPref.getValue(JYSharedPreferences.UUID, "");
        String url  = mPref.getRumpersServerUrl() + "/SetRecordReserve.asp?Version=1&terminalKey=" + terminalKey + "&deviceId=" + uuid + "&channelId="
                + channelId + "&StartTime=" + starttime;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ((SearchMainActivity)getActivity()).hideProgressDialog();
                parseSetRecordReserve(response);
                if ( Constants.CODE_RUMPUS_OK.equals(RemoteChannelControl.get("resultCode")) ) {
                    // ok
                    reloadAll(); // 기존 들고 있던 데이터 다 초기화 하고 다시 받아온다. 셋탑상태+예약녹화리스트
                } else {
                    String errorString = (String)RemoteChannelControl.get("errString");
                    CMAlertUtil.Alert(getActivity(), "알림", errorString);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ((SearchMainActivity)getActivity()).hideProgressDialog();

            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("version", String.valueOf(1));
                params.put("areaCode", String.valueOf(0));
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    private void parseSetRecordReserve(String response) {
        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new ByteArrayInputStream(response.getBytes("utf-8")), "utf-8");

            int eventType = xpp.getEventType();
            while ( eventType != XmlPullParser.END_DOCUMENT ) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equalsIgnoreCase("resultCode")) {
                        String resultCode = xpp.nextText();
                        RemoteChannelControl.put("resultCode", resultCode);
                    } else if (xpp.getName().equalsIgnoreCase("errString")) {
                        String errorString = xpp.nextText();
                        RemoteChannelControl.put("errString", errorString);
                    }
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* 예약 녹화 취소 */
    private void requestSetRecordCancelReserve(String channelId, String starttime, String seriesId) {
        ((SearchMainActivity)getActivity()).showProgressDialog("", getString(R.string.wait_a_moment));
        try {
            starttime = URLEncoder.encode(starttime, "utf-8");
        } catch ( UnsupportedEncodingException e ) {
            e.printStackTrace();
        }
        String terminalKey = JYSharedPreferences.RUMPERS_TERMINAL_KEY;
        String uuid = mPref.getValue(JYSharedPreferences.UUID, "");
        String url  = mPref.getRumpersServerUrl() + "/SetRecordCancelReserve.asp?Version=1&terminalKey=" + terminalKey + "&deviceId=" + uuid + "&channelId="
                + channelId + "&StartTime=" + starttime + "&seriesId=" + seriesId + "&ReserveCancel=2";
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ((SearchMainActivity)getActivity()).hideProgressDialog();
                parseSetRecordCancelReserve(response);
                if ( Constants.CODE_RUMPUS_OK.equals(RemoteChannelControl.get("resultCode")) ) {
                    // ok
                    reloadAll(); // 기존 들고 있던 데이터 다 초기화 하고 다시 받아온다. 셋탑상태+예약녹화리스트
                } else  {        // Hold Mode
                    String errorString = (String)RemoteChannelControl.get("errorString");
                    CMAlertUtil.Alert(getActivity(), "알림", errorString);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ((SearchMainActivity)getActivity()).hideProgressDialog();
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("version", String.valueOf(1));
                params.put("areaCode", String.valueOf(0));
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    private void parseSetRecordCancelReserve(String response) {
        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new ByteArrayInputStream(response.getBytes("utf-8")), "utf-8");

            int eventType = xpp.getEventType();
            while ( eventType != XmlPullParser.END_DOCUMENT ) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equalsIgnoreCase("resultCode")) {
                        String resultCode = xpp.nextText();
                        RemoteChannelControl.put("resultCode", resultCode);
                    } else if (xpp.getName().equalsIgnoreCase("errString")) {
                        String errorString = xpp.nextText();
                        RemoteChannelControl.put("errString", errorString);
                    }
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
