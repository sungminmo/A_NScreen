package com.stvn.nscreen.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stvn.nscreen.R;
import com.stvn.nscreen.common.CMActionBar;
import com.stvn.nscreen.common.CMBaseActivity;

/**
 * 설정 화면
 * Created by kimwoodam on 2015. 9. 19..
 * TODO:메뉴 클릭 이벤트 및 메뉴별 화면 작업, 이미지 및 레이아웃 관련 전체적 수정 작업 필요 15.09.23 kimwoodam
 */
public class CMSettingMainActivity extends CMBaseActivity implements View.OnClickListener {

    private final int CMSetting_Region_Tag = 100;
    private final int CMSetting_Purchase_Auth_Tag = 101;
    private final int CMSetting_Adult_Search_Tag = 102;
    private final int CMSetting_Adult_Auth_Tag = 103;
    private final int CMSetting_Notice_Tag = 104;
    private final int CMSetting_Pay_Channel_Tag = 105;
    private final int CMSetting_Customer_Center_Tag = 106;


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

        String subMessage = "";
        LinearLayout itemRow;
        ViewGroup mainView = (ViewGroup) findViewById(R.id.setting_layout);;

        itemRow = makeSettingItem(R.id.setting_region_Index, true, "지역설정", "현재설정지역 : ", "강동구", R.color.text_area_color, false);
        mainView.addView(itemRow);

        itemRow = makeSettingItem(R.id.setting_purchase_auth_Index, true, "구매인증 비밀번호 관리", "", "", 0, false);
        mainView.addView(itemRow);

        itemRow = makeSettingToggleItem(R.id.setting_adult_search_Index, true, "성인검색 제한설정", CMSettingData.getInstance().getAdultSearchRestriction(this));
        mainView.addView(itemRow);

        if (CMSettingData.getInstance().isAdultAuth(this)) {
            subMessage = "";
        } else {
            subMessage = "성인인증이 필요합니다.";
        }

        itemRow = makeSettingItem(R.id.setting_adult_auth_Index, true, "성인인증", "", subMessage, R.color.red, false);
        mainView.addView(itemRow);
        itemRow = makeSettingItem(R.id.setting_notice_Index, false, "공지사항", "", "", 0, false);
        mainView.addView(itemRow);
        itemRow = makeSettingItem(R.id.setting_pay_channel_Index, false, "유료채널 안내", "", "", 0, false);
        mainView.addView(itemRow);
        itemRow = makeSettingItem(R.id.setting_customer_center_Index, false, "고객센터 안내", "", "", 0, false);
        mainView.addView(itemRow);

    }

    /**
     * 설정 메뉴 아이템을 생성 한다.
     * */
    private LinearLayout makeSettingItem(int itemIndex, boolean useImage, String title, String subTitle1, String subTitle2, int subTitleColor, boolean useArrow) {
        LinearLayout itemRow = (LinearLayout)getLayoutInflater().inflate(R.layout.custom_setting_item, null);

        itemRow.setId(itemIndex);
        itemRow.setOnClickListener(this);

        itemRow.findViewById(R.id.setting_item_title_sub_layout).setVisibility(View.VISIBLE);

        if (useImage == true) {
            itemRow.findViewById(R.id.setting_item_image).setVisibility(View.VISIBLE);
            itemRow.findViewById(R.id.setting_item_image).setOnClickListener(this);
            itemRow.findViewById(R.id.setting_item_image).setTag(itemIndex);
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

        if (useArrow == true) {
            itemRow.findViewById(R.id.setting_item_arrow).setVisibility(View.INVISIBLE);
        } else {
            itemRow.findViewById(R.id.setting_item_arrow).setVisibility(View.VISIBLE);
        }
        return itemRow;
    }

    /**
     * 설정 메뉴 아이템을 생성 한다. (토글버튼 유형)
     * */
    private LinearLayout makeSettingToggleItem(int itemIndex, boolean useImage, String title, boolean isToggleOn) {
        LinearLayout itemRow = (LinearLayout)getLayoutInflater().inflate(R.layout.custom_setting_item, null);

        itemRow.setId(itemIndex);
        itemRow.findViewById(R.id.setting_item_toggle_button).setVisibility(View.VISIBLE);
        itemRow.setBackgroundResource(R.drawable.setting_item_background);

        if (useImage == true) {
            itemRow.findViewById(R.id.setting_item_image).setVisibility(View.VISIBLE);
            itemRow.findViewById(R.id.setting_item_image).setOnClickListener(this);
            itemRow.findViewById(R.id.setting_item_image).setTag(itemIndex);
        } else {
            itemRow.findViewById(R.id.setting_item_image).setVisibility(View.INVISIBLE);
        }

        if (TextUtils.isEmpty(title)) {
            title = "";
        }

        ((TextView)itemRow.findViewById(R.id.setting_item_title)).setText(title);

        // TODO: 가이드 완료 시 디자인 적용 필요 15.10.09
        Button toggleButton = (Button)itemRow.findViewById(R.id.setting_item_toggle_button);
        toggleButton.setTag(itemIndex);
        toggleButton.setOnClickListener(this);
        if (isToggleOn) {
            toggleButton.setText("설정");
        } else {
            toggleButton.setText("미설정");
        }

        itemRow.findViewById(R.id.setting_item_arrow).setVisibility(View.INVISIBLE);
        return itemRow;
    }

    /**
     * 지역명 설정
     * */
    private void setAreaName(String strArea) {
        View itemView = findViewById(R.id.setting_region_Index);
        TextView areaText = (TextView)itemView.findViewById(R.id.setting_item_title_sub2);
        areaText.setText(strArea);
    }

    /**
     * 성인검색제한설정 값 변
     * */
    private void setAdultSearchRestriction(boolean isToggleOn) {
        View itemView = findViewById(R.id.setting_adult_search_Index);
        Button toggleButton = (Button)itemView.findViewById(R.id.setting_item_toggle_button);
        if (isToggleOn) {
            toggleButton.setText("설정");
        } else {
            toggleButton.setText("미설정");
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.setting_region_Index: {
                Intent nextIntent = new Intent(CMSettingMainActivity.this, CMSettingRegionActivity.class);
                startActivityForResult(nextIntent, CMSetting_Region_Tag);
                break;
            }
            case R.id.setting_purchase_auth_Index: {
                Intent nextIntent = new Intent(CMSettingMainActivity.this, CMSettingPurchaseAuthActivity.class);
                startActivityForResult(nextIntent, CMSetting_Purchase_Auth_Tag);
                break;
            }
            case R.id.setting_adult_search_Index: {
                break;
            }
            case R.id.setting_adult_auth_Index: {
                break;
            }
            case R.id.setting_notice_Index: {
                break;
            }
            case R.id.setting_pay_channel_Index: {
                Intent nextIntent = new Intent(CMSettingMainActivity.this, CMSettingPayChannelActivity.class);
                startActivity(nextIntent);
                break;
            }
            case R.id.setting_customer_center_Index: {
                break;
            }
            case R.id.setting_item_image: {
                int tag = (int)v.getTag();
                String title = null;
                String message = null;
                if (tag == R.id.setting_region_Index) {
                    title = getString(R.string.setting_help_area_title);
                    message = getString(R.string.setting_help_area_message);
                } else if (tag == R.id.setting_purchase_auth_Index) {
                    title = getString(R.string.setting_help_purchase_auth_title);
                    message = getString(R.string.setting_help_purchase_auth_message);
                } else if (tag == R.id.setting_adult_search_Index) {
                    title = getString(R.string.setting_help_adult_search_title);
                    message = getString(R.string.setting_help_adult_search_message);
                } else if (tag == R.id.setting_adult_auth_Index) {
                    title = getString(R.string.setting_help_adult_auth_title);
                    message = getString(R.string.setting_help_adult_auth_message);
                }

                if (TextUtils.isEmpty(title) == false && TextUtils.isEmpty(message) == false) {
                    Toast.makeText(this, title+"\n\n"+message, Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.setting_item_toggle_button: {
                int tag = (int)v.getTag();
                if (tag == R.id.setting_adult_search_Index) {
                    // TODO: 본인인증(성인인증) 관련 페이지 적용 처리 필요 15.10.09
                    boolean isToggleOn = !CMSettingData.getInstance().getAdultSearchRestriction(this);
                    CMSettingData.getInstance().setAdultSearchRestriction(this, isToggleOn);
                    setAdultSearchRestriction(isToggleOn);
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CMSetting_Region_Tag: {
                if (resultCode == Activity.RESULT_OK) {
                    // TODO: 지역 설정 성공 시 현재 화면에 대한 지역정보를 갱신한다.
                }
                break;
            }
            case CMSetting_Purchase_Auth_Tag: {
                break;
            }
            case CMSetting_Adult_Search_Tag: {
                break;
            }
            case CMSetting_Adult_Auth_Tag: {
                boolean isAuth = !CMSettingData.getInstance().isAdultAuth(this);
                CMSettingData.getInstance().setAdultAuth(this, isAuth);
                break;
            }
            case CMSetting_Notice_Tag: {
                break;
            }
            case CMSetting_Pay_Channel_Tag: {
                break;
            }
            case CMSetting_Customer_Center_Tag: {
                break;
            }
            default:
                break;
        }
    }
}
