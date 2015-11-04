package com.stvn.nscreen.search;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputType;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.http.JYStringRequest;
import com.stvn.nscreen.R;
import com.stvn.nscreen.common.CMActionBar;
import com.stvn.nscreen.common.CMBaseActivity;
import com.stvn.nscreen.util.CMLog;

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

    private String mVersion = "1";
    private String mTerminalKey = "9CED3A20FB6A4D7FF35D1AC965F988D2";
    private RequestQueue mRequestQueue;
    private ProgressDialog mProgressDialog;

    private JYSharedPreferences mPref;
    private boolean mLockListView = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_main);
        setActionBarStyle(CMActionBar.CMActionBarStyle.BACK);
        setActionBarTitle("검색");
        mRequestQueue = Volley.newRequestQueue(this);
        mPref = new JYSharedPreferences(this);
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
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mKeywordView.setText("");
            }
        });

        mKeywordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        mKeywordListView.setVisibility(View.GONE);
                        hideSoftKeyboard();
                        if (mKeywordView.getText().length() > 0)
                            showFragment();
                        else
                            Toast.makeText(SearchMainActivity.this, "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
                        return true;
                }


                return false;
            }
        });
        mLockListView = false;

        mFragmentlayout = (FrameLayout)findViewById(R.id.searchFragment);
        switchTab(0);
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
            if(s.length()>0)
            {
                // 조회로직 추가
                if(!mLockListView)
                    reqKeywordList();
            }
        }
    };

    public void reqKeywordList()
    {
        mLockListView = true;
        mProgressDialog	 = ProgressDialog.show(this, "", getString(R.string.wait_a_moment));
        String url = mPref.getWebhasServerUrl()+"/getSearchWord.xml?version="+mVersion+"&terminalKey="+mTerminalKey+
                "&searchKeyword="+mKeywordView.getText().toString();
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(tag, response);
                parseGetKeywordList(response);
                mAdapter.notifyDataSetChanged();
                mProgressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
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

    private void parseGetKeywordList(String response) {

        StringBuilder sb = new StringBuilder();
        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new ByteArrayInputStream(response.getBytes("utf-8")), "utf-8");

            int eventType = xpp.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;

                switch (eventType)
                {
                    case XmlPullParser.START_TAG:
                        name = xpp.getName();
                        if("searchWord".equals(name)){
                            mKeywordList.add(xpp.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = xpp.getName();
                        break;

                }
                eventType = xpp.next();
            }
            mKeywordListView.setVisibility(View.VISIBLE);
            mAdapter.notifyDataSetChanged();
            mLockListView = false;

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void switchTab(int idx)
    {
        mTabSelectIdx = idx;
        mKeywordListView.setVisibility(View.VISIBLE);
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
        bundle.putString("KEYWORD",mKeywordView.getText().toString());
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
                    mKeywordView.setText(text);
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
}
