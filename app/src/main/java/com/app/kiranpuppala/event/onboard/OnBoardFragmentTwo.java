package com.app.kiranpuppala.event.onboard;

/**
 * Created by kiran.puppala on 4/4/18.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.kiranpuppala.event.R;

public class OnBoardFragmentTwo extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.onboard_two, container, false);

        return rootView;
    }
}