package com.weather.android.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.weather.android.R;
import com.weather.android.gson.DailyForecast;
import com.weather.android.gson.Weather;
import com.weather.android.service.AutoUpdateService;
import com.weather.android.util.HttpUtil;
import com.weather.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by zhangjiuchu on 2017/1/23.
 */

public class WeatherFragment extends Fragment {

    public DrawerLayout drawerLayout;
    public SwipeRefreshLayout swipeRefresh;
    private Button nvButton;
    private ImageView bingPicture;
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView nowDegree;
    private TextView nowCond;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pmText;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;

    private String mWeatherId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wether, container, false);
        bingPicture = (ImageView) view.findViewById(R.id.bing_pic);
        weatherLayout = (ScrollView) view.findViewById(R.id.weather_layout);
        titleCity = (TextView) view.findViewById(R.id.title_city);
        titleUpdateTime = (TextView) view.findViewById(R.id.title_update_time);
        nowDegree = (TextView) view.findViewById(R.id.degree_text);
        nowCond = (TextView) view.findViewById(R.id.nowcond_text);
        forecastLayout = (LinearLayout) view.findViewById(R.id.forecast_layout);
        aqiText = (TextView) view.findViewById(R.id.aqi_text);
        pmText = (TextView) view.findViewById(R.id.pm2_5_text);
        comfortText = (TextView) view.findViewById(R.id.comfort_text);
        carWashText = (TextView) view.findViewById(R.id.car_wash_text);
        sportText = (TextView) view.findViewById(R.id.sport_text);
        drawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        nvButton = (Button) view.findViewById(R.id.nv_button);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String weatherString = sp.getString("weather", null);
        if (weatherString != null) {
            //有缓存直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            //无缓存时去服务器中请求数据
            mWeatherId = getActivity().getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.GONE);
            requestWeather(mWeatherId);
        }

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });

        nvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        String bingPic = sp.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(getActivity()).load(bingPic).into(bingPicture);
        } else {
            loadBingPic();
        }
    }

    public void requestWeather(final String weatherId) {
        String address = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=149c347fd5d445b29179a8854fde8d47";
        HttpUtil.sendOKHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                Log.d("" + getActivity(), responseText);
                final Weather weather = Utility.handleWeatherResponse(responseText);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            mWeatherId = weather.basic.weatherId;
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(getContext(), "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
    }

    private void showWeatherInfo(Weather weather) {
        titleCity.setText(weather.basic.cityName);
        titleUpdateTime.setText(weather.basic.update.updateTime.split(" ")[1]);
        nowDegree.setText(weather.now.temperature + "°C");
        Log.d("saasd", "" + weather.now.condition.info);
        nowCond.setText(weather.now.condition.info);
        forecastLayout.removeAllViews();
        for (DailyForecast dailyForecast : weather.dailyForecastList) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.forecast_item, forecastLayout, false);
            ((TextView) view.findViewById(R.id.date_text)).setText(dailyForecast.date);
            ((TextView) view.findViewById(R.id.weather_info_forecast)).setText(dailyForecast.condition.info);
            ((TextView) view.findViewById(R.id.max_text)).setText(dailyForecast.temperature.max);
            ((TextView) view.findViewById(R.id.min_text)).setText(dailyForecast.temperature.min);
            forecastLayout.addView(view);
        }
        aqiText.setText(weather.aqi.city.aqi);
        pmText.setText(weather.aqi.city.pm25);
        comfortText.setText("舒适度：" + weather.suggestion.comfort.info);
        carWashText.setText("洗车指数：" + weather.suggestion.carWash.info);
        sportText.setText("运动建议：" + weather.suggestion.sport.info);
        weatherLayout.setVisibility(View.VISIBLE);
        Intent intent = new Intent(getActivity(), AutoUpdateService.class);
        getActivity().startService(intent);
    }

    /**
     * 获取bing每日一图
     */
    private void loadBingPic() {
        String requestAddress = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOKHttpRequest(requestAddress, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPicAddress = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                editor.putString("bing_pic", bingPicAddress);
                editor.apply();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(getActivity()).load(bingPicAddress).into(bingPicture);
                    }
                });
            }
        });
    }
}
