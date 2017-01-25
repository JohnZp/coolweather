package com.weather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zhangjiuchu on 2017/1/20.
 */

public class DailyForecast {

    public String date;

    @SerializedName("cond")
    public ForecastCondition condition;

    @SerializedName("tmp")
    public Temperature temperature;

    public class ForecastCondition {

        @SerializedName("txt_d")
        public String info;
    }

    public class Temperature {

        @SerializedName("max")
        public String max;

        @SerializedName("min")
        public String min;
    }
}
