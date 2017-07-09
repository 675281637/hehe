package com.weiliang.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 创建日期：2017/7/6 0006
 * 描述:
 * 作者:刘敏
 */

public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;
    @SerializedName("cw")
    public CarWash carWash;

    public Sport sport;
    public class Comfort{
        @SerializedName("txt")
        public String info;
    }

    public class CarWash{
        @SerializedName("txt")
        public String info;
    }

    public class Sport{
        @SerializedName("txt")
        public String info;
    }
}
