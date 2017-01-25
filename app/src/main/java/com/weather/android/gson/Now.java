package com.weather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zhangjiuchu on 2017/1/20.
 */

public class Now {

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public NowCondition condition;

    public class NowCondition{

        @SerializedName("txt")
        public String info;
    }
}
