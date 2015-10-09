package com.stvn.nscreen.search;

import android.app.Activity;
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

import com.stvn.nscreen.R;
import com.stvn.nscreen.common.CMActionBar;
import com.stvn.nscreen.common.CMBaseActivity;

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
    private View mSearchLayout;
    private FrameLayout mFragmentlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_main);
        setActionBarStyle(CMActionBar.CMActionBarStyle.BACK);
        setActionBarTitle("검색");
        initView();
        initData();

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

        mKeywordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        mSearchCount.setText("검색결과가 19건 있습니다.");
                        mKeywordListView.setVisibility(View.GONE);
                        mSearchLayout.setVisibility(View.GONE);
                        mKeywordView.setText("");
                        hideSoftKeyboard();
                        showFragment();
                        return true;
                }


                return false;
            }
        });
        mSearchLayout = (View)findViewById(R.id.search_layout);
        mSearchLayout.setVisibility(View.GONE);
        mFragmentlayout = (FrameLayout)findViewById(R.id.searchFragment);
        switchTab(0);
    }
    private void initData()
    {
        mKeywordList.add("AAAA");
        mKeywordList.add("BBBB");
        mKeywordList.add("CCCC");
        mKeywordList.add("DDDD");
        mKeywordList.add("EEEE");
        mKeywordList.add("FFFF");
        mKeywordList.add("GGGG");
        mKeywordList.add("HHHH");
        mKeywordList.add("IIII");
        mKeywordList.add("JJJJ");
        mKeywordList.add("KKKK");
        mKeywordList.add("LLLL");
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
                mKeywordListView.setVisibility(View.VISIBLE);
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    private void switchTab(int idx)
    {
        mTabSelectIdx = idx;
        mSearchLayout.setVisibility(View.VISIBLE);
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
