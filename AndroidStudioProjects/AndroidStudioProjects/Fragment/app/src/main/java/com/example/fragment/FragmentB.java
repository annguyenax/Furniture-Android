package com.example.fragment;

import android.os.Bundle;
import android.view.*;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class FragmentB extends Fragment {

    TextView txtCount;
    int count = 0;

    public FragmentB(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_b, container, false);

        txtCount = view.findViewById(R.id.txtCount);

        return view;
    }

    public void increaseCount(){

        count++;
        txtCount.setText("Count = " + count);

    }
}