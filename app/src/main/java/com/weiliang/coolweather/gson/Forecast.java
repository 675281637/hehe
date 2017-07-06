package com.weiliang.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 创建日期：2017/7/6 0006
 * 描述:预报
 * 作者:刘敏
 */

public class Forecast {
    public String date;
    @SerializedName("tmp")
    public Temperature temperature;
    @SerializedName("cond")
    public More more;

    public class Temperature{
        public String max;
        public String min;
    }

    public class More{
        @SerializedName("txt_d")
        public String info;
    }


}
