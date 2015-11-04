package com.stvn.nscreen.custom;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stvn.nscreen.R;
import com.stvn.nscreen.util.CMAlertUtil;

/**
 * Created by leejunghoon on 15. 10. 9..
 */
public class CMAlertDialog extends Dialog{

    private LinearLayout mType1,mType2,mType3;
    private ImageView mType1_close;
    private TextView mType1_Title,mType1_Content;
    private TextView mType2_Title,mType2_Content1,mType2_Content2;
    private TextView mType3_Title,mType3_Content1,mType3_Content2;
    private EditText mType3_Field;
    private Button mType2_Cancel,mType2_Ok;
    private Button mType3_Cancel,mType3_Ok;
    private CMDialogType mType;
    private int mType3_min_length, mType3_max_length;
    private String mType3_Field_Text;

    /**
     * DialogType1 : 확인 또는 취소 버튼 없이 우측 상단 X 버튼만 가지는 다이얼로그
     * DialogType2 : 확인 취소 버튼을 가지는 다이얼로그
     * DialogType3 : 입력박스 및 확인 취소 버튼을 가지는 다이얼로그
     * DialogType4 : 확인 버튼을 가지는 다이얼로그
     */

    public enum CMDialogType{
        DialogType1, DialogType2, DialogType3, DialogType4
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

        //Dialog Type3
        mType3_Field_Text = "";
        mType3 = (LinearLayout)findViewById(R.id.type3);
        mType3_Title = (TextView)findViewById(R.id.type3_title);
        mType3_Content1 = (TextView)findViewById(R.id.type3_text);
        mType3_Field = (EditText)findViewById(R.id.type3_field);
        mType3_Content2 = (TextView)findViewById(R.id.type3_text2);
        mType3_Cancel = (Button)findViewById(R.id.type3_cancel);
        mType3_Ok = (Button)findViewById(R.id.type3_ok);
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
            case DialogType4:
                mType1.setVisibility(View.GONE);
                mType2.setVisibility(View.VISIBLE);
                mType2_Ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
                mType2_Cancel.setVisibility(View.GONE);
                findViewById(R.id.type2_empty_l).setVisibility(View.INVISIBLE);
                findViewById(R.id.type2_empty_r).setVisibility(View.INVISIBLE);
                break;
            case DialogType3:
                mType1.setVisibility(View.GONE);
                mType2.setVisibility(View.GONE);
                mType3.setVisibility(View.VISIBLE);
                mType3_Ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
                mType3_Cancel.setOnClickListener(new View.OnClickListener() {
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
            case DialogType3:
                mType3_Content1.setText(msg);
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
            case DialogType2: case DialogType4:
                mType2_Title.setText(title);
                break;
            case DialogType3:
                mType3_Title.setText(title);
                break;
        }
    }

    public void setMessage(String msg1,String msg2) {
        mType2_Content1.setText(msg1);

        if (TextUtils.isEmpty(msg2)) {
            mType2_Content2.setVisibility(View.GONE);
        } else {
            mType2_Content2.setText(msg2);
        }

        if (mType == CMDialogType.DialogType3) {
            mType3_Content1.setText(msg1);

            if (TextUtils.isEmpty(msg2)) {
                mType3_Content2.setVisibility(View.GONE);
            } else {
                mType3_Content2.setText(msg2);
            }
        }
    }

    public void setMessage(String msg1,String msg2,boolean isbold1,boolean isbold2)
    {
        mType2_Content1.setText(msg1);
        mType2_Content2.setText(msg2);
        if(isbold1)
            mType2_Content1.setTypeface(null,Typeface.BOLD);
        if(isbold2)
            mType2_Content2.setTypeface(null,Typeface.BOLD);

        if (mType.compareTo(CMDialogType.DialogType3) == 0) {
            mType3_Content1.setText(msg1);
            mType3_Content2.setText(msg2);

            if (isbold1) {
                mType3_Content1.setTypeface(null, Typeface.BOLD);
            }
            if (isbold2) {
                mType3_Content2.setTypeface(null, Typeface.BOLD);
            }
        }
    }

    public void setMessage(String msg1,Spannable msg2,boolean isbold1,boolean isbold2)
    {
        mType2_Content1.setText(msg1);
        mType2_Content2.setText(msg2);
        if(isbold1)
            mType2_Content1.setTypeface(null,Typeface.BOLD);
        if(isbold2)
            mType2_Content2.setTypeface(null,Typeface.BOLD);

        if (mType.compareTo(CMDialogType.DialogType3) == 0) {
            mType3_Content1.setText(msg1);
            mType3_Content2.setText(msg2);

            if (isbold1) {
                mType3_Content1.setTypeface(null, Typeface.BOLD);
            }
            if (isbold2) {
                mType3_Content2.setTypeface(null, Typeface.BOLD);
            }
        }
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
                if (listener != null) {
                    listener.onClick(CMAlertDialog.this, DialogInterface.BUTTON_NEGATIVE);
                }
            }
        });
    }

    /**
     * 입력 다이얼로그 내 EditText 설정
     * */
    public void setInputSetting(final int minLength, final int maxLength, final boolean secureMode) {

        mType3_min_length = minLength;
        mType3_max_length = maxLength;
        mType3_Field.setFilters(new InputFilter[] { new InputFilter.LengthFilter(maxLength)});
        if (secureMode) {
            mType3_Field.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mType3_Field.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    /**
     * 입력 다이얼로그 내 버튼 리스너 등록 처리
     * */
    public void setInputDlgButton(String ok, String cancel,final CMAlertUtil.InputDialogClickListener listener) {

        mType3_Ok.setText(ok);
        mType3_Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (listener!=null) {
                    listener.positiveClickEvent(CMAlertDialog.this, mType3_Field.getText().toString());
                }
            }
        });

        mType3_Cancel.setText(cancel);
        mType3_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (listener!=null) {
                    listener.negativeClickEvent(CMAlertDialog.this);
                }
            }
        });
    }

    @Override
    public void show() {
        super.show();

        if (mType.compareTo(CMDialogType.DialogType3) == 0) {
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(mType3_Field, InputMethodManager.SHOW_IMPLICIT);
                }
            }, 100);
        }
    }
}
