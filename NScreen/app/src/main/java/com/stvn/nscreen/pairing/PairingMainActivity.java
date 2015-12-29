package com.stvn.nscreen.pairing;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjiya.android.common.JYSharedPreferences;
import com.stvn.nscreen.R;
import com.stvn.nscreen.custom.CMEditText;
import com.stvn.nscreen.util.CMAlertUtil;
import com.stvn.nscreen.util.CMLog;

import java.util.regex.Pattern;

/**
 * Created by limdavid on 15. 10. 23..
 */

public class PairingMainActivity extends AppCompatActivity implements CMEditText.CMEditTextImeBackListener {

    private static PairingMainActivity mInstance;
    private        JYSharedPreferences mPref;

    private TextView mErrorMessage;
    private CMEditText mPurchasePassword1Edittext;
    private CMEditText mPurchasePassword2Edittext;
    private        Button              cancleButton, nextButton;
    private        ImageButton         backBtn;
    private        LinearLayout        pairing_main_non_title, pairing_main_ok_title, pairing_main_non_ment1, pairing_main_ok_ment1, pairing_main_non_ment2, pairing_main_ok_ment2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing_main);
        mInstance = this;
        mPref     = new JYSharedPreferences(this);

        this.mErrorMessage = (TextView)findViewById(R.id.pairing_main_purchase_password_error);
        mPurchasePassword1Edittext = (CMEditText)findViewById(R.id.pairing_main_purchase_password1_edittext);
        mPurchasePassword2Edittext = (CMEditText)findViewById(R.id.pairing_main_purchase_password2_edittext);
        cancleButton = (Button)findViewById(R.id.pairing_main_cancle_button);
        nextButton   = (Button)findViewById(R.id.pairing_main_next_button);
        backBtn = (ImageButton)findViewById(R.id.backBtn);
        pairing_main_non_title = (LinearLayout) findViewById(R.id.pairing_main_non_title);
        pairing_main_non_ment1 = (LinearLayout) findViewById(R.id.pairing_main_non_ment1);
        pairing_main_non_ment2 = (LinearLayout) findViewById(R.id.pairing_main_non_ment2);
        pairing_main_ok_title = (LinearLayout) findViewById(R.id.pairing_main_ok_title);
        pairing_main_ok_ment1 = (LinearLayout) findViewById(R.id.pairing_main_ok_ment1);
        pairing_main_ok_ment2 = (LinearLayout) findViewById(R.id.pairing_main_ok_ment2);

        mPurchasePassword1Edittext.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20), filterAlpha});
        mPurchasePassword2Edittext.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20), filterAlpha});



        mPurchasePassword1Edittext.setOnEditTextImeBackListener(this);
        mPurchasePassword2Edittext.setOnEditTextImeBackListener(this);

        mPurchasePassword1Edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPurchasePassword1Edittext.setText("");
            }
        });mPurchasePassword2Edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPurchasePassword2Edittext.setText("");
            }
        });

        mPurchasePassword2Edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false) {
                    checkPassword();
                }
            }
        });

        mPurchasePassword1Edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId & EditorInfo.IME_MASK_ACTION) == EditorInfo.IME_ACTION_NEXT) {
                    mPurchasePassword2Edittext.requestFocus();
                    return true;
                }
                return false;
            }
        });

        mPurchasePassword2Edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId & EditorInfo.IME_MASK_ACTION) == EditorInfo.IME_ACTION_DONE) {
                    CMLog.d("wd", "확인 필드 완료");
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    checkPassword();
                    return true;
                }
                return false;
            }
        });

        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        nextButton.setEnabled(false);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mPurchasePassword2Edittext.getText().toString().equals(mPurchasePassword1Edittext.getText().toString())) {
                    String alertTitle = "비밀번호 오류";
                    String alertMsg1 = "비밀번호가 일치하지 않습니다.";
                    String alertMsg2 = "";
                    CMAlertUtil.Alert1(mInstance, alertTitle, alertMsg1, alertMsg2, false, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mPurchasePassword2Edittext.getText().clear();
                            mPurchasePassword1Edittext.getText().clear();
                            mPurchasePassword1Edittext.requestFocus();
                        }
                    }, true);
                } else {
                    if (mPurchasePassword2Edittext.getText().toString().length() < 4) {
                        String alertTitle = "비밀번호 오류";
                        String alertMsg1 = "4자 이상의 비밀번호를 입력해 주십시오.";
                        String alertMsg2 = "";
                        CMAlertUtil.Alert1(mInstance, alertTitle, alertMsg1, alertMsg2, false, false, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mPurchasePassword1Edittext.getText().clear();
                                mPurchasePassword2Edittext.getText().clear();
                            }
                        }, true);
                    } else if (mPurchasePassword2Edittext.getText().toString().length() > 20) {
                        String alertTitle = "비밀번호 오류";
                        String alertMsg1 = "20자 이하의 비밀번호를 입력해 주십시오.";
                        String alertMsg2 = "";
                        CMAlertUtil.Alert1(mInstance, alertTitle, alertMsg1, alertMsg2, false, false, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mPurchasePassword1Edittext.getText().clear();
                                mPurchasePassword2Edittext.getText().clear();
                            }
                        }, true);
                    } else {
                        Intent intent = new Intent(PairingMainActivity.this, com.stvn.nscreen.pairing.PairingSubActivity.class);
                        intent.putExtra("purchasePassword", mPurchasePassword2Edittext.getText().toString());
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });

        String pwd = mPref.getValue(JYSharedPreferences.PURCHASE_PASSWORD, "");

        if ( pwd.length() > 0 ) {
            pairing_main_non_title.setVisibility(View.GONE);
            pairing_main_non_ment1.setVisibility(View.GONE);
            pairing_main_non_ment2.setVisibility(View.GONE);
            pairing_main_ok_title.setVisibility(View.VISIBLE);
            pairing_main_ok_ment1.setVisibility(View.VISIBLE);
            pairing_main_ok_ment2.setVisibility(View.VISIBLE);
        } else {
            pairing_main_non_title.setVisibility(View.VISIBLE);
            pairing_main_non_ment1.setVisibility(View.VISIBLE);
            pairing_main_non_ment2.setVisibility(View.VISIBLE);
            pairing_main_ok_title.setVisibility(View.GONE);
            pairing_main_ok_ment1.setVisibility(View.GONE);
            pairing_main_ok_ment2.setVisibility(View.GONE);
        }
    }
    protected InputFilter filterAlpha = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {

            Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

    private void checkPassword() {
        mErrorMessage.setText("");
        String pwd_1 = mPurchasePassword1Edittext.getText().toString();
        String pwd_2 = mPurchasePassword2Edittext.getText().toString();

        if (TextUtils.isEmpty(pwd_2) == false) {
            if (pwd_1.equals(pwd_2)) {
                mErrorMessage.setText("입력 비밀번호가 일치합니다.");
                nextButton.setEnabled(true);
            } else {
                mErrorMessage.setText("입력 비밀번호가 일치하지 않습니다.");
                nextButton.setEnabled(false);
            }
        }
    }

    @Override
    public void onImeBack(CMEditText ctrl, String text) {
        checkPassword();
    }
}
