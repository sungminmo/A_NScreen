package com.stvn.nscreen.setting;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.stvn.nscreen.R;
import com.stvn.nscreen.common.CMActionBar;
import com.stvn.nscreen.common.CMBaseActivity;
import com.stvn.nscreen.setting.notice.CMSettingNoticeActivity;
import com.stvn.nscreen.util.CMAlertUtil;

/**
 * 설정 화면
 * Created by kimwoodam on 2015. 9. 19..
 * TODO:메뉴 클릭 이벤트 및 메뉴별 화면 작업, 이미지 및 레이아웃 관련 전체적 수정 작업 필요 15.09.23 kimwoodam
 */

public class CMSettingMainActivity extends CMBaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

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
        ViewGroup mainView = (ViewGroup) findViewById(R.id.setting_layout);

        String areaName = CMSettingData.getInstance().getUserAreaName(CMSettingMainActivity.this);
        itemRow = makeSettingItem(R.id.setting_region_Index, true, "지역설정", "현재설정지역 : ", areaName, R.color.text_area_color, false);
        mainView.addView(itemRow);

        itemRow = makeSettingItem(R.id.setting_purchase_auth_Index, true, "구매인증 비밀번호 관리", "", "", 0, false);
        mainView.addView(itemRow);

        itemRow = makeSettingToggleItem(R.id.setting_adult_search_Index, true, "성인검색 제한설정", CMSettingData.getInstance().getAdultSearchRestriction(this));
        mainView.addView(itemRow);
        itemListUseEvent(R.id.setting_adult_search_Index, false);

        boolean isAuthed = CMSettingData.getInstance().isAdultAuth(CMSettingMainActivity.this);
        subMessage = getAdultAuthString(isAuthed);

        itemRow = makeSettingItem(R.id.setting_adult_auth_Index, true, "성인인증", "", subMessage, R.color.red, false);
        mainView.addView(itemRow);
        itemListUseEvent(R.id.setting_adult_auth_Index, !isAuthed);

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
        itemRow.findViewById(R.id.setting_item_switch_button).setVisibility(View.VISIBLE);

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

        Switch switchButton = (Switch)itemRow.findViewById(R.id.setting_item_switch_button);
        switchButton.setTag(itemIndex);
        switchButton.setOnCheckedChangeListener(this);
        switchButton.setChecked(isToggleOn);

        itemRow.findViewById(R.id.setting_item_arrow).setVisibility(View.INVISIBLE);
        return itemRow;
    }

    /**
     * 리스트 아이템 클릭 처리
     * */
    private void itemListUseEvent(int itemId, boolean useEvent) {
        View itemRow = findViewById(itemId);
        if (useEvent == true) {
            itemRow.setOnClickListener(this);
            itemRow.setBackgroundResource(R.drawable.setting_item_selector);
        } else {
            itemRow.setOnClickListener(null);
            itemRow.setBackgroundResource(R.drawable.setting_item_background);
        }
    }

    /**
     * 성인인증 여부에 따른 화면 표출 문구 반환
     * */
    private String getAdultAuthString(boolean isAuthed) {
        if (isAuthed) {
            return "성인인증 되셨습니다.";
        } else {
            return "성인인증이 필요합니다.";
        }
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
     * 성인검색제한설정 값 변경
     * */
    private void setAdultSearchRestriction(boolean isToggleOn) {
        View itemView = findViewById(R.id.setting_adult_search_Index);
        Switch switchButton = (Switch)itemView.findViewById(R.id.setting_item_switch_button);
        switchButton.setChecked(isToggleOn);
    }

    /**
     * 성인인증값 변경
     * */
    private void setAdultAuth(boolean isAuthed) {
        View itemView = findViewById(R.id.setting_adult_auth_Index);
        itemListUseEvent(R.id.setting_adult_auth_Index, !isAuthed);

        TextView authText = (TextView)itemView.findViewById(R.id.setting_item_title_sub2);
        authText.setText(getAdultAuthString(isAuthed));
    }

    @Override
    public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.setting_item_switch_button: {
                int tag = (int)buttonView.getTag();
                if (tag == R.id.setting_adult_search_Index) {
                    if (isChecked == false) {
                        if (CMSettingData.getInstance().isAdultAuth(CMSettingMainActivity.this) == false) {
                            buttonView.setChecked(true);
                            CMAlertUtil.Alert(CMSettingMainActivity.this,
                                    "성인인증 필요",
                                    "성인검색 제한 설정을 해제하기 위해서는 성인인증이 필요합니다.", "성인인증을 진행하시겠습니까?", "예", "아니오", false, true,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent nextIntent = new Intent(CMSettingMainActivity.this, CMSettingAdultAuthActivity.class);
                                            startActivityForResult(nextIntent, CMSetting_Adult_Search_Tag);
                                        }
                                    }, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {}
                                    });
                        } else {
                            CMSettingData.getInstance().setAdultSearchRestriction(CMSettingMainActivity.this, isChecked);
                        }
                    } else {
                        CMSettingData.getInstance().setAdultSearchRestriction(CMSettingMainActivity.this, isChecked);
                    }
                }
                break;
            }
            default:
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
                CMAlertUtil.Alert(this, "구매인증 비밀번호 입력", "구매인증 비밀번호 입력해주세요.", "인증번호가 기억나지 않으실 경우,\n셋탑박스를 다시 등록해주세요.", "확인", "취소", true, false, true, new CMAlertUtil.InputDialogClickListener() {
                    @Override
                    public void positiveClickEvent(DialogInterface dialog, String text) {

                        String savedPassword = CMSettingData.getInstance().getPurchaseAuthPassword(CMSettingMainActivity.this);
                        if (TextUtils.isEmpty(text)) {
                            CMAlertUtil.Alert(CMSettingMainActivity.this, "구매인증 비밀번호 입력", "인증번호를 입력하세요.");
                        } else if (TextUtils.isEmpty(savedPassword)) {
                            CMAlertUtil.Alert(CMSettingMainActivity.this, "구매인증 비밀번호 입력", "등록된 구매 인증번호가 없습니다.");
                        } else {
                            if (text.equals(savedPassword)) {
                                Intent nextIntent = new Intent(CMSettingMainActivity.this, CMSettingPurchaseAuthActivity.class);
                                startActivityForResult(nextIntent, CMSetting_Purchase_Auth_Tag);
                            } else {
                                CMAlertUtil.Alert(CMSettingMainActivity.this, "구매인증 비밀번호 입력", "구매인증번호가 일치하지 않습니다.");
                            }
                        }
                    }
                    @Override
                    public void negativeClickEvent(DialogInterface dialog) {
                    }
                });
                break;
            }
            case R.id.setting_adult_auth_Index: {
                Intent nextIntent = new Intent(CMSettingMainActivity.this, CMSettingAdultAuthActivity.class);
                startActivityForResult(nextIntent, CMSetting_Adult_Auth_Tag);
                break;
            }
            case R.id.setting_notice_Index: {
                Intent nextIntent = new Intent(CMSettingMainActivity.this, CMSettingNoticeActivity.class);
                startActivity(nextIntent);
                break;
            }
            case R.id.setting_pay_channel_Index: {
                Intent nextIntent = new Intent(CMSettingMainActivity.this, CMSettingPayChannelActivity.class);
                startActivity(nextIntent);
                break;
            }
            case R.id.setting_customer_center_Index: {
                Intent nextIntent = new Intent(CMSettingMainActivity.this, CMSettingCustomerCenterActivity.class);
                nextIntent.putExtra("GUIDE_ID", "2");
                startActivity(nextIntent);
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
                    CMAlertUtil.Alert(this, title, message);
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
                    String areaName = CMSettingData.getInstance().getUserAreaName(CMSettingMainActivity.this);
                    setAreaName(areaName);
                }
                break;
            }
            case CMSetting_Purchase_Auth_Tag: {
                break;
            }
            case CMSetting_Adult_Search_Tag: {
                if (resultCode == Activity.RESULT_OK) {
                    boolean isAuth = CMSettingData.getInstance().isAdultAuth(this);
                    if (isAuth == true) {
                        CMSettingData.getInstance().setAdultSearchRestriction(CMSettingMainActivity.this, false);
                        setAdultSearchRestriction(false);
                        setAdultAuth(isAuth);
                    }
                }
                break;
            }
            case CMSetting_Adult_Auth_Tag: {
                if (resultCode == Activity.RESULT_OK) {
                    boolean isAuth = CMSettingData.getInstance().isAdultAuth(CMSettingMainActivity.this);
                    setAdultAuth(isAuth);
                }
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
