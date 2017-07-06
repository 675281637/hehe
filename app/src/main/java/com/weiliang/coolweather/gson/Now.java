package com.weiliang.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 创建日期：2017/7/6 0006
 * 描述:
 * 作者:刘敏
 */

public class Now {
    //温度
    public String temperature;
    @SerializedName("cond")
    public More more;
    //多云
    public class More{
        @SerializedName("txt")
        public String info;
    }
}
