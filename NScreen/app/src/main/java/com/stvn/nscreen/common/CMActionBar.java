package com.stvn.nscreen.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stvn.nscreen.R;

/**
 * 공통으로 사용되는 상단 액션바
 * Created by kimwoodam on 2015. 9. 17..
 */
public class CMActionBar extends LinearLayout implements View.OnClickListener {

    private Context mContext;
    private CMActionBarStyle mStyle;
    private RelativeLayout mLeftArea, mRightArea;
    private TextView mActionBarTitle;
    private CMActionBarListener mEventListener;

    /** 상단 액션바 스타일 enum */
    public enum CMActionBarStyle {

        EMPTY (0x00), // No Button ActionBar Style
        BACK (0x01), // Back Button Only ActionBar Style
        CLOSE (0x02); // Right Close Button Only ActionBar Style
        /** 스타일 */
        private int	mStyle;

        /** 생성자  */
        CMActionBarStyle(int style) {
            mStyle = style;
        }

        /** 스타일 가져오기 */
        public int getStyle() {
            return mStyle;
        }
    }

    public interface CMActionBarListener {
        public abstract void onBackEventPressed();
        public abstract void onCloseEventPressed();
    }

    /**
     * 생성자
     */
    public CMActionBar(Context context, AttributeSet attr) {
        super(context, attr);
        this.mContext = context;
        initializeLayout(this.mContext, attr);
    }

    /**
     * 화면 레이아웃 초기화
     */
    private void initializeLayout(Context context, AttributeSet attrs) {
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_actionbar, this);

        this.mLeftArea = (RelativeLayout)findViewById(R.id.actionbar_left);
        this.mRightArea = (RelativeLayout)findViewById(R.id.actionbar_right);
        this.mActionBarTitle = (TextView)findViewById(R.id.actionbar_title);

        String text = "";
        if (null != attrs) {

            TypedArray typeArray = context.obtainStyledAttributes(attrs,  R.styleable.CMActionBar);
            text = typeArray.getString(R.styleable.CMActionBar_actionbar_title);

            if(TextUtils.isEmpty(text) == false) {
                this.mActionBarTitle.setText(text);
            }

            // 액션바 스타일
            // 커스텀 스키마 어트리뷰트가 설정된 경우
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CMActionBar, 0, 0);
            int n = array.getIndexCount();
            for (int i = 0; i < n; i++) {
                int attr = array.getIndex(i);

                switch (attr) {
                    case R.styleable.CMActionBar_actionbar_style:
                        int style = array.getInt(attr, CMActionBarStyle.BACK.getStyle());
                        setActionBarStyle(style);
                        break;
                }
            }
        }
    }

    /**
     * 액션바 내의 이벤트 처리를 위한 리스너 설정
     * */
    public void setActionBarListener(CMActionBarListener listener) {
        this.mEventListener = listener;
    }

    /**
     * 리소스의 스타일 값으로 액션바 스타일 적용
     * */
    public void setActionBarStyle(int style) {
        switch (style) {
            case 0x00:
                this.mStyle = CMActionBarStyle.EMPTY;
                break;
            case 0x01:
                this.mStyle = CMActionBarStyle.BACK;
                break;
            case 0x02:
                this.mStyle = CMActionBarStyle.CLOSE;
        }
        setActionBarStyle(mStyle);
    }

    /**
     * 액션바 스타일 값으로 액션바 스타일 적용
     */
    public void setActionBarStyle(CMActionBarStyle style) {
        this.mStyle = style;

        switch (this.mStyle) {
            case EMPTY: {
                this.mLeftArea.setVisibility(View.GONE);
                this.mRightArea.setVisibility(View.GONE);
                break;
            }
            case BACK: {
                this.mLeftArea.setVisibility(View.VISIBLE);
                this.mRightArea.setVisibility(View.INVISIBLE);
                ((ImageView)findViewById(R.id.actionbar_image_l)).setImageResource(R.mipmap.btn_back);
                this.mLeftArea.setOnClickListener(this);
                this.mLeftArea.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        ViewGroup.LayoutParams lParams = mRightArea.getLayoutParams();
                        lParams.width = mLeftArea.getWidth();
                        mRightArea.setLayoutParams(lParams);
                    }
                });
                break;
            }
            case CLOSE: {
                this.mLeftArea.setVisibility(View.INVISIBLE);
                this.mRightArea.setVisibility(View.VISIBLE);
                this.mRightArea.setOnClickListener(this);
                this.mRightArea.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        ViewGroup.LayoutParams lParams = mLeftArea.getLayoutParams();
                        lParams.width = mRightArea.getWidth();
                        mLeftArea.setLayoutParams(lParams);
                    }
                });
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (this.mEventListener == null) return;

        switch (this.mStyle) {
            case BACK: {
                if (v.getId() == R.id.actionbar_left) {
                    this.mEventListener.onBackEventPressed();
                    break;
                }
                break;
            }
            case CLOSE: {
               if (v.getId() == R.id.actionbar_right) {
                   this.mEventListener.onCloseEventPressed();
               }
                break;
            }
            case EMPTY:
            default:
                break;
        }
    }
}