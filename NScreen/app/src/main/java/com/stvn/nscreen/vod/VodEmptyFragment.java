package com.stvn.nscreen.vod;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stvn.nscreen.R;

/**
 * A simple {@link Fragment} subclass.
 */

public class VodEmptyFragment extends Fragment {


    public VodEmptyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vod_empty, container, false);



        return view;
    }


}
