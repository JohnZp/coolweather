package com.weather.android.gson;

import com.weather.android.db.City;

/**
 * Created by zhangjiuchu on 2017/1/20.
 */

public class AQI {

    public AQICity city;

    public class AQICity {

        public String aqi;

        public String pm25;
    }
}
