package com.example.fragment;

import android.os.Bundle;
import android.view.*;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class FragmentA extends Fragment {

    public FragmentA(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_a, container, false);

        Button btn = view.findViewById(R.id.btnClick);

        btn.setOnClickListener(v -> {

            MainActivity activity = (MainActivity) getActivity();
            activity.increaseCount();

        });

        return view;
    }
}