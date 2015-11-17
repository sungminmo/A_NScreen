package com.stvn.nscreen.common;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.ActionBar;
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
    private ProgressDialog mProgressDialog;

    private boolean usCustomActionBar;
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

        useCustomActionBar();
    }


    /**
     * 액션바 사용여부를 설정한다.
     * 액션바 사용여부에 따라 표출여부를 변경
     * */
    public void useActionBar(boolean isUse) {
        if (isUse == false) {
            if (this.usCustomActionBar == true) {
                this.mActionBar.setVisibility(View.GONE);
            } else {
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.hide();
                }
            }
        } else {
            if (this.usCustomActionBar == true) {
                this.mActionBar.setVisibility(View.VISIBLE);
            } else {
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.show();
                }
            }
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
     * OS 기본 액션바 사용시 호출
     * */
    public void useDefaultActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }

        this.mActionBar.setVisibility(View.GONE);

        this.usCustomActionBar = false;
    }

    /**
     * Custom 액션바 사용시 호출
     * */
    public void useCustomActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        this.mActionBar.setVisibility(View.VISIBLE);

        this.usCustomActionBar = true;
    }

    /**
     * Progress Bar 표출
     * */
    public void showProgressDialog(String title, String message) {
        this.mProgressDialog = ProgressDialog.show(this, title, message);
    }

    public void showProgressDialog(String title, String message, boolean indeterminate, boolean cancelable) {
        this.mProgressDialog = ProgressDialog.show(this, title, message, indeterminate, cancelable);
    }

    /**
     * Progress Bar 닫기
     * */
    public void hideProgressDialog() {
        if (this.mProgressDialog != null) {
            this.mProgressDialog.dismiss();
        }
        this.mProgressDialog = null;
    }


    /**
     * 액션바 내의 리스너 메소드
     * */
    @Override
    public void onBackEventPressed() {}
    @Override
    public void onCloseEventPressed() {}
    @Override
    public void onSideMenuEventPressed() {}
    @Override
    public void onSearchEventPressed() {}
}
