package com.example.fragment;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    FragmentB fragmentB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentA fragmentA = new FragmentA();
        fragmentB = new FragmentB();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentA, fragmentA)
                .replace(R.id.fragmentB, fragmentB)
                .commit();
    }

    public void increaseCount(){

        fragmentB.increaseCount();

    }
}