package com.stvn.nscreen.vod;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.stvn.nscreen.R;
import com.stvn.nscreen.leftmenu.LeftMenuActivity;
import com.stvn.nscreen.search.SearchMainActivity;

import org.json.JSONObject;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */

public class VodMainBaseFragment extends Fragment {

    //
    public List<JSONObject> categorys;

    // UI
    TextView textView1, textView2, textView3, textView4, textView5;
    View lineview1, lineview2, lineview3, lineview4, lineview5;
    ImageButton leftImageButton, rightImageButton;

    public VodMainBaseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vod_main_base, container, false);

        return view;
    }

    /**
     * VOD 메인의 공통 화면 UI 설정
     */
    public View initializeBaseView(View view) {

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
                param.putString("tabId", "movie");
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
                param.putString("tabId", "movie");
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                VodMainOtherTabFragment df = new VodMainOtherTabFragment();
                df.setArguments(param);
                ft.replace(R.id.fragment_placeholder, df);
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
                param.putString("tabId", "ani");
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                VodMainOtherTabFragment df = new VodMainOtherTabFragment();
                df.setArguments(param);
                ft.replace(R.id.fragment_placeholder, df);
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
                param.putString("tabId", "tv");
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                VodMainOtherTabFragment df = new VodMainOtherTabFragment();
                df.setArguments(param);
                ft.replace(R.id.fragment_placeholder, df);
                ft.addToBackStack("VodMainOtherTabFragment");
                ft.commit();
            }
        });
        textView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                param.putString("tabId", "adult");
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                VodMainOtherTabFragment df = new VodMainOtherTabFragment();
                df.setArguments(param);
                ft.replace(R.id.fragment_placeholder, df);
                ft.addToBackStack("VodMainOtherTabFragment");
                ft.commit();
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

        return view;
    }
}