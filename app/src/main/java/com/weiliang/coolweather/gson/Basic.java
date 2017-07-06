package com.weiliang.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 创建日期：2017/7/6 0006
 * 描述:
 * 作者:刘敏
 */

public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;
    public class Update{
        @SerializedName("loc")
        public String updateTime;

    }

}
