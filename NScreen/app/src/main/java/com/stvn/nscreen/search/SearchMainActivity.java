package com.stvn.nscreen.search;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jjiya.android.common.Constants;
import com.jjiya.android.common.JYSharedPreferences;
import com.stvn.nscreen.R;
import com.stvn.nscreen.common.CMActionBar;
import com.stvn.nscreen.common.CMBaseActivity;
import com.stvn.nscreen.common.GsonRequest;
import com.stvn.nscreen.common.KeyWordDataObject;
import com.stvn.nscreen.common.VolleyHelper;
import com.stvn.nscreen.setting.CMSettingData;
import com.stvn.nscreen.util.CMLog;

import java.util.ArrayList;

/**
 * Created by limdavid on 15. 9. 15..
 */

public class SearchMainActivity extends CMBaseActivity {
    private static final String                     tag = SearchMainActivity.class.getSimpleName();
    private              SearchBaseFragment         mFragment;
    private Button mTab1;               // VoD
    private Button mTab2;               // 프로그램명
    private int mTabSelectIdx = 0;
    private ImageView mClose;
    private TextView mSearchCount;
    private EditText mKeywordView;
    private ListView mKeywordListView;
    private ArrayList<String> mKeywordList = new ArrayList<String>();
    private SearchKeywordAdapter mAdapter;
    private FrameLayout mFragmentlayout;
    private TextView mKeywordEmptyView;

    private String mVersion = "1";

    private boolean mLockListView = true;

    private VolleyHelper mVolleyHelper;
    private final int GET_KEYWORD_REQ = 0x99;
    private String mKeyword;
    private boolean mListClicked = false;
    private boolean isFragmentVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_main);
        setActionBarStyle(CMActionBar.CMActionBarStyle.BACK);
        setActionBarTitle("검색");
        mVolleyHelper = VolleyHelper.getInstance(this);
        initView();

    }

    private void initView()
    {
        mTab1 = (Button)findViewById(R.id.button1);
        mTab2 = (Button)findViewById(R.id.button2);
        mTab1.setOnClickListener(tabClicklistener);
        mTab2.setOnClickListener(tabClicklistener);
        mSearchCount = (TextView)findViewById(R.id.searchcount);
        mClose = (ImageView)findViewById(R.id.close);
        mKeywordView = (EditText)findViewById(R.id.search_edit);
        mKeywordView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mKeywordView.setInputType(InputType.TYPE_CLASS_TEXT);
        mKeywordListView = (ListView)findViewById(R.id.searchlistview);
        mAdapter = new SearchKeywordAdapter(this,mKeywordList);
        mAdapter.setClickListener(clickListener);
        mKeywordListView.setAdapter(mAdapter);
        mKeywordView.addTextChangedListener(mKeywordWatcher);
        mKeywordEmptyView = (TextView)findViewById(R.id.keyword_emptyview);
        mKeywordEmptyView.setVisibility(View.VISIBLE);
        mKeywordEmptyView.setText("검색 창에 원하시는 검색어를\n입력해주세요.");

        if (CMSettingData.getInstance().getAdultSearchRestriction(this) == true) {
            mKeywordView.setHint("성인 콘텐츠를 검색하시려면 설정 > 성인검색 제한 설정을 해제 해주세요.");
        } else {
            mKeywordView.setHint("");
        }
        // 최초 힌트 텍스트 표출을 위한 EditText의 text size 변경 처리
        changeSearchTextSize(mKeywordView.getText().toString().length());

        mKeywordListView.setVisibility(View.GONE);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mKeywordView.setText("");
                mSearchCount.setText("");
                mFragmentlayout.setVisibility(View.GONE);
            }
        });

        mKeywordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        isFragmentVisible = true;
                        mKeywordHandler.removeMessages(GET_KEYWORD_REQ);
                        mKeywordListView.setVisibility(View.GONE);
                        hideSoftKeyboard();
                        if (mKeywordView.getText().length() > 0) {
                            mKeyword = mKeywordView.getText().toString();
                            if(mKeyword.trim().isEmpty()) {
                                /*
                                mKeyword = "";
                                if (CMSettingData.getInstance().getAdultSearchRestriction(this) == true) {
                                    mKeywordView.setHint("성인 콘텐츠를 검색하시려면 설정 > 성인검색 제한 설정을 해제 해주세요.");
                                } else {
                                    mKeywordView.setHint("");
                                }
                                */
                                return false;
                            } else {
                                showFragment();
                            }
                        } else {
                            mKeywordEmptyView.setVisibility(View.VISIBLE);
                        }
                        return true;

                }
                return false;
            }
        });
        mKeywordView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                mFragmentlayout.setVisibility(View.GONE);
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DEL:
                        mSearchCount.setText("");
                        mKeywordListView.setVisibility(View.INVISIBLE);
                        mKeywordHandler.removeMessages(GET_KEYWORD_REQ);
                        break;
                }
                return false;
            }
        });

        mLockListView = false;
        mFragmentlayout = (FrameLayout)findViewById(R.id.searchFragment);
        switchTab(0);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
             mKeywordView.setText("");
            }
        },200);
    }

    TextWatcher mKeywordWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {

            changeSearchTextSize(s.length());

            if(s.length()>0)
            {
                mSearchCount.setText("");
                isFragmentVisible = false;
                mKeyword = mKeywordView.getText().toString();
                mKeywordHandler.removeMessages(GET_KEYWORD_REQ);
                if(mKeywordEmptyView.getVisibility()==View.VISIBLE)
                    mKeywordEmptyView.setVisibility(View.GONE);
                // 조회로직 추가
                if(!mListClicked)
                {
                    mKeywordHandler.sendEmptyMessageDelayed(GET_KEYWORD_REQ, 400);
                    mFragmentlayout.setVisibility(View.GONE);
                }else
                    mListClicked = false;
            }else
            {
                mKeywordListView.setVisibility(View.GONE);
//                mFragmentlayout.setVisibility(View.VISIBLE);
                mKeywordEmptyView.setVisibility(View.VISIBLE);
            }
        }
    };

    public void reqKeywordList()
    {
        mLockListView = true;
        mKeywordList.clear();
        String url = Constants.SERVER_URL_CASTIS_PUBLIC+"/getSearchWord.json?version="+mVersion+"&terminalKey="+JYSharedPreferences.WEBHAS_PUBLIC_TERMINAL_KEY+"&includeAdultCategory=0&searchKeyword="+mKeywordView.getText().toString();
        CMLog.d("ljh","url : "+url);
        final GsonRequest gsonRequest = new GsonRequest(url, KeyWordDataObject.class,null,new Response.Listener<KeyWordDataObject>(){
            @Override
            public void onResponse(KeyWordDataObject response) {
                if(isFragmentVisible)
                {
                    isFragmentVisible = false;
                    return;
                }
                mKeywordList.clear();
                for(String str : response.getSearchWordList())
                {
                    mKeywordList.add(str);
                }
                if(mKeywordList.size()>0)
                {
                    mKeywordListView.setVisibility(View.VISIBLE);
                    mKeywordEmptyView.setVisibility(View.GONE);
                }else
                {
                    mKeywordListView.setVisibility(View.GONE);
                    mKeywordEmptyView.setText("검색 창에 원하시는 검색어를\n입력해주세요.");
                    mKeywordEmptyView.setVisibility(View.VISIBLE);
                }
                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        mVolleyHelper.addToRequestQueue(gsonRequest);
    }

    private Handler mKeywordHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case GET_KEYWORD_REQ :
                    reqKeywordList();
                    break;
            }
        }
    };

    private void switchTab(int idx)
    {
        mTabSelectIdx = idx;
        if(!TextUtils.isEmpty(mKeyword))
            mKeywordListView.setVisibility(View.VISIBLE);
        else
            mKeywordListView.setVisibility(View.GONE);
        mFragmentlayout.setVisibility(View.GONE);
        switch (mTabSelectIdx)
        {
            case 0:
                mTab1.setSelected(true);
                mTab2.setSelected(false);
                break;
            case 1:
                mTab1.setSelected(false);
                mTab2.setSelected(true);
                break;
        }
    }

    public void setSearchCountText(int cnt)
    {
        mSearchCount.setText("검색결과가 "+cnt+"건 있습니다.");
    }

    public void showFragment()
    {
        switch (mTabSelectIdx)
        {
            case 0:
                mFragment = new SearchVodFragment();
                break;
            case 1:
                mFragment = new SearchProgramFragment();
                break;
        }
        Bundle bundle = new Bundle();
        bundle.putString("KEYWORD",mKeyword);
        mFragment.setArguments(bundle);
        mKeywordListView.setVisibility(View.GONE);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.searchFragment, mFragment);
        ft.commit();
        mFragmentlayout.setVisibility(View.VISIBLE);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String text = (String)v.getTag();
            switch (v.getId())
            {
                case R.id.keyword:
                    mKeyword =text;
                    mListClicked = true;
                    mKeywordView.setText(text);
                    mKeywordListView.setVisibility(View.GONE);
                    hideSoftKeyboard();
                    if(!TextUtils.isEmpty(mKeyword))
                        showFragment();
                    break;
                case R.id.keywordclose:
                    mKeywordList.remove(text);
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    View.OnClickListener tabClicklistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.button1:
                    switchTab(0);
                    break;
                case R.id.button2:
                    switchTab(1);
                    break;
            }
        }
    };

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mKeywordView.getWindowToken(), 0);
    }

    @Override
    public void onBackEventPressed() {
        finish();
    }

    /**
     * 성인인증 검색제한 여부에 따른 힌트 텍스트와 일반 텍스트의 크기 변경처리를 한다.
     * */
    private void changeSearchTextSize(int textCount) {

        if (CMSettingData.getInstance().getAdultSearchRestriction(this) == true && textCount == 0) {
            mKeywordView.setTextSize(8);
        } else {
            mKeywordView.setTextSize(16);
        }
    }
}
