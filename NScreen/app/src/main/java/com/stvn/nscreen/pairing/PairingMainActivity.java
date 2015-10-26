package com.stvn.nscreen.pairing;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.stvn.nscreen.R;

/**
 * Created by limdavid on 15. 10. 23..
 */

public class PairingMainActivity extends AppCompatActivity {

    private static PairingMainActivity   mInstance;
    private        EditText              mPurchasePassword1Edittext;
    private        EditText              mPurchasePassword2Edittext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing_main);

        mInstance = this;

        mPurchasePassword1Edittext = (EditText)findViewById(R.id.pairing_main_purchase_password1_edittext);
        mPurchasePassword2Edittext = (EditText)findViewById(R.id.pairing_main_purchase_password2_edittext);
        Button cancleButton = (Button)findViewById(R.id.pairing_main_cancle_button);
        Button nextButton   = (Button)findViewById(R.id.pairing_main_next_button);

        cancleButton.setOnClickListener(new View.OnClickListener() {
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
                    } else {
                        Intent intent = new Intent(PairingMainActivity.this, com.stvn.nscreen.pairing.PairingSubActivity.class);
                        intent.putExtra("purchasePassword", mPurchasePassword2Edittext.getText().toString());
                        startActivity(intent);
                    }
                }

            }
        });
    }



}
