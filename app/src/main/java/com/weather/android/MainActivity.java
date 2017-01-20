package com.weather.android;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.weather.android.fragment.ChooseAreaFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment fragment = Fragment.instantiate(this, ChooseAreaFragment.class.getName());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment, null).commit();
    }
}
