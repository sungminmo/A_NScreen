package com.stvn.nscreen.vod;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jjiya.android.common.Constants;
import com.jjiya.android.common.IOnBackPressedListener;
import com.jjiya.android.common.JYSharedPreferences;
import com.stvn.nscreen.MainActivity;
import com.stvn.nscreen.R;
import com.stvn.nscreen.leftmenu.LeftMenuActivity;
import com.stvn.nscreen.search.SearchMainActivity;
import com.stvn.nscreen.setting.CMSettingMainActivity;
import com.stvn.nscreen.util.CMAlertUtil;

/**
 * A simple {@link Fragment} subclass.
 */

public class VodMainBaseFragment extends Fragment implements IOnBackPressedListener {

    //private static VodMainBaseFragment mInstance;
    private static Context             mContext;
    private        JYSharedPreferences mPref;

    private boolean isLeftMenuShow = false;

    // UI
    private int iMyTabNumber;
    TextView mTab1TextView, textView2, textView3, textView4, textView5;
    View lineview1, lineview2, lineview3, lineview4, lineview5;
    ImageButton leftImageButton, rightImageButton;

    public VodMainBaseFragment() {
        // Required empty public constructor
        iMyTabNumber = 0;
    }

    public void setMyContext(Context c){
        this.mContext = c;
        mPref = new JYSharedPreferences(c);
    }

    public int getiMyTabNumber() {
        return iMyTabNumber;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vod_main_base, container, false);

        //mInstance = this;
        //mPref = new JYSharedPreferences(mInstance.getActivity());
        this.isLeftMenuShow = false;
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity)activity).setOnBackPressedListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.isLeftMenuShow = false;
    }

    /**
     * VOD 메인의 공통 화면 UI 설정
     */
    public View initializeBaseView(View view, int iTabNumber) {

        iMyTabNumber = iTabNumber;

        mTab1TextView = (TextView) view.findViewById(R.id.vod_fragment_topmenu_textView1);
        textView2 = (TextView) view.findViewById(R.id.vod_fragment_topmenu_textView2);
        textView3 = (TextView) view.findViewById(R.id.vod_fragment_topmenu_textView3);
        textView4 = (TextView) view.findViewById(R.id.vod_fragment_topmenu_textView4);
        textView5 = (TextView) view.findViewById(R.id.vod_fragment_topmenu_textView5);

        lineview1 = (View) view.findViewById(R.id.vod_fragment_topmenu_lineview1);
        lineview2 = (View) view.findViewById(R.id.vod_fragment_topmenu_lineview2);
        lineview3 = (View) view.findViewById(R.id.vod_fragment_topmenu_lineview3);
        lineview4 = (View) view.findViewById(R.id.vod_fragment_topmenu_lineview4);
        lineview5 = (View) view.findViewById(R.id.vod_fragment_topmenu_lineview5);

        mTab1TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mTab1TextView.setTypeface(null, Typeface.BOLD);
                textView2.setTypeface(null, Typeface.NORMAL);
                textView3.setTypeface(null, Typeface.NORMAL);
                textView4.setTypeface(null, Typeface.NORMAL);
                textView5.setTypeface(null, Typeface.NORMAL);
                mTab1TextView.setTextColor(getResources().getColor(R.color.white));
                textView2.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
                textView3.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
                textView4.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
                textView5.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
                lineview1.setBackgroundColor(getResources().getColor(R.color.white));
                lineview2.setBackgroundColor(getResources().getColor(R.color.transparent));
                lineview3.setBackgroundColor(getResources().getColor(R.color.transparent));
                lineview4.setBackgroundColor(getResources().getColor(R.color.transparent));
                lineview5.setBackgroundColor(getResources().getColor(R.color.transparent));

                Bundle param = new Bundle();
                param.putString("tabId", "0");
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                VodMainFirstTabFragment df = new VodMainFirstTabFragment();
                df.setArguments(param);
                ft.replace(R.id.fragment_placeholder, df);
                ft.addToBackStack("VodMainFirstTabFragment");
                ft.commit();
            }
        });

        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTab1TextView.setTypeface(null, Typeface.NORMAL);
                textView2.setTypeface(null, Typeface.BOLD);
                textView3.setTypeface(null, Typeface.NORMAL);
                textView4.setTypeface(null, Typeface.NORMAL);
                textView5.setTypeface(null, Typeface.NORMAL);
                mTab1TextView.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
                textView2.setTextColor(getResources().getColor(R.color.white));
                textView3.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
                textView4.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
                textView5.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
                lineview1.setBackgroundColor(getResources().getColor(R.color.transparent));
                lineview2.setBackgroundColor(getResources().getColor(R.color.white));
                lineview3.setBackgroundColor(getResources().getColor(R.color.transparent));
                lineview4.setBackgroundColor(getResources().getColor(R.color.transparent));
                lineview5.setBackgroundColor(getResources().getColor(R.color.transparent));

                String categoryId = mPref.getValue(Constants.CATEGORY_ID_TAB2, "");
                String categoryIdFromVodMain = mPref.getValue(JYSharedPreferences.VOD_OTHER_TAB_CATEGORY_ID, "");
                if (categoryIdFromVodMain.length() > 0) {
                    mPref.put(JYSharedPreferences.VOD_OTHER_TAB_CATEGORY_ID, "");
                    categoryId = categoryIdFromVodMain;
                }
                Bundle param = new Bundle();
                param.putString("tabId", "1"); // movie
                param.putString("categoryId", categoryId);
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                VodMainOtherTabFragment otherTabFragment = new VodMainOtherTabFragment();
                otherTabFragment.setArguments(param);
                ft.replace(R.id.fragment_placeholder, otherTabFragment);
                ft.addToBackStack("VodMainOtherTabFragment");
                ft.commit();
            }
        });
        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTab1TextView.setTypeface(null, Typeface.NORMAL);
                textView2.setTypeface(null, Typeface.NORMAL);
                textView3.setTypeface(null, Typeface.BOLD);
                textView4.setTypeface(null, Typeface.NORMAL);
                textView5.setTypeface(null, Typeface.NORMAL);
                mTab1TextView.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
                textView2.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
                textView3.setTextColor(getResources().getColor(R.color.white));
                textView4.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
                textView5.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
                lineview1.setBackgroundColor(getResources().getColor(R.color.transparent));
                lineview2.setBackgroundColor(getResources().getColor(R.color.transparent));
                lineview3.setBackgroundColor(getResources().getColor(R.color.white));
                lineview4.setBackgroundColor(getResources().getColor(R.color.transparent));
                lineview5.setBackgroundColor(getResources().getColor(R.color.transparent));

                String categoryId = mPref.getValue(Constants.CATEGORY_ID_TAB3, "");
                String categoryIdFromVodMain = mPref.getValue(JYSharedPreferences.VOD_OTHER_TAB_CATEGORY_ID, "");
                if (categoryIdFromVodMain.length() > 0) {
                    mPref.put(JYSharedPreferences.VOD_OTHER_TAB_CATEGORY_ID, "");
                    categoryId = categoryIdFromVodMain;
                }
                Bundle param = new Bundle();
                param.putString("tabId", "2"); // ani
                param.putString("categoryId", categoryId);
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                VodMainOtherTabFragment otherTabFragment = new VodMainOtherTabFragment();
                otherTabFragment.setArguments(param);
                ft.replace(R.id.fragment_placeholder, otherTabFragment);
                ft.addToBackStack("VodMainOtherTabFragment");
                ft.commit();
            }
        });
        textView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTab1TextView.setTypeface(null, Typeface.NORMAL);
                textView2.setTypeface(null, Typeface.NORMAL);
                textView3.setTypeface(null, Typeface.NORMAL);
                textView4.setTypeface(null, Typeface.BOLD);
                textView5.setTypeface(null, Typeface.NORMAL);
                mTab1TextView.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
                textView2.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
                textView3.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
                textView4.setTextColor(getResources().getColor(R.color.white));
                textView5.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
                lineview1.setBackgroundColor(getResources().getColor(R.color.transparent));
                lineview2.setBackgroundColor(getResources().getColor(R.color.transparent));
                lineview3.setBackgroundColor(getResources().getColor(R.color.transparent));
                lineview4.setBackgroundColor(getResources().getColor(R.color.white));
                lineview5.setBackgroundColor(getResources().getColor(R.color.transparent));

                String categoryId = mPref.getValue(Constants.CATEGORY_ID_TAB4, "");
                String categoryIdFromVodMain = mPref.getValue(JYSharedPreferences.VOD_OTHER_TAB_CATEGORY_ID, "");
                if (categoryIdFromVodMain.length() > 0) {
                    mPref.put(JYSharedPreferences.VOD_OTHER_TAB_CATEGORY_ID, "");
                    categoryId = categoryIdFromVodMain;
                }
                Bundle param = new Bundle();
                param.putString("tabId", "3"); // tv
                param.putString("categoryId", categoryId);
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                VodMainOtherTabFragment otherTabFragment = new VodMainOtherTabFragment();
                otherTabFragment.setArguments(param);
                ft.replace(R.id.fragment_placeholder, otherTabFragment);
                ft.addToBackStack("VodMainOtherTabFragment");
                ft.commit();
            }
        });
        textView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mPref.isAdultVerification() == false) {
                    String alertTitle = "성인인증 필요";
                    String alertMsg1 = getString(R.string.error_not_adult1);
                    String alertMsg2 = getString(R.string.error_not_adult2);
                    CMAlertUtil.Alert1(getActivity(), alertTitle, alertMsg1, alertMsg2, false, true, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(mContext, CMSettingMainActivity.class);
                            startActivity(intent);
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                } else {

                    mTab1TextView.setTypeface(null, Typeface.NORMAL);
                    textView2.setTypeface(null, Typeface.NORMAL);
                    textView3.setTypeface(null, Typeface.NORMAL);
                    textView4.setTypeface(null, Typeface.NORMAL);
                    textView5.setTypeface(null, Typeface.BOLD);
                    mTab1TextView.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
                    textView2.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
                    textView3.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
                    textView4.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
                    textView5.setTextColor(getResources().getColor(R.color.white));
                    lineview1.setBackgroundColor(getResources().getColor(R.color.transparent));
                    lineview2.setBackgroundColor(getResources().getColor(R.color.transparent));
                    lineview3.setBackgroundColor(getResources().getColor(R.color.transparent));
                    lineview4.setBackgroundColor(getResources().getColor(R.color.transparent));
                    lineview5.setBackgroundColor(getResources().getColor(R.color.white));

                    String categoryId = mPref.getValue(Constants.CATEGORY_ID_TAB5, "");
                    String categoryIdFromVodMain = mPref.getValue(JYSharedPreferences.VOD_OTHER_TAB_CATEGORY_ID, "");
                    if ( categoryIdFromVodMain.length() > 0 ) {
                        mPref.put(JYSharedPreferences.VOD_OTHER_TAB_CATEGORY_ID, "");
                        categoryId = categoryIdFromVodMain;
                    }
                    Bundle param = new Bundle();
                    param.putString("tabId", "4"); // adult
                    param.putString("categoryId", categoryId);
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    VodMainOtherTabFragment otherTabFragment = new VodMainOtherTabFragment();
                    otherTabFragment.setArguments(param);
                    ft.replace(R.id.fragment_placeholder, otherTabFragment);
                    ft.addToBackStack("VodMainOtherTabFragment");
                    ft.commit();
                }
            }
        });

        leftImageButton = (ImageButton) view.findViewById(R.id.vod_fragment_topmenu_left_imagebutton);
        leftImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLeftMenuShow == false) {
                    isLeftMenuShow = true;
                    Intent intent = new Intent(getActivity(), LeftMenuActivity.class);
                    startActivity(intent);
                }
            }
        });

        rightImageButton = (ImageButton) view.findViewById(R.id.vod_fragment_topmenu_right_imagebutton);
        rightImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchMainActivity.class);
                startActivity(intent);
            }
        });

        changeTabUI();

        return view;
    }

    private void changeTabUI() {
        mTab1TextView.setTypeface(null, Typeface.NORMAL);
        textView2.setTypeface(null, Typeface.NORMAL);
        textView3.setTypeface(null, Typeface.NORMAL);
        textView4.setTypeface(null, Typeface.NORMAL);
        textView5.setTypeface(null, Typeface.NORMAL);
        mTab1TextView.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
        textView2.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
        textView3.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
        textView4.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
        textView5.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
        lineview1.setBackgroundColor(getResources().getColor(R.color.transparent));
        lineview2.setBackgroundColor(getResources().getColor(R.color.transparent));
        lineview3.setBackgroundColor(getResources().getColor(R.color.transparent));
        lineview4.setBackgroundColor(getResources().getColor(R.color.transparent));
        lineview5.setBackgroundColor(getResources().getColor(R.color.transparent));

        switch ( iMyTabNumber ) {
            case 0: {
                mTab1TextView.setTypeface(null, Typeface.BOLD);
                mTab1TextView.setTextColor(getResources().getColor(R.color.white));
                lineview1.setBackgroundColor(getResources().getColor(R.color.white));
            } break;
            case 1: {
                textView2.setTypeface(null, Typeface.BOLD);
                textView2.setTextColor(getResources().getColor(R.color.white));
                lineview2.setBackgroundColor(getResources().getColor(R.color.white));
            } break;
            case 2: {
                textView3.setTypeface(null, Typeface.BOLD);
                textView3.setTextColor(getResources().getColor(R.color.white));
                lineview3.setBackgroundColor(getResources().getColor(R.color.white));
            } break;
            case 3: {
                textView4.setTypeface(null, Typeface.BOLD);
                textView4.setTextColor(getResources().getColor(R.color.white));
                lineview4.setBackgroundColor(getResources().getColor(R.color.white));
            } break;
            case 4: {
                textView5.setTypeface(null, Typeface.BOLD);
                textView5.setTextColor(getResources().getColor(R.color.white));
                lineview5.setBackgroundColor(getResources().getColor(R.color.white));
            } break;
        }
    }


    @Override
    public void onBackPressedCallback() { }
}