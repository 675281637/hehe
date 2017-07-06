package com.weiliang.coolweather.gson;

/**
 * 创建日期：2017/7/6 0006
 * 描述:
 * 作者:刘敏
 */

public class AQI {
    public AQICity city;
    public class AQICity{
        public String aqi;
        public String pm25;
    }
}
