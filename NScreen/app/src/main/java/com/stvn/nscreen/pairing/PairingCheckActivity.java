package com.stvn.nscreen.pairing;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.stvn.nscreen.R;

/**
 * Created by limdavid on 15. 10. 23..
 */

public class PairingCheckActivity extends AppCompatActivity {

    private Button pairing_check_okBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing_check);

        pairing_check_okBtn = (Button) findViewById(R.id.pairing_check_okBtn);

        pairing_check_okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}