package com.example.chevbook.Fragments.FragmentAboutTabs;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chevbook.R;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 *
 */
public class FragmentAboutLicences extends Fragment {


    public FragmentAboutLicences() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about_licences, container, false);
    }


}