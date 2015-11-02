package com.stvn.nscreen.vod;


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

/**
 * A simple {@link Fragment} subclass.
 */
public class VodFirstTabFragment extends Fragment {

    TextView textView1, textView2, textView3, textView4, textView5;
    View     lineview1, lineview2, lineview3, lineview4, lineview5;
    ImageButton leftImageButton, rightImageButton;


    public VodFirstTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vod_first_tab, container, false);

        textView1 = (TextView)view.findViewById(R.id.vod_fragment_topmenu_textView1);
        textView2 = (TextView)view.findViewById(R.id.vod_fragment_topmenu_textView2);
        textView3 = (TextView)view.findViewById(R.id.vod_fragment_topmenu_textView3);
        textView4 = (TextView)view.findViewById(R.id.vod_fragment_topmenu_textView4);
        textView5 = (TextView)view.findViewById(R.id.vod_fragment_topmenu_textView5);

        lineview1 = (View)view.findViewById(R.id.vod_fragment_topmenu_lineview1);
        lineview2 = (View)view.findViewById(R.id.vod_fragment_topmenu_lineview2);
        lineview3 = (View)view.findViewById(R.id.vod_fragment_topmenu_lineview3);
        lineview4 = (View)view.findViewById(R.id.vod_fragment_topmenu_lineview4);
        lineview5 = (View)view.findViewById(R.id.vod_fragment_topmenu_lineview5);

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
            }
        });

        leftImageButton = (ImageButton)view.findViewById(R.id.vod_fragment_topmenu_left_imagebutton);
        leftImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LeftMenuActivity.class);
                startActivity(intent);
            }
        });

        rightImageButton = (ImageButton)view.findViewById(R.id.vod_fragment_topmenu_right_imagebutton);
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
