package com.weather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.weather.android.fragment.WeatherFragment;

public class WeatherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        Fragment fragment = Fragment.instantiate(this, WeatherFragment.class.getName());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_weather, fragment, null).commit();
    }
}
