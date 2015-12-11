package com.xxk.photogalllery;

import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;

/**
 * Created by xxk on 15/11/16.
 */
public abstract class Activity_SingleFragment extends AppCompatActivity {
    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        FragmentManager fm=getSupportFragmentManager();
        Fragment fragment=fm.findFragmentById(R.id.fragmentContainer);
        if(fragment==null){
            fragment=createFragment();
            fm.beginTransaction().add(R.id.fragmentContainer,fragment).commit();
        }

//        try {
//            Log.d("mCriminalinten", "to read");
////            Test.get(this).readTxt("sh603979.txt");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}
