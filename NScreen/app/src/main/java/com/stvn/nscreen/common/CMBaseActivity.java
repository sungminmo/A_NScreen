package com.stvn.nscreen.common;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.stvn.nscreen.R;

/**
 * 공통 처리를 위한 최상위 부모 액티티
 * Created by kimwoodam on 2015. 9. 19..
 */
public class CMBaseActivity extends AppCompatActivity implements CMActionBar.CMActionBarListener {

    private LinearLayout mMainView;
    private CMActionBar mActionBar;


    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.activity_base);
        this.mMainView = (LinearLayout)findViewById(R.id.base_view);

        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(layoutResID, null);
        this.mMainView.addView(contentView);

        ViewGroup.LayoutParams lParams = contentView.getLayoutParams();
        lParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lParams.height = ViewGroup.LayoutParams.MATCH_PARENT;

        this.mActionBar = (CMActionBar)findViewById(R.id.common_actionbar);
        this.mActionBar.setActionBarListener(this);
    }


    /**
     * 액션바 사용여부를 설정한다.
     * 액션바 사용여부에 따라 표출여부를 변경
     * */
    public void useActionBar(boolean isUse) {
        if (isUse == false) {
            this.mActionBar.setVisibility(View.GONE);
        } else {
            this.mActionBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 현재 액티비티의 액션바 타이틀을 설정한다.
     * */
    public void setActionBarTitle(String title) {
        this.mActionBar.setActionBarTitle(title);
    }

    /**
     * 현재 액티비티의 액션바 스타일을 설정한다.
     * */
    public void setActionBarStyle(CMActionBar.CMActionBarStyle style) {
        this.mActionBar.setActionBarStyle(style);
    }

    /**
     * 현재 액티비티에서 사용될 타이틀 및 액션바 스타일을 설정한다.
     * */
    public void setActionBarInfo(String title, CMActionBar.CMActionBarStyle style) {
        this.mActionBar.setActionBarTitle(title);
        this.mActionBar.setActionBarStyle(style);
    }

    /**
     * 액션바 내의 리스너 메소드
     * */
    @Override
    public void onBackEventPressed() {}
    @Override
    public void onCloseEventPressed() {}
}
