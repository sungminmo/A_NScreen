package com.stvn.nscreen.custom;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stvn.nscreen.R;

/**
 * Created by leejunghoon on 15. 10. 9..
 */
public class CMAlertDialog extends Dialog{

    private LinearLayout mType1,mType2;
    private ImageView mType1_close;
    private TextView mType1_Title,mType1_Content;
    private TextView mType2_Title,mType2_Content1,mType2_Content2;
    private Button mType2_Cancel,mType2_Ok;
    private CMDialogType mType;
    public enum CMDialogType{
        DialogType1, DialogType2
    };


    public CMAlertDialog(Context context) {
        super(context, R.style.CustomAlertDialog);
        setContentView(R.layout.dialog_custom);
        mType = CMDialogType.DialogType2;
        initView();
    }
    public CMAlertDialog(Context context,CMDialogType type) {
        super(context, R.style.CustomAlertDialog);
        setContentView(R.layout.dialog_custom);
        mType = type;
        initView();
    }

    private void initView()
    {
        //Dialog Type1
        mType1 = (LinearLayout)findViewById(R.id.type1);
        mType1_close = (ImageView)findViewById(R.id.type1_close);
        mType1_Title = (TextView)findViewById(R.id.type1_title);
        mType1_Content = (TextView)findViewById(R.id.type1_text);
        mType1_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //Dialog Type2
        mType2 = (LinearLayout)findViewById(R.id.type2);
        mType2_Title = (TextView)findViewById(R.id.type2_title);
        mType2_Content1 = (TextView)findViewById(R.id.type2_text);
        mType2_Content2 = (TextView)findViewById(R.id.type2_text2);
        mType2_Cancel = (Button)findViewById(R.id.type2_cancel);
        mType2_Ok = (Button)findViewById(R.id.type2_ok);

        switch (mType)
        {
            case DialogType1:
                mType1.setVisibility(View.VISIBLE);
                mType2.setVisibility(View.GONE);
                break;
            case DialogType2:
                mType1.setVisibility(View.GONE);
                mType2.setVisibility(View.VISIBLE);
                mType2_Ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
                mType2_Cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
                break;
        }
    }

    public void setMessage(String msg)
    {
        switch (mType)
        {
            case DialogType1:
                mType1_Content.setText(msg);
                break;
            case DialogType2:
                mType2_Content1.setText(msg);
                break;
        }
    }

    public void setTitle(String title)
    {
        switch (mType)
        {
            case DialogType1:
                mType1_Title.setText(title);
                break;
            case DialogType2:
                mType2_Title.setText(title);
                break;
        }
    }

    public void setMessage(String msg1,String msg2)
    {
        mType2_Content1.setText(msg1);
        mType2_Content2.setText(msg2);

    }

    public void setMessage(String msg1,String msg2,boolean isbold1,boolean isbold2)
    {
        mType2_Content1.setText(msg1);
        mType2_Content2.setText(msg2);
        if(isbold1)
            mType2_Content1.setTypeface(null,Typeface.BOLD);
        if(isbold2)
            mType2_Content2.setTypeface(null,Typeface.BOLD);

    }

    /***
     * 확인버튼
     */
    public void setPositiveButton(String text,final DialogInterface.OnClickListener listener)
    {
        mType2_Ok.setText(text);
        mType2_Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (listener != null) {
                    listener.onClick(CMAlertDialog.this, DialogInterface.BUTTON_POSITIVE);
                }
            }
        });
    }

    /***
     * 취소버튼
     */
    public void setNegativeButton(String text,final DialogInterface.OnClickListener listener)
    {
        mType2_Cancel.setText(text);
        mType2_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(listener!=null)
                {
                    listener.onClick(CMAlertDialog.this,DialogInterface.BUTTON_NEGATIVE);
                }
            }
        });
    }

}
