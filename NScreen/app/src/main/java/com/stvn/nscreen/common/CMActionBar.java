package com.stvn.nscreen.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.jjiya.android.common.CMConstants;

/**
 * 공통으로 사용되는 상단 액션바
 * Created by kimwoodam on 2015. 9. 17..
 */
public class CMActionBar extends LinearLayout {

    private Context mContext;


    public CMActionBar(Context context) {
        super(context);
        this.mContext = context;
//        initializeLayout();
        LayoutParams lParams;
        lParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        setLayoutParams(lParams);
    }

    public CMActionBar(Context context, AttributeSet attr) {
        super(context, attr);
        this.mContext = context;
//        initialize();

        // xml android namespace 에서 text 항목에 정의한 이름이 있다면 | (pipeline) 으로 split 한 후 갯수만큼 탭을 만든다.
        String text = "";
        if (null != attr) {
            int textResId = attr.getAttributeResourceValue(CMConstants.ANDROID_SCHEMA, "text", 0);
            if (textResId == 0) {
                text = attr.getAttributeValue(CMConstants.ANDROID_SCHEMA, "text");
            } else {
                text = context.getString(textResId);
            }

            if(text != null && !"".equals(text)) {
                String[] tabTitle = text.split("\\|");
                if(tabTitle.length > 0){
//                    initializeTabMenu(tabTitle);
//                    makeTab(0);
                }
            }
        }
    }

    private void initializeLayout(Context context, AttributeSet attrs) {
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        inflater.inflate();
    }
}