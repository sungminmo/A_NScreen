package com.stvn.nscreen.vod;

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
import com.jjiya.android.common.JYSharedPreferences;
import com.stvn.nscreen.R;
import com.stvn.nscreen.leftmenu.LeftMenuActivity;
import com.stvn.nscreen.search.SearchMainActivity;
import com.stvn.nscreen.setting.CMSettingMainActivity;
import com.widevine.sampleplayer.SettingsActivity;

import org.json.JSONObject;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */

public class VodMainBaseFragment extends Fragment {

    //private static VodMainBaseFragment mInstance;
    private static Context             mContext;
    private        JYSharedPreferences mPref;


    // UI
    private int iMyTabNumber;
    TextView textView1, textView2, textView3, textView4, textView5;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vod_main_base, container, false);

        //mInstance = this;
        //mPref = new JYSharedPreferences(mInstance.getActivity());

        return view;
    }

    /**
     * VOD 메인의 공통 화면 UI 설정
     */
    public View initializeBaseView(View view, int iTabNumber) {

        iMyTabNumber = iTabNumber;

        textView1 = (TextView) view.findViewById(R.id.vod_fragment_topmenu_textView1);
        textView2 = (TextView) view.findViewById(R.id.vod_fragment_topmenu_textView2);
        textView3 = (TextView) view.findViewById(R.id.vod_fragment_topmenu_textView3);
        textView4 = (TextView) view.findViewById(R.id.vod_fragment_topmenu_textView4);
        textView5 = (TextView) view.findViewById(R.id.vod_fragment_topmenu_textView5);

        lineview1 = (View) view.findViewById(R.id.vod_fragment_topmenu_lineview1);
        lineview2 = (View) view.findViewById(R.id.vod_fragment_topmenu_lineview2);
        lineview3 = (View) view.findViewById(R.id.vod_fragment_topmenu_lineview3);
        lineview4 = (View) view.findViewById(R.id.vod_fragment_topmenu_lineview4);
        lineview5 = (View) view.findViewById(R.id.vod_fragment_topmenu_lineview5);

        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView1.setTypeface(null, Typeface.BOLD);
                textView2.setTypeface(null, Typeface.NORMAL);
                textView3.setTypeface(null, Typeface.NORMAL);
                textView4.setTypeface(null, Typeface.NORMAL);
                textView5.setTypeface(null, Typeface.NORMAL);
                textView1.setTextColor(getResources().getColor(R.color.white));
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
                textView1.setTypeface(null, Typeface.NORMAL);
                textView2.setTypeface(null, Typeface.BOLD);
                textView3.setTypeface(null, Typeface.NORMAL);
                textView4.setTypeface(null, Typeface.NORMAL);
                textView5.setTypeface(null, Typeface.NORMAL);
                textView1.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
                textView2.setTextColor(getResources().getColor(R.color.white));
                textView3.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
                textView4.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
                textView5.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
                lineview1.setBackgroundColor(getResources().getColor(R.color.transparent));
                lineview2.setBackgroundColor(getResources().getColor(R.color.white));
                lineview3.setBackgroundColor(getResources().getColor(R.color.transparent));
                lineview4.setBackgroundColor(getResources().getColor(R.color.transparent));
                lineview5.setBackgroundColor(getResources().getColor(R.color.transparent));

                Bundle param = new Bundle();
                param.putString("tabId", "1"); // movie
                param.putString("categoryId", Constants.CATEGORY_ID_MOVIE);
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
                textView1.setTypeface(null, Typeface.NORMAL);
                textView2.setTypeface(null, Typeface.NORMAL);
                textView3.setTypeface(null, Typeface.BOLD);
                textView4.setTypeface(null, Typeface.NORMAL);
                textView5.setTypeface(null, Typeface.NORMAL);
                textView1.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
                textView2.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
                textView3.setTextColor(getResources().getColor(R.color.white));
                textView4.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
                textView5.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
                lineview1.setBackgroundColor(getResources().getColor(R.color.transparent));
                lineview2.setBackgroundColor(getResources().getColor(R.color.transparent));
                lineview3.setBackgroundColor(getResources().getColor(R.color.white));
                lineview4.setBackgroundColor(getResources().getColor(R.color.transparent));
                lineview5.setBackgroundColor(getResources().getColor(R.color.transparent));

                Bundle param = new Bundle();
                param.putString("tabId", "2"); // ani
                param.putString("categoryId", Constants.CATEGORY_ID_ANI);
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
                textView1.setTypeface(null, Typeface.NORMAL);
                textView2.setTypeface(null, Typeface.NORMAL);
                textView3.setTypeface(null, Typeface.NORMAL);
                textView4.setTypeface(null, Typeface.BOLD);
                textView5.setTypeface(null, Typeface.NORMAL);
                textView1.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
                textView2.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
                textView3.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
                textView4.setTextColor(getResources().getColor(R.color.white));
                textView5.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
                lineview1.setBackgroundColor(getResources().getColor(R.color.transparent));
                lineview2.setBackgroundColor(getResources().getColor(R.color.transparent));
                lineview3.setBackgroundColor(getResources().getColor(R.color.transparent));
                lineview4.setBackgroundColor(getResources().getColor(R.color.white));
                lineview5.setBackgroundColor(getResources().getColor(R.color.transparent));

                Bundle param = new Bundle();
                param.putString("tabId", "3"); // tv
                param.putString("categoryId", Constants.CATEGORY_ID_TV);
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
                    AlertDialog.Builder ad = new AlertDialog.Builder(mContext);
                    ad.setTitle("알림").setMessage(getResources().getString(R.string.adult_auth_message)).setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(mContext, CMSettingMainActivity.class);
                            startActivity(intent);
                        }
                    }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {// 'No'
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = ad.create();
                    alert.show();
                } else {

                    textView1.setTypeface(null, Typeface.NORMAL);
                    textView2.setTypeface(null, Typeface.NORMAL);
                    textView3.setTypeface(null, Typeface.NORMAL);
                    textView4.setTypeface(null, Typeface.NORMAL);
                    textView5.setTypeface(null, Typeface.BOLD);
                    textView1.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
                    textView2.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
                    textView3.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
                    textView4.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
                    textView5.setTextColor(getResources().getColor(R.color.white));
                    lineview1.setBackgroundColor(getResources().getColor(R.color.transparent));
                    lineview2.setBackgroundColor(getResources().getColor(R.color.transparent));
                    lineview3.setBackgroundColor(getResources().getColor(R.color.transparent));
                    lineview4.setBackgroundColor(getResources().getColor(R.color.transparent));
                    lineview5.setBackgroundColor(getResources().getColor(R.color.white));

                    Bundle param = new Bundle();
                    param.putString("tabId", "4"); // adult
                    param.putString("categoryId", Constants.CATEGORY_ID_ADULT);
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
                Intent intent = new Intent(getActivity(), LeftMenuActivity.class);
                startActivity(intent);
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
        textView1.setTypeface(null, Typeface.NORMAL);
        textView2.setTypeface(null, Typeface.NORMAL);
        textView3.setTypeface(null, Typeface.NORMAL);
        textView4.setTypeface(null, Typeface.NORMAL);
        textView5.setTypeface(null, Typeface.NORMAL);
        textView1.setTextColor(getResources().getColor(R.color.violet_topmenu_unselected));
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
                textView1.setTypeface(null, Typeface.BOLD);
                textView1.setTextColor(getResources().getColor(R.color.white));
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
}