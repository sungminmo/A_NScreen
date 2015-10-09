package com.stvn.nscreen.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stvn.nscreen.R;
import com.stvn.nscreen.common.CMActionBar;
import com.stvn.nscreen.common.CMBaseActivity;

/**
 * 설정 화면
 * Created by kimwoodam on 2015. 9. 19..
 * TODO:메뉴 클릭 이벤트 및 메뉴별 화면 작업, 이미지 및 레이아웃 관련 전체적 수정 작업 필요 15.09.23 kimwoodam
 */
public class CMSettingMainActivity extends CMBaseActivity implements View.OnClickListener {

    private final int CMSetting_Region_Index = 100;
    private final int CMSetting_Purchase_Auth_Index = 101;
    private final int CMSetting_Adult_Search_Index = 102;
    private final int CMSetting_Adult_Auth_Index = 103;
    private final int CMSetting_Notice_Index = 104;
    private final int CMSetting_Pay_Channel_Index = 105;
    private final int CMSetting_Customer_Center_Index = 106;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_main);
        setActionBarInfo("설정", CMActionBar.CMActionBarStyle.BACK);

        initializeView();
    }

    @Override
    public void onBackEventPressed() {
        finish();
    }

    /**
     * 설정 메인 화면 UI 설정
     * */
    private void initializeView() {

        ViewGroup mainView = (ViewGroup) findViewById(R.id.setting_layout);;
        LinearLayout itemRow;

        itemRow = makeSettingItem(CMSetting_Region_Index, true, "지역설정", "현재설정지역 : ", "강동구", R.color.text_area_color, false);
        mainView.addView(itemRow);
        itemRow = makeSettingItem(CMSetting_Purchase_Auth_Index, true, "구매인증 비밀번호 관리", "", "", 0, false);
        mainView.addView(itemRow);
        itemRow = makeSettingItem(CMSetting_Adult_Search_Index, true, "성인검색 제한설정", "", "", 0, true);
        mainView.addView(itemRow);
        itemRow = makeSettingItem(CMSetting_Adult_Auth_Index, true, "성인인증", "", "성인인증이 필요합니다.", R.color.red, false);
        mainView.addView(itemRow);
        itemRow = makeSettingItem(CMSetting_Notice_Index, false, "공지사항", "", "", 0, false);
        mainView.addView(itemRow);
        itemRow = makeSettingItem(CMSetting_Pay_Channel_Index, false, "유료채널 안내", "", "", 0, false);
        mainView.addView(itemRow);
        itemRow = makeSettingItem(CMSetting_Customer_Center_Index, false, "고객센터 안내", "", "", 0, false);
        mainView.addView(itemRow);

    }

    /**
     * 설정 메뉴 아이템을 생성 한다.
     * */
    private LinearLayout makeSettingItem(int itemIndex, boolean useImage, String title, String subTitle1, String subTitle2, int subTitleColor, boolean use) {
        LinearLayout itemRow = (LinearLayout)getLayoutInflater().inflate(R.layout.custom_setting_item, null);

        itemRow.setId(itemIndex);
        itemRow.setOnClickListener(this);

        if (useImage == true) {
            itemRow.findViewById(R.id.setting_item_image).setVisibility(View.VISIBLE);
        } else {
            itemRow.findViewById(R.id.setting_item_image).setVisibility(View.INVISIBLE);
        }

        if (TextUtils.isEmpty(title)) {
            title = "";
        }

        if (TextUtils.isEmpty(subTitle1)) {
            subTitle1 = "";
        }

        if (TextUtils.isEmpty(subTitle2)) {
            subTitle2 = "";
        }

        ((TextView)itemRow.findViewById(R.id.setting_item_title)).setText(title);
        ((TextView)itemRow.findViewById(R.id.setting_item_title_sub1)).setText(subTitle1);
        ((TextView)itemRow.findViewById(R.id.setting_item_title_sub2)).setText(subTitle2);

        if (subTitleColor > 0) {
            ((TextView)itemRow.findViewById(R.id.setting_item_title_sub2)).setTextColor(getResources().getColor(subTitleColor));
        }

        if (use == true) {
            itemRow.findViewById(R.id.setting_item_arrow).setVisibility(View.INVISIBLE);
        } else {
            itemRow.findViewById(R.id.setting_item_arrow).setVisibility(View.VISIBLE);
        }
        return itemRow;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case CMSetting_Region_Index: {
                Intent nextIntent = new Intent(CMSettingMainActivity.this, CMSettingRegionActivity.class);
                startActivityForResult(nextIntent, CMSetting_Region_Index);
                break;
            }
            case CMSetting_Purchase_Auth_Index: {
                Intent nextIntent = new Intent(CMSettingMainActivity.this, CMSettingPurchaseAuthActivity.class);
                startActivityForResult(nextIntent, CMSetting_Purchase_Auth_Index);
                break;
            }
            case CMSetting_Adult_Search_Index: {
                break;
            }
            case CMSetting_Adult_Auth_Index: {
                break;
            }
            case CMSetting_Notice_Index: {
                break;
            }
            case CMSetting_Pay_Channel_Index: {
                Intent nextIntent = new Intent(CMSettingMainActivity.this, CMSettingPayChannelActivity.class);
                startActivity(nextIntent);
                break;
            }
            case CMSetting_Customer_Center_Index: {
                break;
            }
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CMSetting_Region_Index: {
                if (resultCode == Activity.RESULT_OK) {
                    // TODO: 지역 설정 성공 시 현재 화면에 대한 지역정보를 갱신한다.
                }
                break;
            }
            case CMSetting_Purchase_Auth_Index: {
                break;
            }
            case CMSetting_Adult_Search_Index: {
                break;
            }
            case CMSetting_Adult_Auth_Index: {
                break;
            }
            case CMSetting_Notice_Index: {
                break;
            }
            case CMSetting_Pay_Channel_Index: {
                break;
            }
            case CMSetting_Customer_Center_Index: {
                break;
            }
            default:
                break;
        }
    }
}
