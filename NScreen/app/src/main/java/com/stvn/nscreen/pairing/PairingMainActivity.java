package com.stvn.nscreen.pairing;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.jjiya.android.common.JYSharedPreferences;
import com.stvn.nscreen.R;

/**
 * Created by limdavid on 15. 10. 23..
 */

public class PairingMainActivity extends AppCompatActivity {

    private static PairingMainActivity   mInstance;
    private              JYSharedPreferences mPref;


    private        EditText              mPurchasePassword1Edittext;
    private        EditText              mPurchasePassword2Edittext;
    private        Button                cancleButton, nextButton;
    private ImageButton backBtn;
    private LinearLayout pairing_main_non_title, pairing_main_ok_title, pairing_main_non_ment1, pairing_main_ok_ment1, pairing_main_non_ment2, pairing_main_ok_ment2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing_main);

        mInstance = this;
        mPref     = new JYSharedPreferences(this);

        mPurchasePassword1Edittext = (EditText)findViewById(R.id.pairing_main_purchase_password1_edittext);
        mPurchasePassword2Edittext = (EditText)findViewById(R.id.pairing_main_purchase_password2_edittext);
        cancleButton = (Button)findViewById(R.id.pairing_main_cancle_button);
        nextButton   = (Button)findViewById(R.id.pairing_main_next_button);
        backBtn = (ImageButton)findViewById(R.id.backBtn);
        pairing_main_non_title = (LinearLayout) findViewById(R.id.pairing_main_non_title);
        pairing_main_non_ment1 = (LinearLayout) findViewById(R.id.pairing_main_non_ment1);
        pairing_main_non_ment2 = (LinearLayout) findViewById(R.id.pairing_main_non_ment2);
        pairing_main_ok_title = (LinearLayout) findViewById(R.id.pairing_main_ok_title);
        pairing_main_ok_ment1 = (LinearLayout) findViewById(R.id.pairing_main_ok_ment1);
        pairing_main_ok_ment2 = (LinearLayout) findViewById(R.id.pairing_main_ok_ment2);


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

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( ! mPurchasePassword1Edittext.getText().toString().equals(mPurchasePassword2Edittext.getText().toString()) ) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(mInstance);
                    alert.setPositiveButton("알림", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.setMessage("비밀번호가 일치하지 않습니다.");
                    alert.show();
                } else {
                    if ( mPurchasePassword2Edittext.getText().toString().length() < 4 ) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(mInstance);
                        alert.setPositiveButton("알림", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alert.setMessage("4자 이상의 비밀번호를 입력해 주십시오.");
                        alert.show();
                    } else if ( mPurchasePassword2Edittext.getText().toString().length() > 20 ) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(mInstance);
                        alert.setPositiveButton("알림", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alert.setMessage("20자 이하의 비밀번호를 입력해 주십시오.");
                        alert.show();
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



}
