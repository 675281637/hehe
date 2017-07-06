package com.weiliang.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 创建日期：2017/7/6 0006
 * 描述:引用实体类
 * 作者:刘敏
 */

public class Weather {
    public String status;
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;

}
